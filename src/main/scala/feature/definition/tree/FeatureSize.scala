package de.martenschaefer.minecraft.worldgenupdater
package feature.definition.tree

import de.martenschaefer.data.registry.Registry
import de.martenschaefer.data.registry.Registry.register
import de.martenschaefer.data.registry.impl.SimpleRegistry
import de.martenschaefer.data.serialization.Codec
import de.martenschaefer.data.util._
import de.martenschaefer.minecraft.worldgenupdater.valueprovider.IntProvider
import util._

trait FeatureSize {
    val sizeType: FeatureSizeType[_]

    val process: FeatureSize = this
}

object FeatureSize {
    given Codec[FeatureSize] = Registry[FeatureSizeType[_]].dispatch(_.sizeType, _.codec)
}

case class FeatureSizeType[P <: FeatureSize](val codec: Codec[P])

object FeatureSizeType {
    given Registry[FeatureSizeType[_]] = new SimpleRegistry(Identifier("minecraft", "feature_size_type"))

    val TWO_LAYERS_FEATURE_SIZE = register("two_layers_feature_size", Codec[TwoLayersFeatureSize])
    val THREE_LAYERS_FEATURE_SIZE = register("three_layers_feature_size", Codec[ThreeLayersFeatureSize])

    private def register[P <: FeatureSize](name: String, codec: Codec[P]): FeatureSizeType[P] = {
        val sizeType = FeatureSizeType(codec)

        sizeType.register(Identifier("minecraft", name))
        sizeType
    }
}

case class TwoLayersFeatureSize(val limit: Int, val lowerSize: Int, val upperSize: Int) extends FeatureSize {
    val sizeType = FeatureSizeType.TWO_LAYERS_FEATURE_SIZE
}

object TwoLayersFeatureSize {
    given Codec[TwoLayersFeatureSize] = Codec.record {
        val limit = Codec[Int].orElse(1).fieldOf("limit").forGetter[TwoLayersFeatureSize](_.limit)
        val lowerSize = Codec[Int].orElse(0).fieldOf("lower_size").forGetter[TwoLayersFeatureSize](_.lowerSize)
        val upperSize = Codec[Int].orElse(1).fieldOf("upper_size").forGetter[TwoLayersFeatureSize](_.upperSize)

        Codec.build(TwoLayersFeatureSize(limit.get, lowerSize.get, upperSize.get))
    }
}

case class ThreeLayersFeatureSize(val limit: Int, val upperLimit: Int, val lowerSize: Int, val middleSize: Int, val upperSize: Int) extends FeatureSize {
    val sizeType = FeatureSizeType.THREE_LAYERS_FEATURE_SIZE
}

object ThreeLayersFeatureSize {
    given Codec[ThreeLayersFeatureSize] = Codec.record {
        val limit = Codec[Int].orElse(1).fieldOf("limit").forGetter[ThreeLayersFeatureSize](_.limit)
        val upperLimit = Codec[Int].orElse(1).fieldOf("upper_limit").forGetter[ThreeLayersFeatureSize](_.upperLimit)
        val lowerSize = Codec[Int].orElse(0).fieldOf("lower_size").forGetter[ThreeLayersFeatureSize](_.lowerSize)
        val middleSize = Codec[Int].orElse(1).fieldOf("middle_size").forGetter[ThreeLayersFeatureSize](_.middleSize)
        val upperSize = Codec[Int].orElse(1).fieldOf("upper_size").forGetter[ThreeLayersFeatureSize](_.upperSize)

        Codec.build(ThreeLayersFeatureSize(limit.get, upperLimit.get, lowerSize.get, middleSize.get, upperSize.get))
    }
}
