package de.martenschaefer.minecraft.worldgenupdater
package feature.definition

import de.martenschaefer.data.serialization.{ Codec, ElementError, ElementNode }
import feature.definition.RandomSelectorFeatureConfig.RandomSelectorFeatureEntry
import feature.{ Feature, FeatureProcessResult }
import cats.catsInstancesForId
import cats.data.Writer

case object RandomSelectorFeature extends Feature(Codec[RandomSelectorFeatureConfig]) {
    override def process(config: RandomSelectorFeatureConfig, context: FeatureUpdateContext): FeatureProcessResult = {
        val featuresResult = config.features.foldLeft(Writer[List[ElementError], (List[RandomSelectorFeatureEntry], Int)](List.empty, (List.empty, 0)))(
            (writer, feature) => writer.flatMap(features => feature.feature.feature.process(feature.feature.config, context)
                .mapWritten(_.map(_.withPrependedPath(ElementNode.Index(features._2)).withPrependedPath("features")))
                .map(processedFeature => (RandomSelectorFeatureEntry(processedFeature, feature.chance) :: features._1, features._2 + 1))))

        featuresResult.flatMap(features => config.default.feature.process(config.default.config, context)
            .mapWritten(_.map(_.withPrependedPath("default"))).map(processedDefault =>
            this.configure(RandomSelectorFeatureConfig(features._1, processedDefault))))
            .mapWritten(_.map(_.withPrependedPath("config")))
    }
}
