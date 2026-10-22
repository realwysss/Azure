package dev.wscl;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Util;

public class Intro {
	public static long t0;
	public static boolean done;
	static boolean wb;
	static final float HOLD = 0.5f;
	static final float FADE = 1.0f;
	static final float GROW = 2.5f;
	static final float OPEN = 3.7f;
	static final float END = 5.2f;

	public static boolean on() {
		return !done && Cfg.c.lod && Cfg.gi("intro", 1) == 1;
	}

	public static void start(long at) {
		if (t0 == 0 && on()) t0 = at;
	}

	public static void skip() {
		if (t0 != 0 && t() < GROW + 0.4f) t0 = Util.getMillis() - (long) ((GROW + 0.4f) * 1000f);
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

	public static void load(GuiGraphics g, float pr) {
		int w = g.guiWidth();
		int h = g.guiHeight();
		g.fill(0, 0, w, h, 0xFF000000);
		pr = Math.max(0f, Math.min(1f, pr));
		int ac = Hud.ac() & 0xFFFFFF;
		Vis.glow(g, w / 2, h / 2, 80, 30, ac, 0.55f * pr * pr);
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
		int hi = Vis.mix(ac, 0xFFFFFF, 0.55f);
		float cy = h / 2f;
		float cx = w / 2f;
		float open = eio((t - GROW - 0.3f) / (OPEN - GROW));
		int gap = Math.round(open * (h / 2f + 6f));
		int top = Math.round(cy) - gap;
		int bot = Math.round(cy) + gap;
		if (top > 0) {
			g.fill(0, 0, w, top, 0xFF000000);
			g.fillGradient(0, Math.max(0, top - 18), w, top, c(ac, 0f), c(ac, 0.22f * (1f - open)));
		}
		if (bot < h) {
			g.fill(0, bot, w, h, 0xFF000000);
			g.fillGradient(0, bot, w, Math.min(h, bot + 18), c(ac, 0.22f * (1f - open)), c(ac, 0f));
		}
		float gp0 = ez((t - 0.9f) / (GROW - 1.0f));
		if (t < GROW) Vis.glow(g, Math.round(cx), Math.round(cy), 80 + Math.round(gp0 * 160f), 30, ac, 0.55f * (1f - gp0 * 0.6f));
		float gp = ez((t - 0.9f) / (GROW - 0.9f - 0.1f));
		if (gp > 0f) {
			float conn = t >= GROW - 0.1f ? 1f : 0f;
			float fade = 1f - ez((t - GROW - 0.5f) / 1.0f);
			int half = Math.round((w / 2f) * gp);
			int y = Math.round(cy);
			if (fade > 0.01f) {
				g.fillGradient(0, y - 10, half, y, c(ac, 0f), c(ac, 0.3f * fade));
				g.fillGradient(0, y, half, y + 10, c(ac, 0.3f * fade), c(ac, 0f));
				g.fill(0, y - 1, half, y + 1, c(hi, fade));
				g.fillGradient(w - half, y - 10, w, y, c(ac, 0f), c(ac, 0.3f * fade));
				g.fillGradient(w - half, y, w, y + 10, c(ac, 0.3f * fade), c(ac, 0f));
				g.fill(w - half, y - 1, w, y + 1, c(hi, fade));
				if (conn == 0f) {
					Vis.glow(g, half, y, 140, 60, ac, 0.9f);
					Vis.glow(g, w - half, y, 140, 60, ac, 0.9f);
					g.fill(Math.max(0, half - 6), y - 1, half, y + 1, c(0xFFFFFF, 1f));
					g.fill(w - half, y - 1, Math.min(w, w - half + 6), y + 1, c(0xFFFFFF, 1f));
				}
			}
			float fl = (t - (GROW - 0.1f)) / 0.7f;
			if (fl > 0f && fl < 1f) {
				float a = (1f - fl) * (1f - fl);
				Vis.glow(g, Math.round(cx), y, Math.round(w * (0.3f + fl * 0.9f)), Math.round(120 + 200 * fl), hi, a);
				Vis.glow(g, Math.round(cx), y, Math.round(260 + 700 * fl), 100, 0xFFFFFF, a * 0.5f);
			}
		}
	}
}
