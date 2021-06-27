package de.martenschaefer.minecraft.worldgenupdater
package feature.definition

import cats.data.Writer
import de.martenschaefer.data.serialization.{ Codec, ElementNode, ValidationError }
import de.martenschaefer.minecraft.worldgenupdater.decorator.{ ConfiguredDecorator, Decorators }
import de.martenschaefer.minecraft.worldgenupdater.decorator.definition.{ HeightmapDecoratorConfig, WaterDepthThresholdDecoratorConfig }
import feature.{ ConfiguredFeature, Feature, FeatureProcessResult, Features }

case object TreeFeature extends Feature(Codec[TreeFeatureConfig]) {
    override def process(config: TreeFeatureConfig): FeatureProcessResult = {
        if (config.heightmap.isInstanceOf[Some[_]]) {
            Features.DECORATED.process(DecoratedFeatureConfig(ConfiguredFeature(Features.TREE, TreeFeatureConfig(
                config.trunkProvider,
                config.trunkPlacer,
                config.foliageProvider,
                config.saplingProvider,
                config.foliagePlacer,
                config.dirtProvider,
                config.minimumSize,
                config.decorators,
                config.ignoreVines,
                config.forceDirt,
                config.maxWaterDepth,
                None
            )), ConfiguredDecorator(Decorators.HEIGHTMAP, HeightmapDecoratorConfig(config.heightmap.get))))
        } else if (config.maxWaterDepth != 0) {
            Features.DECORATED.process(DecoratedFeatureConfig(ConfiguredFeature(Features.TREE, TreeFeatureConfig(
                config.trunkProvider,
                config.trunkPlacer,
                config.foliageProvider,
                config.saplingProvider,
                config.foliagePlacer,
                config.dirtProvider,
                config.minimumSize,
                config.decorators,
                config.ignoreVines,
                config.forceDirt,
                0,
                config.heightmap
            )), ConfiguredDecorator(Decorators.WATER_DEPTH_THRESHOLD, WaterDepthThresholdDecoratorConfig(config.maxWaterDepth))))
        } else Writer(List(), ConfiguredFeature(Features.TREE, config.process))
    }
}
