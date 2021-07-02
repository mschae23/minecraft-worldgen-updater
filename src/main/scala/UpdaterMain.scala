package de.martenschaefer.minecraft.worldgenupdater

import java.io.{ File, FileInputStream, InputStream, IOException, OutputStreamWriter, Writer }
import java.nio.charset.StandardCharsets
import java.nio.file.{ Files, Path, Paths, StandardOpenOption }
import java.util.Scanner
import scala.jdk.CollectionConverters.IterableHasAsScala
import scala.util.Using
import de.martenschaefer.data.serialization.{ Codec, Decoder, Element, ElementError, JsonCodecs, ValidationError }
import de.martenschaefer.data.serialization.JsonCodecs.given
import de.martenschaefer.data.util._
import de.martenschaefer.data.util.DataResult._
import feature.{ ConfiguredFeature, Feature, FeatureConfig, FeatureProcessResult }
import util._

object UpdaterMain {
    val NAMESPACE = "worldgenupdater"

    private val JSON_SUFFIX = ".json"

    enum FileProcessResult {
        case Normal
        case Warnings(val warnings: List[ElementError])
        case Errors(val errors: List[ElementError])

        def +(other: FileProcessResult): FileProcessResult = other match {
            case Errors(errors) => this match {
                case Errors(errors2) => Errors(errors2 ::: errors)
                case _ => Errors(errors)
            }
            case _ if this.isInstanceOf[Errors] => this
            case Warnings(warnings) => this match {
                case Warnings(warnings2) => Warnings(warnings2 ::: warnings)
                case _ => Warnings(warnings)
            }
            case _ => this
        }
    }

    def main(args: Array[String]): Unit = {
        if (args.length < 2) {
            println("At least two paramaters are required.")
            return
        }

        val originFile = Paths.get(args(0))
        val targetFile = Paths.get(args(1))

        if (Files.isDirectory(originFile))
            this.processDirectory(originFile, targetFile)
        else if (Files.isRegularFile(originFile))
            this.processFeatureFile(originFile, targetFile)
    }

    def processFeatureFile(originFile: Path, targetFile: Path): FileProcessResult = {
        Option(targetFile.getParent).foreach(Files.createDirectories(_))

        val result = this.processFile(originFile, targetFile)

        result match {
            case FileProcessResult.Errors(errors) => printWarnings("Errors", errors)
            case FileProcessResult.Warnings(warnings) => printWarnings("Warnings", warnings)
            case _ =>
        }

        result
    }

    def processDirectory(originDirectory: Path, targetDirectory: Path): Unit = {
        if (!Files.exists(originDirectory) || !Files.isDirectory(originDirectory))
            throw new IllegalArgumentException(s"${ originDirectory.getFileName } doesn't exist or is not a directory")

        if (Files.exists(targetDirectory) && !Files.isDirectory(targetDirectory))
            throw new IllegalArgumentException(s"${ targetDirectory.getFileName } is not a directory")

        Files.createDirectories(targetDirectory)

        var foundWarnings = false
        var foundErrors = false

        Using(Files.newDirectoryStream(originDirectory)) { directoryStream =>
            for (path <- directoryStream.asScala if path.getFileName.toString.endsWith(JSON_SUFFIX)) {
                println(s"Processing ${ path.getFileName }")

                val result = processFile(path, targetDirectory.resolve(path.getFileName))

                result match {
                    case FileProcessResult.Errors(errors) => println()
                        printWarnings("Errors", errors)
                        println()
                        foundErrors = true
                    case FileProcessResult.Warnings(warnings) => println()
                        printWarnings("Warnings", warnings)
                        println()
                        foundWarnings = true
                    case _ =>
                }
            }
        }

        val notice = if (foundErrors) "Done with errors." else if (foundWarnings) "Done with warnings." else "Done."

        println(notice)
    }

    def processFile(originFile: Path, targetFile: Path): FileProcessResult = {
        var lifecycle = Lifecycle.Stable

        val originString = read(originFile)
        val originFeature: ConfiguredFeature[_, _] = Codec[ConfiguredFeature[_, _]].decode(originString) match {
            case Success(feature, l) => lifecycle += l
                feature
            case Failure(errors, _) => return FileProcessResult.Errors(errors.toList)
        }

        val targetFeatureWriter: FeatureProcessResult = originFeature.feature.process(originFeature.config)
        var warnings: List[ElementError] = targetFeatureWriter.written
        val targetFeature: ConfiguredFeature[_, _] = targetFeatureWriter.value
        warnings = warnings ::: targetFeature.feature.getPostProcessWarnings(targetFeature.config)

        var foundWarnings = false

        if (!warnings.isEmpty) foundWarnings = true

        val targetFeatureString: String = Codec[ConfiguredFeature[_, _]].encode(targetFeature)(using JsonCodecs.prettyJsonEncoder) match {
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

    def printWarnings(label: String, warnings: List[ElementError]): Unit = {
        println(s"$label found:")
        println(warnings.mkString("- ", "\n- ", ""))
    }

    @throws[IOException]
    def read(file: Path): String = {
        Using.Manager { use =>
            val in: InputStream = use(Files.newInputStream(file))
            val scanner: Scanner = use(new Scanner(in, StandardCharsets.UTF_8.name))

            scanner.useDelimiter("\\A").next()
        }.get
    }

    @throws[IOException]
    def write(file: Path, content: String): Unit = {
        Using.Manager { use =>
            val out: Writer = use(OutputStreamWriter(Files.newOutputStream(file, StandardOpenOption.CREATE)))

            out.write(content)
        }
    }
}
