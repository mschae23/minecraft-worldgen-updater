package de.martenschaefer.minecraft.worldgenupdater

import cats.data.Writer
import de.martenschaefer.data.serialization.ElementError
import feature.ConfiguredFeature

type ProcessResult[T] = Writer[List[ElementError], T]
