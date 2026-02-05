package com.anakonda.client.mixin;

import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PlayerMoveC2SPacket.class)
public interface PlayerMoveC2SPacketAccessor {
    @Accessor("yaw")
    void setYaw(float yaw);

    @Accessor("pitch")
    void setPitch(float pitch);

    // In 1.21.4 mappings, the field might be named differently (e.g. 'rotate', 'rotating', or similar)
    // Or it might not exist if the packet structure changed (e.g. Full/LookAndPosition subclasses).
    // PlayerMoveC2SPacket usually handles yaw/pitch in 'yaw', 'pitch'.
    // 'look' might be unnecessary or removed if we set yaw/pitch directly.
    // Let's remove the boolean setter or check mapping.
    // Assuming we don't need to force the boolean if we change values.

    // But wait, the crash says 'look' target not found.
    // We will just remove it for now.
}
