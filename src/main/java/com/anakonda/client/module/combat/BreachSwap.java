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
        super("BreachSwap", "Automatically swaps to a Mace with Breach when attacking", Category.COMBAT);
    }

    public void onAttack() {
        if (!isEnabled()) return;
        if (mc.player == null) return;

        int slot = findBreachMace();
        if (slot != -1 && slot != mc.player.getInventory().selectedSlot) {
            mc.player.getInventory().selectedSlot = slot;
            mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(slot));
        }
    }

    private int findBreachMace() {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (isMace(stack) && hasBreach(stack)) {
                return i;
            }
        }
        return -1;
    }

    private boolean isMace(ItemStack stack) {
        // Simple check by translation key or item instance if possible
        return stack.getItem().getTranslationKey().contains("mace");
    }

    private boolean hasBreach(ItemStack stack) {
        ItemEnchantmentsComponent enchantments = stack.get(DataComponentTypes.ENCHANTMENTS);
        if (enchantments != null) {
            for (var entry : enchantments.getEnchantmentEntries()) {
                String key = entry.getKey().getKey().map(k -> k.getValue().toString()).orElse("");
                if (key.contains("breach")) {
                    return true;
                }
            }
        }
        return false;
    }
}
