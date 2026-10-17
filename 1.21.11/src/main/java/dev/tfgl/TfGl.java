package dev.tfgl;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.network.chat.Component;

public class TfGl implements ClientModInitializer {
	public static KeyMapping key;

	static final String[] PS = {"vanilla", "aqua", "kitty", "cry", "custom"};

	static int say(FabricClientCommandSource s, String m) {
		Cfg.sv();
		s.sendFeedback(Component.literal(m));
		return 1;
	}

	static LiteralArgumentBuilder<FabricClientCommandSource> num(String n, int lo, int hi) {
		return ClientCommandManager.literal(n).then(ClientCommandManager.argument("v", IntegerArgumentType.integer(lo, hi)).executes(x -> {
			int v = IntegerArgumentType.getInteger(x, "v");
			if (n.equals("strength")) Cfg.c.str = v;
			else if (n.equals("speed")) Cfg.c.spd = v;
			else if (n.equals("size")) Cfg.c.siz = v;
			else Cfg.c.ang = v;
			return say(x.getSource(), "glint " + n + " " + v + (n.equals("angle") ? " degrees" : "%"));
		}));
	}

	public void onInitializeClient() {
		Cfg.ld();
		KeyMapping k = key = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.tuffglint.open", InputConstants.KEY_B, KeyMapping.Category.register(Identifier.fromNamespaceAndPath("tuffglint", "main"))));
		ClientTickEvents.END_CLIENT_TICK.register(m -> {
			while (k.consumeClick()) if (m.screen == null) {
				if (FabricLoader.getInstance().isModLoaded("wysslib")) m.setScreen(Gui.open(null));
				else if (m.player != null) m.player.displayClientMessage(Component.literal("Tuff Glint needs Wysss Library to open its menu"), true);
			}
		});
		ClientCommandRegistrationCallback.EVENT.register((d, r) -> d.register(ClientCommandManager.literal("tuffglint")
				.executes(x -> {
					Cfg c = Cfg.c;
					x.getSource().sendFeedback(Component.literal("tuff glint " + (c.on ? "on" : "off") + ", " + PS[c.pre] + ", strength " + c.str + "%, speed " + c.spd + "%, size " + c.siz + "%, angle " + c.ang));
					return 1;
				})
				.then(ClientCommandManager.literal("on").executes(x -> {
					Cfg.c.on = true;
					return say(x.getSource(), "tuff glint on");
				}))
				.then(ClientCommandManager.literal("off").executes(x -> {
					Cfg.c.on = false;
					return say(x.getSource(), "tuff glint off");
				}))
				.then(ClientCommandManager.literal("preset").then(ClientCommandManager.argument("p", StringArgumentType.word()).suggests((x, b) -> {
					for (String s : PS) b.suggest(s);
					return b.buildFuture();
				}).executes(x -> {
					String p = StringArgumentType.getString(x, "p");
					for (int i = 0; i < PS.length; i++) {
						if (PS[i].equals(p)) {
							Cfg.c.pre = i;
							return say(x.getSource(), "glint preset " + p);
						}
					}
					x.getSource().sendFeedback(Component.literal("use vanilla, aqua, kitty, cry or custom"));
					return 0;
				})))
				.then(num("strength", 0, 1000))
				.then(num("speed", 10, 1000))
				.then(num("size", 25, 400))
				.then(num("angle", -180, 180))));
	}
}
