package dev.wscl;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class Sid {
	public static void init() {
		ScreenEvents.AFTER_INIT.register((m, sc, w, h) -> {
			if (!sc.getClass().getName().equals("ru.vidtu.ias.screen.AccountScreen")) return;
			Screens.getButtons(sc).add(Button.builder(Component.literal("Session ID"), b -> m.setScreen(new AzAcc(sc))).bounds(w / 2 + 80, 11, 80, 20).build());
		});
	}
}
