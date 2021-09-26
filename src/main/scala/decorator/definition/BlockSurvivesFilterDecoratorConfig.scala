package de.martenschaefer.minecraft.worldgenupdater
package decorator.definition

import de.martenschaefer.data.serialization.Codec
import util.BlockState
import de.martenschaefer.minecraft.worldgenupdater.decorator.DecoratorConfig

case class BlockSurvivesFilterDecoratorConfig(state: BlockState) extends DecoratorConfig derives Codec
