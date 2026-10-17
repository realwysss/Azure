package dev.noeb;

import net.fabricmc.api.ClientModInitializer;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;


public class NoebClient implements ClientModInitializer {
	public static KeyMapping key;

	public void onInitializeClient() {
		Cfg.ld();
		KeyMapping kk = key = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.noeb.open", InputConstants.KEY_X, KeyMapping.Category.register(Identifier.fromNamespaceAndPath("noeb", "main"))));
		ClientTickEvents.END_CLIENT_TICK.register(m -> {
			while (kk.consumeClick()) if (m.screen == null) {
				if (FabricLoader.getInstance().isModLoaded("wysslib")) m.setScreen(Gui.open(null));
				else if (m.player != null) m.player.displayClientMessage(Component.literal("No Eat Bounce needs Wysss Library to open its menu"), true);
			}
		});
	}
}