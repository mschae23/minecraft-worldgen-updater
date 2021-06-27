package de.martenschaefer.minecraft.worldgenupdater
package decorator.definition

import de.martenschaefer.data.serialization.Codec
import decorator.Decorator

case object WaterDepthThresholdDecorator extends Decorator(Codec[WaterDepthThresholdDecoratorConfig])
