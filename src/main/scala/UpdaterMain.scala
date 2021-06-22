package de.martenschaefer.minecraft.worldgenupdater

import java.io.{ File, FileInputStream, FileWriter, InputStream, IOException }
import java.nio.charset.StandardCharsets
import java.util.Scanner
import scala.util.Using
import de.martenschaefer.data.serialization.{ Codec, Decoder, Element, ElementError, JsonCodecs }
import de.martenschaefer.data.serialization.JsonCodecs.given
import de.martenschaefer.data.util.Either._
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

        val originString = read(originFile)
        val originFeature: ConfiguredFeature[_, _] = Codec[ConfiguredFeature[_, _]].decode(originString) match {
            case Right(feature) => feature
            case Left(errors) => {
                println("Errors decoding origin feature:")
                println()
                println(errors.mkString("", "\n", ""))
                return
            }
        }

        val targetFeatureWriter: FeatureProcessResult = originFeature.feature.process(originFeature.config)
        val warnings: List[String] = targetFeatureWriter.written.map(_.toString).distinct
        val targetFeature: ConfiguredFeature[_, _] = targetFeatureWriter.value

        if (!warnings.isEmpty) {
            println("Warnings:")
            println()
            println(warnings.mkString("", "\n", ""))
            println()
        }

        val targetFeatureString: String = Codec[ConfiguredFeature[_, _]].encode(targetFeature)(using JsonCodecs.prettyJsonEncoder) match {
            case Right(json) => json
            case Left(errors) => {
                println("Errors encoding configured feature:")
                println()
                println(errors.mkString("", "\n", ""))
                return
            }
        }

        write(targetFile, targetFeatureString)
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
