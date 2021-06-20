package de.martenschaefer.minecraft.worldgenupdater
package feature

import de.martenschaefer.data.serialization.{ Codec, Element }

case class DefaultFeature(val encoded: Element) extends Feature[DefaultFeatureConfig](Codec[DefaultFeatureConfig])
