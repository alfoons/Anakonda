package com.anakonda.client.mixin;

import com.anakonda.client.module.ModuleManager;
import com.anakonda.client.module.movement.NoSlowdown;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public class MixinClientPlayerEntity {

    @Inject(method = "isUsingItem", at = @At("HEAD"), cancellable = true)
    private void onIsUsingItem(CallbackInfoReturnable<Boolean> cir) {
        if (ModuleManager.INSTANCE.getModule(NoSlowdown.class) != null &&
            ModuleManager.INSTANCE.getModule(NoSlowdown.class).isEnabled()) {
            cir.setReturnValue(false);
        }
    }
}
