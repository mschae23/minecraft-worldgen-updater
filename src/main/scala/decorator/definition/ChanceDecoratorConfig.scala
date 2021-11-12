package de.martenschaefer.minecraft.worldgenupdater
package decorator.definition

import de.martenschaefer.data.serialization.Codec
import decorator.DecoratorConfig

case class ChanceDecoratorConfig(chance: Int) extends DecoratorConfig derives Codec
