package de.martenschaefer.minecraft.worldgenupdater
package valueprovider

import de.martenschaefer.data.registry.Registry
import de.martenschaefer.data.registry.Registry.register
import de.martenschaefer.data.registry.impl.SimpleRegistry
import de.martenschaefer.data.serialization.Codec
import de.martenschaefer.data.util._
import de.martenschaefer.minecraft.worldgenupdater.util.YOffset

trait HeightProvider {
    val providerType: HeightProviderType[_]

    val process: HeightProvider = this
}

object HeightProvider {
    val errorMsg = (path: String) =>
        s"$path can be an int, or a \"constant\", \"uniform\", \"biased_to_bottom\", \"very_biased_to_bottom\", or \"trapezoid\" height provider"

    given Codec[HeightProvider] = Codec.either(errorMsg)(using Codec[Int], Registry[HeightProviderType[_]]
        .dispatch[HeightProvider](_.providerType, _.codec)).xmap(_ match {
        case Left(value) => ConstantHeightProvider(YOffset.Fixed(value))
        case Right(provider) => provider
    })(_ match {
        case ConstantHeightProvider(YOffset.Fixed(value)) => Left(value)
        case provider => Right(provider)
    })
}

case class HeightProviderType[P <: HeightProvider](val codec: Codec[P])

object HeightProviderType {
    given Registry[HeightProviderType[_]] = new SimpleRegistry(Identifier("minecraft", "height_provider_type"))

    val CONSTANT = register("constant", Codec[ConstantHeightProvider])
    val UNIFORM = register("uniform", Codec[UniformHeightProvider])
    val BIASED_TO_BOTTOM = register("biased_to_bottom", Codec[BiasedToBottomHeightProvider])
    val VERY_BIASED_TO_BOTTOM = register("very_biased_to_bottom", Codec[VeryBiasedToBottomHeightProvider])
    val TRAPEZOID = register("trapezoid", Codec[TrapezoidHeightProvider])

    private def register[P <: HeightProvider](name: String, codec: Codec[P]): HeightProviderType[P] = {
        val providerType = HeightProviderType(codec)

        providerType.register(Identifier("minecraft", name))
        providerType
    }
}

case class ConstantHeightProvider(val value: YOffset) extends HeightProvider derives Codec {
    override val providerType: HeightProviderType[_] = HeightProviderType.CONSTANT
}

case class UniformHeightProvider(val minInclusive: YOffset, val maxInclusive: YOffset) extends HeightProvider derives Codec {
    override val providerType: HeightProviderType[_] = HeightProviderType.UNIFORM

    override val process: HeightProvider =
        if (this.minInclusive == this.maxInclusive) ConstantHeightProvider(this.minInclusive)
        else this
}

case class BiasedToBottomHeightProvider(val minInclusive: YOffset, val maxInclusive: YOffset, val inner: Int = 1) extends HeightProvider {
    override val providerType: HeightProviderType[_] = HeightProviderType.BIASED_TO_BOTTOM
}

object BiasedToBottomHeightProvider {
    given Codec[BiasedToBottomHeightProvider] = Codec.record {
        val minInclusive = Codec[YOffset].fieldOf("min_inclusive").forGetter[BiasedToBottomHeightProvider](_.minInclusive)
        val maxInclusive = Codec[YOffset].fieldOf("max_inclusive").forGetter[BiasedToBottomHeightProvider](_.maxInclusive)
        val inner = Codec[Int].orElse(1).fieldOf("inner").forGetter[BiasedToBottomHeightProvider](_.inner)

        Codec.build(BiasedToBottomHeightProvider(minInclusive.get, maxInclusive.get, inner.get))
    }
}

case class VeryBiasedToBottomHeightProvider(val minInclusive: YOffset, val maxInclusive: YOffset, val inner: Int = 1) extends HeightProvider {
    override val providerType: HeightProviderType[_] = HeightProviderType.VERY_BIASED_TO_BOTTOM
}

object VeryBiasedToBottomHeightProvider {
    given Codec[VeryBiasedToBottomHeightProvider] = Codec.record {
        val minInclusive = Codec[YOffset].fieldOf("min_inclusive").forGetter[VeryBiasedToBottomHeightProvider](_.minInclusive)
        val maxInclusive = Codec[YOffset].fieldOf("max_inclusive").forGetter[VeryBiasedToBottomHeightProvider](_.maxInclusive)
        val inner = Codec[Int].orElse(1).fieldOf("inner").forGetter[VeryBiasedToBottomHeightProvider](_.inner)

        Codec.build(VeryBiasedToBottomHeightProvider(minInclusive.get, maxInclusive.get, inner.get))
    }
}

case class TrapezoidHeightProvider(val minInclusive: YOffset, val maxInclusive: YOffset, val plateau: Int = 0) extends HeightProvider {
    override val providerType: HeightProviderType[_] = HeightProviderType.TRAPEZOID
}

object TrapezoidHeightProvider {
    given Codec[TrapezoidHeightProvider] = Codec.record {
        val minInclusive = Codec[YOffset].fieldOf("min_inclusive").forGetter[TrapezoidHeightProvider](_.minInclusive)
        val maxInclusive = Codec[YOffset].fieldOf("max_inclusive").forGetter[TrapezoidHeightProvider](_.maxInclusive)
        val plateau = Codec[Int].orElse(0).fieldOf("plateau").forGetter[TrapezoidHeightProvider](_.plateau)

        Codec.build(TrapezoidHeightProvider(minInclusive.get, maxInclusive.get, plateau.get))
    }
}