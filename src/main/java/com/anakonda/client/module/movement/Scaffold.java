package com.anakonda.client.module.movement;

import com.anakonda.client.event.Event;
import com.anakonda.client.event.EventManager;
import com.anakonda.client.event.impl.PacketEvent;
import com.anakonda.client.module.Category;
import com.anakonda.client.module.Module;
import com.anakonda.client.module.setting.BooleanSetting;
import com.anakonda.client.module.setting.ModeSetting;
import com.anakonda.client.utils.RotationUtils;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class Scaffold extends Module {
    private final ModeSetting mode = new ModeSetting("Mode", "Grim", "Grim", "Normal");
    private final BooleanSetting swing = new BooleanSetting("Swing", true);
    private final BooleanSetting eagle = new BooleanSetting("Eagle", false); // Auto-sneak at edge

    private BlockPos currentPos;
    private Direction currentFace;
    private float[] currentRotations;
    private BlockHitResult currentHitResult;
    private boolean isSending = false;

    public Scaffold() {
        super("Scaffold", "Automatically places blocks under you", Category.MOVEMENT);
        addSetting(mode);
        addSetting(swing);
        addSetting(eagle);
    }

    @Override
    public void onEnable() {
        EventManager.INSTANCE.register(this::onPacket);
        currentRotations = null;
        currentHitResult = null;
        isSending = false;
    }

    @Override
    public void onDisable() {
        if (mc.player != null && eagle.getValue()) {
            mc.options.sneakKey.setPressed(false);
        }
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null) return;

        // Slot finding
        int slot = -1;
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (!stack.isEmpty() && stack.getItem() instanceof BlockItem) {
                slot = i;
                break;
            }
        }

        if (slot == -1) return;
        mc.player.getInventory().selectedSlot = slot;

        BlockPos posBelow = mc.player.getBlockPos().down();
        if (!mc.world.getBlockState(posBelow).isReplaceable()) {
             // Block exists
             currentPos = null;
             currentFace = null;
             currentRotations = null;
             currentHitResult = null;

             // Handle Eagle (Sneak) logic if enabled
             if (eagle.getValue()) {
                 mc.options.sneakKey.setPressed(false);
             }
             return;
        }

        // Find valid neighbor
        findPlacement(posBelow);

        if (currentPos != null && currentFace != null) {
            // Calculate hit vector on the face
            Vec3d hitVec = new Vec3d(currentPos.getX() + 0.5 + currentFace.getOffsetX() * 0.5,
                                     currentPos.getY() + 0.5 + currentFace.getOffsetY() * 0.5,
                                     currentPos.getZ() + 0.5 + currentFace.getOffsetZ() * 0.5);

            // Calculate rotations
            currentRotations = RotationUtils.getRotations(mc.player.getEyePos(), hitVec);

            // Eagle logic: Sneak if we are over air and extending
            if (eagle.getValue()) {
                mc.options.sneakKey.setPressed(true);
            }

            // Store hit result for onPacket
            currentHitResult = new BlockHitResult(hitVec, currentFace, currentPos, false);
        }
    }

    private void findPlacement(BlockPos pos) {
        currentPos = null;
        currentFace = null;

        // Check 4 horizontal directions then down/up?
        // Simple 1-block extension for now.

        Direction[] directions = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.UP, Direction.DOWN};

        for (Direction dir : directions) {
            BlockPos neighbor = pos.offset(dir);
            BlockState state = mc.world.getBlockState(neighbor);

            if (!state.isReplaceable()) {
                currentPos = neighbor;
                currentFace = dir.getOpposite();
                return;
            }
        }
    }

    private void onPacket(Event event) {
        if (!isEnabled()) return;
        if (isSending) return; // Prevent recursion

        if (event instanceof PacketEvent.Send packetEvent) {
            if (packetEvent.getPacket() instanceof PlayerMoveC2SPacket packet) {
                 if (currentRotations != null && currentHitResult != null && mode.is("Grim")) {
                     packetEvent.cancel();
                     isSending = true;

                     double x = packet.getX(mc.player.getX());
                     double y = packet.getY(mc.player.getY());
                     double z = packet.getZ(mc.player.getZ());
                     boolean onGround = packet.isOnGround();
                     boolean horizontalCollision = mc.player.horizontalCollision;

                     // Inject our rotations
                     PlayerMoveC2SPacket newPacket = new PlayerMoveC2SPacket.Full(
                         x, y, z,
                         currentRotations[0], currentRotations[1],
                         onGround, horizontalCollision
                     );

                     mc.getNetworkHandler().sendPacket(newPacket);

                     // Interact immediately after rotation packet
                     mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, currentHitResult);

                     if (swing.getValue()) {
                        mc.player.swingHand(Hand.MAIN_HAND);
                     } else {
                        mc.getNetworkHandler().sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
                     }

                     isSending = false;
                 }
            }
        }
    }
}
