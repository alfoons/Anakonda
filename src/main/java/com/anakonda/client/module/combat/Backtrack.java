package com.anakonda.client.module.combat;

import com.anakonda.client.event.Event;
import com.anakonda.client.event.EventManager;
import com.anakonda.client.event.impl.PacketEvent;
import com.anakonda.client.module.Category;
import com.anakonda.client.module.Module;
import com.anakonda.client.module.setting.NumberSetting;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.*;

import java.util.concurrent.ConcurrentLinkedQueue;

public class Backtrack extends Module {
    private final NumberSetting delayMS = new NumberSetting("Delay (ms)", 200, 0, 1000);

    private final ConcurrentLinkedQueue<DelayedPacket> packetQueue = new ConcurrentLinkedQueue<>();
    private LivingEntity target = null; // We might want to only backtrack the target?

    public Backtrack() {
        super("Backtrack", "Delays incoming entity packets to hit previous positions", Category.COMBAT);
        addSetting(delayMS);
    }

    @Override
    public void onEnable() {
        EventManager.INSTANCE.register(this::onPacket);
        packetQueue.clear();
    }

    @Override
    public void onDisable() {
        flushAll();
    }

    @Override
    public void onTick() {
        if (mc.player == null) return;

        long maxDelay = delayMS.getValue().longValue();
        long now = System.currentTimeMillis();

        while (!packetQueue.isEmpty()) {
            DelayedPacket dp = packetQueue.peek();
            if (now - dp.time >= maxDelay) {
                packetQueue.poll();
                processPacket(dp.packet);
            } else {
                break;
            }
        }
    }

    private void flushAll() {
        while (!packetQueue.isEmpty()) {
            processPacket(packetQueue.poll().packet);
        }
    }

    @SuppressWarnings("unchecked")
    private void processPacket(Packet<?> packet) {
        if (mc.getNetworkHandler() != null) {
            try {
                ((Packet<ClientPlayPacketListener>) packet).apply(mc.getNetworkHandler());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void onPacket(Event event) {
        if (!isEnabled() || mc.player == null) return;

        if (event instanceof PacketEvent.Receive packetEvent) {
            Packet<?> packet = packetEvent.getPacket();

            if (shouldDelay(packet)) {
                packetEvent.cancel();
                packetQueue.add(new DelayedPacket(packet, System.currentTimeMillis()));
            }
        }
    }

    private boolean shouldDelay(Packet<?> packet) {
        // Check for specific entity movement packets
        if (packet instanceof EntityS2CPacket || // RelMove, Rotate
            packet instanceof EntityPositionS2CPacket || // Teleport
            packet instanceof EntityVelocityUpdateS2CPacket) {

            return checkEntity(packet);
        }

        // Handle Bundles (Disabled for now due to mapping uncertainty)
        /*
        if (packet instanceof BundleS2CPacket) {
            BundleS2CPacket<?> bundle = (BundleS2CPacket<?>) packet;
            for (Packet<?> sub : bundle.getPackets()) {
                if (shouldDelay(sub)) return true;
            }
        }
        */

        return false;
    }

    private boolean checkEntity(Packet<?> packet) {
        // We need to know WHICH entity is being moved to decide if we want to backtrack it.
        // For broad backtrack, we backtrack ALL living entities (or players).
        // Resolving entity ID from packet requires world, which is available.

        int entityId = -1;
        if (packet instanceof EntityS2CPacket p) {
            Entity e = p.getEntity(mc.world);
            if (e != null) entityId = e.getId();
        } else if (packet instanceof EntityVelocityUpdateS2CPacket p) {
            entityId = p.getEntityId();
        }

        if (entityId != -1 && mc.world != null) {
             Entity e = mc.world.getEntityById(entityId);
             if (e instanceof LivingEntity && e != mc.player) {
                 return true;
             }
        }

        return false;
    }

    private static class DelayedPacket {
        Packet<?> packet;
        long time;

        public DelayedPacket(Packet<?> packet, long time) {
            this.packet = packet;
            this.time = time;
        }
    }
}
