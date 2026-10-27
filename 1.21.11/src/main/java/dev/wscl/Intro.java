package dev.wscl;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Util;

public class Intro {
	public static long t0;
	public static boolean done;
	static boolean wb;
	static final float GROW = 1.1f;
	static final float END = 2.7f;
	static final java.util.Random RN = new java.util.Random(7);
	static final float[][] SP = new float[56][4];

	static {
		for (float[] p : SP) {
			p[0] = RN.nextFloat() * 6.2832f;
			p[1] = 0.4f + RN.nextFloat() * 0.6f;
			p[2] = RN.nextFloat() * 0.35f;
			p[3] = RN.nextFloat() < 0.3f ? 1f : 0f;
		}
	}

	public static boolean on() {
		return !done && Cfg.c.lod && Cfg.gi("intro", 1) == 1;
	}

	public static void start(long at) {
		if (t0 == 0 && on()) t0 = at;
	}

	public static void skip() {
		if (t0 != 0 && t() < GROW) t0 = Util.getMillis() - (long) (GROW * 1000f);
	}

	public static float t() {
		if (t0 == 0) return 0f;
		return (Util.getMillis() - t0) / 1000f;
	}

	public static boolean busy() {
		return on() && t0 != 0 && t() < END;
	}

	public static long ui() {
		return t0 == 0 ? 0 : t0 + (long) (GROW * 1000f);
	}

	static float ez(float p) {
		p = Math.max(0f, Math.min(1f, p));
		return 1f - (1f - p) * (1f - p) * (1f - p);
	}

	static float eio(float p) {
		p = Math.max(0f, Math.min(1f, p));
		return p < 0.5f ? 4f * p * p * p : 1f - (float) Math.pow(-2f * p + 2f, 3) / 2f;
	}

	static int c(int rgb, float a) {
		return Vis.col(rgb, a);
	}

	public static void word(GuiGraphics g, float cx, float cy, float z, float spread, float a) {
		if (a <= 0.01f) return;
		int ac = Hud.ac() & 0xFFFFFF;
		String s = "Azure";
		float sp = 1.5f + spread;
		float tot = -sp;
		for (int i = 0; i < s.length(); i++) tot += Tf.wf(String.valueOf(s.charAt(i)), z) + sp;
		float t = (Util.getMillis() % 1000000L) / 1000f;
		Vis.glow(g, Math.round(cx), Math.round(cy), (int) tot + 200, 120, ac, a * (0.42f + 0.08f * (float) Math.sin(t * 1.4f)));
		float x = cx - tot / 2f;
		for (int i = 0; i < s.length(); i++) {
			String ch = String.valueOf(s.charAt(i));
			float pl = 0.5f + 0.5f * (float) Math.sin(t * 3.2f - i * 0.7f);
			Tf.dr(g, ch, x, cy - z * 0.5f, z, c(0xFFFFFF, a * (0.55f + 0.45f * pl)));
			x += Tf.wf(ch, z) + sp;
		}
	}

	static float br() {
		return 0.5f + 0.5f * (float) Math.sin((Util.getMillis() % 1000000L) / 1000f * 2.4f);
	}

	public static void load(GuiGraphics g, float pr) {
		int w = g.guiWidth();
		int h = g.guiHeight();
		g.fill(0, 0, w, h, 0xFF000000);
		pr = Math.max(0f, Math.min(1f, pr));
		int ac = Hud.ac() & 0xFFFFFF;
		Vis.glow(g, w / 2, h / 2, 90 + Math.round(60 * pr), 34 + Math.round(10 * pr), ac, (0.2f + 0.5f * pr) * (0.85f + 0.15f * br()));
	}

	static void ring(GuiGraphics g, int cx, int cy, float r0, float r1, int w, int h, int c) {
		if (r1 <= 0.5f || (c >>> 24) == 0) return;
		r0 = Math.max(0f, r0);
		int ya = Math.max(0, (int) Math.floor(cy - r1));
		int yb = Math.min(h, (int) Math.ceil(cy + r1));
		for (int y = ya; y < yb; y++) {
			float dy = y + 0.5f - cy;
			float xo = (float) Math.sqrt(Math.max(0f, r1 * r1 - dy * dy));
			float xi = Math.abs(dy) < r0 ? (float) Math.sqrt(r0 * r0 - dy * dy) : 0f;
			int ao = Math.round(cx - xo);
			int ai = Math.round(cx - xi);
			int bi = Math.round(cx + xi);
			int bo = Math.round(cx + xo);
			if (xi <= 0f) g.fill(ao, y, bo, y + 1, c);
			else {
				if (ai > ao) g.fill(ao, y, ai, y + 1, c);
				if (bo > bi) g.fill(bi, y, bo, y + 1, c);
			}
		}
	}

	static void out(GuiGraphics g, int cx, int cy, float r, int w, int h) {
		for (int y = 0; y < h; y++) {
			float dy = y + 0.5f - cy;
			if (Math.abs(dy) >= r) {
				g.fill(0, y, w, y + 1, 0xFF000000);
				continue;
			}
			int x = Math.round((float) Math.sqrt(r * r - dy * dy));
			if (cx - x > 0) g.fill(0, y, cx - x, y + 1, 0xFF000000);
			if (cx + x < w) g.fill(cx + x, y, w, y + 1, 0xFF000000);
		}
	}

	public static void draw(GuiGraphics g) {
		if (t0 == 0 || !on()) return;
		float t = t();
		if (t >= END) {
			done = true;
			return;
		}
		if (!wb) {
			wb = true;
			g.blurBeforeThisStratum();
		}
		int w = g.guiWidth();
		int h = g.guiHeight();
		int ac = Hud.ac() & 0xFFFFFF;
		int hi = Vis.mix(ac, 0xFFFFFF, 0.6f);
		int cx = w / 2;
		int cy = h / 2;
		float far = (float) Math.sqrt(w * w + h * h) / 2f + 40f;
		if (t < GROW) {
			g.fill(0, 0, w, h, 0xFF000000);
			float k = ez(t / GROW);
			float kk = k * k;
			Vis.glow(g, cx, cy, Math.round(150 - 70 * k), Math.round(44 + 36 * k), ac, 0.7f + 0.3f * kk);
			Vis.glow(g, cx, cy, Math.round(40 + 40 * kk), Math.round(20 + 40 * kk), hi, 0.3f + 0.7f * kk);
			int core = Math.round(1 + 5 * kk);
			g.fill(cx - core, cy - core, cx + core, cy + core, c(0xFFFFFF, 0.4f + 0.6f * kk));
			for (float[] p : SP) {
				float q = (t - p[2]) / (GROW - p[2]);
				if (q <= 0f) continue;
				q = q * q;
				float d = (1f - q) * far * p[1];
				int x = Math.round(cx + (float) Math.cos(p[0]) * d);
				int y = Math.round(cy + (float) Math.sin(p[0]) * d * 0.7f);
				int sz = p[3] > 0f ? 2 : 1;
				int col = c(p[3] > 0f ? 0xFFFFFF : hi, Math.min(1f, q * 3f));
				g.fill(x - sz, y - sz, x + sz, y + sz, col);
				int tx = Math.round(cx + (float) Math.cos(p[0]) * (d + 10f));
				int ty = Math.round(cy + (float) Math.sin(p[0]) * (d + 10f) * 0.7f);
				Ic.ln(g, x, y, tx, ty, c(ac, 0.5f * Math.min(1f, q * 3f)));
			}
			return;
		}
		float u = (t - GROW) / (END - GROW);
		float r = ez(u) * far;
		out(g, cx, cy, r, w, h);
		float fa = 1f - Math.min(1f, u * 2.2f);
		ring(g, cx, cy, r - 3f, r, w, h, c(0xFFFFFF, 0.95f * fa + 0.05f));
		ring(g, cx, cy, r - 10f, r - 3f, w, h, c(hi, 0.55f * fa));
		ring(g, cx, cy, r - 26f, r - 10f, w, h, c(ac, 0.28f * fa));
		ring(g, cx, cy, r - 52f, r - 26f, w, h, c(ac, 0.1f * fa));
		for (float[] p : SP) {
			float d = r * (0.55f + 0.5f * p[1]) + 10f;
			int x = Math.round(cx + (float) Math.cos(p[0]) * d);
			int y = Math.round(cy + (float) Math.sin(p[0]) * d);
			int sz = p[3] > 0f ? 2 : 1;
			g.fill(x - sz, y - sz, x + sz, y + sz, c(p[3] > 0f ? 0xFFFFFF : hi, fa));
		}
		float fl = 1f - Math.min(1f, u * 3f);
		if (fl > 0f) {
			g.fill(0, 0, w, h, c(0xFFFFFF, 0.55f * fl * fl));
			Vis.glow(g, cx, cy, Math.round(300 + 900 * u), Math.round(160 + 400 * u), hi, fl);
		}
	}
}
