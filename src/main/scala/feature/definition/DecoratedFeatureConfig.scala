package de.martenschaefer.minecraft.worldgenupdater
package feature.definition

import de.martenschaefer.data.serialization.Codec
import feature.{ ConfiguredFeature, FeatureConfig }
import decorator.ConfiguredDecorator

case class DecoratedFeatureConfig(val feature: ConfiguredFeature[_, _], val decorator: ConfiguredDecorator[_, _])
    extends FeatureConfig derives Codec
