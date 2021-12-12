package de.martenschaefer.minecraft.worldgenupdater
package feature.definition

import de.martenschaefer.data.serialization.Codec
import de.martenschaefer.data.util.Identifier
import feature.FeatureConfig
import feature.placement.PlacedFeatureReference
import util.*
import valueprovider.{ BlockStateProvider, IntProvider }

case class VegetationPatchFeatureConfig(replaceable: Identifier,
                                        groundState: BlockStateProvider,
                                        vegetationFeature: PlacedFeatureReference,
                                        surface: VerticalSurfaceType,
                                        depth: IntProvider,
                                        extraBottomBlockChance: Float,
                                        verticalRange: Int,
                                        vegetationChance: Float,
                                        xzRadius: IntProvider,
                                        extraEdgeColumnChance: Float) extends FeatureConfig derives Codec {
    def process(using context: FeatureUpdateContext): ProcessResult[VegetationPatchFeatureConfig] =
        this.vegetationFeature.process.map(PlacedFeatureReference.apply).map(vegetationFeature => VegetationPatchFeatureConfig(
            this.replaceable, if (context.onlyUpdate) this.groundState else this.groundState.process,
            vegetationFeature, this.surface,
            if (context.onlyUpdate) this.depth else this.depth.process,
            this.extraBottomBlockChance, this.verticalRange, this.vegetationChance,
            if (context.onlyUpdate) this.xzRadius else this.xzRadius.process,
            this.extraEdgeColumnChance
        ))
}
