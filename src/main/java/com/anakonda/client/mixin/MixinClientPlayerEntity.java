package com.anakonda.client.mixin;

import com.anakonda.client.module.ModuleManager;
import com.anakonda.client.module.movement.NoSlowdown;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientPlayerEntity.class)
public class MixinClientPlayerEntity {

    @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isUsingItem()Z"))
    private boolean onIsUsingItem(LivingEntity instance) {
        if (ModuleManager.INSTANCE.getModule(NoSlowdown.class) != null &&
            ModuleManager.INSTANCE.getModule(NoSlowdown.class).isEnabled()) {
            return false;
        }
        return instance.isUsingItem();
    }
}
