package me.jellysquid.mods.phosphor.common.chunk.level;

import net.minecraft.block.BlockState;

public interface LevelPropagatorExtended {
    /**
     * Mirrors {@link net.minecraft.world.lighting.LevelBasedGraph#notifyNeighbors(long, int, boolean)} (long, int, boolean)}, but allows a block state to be passed to
     * prevent subsequent lookup later.
     */
    void propagateLevel(long sourceId, BlockState sourceState, long targetId, int level, boolean decrease);

    /**
     * Copy of {@link net.minecraft.world.lighting.LevelBasedGraph#getEdgeLevel(long, long, int)} but with an additional argument to pass the
     * block state belonging to {@param sourceId}.
     */
    int getPropagatedLevel(long sourceId, BlockState sourceState, long targetId, int level);
}
