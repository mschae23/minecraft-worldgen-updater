package de.martenschaefer.minecraft.worldgenupdater

import java.io.IOException
import java.nio.file.{ Files, LinkOption, Path, Paths }
import java.util.Scanner
import scala.jdk.CollectionConverters.IterableHasAsScala
import scala.util.Using
import de.martenschaefer.data.Result
import de.martenschaefer.data.serialization.{ AlternativeError, Codec, Element, ElementError, ElementNode, Encoder, JsonCodecs, RecordParseError, ValidationError }
import de.martenschaefer.data.serialization.JsonCodecs.given
import de.martenschaefer.data.util.DataResult.*
import de.martenschaefer.data.util.*
import feature.ConfiguredFeature
import util.*

object FeatureUpdater {
    def process[T: Codec](inputPath: Path, outputPath: Path,
                          processor: T => ProcessResult[T],
                          getPostProcessWarnings: T => List[ElementError],
                          fileNameRegex: String, featureType: Option[String])(using flags: Flags): Option[WarningType] = {
        if (inputPath.equals(outputPath) && !Flag.AssumeYes.get) {
            println(colored("Input and output path are the same. The input files will be overwritten.", Console.YELLOW))
            println("Continue (y / N)? ")

            Using(new Scanner(System.in)) { scanner =>
                val input = scanner.nextLine()

                if (!"y".equalsIgnoreCase(input)) {
                    if (!"n".equalsIgnoreCase(input) && input.nonEmpty) {
                        println("Write either \"y\" for yes or \"n\" for no.")
                    }

                    println(colored("Aborting.", Console.YELLOW))
                    return None
                }
            }
        }

        if (Files.isDirectory(inputPath)) {
            Some(this.processDirectory(inputPath, outputPath, inputPath,
                processor, getPostProcessWarnings, fileNameRegex, featureType))
        } else if (Files.isRegularFile(inputPath))
            Some(this.processFeatureFile(inputPath, outputPath, processor, getPostProcessWarnings).warningType)
        else
            None
    }

    def processFeatureFile[T: Codec](originFile: Path, targetFile: Path,
                                     processor: T => ProcessResult[T],
                                     getPostProcessWarnings: T => List[ElementError])(using flags: Flags): FileProcessResult = {
        Option(targetFile.getParent).foreach(Files.createDirectories(_))

        val result = this.processFile[T](originFile, targetFile, processor, getPostProcessWarnings)

        result match {
            case FileProcessResult.Errors(errors) => printWarnings(WarningType.Error, errors)
            case FileProcessResult.Warnings(warnings) => printWarnings(WarningType.Warning, warnings)
            case _ =>
        }

        result
    }

    def processDirectory[T: Codec](inputDirectory: Path, outputDirectory: Path,
                                   startingDirectory: Path,
                                   processor: T => ProcessResult[T],
                                   getPostProcessWarnings: T => List[ElementError],
                                   fileNameRegex: String, featureType: Option[String],
                                   recursive: Boolean = false)(using flags: Flags): WarningType = {
        if (!Files.exists(inputDirectory) || !Files.isDirectory(inputDirectory))
            throw new IllegalArgumentException(s"${inputDirectory.getFileName} doesn't exist or is not a directory")

        if (Files.exists(outputDirectory) && !Files.isDirectory(outputDirectory))
            throw new IllegalArgumentException(s"${outputDirectory.getFileName} is not a directory")

        Files.createDirectories(outputDirectory)

        var foundWarnings = false
        var foundErrors = false

        val featureTypeString = featureType.map(_ + " ").getOrElse("")

        Using(Files.newDirectoryStream(inputDirectory)) { directoryStream =>
            for (path <- directoryStream.asScala) {
                if (Flag.Recursive.get && Files.isDirectory(path)) {
                    processDirectory(path, outputDirectory.resolve(path.getFileName), startingDirectory,
                        processor, getPostProcessWarnings, fileNameRegex, featureType, recursive = true) match {
                        case WarningType.Error => foundErrors = true
                        case WarningType.Warning => foundWarnings = true
                        case _ =>
                    }
                }

                if (path.getFileName.toString.matches(fileNameRegex)) {
                    val relativePath = startingDirectory.relativize(path)

                    println(s"[info] Processing $featureTypeString$relativePath")

                    val result = processFile[T](path, outputDirectory.resolve(path.getFileName), processor, getPostProcessWarnings)

                    result match {
                        case FileProcessResult.Errors(errors) => println()
                            printWarnings(WarningType.Error, errors)
                            println()
                            foundErrors = true
                        case FileProcessResult.Warnings(warnings) => println()
                            printWarnings(WarningType.Warning, warnings)
                            println()
                            foundWarnings = true
                        case _ =>
                    }
                }
            }
        }

        if (foundErrors) WarningType.Error
        else if (foundWarnings) WarningType.Warning
        else WarningType.Okay
    }

    def readFeature[T: Codec](inputPath: Path): Result[T] = {
        val inputString: String = try {
            read(inputPath)
        } catch {
            case e: IOException => {
                return Failure(List(createErrorForException("read", Some("IO"), e)))
            }

            case e: Exception => {
                return Failure(List(createErrorForException("read", None, e)))
            }
        }

        try {
            Codec[T].decode(inputString)
        } catch {
            case e: Exception => {
                Failure(List(createErrorForException("decoding", None, e)))
            }
        }
    }

    def encodeFeature[T, E](feature: T)(using Codec[T], Encoder[Element, E]): Result[E] = {
        try {
            Codec[T].encode(feature)
        } catch {
            case e: Exception => {
                Failure(List(createErrorForException("encoding", None, e)))
            }
        }
    }

    def writeFeature(targetPath: Path, feature: String): Option[List[ElementError]] = {
        try {
            write(targetPath, feature)
            None
        } catch {
            case e: IOException => {
                Some(List(createErrorForException("write", None, e)))
            }

            case e: Exception => {
                Some(List(createErrorForException("write", None, e)))
            }
        }
    }

    def getWarningForLifecycle(lifecycle: Lifecycle): Option[ElementError] = lifecycle match {
        case Lifecycle.Internal =>
            Some(ValidationError(_ => "Implementation details of Worldgen Updater are used."))
        case Lifecycle.Experimental =>
            Some(ValidationError(_ => "Experimental features are used."))
        case Lifecycle.Deprecated(since) =>
            Some(ValidationError(_ => s"Features that are deprecated since v$since are used."))
        case _ => None
    }

    def printDone(warningType: WarningType)(using Flags): Unit = {
        println(warningType match {
            case WarningType.Error =>
                colored("Done with errors.", WarningType.Error.color)
            case WarningType.Warning =>
                colored("Done with warnings.", WarningType.Warning.color)
            case _ => colored("Done.", WarningType.Okay.color)
        })
    }

    def processFile[T: Codec](inputPath: Path, outputPath: Path,
                              processor: T => ProcessResult[T],
                              getPostProcessWarnings: T => List[ElementError])(using Flags): FileProcessResult = {
        var lifecycle = Lifecycle.Stable
        var foundWarnings = false

        val originFeature: T = readFeature(inputPath) match {
            case Success(feature, l) => lifecycle = l
                feature

            case Failure(errors, _) => return FileProcessResult.Errors(errors)
        }

        var warnings: List[ElementError] = List.empty

        val targetFeature: T = try {
            val targetFeatureWriter: ProcessResult[T] = processor(originFeature)
            warnings = targetFeatureWriter.written
            val targetFeature: T = targetFeatureWriter.value
            warnings = warnings ::: getPostProcessWarnings(targetFeature)

            targetFeature
        } catch {
            case e: Exception => {
                warnings ::= createErrorForException("processing", None, e)
                return FileProcessResult.Errors(warnings)
            }
        }

        val targetFeatureString: String = encodeFeature(targetFeature)(using Codec[T], JsonCodecs.prettyJsonEncoder) match {
            case Success(s, l) => lifecycle += l
                s
            case Failure(errors, _) => return FileProcessResult.Errors(errors)
        }

        // Add lifecycle warning
        getWarningForLifecycle(lifecycle).foreach(error => warnings = error :: warnings)

        // Write feature to file
        writeFeature(outputPath, targetFeatureString).foreach(errors => warnings = warnings ::: errors)

        if (warnings.isEmpty) FileProcessResult.Normal else FileProcessResult.Warnings(warnings)
    }

    private def createErrorForException(phase: String, kind: Option[String], e: Exception): ElementError =
        ValidationError(_ => s"${ kind.map(_ + " ").getOrElse("") }Exception thrown during $phase: $e", List.empty)

    def printWarnings(warningType: WarningType, warnings: List[ElementError], indent: Int = 0)(using flags: Flags): Unit = {
        if (indent == 0)
            println(colored(s"${warningType.label} found:", warningType.color))

        for (warning <- warnings)
            printWarning(warningType, warning, indent)
    }

    private val DIMMED: String = "\u001b[2m"

    def printWarning(warningType: WarningType, warning: ElementError, indent: Int = 0)(using flags: Flags): Unit = {
        val indentation = "  ".repeat(indent)

        val warningString = warning match {
            case _ if warning.isInstanceOf[RecordParseError] => {
                val parseError = warning.asInstanceOf[RecordParseError]

                val reducedDebugInfo = !Flag.Verbose.get && (parseError.element match {
                    case Element.ObjectElement(_) => true
                    case Element.ArrayElement(_) => true
                    case _ => false
                })

                warning.getDescription(ElementError.getPath(warning.path))
                    + ": " + (if (reducedDebugInfo) "<reduced debug info>" else colored(parseError.element.toString, DIMMED))
            }

            case _ => warning.toString
        }

        println(indentation + "- " + warningString)

        for (subError <- warning.getSubErrors) {
            printWarning(warningType, subError, indent + 1)
        }
    }
}
