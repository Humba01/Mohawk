package me.jellysquid.mods.lithium.mixin.gen.cached_generator_settings;

import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.NoiseChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(NoiseChunkGenerator.class)
public class NoiseChunkGeneratorMixin {
    private int cachedSeaLevel;

    /**
     * Use cached sea level instead of retrieving from the registry every time.
     * This method is called for every block in the chunk so this will save a lot of registry lookups.
     *
     * @author SuperCoder79
     */
    @Overwrite
    public int getSeaLevel() {
        return this.cachedSeaLevel;
    }

    /**
     * "Initialize the cache early in the ctor to avoid potential future problems with uninitialized usages"
     *
     * Well, that was the intent from lithium-fabric, but it seems like lithium-forge isn't allowed to inject into
     * arbitrary points in constructors :(
     */
    @Inject(
            method = "<init>(Lnet/minecraft/world/biome/provider/BiomeProvider;Lnet/minecraft/world/biome/provider/BiomeProvider;JLjava/util/function/Supplier;)V",
            at = @At("RETURN")
    )
    private void hookConstructor(BiomeProvider populationSource, BiomeProvider biomeSource, long seed, Supplier<DimensionSettings> settings, CallbackInfo ci) {
        this.cachedSeaLevel = settings.get().getSeaLevel();
    }
}
