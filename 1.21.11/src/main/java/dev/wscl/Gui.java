package dev.wscl;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

public class Gui {
	static final String[][] MD = {
		{"Hit Glow", "dev.htgl.ui.Panel"},
		{"Hit Color", "dev.htcl.ui.Panel"},
		{"XP Glow", "dev.xpgl.ui.Panel"},
		{"Crystal Glow", "dev.crgl.ui.Panel"},
		{"Anchor Glow", "dev.angl.ui.Panel"},
		{"Totem Particle Glow", "dev.tpgl.ui.Panel"},
		{"Fire Glow", "hits.fire.ui.Panel"},
		{"Glow Library", "dev.glib.Gl"},
		{"Frame Trim", "dev.frtr.ui.Panel"},
		{"Blur Reborn", "dev.blrb.Gui"},
		{"OG Fog", "dev.ogfg.Gui"},
		{"Tuff Glint", "dev.tfgl.Gui"},
		{"Tuff Bright", "dev.tfbr.Gui"},
		{"Items Opacity", "dev.itop.ui.Gui"},
		{"Old Potions", "dev.oldp.Gui"},
		{"No Bounce", "dev.nobn.Gui"},
		{"No Eat Bounce", "dev.noeb.Gui"},
		{"Kill Effects", "dev.klfx.Gui"}
	};

	static void go(String cls) {
		Minecraft mc = Minecraft.getInstance();
		try {
			Screen p = mc.screen;
			Class<?> k = Class.forName(cls);
			try {
				mc.setScreen((Screen) k.getMethod("open", Screen.class).invoke(null, p));
			} catch (NoSuchMethodException e) {
				mc.setScreen((Screen) k.getConstructor(Screen.class).newInstance(p));
			}
		} catch (Throwable t) {
		}
	}

	public static Screen open(Screen p) {
		long t = System.nanoTime();
		Screen s = new Mnu(p);
		long ms = (System.nanoTime() - t) / 1000000L;
		if (ms > 40) Sv.lg("menu open took " + ms + " ms");
		return s;
	}
}
