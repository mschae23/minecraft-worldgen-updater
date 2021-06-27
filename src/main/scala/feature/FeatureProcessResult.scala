package de.martenschaefer.minecraft.worldgenupdater
package feature

import cats.data.Writer
import de.martenschaefer.data.serialization.ElementError
import feature.ConfiguredFeature

type FeatureProcessResult = ProcessResult[ConfiguredFeature[_, _]]
