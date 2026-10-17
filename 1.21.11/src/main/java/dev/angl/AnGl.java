package dev.angl;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;

public class AnGl implements ClientModInitializer {
	public static KeyMapping key;

	public void onInitializeClient() {
		Cfg.ld();
		net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents.CHUNK_LOAD.register(Anc::load);
		net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents.CHUNK_UNLOAD.register(Anc::unload);
		net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents.DISCONNECT.register((h, m) -> Anc.clear());
		KeyMapping k = key = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.anchorglow.open", InputConstants.KEY_I, KeyMapping.Category.register(Identifier.fromNamespaceAndPath("anchorglow", "main"))));
		ClientTickEvents.END_CLIENT_TICK.register(m -> {
			while (k.consumeClick()) if (m.screen == null) {
				if (net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded("wysslib")) m.setScreen(new dev.angl.ui.Panel(null));
				else if (m.player != null) m.player.displayClientMessage(net.minecraft.network.chat.Component.literal("Anchor Glow needs Wysss Library to open its menu"), true);
			}
		});
	}
}
