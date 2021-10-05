package de.martenschaefer.minecraft.worldgenupdater
package feature.definition

import de.martenschaefer.data.serialization.Codec
import de.martenschaefer.data.util.Identifier
import feature.FeatureConfig
import feature.definition.tree.{ FeatureSize, FoliagePlacer, TreeDecorator, TrunkPlacer }
import util.*
import valueprovider.{ BlockStateProvider, SimpleBlockStateProvider }

case class TreeFeatureConfig(val trunkProvider: BlockStateProvider,
                             val trunkPlacer: TrunkPlacer,
                             val foliageProvider: BlockStateProvider,
                             val foliagePlacer: FoliagePlacer,
                             val dirtProvider: BlockStateProvider,
                             val minimumSize: FeatureSize,
                             val decorators: List[TreeDecorator],
                             val ignoreVines: Boolean,
                             val forceDirt: Boolean,
                             val maxWaterDepth: Int = 0,
                             val heightmap: Option[HeightmapType] = None,
                             val saplingProvider: Option[BlockStateProvider] = None) extends FeatureConfig {
    def process: TreeFeatureConfig = TreeFeatureConfig(
        this.trunkProvider.process,
        this.trunkPlacer.process,
        this.foliageProvider.process,
        this.foliagePlacer.process,
        this.dirtProvider.process,
        this.minimumSize.process,
        this.decorators.map(_.process),
        this.ignoreVines,
        this.forceDirt,
        this.maxWaterDepth,
        this.heightmap,
        this.saplingProvider.map(_.process)
    )
}

object TreeFeatureConfig {
    val old1Codec = Codec.record {
        val trunkProvider = Codec[BlockStateProvider].fieldOf("trunk_provider").forGetter[TreeFeatureConfig](_.trunkProvider)
        val trunkPlacer = Codec[TrunkPlacer].fieldOf("trunk_placer").forGetter[TreeFeatureConfig](_.trunkPlacer)
        val foliageProvider = Codec[BlockStateProvider].fieldOf("leaves_provider").forGetter[TreeFeatureConfig](_.foliageProvider)
        val foliagePlacer = Codec[FoliagePlacer].fieldOf("foliage_placer").forGetter[TreeFeatureConfig](_.foliagePlacer)
        val minimumSize = Codec[FeatureSize].fieldOf("minimum_size").forGetter[TreeFeatureConfig](_.minimumSize)
        val decorators = Codec[List[TreeDecorator]].fieldOf("decorators").forGetter[TreeFeatureConfig](_.decorators)
        val ignoreVines = Codec[Boolean].orElse(false).fieldOf("ignore_vines").forGetter[TreeFeatureConfig](_.ignoreVines)
        val maxWaterDepth = Codec[Int].orElse(0).fieldOf("max_water_depth").forGetter[TreeFeatureConfig](_.maxWaterDepth)
        val heightmap = Codec[HeightmapType].fieldOf("heightmap").forGetter[TreeFeatureConfig](_.heightmap.get)

        Codec.build(TreeFeatureConfig(trunkProvider.get, trunkPlacer.get, foliageProvider.get, foliagePlacer.get, SimpleBlockStateProvider(
            BlockState(Identifier("minecraft", "dirt"), Map.empty)), minimumSize.get, decorators.get, ignoreVines.get,
            false, maxWaterDepth.get, Some(heightmap.get), Some(SimpleBlockStateProvider(
                BlockState(Identifier("minecraft", "oak_sapling"), Map.empty)))))
    }

    val old2Codec = Codec.record {
        val trunkProvider = Codec[BlockStateProvider].fieldOf("trunk_provider").forGetter[TreeFeatureConfig](_.trunkProvider)
        val trunkPlacer = Codec[TrunkPlacer].fieldOf("trunk_placer").forGetter[TreeFeatureConfig](_.trunkPlacer)
        val foliageProvider = Codec[BlockStateProvider].fieldOf("foliage_provider").forGetter[TreeFeatureConfig](_.foliageProvider)
        val saplingProvider = Codec[BlockStateProvider].fieldOf("sapling_provider").forGetter[TreeFeatureConfig](_.saplingProvider.get)
        val foliagePlacer = Codec[FoliagePlacer].fieldOf("foliage_placer").forGetter[TreeFeatureConfig](_.foliagePlacer)
        val dirtProvider = Codec[BlockStateProvider].fieldOf("dirt_provider").forGetter[TreeFeatureConfig](_.dirtProvider)
        val minimumSize = Codec[FeatureSize].fieldOf("minimum_size").forGetter[TreeFeatureConfig](_.minimumSize)
        val decorators = Codec[List[TreeDecorator]].fieldOf("decorators").forGetter[TreeFeatureConfig](_.decorators)
        val ignoreVines = Codec[Boolean].orElse(false).fieldOf("ignore_vines").forGetter[TreeFeatureConfig](_.ignoreVines)
        val forceDirt = Codec[Boolean].orElse(false).fieldOf("force_dirt").forGetter[TreeFeatureConfig](_.forceDirt)

        Codec.build(TreeFeatureConfig(trunkProvider.get, trunkPlacer.get, foliageProvider.get, foliagePlacer.get,
            dirtProvider.get, minimumSize.get, decorators.get, ignoreVines.get, forceDirt.get,
            saplingProvider = Some(saplingProvider.get)))
    }

    val currentCodec = Codec.record {
        val trunkProvider = Codec[BlockStateProvider].fieldOf("trunk_provider").forGetter[TreeFeatureConfig](_.trunkProvider)
        val trunkPlacer = Codec[TrunkPlacer].fieldOf("trunk_placer").forGetter[TreeFeatureConfig](_.trunkPlacer)
        val foliageProvider = Codec[BlockStateProvider].fieldOf("foliage_provider").forGetter[TreeFeatureConfig](_.foliageProvider)
        val foliagePlacer = Codec[FoliagePlacer].fieldOf("foliage_placer").forGetter[TreeFeatureConfig](_.foliagePlacer)
        val dirtProvider = Codec[BlockStateProvider].fieldOf("dirt_provider").forGetter[TreeFeatureConfig](_.dirtProvider)
        val minimumSize = Codec[FeatureSize].fieldOf("minimum_size").forGetter[TreeFeatureConfig](_.minimumSize)
        val decorators = Codec[List[TreeDecorator]].fieldOf("decorators").forGetter[TreeFeatureConfig](_.decorators)
        val ignoreVines = Codec[Boolean].orElse(false).fieldOf("ignore_vines").forGetter[TreeFeatureConfig](_.ignoreVines)
        val forceDirt = Codec[Boolean].orElse(false).fieldOf("force_dirt").forGetter[TreeFeatureConfig](_.forceDirt)

        Codec.build(TreeFeatureConfig(trunkProvider.get, trunkPlacer.get, foliageProvider.get, foliagePlacer.get,
            dirtProvider.get, minimumSize.get, decorators.get, ignoreVines.get, forceDirt.get))
    }

    given Codec[TreeFeatureConfig] = Codec.alternatives(List(old2Codec, currentCodec, old1Codec))
}
