package me.jellysquid.mods.lithium.mixin.gen.chunk_region;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.WorldGenRegion;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(WorldGenRegion.class)
public abstract class ChunkRegionMixin implements ISeedReader {
    @Shadow
    @Final
    private ChunkPos field_241160_n_;

    @Shadow
    @Final
    private int field_217380_e; //width

    // Array view of the chunks in the region to avoid an unnecessary de-reference
    private IChunk[] chunksArr;

    // The starting position of this region
    private int minChunkX, minChunkZ;

    /**
     * @author JellySquid
     */
    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(ServerWorld world, List<IChunk> chunks, CallbackInfo ci) {
        this.minChunkX = this.field_241160_n_.x;
        this.minChunkZ = this.field_241160_n_.z;

        this.chunksArr = chunks.toArray(new IChunk[0]);
    }

    /**
     * @reason Avoid pointer de-referencing, make method easier to inline
     * @author JellySquid
     */
    @Overwrite
    public BlockState getBlockState(BlockPos pos) {
        int x = (pos.getX() >> 4) - this.minChunkX;
        int z = (pos.getZ() >> 4) - this.minChunkZ;
        int w = this.field_217380_e;

        if (x >= 0 && z >= 0 && x < w && z < w) {
            return this.chunksArr[x + z * w].getBlockState(pos);
        } else {
            throw new NullPointerException("No chunk exists at " + new ChunkPos(pos));
        }
    }

    /**
     * @reason Use the chunk array for faster access
     * @author SuperCoder7979, 2No2Name
     */
    @Overwrite
    public IChunk getChunk(int chunkX, int chunkZ) {
        int x = chunkX - this.minChunkX;
        int z = chunkZ - this.minChunkZ;
        int w = this.field_217380_e;

        if (x >= 0 && z >= 0 && x < w && z < w) {
            return this.chunksArr[x + z * w];
        } else {
            throw new NullPointerException("No chunk exists at " + new ChunkPos(chunkX, chunkZ));
        }
    }

    /**
     * Use our chunk fetch function
     */
    public IChunk getChunk(BlockPos pos) {
        //skip checking chunk.getStatus().isAtLeast(ChunkStatus.EMPTY) here, because it is always true
        return this.getChunk(pos.getX() >> 4, pos.getZ() >> 4);
    }
}
