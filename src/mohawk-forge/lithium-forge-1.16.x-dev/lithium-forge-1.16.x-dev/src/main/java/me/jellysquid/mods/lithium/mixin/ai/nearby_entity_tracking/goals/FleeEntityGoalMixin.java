package me.jellysquid.mods.lithium.mixin.ai.nearby_entity_tracking.goals;

import me.jellysquid.mods.lithium.common.entity.tracker.nearby.NearbyEntityListenerProvider;
import me.jellysquid.mods.lithium.common.entity.tracker.nearby.NearbyEntityTracker;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;

@Mixin(AvoidEntityGoal.class)
public class FleeEntityGoalMixin<T extends LivingEntity> {
    private NearbyEntityTracker<T> tracker;

    @Inject(
            method = "<init>(Lnet/minecraft/entity/CreatureEntity;Ljava/lang/Class;Ljava/util/function/Predicate;FDDLjava/util/function/Predicate;)V",
            at = @At("RETURN")
    )
    private void init(CreatureEntity entityIn, Class<T> avoidClass, Predicate<LivingEntity> predicate, float distance, double nearSpeedIn, double farSpeedIn, Predicate<LivingEntity> predicate2, CallbackInfo ci) {
        this.tracker = new NearbyEntityTracker<>(avoidClass, entityIn, distance);

        ((NearbyEntityListenerProvider) entityIn).lithium$getListener().addListener(this.tracker);
    }

    @Redirect(
            method = "shouldExecute",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;getClosestEntity(Ljava/lang/Class;Lnet/minecraft/entity/EntityPredicate;Lnet/minecraft/entity/LivingEntity;DDDLnet/minecraft/util/math/AxisAlignedBB;)Lnet/minecraft/entity/LivingEntity;"
            )
    )
    private T redirectGetNearestEntity(World world, Class<? extends T> entityClass, EntityPredicate entityPredicate, LivingEntity entity, double x, double y, double z, AxisAlignedBB box) {
        return this.tracker.getClosestEntity(box, entityPredicate);
    }
}
