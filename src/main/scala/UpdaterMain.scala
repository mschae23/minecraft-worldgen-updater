package de.martenschaefer.minecraft.worldgenupdater

import java.io.{ File, FileInputStream, FileWriter, InputStream, IOException }
import java.nio.charset.StandardCharsets
import java.util.Scanner
import scala.util.Using
import de.martenschaefer.data.serialization.{ Codec, Decoder, Element, ElementError, JsonCodecs }
import de.martenschaefer.data.serialization.JsonCodecs.given
import de.martenschaefer.data.util._
import de.martenschaefer.data.util.DataResult._
import feature.{ ConfiguredFeature, Feature, FeatureConfig }

object UpdaterMain {
    val NAMESPACE = "worldgenupdater"

    def main(args: Array[String]): Unit = {
        if (args.length < 2) {
            println("At least two paramaters are required.")
            return
        }

        val originFile = File(args(0))
        val targetFile = File(args(1))

        val result = this.processFile(originFile, targetFile)
        println()

        result match {
            case FileProcessResult.Errors(errors) => println("Errors found:")
                println(errors.mkString("- ", "\n- ", ""))
            case FileProcessResult.Warnings(warnings) => println("Warnings found:")
                println(warnings.mkString("- ", "\n- ", ""))
            case _ =>
        }
    }

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

    def processFile(originFile: File, targetFile: File): FileProcessResult = {
        var lifecycle = Lifecycle.Stable

        val originString = read(originFile)
        val originFeature: ConfiguredFeature[_, _] = Codec[ConfiguredFeature[_, _]].decode(originString) match {
            case Success(feature, l) => lifecycle += l
                feature
            case Failure(errors, _) => return FileProcessResult.Errors(errors.toList)
        }

        val targetFeatureWriter: FeatureProcessResult = originFeature.feature.process(originFeature.config)
        val warnings: List[ElementError] = targetFeatureWriter.written
        val targetFeature: ConfiguredFeature[_, _] = targetFeatureWriter.value

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
            case Lifecycle.Experimental => println()
                println("You're using experimental features.")
                foundWarnings = true
            case Lifecycle.Deprecated(since) => println()
                println(s"You're using features that are deprecated since version $since.")
                foundWarnings = true
            case _ =>
        }

        write(targetFile, targetFeatureString)

        return if (foundWarnings) FileProcessResult.Warnings(warnings) else FileProcessResult.Normal
    }

    @throws[IOException]
    def read(file: File): String = {
        Using.Manager { use =>
            val in: InputStream = use(FileInputStream(file))
            val scanner: Scanner = use(new Scanner(in, StandardCharsets.UTF_8.name))

            scanner.useDelimiter("\\A").next()
        }.get
    }

    @throws[IOException]
    def write(file: File, string: String): Unit = {
        Using.Manager { use =>
            val out: FileWriter = use(FileWriter(file))

            out.write(string)
        }
    }
}
