package dev.tfbr;

import dev.wysl.Wl;
import dev.wysl.ui.Frm;
import net.minecraft.client.gui.screens.Screen;

public class Gui {
	public static Screen open(Screen p) {
		Cfg c = Cfg.c;
		Frm f = Frm.of("Tuff Bright", Cfg::sv)
				.tab("Settings")
				.tgl("Fullbright on", () -> c.on, v -> c.on = v).tip("Turn this off to go back to the normal game lighting.")
				.sld("Brightness", () -> c.pct, v -> c.pct = v, 0, 100, "%").tip("100% lights everything like full daylight, lower is softer, 0% does nothing.")
				.note("Also removes the darkness effect.");
		return Wl.screen(p, f, "Tuff Bright", TfbrClient.key);
	}
}
