package de.martenschaefer.minecraft.worldgenupdater
package feature.definition

import de.martenschaefer.data.serialization.{ Codec, ElementError, ValidationError }
import feature.definition.RandomSelectorFeatureConfig.RandomSelectorFeatureEntry
import feature.placement.PlacedFeature
import feature.{ ConfiguredFeature, Feature, FeatureProcessResult }
import cats.catsInstancesForId
import cats.data.Writer

case object RandomBooleanSelectorFeature extends Feature(Codec[RandomBooleanSelectorFeatureConfig]) {
    override def process(config: RandomBooleanSelectorFeatureConfig, context: FeatureUpdateContext): FeatureProcessResult = for {
        processedFeatureTrue <- config.featureTrue.feature.process(config.featureTrue.config, context)
            .mapWritten(_.map(_.withPrependedPath("feature_true").withPrependedPath("config")))
        processedFeatureFalse <- config.featureFalse.feature.process(config.featureFalse.config, context)
            .mapWritten(_.map(_.withPrependedPath("feature_false").withPrependedPath("config")))
        usedDecorators <- Writer.value(!processedFeatureTrue.modifiers.isEmpty || !processedFeatureFalse.modifiers.isEmpty)
        resultFeature <- Writer.value(PlacedFeature(this.configure(RandomBooleanSelectorFeatureConfig(
            processedFeatureTrue.feature, processedFeatureFalse.feature)), List.empty))
        result <-
            if (usedDecorators) Writer(List(ValidationError(path => s"$path: Any decorators used in random_boolean_selector were REMOVED.", List.empty)), resultFeature)
            else Writer.value[List[ElementError], PlacedFeature](resultFeature)
    } yield result
}
