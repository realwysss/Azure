package dev.wscl;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

public class Gs {
	static int tg(Minecraft m) {
		return Math.max(1, Math.round(m.getWindow().getHeight() / 540f));
	}

	public static int sc(Minecraft m, Screen s, int b) {
		if (s == null || m.level != null || s instanceof HudEd || !s.getClass().getName().startsWith("dev.wscl.")) return b;
		return Math.min(m.getWindow().calculateScale(0, m.isEnforceUnicode()), tg(m));
	}

	public static void ap(Minecraft m, Screen s) {
		if (m.getWindow() == null || m.options == null) return;
		int b = m.getWindow().calculateScale(m.options.guiScale().get(), m.isEnforceUnicode());
		int n = sc(m, s, b);
		if (n != (int) m.getWindow().getGuiScale()) m.getWindow().setGuiScale(n);
	}

	public static float f() {
		Minecraft m = Minecraft.getInstance();
		if (m.level == null) return 1f;
		return tg(m) / (float) m.getWindow().getGuiScale();
	}
}
