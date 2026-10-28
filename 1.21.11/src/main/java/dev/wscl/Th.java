package dev.wscl;

import net.minecraft.client.gui.GuiGraphics;

public class Th {
	public static final int PN = 0x0E0B1E;
	public static final float PA = 0.85f;
	public static final int BD = 0xFFFFFF;
	public static final float BA = 0.14f;
	public static final int TX = 0xFFFFFF;
	public static final float SUB = 0.45f;
	public static final int ON = 0x5CE08A;
	public static final int WARN = 0xFFC850;
	public static final int GAP = 4;
	public static final int PAD = 12;
	public static final int ROW = 26;
	public static final float TS = 9f;

	public static int pn() {
		return Ui.tn(PN);
	}

	public static int ac() {
		return Hud.ac() & 0xFFFFFF;
	}

	public static int lt() {
		return Vis.mix(ac(), 0xFFFFFF, 0.45f);
	}

	static void sh(GuiGraphics g, int x, int y, int w, int h, int c) {
		g.fill(x + 2, y, x + w - 2, y + 1, c);
		g.fill(x + 1, y + 1, x + w - 1, y + 2, c);
		g.fill(x, y + 2, x + w, y + h - 2, c);
		g.fill(x + 1, y + h - 2, x + w - 1, y + h - 1, c);
		g.fill(x + 2, y + h - 1, x + w - 2, y + h, c);
	}

	public static void fill(GuiGraphics g, int x, int y, int w, int h, int c) {
		if (w < 4 || h < 4) return;
		sh(g, x, y, w, h, c);
	}

	public static void box(GuiGraphics g, int x, int y, int w, int h, float hv) {
		box(g, x, y, w, h, Vis.mix(pn(), ac(), 0.12f * hv), PA, hv > 0.02f ? Vis.mix(BD, ac(), hv) : BD, BA + 0.5f * hv);
	}

	public static void box(GuiGraphics g, int x, int y, int w, int h, int fc, float fa, int bc, float ba) {
		if (w < 6 || h < 6) return;
		int b = Ui.al(bc, ba);
		g.fill(x + 2, y, x + w - 2, y + 1, b);
		g.fill(x + 2, y + h - 1, x + w - 2, y + h, b);
		g.fill(x, y + 2, x + 1, y + h - 2, b);
		g.fill(x + w - 1, y + 2, x + w, y + h - 2, b);
		g.fill(x + 1, y + 1, x + 2, y + 2, b);
		g.fill(x + w - 2, y + 1, x + w - 1, y + 2, b);
		g.fill(x + 1, y + h - 2, x + 2, y + h - 1, b);
		g.fill(x + w - 2, y + h - 2, x + w - 1, y + h - 1, b);
		int f = Ui.al(fc, fa);
		g.fill(x + 2, y + 1, x + w - 2, y + 2, f);
		g.fill(x + 1, y + 2, x + w - 1, y + h - 2, f);
		g.fill(x + 2, y + h - 2, x + w - 2, y + h - 1, f);
		g.fill(x + 3, y + 1, x + w - 3, y + 2, Ui.al(0xFFFFFF, 0.09f));
	}

	public static void ct(GuiGraphics g, String s, float cx, float cy, float z, int c, float a) {
		Ui.ts(g, s, Math.round(cx - (Ui.w(s, z) - 1) / 2f), Math.round(Tf.top(cy, z)), z, c, a);
	}

	public static void lt(GuiGraphics g, String s, float x, float cy, float z, int c, float a) {
		Ui.ts(g, s, Math.round(x), Math.round(Tf.top(cy, z)), z, c, a);
	}

	public static void plus(GuiGraphics g, int cx, int cy, int c) {
		g.fill(cx - 3, cy, cx + 4, cy + 1, c);
		g.fill(cx, cy - 3, cx + 1, cy + 4, c);
	}
}
