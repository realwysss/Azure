package dev.wscl;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

public class Ui {
	public static float GA = 1f;

	public static float eb(float t) {
		t = Math.max(0f, Math.min(1f, t));
		float c = 1.4f;
		float u = t - 1f;
		return 1f + (c + 1f) * u * u * u + c * u * u;
	}

	public static int al(int c, float a) {
		return ((int) (Math.max(0f, Math.min(1f, a * GA)) * 255) << 24) | (c & 0xFFFFFF);
	}

	static int tla = -1;
	static float tdh;
	static final java.util.HashMap<Integer, Integer> TC = new java.util.HashMap<>();

	public static int tn(int c) {
		int a = Hud.ac() & 0xFFFFFF;
		if (a != tla) {
			tla = a;
			TC.clear();
			float[] h1 = java.awt.Color.RGBtoHSB(0x8B, 0x5C, 0xF6, null);
			float[] h2 = java.awt.Color.RGBtoHSB(a >> 16 & 255, a >> 8 & 255, a & 255, null);
			tdh = h2[1] < 0.08f ? 0f : h2[0] - h1[0];
		}
		Integer r = TC.get(c);
		if (r != null) return r;
		float[] h = java.awt.Color.RGBtoHSB(c >> 16 & 255, c >> 8 & 255, c & 255, null);
		float hh = h[0] + tdh;
		hh -= (float) Math.floor(hh);
		int o = (c & 0xFF000000) | (java.awt.Color.HSBtoRGB(hh, h[1], h[2]) & 0xFFFFFF);
		TC.put(c, o);
		return o;
	}

	public static float ez(float t) {
		t = Math.max(0f, Math.min(1f, t));
		return 1f - (1f - t) * (1f - t) * (1f - t);
	}

	static final Identifier RR = Identifier.fromNamespaceAndPath("azure", "textures/gui/rr.png");
	static final Identifier GL9 = Identifier.fromNamespaceAndPath("azure", "textures/gui/gl9.png");
	static final int[] RS = {2, 3, 4, 5, 6, 7, 8, 9, 10, 12};
	static final Identifier[] RO = new Identifier[RS.length];

	static {
		for (int i = 0; i < RS.length; i++) RO[i] = Identifier.fromNamespaceAndPath("azure", "textures/gui/ro" + RS[i] + ".png");
	}

	static void nine(GuiGraphics g, Identifier t, int n, int cs, int x, int y, int w, int h, int cd, int c) {
		if (w <= 0 || h <= 0 || (c >>> 24) == 0) return;
		cd = Math.max(1, Math.min(cd, Math.min(w, h) / 2));
		var p = RenderPipelines.GUI_TEXTURED;
		int m = n - 2 * cs;
		int iw = w - 2 * cd;
		int ih = h - 2 * cd;
		g.blit(p, t, x, y, 0f, 0f, cd, cd, cs, cs, n, n, c);
		g.blit(p, t, x + w - cd, y, n - cs, 0f, cd, cd, cs, cs, n, n, c);
		g.blit(p, t, x, y + h - cd, 0f, n - cs, cd, cd, cs, cs, n, n, c);
		g.blit(p, t, x + w - cd, y + h - cd, n - cs, n - cs, cd, cd, cs, cs, n, n, c);
		if (iw > 0) {
			g.blit(p, t, x + cd, y, cs, 0f, iw, cd, m, cs, n, n, c);
			g.blit(p, t, x + cd, y + h - cd, cs, n - cs, iw, cd, m, cs, n, n, c);
		}
		if (ih > 0) {
			g.blit(p, t, x, y + cd, 0f, cs, cd, ih, cs, m, n, n, c);
			g.blit(p, t, x + w - cd, y + cd, n - cs, cs, cd, ih, cs, m, n, n, c);
		}
		if (iw > 0 && ih > 0) g.blit(p, t, x + cd, y + cd, cs, cs, iw, ih, m, m, n, n, c);
	}

	public static boolean px = true;

	static int ck(int w, int h, int r) {
		if (r >= 4 && w >= 6 && h >= 6) return 2;
		if (r >= 2 && w >= 3 && h >= 3) return 1;
		return 0;
	}

	public static void rr(GuiGraphics g, int x, int y, int w, int h, int r, int c) {
		if (!px) {
			nine(g, RR, 96, 32, x, y, w, h, r, c);
			return;
		}
		if (w <= 0 || h <= 0 || (c >>> 24) == 0) return;
		int k = ck(w, h, r);
		if (k == 0) {
			g.fill(x, y, x + w, y + h, c);
			return;
		}
		if (k == 1) {
			g.fill(x + 1, y, x + w - 1, y + 1, c);
			g.fill(x, y + 1, x + w, y + h - 1, c);
			g.fill(x + 1, y + h - 1, x + w - 1, y + h, c);
			return;
		}
		g.fill(x + 2, y, x + w - 2, y + 1, c);
		g.fill(x + 1, y + 1, x + w - 1, y + 2, c);
		g.fill(x, y + 2, x + w, y + h - 2, c);
		g.fill(x + 1, y + h - 2, x + w - 1, y + h - 1, c);
		g.fill(x + 2, y + h - 1, x + w - 2, y + h, c);
	}

	public static void ro(GuiGraphics g, int x, int y, int w, int h, int r, int c) {
		if (px) {
			if (w <= 1 || h <= 1 || (c >>> 24) == 0) return;
			int q = ck(w, h, r);
			g.fill(x + q, y, x + w - q, y + 1, c);
			g.fill(x + q, y + h - 1, x + w - q, y + h, c);
			g.fill(x, y + q, x + 1, y + h - q, c);
			g.fill(x + w - 1, y + q, x + w, y + h - q, c);
			if (q == 2) {
				g.fill(x + 1, y + 1, x + 2, y + 2, c);
				g.fill(x + w - 2, y + 1, x + w - 1, y + 2, c);
				g.fill(x + 1, y + h - 2, x + 2, y + h - 1, c);
				g.fill(x + w - 2, y + h - 2, x + w - 1, y + h - 1, c);
			}
			return;
		}
		int k = 0;
		for (int i = 0; i < RS.length; i++) if (Math.abs(RS[i] - r) < Math.abs(RS[k] - r)) k = i;
		nine(g, RO[k], 96, 32, x, y, w, h, Math.max(r, 2), c);
	}

	public static void glow(GuiGraphics g, int x, int y, int w, int h, int r, int c, float a) {
		int s = 7;
		nine(g, GL9, 128, 48, x - s, y - s, w + 2 * s, h + 2 * s, s * 3 / 2 + r / 2, al(c, a * 0.8f));
	}

	public static void gloss(GuiGraphics g, int x, int y, int w, int h, int r, int c, float a) {
		int n = h / 2;
		for (int i = 0; i < n; i++) {
			float d = r - 0.5f - i;
			int ins = d > 0 ? Math.round(r - (float) Math.sqrt(Math.max(0f, r * r - d * d))) : 0;
			g.fill(x + ins, y + 1 + i, x + w - ins, y + 2 + i, al(c, a * (1f - i / (float) n)));
		}
	}

	public static void card(GuiGraphics g, int x, int y, int w, int h, int r, int ac, float a, boolean hv) {
		rr(g, x, y, w, h, r, al(Ui.tn(0x1A0B24), 0.82f * a));
		gloss(g, x, y, w, h, r, ac, (hv ? 0.12f : 0.06f) * a);
		ro(g, x, y, w, h, r, al(hv ? ac : 0xFFFFFF, (hv ? 0.7f : 0.1f) * a));
	}

	public static void rim(GuiGraphics g, int x, int y, int w, int h, int r, int ac, float a) {
		int c = Vis.mix(ac, 0xFFFFFF, 0.55f);
		g.fill(x + r + 6, y, x + w - r - 6, y + 1, al(c, 0.95f * a));
		g.fill(x + r, y, x + r + 6, y + 1, al(c, 0.45f * a));
		g.fill(x + w - r - 6, y, x + w - r, y + 1, al(c, 0.45f * a));
		g.fill(x + r + 10, y + 1, x + w - r - 10, y + 2, al(ac, 0.28f * a));
	}

	public static String fit(String s, int max) {
		if (w(s) <= max) return s;
		String o = s;
		while (o.length() > 1 && w(o + "...") > max) o = o.substring(0, o.length() - 1);
		return o + "...";
	}

	public static int w(String s) {
		return Tf.w(s, 9f);
	}

	public static int w(String s, float z) {
		return Tf.w(s, z);
	}

	public static void tx(GuiGraphics g, String s, int x, int y, int c, float a) {
		Tf.dr(g, s, x, y, 9f, al(c, a));
	}

	public static void ts(GuiGraphics g, String s, float x, float y, float z, int c, float a) {
		Tf.dr(g, s, x, y, z, al(c, a));
	}

	public static void cx(GuiGraphics g, String s, int x, int y, int c, float a) {
		tx(g, s, x - w(s) / 2, y, c, a);
	}
}
