package dev.cropt;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.chat.Component;

public class CoClient implements ClientModInitializer {
	static LiteralArgumentBuilder<FabricClientCommandSource> sw(String n, boolean v) {
		return ClientCommandManager.literal(n).executes(x -> {
			if (v && Co.remote && !Co.allowed) {
				x.getSource().sendFeedback(Component.literal("this server has switched the crystal optimizer off"));
				return 0;
			}
			Co.on = v;
			x.getSource().sendFeedback(Component.literal("crystal optimizer " + (v ? "on" : "off")));
			return 1;
		});
	}

	public void onInitializeClient() {
		ClientPlayConnectionEvents.JOIN.register((h, s, m) -> Co.remote = !m.isLocalServer());
		ClientPlayConnectionEvents.DISCONNECT.register((h, m) -> Co.reset());
		ClientPlayNetworking.registerGlobalReceiver(CoPayload.T, (p, c) -> c.client().execute(() -> {
			Co.allowed = p.ok();
			if (!p.ok()) c.client().gui.getChat().addMessage(Component.literal("crystal optimizer is switched off on this server"));
		}));
		ClientCommandRegistrationCallback.EVENT.register((d, r) -> d.register(ClientCommandManager.literal("crystalopt").then(sw("on", true)).then(sw("off", false))));
	}
}
