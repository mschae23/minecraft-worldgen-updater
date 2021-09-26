package de.martenschaefer.minecraft.worldgenupdater
package util

import de.martenschaefer.data.serialization.Codec

case class NoiseParameters(firstOctave: Int, amplitudes: List[Double])

object NoiseParameters {
    given Codec[NoiseParameters] = Codec.record {
        val firstOctave = Codec[Int].fieldOf("firstOctave").forGetter[NoiseParameters](_.firstOctave)
        val amplitudes = Codec[List[Double]].fieldOf("amplitudes").forGetter[NoiseParameters](_.amplitudes)

        Codec.build(NoiseParameters(firstOctave.get, amplitudes.get))
    }
}
