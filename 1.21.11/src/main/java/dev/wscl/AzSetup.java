package dev.wscl;

import dev.wysl.Thm;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;

public class AzSetup extends Screen {
	static final int[] CO = {0x8B5CF6, 0x6366F1, 0x3B82F6, 0x06B6D4, 0x10B981, 0xF59E0B, 0xEF4444, 0xEC4899};
	final Screen par;
	int step;
	boolean lis;
	long t0 = Util.getMillis();
	int cx;
	int cy;
	int cw = 380;
	int ch = 230;
	int[][] bx = new int[0][];

	public AzSetup(Screen p) {
		super(Component.literal("Azure setup"));
		par = p;
	}

	public void renderBackground(GuiGraphics g, int mx, int my, float d) {
	}

	boolean in(int mx, int my, int x, int y, int w, int h) {
		return mx >= x && mx < x + w && my >= y && my < y + h;
	}

	void btn(GuiGraphics g, int mx, int my, int ac, int x, int y, int w, int h, String t, boolean pri) {
		boolean ov = in(mx, my, x, y, w, h);
		Ui.rr(g, x, y, w, h, 9, Ui.al(pri ? ac : 0x120E26, pri ? 0.55f + (ov ? 0.25f : 0f) : 0.8f));
		Ui.ro(g, x, y, w, h, 9, Ui.al(pri || ov ? ac : 0xFFFFFF, pri ? 0.9f : ov ? 0.8f : 0.16f));
		Ui.ts(g, t, x + (w - Ui.w(t, 9f)) / 2f, y + h / 2f - 3.7f, 9f, 0xFFFFFF, 1f);
	}

	public void render(GuiGraphics g, int mx, int my, float d) {
		float t = (Util.getMillis() % 1000000L) / 1000f;
		Vis.bg(g, t, 1f);
		int ac = Hud.ac() & 0xFFFFFF;
		float k = Ui.ez((Util.getMillis() - t0) / 400f);
		Ui.GA = k;
		cx = (width - cw) / 2;
		cy = (height - ch) / 2 + Math.round((1f - k) * 12);
		Ui.glow(g, cx, cy, cw, ch, 14, ac, 0.4f);
		Ui.rr(g, cx, cy, cw, ch, 14, Ui.al(0x090714, 0.95f));
		g.fillGradient(cx + 14, cy + 1, cx + cw - 14, cy + 70, Ui.al(ac, 0.1f), Ui.al(ac, 0f));
		Ui.ro(g, cx, cy, cw, ch, 14, Ui.al(0xFFFFFF, 0.14f));
		String h = step == 0 ? "Welcome to Azure" : "Menu key";
		Ui.ts(g, h, cx + 24, cy + 22, 16f, 0xFFFFFF, 1f);
		Ui.tx(g, "Step " + (step + 1) + " of 2", cx + cw - 24 - Ui.w("Step " + (step + 1) + " of 2"), cy + 27, 0xFFFFFF, 0.4f);
		if (step == 0) {
			Ui.tx(g, "Pick your color. You can change it any time in Settings.", cx + 24, cy + 56, 0xFFFFFF, 0.6f);
			int sw = 34;
			int gp = 10;
			int tw = CO.length * sw + (CO.length - 1) * gp;
			int sx = cx + (cw - tw) / 2;
			int sy = cy + 96;
			for (int i = 0; i < CO.length; i++) {
				int x = sx + i * (sw + gp);
				boolean on = (Thm.get() & 0xFFFFFF) == CO[i];
				boolean ov = in(mx, my, x, sy, sw, sw);
				if (on || ov) Ui.glow(g, x, sy, sw, sw, 10, CO[i], on ? 0.8f : 0.4f);
				Ui.rr(g, x, sy, sw, sw, 10, Ui.al(CO[i], 1f));
				if (on) Ui.ro(g, x - 3, sy - 3, sw + 6, sw + 6, 12, Ui.al(0xFFFFFF, 0.9f));
			}
			Ui.cx(g, "Your accent is used for buttons, glows and the HUD.", cx + cw / 2, cy + 156, 0xFFFFFF, 0.4f);
		} else {
			Ui.tx(g, "This key opens the Azure menu in game.", cx + 24, cy + 56, 0xFFFFFF, 0.6f);
			String s = lis ? "Press a key..." : Wc.key.getTranslatedKeyMessage().getString();
			int kw = Math.max(110, Ui.w(s) + 40);
			int kx = cx + (cw - kw) / 2;
			int ky = cy + 92;
			boolean ov = in(mx, my, kx, ky, kw, 32);
			Ui.rr(g, kx, ky, kw, 32, 10, Ui.al(lis ? ac : 0xFFFFFF, lis ? 0.3f : 0.08f + (ov ? 0.05f : 0f)));
			Ui.ro(g, kx, ky, kw, 32, 10, Ui.al(lis ? ac : 0xFFFFFF, lis ? 0.9f : 0.22f));
			Ui.cx(g, s, cx + cw / 2, ky + 12, 0xFFFFFF, 1f);
			Ui.cx(g, "Click the key to change it", cx + cw / 2, cy + 140, 0xFFFFFF, 0.4f);
		}
		btn(g, mx, my, ac, cx + 24, cy + ch - 44, 90, 26, "Skip", false);
		btn(g, mx, my, ac, cx + cw - 24 - 110, cy + ch - 44, 110, 26, step == 0 ? "Next" : "Done", true);
		Ui.GA = 1f;
	}

	void fin() {
		Cfg.c.fs = true;
		Cfg.sv();
		minecraft.setScreen(par);
	}

	public boolean mouseClicked(MouseButtonEvent e, boolean dbl) {
		int mx = (int) e.x();
		int my = (int) e.y();
		if (in(mx, my, cx + 24, cy + ch - 44, 90, 26)) {
			fin();
			return true;
		}
		if (in(mx, my, cx + cw - 24 - 110, cy + ch - 44, 110, 26)) {
			lis = false;
			if (step == 0) step = 1;
			else fin();
			return true;
		}
		if (step == 0) {
			int sw = 34;
			int gp = 10;
			int tw = CO.length * sw + (CO.length - 1) * gp;
			int sx = cx + (cw - tw) / 2;
			for (int i = 0; i < CO.length; i++) if (in(mx, my, sx + i * (sw + gp), cy + 96, sw, sw)) {
				Thm.set(CO[i]);
				Snd.p("click");
				return true;
			}
		} else {
			String s = lis ? "Press a key..." : Wc.key.getTranslatedKeyMessage().getString();
			int kw = Math.max(110, Ui.w(s) + 40);
			if (in(mx, my, cx + (cw - kw) / 2, cy + 92, kw, 32)) {
				lis = true;
				return true;
			}
		}
		lis = false;
		return super.mouseClicked(e, dbl);
	}

	public boolean keyPressed(KeyEvent e) {
		if (lis) {
			if (e.key() != org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE) {
				Wc.key.setKey(com.mojang.blaze3d.platform.InputConstants.Type.KEYSYM.getOrCreate(e.key()));
				net.minecraft.client.KeyMapping.resetMapping();
				minecraft.options.save();
			}
			lis = false;
			return true;
		}
		return super.keyPressed(e);
	}

	public void onClose() {
		fin();
	}

	public boolean isPauseScreen() {
		return false;
	}
}
