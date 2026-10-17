package dev.glib;

import com.mojang.blaze3d.platform.InputConstants;
import dev.glib.ui.Gui;
import dev.wysl.Wl;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class Gl implements ClientModInitializer {
    public static KeyMapping key;

    public void onInitializeClient() {
        KeyMapping k = key = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.glowlib.open", InputConstants.KEY_L, KeyMapping.Category.register(Identifier.fromNamespaceAndPath("glowlib", "main"))));
        ClientTickEvents.END_CLIENT_TICK.register(m -> {
            while (k.consumeClick()) if (m.screen == null) {
                if (FabricLoader.getInstance().isModLoaded("wysslib")) m.setScreen(open(null));
                else if (m.player != null) m.player.displayClientMessage(Component.literal("Glow Library needs Wysss Library to open its menu"), true);
            }
        });
    }

    public static Screen open(Screen p) {
        return Wl.screen(p, new Gui(), "Glow Library", key);
    }
}
