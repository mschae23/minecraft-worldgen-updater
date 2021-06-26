package de.martenschaefer.minecraft.worldgenupdater
package feature.definition.tree

import de.martenschaefer.data.registry.Registry
import de.martenschaefer.data.registry.Registry.register
import de.martenschaefer.data.registry.impl.SimpleRegistry
import de.martenschaefer.data.serialization.Codec
import de.martenschaefer.data.util._
import de.martenschaefer.minecraft.worldgenupdater.valueprovider.IntProvider
import util._

trait FoliagePlacer {
    val placerType: FoliagePlacerType[_]

    val process: FoliagePlacer = this
}

object FoliagePlacer {
    given Codec[FoliagePlacer] = Registry[FoliagePlacerType[_]].dispatch(_.placerType, _.codec)
}

case class FoliagePlacerType[P <: FoliagePlacer](val codec: Codec[P])

object FoliagePlacerType {
    given Registry[FoliagePlacerType[_]] = new SimpleRegistry(Identifier("minecraft", "foliage_placer_type"))

    val BLOB_FOLIAGE_PLACER = register("blob_foliage_placer", Codec[BlobFoliagePlacer])

    private def register[P <: FoliagePlacer](name: String, codec: Codec[P]): FoliagePlacerType[P] = {
        val placerType = FoliagePlacerType(codec)

        placerType.register(Identifier("minecraft", name))
        placerType
    }
}

case class BlobFoliagePlacer(val radius: IntProvider, val offset: IntProvider, val height: Int) extends FoliagePlacer derives Codec {
    override val placerType: FoliagePlacerType[_] = FoliagePlacerType.BLOB_FOLIAGE_PLACER
}

// TODO add the remaining foliage placer types
