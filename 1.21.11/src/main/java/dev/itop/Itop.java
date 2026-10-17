package dev.itop;

import com.mojang.blaze3d.platform.InputConstants;
import dev.itop.ui.Gui;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelModifier;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class Itop implements ClientModInitializer {
	public static KeyMapping key;

	public void onInitializeClient() {
		Cfg.ld();
		ModelLoadingPlugin.register(c -> c.modifyBlockModelAfterBake().register(ModelModifier.WRAP_PHASE, (m, x) -> new Wr(m)));
		KeyMapping k = key = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.itemsopacity.open", InputConstants.KEY_M, KeyMapping.Category.register(Identifier.fromNamespaceAndPath("itemsopacity", "main"))));
		ClientTickEvents.END_CLIENT_TICK.register(m -> {
			while (k.consumeClick()) if (m.screen == null) {
				if (FabricLoader.getInstance().isModLoaded("wysslib")) m.setScreen(Gui.open(null));
				else if (m.player != null) m.player.displayClientMessage(Component.literal("Items Opacity needs Wysss Library to open its menu"), true);
			}
		});
	}
}
