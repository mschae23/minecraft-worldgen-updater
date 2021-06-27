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
    val SPRUCE_FOLIAGE_PLACER = register("spruce_foliage_placer", Codec[SpruceFoliagePlacer])
    val PINE_FOLIAGE_PLACER = register("pine_foliage_placer", Codec[PineFoliagePlacer])
    val ACACIA_FOLIAGE_PLACER = register("acacia_foliage_placer", Codec[AcaciaFoliagePlacer])
    val BUSH_FOLIAGE_PLACER = register("bush_foliage_placer", Codec[BushFoliagePlacer])
    val FANCY_FOLIAGE_PLACER = register("fancy_foliage_placer", Codec[FancyFoliagePlacer])
    val JUNGLE_FOLIAGE_PLACER = register("jungle_foliage_placer", Codec[JungleFoliagePlacer])
    val MEGA_PINE_FOLIAGE_PLACER = register("mega_pine_foliage_placer", Codec[MegaPineFoliagePlacer])
    val DARK_OAK_FOLIAGE_PLACER = register("dark_oak_foliage_placer", Codec[DarkOakFoliagePlacer])
    val RANDOM_SPREAD_FOLIAGE_PLACER = register("random_spread_foliage_placer", Codec[RandomSpreadFoliagePlacer])

    private def register[P <: FoliagePlacer](name: String, codec: Codec[P]): FoliagePlacerType[P] = {
        val placerType = FoliagePlacerType(codec)

        placerType.register(Identifier("minecraft", name))
        placerType
    }
}

case class BlobFoliagePlacer(val radius: IntProvider, val offset: IntProvider, val height: Int) extends FoliagePlacer derives Codec {
    override val placerType: FoliagePlacerType[_] = FoliagePlacerType.BLOB_FOLIAGE_PLACER
}

case class SpruceFoliagePlacer(val radius: IntProvider, val offset: IntProvider, val trunkHeight: IntProvider) extends FoliagePlacer derives Codec {
    override val placerType: FoliagePlacerType[_] = FoliagePlacerType.SPRUCE_FOLIAGE_PLACER
}

case class PineFoliagePlacer(val radius: IntProvider, val offset: IntProvider, val height: IntProvider) extends FoliagePlacer derives Codec {
    override val placerType: FoliagePlacerType[_] = FoliagePlacerType.PINE_FOLIAGE_PLACER
}

case class AcaciaFoliagePlacer(val radius: IntProvider, val offset: IntProvider) extends FoliagePlacer derives Codec {
    override val placerType: FoliagePlacerType[_] = FoliagePlacerType.ACACIA_FOLIAGE_PLACER
}

case class BushFoliagePlacer(val radius: IntProvider, val offset: IntProvider, val height: Int) extends FoliagePlacer derives Codec {
    override val placerType: FoliagePlacerType[_] = FoliagePlacerType.BUSH_FOLIAGE_PLACER
}

case class FancyFoliagePlacer(val radius: IntProvider, val offset: IntProvider, val height: Int) extends FoliagePlacer derives Codec {
    override val placerType: FoliagePlacerType[_] = FoliagePlacerType.FANCY_FOLIAGE_PLACER
}

case class JungleFoliagePlacer(val radius: IntProvider, val offset: IntProvider, val height: Int) extends FoliagePlacer derives Codec {
    override val placerType: FoliagePlacerType[_] = FoliagePlacerType.JUNGLE_FOLIAGE_PLACER
}

case class MegaPineFoliagePlacer(val radius: IntProvider, val offset: IntProvider, val crownHeight: IntProvider) extends FoliagePlacer derives Codec {
    override val placerType: FoliagePlacerType[_] = FoliagePlacerType.MEGA_PINE_FOLIAGE_PLACER
}

case class DarkOakFoliagePlacer(val radius: IntProvider, val offset: IntProvider) extends FoliagePlacer derives Codec {
    override val placerType: FoliagePlacerType[_] = FoliagePlacerType.DARK_OAK_FOLIAGE_PLACER
}

case class RandomSpreadFoliagePlacer(val radius: IntProvider, val offset: IntProvider,
                                     val foliageHeight: IntProvider, val leafPlacementAttempts: Int) extends FoliagePlacer derives Codec {
    override val placerType: FoliagePlacerType[_] = FoliagePlacerType.RANDOM_SPREAD_FOLIAGE_PLACER
}
