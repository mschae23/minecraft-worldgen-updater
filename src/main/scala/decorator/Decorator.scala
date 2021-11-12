package de.martenschaefer.minecraft.worldgenupdater
package decorator

import de.martenschaefer.data.registry.Registry
import de.martenschaefer.data.registry.Registry.register
import de.martenschaefer.data.registry.impl.SimpleRegistry
import de.martenschaefer.data.serialization.Codec
import de.martenschaefer.data.util.*
import decorator.definition.*
import feature.definition.DecoratedFeatureConfig
import feature.placement.PlacedFeature
import feature.{ ConfiguredFeature, FeatureProcessResult, Features }
import cats.data.Writer

abstract class Decorator[DC <: DecoratorConfig](configCodec: Codec[DC]) {
    val codec: Codec[ConfiguredDecorator[DC, Decorator[DC]]] =
        configCodec.fieldOf("config").xmap(config => ConfiguredDecorator(this, config))(_.config)

    def configure(config: DC): ConfiguredDecorator[DC, Decorator[DC]] = ConfiguredDecorator(this, config)

    def process(config: DC, feature: PlacedFeature, context: FeatureUpdateContext): FeatureProcessResult

    override def toString: String = Registry[Decorator[_]].getId(this).map(_.toString).getOrElse(super.toString)
}

object Decorator {
    given Registry[Decorator[_]] = new SimpleRegistry(Identifier("minecraft", "decorator"))
}
