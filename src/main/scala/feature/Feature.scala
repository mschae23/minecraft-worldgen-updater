package de.martenschaefer.minecraft.worldgenupdater
package feature

import cats.data.Writer
import de.martenschaefer.data.registry.Registry
import de.martenschaefer.data.registry.Registry.register
import de.martenschaefer.data.registry.impl.SimpleRegistry
import de.martenschaefer.data.serialization.Codec
import de.martenschaefer.data.util._
import feature.definition.DecoratedFeatureConfig

class Feature[FC <: FeatureConfig](val configCodec: Codec[FC]) {
    val codec: Codec[ConfiguredFeature[FC, Feature[FC]]] =
        configCodec.fieldOf("config").xmap(config => ConfiguredFeature(this, config))(_.config)

    def configure(config: FC): ConfiguredFeature[FC, Feature[FC]] = ConfiguredFeature(this, config)

    def process(config: FC): FeatureProcessResult = Writer(List(), ConfiguredFeature(this, config))

    override def toString: String = Registry[Feature[_]].getId(this).map(_.toString).getOrElse(super.toString)
}

object Feature {
    given Registry[Feature[_]] = new SimpleRegistry(Identifier("minecraft", "feature"))
}
