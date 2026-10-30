package dev.wscl;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public class Crs {
	public static float xk;

	static void rc(GuiGraphics g, int x1, int y1, int x2, int y2, int col, boolean o) {
		if (o) g.fill(x1 - 1, y1 - 1, x2 + 1, y2 + 1, 0xCC000000);
		g.fill(x1, y1, x2, y2, col);
	}

	public static void draw(GuiGraphics g) {
		Minecraft m = Minecraft.getInstance();
		if (!m.options.getCameraType().isFirstPerson() || m.options.hideGui) return;
		at(g, g.guiWidth() / 2, g.guiHeight() / 2, false);
	}

	public static void at(GuiGraphics g, int cx, int cy, boolean pv) {
		Cfg c = Cfg.c;
		int col = 0xFF000000 | Rw.cv(c.xc);
		int gp = c.xg + (c.xd && !pv ? (int) xk : 0);
		int th = c.xt;
		int lo = th / 2;
		int hi = th - lo;
		int sz = c.xz;
		if (c.xs == 0) {
			rc(g, cx - gp - sz, cy - lo, cx - gp, cy + hi, col, c.xo);
			rc(g, cx + gp, cy - lo, cx + gp + sz, cy + hi, col, c.xo);
			rc(g, cx - lo, cy - gp - sz, cx + hi, cy - gp, col, c.xo);
			rc(g, cx - lo, cy + gp, cx + hi, cy + gp + sz, col, c.xo);
		} else if (c.xs == 1) {
			int r = Math.max(1, sz / 3 + th - 1);
			rc(g, cx - r, cy - r, cx + r, cy + r, col, c.xo);
		} else if (c.xs == 2) {
			int r = sz + gp;
			int n = Math.max(24, r * 8);
			if (c.xo) for (int i = 0; i < n; i++) {
				double a = i * Math.PI * 2 / n;
				int x = cx + (int) Math.round(Math.cos(a) * r);
				int y = cy + (int) Math.round(Math.sin(a) * r);
				g.fill(x - 1, y - 1, x + th + 1, y + th + 1, 0x55000000);
			}
			for (int i = 0; i < n; i++) {
				double a = i * Math.PI * 2 / n;
				int x = cx + (int) Math.round(Math.cos(a) * r);
				int y = cy + (int) Math.round(Math.sin(a) * r);
				g.fill(x, y, x + th, y + th, col);
			}
		} else {
			rc(g, cx - gp - sz, cy - lo, cx - gp, cy + hi, col, c.xo);
			rc(g, cx + gp, cy - lo, cx + gp + sz, cy + hi, col, c.xo);
			rc(g, cx - lo, cy + gp, cx + hi, cy + gp + sz, col, c.xo);
		}
	}
}
