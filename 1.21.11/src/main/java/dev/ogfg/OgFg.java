package dev.ogfg;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.api.ClientModInitializer;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.network.chat.Component;

public class OgFg implements ClientModInitializer {
	public static KeyMapping key;

	static int say(FabricClientCommandSource s, String m) {
		Cfg.fx();
		Cfg.sv();
		s.sendFeedback(Component.literal(m));
		return 1;
	}

	static LiteralArgumentBuilder<FabricClientCommandSource> num(String n, int lo, int hi) {
		return ClientCommandManager.literal(n).then(ClientCommandManager.argument("v", IntegerArgumentType.integer(lo, hi)).executes(x -> {
			int v = IntegerArgumentType.getInteger(x, "v");
			if (n.equals("start")) Cfg.c.st = v;
			else if (n.equals("end")) Cfg.c.en = v;
			else Cfg.c.str = v;
			Cfg.fx();
			return say(x.getSource(), "fog " + n + " " + v + (n.equals("strength") ? "%" : " blocks"));
		}));
	}

	public void onInitializeClient() {
		Cfg.ld();
		KeyMapping k = key = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.ogfog.open", InputConstants.KEY_V, KeyMapping.Category.register(Identifier.fromNamespaceAndPath("ogfog", "main"))));
		ClientTickEvents.END_CLIENT_TICK.register(m -> {
			while (k.consumeClick()) if (m.screen == null) {
				if (FabricLoader.getInstance().isModLoaded("wysslib")) m.setScreen(Gui.open(null));
				else if (m.player != null) m.player.displayClientMessage(Component.literal("OG Fog needs Wysss Library to open its menu"), true);
			}
		});
		ClientCommandRegistrationCallback.EVENT.register((d, r) -> d.register(ClientCommandManager.literal("ogfog")
				.executes(x -> {
					Cfg c = Cfg.c;
					x.getSource().sendFeedback(Component.literal("og fog " + (c.on ? "on" : "off") + ", start " + c.st + ", end " + c.en + " blocks, " + (c.mode == 0 ? "pack sky" : "color " + String.format("%06x", c.col)) + ", strength " + c.str + "%"));
					return 1;
				})
				.then(ClientCommandManager.literal("debug").executes(x -> {
					x.getSource().sendFeedback(Component.literal("ok " + Fg.ok + " caps " + Fg.caps + " skips " + Fg.skips + " dim " + x.getSource().getWorld().dimension().identifier()));
					return 1;
				}))
				.then(ClientCommandManager.literal("on").executes(x -> {
					Cfg.c.on = true;
					return say(x.getSource(), "og fog on");
				}))
				.then(ClientCommandManager.literal("off").executes(x -> {
					Cfg.c.on = false;
					return say(x.getSource(), "og fog off");
				}))
				.then(ClientCommandManager.literal("sky").executes(x -> {
					Cfg.c.mode = 0;
					return say(x.getSource(), "fog uses the pack sky");
				}))
				.then(ClientCommandManager.literal("color").then(ClientCommandManager.argument("hex", StringArgumentType.word()).executes(x -> {
					String h = StringArgumentType.getString(x, "hex").replace("#", "");
					try {
						Cfg.c.col = Integer.parseInt(h, 16) & 0xffffff;
					} catch (Exception e) {
						x.getSource().sendFeedback(Component.literal("use a hex color like 3a1f5c"));
						return 0;
					}
					Cfg.c.mode = 1;
					return say(x.getSource(), "fog color " + String.format("%06x", Cfg.c.col));
				})))
				.then(num("start", 0, 250))
				.then(num("end", 2, 256))
				.then(num("strength", 0, 100))));
	}
}
