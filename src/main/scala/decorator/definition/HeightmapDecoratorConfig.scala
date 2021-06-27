package de.martenschaefer.minecraft.worldgenupdater
package decorator.definition

import de.martenschaefer.data.serialization.Codec
import de.martenschaefer.data.util._
import decorator.DecoratorConfig
import util.HeightmapType

case class HeightmapDecoratorConfig(val heightmap: HeightmapType) extends DecoratorConfig derives Codec
