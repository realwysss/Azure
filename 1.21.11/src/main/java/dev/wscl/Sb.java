package dev.wscl;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Util;

public class Sb {
	float a;
	float c;
	long t;

	public int dr(GuiGraphics g, int x, int y, int w0, int w1, int h, boolean foc, String q, String hint, int ac) {
		long n = Util.getMillis();
		float dt = t == 0 ? 0f : Math.min(0.05f, (n - t) / 1000f);
		t = n;
		float tg = foc || !q.isEmpty() ? 1f : 0f;
		a += Math.signum(tg - a) * Math.min(Math.abs(tg - a), dt / 0.32f);
		c += ((foc ? 1f : 0f) - c) * Math.min(1f, dt * 12f);
		float e = a * a * a * (a * (a * 6f - 15f) + 10f);
		int w = w0 + Math.round((w1 - w0) * e);
		int r = h / 2;
		if (c > 0.02f) Ui.glow(g, x, y, w, h, r, ac, c * 0.4f);
		Ui.rr(g, x, y, w, h, r, Ui.al(Vis.mix(0x070F14, ac, 0.1f * c), 0.92f));
		Ui.gloss(g, x, y, w, h, r, 0xFFFFFF, 0.06f);
		Ui.ro(g, x, y, w, h, r, Ui.al(Vis.mix(0xFFFFFF, ac, c), 0.14f + 0.8f * c));
		int isz = Math.round(h * 0.5f + 1 + c);
		Ic.dr(g, "search", x + r + 1, y + h / 2, isz, Ui.al(Vis.mix(0xFFFFFF, ac, c), 0.5f + 0.4f * c));
		int tx = x + h - 2;
		int tw = w - h - (q.isEmpty() ? 6 : h - 4);
		int ty = y + h / 2 - 4;
		if (q.isEmpty()) {
			Ui.tx(g, hint, tx, ty, 0xFFFFFF, 0.38f * (1f - Math.min(1f, c * 0.55f)));
		} else {
			String s = q;
			while (s.length() > 1 && Ui.w(s) > tw) s = s.substring(1);
			Ui.tx(g, s, tx, ty, 0xFFFFFF, 1f);
			if (foc && (n / 500) % 2 == 0) g.fill(tx + Ui.w(s) + 1, ty - 1, tx + Ui.w(s) + 2, ty + 9, Ui.al(ac, 1f));
			Ic.dr(g, "x", x + w - r - 1, y + h / 2, Math.round(h * 0.38f), Ui.al(0xFFFFFF, 0.55f));
		}
		if (q.isEmpty() && foc && (n / 500) % 2 == 0) g.fill(tx, ty - 1, tx + 1, ty + 9, Ui.al(ac, 1f));
		return w;
	}

	public static boolean cl(int x, int w, int h, double mx, double my, int y) {
		return mx >= x + w - h && mx < x + w && my >= y && my < y + h;
	}
}
