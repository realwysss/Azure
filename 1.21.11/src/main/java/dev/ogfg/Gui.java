package dev.ogfg;

import dev.wysl.Wl;
import dev.wysl.ui.Canvas;
import dev.wysl.ui.Frm;
import net.minecraft.client.gui.screens.Screen;

public class Gui {
	static final String[] MS = {"Pack sky", "Color"};

	static void pv(Canvas g, Cfg c, float x, float y, float w, float h) {
		g.text("Preview", x + 10, y + 9, 0x99FFFFFF);
		int sky = 0xFF6FA8F0;
		int fog = c.mode == 0 ? 0xFFB8D4F8 : 0xFF000000 | c.col;
		g.fill(x + 6, y + 22, w - 12, h * 0.5f - 22, 6, sky, 0xFFB8D4F8);
		g.fill(x + 6, y + h * 0.5f, w - 12, h * 0.5f - 6, 6, 0xFF3C7A2C, 0xFF1F4818);
		for (int i = 0; i < 7; i++) {
			float d = 8 + i * 12;
			float k = c.on ? Math.max(0f, Math.min(1f, (d - c.st / 4f) / Math.max(1f, (c.en - c.st) / 4f))) * c.str / 100f : 0f;
			float tx = x + 12 + i * (w - 36) / 6f;
			float th = 56 - i * 5;
			float ty = y + h * 0.5f - th * 0.6f;
			int col = 0xFF24401C;
			int a = (int) (k * 255);
			g.fill(tx, ty, 10, th, 3, col, col);
			g.fill(tx - 8, ty - 14, 26, 18, 4, 0xFF2E6B24, 0xFF2E6B24);
			if (a > 0) {
				g.fill(tx, ty, 10, th, 3, a << 24 | (fog & 0xFFFFFF), a << 24 | (fog & 0xFFFFFF));
				g.fill(tx - 8, ty - 14, 26, 18, 4, a << 24 | (fog & 0xFFFFFF), a << 24 | (fog & 0xFFFFFF));
			}
		}
	}

	public static Screen open(Screen p) {
		Cfg c = Cfg.c;
		Frm f = Frm.of("OG Fog", Cfg::sv)
				.tab("Fog")
				.tgl("OG Fog on", () -> c.on, v -> c.on = v)
				.sld("Fog start", () -> c.st, v -> c.st = v, 0, 250, " blocks").tip("How many blocks away the fog starts. Everything closer is clear.")
				.sld("Fog end", () -> c.en, v -> c.en = v, 2, 256, " blocks").tip("Where you stop seeing blocks. Try 40 to 50.")
				.sld("Strength", () -> c.str, v -> c.str = v, 0, 100, "%").tip("100% hides everything behind the end, lower keeps far blocks faintly visible.")
				.tgl("Soft fade", () -> c.soft, v -> c.soft = v).tip("Fades the fog in smoothly. Off is the straight line fade of the old game.")
				.tab("Color")
				.cho("Fog color", MS, () -> c.mode, v -> c.mode = v).tip("Pack sky takes the colors of the sky behind it, so it blends with a custom sky.")
				.clr("Color", () -> c.col, v -> c.col = v & 0xffffff)
				.tab("Worlds")
				.tgl("Nether and End", () -> c.dims, v -> c.dims = v).tip("Also use the fog in the Nether and the End.")
				.note("Command: /ogfog (debug shows what it does)")
				.pre((g, fm, x, y, w, h) -> pv(g, c, x, y, w, h))
				.rst(() -> {
					Cfg d = new Cfg();
					c.on = d.on;
					c.mode = d.mode;
					c.col = d.col;
					c.st = d.st;
					c.en = d.en;
					c.str = d.str;
					c.soft = d.soft;
					c.dims = d.dims;
				});
		return Wl.screen(p, f, "OG Fog", OgFg.key);
	}
}
