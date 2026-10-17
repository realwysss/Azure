package dev.noeb;

import dev.wysl.Wl;
import dev.wysl.ui.Frm;
import net.minecraft.client.gui.screens.Screen;

public class Gui {
	public static Screen open(Screen p) {
		Cfg c = Cfg.c;
		Frm f = Frm.of("No Eat Bounce", Cfg::sv)
				.tab("Settings")
				.tgl("Mod on", () -> c.on, v -> c.on = v).tip("Turn this off to get the normal vanilla eating animation back.")
				.sld("Bounce height", () -> c.amt, v -> c.amt = v, 0, 100, "%").tip("0% removes the up and down bounce, 100% is the vanilla bounce.")
				.note("The item still moves to your mouth.");
		return Wl.screen(p, f, "No Eat Bounce", NoebClient.key);
	}
}
