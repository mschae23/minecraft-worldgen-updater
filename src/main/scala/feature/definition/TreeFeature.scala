package de.martenschaefer.minecraft.worldgenupdater
package feature.definition

import de.martenschaefer.data.serialization.{ Codec, ElementError, ElementNode, ValidationError }
import decorator.definition.{ BlockFilterDecoratorConfig, BlockSurvivesFilterDecoratorConfig, HeightmapDecoratorConfig, WaterDepthThresholdDecoratorConfig }
import decorator.{ ConfiguredDecorator, Decorators }
import feature.placement.PlacedFeature
import feature.placement.definition.{ BlockPredicateFilterPlacement, HeightmapPlacement, SurfaceWaterDepthFilterPlacement }
import feature.{ ConfiguredFeature, Feature, FeatureProcessResult, Features }
import util.BlockPos
import valueprovider.{ SimpleBlockStateProvider, WouldSurviveBlockPredicate }
import cats.data.Writer

case object TreeFeature extends Feature(Codec[TreeFeatureConfig]) {
    override def process(unprocessedConfig: TreeFeatureConfig, context: FeatureUpdateContext): FeatureProcessResult = {
        val config = unprocessedConfig.process

        if (config.heightmap.isDefined) {
            HeightmapPlacement(config.heightmap.get).process(PlacedFeature(this.configure(TreeFeatureConfig(
                config.trunkProvider,
                config.trunkPlacer,
                config.foliageProvider,
                config.foliagePlacer,
                config.dirtProvider,
                config.minimumSize,
                config.decorators,
                config.ignoreVines,
                config.forceDirt,
                config.maxWaterDepth,
                None,
                config.saplingProvider
            )), List.empty))(using context)
        } else if (config.maxWaterDepth != 0) {
            SurfaceWaterDepthFilterPlacement(config.maxWaterDepth).process(PlacedFeature(this.configure(TreeFeatureConfig(
                config.trunkProvider,
                config.trunkPlacer,
                config.foliageProvider,
                config.foliagePlacer,
                config.dirtProvider,
                config.minimumSize,
                config.decorators,
                config.ignoreVines,
                config.forceDirt,
                0,
                config.heightmap,
                config.saplingProvider
            )), List.empty))(using context)
        } else if (config.saplingProvider.isDefined && config.saplingProvider.get.isInstanceOf[SimpleBlockStateProvider]) {
            BlockPredicateFilterPlacement(WouldSurviveBlockPredicate(BlockPos.ORIGIN,
                config.saplingProvider.get.process.asInstanceOf[SimpleBlockStateProvider].state)).process(PlacedFeature(this.configure(TreeFeatureConfig(
                config.trunkProvider,
                config.trunkPlacer,
                config.foliageProvider,
                config.foliagePlacer,
                config.dirtProvider,
                config.minimumSize,
                config.decorators,
                config.ignoreVines,
                config.forceDirt,
                config.maxWaterDepth,
                config.heightmap,
                None
            )), List.empty))(using context)
        } else if (config.saplingProvider.isDefined) {
            Writer(getSaplingProviderErrorList,
                PlacedFeature(this.configure(TreeFeatureConfig(
                    config.trunkProvider,
                    config.trunkPlacer,
                    config.foliageProvider,
                    config.foliagePlacer,
                    config.dirtProvider,
                    config.minimumSize,
                    config.decorators,
                    config.ignoreVines,
                    config.forceDirt,
                    config.maxWaterDepth,
                    config.heightmap,
                    None
                ).process), List.empty))
        } else
            Writer.value(PlacedFeature(Features.TREE.configure(if (context.onlyUpdate) config else config.process), List.empty))
    }

    def getSaplingProviderErrorList: List[ElementError] =
        List(ValidationError(path => s"$path: Could not add block_survives_filter decorator to tree",
            List(ElementNode.Name("config"), ElementNode.Name("sapling_provider"))))
}
