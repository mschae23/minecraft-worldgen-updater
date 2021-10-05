package de.martenschaefer.minecraft.worldgenupdater
package feature.definition

import de.martenschaefer.data.serialization.{ Codec, ElementError }
import feature.definition.RandomSelectorFeatureConfig.RandomSelectorFeatureEntry
import feature.{ ConfiguredFeature, Feature, FeatureProcessResult }
import cats.catsInstancesForId
import cats.data.Writer

case object RandomBooleanSelectorFeature extends Feature(Codec[RandomBooleanSelectorFeatureConfig]) {
    override def process(config: RandomBooleanSelectorFeatureConfig, context: FeatureUpdateContext): FeatureProcessResult = for {
        processedFeatureTrue <- config.featureTrue.feature.process(config.featureTrue.config, context)
            .mapWritten(_.map(_.withPrependedPath("feature_true").withPrependedPath("config")))
        processedFeatureFalse <- config.featureFalse.feature.process(config.featureFalse.config, context)
            .mapWritten(_.map(_.withPrependedPath("feature_false").withPrependedPath("config")))
    } yield this.configure(RandomBooleanSelectorFeatureConfig(processedFeatureTrue, processedFeatureFalse))
}
