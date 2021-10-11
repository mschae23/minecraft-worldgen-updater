package de.martenschaefer.minecraft.worldgenupdater

import java.io.IOException
import java.nio.file.{ Files, LinkOption, Path, Paths }
import java.util.Scanner
import scala.jdk.CollectionConverters.IterableHasAsScala
import scala.util.Using
import de.martenschaefer.data.serialization.{ AlternativeError, Codec, Element, ElementError, ElementNode, JsonCodecs, RecordParseError, ValidationError }
import de.martenschaefer.data.serialization.JsonCodecs.given
import de.martenschaefer.data.util.DataResult.*
import de.martenschaefer.data.util.Lifecycle
import feature.ConfiguredFeature
import util.*

object FeatureUpdater {
    def process[T: Codec](originPath: Path, targetPath: Path,
                          processor: T => ProcessResult[T],
                          getPostProcessWarnings: T => List[ElementError],
                          fileNameRegex: String)(using flags: Flags): Unit = {
        if (originPath.equals(targetPath) && !Flag.AssumeYes.get) {
            println(colored("Origin and target path are the same. The origin files will be overwritten.", Console.YELLOW))
            println("Continue (y / N)? ")

            Using(new Scanner(System.in)) { scanner =>
                val input = scanner.nextLine()

                if (!"y".equalsIgnoreCase(input)) {
                    if (!"n".equalsIgnoreCase(input) && !input.isEmpty) {
                        println("Write either \"y\" for yes or \"n\" for no.")
                    }

                    println(colored("Aborting.", Console.YELLOW))
                    return;
                }
            }
        }

        if (Files.isDirectory(originPath)) {
            val warningType = this.processDirectory(originPath, targetPath, originPath,
                processor, getPostProcessWarnings, fileNameRegex)

            val notice = warningType match {
                case WarningType.Error =>
                    colored("Done with errors.", WarningType.Error.color)
                case WarningType.Warning =>
                    colored("Done with warnings.", WarningType.Warning.color)
                case _ => colored("Done.", WarningType.Okay.color)
            }

            println(notice)
        } else if (Files.isRegularFile(originPath))
            this.processFeatureFile(originPath, targetPath, processor, getPostProcessWarnings)
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

    def processDirectory[T: Codec](originDirectory: Path, targetDirectory: Path,
                                   startingDirectory: Path,
                                   processor: T => ProcessResult[T],
                                   getPostProcessWarnings: T => List[ElementError],
                                   fileNameRegex: String, recursive: Boolean = false)(using flags: Flags): WarningType = {
        if (!Files.exists(originDirectory) || !Files.isDirectory(originDirectory))
            throw new IllegalArgumentException(s"${originDirectory.getFileName} doesn't exist or is not a directory")

        if (Files.exists(targetDirectory) && !Files.isDirectory(targetDirectory))
            throw new IllegalArgumentException(s"${targetDirectory.getFileName} is not a directory")

        Files.createDirectories(targetDirectory)

        var foundWarnings = false
        var foundErrors = false

        Using(Files.newDirectoryStream(originDirectory)) { directoryStream =>
            for (path <- directoryStream.asScala) {
                if (Flag.Recursive.get && Files.isDirectory(path)) {
                    processDirectory(path, targetDirectory.resolve(path.getFileName), startingDirectory,
                        processor, getPostProcessWarnings, fileNameRegex, true) match {
                        case WarningType.Error => foundErrors = true
                        case WarningType.Warning => foundWarnings = true
                        case _ =>
                    }
                }

                if (path.getFileName.toString.matches(fileNameRegex)) {
                    val relativePath = startingDirectory.relativize(path)

                    println(s"[info] Processing $relativePath")

                    val result = processFile[T](path, targetDirectory.resolve(path.getFileName), processor, getPostProcessWarnings)

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

        return if (foundErrors) WarningType.Error
        else if (foundWarnings) WarningType.Warning
        else WarningType.Okay
    }

    def processFile[T: Codec](originFile: Path, targetFile: Path,
                              processor: T => ProcessResult[T],
                              getPostProcessWarnings: T => List[ElementError])(using flags: Flags): FileProcessResult = {
        var lifecycle = Lifecycle.Stable
        var foundWarnings = false
        var warnings: List[ElementError] = List.empty

        val originString: String = try {
            read(originFile)
        } catch {
            case e: IOException => {
                warnings ::= ValidationError(_ => s"IO Exception thrown during read: ${e.getLocalizedMessage}", List.empty)
                return FileProcessResult.Errors(warnings)
            }

            case e: Exception => {
                warnings ::= ValidationError(_ => s"Exception thrown during read: ${e.getLocalizedMessage}", List.empty)
                return FileProcessResult.Errors(warnings)
            }
        }

        val originFeature: T = try {
            Codec[T].decode(originString) match {
                case Success(feature, l) => lifecycle += l
                    feature
                case Failure(errors, _) => return FileProcessResult.Errors(errors.toList)
            }
        } catch {
            case e: Exception => {
                warnings ::= ValidationError(_ => s"Exception thrown during decoding: ${e.getLocalizedMessage}", List.empty)
                return FileProcessResult.Errors(warnings)
            }
        }

        val targetFeature: T = try {
            val targetFeatureWriter: ProcessResult[T] = processor(originFeature)
            warnings = targetFeatureWriter.written
            val targetFeature: T = targetFeatureWriter.value
            warnings = warnings ::: getPostProcessWarnings(targetFeature)

            targetFeature
        } catch {
            case e: Exception => {
                warnings ::= ValidationError(_ => s"Exception thrown during processing: ${e.getLocalizedMessage}", List.empty)
                return FileProcessResult.Errors(warnings)
            }
        }

        if (!warnings.isEmpty) foundWarnings = true

        val targetFeatureString: String = try {
            Codec[T].encode(targetFeature)(using JsonCodecs.prettyJsonEncoder) match {
                case Success(json, l) => lifecycle += l
                    json
                case Failure(errors, _) => {
                    return FileProcessResult.Errors(errors.toList)
                }
            }
        } catch {
            case e: Exception => {
                warnings ::= ValidationError(_ => s"Exception thrown during encoding: ${e.getLocalizedMessage}", List.empty)
                return FileProcessResult.Errors(warnings)
            }
        }

        lifecycle match {
            case Lifecycle.Experimental =>
                warnings = ValidationError(_ => "Experimental features used.", List.empty) :: warnings
                foundWarnings = true
            case Lifecycle.Deprecated(since) =>
                warnings = ValidationError(_ => s"Used features that are deprecated since $since.", List.empty) :: warnings
                foundWarnings = true
            case _ =>
        }

        try {
            write(targetFile, targetFeatureString)
        } catch {
            case e: IOException => {
                warnings ::= ValidationError(_ => s"IO Exception thrown during write: ${e.getLocalizedMessage}", List.empty)
                return FileProcessResult.Errors(warnings)
            }

            case e: Exception => {
                warnings ::= ValidationError(_ => s"Exception thrown during write: ${e.getLocalizedMessage}", List.empty)
                return FileProcessResult.Errors(warnings)
            }
        }

        return if (foundWarnings) FileProcessResult.Warnings(warnings) else FileProcessResult.Normal
    }

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

                val reducedDebugInfo = Flag.ReducedDebugInfo.get && (parseError.element match {
                    case Element.ObjectElement(_) => true
                    case Element.ArrayElement(_) => true
                    case _ => false
                })

                warning.getDescription(ElementError.getPath(warning.path))
                    + ": " + (if (reducedDebugInfo) "<reduced debug info>" else colored(parseError.element.toString, DIMMED))
            }

            case AlternativeError(errors, path) => {
                println(indentation + "- " + "Multiple alternatives failed:")

                for (i <- 0 until errors.length) {
                    val alternative = errors(i)

                    println(indentation + s"  - Alternative ${i + 1}:")
                    printWarnings(warningType, alternative, indent + 2)
                }

                return;
            }

            case _ => warning.toString
        }

        println(indentation + "- " + warningString)
    }
}
