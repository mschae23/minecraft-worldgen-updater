package de.martenschaefer.minecraft.worldgenupdater
package feature.definition

import de.martenschaefer.data.serialization.Codec
import feature.FeatureConfig
import feature.definition.OreFeatureConfig.Target
import util.{ BlockState, RuleTest }

case class OreFeatureConfig(targets: List[Target], size: Int, discardChanceOnAirExposure: Float) extends FeatureConfig

object OreFeatureConfig {
    case class Target(target: RuleTest, state: BlockState) extends FeatureConfig derives Codec

    case class Old1(target: RuleTest, state: BlockState, size: Int) derives Codec

    val old1Codec: Codec[OreFeatureConfig] = Codec[Old1].xmap(old1 =>
        OreFeatureConfig(List(Target(old1.target, old1.state)), old1.size, 0f))(_ => null)

    val old2Codec = Codec.record {
        val targets = Codec[List[Target]].fieldOf("targets").forGetter[OreFeatureConfig](_.targets)
        val size = Codec[Int].fieldOf("size").forGetter[OreFeatureConfig](_.size)

        Codec.build(OreFeatureConfig(targets.get, size.get, 0f))
    }

    given Codec[OreFeatureConfig] = Codec.alternatives(List(Codec.derived[OreFeatureConfig], old2Codec, old1Codec))
}
