package de.martenschaefer.minecraft.worldgenupdater
package decorator.definition

import de.martenschaefer.data.serialization.Codec
import de.martenschaefer.data.util._
import decorator.DecoratorConfig
import valueprovider.{ ConstantIntProvider, IntProvider }

case class CountDecoratorConfig(val count: IntProvider) extends DecoratorConfig derives Codec {
    def process: CountDecoratorConfig = CountDecoratorConfig(this.count.process)
}
