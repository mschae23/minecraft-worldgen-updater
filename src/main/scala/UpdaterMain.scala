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
            withFlags(Map(
                Flag.AssumeYes -> ("assume-yes", Some('y')),
                Flag.UpdateOnly -> ("update-only", Some('u')),
                Flag.Colored -> ("colored", None),
                Flag.Recursive -> ("recursive", Some('r')),
                Flag.ReducedDebugInfo -> ("reduced-debug-info", None))) { flags =>
                defaultedArgumentFlag("matches", None, CommandArgument.string("file name regex"), ".+\\.json$") { fileNameRegex =>
                    literal("features") {
                        argument(CommandArgument.string("origin")) { origin =>
                            argument(CommandArgument.string("target")) { target =>
                                result {
                                    this.processFeatures(origin, target, fileNameRegex)(using flags)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    def main(args: Array[String]): Unit = {
        val arguments = List.from(args)

        COMMAND.run(arguments) match {
            case Failure(_, _) => {
                println("Invalid command.\n")

                this.printHelp()
            }

            case _ =>
        }
    }

    def processFeatures(origin: String, target: String, fileNameRegex: String)(using flags: Flags): Unit = {
        val originPath = Paths.get(origin)
        val targetPath = Paths.get(target)

        val context = FeatureUpdateContext(flags(Flag.UpdateOnly))

        val featureProcessor: ConfiguredFeature[_, _] => FeatureProcessResult = feature =>
            feature.feature.process(feature.config, context)

        val getFeaturePostProcessWarnings: ConfiguredFeature[_, _] => List[ElementError] = feature =>
            feature.feature.getPostProcessWarnings(feature.config, context)

        FeatureUpdater.process(originPath, targetPath, featureProcessor, getFeaturePostProcessWarnings, fileNameRegex)
    }

    def printHelp(): Unit = {
        println("Commands:")
        println("update features <origin> <target>")
        println()
        println("Flags:")
        printFlag(Some('h'), "help", "Shows a list of commands and flags")
        printFlag(Some('u'), "update-only", "Disables optimization of features")
        printFlag(Some('y'), "assume-yes", "Skip question if input files would be overwritten")
        printFlag(None, "colored", "Use colored output")
        printFlag(Some('r'), "recursive", "Recursively process features in subfolders")
        printFlag(None, "reduced-debug-info", "Removes some debug information for errors")
    }

    private def printFlag(shortFlag: Option[Char], flag: String, description: String): Unit =
        println(shortFlag.map("-" + _.toString).getOrElse("  ") + "   --" + flag + " ".repeat(24 - flag.length) + description)
}
