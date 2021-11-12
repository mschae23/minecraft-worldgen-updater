package de.martenschaefer.minecraft.worldgenupdater
package decorator.definition

import de.martenschaefer.data.serialization.Codec
import decorator.DecoratorConfig

case class CountNoiseBiasedDecoratorConfig(noiseToCountRatio: Int,
                                           noiseFactor: Float, noiseOffset: Float) extends DecoratorConfig derives Codec
