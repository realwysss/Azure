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
			int stp = 4;
			for (int x = 0; x < w; x += stp) {
				float y = yb + am * (float) Math.sin(x * v[2] + t * v[3] * 1.4f) + am * 0.45f * (float) Math.sin(x * v[2] * 2.3f - t * v[4] * 30f);
				g.blit(RenderPipelines.GUI_TEXTURED, RB, x, Math.round(y), 0f, 0f, stp, rh, 8, 256, 8, 256, cl);
			}
		}
		stars(g, t, al, w, h);
		g.fillGradient(0, 0, w, h, col(0x000000, al * 0.1f), col(0x000000, al * 0.42f));
	}

	static final float[][] SD = new float[48][];

	static {
		java.util.Random r = new java.util.Random(7);
		for (int i = 0; i < SD.length; i++) SD[i] = new float[]{r.nextFloat(), r.nextFloat(), 0.5f + r.nextFloat(), 12f + r.nextFloat() * 22f, r.nextFloat()};
	}

	static void stars(GuiGraphics g, float t, float al, int w, int h) {
		for (int i = 0; i < 36; i++) {
			float tw = 0.35f + 0.65f * (0.5f + 0.5f * (float) Math.sin(t * (0.8f + SD[i][2]) + i * 1.7f));
			g.fill((int) (SD[i][0] * w), (int) (SD[i][1] * h * 0.9f), (int) (SD[i][0] * w) + 1, (int) (SD[i][1] * h * 0.9f) + 1, col(0xFFFFFF, al * 0.5f * tw));
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
			g.fillGradient(0, -(int) len, 1, 0, col(0xFFFFFF, 0f), col(0xFFFFFF, a));
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
		float tot = -sp;
		for (int i = 0; i < s.length(); i++) tot += Tf.wf(String.valueOf(s.charAt(i)), z) + sp;
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
			x += Tf.wf(ch, z) + sp;
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
