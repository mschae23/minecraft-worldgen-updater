package de.martenschaefer.minecraft.worldgenupdater

import java.nio.file.{ Files, Path, Paths }
import java.util.Scanner
import scala.jdk.CollectionConverters.IterableHasAsScala
import scala.util.Using
import de.martenschaefer.data.serialization.{ Codec, ElementError, JsonCodecs, ValidationError }
import de.martenschaefer.data.serialization.JsonCodecs.given
import de.martenschaefer.data.util.DataResult.*
import de.martenschaefer.data.util.Lifecycle
import feature.ConfiguredFeature
import util.*

object FeatureUpdater {
    private val JSON_SUFFIX = ".json"

    def process[T: Codec](originPath: Path, targetPath: Path,
                          processor: T => ProcessResult[T],
                          getPostProcessWarnings: T => List[ElementError], assumeYes: Boolean): Unit = {
        if (originPath.equals(targetPath) && !assumeYes) {
            println("Origin and target path are the same. The origin files will be overwritten.")
            println("Continue (y / N)? ")

            Using(new Scanner(System.in)) { scanner =>
                val input = scanner.nextLine()

                if (!"y".equalsIgnoreCase(input)) {
                    if (!"n".equalsIgnoreCase(input) && !input.isEmpty) {
                        println("Write either \"y\" for yes or \"n\" for no.")
                    }

                    println("Aborting.")
                    return;
                }
            }
        }

        if (Files.isDirectory(originPath))
            this.processDirectory(originPath, targetPath, processor, getPostProcessWarnings)
        else if (Files.isRegularFile(originPath))
            this.processFeatureFile(originPath, targetPath, processor, getPostProcessWarnings)
    }

    def processFeatureFile[T: Codec](originFile: Path, targetFile: Path,
                                     processor: T => ProcessResult[T],
                                     getPostProcessWarnings: T => List[ElementError]): FileProcessResult = {
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
                                   processor: T => ProcessResult[T],
                                   getPostProcessWarnings: T => List[ElementError]): Unit = {
        if (!Files.exists(originDirectory) || !Files.isDirectory(originDirectory))
            throw new IllegalArgumentException(s"${originDirectory.getFileName} doesn't exist or is not a directory")

        if (Files.exists(targetDirectory) && !Files.isDirectory(targetDirectory))
            throw new IllegalArgumentException(s"${targetDirectory.getFileName} is not a directory")

        Files.createDirectories(targetDirectory)

        var foundWarnings = false
        var foundErrors = false

        Using(Files.newDirectoryStream(originDirectory)) { directoryStream =>
            for (path <- directoryStream.asScala if path.getFileName.toString.endsWith(JSON_SUFFIX)) {
                println(s"[info] Processing ${path.getFileName}")

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

        val notice = if (foundErrors) "Done with errors." else if (foundWarnings) "Done with warnings." else "Done."

        println(notice)
    }

    def processFile[T: Codec](originFile: Path, targetFile: Path,
                              processor: T => ProcessResult[T],
                              getPostProcessWarnings: T => List[ElementError]): FileProcessResult = {
        var lifecycle = Lifecycle.Stable

        val originString = read(originFile)
        val originFeature: T = Codec[T].decode(originString) match {
            case Success(feature, l) => lifecycle += l
                feature
            case Failure(errors, _) => return FileProcessResult.Errors(errors.toList)
        }

        val targetFeatureWriter: ProcessResult[T] = processor(originFeature)
        var warnings: List[ElementError] = targetFeatureWriter.written
        val targetFeature: T = targetFeatureWriter.value
        warnings = warnings ::: getPostProcessWarnings(targetFeature)

        var foundWarnings = false

        if (!warnings.isEmpty) foundWarnings = true

        val targetFeatureString: String = Codec[T].encode(targetFeature)(using JsonCodecs.prettyJsonEncoder) match {
            case Success(json, l) => lifecycle += l
                json
            case Failure(errors, _) => {
                return FileProcessResult.Errors(errors.toList)
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

        write(targetFile, targetFeatureString)

        return if (foundWarnings) FileProcessResult.Warnings(warnings) else FileProcessResult.Normal
    }

    def printWarnings(warningType: WarningType, warnings: List[ElementError]): Unit = {
        println(s"${warningType.label} found:")
        println(warnings.mkString("- ", "\n- ", ""))
    }
}
