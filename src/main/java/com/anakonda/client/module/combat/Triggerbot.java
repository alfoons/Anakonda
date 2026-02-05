package com.anakonda.client.module.combat;

import com.anakonda.client.module.Category;
import com.anakonda.client.module.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

public class Triggerbot extends Module {
    public Triggerbot() {
        super("Triggerbot", "Automatically attacks entities you are looking at", Category.COMBAT);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null) return;

        // Check cooldown (1.0 = full charge)
        if (mc.player.getAttackCooldownProgress(0.5f) < 1.0f) return;

        HitResult target = mc.crosshairTarget;
        if (target instanceof EntityHitResult entityHit) {
            Entity entity = entityHit.getEntity();
            if (entity instanceof LivingEntity && entity.isAlive()) {
                // Attack
                mc.interactionManager.attackEntity(mc.player, entity);
                mc.player.swingHand(Hand.MAIN_HAND);
            }
        }
    }
}
