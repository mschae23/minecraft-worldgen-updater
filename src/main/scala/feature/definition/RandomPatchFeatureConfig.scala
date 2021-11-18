package de.martenschaefer.minecraft.worldgenupdater
package feature.definition

import de.martenschaefer.data.serialization.{ Codec, ValidationError }
import de.martenschaefer.data.util.DataResult.*
import de.martenschaefer.data.util.Identifier
import feature.definition.RandomPatchFeatureConfig.Old2
import feature.placement.PlacedFeature
import feature.{ ConfiguredFeature, FeatureConfig, Features }
import util.*
import valueprovider.{ BlockPlacer, BlockStateProvider }

case class RandomPatchFeatureConfig(tries: Int, spreadXz: Int, spreadY: Int,
                                    feature: PlacedFeature,
                                    old2: Option[Old2] = None) extends FeatureConfig

object RandomPatchFeatureConfig {
    case class Old1(stateProvider: BlockStateProvider, blockPlacer: BlockPlacer,
                    whitelist: List[BlockState], blacklist: List[BlockState], tries: Int,
                    spreadX: Int, spreadY: Int, spreadZ: Int,
                    canReplace: Boolean, project: Boolean, needsWater: Boolean)

    given Codec[Old1] = Codec.record {
        val stateProvider = Codec[BlockStateProvider].fieldOf("state_provider").forGetter[Old1](_.stateProvider)
        val blockPlacer = Codec[BlockPlacer].fieldOf("block_placer").forGetter[Old1](_.blockPlacer)
        val whitelist = Codec[List[BlockState]].fieldOf("whitelist").forGetter[Old1](_.whitelist)
        val blacklist = Codec[List[BlockState]].fieldOf("blacklist").forGetter[Old1](_.blacklist)
        val tries = Codec[Int].orElse(128).fieldOf("tries").forGetter[Old1](_.tries)

        val spreadX = Codec[Int].orElse(7).fieldOf("xspread").forGetter[Old1](_.spreadX)
        val spreadY = Codec[Int].orElse(3).fieldOf("yspread").forGetter[Old1](_.spreadY)
        val spreadZ = Codec[Int].orElse(7).fieldOf("zspread").forGetter[Old1](_.spreadZ)

        val canReplace = Codec[Boolean].orElse(false).fieldOf("can_replace").forGetter[Old1](_.canReplace)
        val project = Codec[Boolean].orElse(true).fieldOf("project").forGetter[Old1](_.project)
        val needsWater = Codec[Boolean].orElse(false).fieldOf("need_water").forGetter[Old1](_.needsWater)

        Codec.build(Old1(stateProvider.get, blockPlacer.get, whitelist.get, blacklist.get, tries.get, spreadX.get, spreadY.get, spreadZ.get, canReplace.get, project.get, needsWater.get))
    }

    val old1Codec: Codec[RandomPatchFeatureConfig] = Codec[Old1].flatXmap(old1 => {
        if (old1.spreadX != old1.spreadZ)
            Failure(List(ValidationError(path => s"$path: Can't update random patch feature; xspread (${old1.spreadX}) and zspread (${old1.spreadZ}) are different",
                List.empty)))
        else
            Success(RandomPatchFeatureConfig(old1.tries, old1.spreadX, old1.spreadY,
                PlacedFeature(Features.SIMPLE_BLOCK.configure(SimpleBlockFeatureConfig(old1.stateProvider)), List.empty), Some(Old2(
                    old1.whitelist.map(_.name), old1.blacklist, if (old1.needsWater) Some(false) else None))))
    })(_ => Failure(List(ValidationError(path => s"random patch encoding failure at $path", List.empty))))

    case class Old2(allowedOn: List[Identifier], disallowedOn: List[BlockState], onlyInAir: Option[Boolean])

    val old2Codec: Codec[RandomPatchFeatureConfig] = Codec.record {
        val tries = Codec[Int].orElse(128).fieldOf("tries").forGetter[RandomPatchFeatureConfig](_.tries)
        val spreadXz = Codec[Int].orElse(7).fieldOf("xz_spread").forGetter[RandomPatchFeatureConfig](_.spreadXz)
        val spreadY = Codec[Int].orElse(3).fieldOf("y_spread").forGetter[RandomPatchFeatureConfig](_.spreadY)

        val allowedOn = Codec[List[Identifier]].fieldOf("allowed_on").forGetter[RandomPatchFeatureConfig](_.old2.get.allowedOn)
        val disallowedOn = Codec[List[BlockState]].fieldOf("disallowed_on").forGetter[RandomPatchFeatureConfig](_.old2.get.disallowedOn)
        val onlyInAir = Codec[Boolean].fieldOf("only_in_air").forGetter[RandomPatchFeatureConfig](_.old2.get.onlyInAir.getOrElse(false))

        val feature = Codec[PlacedFeature].fieldOf("feature").forGetter[RandomPatchFeatureConfig](_.feature)

        Codec.build(RandomPatchFeatureConfig(tries.get, spreadXz.get, spreadY.get, feature.get, Some(Old2(
            allowedOn.get, disallowedOn.get, Some(onlyInAir.get)))))
    }

    val currentCodec: Codec[RandomPatchFeatureConfig] = Codec.record {
        val tries = Codec[Int].orElse(128).fieldOf("tries").forGetter[RandomPatchFeatureConfig](_.tries)
        val spreadXz = Codec[Int].orElse(7).fieldOf("xz_spread").forGetter[RandomPatchFeatureConfig](_.spreadXz)
        val spreadY = Codec[Int].orElse(3).fieldOf("y_spread").forGetter[RandomPatchFeatureConfig](_.spreadY)

        val feature = Codec[PlacedFeature].fieldOf("feature").forGetter[RandomPatchFeatureConfig](_.feature)

        Codec.build(RandomPatchFeatureConfig(tries.get, spreadXz.get, spreadY.get, feature.get))
    }

    given Codec[RandomPatchFeatureConfig] = Codec.alternatives(List(old2Codec, currentCodec, old1Codec))
}
