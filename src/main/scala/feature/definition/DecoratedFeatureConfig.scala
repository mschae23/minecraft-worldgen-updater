package de.martenschaefer.minecraft.worldgenupdater
package feature.definition

import de.martenschaefer.data.serialization.Codec
import feature.{ ConfiguredFeature, FeatureConfig }
import decorator.ConfiguredDecorator

case class DecoratedFeatureConfig(feature: ConfiguredFeature[_, _], decorator: ConfiguredDecorator[_, _])
    extends FeatureConfig derives Codec
