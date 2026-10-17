package dev.oldp;

import dev.wysl.Wl;
import dev.wysl.ui.Frm;
import net.minecraft.client.gui.screens.Screen;

public class Gui {
	public static Screen open(Screen p) {
		Cfg c = Cfg.c;
		Frm f = Frm.of("Old Potions", Cfg::sv)
				.tab("Settings")
				.tgl("Old colors", () -> c.col, v -> c.col = v).tip("Uses the potion and effect colors from 1.18: bottle, swirl particles and tipped arrows.")
				.tgl("Potion glint", () -> c.gl, v -> c.gl = v).tip("Potions with effects shine like in 1.18.")
				.tgl("Water bottle glint", () -> c.wat, v -> c.wat = v).tip("Also makes potions without effects shine. 1.18 did not do this.")
				.tgl("Arrow glint", () -> c.arr, v -> c.arr = v).tip("Tipped arrows with effects shine too. 1.18 did not do this.");
		return Wl.screen(p, f, "Old Potions", OldP.key);
	}
}
