package com.anakonda.client.module.movement;

import com.anakonda.client.event.Event;
import com.anakonda.client.event.EventManager;
import com.anakonda.client.event.impl.PacketEvent;
import com.anakonda.client.module.Category;
import com.anakonda.client.module.Module;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class NoSlowdown extends Module {
    public NoSlowdown() {
        super("NoSlowdown", "Prevents slowdown when using items", Category.MOVEMENT);
    }

    @Override
    public void onEnable() {
        EventManager.INSTANCE.register(this::onPacket);
    }

    @Override
    public void onDisable() {
        // In a real system we would unregister, but for this simple event manager we check isEnabled in the listener
    }

    private void onPacket(Event event) {
        if (!isEnabled()) return;

        if (event instanceof PacketEvent.Send packetEvent) {
             if (mc.player != null && mc.player.isUsingItem() && !mc.player.isRiding()) {
                 // Grim AC Bypass logic often involves sending slot updates or specific packet ordering
                 // This is a basic Grim implementation concept:
                 // When moving while using item, send a slot update to current slot to confuse checks
                 if (packetEvent.getPacket() instanceof PlayerMoveC2SPacket) {
                     mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(mc.player.getInventory().selectedSlot));
                 }
             }
        }
    }
}
