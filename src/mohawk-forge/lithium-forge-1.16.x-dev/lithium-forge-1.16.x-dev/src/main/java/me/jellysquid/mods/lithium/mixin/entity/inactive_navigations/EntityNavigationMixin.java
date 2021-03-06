package me.jellysquid.mods.lithium.mixin.entity.inactive_navigations;

import me.jellysquid.mods.lithium.common.entity.EntityNavigationExtended;
import me.jellysquid.mods.lithium.common.world.ServerWorldExtended;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PathNavigator.class)
public abstract class EntityNavigationMixin implements EntityNavigationExtended {

    @Shadow
    @Final
    protected World world;

    @Shadow
    protected Path currentPath;

    private boolean canListenForBlocks = false;

    @Inject(
            method = "updatePath",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/pathfinding/PathNavigator;getPathToPos(Lnet/minecraft/util/math/BlockPos;I)Lnet/minecraft/pathfinding/Path;",
                    shift = At.Shift.AFTER
            )
    )
    private void updateListeningState(CallbackInfo ci) {
        if (this.canListenForBlocks) {
            if (this.currentPath == null) {
                ((ServerWorldExtended) this.world).setNavigationInactive(this);
            } else {
                ((ServerWorldExtended) this.world).setNavigationActive(this);
            }
        }
    }

    @Inject(method = "setPath", at = @At("RETURN"))
    private void updateListeningState2(Path path, double speed, CallbackInfoReturnable<Boolean> cir) {
        if (this.canListenForBlocks) {
            if (this.currentPath == null) {
                ((ServerWorldExtended) this.world).setNavigationInactive(this);
            } else {
                ((ServerWorldExtended) this.world).setNavigationActive(this);
            }
        }
    }

    @Inject(method = "clearPath", at = @At("RETURN"))
    private void stopListening(CallbackInfo ci) {
        if (this.canListenForBlocks) {
            ((ServerWorldExtended) this.world).setNavigationInactive(this);
        }
    }

    @Override
    public void setRegisteredToWorld(boolean isRegistered) {
        // Drowneds are problematic. Their EntityNavigations do not register properly.
        // We make sure to not register them, when vanilla doesn't register them.
        this.canListenForBlocks = isRegistered;
    }

    @Override
    public boolean isRegisteredToWorld() {
        return this.canListenForBlocks;
    }
}
