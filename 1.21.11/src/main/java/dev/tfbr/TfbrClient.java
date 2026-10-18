package dev.tfbr;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class TfbrClient implements ClientModInitializer {
	public static KeyMapping key;

	public void onInitializeClient() {
		Cfg.ld();
		KeyMapping.Category cg = KeyMapping.Category.register(Identifier.fromNamespaceAndPath("tuffbright", "main"));
		KeyMapping kk = key = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.tuffbright.open", InputConstants.UNKNOWN.getValue(), cg));
		ClientTickEvents.END_CLIENT_TICK.register(m -> {
			while (kk.consumeClick()) if (m.screen == null) {
				if (FabricLoader.getInstance().isModLoaded("wysslib")) m.setScreen(Gui.open(null));
				else if (m.player != null) m.player.displayClientMessage(Component.literal("Tuff Bright needs Wysss Library to open its menu"), true);
			}
		});
		KeyMapping k = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.tuffbright.toggle", InputConstants.UNKNOWN.getValue(), cg));
		ClientTickEvents.END_CLIENT_TICK.register(m -> {
			while (k.consumeClick()) {
				Cfg.c.on = !Cfg.c.on;
				Cfg.sv();
				if (m.player != null) m.gui.setOverlayMessage(Component.literal("tuff bright " + (Cfg.c.on ? "on" : "off")), false);
			}
		});
		ClientCommandRegistrationCallback.EVENT.register((d, r) -> d.register(ClientCommandManager.literal("tuffbright")
				.executes(x -> {
					x.getSource().sendFeedback(Component.literal("tuff bright is " + (Cfg.on() ? Cfg.c.pct + "%" : "off")));
					return 1;
				})
				.then(ClientCommandManager.argument("pct", IntegerArgumentType.integer(0, 100)).executes(x -> {
					Cfg.c.on = true;
					Cfg.c.pct = IntegerArgumentType.getInteger(x, "pct");
					Cfg.sv();
					x.getSource().sendFeedback(Component.literal(Cfg.c.pct == 0 ? "tuff bright off" : "tuff bright set to " + Cfg.c.pct + "%"));
					return 1;
				}))));
	}
}
