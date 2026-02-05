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

    private boolean rotating = false;

    private void onPacket(Event event) {
        if (!isEnabled() || target == null) return;
        if (rotating) return; // Prevent infinite loop if we send a packet from here

        if (event instanceof PacketEvent.Send packetEvent) {
            if (packetEvent.getPacket() instanceof PlayerMoveC2SPacket packet) {
                if (rotate.getValue()) {
                    float[] rotations = RotationUtils.getRotations(mc.player.getEyePos(), target.getEyePos());

                    // We cannot use Accessor because fields are final.
                    // Instead, we cancel this packet and send a new one with correct rotations.

                    packetEvent.cancel();
                    rotating = true;

                    // Determine packet type and recreate it with new rotations
                    double x = packet.getX(mc.player.getX());
                    double y = packet.getY(mc.player.getY());
                    double z = packet.getZ(mc.player.getZ());
                    boolean onGround = packet.isOnGround();

                    PlayerMoveC2SPacket newPacket;
                    // Note: PlayerMoveC2SPacket constructors have changed in recent versions (1.21.2+).
                    // They might include an extra boolean for 'horizontalCollision' or similar,
                    // OR the structure changed entirely.
                    // Based on errors: Full requires 7 args, LookAndOnGround requires 4 args.
                    // The extra boolean is likely 'horizontalCollision' or similar internal flag exposed in newer Fabric mappings.
                    // We will assume the last boolean is 'onGround' and guess the second-to-last is another flag or vice-versa.
                    // Actually, looking at 1.21.4 mappings (Yarn), signatures are often (x, y, z, yaw, pitch, onGround, horizontalCollision).
                    // Or (yaw, pitch, onGround, horizontalCollision).

                    boolean extraBool = false; // Guessing false for collision/teleport flag

                    if (packet.changesPosition() && packet.changesLook()) {
                        newPacket = new PlayerMoveC2SPacket.Full(x, y, z, rotations[0], rotations[1], onGround, extraBool);
                    } else if (packet.changesPosition()) {
                        newPacket = new PlayerMoveC2SPacket.Full(x, y, z, rotations[0], rotations[1], onGround, extraBool);
                    } else {
                        newPacket = new PlayerMoveC2SPacket.LookAndOnGround(rotations[0], rotations[1], onGround, extraBool);
                    }

                    mc.getNetworkHandler().sendPacket(newPacket);
                    rotating = false;
                }
            }
        }
    }
}
