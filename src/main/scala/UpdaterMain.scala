package de.martenschaefer.minecraft.worldgenupdater

import java.nio.file.Paths
import de.martenschaefer.data.command.Command
import de.martenschaefer.data.command.argument.CommandArgument
import de.martenschaefer.data.command.builder.CommandBuilder.*
import de.martenschaefer.data.serialization.ElementError
import de.martenschaefer.data.util.DataResult.*
import feature.{ ConfiguredFeature, FeatureProcessResult }
import util.*

object UpdaterMain {
    val NAMESPACE = "worldgenupdater"

    val COMMAND: Command[Unit] = Command.build {
        literalFlag("help", Some('h')) {
            result {
                this.printHelp()
            }
        }

        literal("update") {
            withFlag("assume-yes", Some('y')) { assumeYes =>
                withFlag("update-only", Some('u')) { onlyUpdate =>
                    literal("features") {
                        argument(CommandArgument.string("origin")) { origin =>
                            argument(CommandArgument.string("target")) { target =>
                                result {
                                    this.processFeatures(origin, target, onlyUpdate, assumeYes)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    def main(args: Array[String]): Unit = {
        COMMAND.run(List.from(args)) match {
            case Failure(_, _) => {
                println("Invalid command.\n")

                this.printHelp()
            }

            case _ =>
        }
    }

    def processFeatures(origin: String, target: String, onlyUpdate: Boolean, assumeYes: Boolean): Unit = {
        val originPath = Paths.get(origin)
        val targetPath = Paths.get(target)

        val context = FeatureUpdateContext(onlyUpdate)

        val featureProcessor: ConfiguredFeature[_, _] => FeatureProcessResult = feature =>
            feature.feature.process(feature.config, context)

        val getFeaturePostProcessWarnings: ConfiguredFeature[_, _] => List[ElementError] = feature =>
            feature.feature.getPostProcessWarnings(feature.config, context)

        FeatureUpdater.process(originPath, targetPath, featureProcessor, getFeaturePostProcessWarnings, assumeYes)
    }

    def printHelp(): Unit = {
        println("Commands:")
        println("update features <origin> <target>")
        println()
        println("Flags:")
        printFlag(Some('h'), "help", "Shows a list of commands and flags", 4)
        printFlag(Some('u'), "update-only", "Disables optimization of features", 2)
        printFlag(Some('y'), "assume-yes", "Skip question if input files would be overwritten", 2)
    }

    private def printFlag(shortFlag: Option[Char], flag: String, description: String, tabs: Int): Unit =
        println(shortFlag.map("-" + _.toString).getOrElse("") + "\t\t--" + flag + "\t".repeat(tabs) + description)
}
