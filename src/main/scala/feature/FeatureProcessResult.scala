package de.martenschaefer.minecraft.worldgenupdater
package feature

import de.martenschaefer.data.serialization.ElementError
import feature.ConfiguredFeature
import feature.placement.PlacedFeature
import cats.data.Writer

type FeatureProcessResult = ProcessResult[PlacedFeature]
