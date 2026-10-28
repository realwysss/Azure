package dev.wscl;

import dev.wysl.Wl;
import dev.wysl.ui.Frm;
import net.minecraft.client.gui.screens.Screen;

public class ArGui {
	public static Screen open(Screen p) {
		Mod md = Mod.get("armorop");
		Frm f = Frm.of("Armor Opacity", Cfg::sv)
				.tab("Armor")
				.tgl("Mod on", () -> md.on.getAsBoolean(), md::put)
				.sld("Your armor", () -> md.i("so", 50), v -> Cfg.si("armorop.so", v), 0, 100, "%").tip("How see through your own armor is in third person and on the HUD player. 0% hides it.")
				.sld("Other players' armor", () -> md.i("oo", 100), v -> Cfg.si("armorop.oo", v), 0, 100, "%").tip("The armor you see on everyone else.")
				.sld("Your armor in menus", () -> md.i("io", 100), v -> Cfg.si("armorop.io", v), 0, 100, "%").tip("Your armor in the inventory screen.")
				.tab("Elytra")
				.sld("Your elytra", () -> md.i("es", 100), v -> Cfg.si("armorop.es", v), 0, 100, "%").tip("Your elytra has its own setting, so it stays visible when your armor is hidden.")
				.sld("Other players' elytra", () -> md.i("eo", 100), v -> Cfg.si("armorop.eo", v), 0, 100, "%").tip("The elytra you see on everyone else.")
				.note("0% hides it, 100% looks normal.")
				.pre((g, fm, x, y, w, h) -> {
					if (!g.player(x + 6, y + 6, w - 12, h - 12)) g.text("Join a world to see yourself here", x + 10, y + 10, 0x99FFFFFF);
				});
		return Wl.screen(p, f, "Armor Opacity", null);
	}
}
