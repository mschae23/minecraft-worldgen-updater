package de.martenschaefer.minecraft.worldgenupdater
package feature.definition

import de.martenschaefer.data.serialization.Codec
import feature.FeatureConfig
import de.martenschaefer.minecraft.worldgenupdater.valueprovider.BlockStateProvider

case class SimpleBlockFeatureConfig(val toPlace: BlockStateProvider) extends FeatureConfig derives Codec
