package de.martenschaefer.minecraft.worldgenupdater
package feature.definition

import de.martenschaefer.data.serialization.{ Codec, ElementError, ElementNode }
import feature.definition.RandomSelectorFeatureConfig.RandomSelectorFeatureEntry
import feature.{ ConfiguredFeature, Feature, FeatureProcessResult }
import cats.catsInstancesForId
import cats.data.Writer

case object SimpleRandomSelectorFeature extends Feature(Codec[SimpleRandomSelectorFeatureConfig]) {
    override def process(config: SimpleRandomSelectorFeatureConfig, context: FeatureUpdateContext): FeatureProcessResult = {
        val featuresResult = config.features.foldLeft(Writer[List[ElementError], (List[ConfiguredFeature[_, _]], Int)](
            List.empty, (List.empty, 0)))((writer, feature) => writer.flatMap(features =>
            feature.feature.process(feature.config, context)
                .mapWritten(_.map(_.withPrependedPath(ElementNode.Index(features._2))))
                .map(processedFeature => (processedFeature :: features._1, features._2 + 1))))

        featuresResult.mapWritten(_.map(_.withPrependedPath("features").withPrependedPath("config")))
            .map(features => this.configure(SimpleRandomSelectorFeatureConfig(features._1)))
    }
}
