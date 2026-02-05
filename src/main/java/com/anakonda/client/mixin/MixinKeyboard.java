package com.anakonda.client.mixin;

import com.anakonda.client.Anakonda;
import com.anakonda.client.event.EventManager;
import com.anakonda.client.event.impl.KeyEvent;
import com.anakonda.client.module.ModuleManager;
import com.anakonda.client.ui.modern.ModernClickGUI;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class MixinKeyboard {

    @Inject(method = "onKey", at = @At("HEAD"))
    private void onKey(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        if (action == GLFW.GLFW_PRESS) {
            // Handle ClickGUI bind
            if (key == GLFW.GLFW_KEY_RIGHT_SHIFT) {
                MinecraftClient mc = MinecraftClient.getInstance();
                if (mc.currentScreen instanceof ModernClickGUI) {
                    mc.setScreen(null);
                } else if (mc.currentScreen == null) {
                    mc.setScreen(ModernClickGUI.INSTANCE);
                }
            }

            // Handle Module Binds
            for (var module : ModuleManager.INSTANCE.getModules()) {
                if (module.getKey() == key) {
                    module.toggle();
                }
            }
        }
    }
}
