package de.martenschaefer.minecraft.worldgenupdater
package decorator.definition

import de.martenschaefer.data.serialization.Codec
import de.martenschaefer.data.util.Identifier
import de.martenschaefer.minecraft.worldgenupdater.decorator.DecoratorConfig
import de.martenschaefer.minecraft.worldgenupdater.util.BlockPos

case class BlockFilterDecoratorConfig(allowed: List[Identifier],
                                      disallowed: List[Identifier],
                                      offset: BlockPos) extends DecoratorConfig derives Codec
