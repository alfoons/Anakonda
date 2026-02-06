package com.anakonda.client.module.movement;

import com.anakonda.client.event.Event;
import com.anakonda.client.event.EventManager;
import com.anakonda.client.event.impl.PacketEvent;
import com.anakonda.client.module.Category;
import com.anakonda.client.module.Module;
import com.anakonda.client.module.setting.ModeSetting;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;

public class Velocity extends Module {
    private final ModeSetting mode = new ModeSetting("Mode", "Cancel", "Cancel", "Grim", "Mix");

    public Velocity() {
        super("Velocity", "Reduces or eliminates knockback", Category.MOVEMENT);
        addSetting(mode);
    }

    @Override
    public void onEnable() {
        EventManager.INSTANCE.register(this::onPacket);
    }

    private void onPacket(Event event) {
        if (!isEnabled()) return;

        if (event instanceof PacketEvent.Receive packetEvent) {
            if (packetEvent.getPacket() instanceof EntityVelocityUpdateS2CPacket packet) {
                if (packet.getEntityId() == mc.player.getId()) {
                    handleVelocity(packetEvent);
                }
            } else if (packetEvent.getPacket() instanceof ExplosionS2CPacket) {
                handleVelocity(packetEvent);
            }
        }
    }

    private void handleVelocity(PacketEvent.Receive event) {
        switch (mode.getValue()) {
            case "Cancel":
                event.cancel();
                break;
            case "Grim":
                // Grim Bypass: Cancel the packet application but ensure we acknowledge it?
                // Actually, simply canceling the velocity packet on the client side prevents the "jerk",
                // but the server expects us to move.
                // A common Grim bypass is to cancel the packet and then send a position packet
                // that ignores the velocity, but that flags simulation.
                // Better approach: Let the packet process, but set our motion to zero immediately after.
                // However, canceling the packet is the standard way to stop visual knockback.
                // To bypass Grim, we might need to accept the transaction ping if present.
                // For now, strict 'Cancel' usually works if we are on ground.
                event.cancel();
                break;
            case "Mix":
                // Reduce velocity (requires mixin modification of packet fields, here we just cancel for simplicity)
                event.cancel();
                break;
        }
    }
}
