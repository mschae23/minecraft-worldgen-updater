package de.martenschaefer.minecraft.worldgenupdater
package feature.definition

import de.martenschaefer.data.serialization.{ Codec, ElementError, ValidationError }
import feature.definition.RandomSelectorFeatureConfig.RandomSelectorFeatureEntry
import feature.placement.{ PlacedFeature, PlacedFeatureReference }
import feature.{ ConfiguredFeature, Feature, FeatureProcessResult }
import cats.catsInstancesForId
import cats.data.Writer

case object RandomBooleanSelectorFeature extends Feature(Codec[RandomBooleanSelectorFeatureConfig]) {
    override def process(config: RandomBooleanSelectorFeatureConfig, context: FeatureUpdateContext): FeatureProcessResult = for {
        processedFeatureTrue <- config.featureTrue.process(using context).map(PlacedFeatureReference.apply)
            .mapWritten(_.map(_.withPrependedPath("feature_true").withPrependedPath("config")))
        processedFeatureFalse <- config.featureFalse.process(using context).map(PlacedFeatureReference.apply)
            .mapWritten(_.map(_.withPrependedPath("feature_false").withPrependedPath("config")))
    } yield PlacedFeature(this.configure(RandomBooleanSelectorFeatureConfig(
        processedFeatureTrue, processedFeatureFalse)), List.empty)
}
