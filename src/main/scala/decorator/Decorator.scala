package de.martenschaefer.minecraft.worldgenupdater
package decorator

import cats.data.Writer
import de.martenschaefer.data.registry.Registry
import de.martenschaefer.data.registry.impl.SimpleRegistry
import de.martenschaefer.data.registry.Registry.register
import de.martenschaefer.data.serialization.Codec
import de.martenschaefer.data.util._
import decorator.definition._
import feature.{ ConfiguredFeature, Features, FeatureProcessResult }
import feature.definition.DecoratedFeatureConfig

class Decorator[DC <: DecoratorConfig](configCodec: Codec[DC]) {
    val codec: Codec[ConfiguredDecorator[DC, Decorator[DC]]] =
        configCodec.fieldOf("config").xmap(config => ConfiguredDecorator(this, config))(_.config)

    def configure(config: DC): ConfiguredDecorator[DC, Decorator[DC]] = ConfiguredDecorator(this, config)

    def process(config: DC, feature: ConfiguredFeature[_, _]): FeatureProcessResult =
        Writer(List.empty, ConfiguredFeature(Features.DECORATED, DecoratedFeatureConfig(feature, ConfiguredDecorator(this, config))))

    override def toString: String = Registry[Decorator[_]].getId(this).map(_.toString).getOrElse(super.toString)
}

object Decorator {
    given Registry[Decorator[_]] = new SimpleRegistry(Identifier("minecraft", "decorator"))
}
