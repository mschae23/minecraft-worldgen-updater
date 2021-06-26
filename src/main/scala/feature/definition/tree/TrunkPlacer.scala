package de.martenschaefer.minecraft.worldgenupdater
package feature.definition.tree

import de.martenschaefer.data.registry.Registry
import de.martenschaefer.data.registry.Registry.register
import de.martenschaefer.data.registry.impl.SimpleRegistry
import de.martenschaefer.data.serialization.Codec
import de.martenschaefer.data.util._
import de.martenschaefer.minecraft.worldgenupdater.valueprovider.IntProvider
import util._

trait TrunkPlacer {
    val placerType: TrunkPlacerType[_]

    val process: TrunkPlacer = this
}

object TrunkPlacer {
    given Codec[TrunkPlacer] = Registry[TrunkPlacerType[_]].dispatch(_.placerType, _.codec)
}

case class TrunkPlacerType[P <: TrunkPlacer](val codec: Codec[P])

object TrunkPlacerType {
    given Registry[TrunkPlacerType[_]] = new SimpleRegistry(Identifier("minecraft", "trunk_placer_type"))

    val STRAIGHT_TRUNK_PLACER = registerDefault("straight_trunk_placer")
    val FORKING_TRUNK_PLACER = registerDefault("forking_trunk_placer")
    val GIANT_TRUNK_PLACER = registerDefault("giant_trunk_placer")
    val MEGA_JUNGLE_TRUNK_PLACER = registerDefault("mega_jungle_trunk_placer")
    val DARK_OAK_TRUNK_PLACER = registerDefault("dark_oak_trunk_placer")
    val FANCY_TRUNK_PLACER = registerDefault("fancy_trunk_placer")
    val BENDING_TRUNK_PLACER = register("bending_trunk_placer", Codec[BendingTrunkPlacer])

    private def registerDefault(name: String): TrunkPlacerType[DefaultTrunkPlacer] = {
        val placerType = new TrunkPlacerType[DefaultTrunkPlacer](null) {
            override val codec: Codec[DefaultTrunkPlacer] = DefaultTrunkPlacer.createCodec(this)
        }

        placerType.register(Identifier("minecraft", name))
        placerType
    }

    private def register[P <: TrunkPlacer](name: String, codec: Codec[P]): TrunkPlacerType[P] = {
        val placerType = TrunkPlacerType(codec)

        placerType.register(Identifier("minecraft", name))
        placerType
    }
}

case class DefaultTrunkPlacer(val baseHeight: Int, val heightRandA: Int, val heightRandB: Int, override val placerType: TrunkPlacerType[_])
    extends TrunkPlacer

object DefaultTrunkPlacer {
    def createCodec(placerType: TrunkPlacerType[_]): Codec[DefaultTrunkPlacer] = Codec.record {
        val baseHeight = Codec[Int].fieldOf("base_height").forGetter[DefaultTrunkPlacer](_.baseHeight)
        val heightRandA = Codec[Int].fieldOf("height_rand_a").forGetter[DefaultTrunkPlacer](_.heightRandA)
        val heightRandB = Codec[Int].fieldOf("height_rand_b").forGetter[DefaultTrunkPlacer](_.heightRandB)

        Codec.build(DefaultTrunkPlacer(baseHeight.get, heightRandA.get, heightRandB.get, placerType))
    }
}

case class BendingTrunkPlacer(val baseHeight: Int,
                              val heightRandA: Int,
                              val heightRandB: Int,
                              val minHeightForLeaves: Int,
                              val bendLength: IntProvider) extends TrunkPlacer {
    override val placerType: TrunkPlacerType[_] = TrunkPlacerType.BENDING_TRUNK_PLACER
}

object BendingTrunkPlacer {
    given Codec[BendingTrunkPlacer] = Codec.record {
        val baseHeight = Codec[Int].fieldOf("base_height").forGetter[BendingTrunkPlacer](_.baseHeight)
        val heightRandA = Codec[Int].fieldOf("height_rand_a").forGetter[BendingTrunkPlacer](_.heightRandA)
        val heightRandB = Codec[Int].fieldOf("height_rand_b").forGetter[BendingTrunkPlacer](_.heightRandB)
        val minHeightForLeaves = Codec[Int].orElse(1).fieldOf("min_height_for_leaves").forGetter[BendingTrunkPlacer](_.minHeightForLeaves)
        val bendLength = Codec[IntProvider].fieldOf("bend_length").forGetter[BendingTrunkPlacer](_.bendLength)

        Codec.build(BendingTrunkPlacer(baseHeight.get, heightRandA.get, heightRandB.get, minHeightForLeaves.get, bendLength.get))
    }
}
