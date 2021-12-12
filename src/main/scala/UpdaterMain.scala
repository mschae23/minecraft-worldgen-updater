package de.martenschaefer.minecraft.worldgenupdater

import java.nio.file.Paths
import de.martenschaefer.data.command.Command
import de.martenschaefer.data.command.argument.CommandArgument
import de.martenschaefer.data.command.builder.CommandBuilder.*
import de.martenschaefer.data.serialization.{ ElementError, ValidationError }
import de.martenschaefer.data.util.DataResult.*
import feature.placement.{ PlacedFeature, PlacedFeatureReference }
import feature.{ ConfiguredFeature, FeatureProcessResult }
import util.*
import cats.catsInstancesForId
import cats.data.Writer

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
                        literalFlag("legacy", None) { // Legacy; from configured features to placed features
                            argument(CommandArgument.string("input")) { input =>
                                argument(CommandArgument.string("output")) { output =>
                                    result {
                                        this.processPlacedFeatures(input, output, fileNameRegex)(using flags)
                                    }
                                }
                            }
                        }

                        // New; update configured and placed features separately
                        argument(CommandArgument.string("configured feature input")) { configuredInput =>
                            argument(CommandArgument.string("placed feature input")) { placedInput =>
                                argument(CommandArgument.string("configured feature output")) { configuredOutput =>
                                    argument(CommandArgument.string("placed feature output")) { placedOutput =>
                                        result {
                                            // TODO find a better solution for this
                                            this.processConfiguredFeatures(configuredInput, configuredOutput, fileNameRegex)(using flags)
                                            println()
                                            this.processPlacedFeatures(placedInput, placedOutput, fileNameRegex)(using flags)
                                        }
                                    }
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

    def processConfiguredFeatures(origin: String, target: String, fileNameRegex: String)(using flags: Flags): Unit = {
        val originPath = Paths.get(origin)
        val targetPath = Paths.get(target)

        val context = FeatureUpdateContext(flags(Flag.UpdateOnly))

        val featureProcessor: ConfiguredFeature[_, _] => ProcessResult[ConfiguredFeature[_, _]] = feature => {
            val placedResult = feature.feature.process(feature.config, context)

            if (placedResult.value.modifiers.isEmpty) placedResult.map(_.feature)
            else
                Writer(ValidationError(_ => s"Configured feature has modifiers after processing. These have been REMOVED.")
                    :: placedResult.written, placedResult.value.feature)
        }

        val getFeaturePostProcessWarnings: ConfiguredFeature[_, _] => List[ElementError] =
            feature => feature.feature.getPostProcessWarnings(feature.config, context)

        FeatureUpdater.process(originPath, targetPath, featureProcessor, getFeaturePostProcessWarnings, fileNameRegex)
    }

    def processPlacedFeatures(origin: String, target: String, fileNameRegex: String)(using flags: Flags): Unit = {
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
