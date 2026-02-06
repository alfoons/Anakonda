package com.anakonda.client.module.combat;

import com.anakonda.client.event.Event;
import com.anakonda.client.event.EventManager;
import com.anakonda.client.event.impl.PacketEvent;
import com.anakonda.client.module.Category;
import com.anakonda.client.module.Module;
import com.anakonda.client.module.setting.BooleanSetting;
import com.anakonda.client.module.setting.ModeSetting;
import com.anakonda.client.module.setting.NumberSetting;
import com.anakonda.client.utils.RotationUtils;
import com.anakonda.client.mixin.PlayerMoveC2SPacketAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.MathHelper;

import java.util.Comparator;
import java.util.Random;

public class KillAura extends Module {
    private final NumberSetting range = new NumberSetting("Range", 4.0, 3.0, 6.0);
    private final ModeSetting mode = new ModeSetting("Mode", "Legit", "Legit", "Rage");
    private final BooleanSetting cooldown = new BooleanSetting("1.9 Delay", true);
    private final BooleanSetting wallCheck = new BooleanSetting("Wall Check", true);

    private Entity target;
    private final Random random = new Random();

    public KillAura() {
        super("KillAura", "Attacks entities around you", Category.COMBAT);
        addSetting(range);
        addSetting(mode);
        addSetting(cooldown);
        addSetting(wallCheck);
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
                    // Wall Check logic
                    if (wallCheck.getValue() && !mc.player.canSee(entity)) {
                        continue;
                    }
                    // Intelligent Targeting (Heuristic: Low HP priority)
                    // If current target is defined and has lower HP than candidate, keep current
                    if (target instanceof LivingEntity currentTarget) {
                        if (living.getHealth() >= currentTarget.getHealth()) continue;
                    }

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

    private boolean rotating = false;

    private void onPacket(Event event) {
        if (!isEnabled() || target == null || !mode.is("Rage")) return;
        if (rotating) return;

        if (event instanceof PacketEvent.Send packetEvent) {
            if (packetEvent.getPacket() instanceof PlayerMoveC2SPacket packet) {
                // Rage Mode implies silent rotation logic
                float[] rotations = RotationUtils.getRotations(mc.player.getEyePos(), target.getEyePos());

                packetEvent.cancel();
                rotating = true;

                double x = packet.getX(mc.player.getX());
                double y = packet.getY(mc.player.getY());
                double z = packet.getZ(mc.player.getZ());
                boolean onGround = packet.isOnGround();

                boolean horizontalCollision = mc.player.horizontalCollision;

                PlayerMoveC2SPacket newPacket;

                if (packet.changesPosition() && packet.changesLook()) {
                    newPacket = new PlayerMoveC2SPacket.Full(x, y, z, rotations[0], rotations[1], onGround, horizontalCollision);
                } else if (packet.changesPosition()) {
                    newPacket = new PlayerMoveC2SPacket.Full(x, y, z, rotations[0], rotations[1], onGround, horizontalCollision);
                } else {
                    newPacket = new PlayerMoveC2SPacket.LookAndOnGround(rotations[0], rotations[1], onGround, horizontalCollision);
                }

                mc.getNetworkHandler().sendPacket(newPacket);
                rotating = false;
            }
        }
    }
}
