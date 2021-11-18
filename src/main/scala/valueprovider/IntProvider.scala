package de.martenschaefer.minecraft.worldgenupdater
package valueprovider

import de.martenschaefer.data.registry.Registry
import de.martenschaefer.data.registry.Registry.register
import de.martenschaefer.data.registry.impl.SimpleRegistry
import de.martenschaefer.data.serialization.{ Codec, ValidationError }
import de.martenschaefer.data.util.*
import de.martenschaefer.data.util.DataResult.*
import util.{ DataPool, Weighted }

trait IntProvider {
    val providerType: IntProviderType[_]

    def process: IntProvider = this

    def map(mapMin: Int => Int, mapMax: Int => Int): IntProvider = this match {
        case ConstantIntProvider(value) => ConstantIntProvider(mapMin(value))
        case UniformIntProvider(min, max) => UniformIntProvider(mapMin(min), mapMax(max))
        case BiasedToBottomIntProvider(min, max) => BiasedToBottomIntProvider(mapMin(min), mapMax(max))
        case ClampedIntProvider(source, min, max) => ClampedIntProvider(source.map(mapMin, mapMax),
            mapMin(min), mapMax(max))
        case WeightedListIntProvider(distribution) => WeightedListIntProvider(DataPool(distribution.entries.map(
            weighted => Weighted.Present(weighted.data.map(mapMin, mapMax), weighted.weight))))
    }
}

object IntProvider {
    private val literalCodec: Codec[IntProvider] = Codec[Int].flatXmap(value =>
        Success(ConstantIntProvider(value))) {
        case ConstantIntProvider(value) => Success(value)
        case _ => Failure(List(ValidationError(path => s"$path: Not a constant int provider", List.empty)))
    }

    given Codec[IntProvider] = Codec.alternatives(List(literalCodec,
        Registry[IntProviderType[_]].dispatch[IntProvider](_.providerType, _.codec)))

    IntProviderTypes // init
}

case class IntProviderType[P <: IntProvider](codec: Codec[P])

object IntProviderType {
    given Registry[IntProviderType[_]] = new SimpleRegistry(Identifier("minecraft", "int_provider_type"))
}

object IntProviderTypes {
    val CONSTANT = register("constant", Codec[ConstantIntProvider])
    val UNIFORM = register("uniform", Codec[UniformIntProvider])
    val BIASED_TO_BOTTOM = register("biased_to_bottom", Codec[BiasedToBottomIntProvider])
    val CLAMPED = register("clamped", Codec[ClampedIntProvider])
    val WEIGHTED_LIST = register("weighted_list", Codec[WeightedListIntProvider])

    private def register[P <: IntProvider](name: String, codec: Codec[P]): IntProviderType[P] = {
        val providerType = IntProviderType(codec)

        providerType.register(Identifier("minecraft", name))
        providerType
    }
}

case class ConstantIntProvider(value: Int) extends IntProvider derives Codec {
    override val providerType: IntProviderType[_] = IntProviderTypes.CONSTANT
}

case class UniformIntProvider(minInclusive: Int, maxInclusive: Int) extends IntProvider derives Codec {
    override val providerType: IntProviderType[_] = IntProviderTypes.UNIFORM

    override def process: IntProvider =
        if (this.minInclusive == this.maxInclusive) ConstantIntProvider(this.minInclusive)
        else this
}

case class BiasedToBottomIntProvider(minInclusive: Int, maxInclusive: Int) extends IntProvider derives Codec {
    override val providerType: IntProviderType[_] = IntProviderTypes.BIASED_TO_BOTTOM

    override def process: IntProvider =
        if (this.minInclusive == this.maxInclusive) ConstantIntProvider(this.minInclusive)
        else this
}

case class ClampedIntProvider(source: IntProvider, minInclusive: Int, maxInclusive: Int) extends IntProvider derives Codec {
    override val providerType: IntProviderType[_] = IntProviderTypes.CLAMPED

    override def process: IntProvider = source match {
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

case class WeightedListIntProvider(distribution: DataPool[IntProvider]) extends IntProvider derives Codec {
    override val providerType: IntProviderType[_] = IntProviderTypes.WEIGHTED_LIST

    override def process: IntProvider = this.distribution.entries match {
        case head :: Nil => head.data.process
        case Nil => this

        case _ => val pool = DataPool(this.distribution.entries.map(weighted =>
            Weighted.Present(weighted.data.process, weighted.weight))).process

            pool.entries match {
                case head :: Nil => head.data.process
                case _ => WeightedListIntProvider(pool)
            }
    }
}
