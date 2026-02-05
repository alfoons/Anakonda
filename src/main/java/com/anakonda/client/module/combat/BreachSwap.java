package com.anakonda.client.module.combat;

import com.anakonda.client.module.Category;
import com.anakonda.client.module.Module;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.registry.RegistryKeys;

public class BreachSwap extends Module {
    public BreachSwap() {
        super("AutoMace", "Automatically swaps to any Mace when attacking", Category.COMBAT);
    }

    public void onAttack() {
        if (!isEnabled()) return;
        if (mc.player == null) return;

        int slot = findMace();
        if (slot != -1 && slot != mc.player.getInventory().selectedSlot) {
            mc.player.getInventory().selectedSlot = slot;
            mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(slot));
        }
    }

    private int findMace() {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (isMace(stack)) {
                return i;
            }
        }
        return -1;
    }

    private boolean isMace(ItemStack stack) {
        // Check for mace item key
        return stack.getItem().getTranslationKey().contains("mace");
    }
}
