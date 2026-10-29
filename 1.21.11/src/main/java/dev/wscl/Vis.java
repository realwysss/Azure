package dev.wscl;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

public class Vis {
	public static int mix(int a, int b, float t) {
		t = Math.max(0f, Math.min(1f, t));
		int r = (int) (((a >> 16) & 255) * (1 - t) + ((b >> 16) & 255) * t);
		int g = (int) (((a >> 8) & 255) * (1 - t) + ((b >> 8) & 255) * t);
		int bl = (int) ((a & 255) * (1 - t) + (b & 255) * t);
		return (r << 16) | (g << 8) | bl;
	}

	static int col(int rgb, float a) {
		return ((int) (Math.max(0f, Math.min(1f, a)) * 255) << 24) | (rgb & 0xFFFFFF);
	}

	static final Identifier GL = Identifier.fromNamespaceAndPath("azure", "textures/gui/glow.png");
	public static long t0 = System.currentTimeMillis();

	static final Identifier RB = Identifier.fromNamespaceAndPath("azure", "textures/gui/rib.png");
	static final float[][] WV = {
		{0.50f, 0.075f, 0.011f, 0.55f, 0.020f, 0.9f, 0.5f, 0.34f, 0f},
		{0.60f, 0.060f, 0.015f, -0.42f, 0.017f, 0.8f, 0.6f, 0.26f, 0.5f},
		{0.42f, 0.050f, 0.019f, 0.35f, 0.013f, 0.7f, 0.4f, 0.20f, 1f}
	};

	public static final String[] BGN = {"Night", "Waves", "Rain", "Aurora", "Fireflies", "Snow"};
	static float px;
	static float py;
	static long pl;

	public static int sel() {
		return Math.max(0, Math.min(BGN.length - 1, Cfg.gi("bg", 0)));
	}

	static void par() {
		var m = Minecraft.getInstance();
		long n = net.minecraft.util.Util.getMillis();
		if (n == pl) return;
		float dt = Math.min(0.1f, (n - pl) / 1000f);
		pl = n;
		float sw = Math.max(1, m.getWindow().getScreenWidth());
		float sh = Math.max(1, m.getWindow().getScreenHeight());
		float tx = Math.max(-1f, Math.min(1f, (float) (m.mouseHandler.xpos() / sw * 2 - 1)));
		float ty = Math.max(-1f, Math.min(1f, (float) (m.mouseHandler.ypos() / sh * 2 - 1)));
		px += (tx - px) * Math.min(1f, dt * 3f);
		py += (ty - py) * Math.min(1f, dt * 3f);
	}

	public static void bg(GuiGraphics g, float t, float al) {
		bgx(g, t, al);
	}

	public static void bgx(GuiGraphics g, float t, float al) {
		draw(g, sel(), t, al, g.guiWidth(), g.guiHeight(), true);
	}

	static int bac(float t) {
		int cm = Cfg.gi("tbg.cm", 0);
		return cm == 1 ? Cfg.gi("tbg.cc", 0x4AA8FF) & 0xFFFFFF : cm == 2 ? java.awt.Color.HSBtoRGB((t * 0.05f) % 1f, 0.6f, 1f) & 0xFFFFFF : Hud.ac() & 0xFFFFFF;
	}

	static void lay(GuiGraphics g, float d) {
		g.pose().pushMatrix();
		g.pose().translate(-px * d, -py * d);
	}

	static void end(GuiGraphics g) {
		g.pose().popMatrix();
	}

	public static void draw(GuiGraphics g, int st, float t, float al, int w, int h, boolean live) {
		if (live) par();
		g.fillGradient(0, 0, w, h, col(Ui.tn(0x0B0718), al), col(Ui.tn(0x040309), al));
		if (Cfg.c.lite) {
			g.fillGradient(0, 0, w, h, col(mix(0x000000, Hud.ac() & 0xFFFFFF, 0.16f), al * 0.35f), col(0x000000, al * 0.5f));
			return;
		}
		t = t * Cfg.gi("tbg.sp", 100) / 100f;
		float br = Cfg.gi("tbg.br", 100) / 100f;
		int ac = bac(t);
		float a2 = Math.min(1f, al * br);
		float k = h / 270f;
		float ga = Ui.GA;
		if (st == 1) wav(g, t, a2, w, h, ac, k);
		else if (st == 2) rain(g, t, a2, w, h, ac, k, 1f);
		else if (st == 3) aur(g, t, a2, w, h, ac, k);
		else if (st == 4) ffl(g, t, a2, w, h, ac, k);
		else if (st == 5) snow(g, t, a2, w, h, ac, k);
		else night(g, t, a2, w, h, ac, k);
		Ui.GA = ga;
		g.fillGradient(0, 0, w, h, col(0x000000, al * 0.08f), col(0x000000, al * Math.max(0.05f, 0.4f - 0.3f * (br - 1f))));
	}

	static void blob(GuiGraphics g, int cx, int cy, int bw, int bh, int c, float a) {
		glow(g, cx, cy, Math.round(bw * 1.6f), Math.round(bh * 1.4f), c, a);
	}

	static void night(GuiGraphics g, float t, float a, int w, int h, int ac, float k) {
		int cl = mix(Ui.tn(0x2A1066), ac, 0.35f);
		for (int i = 0; i < 6; i++) {
			float[] q = SD[i * 5];
			lay(g, (i % 2 == 0 ? 6f : 12f) * k);
			int cx = Math.round(w * (q[0] * 1.1f - 0.05f) + w * 0.05f * (float) Math.sin(t * 0.05f + i * 1.7f));
			int cy = Math.round(h * (0.25f + q[1] * 0.75f) + h * 0.04f * (float) Math.cos(t * 0.04f + i));
			blob(g, cx, cy, Math.round(w * (0.45f + 0.25f * q[2] / 1.5f)), Math.round(h * (0.35f + 0.2f * q[1])), i % 3 == 0 ? mix(cl, 0xFFFFFF, 0.08f) : cl, a * 0.5f);
			end(g);
		}
		float m = 0.5f + 0.5f * (float) Math.sin(t * 2.0943951f);
		lay(g, 4f * k);
		blob(g, Math.round(w * 0.68f), Math.round(h * 0.45f), Math.round(w * 0.3f), Math.round(h * 0.6f), mix(ac, 0xFFFFFF, 0.45f * m), a * 0.2f);
		end(g);
		lay(g, 3f * k);
		stars(g, t, a, w, h, false);
		end(g);
		float ph = t % 11f;
		if (ph < 0.8f) {
			float p = ph / 0.8f;
			lay(g, 5f * k);
			float hx = w * (0.78f - 0.4f * p);
			float hy = h * (0.06f + 0.26f * p);
			for (int j = 0; j < 30; j++) {
				int x = Math.round(hx + j * 2.2f * k);
				int y = Math.round(hy - j * 0.8f * k);
				g.fill(x, y, x + 1, y + 1, col(0xFFFFFF, a * (1f - j / 30f) * (1f - p * 0.6f)));
			}
			end(g);
		}
	}

	static void wav(GuiGraphics g, float t, float a, int w, int h, int ac, float k) {
		int rh = Math.round(h * 0.62f);
		lay(g, 8f * k);
		int fp = Ffp.real > 0 ? Ffp.real : net.minecraft.client.Minecraft.getInstance().getFps();
		int stp = fp < 40 ? 6 : fp < 100 ? 3 : 2;
		for (float[] v : WV) {
			int c = mix(mix(Ui.tn(0x2A1066), ac, v[6]), 0xFFFFFF, v[8] * 0.12f);
			int cl = col(c, a * v[7]);
			float yb = h * v[0] - rh * 0.3f;
			float am = h * v[1];
			var pp = g.pose();
			for (int x = -20; x < w + 20; x += stp) {
				float y = yb + am * (float) Math.sin(x * v[2] + t * v[3] * 1.4f) + am * 0.45f * (float) Math.sin(x * v[2] * 2.3f - t * v[4] * 30f);
				pp.pushMatrix();
				pp.translate(x, y);
				g.blit(RenderPipelines.GUI_TEXTURED, RB, 0, 0, 0f, 0f, stp, rh, 8, 256, 8, 256, cl);
				pp.popMatrix();
			}
		}
		end(g);
		lay(g, 3f * k);
		stars(g, t, a, w, h);
		end(g);
	}

	static void rain(GuiGraphics g, float t, float a, int w, int h, int ac, float k, float n) {
		int cnt = Math.round(SD.length * n);
		for (int i = 0; i < cnt; i++) {
			float[] q = SD[i];
			boolean nr = q[2] > 1.1f;
			float dp = nr ? 14f : 6f;
			float sp = (120f + q[3] * 9f) * (nr ? 1.3f : 0.8f);
			int len = Math.round((30 + q[2] * 40f) * (nr ? 1f : 0.6f));
			float y = (t * sp + q[4] * (h + 120)) % (h + 120) - 60;
			int x = Math.round(q[0] * (w + 40) - 20 - px * dp * k);
			int yy = Math.round(y - py * dp * k);
			g.fillGradient(x, yy, x + 1, yy + len, col(ac, 0f), col(mix(ac, 0xFFFFFF, 0.4f), a * (nr ? 0.55f : 0.3f)));
		}
		if (n >= 1f) {
			g.fillGradient(0, Math.round(h * 0.7f), w, h, col(ac, 0f), col(ac, a * 0.16f));
			lay(g, 2f * k);
			stars(g, t, a * 0.4f, w, h, false);
			end(g);
		}
	}

	static void aur(GuiGraphics g, float t, float a, int w, int h, int ac, float k) {
		lay(g, 3f * k);
		stars(g, t, a * 0.8f, w, h, false);
		end(g);
		int c1 = mix(ac, 0xFFFFFF, 0.25f);
		int c2 = mix(Ui.tn(0x2A1066), ac, 0.6f);
		for (int b = 0; b < 2; b++) {
			lay(g, (b == 0 ? 7f : 12f) * k);
			int n = 26;
			for (int i = 0; i < n; i++) {
				float u = i / (float) (n - 1);
				float x = -0.1f * w + u * 1.2f * w;
				float y = h * (0.2f + 0.08f * b + 0.07f * (float) Math.sin(u * 5.5f + t * 0.22f + b * 2f) + 0.025f * (float) Math.sin(u * 13f - t * 0.35f));
				float s = 0.5f + 0.5f * (float) Math.sin(u * 7f - t * 0.6f + b * 1.3f);
				int c = mix(b == 0 ? c1 : c2, ac, s * 0.5f);
				glow(g, Math.round(x), Math.round(y), Math.round(w * 0.11f), Math.round(h * (0.42f + 0.12f * s)), c, a * (0.16f + 0.14f * s));
			}
			end(g);
		}
		g.fillGradient(0, Math.round(h * 0.6f), w, h, col(ac, 0f), col(ac, a * 0.1f));
	}

	static void ffl(GuiGraphics g, float t, float a, int w, int h, int ac, float k) {
		lay(g, 3f * k);
		blob(g, w / 2, Math.round(h * 0.95f), w, Math.round(h * 0.5f), mix(Ui.tn(0x2A1066), ac, 0.4f), a * 0.45f);
		end(g);
		int fc = mix(ac, 0xFFFFFF, 0.3f);
		for (int i = 0; i < 40; i++) {
			float[] q = SD[i % SD.length];
			float dp = 4f + q[2] * 7f;
			float x = ((q[0] + i * 0.137f) % 1f) * w + 26f * k * (float) Math.sin(t * 0.25f * q[2] + i) - px * dp * k;
			float y = (0.18f + 0.8f * ((q[1] + i * 0.071f) % 1f)) * h + 16f * k * (float) Math.cos(t * 0.2f * q[2] + i * 1.3f) - py * dp * k;
			float p = 0.5f + 0.5f * (float) Math.sin(t * (0.7f + 0.5f * q[2]) + i * 2.1f);
			int xi = Math.round(x);
			int yi = Math.round(y);
			int gs = Math.round((10f + 8f * q[2]) * k);
			glow(g, xi, yi, gs * 2, gs, ac, a * 0.35f * p);
			int sz = q[2] > 1.1f ? 2 : 1;
			g.fill(xi, yi, xi + sz, yi + sz, col(fc, a * (0.35f + 0.65f * p)));
		}
	}

	static void snow(GuiGraphics g, float t, float a, int w, int h, int ac, float k) {
		lay(g, 3f * k);
		blob(g, w / 2, Math.round(h * 0.1f), w, Math.round(h * 0.5f), mix(Ui.tn(0x2A1066), ac, 0.4f), a * 0.4f);
		end(g);
		int fc = mix(ac, 0xFFFFFF, 0.55f);
		for (int i = 0; i < 120; i++) {
			float[] q = SD[i % SD.length];
			boolean nr = (i % 3) == 0;
			float dp = nr ? 12f : 5f;
			float sp = (nr ? 22f : 12f) * (0.8f + 0.2f * q[2]) * k;
			float bx = ((q[0] * 7.3f + i * 0.618f) % 1f) * (w + 40) - 20;
			float y = (t * sp + q[4] * (h + 20) + i * 37f) % (h + 20) - 10;
			float x = bx + 9f * k * (float) Math.sin(t * 0.5f + i * 0.7f);
			int xi = Math.round(x - px * dp * k);
			int yi = Math.round(y - py * dp * k);
			int sz = nr ? 2 : 1;
			g.fill(xi, yi, xi + sz, yi + sz, col(fc, a * (nr ? 1f : 0.7f)));
		}
		g.fillGradient(0, Math.round(h * 0.75f), w, h, col(ac, 0f), col(ac, a * 0.1f));
	}

	static final float[][] SD = new float[48][];

	static {
		java.util.Random r = new java.util.Random(7);
		for (int i = 0; i < SD.length; i++) SD[i] = new float[]{r.nextFloat(), r.nextFloat(), 0.5f + r.nextFloat(), 12f + r.nextFloat() * 22f, r.nextFloat()};
	}

	static void stars(GuiGraphics g, float t, float al, int w, int h) {
		stars(g, t, al, w, h, true);
	}

	static void stars(GuiGraphics g, float t, float al, int w, int h, boolean fl) {
		for (int i = 0; i < 36; i++) {
			float tw = 0.35f + 0.65f * (0.5f + 0.5f * (float) Math.sin(t * (0.8f + SD[i][2]) + i * 1.7f));
			var sp = g.pose();
			sp.pushMatrix();
			sp.translate(SD[i][0] * w, SD[i][1] * h * 0.9f);
			sp.scale(0.5f, 0.5f);
			g.fill(0, 0, 1, 1, col(0xFFFFFF, al * 0.5f * tw));
			sp.popMatrix();
		}
		if (!fl) return;
		float sn = 0.34f;
		var p = g.pose();
		for (int i = 36; i < SD.length; i++) {
			float len = SD[i][3];
			float span = h + len + 60f;
			float s = (t * (38f + SD[i][2] * 46f) + SD[i][4] * span) % span - len;
			float x = SD[i][0] * (w + h * 0.4f) - s * (float) Math.sin(sn);
			float y = s * (float) Math.cos(sn);
			float a = al * (0.55f + 0.45f * SD[i][2] / 1.5f);
			p.pushMatrix();
			p.translate(x, y);
			p.rotate(sn);
			p.scale(0.5f, 0.5f);
			g.fillGradient(0, -(int) (len * 2), 1, 0, col(0xFFFFFF, 0f), col(0xFFFFFF, a));
			g.fill(0, 0, 1, 1, col(0xFFFFFF, a));
			p.popMatrix();
		}
	}

	public static void glow(GuiGraphics g, int cx, int cy, int w, int h, int c, float a) {
		g.blit(RenderPipelines.GUI_TEXTURED, GL, cx - w / 2, cy - h / 2, 0f, 0f, w, h, 512, 128, 512, 128, ((int) (Math.max(0f, Math.min(1f, a)) * 255) << 24) | (c & 0xFFFFFF));
	}

	public static Component pf(String s, String f) {
		return Component.literal(s).withStyle(st -> st.withFont(new FontDescription.Resource(Identifier.fromNamespaceAndPath("azure", f))));
	}

	public static void logo(GuiGraphics g, float t, float al, int cy) {
		logo(g, t, al, cy, 99f);
	}

	public static void logoAt(GuiGraphics g, float t, float al, int lx, int cy, float rv) {
		lg(g, t, al, lx, cy, rv, false, 34f);
	}

	public static void logo(GuiGraphics g, float t, float al, int cy, float rv) {
		lg(g, t, al, g.guiWidth() / 2, cy, rv, true, 44f);
	}

	public static int lmode = 0;

	static void lg(GuiGraphics g, float t, float al, int xm, int cy, float rv, boolean center, float z) {
		int ac = Hud.ac() & 0xFFFFFF;
		String s = "Azure";
		float sp = 1.5f;
		float[] ke = {0f, 0f, 0f, 0f, 0f};
		float tot = -sp;
		for (int i = 0; i < s.length(); i++) tot += Tf.wf(String.valueOf(s.charAt(i)), z) + sp + (Tf.on() ? ke[i] * z / 44f : 0f);
		float x0 = center ? xm - tot / 2f : xm;
		glow(g, Math.round(x0 + tot / 2f), cy, (int) tot + 200, 120, ac, al * (0.34f + 0.16f * (float) Math.sin(t * 1.5707964f)));
		float swp = (t % 8f) / 0.9f;
		float x = x0;
		for (int i = 0; i < s.length(); i++) {
			float k = Ui.ez((rv - i * 0.09f) / 0.5f);
			String ch = String.valueOf(s.charAt(i));
			float y = cy - z * 0.5f + (1f - k) * 7f;
			if (lmode == 0) {
				y += (float) Math.sin(t * 2.2f + i * 0.9f) * 1.6f;
				float sw = 0.5f + 0.5f * (float) Math.sin(t * 2.6f - i * 0.85f);
				float fl = swp < 1.3f ? Math.max(0f, 1f - Math.abs(swp * 6.5f - 1f - i) / 1.3f) : 0f;
				Tf.dr(g, ch, x, y, z, col(mix(mix(0xFFFFFF, mix(ac, 0xFFFFFF, 0.35f), sw * (1f - fl)), 0xFFFFFF, fl), al * k));
			} else {
				float pl = 0.5f + 0.5f * (float) Math.sin(t * 3.2f - i * 0.7f);
				Tf.dr(g, ch, x, y, z, col(0xFFFFFF, al * k * (0.45f + 0.55f * pl)));
			}
			x += Tf.wf(ch, z) + sp + (Tf.on() ? ke[i] * z / 44f : 0f);
		}
	}

	public static void bar(GuiGraphics g, float t, float al, int y, float pr) {
		int w = g.guiWidth();
		int ac = Hud.ac() & 0xFFFFFF;
		int bw = 120;
		int bx = (w - bw) / 2;
		pr = Math.max(0f, Math.min(1f, pr));
		int fw = (int) (bw * pr);
		Ui.rr(g, bx, y, bw, 2, 1, col(0xFFFFFF, al * 0.14f));
		if (fw > 1) {
			Ui.rr(g, bx - 1, y - 1, fw + 2, 4, 2, col(ac, al * 0.18f));
			Ui.rr(g, bx, y, fw, 2, 1, col(mix(ac, 0xFFFFFF, 0.55f), al));
		}
		String pc = (int) (pr * 100) + "%";
		Tf.dr(g, pc, (w - Tf.w(pc, 8f)) / 2f, y + 8, 8f, col(0xFFFFFF, al * 0.4f));
	}

	static final String[] TP = {"Right Shift opens the Azure menu", "Drag your hud in Edit HUD", "Share your setup with a profile code", "Turn modules on one at a time", "Drag your skin on the title screen"};

	public static void tip(GuiGraphics g, float t, float al, int y) {
		int w = g.guiWidth();
		int i = (int) (t / 3f) % TP.length;
		float ph = (t % 3f) / 3f;
		float a = (float) Math.min(1f, Math.min(ph * 6f, (1f - ph) * 6f));
		Tf.dr(g, TP[i], (w - Tf.w(TP[i], 9f)) / 2f, y, 9f, col(0xFFFFFF, al * a * 0.55f));
	}
}
