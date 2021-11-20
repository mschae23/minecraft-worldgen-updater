package de.martenschaefer.minecraft.worldgenupdater
package feature.definition

import cats.data.Writer
import de.martenschaefer.data.serialization.{ Codec, ValidationError }
import de.martenschaefer.minecraft.worldgenupdater.feature.placement.PlacedFeature
import decorator.Decorators
import decorator.definition.{ BlockFilterDecorator, BlockFilterDecoratorConfig }
import feature.{ Feature, FeatureProcessResult, Features }
import util.*
import valueprovider.{ AllOfBlockPredicate, BlockPredicate, TrueBlockPredicate }

case object VegetationPatchFeature extends Feature(Codec[VegetationPatchFeatureConfig]) {
    override def process(config: VegetationPatchFeatureConfig, context: FeatureUpdateContext): FeatureProcessResult =
        config.process(using context).map(processedConfig => PlacedFeature(this.configure(processedConfig), List.empty))
}
