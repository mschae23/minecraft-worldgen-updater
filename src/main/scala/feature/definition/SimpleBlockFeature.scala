package de.martenschaefer.minecraft.worldgenupdater
package feature.definition

import de.martenschaefer.data.serialization.{ Codec, ElementError, ValidationError }
import decorator.definition.BlockFilterDecoratorConfig
import decorator.{ ConfiguredDecorator, Decorators }
import feature.definition.SimpleBlockFeatureConfig.Old1
import feature.placement.PlacedFeature
import feature.placement.definition.BlockPredicateFilterPlacement
import feature.{ ConfiguredFeature, Feature, FeatureProcessResult, Features }
import util.*
import valueprovider.{ AllOfBlockPredicate, BlockPredicate, MatchingBlocksBlockPredicate, TrueBlockPredicate }

case object SimpleBlockFeature extends Feature(Codec[SimpleBlockFeatureConfig]) {
    override def process(config: SimpleBlockFeatureConfig, context: FeatureUpdateContext): FeatureProcessResult = {
        if (config.old1.isDefined)
            PlacedFeature(this.configure(SimpleBlockFeatureConfig(config.toPlace)),
                List(BlockPredicateFilterPlacement(updateOld1(config.old1.get)))).process(using context)
        else if (!context.onlyUpdate)
            super.process(SimpleBlockFeatureConfig(config.toPlace.process), context)
        else
            super.process(config, context)
    }

    def updateOld1(old1: Old1): BlockPredicate = {
        if (old1.placeOn.isEmpty && old1.placeIn.isEmpty && old1.placeUnder.isEmpty)
            return TrueBlockPredicate

        val placeOn =
            if (old1.placeOn.isEmpty) TrueBlockPredicate
            else MatchingBlocksBlockPredicate(old1.placeOn.map(_.name), BlockPos(0, -1, 0))
        val placeIn =
            if (old1.placeIn.isEmpty) TrueBlockPredicate
            else MatchingBlocksBlockPredicate(old1.placeIn.map(_.name), BlockPos(0, 0, 0))
        val placeUnder =
            if (old1.placeUnder.isEmpty) TrueBlockPredicate
            else MatchingBlocksBlockPredicate(old1.placeUnder.map(_.name), BlockPos(0, 1, 0))

        AllOfBlockPredicate(List(placeOn, placeIn, placeUnder)).process
    }
}
