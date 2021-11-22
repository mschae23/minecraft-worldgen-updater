package de.martenschaefer.minecraft.worldgenupdater

import java.nio.file.Paths
import de.martenschaefer.data.command.Command
import de.martenschaefer.data.command.argument.CommandArgument
import de.martenschaefer.data.command.builder.CommandBuilder.*
import de.martenschaefer.data.serialization.ElementError
import de.martenschaefer.data.util.DataResult.*
import feature.placement.{ PlacedFeature, PlacedFeatureReference }
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
                Flag.Verbose -> ("verbose", Some('v')))) { flags =>
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

        val featureProcessor: PlacedFeatureReference => ProcessResult[PlacedFeatureReference] = _.process(using context)
            .map(PlacedFeatureReference.apply)

        val getFeaturePostProcessWarnings: PlacedFeatureReference => List[ElementError] = _.getPostProcessWarnings(using context)

        FeatureUpdater.process(originPath, targetPath, featureProcessor, getFeaturePostProcessWarnings, fileNameRegex)
    }

    def printHelp(): Unit = {
        println("Commands:")
        println("update features <input> <output>")
        println()
        println("Flags:")
        printFlag(Some('h'), "help", "Shows a list of commands and flags")
        printFlag(Some('u'), "update-only", "Disables optimization of features")
        printFlag(Some('y'), "assume-yes", "Skip question if input files would be overwritten")
        printFlag(None, "colored", "Use colored output")
        printFlag(Some('r'), "recursive", "Recursively process features in subfolders")
        printFlag(Some('v'), "verbose", "Adds some debug information for parse errors")
    }

    private def printFlag(shortFlag: Option[Char], flag: String, description: String): Unit =
        println(shortFlag.map("-" + _.toString).getOrElse("  ") + "   --" + flag + " ".repeat(24 - flag.length) + description)
}
