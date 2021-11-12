package de.martenschaefer.minecraft.worldgenupdater
package feature.definition

import de.martenschaefer.data.serialization.{ Codec, ElementError, ElementNode, ValidationError }
import decorator.definition.{ BlockFilterDecoratorConfig, BlockSurvivesFilterDecoratorConfig, HeightmapDecoratorConfig, WaterDepthThresholdDecoratorConfig }
import decorator.{ ConfiguredDecorator, Decorators }
import feature.{ ConfiguredFeature, Feature, FeatureProcessResult, Features }
import valueprovider.{ SimpleBlockStateProvider, WouldSurviveBlockPredicate }
import cats.data.Writer
import de.martenschaefer.minecraft.worldgenupdater.feature.placement.PlacedFeature
import de.martenschaefer.minecraft.worldgenupdater.util.BlockPos

case object TreeFeature extends Feature(Codec[TreeFeatureConfig]) {
    override def process(unprocessedConfig: TreeFeatureConfig, context: FeatureUpdateContext): FeatureProcessResult = {
        val config = unprocessedConfig.process

        if (config.heightmap.isDefined) {
            Features.DECORATED.process(DecoratedFeatureConfig(ConfiguredFeature(Features.TREE, TreeFeatureConfig(
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
            )), ConfiguredDecorator(Decorators.HEIGHTMAP, HeightmapDecoratorConfig(config.heightmap.get))), context)
        } else if (config.maxWaterDepth != 0) {
            Features.DECORATED.process(DecoratedFeatureConfig(ConfiguredFeature(Features.TREE, TreeFeatureConfig(
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
            )), ConfiguredDecorator(Decorators.WATER_DEPTH_THRESHOLD, WaterDepthThresholdDecoratorConfig(config.maxWaterDepth))), context)
        } else if (config.saplingProvider.isDefined && config.saplingProvider.get.isInstanceOf[SimpleBlockStateProvider]) {
            Features.DECORATED.process(DecoratedFeatureConfig(ConfiguredFeature(Features.TREE, TreeFeatureConfig(
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
            )), ConfiguredDecorator(Decorators.BLOCK_FILTER, BlockFilterDecoratorConfig(WouldSurviveBlockPredicate(
                BlockPos.ORIGIN, config.saplingProvider.get.process.asInstanceOf[SimpleBlockStateProvider].state)))), context)
        } else if (config.saplingProvider.isDefined) {
            Writer(getSaplingProviderErrorList,
                PlacedFeature(ConfiguredFeature(Features.TREE, TreeFeatureConfig(
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
