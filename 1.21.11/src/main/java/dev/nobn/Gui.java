package dev.nobn;

import dev.wysl.Wl;
import dev.wysl.ui.Frm;
import net.minecraft.client.gui.screens.Screen;

public class Gui {
	public static Screen open(Screen p) {
		Cfg c = Cfg.c;
		Frm f = Frm.of("No Bounce", Cfg::sv)
				.tab("Settings")
				.tgl("No bounce", () -> c.nb, v -> c.nb = v).tip("Removes the jump your hotbar item does when the server gives items back, like crystals on high ping.")
				.tgl("Fade number", () -> c.fd, v -> c.fd = v).tip("The stack number fades into the new number instead of snapping to it.")
				.sld("Fade time", () -> c.ms, v -> c.ms = v, 20, 1000, " ms");
		return Wl.screen(p, f, "No Bounce", NoBn.key);
	}
}
