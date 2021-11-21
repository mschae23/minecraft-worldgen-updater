package de.martenschaefer.minecraft.worldgenupdater
package feature.definition

import de.martenschaefer.data.serialization.Codec
import de.martenschaefer.data.util.Version
import decorator.ConfiguredDecorator
import feature.{ ConfiguredFeature, FeatureConfig }

case class ArrayDecoratedFeatureConfig(feature: ConfiguredFeature[_, _],
                                       decorators: List[ConfiguredDecorator[_, _]]) extends FeatureConfig

object ArrayDecoratedFeatureConfig {
    given Codec[ArrayDecoratedFeatureConfig] = Codec.derived[ArrayDecoratedFeatureConfig]
        .deprecated(Version.Semver(2, 0, 0, List("pre", "5")))
}
