package dev.wscl;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

public class Ias {
	public static boolean on() {
		return net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded("ias");
	}

	public static void hide() {
		if (!on()) return;
		try {
			Class<?> k = Class.forName("ru.vidtu.ias.config.IASConfig");
			for (String f : new String[]{"serversButton", "serversText", "titleButton", "titleText"}) {
				try {
					var fl = k.getDeclaredField(f);
					fl.setAccessible(true);
					fl.setBoolean(null, false);
				} catch (Throwable e) {
				}
			}
		} catch (Throwable e) {
		}
	}

	public static boolean open() {
		Minecraft m = Minecraft.getInstance();
		for (String cn : new String[]{"ru.vidtu.ias.screen.AccountScreen"}) {
			try {
				m.setScreen((Screen) Class.forName(cn).getConstructor(Screen.class).newInstance(m.screen));
				return true;
			} catch (Throwable x) {
			}
		}
		return false;
	}
}
