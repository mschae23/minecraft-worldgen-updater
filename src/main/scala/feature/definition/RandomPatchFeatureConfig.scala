package de.martenschaefer.minecraft.worldgenupdater
package feature.definition

import de.martenschaefer.data.serialization.{ Codec, ValidationError }
import de.martenschaefer.data.util.DataResult.*
import de.martenschaefer.data.util.Identifier
import feature.{ ConfiguredFeature, FeatureConfig, Features }
import util.*
import valueprovider.{ BlockPlacer, BlockStateProvider }

case class RandomPatchFeatureConfig(val tries: Int, val spreadXz: Int, val spreadY: Int,
                                    val allowedOn: List[Identifier], val disallowedOn: List[BlockState],
                                    val onlyInAir: Boolean, val feature: ConfiguredFeature[_, _]) extends FeatureConfig

object RandomPatchFeatureConfig {
    case class Old1(val stateProvider: BlockStateProvider, val blockPlacer: BlockPlacer,
                    val whitelist: List[BlockState], val blacklist: List[BlockState], val tries: Int,
                    val spreadX: Int, val spreadY: Int, val spreadZ: Int,
                    val canReplace: Boolean, val project: Boolean, val needsWater: Boolean)

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

    val old1Codec: Codec[RandomPatchFeatureConfig] = Codec[Old1].flatXmap((old1, element) => {
        if (old1.spreadX != old1.spreadZ)
            Failure(List(ValidationError(path => s"$path: Can't update random patch feature; xspread (${old1.spreadX}) and zspread (${old1.spreadZ}) are different",
                List.empty)))
        else
            Success(RandomPatchFeatureConfig(old1.tries, old1.spreadX, old1.spreadY, old1.whitelist.map(_.name), old1.blacklist,
                !old1.needsWater, Features.SIMPLE_BLOCK.configure(SimpleBlockFeatureConfig(old1.stateProvider))))
    })(_ => Failure(List(ValidationError(path => s"random patch encoding failure at $path", List.empty))))

    val currentCodec: Codec[RandomPatchFeatureConfig] = Codec.record {
        val tries = Codec[Int].orElse(128).fieldOf("tries").forGetter[RandomPatchFeatureConfig](_.tries)
        val spreadXz = Codec[Int].orElse(7).fieldOf("xz_spread").forGetter[RandomPatchFeatureConfig](_.spreadXz)
        val spreadY = Codec[Int].orElse(3).fieldOf("y_spread").forGetter[RandomPatchFeatureConfig](_.spreadY)

        val allowedOn = Codec[List[Identifier]].fieldOf("allowed_on").forGetter[RandomPatchFeatureConfig](_.allowedOn)
        val disallowedOn = Codec[List[BlockState]].fieldOf("disallowed_on").forGetter[RandomPatchFeatureConfig](_.disallowedOn)
        val onlyInAir = Codec[Boolean].fieldOf("only_in_air").forGetter[RandomPatchFeatureConfig](_.onlyInAir)

        val feature = Codec[ConfiguredFeature[_, _]].fieldOf("feature").forGetter[RandomPatchFeatureConfig](_.feature)

        Codec.build(RandomPatchFeatureConfig(tries.get, spreadXz.get, spreadY.get, allowedOn.get, disallowedOn.get, onlyInAir.get, feature.get))
    }

    given Codec[RandomPatchFeatureConfig] = currentCodec
        .flatOrElse(old1Codec)
}
