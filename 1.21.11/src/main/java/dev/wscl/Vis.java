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

	public static void bg(GuiGraphics g, float t, float al) {
		int w = g.guiWidth();
		int h = g.guiHeight();
		int ac = Hud.ac() & 0xFFFFFF;
		g.fillGradient(0, 0, w, h, col(0x0B0718, al), col(0x040309, al));
		if (Cfg.c.lite) {
			g.fillGradient(0, 0, w, h, col(mix(0x000000, ac, 0.16f), al * 0.35f), col(0x000000, al * 0.5f));
			return;
		}
		int rh = Math.round(h * 0.62f);
		for (float[] v : WV) {
			int c = mix(mix(0x2A1066, ac, v[6]), 0xFFFFFF, v[8] * 0.12f);
			int cl = col(c, al * v[7]);
			float yb = h * v[0] - rh * 0.3f;
			float am = h * v[1];
			int fp = net.minecraft.client.Minecraft.getInstance().getFps();
			int stp = fp < 40 ? 6 : fp < 100 ? 3 : 2;
			var pp = g.pose();
			for (int x = 0; x < w; x += stp) {
				float y = yb + am * (float) Math.sin(x * v[2] + t * v[3] * 1.4f) + am * 0.45f * (float) Math.sin(x * v[2] * 2.3f - t * v[4] * 30f);
				pp.pushMatrix();
				pp.translate(x, y);
				g.blit(RenderPipelines.GUI_TEXTURED, RB, 0, 0, 0f, 0f, stp, rh, 8, 256, 8, 256, cl);
				pp.popMatrix();
			}
		}
		stars(g, t, al, w, h);
		g.fillGradient(0, 0, w, h, col(0x000000, al * 0.1f), col(0x000000, al * 0.42f));
	}

	public static void bgx(GuiGraphics g, float t, float al) {
		int st = Cfg.gi("beta", 0) == 1 ? Cfg.gi("tbg", 0) : 0;
		if (st <= 0 || Cfg.c.lite) {
			bg(g, t, al);
			return;
		}
		t = t * Cfg.gi("tbg.sp", 100) / 100f;
		float br = Cfg.gi("tbg.br", 100) / 100f;
		int w = g.guiWidth();
		int h = g.guiHeight();
		int cm = Cfg.gi("tbg.cm", 0);
		int ac = cm == 1 ? Cfg.gi("tbg.cc", 0x8B5CF6) & 0xFFFFFF : cm == 2 ? java.awt.Color.HSBtoRGB((t * 0.05f) % 1f, 0.6f, 1f) & 0xFFFFFF : Hud.ac() & 0xFFFFFF;
		float a2 = Math.min(1f, al * br);
		g.fillGradient(0, 0, w, h, col(0x0B0718, al), col(0x040309, al));
		if (st == 1) {
			int hy = Math.round(h * 0.58f);
			g.fillGradient(0, hy - Math.round(h * 0.3f), w, hy, col(ac, 0f), col(mix(0x2A1066, ac, 0.6f), a2 * 0.38f));
			float p = (t * 0.35f) % 1f;
			for (int i = 0; i < 15; i++) {
				float k = (i + p) / 15f;
				int y = hy + Math.round((h - hy) * k * k);
				g.fill(0, y, w, y + 1, col(ac, a2 * (0.1f + 0.4f * k)));
			}
			g.fill(0, hy, w, hy + 1, col(mix(ac, 0xFFFFFF, 0.5f), a2 * 0.7f));
			for (int k = -6; k <= 6; k++) {
				float bx = w / 2f + k * w * 0.16f;
				for (int y = hy; y < h; y += 3) {
					float f = (y - hy) / (float) (h - hy);
					int x = Math.round(w / 2f + (bx - w / 2f) * f);
					g.fill(x, y, x + 1, y + 3, col(ac, a2 * (0.06f + 0.22f * f)));
				}
			}
			stars(g, t, a2 * 0.7f, w, hy);
		} else if (st == 2) {
			for (int i = 0; i < 5; i++) {
				float a = t * (0.07f + i * 0.021f) + i * 1.9f;
				int bw = Math.round(w * (0.5f + 0.1f * i));
				int bh = Math.round(h * 0.7f);
				int x = Math.round(w * (0.5f + 0.42f * (float) Math.sin(a)) - bw / 2f);
				int y = Math.round(h * (0.5f + 0.38f * (float) Math.cos(a * 1.3f)) - bh / 2f);
				Ui.GA = 1f;
				Ui.glow(g, x, y, bw, bh, Math.min(bw, bh) / 2, mix(mix(0x2A1066, ac, i % 2 == 0 ? 0.8f : 0.3f), 0xFF5CC8, i == 3 ? 0.4f : 0f), a2 * 0.5f);
			}
			stars(g, t, a2 * 0.6f, w, h);
		} else if (st == 3) {
			for (int i = 0; i < SD.length; i++) {
				float sp = 120f + SD[i][3] * 9f;
				int len = 30 + Math.round(SD[i][2] * 40f);
				float y = (t * sp + SD[i][4] * (h + 120)) % (h + 120) - 60;
				int x = Math.round(SD[i][0] * w);
				g.fillGradient(x, Math.round(y), x + 1, Math.round(y) + len, col(ac, 0f), col(mix(ac, 0xFFFFFF, 0.4f), a2 * 0.55f));
			}
			g.fillGradient(0, Math.round(h * 0.7f), w, h, col(ac, 0f), col(ac, a2 * 0.16f));
		} else if (st == 4) {
			for (int i = 0; i < 3; i++) {
				float s = 0.5f + 0.5f * (float) Math.sin(t * (0.25f + i * 0.11f) + i * 2.1f);
				int c = mix(mix(0x2A1066, ac, 0.3f + 0.5f * s), 0xFF5CC8, i == 2 ? 0.25f * s : 0f);
				int y0 = Math.round(h * (i * 0.28f - 0.1f + 0.1f * s));
				g.fillGradient(0, y0, w, y0 + Math.round(h * 0.55f), col(c, 0f), col(c, a2 * 0.32f));
			}
			stars(g, t, a2 * 0.5f, w, h);
		} else if (st == 5) {
			float cx = w / 2f;
			float cy = h / 2f;
			float far = (float) Math.sqrt(cx * cx + cy * cy) * 1.1f;
			for (int i = 0; i < 90; i++) {
				float an = (SD[i % SD.length][0] + i * 0.618f) * 6.2831855f;
				float r = ((t * (0.12f + 0.1f * SD[i % SD.length][2]) + SD[(i + 7) % SD.length][1] + i * 0.13f) % 1f);
				float d0 = r * r * far;
				float d1 = Math.max(0f, (r - 0.05f) * (r - 0.05f)) * far;
				float al2 = Math.min(1f, r * 2.5f) * a2;
				for (int k = 0; k < 5; k++) {
					float d = d1 + (d0 - d1) * k / 4f;
					int x = Math.round(cx + (float) Math.cos(an) * d);
					int y = Math.round(cy + (float) Math.sin(an) * d);
					g.fill(x, y, x + 1 + (r > 0.6f ? 1 : 0), y + 1 + (r > 0.6f ? 1 : 0), col(mix(ac, 0xFFFFFF, 0.5f), al2 * (0.3f + 0.15f * k)));
				}
			}
			Ui.GA = 1f;
			Ui.glow(g, Math.round(cx - w * 0.2f), Math.round(cy - h * 0.2f), Math.round(w * 0.4f), Math.round(h * 0.4f), Math.round(h * 0.2f), ac, a2 * 0.18f);
		} else if (st == 6) {
			for (int i = 0; i < 70; i++) {
				float[] q = SD[i % SD.length];
				float sp = 0.05f + 0.06f * q[2] + (i / SD.length) * 0.013f;
				float p = (t * sp + q[4] + i * 0.37f) % 1f;
				int x = Math.round(((q[0] + i * 0.211f) % 1f) * w + 14f * (float) Math.sin(t * 0.8f + i));
				int y = Math.round(h * (1f - p));
				int sz = 1 + (q[2] > 1.1f ? 1 : 0);
				float f = (float) Math.sin(p * Math.PI);
				g.fill(x, y, x + sz, y + sz, col(mix(0xFFB13B, ac, 0.4f + 0.4f * q[1]), a2 * f * 0.9f));
			}
			g.fillGradient(0, Math.round(h * 0.65f), w, h, col(0xFF6A1A, 0f), col(mix(0xFF6A1A, ac, 0.4f), a2 * 0.2f));
		} else if (st == 7) {
			for (int i = 0; i < 26; i++) {
				float[] q = SD[i % SD.length];
				int r = 5 + Math.round(q[2] * 11f);
				float sp = 0.03f + 0.03f * q[2];
				float p = (t * sp + q[4] + i * 0.41f) % 1f;
				int x = Math.round(q[0] * w + 18f * (float) Math.sin(t * 0.6f + i * 1.3f));
				int y = Math.round(h + r * 2 - p * (h + r * 4));
				float f = (float) Math.sin(p * Math.PI);
				Ui.GA = 1f;
				Ui.ro(g, x - r, y - r, r * 2, r * 2, Math.min(r, 12), col(mix(ac, 0xFFFFFF, 0.3f), a2 * 0.5f * f));
				g.fill(x - r / 2, y - r / 2, x - r / 2 + 2, y - r / 2 + 2, col(0xFFFFFF, a2 * 0.5f * f));
			}
			g.fillGradient(0, 0, w, h / 2, col(ac, a2 * 0.1f), col(ac, 0f));
		} else {
			float p = (t * 0.2f) % 1f;
			int cell = Math.max(24, h / 14);
			int off = Math.round(p * cell);
			for (int x = -cell + off; x < w; x += cell) {
				float d = Math.abs(x - w / 2f) / (w / 2f);
				g.fill(x, 0, x + 1, h, col(ac, a2 * (0.22f - 0.15f * d)));
			}
			for (int y = -cell + off; y < h; y += cell) {
				float d = Math.abs(y - h / 2f) / (h / 2f);
				g.fill(0, y, w, y + 1, col(ac, a2 * (0.22f - 0.15f * d)));
			}
			Ui.GA = 1f;
			Ui.glow(g, Math.round(w * 0.2f), Math.round(h * 0.2f), Math.round(w * 0.6f), Math.round(h * 0.6f), Math.round(h * 0.3f), ac, a2 * 0.2f);
		}
		g.fillGradient(0, 0, w, h, col(0x000000, al * 0.08f), col(0x000000, al * Math.max(0.05f, 0.4f - 0.3f * (br - 1f))));
	}

	static final float[][] SD = new float[48][];

	static {
		java.util.Random r = new java.util.Random(7);
		for (int i = 0; i < SD.length; i++) SD[i] = new float[]{r.nextFloat(), r.nextFloat(), 0.5f + r.nextFloat(), 12f + r.nextFloat() * 22f, r.nextFloat()};
	}

	static void stars(GuiGraphics g, float t, float al, int w, int h) {
		for (int i = 0; i < 36; i++) {
			float tw = 0.35f + 0.65f * (0.5f + 0.5f * (float) Math.sin(t * (0.8f + SD[i][2]) + i * 1.7f));
			var sp = g.pose();
			sp.pushMatrix();
			sp.translate(SD[i][0] * w, SD[i][1] * h * 0.9f);
			sp.scale(0.5f, 0.5f);
			g.fill(0, 0, 1, 1, col(0xFFFFFF, al * 0.5f * tw));
			sp.popMatrix();
		}
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
		glow(g, Math.round(x0 + tot / 2f), cy, (int) tot + 200, 120, ac, al * (0.42f + 0.08f * (float) Math.sin(t * 1.4f)));
		float x = x0;
		for (int i = 0; i < s.length(); i++) {
			float k = Ui.ez((rv - i * 0.09f) / 0.5f);
			String ch = String.valueOf(s.charAt(i));
			float y = cy - z * 0.5f + (1f - k) * 7f;
			if (lmode == 0) {
				y += (float) Math.sin(t * 2.2f + i * 0.9f) * 1.6f;
				float sw = 0.5f + 0.5f * (float) Math.sin(t * 2.6f - i * 0.85f);
				Tf.dr(g, ch, x, y, z, col(mix(0xFFFFFF, mix(ac, 0xFFFFFF, 0.35f), sw), al * k));
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
