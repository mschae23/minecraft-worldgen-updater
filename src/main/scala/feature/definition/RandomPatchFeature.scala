package de.martenschaefer.minecraft.worldgenupdater
package feature.definition

import de.martenschaefer.data.serialization.{ Codec, ValidationError }
import decorator.Decorators
import decorator.definition.{ BlockFilterDecorator, BlockFilterDecoratorConfig }
import feature.placement.definition.BlockPredicateFilterPlacement
import feature.placement.{ PlacedFeature, PlacedFeatureReference }
import feature.{ Feature, FeatureProcessResult, Features }
import util.*
import valueprovider.{ AllOfBlockPredicate, BlockPredicate, TrueBlockPredicate }
import cats.catsInstancesForId

case object RandomPatchFeature extends Feature(Codec[RandomPatchFeatureConfig]) {
    override def process(config: RandomPatchFeatureConfig, context: FeatureUpdateContext): FeatureProcessResult = {
        if (config.old2.isDefined) {
            PlacedFeature(this.configure(RandomPatchFeatureConfig(config.tries, config.spreadXz, config.spreadY, config.feature, None)),
                List.empty).process(using context).flatMap(feature => BlockPredicateFilterPlacement(AllOfBlockPredicate(List(
                BlockFilterDecorator.updateOld1(
                    BlockFilterDecoratorConfig.Old1(config.old2.orNull.allowedOn, config.old2.orNull.disallowedOn.map(_.name),
                        BlockPos(0, -1, 0))
                ), if (config.old2.orNull.onlyInAir.isDefined) {
                    if (config.old2.orNull.onlyInAir.get)
                        BlockPredicate.MATCHING_AIR else BlockPredicate.MATCHING_AIR_OR_WATER
                } else TrueBlockPredicate)).process).process(feature)(using context))
        } else
            config.feature.process(using context).map(PlacedFeatureReference.apply).mapWritten(_.map(_
                .withPrependedPath("feature").withPrependedPath("config"))).map(feature =>
                PlacedFeature(this.configure(RandomPatchFeatureConfig(config.tries, config.spreadXz, config.spreadY,
                    feature)), List.empty))
    }
}
