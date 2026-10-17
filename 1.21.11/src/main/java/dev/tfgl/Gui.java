package dev.tfgl;

import dev.wysl.Wl;
import dev.wysl.ui.Frm;
import net.minecraft.client.gui.screens.Screen;

public class Gui {
	static final String[] PS = {"Normal", "Aqua", "Kitty", "Cry4me", "Own"};

	public static Screen open(Screen p) {
		Cfg c = Cfg.c;
		Frm f = Frm.of("Tuff Glint", Cfg::sv)
				.tab("Glint")
				.tgl("Tuff Glint on", () -> c.on, v -> c.on = v)
				.cho("Look", PS, () -> c.pre, v -> c.pre = v).tip("Normal uses the glint of the game or your pack. Aqua, Kitty and Cry4me are the glints of the HitsOnly packs. Own uses item.png and armor.png from config/tuffglint.")
				.sld("Strength", () -> c.str, v -> c.str = v, 0, 1000, "%").tip("100% is the normal game, 1000% is 10 times stronger.")
				.sld("Speed", () -> c.spd, v -> c.spd = v, 10, 1000, "%")
				.sld("Size", () -> c.siz, v -> c.siz = v, 25, 400, "%")
				.sld("Angle", () -> c.ang, v -> c.ang = v, -180, 180, " deg")
				.tab("Color")
				.clr("Tint", () -> c.col, v -> c.col = v & 0xffffff).tip("White keeps the colors of the glint image.")
				.tab("Apply")
				.tgl("Items", () -> c.itm, v -> c.itm = v)
				.tgl("Armor", () -> c.arm, v -> c.arm = v)
				.note("Own image: config/tuffglint/item.png")
				.pre((g, fm, x, y, w, h) -> {
					float s = Math.min(32f, w - 24f);
					g.text("Preview", x + 10, y + 9, 0x99FFFFFF);
					g.itm("minecraft:diamond_sword", x + (w - s) / 2f, y + 24, s, true);
					g.itm("minecraft:diamond_chestplate", x + (w - s) / 2f, y + 24 + s + 14, s, true);
					g.itm("minecraft:diamond_pickaxe", x + (w - s) / 2f, y + 24 + (s + 14) * 2, s, true);
				})
				.rst(() -> {
					Cfg d = new Cfg();
					c.on = d.on;
					c.pre = d.pre;
					c.str = d.str;
					c.spd = d.spd;
					c.siz = d.siz;
					c.ang = d.ang;
					c.col = d.col;
					c.itm = d.itm;
					c.arm = d.arm;
				});
		return Wl.screen(p, f, "Tuff Glint", TfGl.key);
	}
}
