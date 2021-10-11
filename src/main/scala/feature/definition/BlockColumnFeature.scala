package de.martenschaefer.minecraft.worldgenupdater
package feature.definition

import de.martenschaefer.data.serialization.{ Codec, ValidationError }
import decorator.Decorators
import decorator.definition.{ BlockFilterDecorator, BlockFilterDecoratorConfig }
import valueprovider.{ AllOfBlockPredicate, BlockPredicate, TrueBlockPredicate }
import cats.data.Writer
import feature.{ Feature, FeatureProcessResult, Features }
import util.*

case object BlockColumnFeature extends Feature(Codec[BlockColumnFeatureConfig]) {
    override def process(config: BlockColumnFeatureConfig, context: FeatureUpdateContext): FeatureProcessResult = {
        if (context.onlyUpdate)
            super.process(config, context)
        else
            super.process(BlockColumnFeatureConfig(config.layers.map(_.process), config.direction,
                config.allowedPlacement.process, config.prioritizeTip), context)
    }
}
