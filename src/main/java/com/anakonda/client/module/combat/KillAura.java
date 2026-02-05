package com.anakonda.client.module.combat;

import com.anakonda.client.event.Event;
import com.anakonda.client.event.EventManager;
import com.anakonda.client.event.impl.PacketEvent;
import com.anakonda.client.module.Category;
import com.anakonda.client.module.Module;
import com.anakonda.client.module.setting.BooleanSetting;
import com.anakonda.client.module.setting.NumberSetting;
import com.anakonda.client.utils.RotationUtils;
import com.anakonda.client.mixin.PlayerMoveC2SPacketAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;

import java.util.Comparator;

public class KillAura extends Module {
    private final NumberSetting range = new NumberSetting("Range", 4.0, 3.0, 6.0);
    private final BooleanSetting rotate = new BooleanSetting("Rotate", true);
    private final BooleanSetting cooldown = new BooleanSetting("1.9 Delay", true);

    private Entity target;

    public KillAura() {
        super("KillAura", "Attacks entities around you", Category.COMBAT);
        addSetting(range);
        addSetting(rotate);
        addSetting(cooldown);
    }

    @Override
    public void onEnable() {
        EventManager.INSTANCE.register(this::onPacket);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null) return;

        // Find target
        target = null;
        double minDistance = range.getValue();

        for (Entity entity : mc.world.getEntities()) {
            if (entity instanceof LivingEntity living && entity != mc.player) {
                if (entity.distanceTo(mc.player) <= minDistance && !entity.isRemoved() && living.getHealth() > 0) {
                    target = entity;
                    minDistance = entity.distanceTo(mc.player);
                }
            }
        }

        if (target != null) {
            // Attack logic
            boolean canAttack = !cooldown.getValue() || mc.player.getAttackCooldownProgress(0.5f) >= 1.0f;

            if (canAttack) {
                mc.interactionManager.attackEntity(mc.player, target);
                mc.player.swingHand(Hand.MAIN_HAND);
            }
        }
    }

    private void onPacket(Event event) {
        if (!isEnabled() || target == null) return;

        if (event instanceof PacketEvent.Send packetEvent) {
            if (packetEvent.getPacket() instanceof PlayerMoveC2SPacket packet) {
                if (rotate.getValue()) {
                    float[] rotations = RotationUtils.getRotations(mc.player.getEyePos(), target.getEyePos());

                    // Silent Rotation via Accessor
                    if (packet instanceof PlayerMoveC2SPacketAccessor accessor) {
                        accessor.setYaw(rotations[0]);
                        accessor.setPitch(rotations[1]);
                        // accessor.setLook(true); // Removed as field not found
                    }
                }
            }
        }
    }
}
