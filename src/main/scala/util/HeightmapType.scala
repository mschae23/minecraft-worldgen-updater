package de.martenschaefer.minecraft.worldgenupdater
package util

import de.martenschaefer.data.serialization.{ Codec, ValidationError }
import de.martenschaefer.data.util.DataResult.*

enum HeightmapType(val name: String, val purpose: HeightmapPurpose) {
    case WorldSurfaceWg extends HeightmapType("WORLD_SURFACE_WG", HeightmapPurpose.Worldgen)
    case WorldSurface extends HeightmapType("WORLD_SURFACE", HeightmapPurpose.Client)
    case OceanFloorWg extends HeightmapType("OCEAN_FLOOR_WG", HeightmapPurpose.Worldgen)
    case OceanFloor extends HeightmapType("OCEAN_FLOOR", HeightmapPurpose.LiveWorld)
    case MotionBlocking extends HeightmapType("MOTION_BLOCKING", HeightmapPurpose.Client)
    case MotionBlockingNoLeaves extends HeightmapType("MOTION_BLOCKING_NO_LEAVES", HeightmapPurpose.LiveWorld)
}

enum HeightmapPurpose {
    case Worldgen, LiveWorld, Client
}

object HeightmapType {
    given Codec[HeightmapType] = Codec[String].flatXmap(name => name match {
        case "WORLD_SURFACE_WG" => Success(WorldSurfaceWg)
        case "WORLD_SURFACE" => Success(WorldSurface)
        case "OCEAN_FLOOR_WG" => Success(OceanFloorWg)
        case "OCEAN_FLOOR" => Success(OceanFloor)
        case "MOTION_BLOCKING" => Success(MotionBlocking)
        case "MOTION_BLOCKING_NO_LEAVES" => Success(MotionBlockingNoLeaves)
        case _ => Failure(List(ValidationError(path => s"$path is not a valid heightmap", List.empty)))
    })(_ match {
        case WorldSurfaceWg => Success(WorldSurfaceWg.name)
        case WorldSurface => Success(WorldSurface.name)
        case OceanFloorWg => Success(OceanFloorWg.name)
        case OceanFloor => Success(OceanFloor.name)
        case MotionBlocking => Success(MotionBlocking.name)
        case MotionBlockingNoLeaves => Success(MotionBlockingNoLeaves.name)
    })
}
