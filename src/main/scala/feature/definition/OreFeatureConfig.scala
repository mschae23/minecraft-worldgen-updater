package de.martenschaefer.minecraft.worldgenupdater
package feature.definition

import de.martenschaefer.data.serialization.Codec
import de.martenschaefer.minecraft.worldgenupdater.feature.definition.OreFeatureConfig.Target
import de.martenschaefer.minecraft.worldgenupdater.util.{ BlockState, RuleTest }
import feature.FeatureConfig

case class OreFeatureConfig(val targets: List[Target], val size: Int, val discardChanceOnAirExposure: Float) extends FeatureConfig

object OreFeatureConfig {
    case class Target(val target: RuleTest, val state: BlockState) extends FeatureConfig derives Codec

    case class Old1(val target: RuleTest, val state: BlockState, val size: Int) derives Codec

    val old1Codec: Codec[OreFeatureConfig] = Codec[Old1].xmap(old1 =>
        OreFeatureConfig(List(Target(old1.target, old1.state)), old1.size, 0f))(_ => null)

    val old2Codec = Codec.record {
        val targets = Codec[List[Target]].fieldOf("targets").forGetter[OreFeatureConfig](_.targets)
        val size = Codec[Int].fieldOf("size").forGetter[OreFeatureConfig](_.size)

        Codec.build(OreFeatureConfig(targets.get, size.get, 0f))
    }

    given Codec[OreFeatureConfig] = Codec.alternatives(List(Codec.derived[OreFeatureConfig], old2Codec, old1Codec))
}
