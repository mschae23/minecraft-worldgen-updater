package de.martenschaefer.minecraft.worldgenupdater
package feature.definition

import de.martenschaefer.data.serialization.{ Codec, ValidationError }
// import feature.definition.RandomPatchFeatureConfig.Current
import feature.{ Feature, FeatureProcessResult, Features }
import util.*
import cats.data.Writer

case object RandomPatchFeature extends Feature(Codec[RandomPatchFeatureConfig]) {
    override def process(config: RandomPatchFeatureConfig, context: FeatureUpdateContext): FeatureProcessResult = {
        super.process(config, context)
    }
}
