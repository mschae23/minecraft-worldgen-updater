package de.martenschaefer.minecraft.worldgenupdater
package feature

import de.martenschaefer.data.registry.Registry
import de.martenschaefer.data.registry.Registry.register
import de.martenschaefer.data.registry.impl.SimpleRegistry
import de.martenschaefer.data.serialization.{ Codec, ElementError }
import de.martenschaefer.data.util.*
import feature.definition.DecoratedFeatureConfig
import feature.placement.PlacedFeature
import cats.data.Writer

class Feature[FC <: FeatureConfig](val configCodec: Codec[FC]) {
    val codec: Codec[ConfiguredFeature[FC, Feature[FC]]] =
        configCodec.fieldOf("config").xmap(config => ConfiguredFeature(this, config))(_.config)

    def configure(config: FC): ConfiguredFeature[FC, Feature[FC]] = ConfiguredFeature(this, config)

    def process(config: FC, context: FeatureUpdateContext): FeatureProcessResult =
        Writer.value(PlacedFeature(ConfiguredFeature(this, config), List.empty))

    def getPostProcessWarnings(config: FC, context: FeatureUpdateContext): List[ElementError] = List.empty

    override def toString: String = Registry[Feature[_]].getId(this).map(_.toString).getOrElse(super.toString)
}

object Feature {
    given Registry[Feature[_]] = new SimpleRegistry(Identifier("minecraft", "feature"))
}
