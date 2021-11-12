package de.martenschaefer.minecraft.worldgenupdater
package decorator.definition

import de.martenschaefer.data.serialization.Codec
import de.martenschaefer.minecraft.worldgenupdater.util.VerticalDirection
import de.martenschaefer.minecraft.worldgenupdater.valueprovider.BlockPredicate
import decorator.DecoratorConfig

case class EnvironmentScanDecoratorConfig(directionOfSearch: VerticalDirection,
                                          targetCondition: BlockPredicate,
                                          maxSteps: Int) extends DecoratorConfig derives Codec
