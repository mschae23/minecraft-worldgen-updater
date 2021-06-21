package de.martenschaefer.minecraft.worldgenupdater
package feature.definition

import de.martenschaefer.data.serialization.Codec
import feature.{ ConfiguredFeature, FeatureConfig }
import decorator.ConfiguredDecorator

case class ArrayDecoratedFeatureConfig(val feature: ConfiguredFeature[_, _], val decorators: List[ConfiguredDecorator[_, _]])
    extends FeatureConfig derives Codec
