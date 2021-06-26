package de.martenschaefer.minecraft.worldgenupdater
package valueprovider

import de.martenschaefer.data.registry.Registry
import de.martenschaefer.data.registry.Registry.register
import de.martenschaefer.data.registry.impl.SimpleRegistry
import de.martenschaefer.data.serialization.Codec
import de.martenschaefer.data.util._

trait IntProvider {
    val providerType: IntProviderType[_]

    val process: IntProvider = this
}

object IntProvider {
    val errorMsg = (path: String) =>
        s"$path can be an int, or a \"constant\", \"uniform\", \"biased_to_bottom\", or \"clamped\" height provider"

    given Codec[IntProvider] = Codec.either(errorMsg)(using Codec[Int], Registry[IntProviderType[_]]
        .dispatch[IntProvider](_.providerType, _.codec)).xmap(_ match {
        case Left(value) => ConstantIntProvider(value)
        case Right(provider) => provider
    })(_ match {
        case ConstantIntProvider(value) => Left(value)
        case provider => Right(provider)
    })

    IntProviderTypes // init
}

case class IntProviderType[P <: IntProvider](val codec: Codec[P])

object IntProviderType {
    given Registry[IntProviderType[_]] = new SimpleRegistry(Identifier("minecraft", "int_provider_type"))
}

object IntProviderTypes {
    val CONSTANT = register("constant", Codec[ConstantIntProvider])
    val UNIFORM = register("uniform", Codec[UniformIntProvider])
    val BIASED_TO_BOTTOM = register("biased_to_bottom", Codec[BiasedToBottomIntProvider])
    val CLAMPED = register("clamped", Codec[ClampedIntProvider])

    private def register[P <: IntProvider](name: String, codec: Codec[P]): IntProviderType[P] = {
        val providerType = IntProviderType(codec)

        providerType.register(Identifier("minecraft", name))
        providerType
    }
}

case class ConstantIntProvider(val value: Int) extends IntProvider derives Codec {
    override val providerType: IntProviderType[_] = IntProviderTypes.CONSTANT
}

case class UniformIntProvider(val minInclusive: Int, val maxInclusive: Int) extends IntProvider derives Codec {
    override val providerType: IntProviderType[_] = IntProviderTypes.UNIFORM

    override val process: IntProvider =
        if (this.minInclusive == this.maxInclusive) ConstantIntProvider(this.minInclusive)
        else this
}

case class BiasedToBottomIntProvider(val minInclusive: Int, val maxInclusive: Int) extends IntProvider derives Codec {
    override val providerType: IntProviderType[_] = IntProviderTypes.BIASED_TO_BOTTOM

    override val process: IntProvider =
        if (this.minInclusive == this.maxInclusive) ConstantIntProvider(this.minInclusive)
        else this
}

case class ClampedIntProvider(val source: IntProvider, val minInclusive: Int, val maxInclusive: Int) extends IntProvider derives Codec {
    override val providerType: IntProviderType[_] = IntProviderTypes.CLAMPED

    override val process: IntProvider = source match {
        case ConstantIntProvider(value) if value >= this.minInclusive && value <= this.maxInclusive =>
            ConstantIntProvider(value).process
        case UniformIntProvider(minInclusive, maxInclusive) if minInclusive >= this.minInclusive && maxInclusive <= this.maxInclusive =>
            UniformIntProvider(minInclusive, maxInclusive).process
        case BiasedToBottomIntProvider(minInclusive, maxInclusive)
            if minInclusive >= this.minInclusive && maxInclusive <= this.maxInclusive =>
            BiasedToBottomIntProvider(minInclusive, maxInclusive).process
        case ClampedIntProvider(source, minInclusive, maxInclusive)
            if minInclusive >= this.minInclusive && maxInclusive <= this.maxInclusive => source.process

        case _ if this.minInclusive == this.maxInclusive => this.source.process

        case _ => ClampedIntProvider(this.source.process, this.minInclusive, this.maxInclusive)
    }
}
