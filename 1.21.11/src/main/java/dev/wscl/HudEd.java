package dev.wscl;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public class HudEd extends Screen {
	final Screen par;
	Hud.El sel;
	double ox;
	double oy;
	boolean gx;
	boolean gy;
	final float[] an = new float[4];

	public HudEd(Screen p) {
		super(Component.literal("Hud editor"));
		par = p;
	}

	protected void init() {
		Hud.ed = true;
	}

	public void removed() {
		Hud.ed = false;
		Cfg.sv();
	}

	Hud.El at(double mx, double my) {
		for (int i = Hud.ELS.size() - 1; i >= 0; i--) if (Hud.ELS.get(i).has(mx, my)) return Hud.ELS.get(i);
		return null;
	}

	public void renderBackground(GuiGraphics g, int mx, int my, float d) {
	}

	boolean in(double mx, double my, int x, int y, int w, int h) {
		return mx >= x && mx < x + w && my >= y && my < y + h;
	}

	boolean snap() {
		return Cfg.gi("hudsnap", 1) == 1;
	}

	void btn(GuiGraphics g, int i, int x, int y, int w, String t, boolean pri, int mx, int my, int ac) {
		boolean ov = in(mx, my, x, y, w, 18);
		an[i] += ((ov ? 1f : 0f) - an[i]) * 0.3f;
		float a = an[i];
		if (pri) {
			Ui.rr(g, x, y, w, 18, 9, Ui.al(ac, 0.55f + 0.35f * a));
			Ui.ro(g, x, y, w, 18, 9, Ui.al(ac, 0.9f));
		} else {
			Ui.rr(g, x, y, w, 18, 9, Ui.al(0x0C0A1A, 0.8f));
			Ui.ro(g, x, y, w, 18, 9, Ui.al(a > 0.05f ? ac : 0xFFFFFF, 0.2f + 0.7f * a));
		}
		Ui.ts(g, t, x + (w - Ui.w(t, 9f)) / 2f, y + 9 - 4f, 9f, 0xFFFFFF, 1f);
	}

	public void render(GuiGraphics g, int mx, int my, float d) {
		g.fill(0, 0, width, height, 0x66000000);
		int ac = Hud.ac() & 0xFFFFFF;
		for (int x = 0; x < width; x += 20) g.fill(x, 0, x + 1, height, Ui.al(0xFFFFFF, 0.03f));
		for (int y = 0; y < height; y += 20) g.fill(0, y, width, y + 1, Ui.al(0xFFFFFF, 0.03f));
		g.fill(width / 2, 0, width / 2 + 1, height, Ui.al(ac, gx ? 0.7f : 0.14f));
		g.fill(0, height / 2, width, height / 2 + 1, Ui.al(ac, gy ? 0.7f : 0.14f));
		Hud.El hv = at(mx, my);
		for (Hud.El e : Hud.ELS) {
			if (e.w <= 0) continue;
			int x = (int) e.x;
			int y = (int) e.y;
			int w = (int) e.w;
			int h = (int) e.h;
			boolean on = e == sel;
			boolean hh = e == hv;
			Ui.rr(g, x - 2, y - 2, w + 4, h + 4, 5, Ui.al(ac, on ? 0.2f : hh ? 0.14f : 0.06f));
			Ui.ro(g, x - 2, y - 2, w + 4, h + 4, 5, Ui.al(on ? 0xFFFFFF : ac, on ? 1f : hh ? 0.9f : 0.5f));
			if (on || hh) {
				int tw = Ui.w(e.nm) + 12;
				int tx = Math.max(2, Math.min(x, width - tw - 2));
				int ty = y < 18 ? y + h + 5 : y - 17;
				Ui.rr(g, tx, ty, tw, 13, 6, Ui.al(0x0C0A1A, 0.95f));
				Ui.ro(g, tx, ty, tw, 13, 6, Ui.al(ac, 0.8f));
				Ui.ts(g, e.nm, tx + 6, ty + 6.5f - 4f, 9f, 0xFFFFFF, 1f);
			}
		}
		int bw = 400;
		int bx = (width - bw) / 2;
		Ui.glow(g, bx, 8, bw, 24, 12, ac, 0.5f);
		Ui.rr(g, bx, 8, bw, 24, 12, Ui.al(0x0C0A1A, 0.92f));
		Ui.ro(g, bx, 8, bw, 24, 12, Ui.al(0xFFFFFF, 0.14f));
		Ui.ts(g, "HUD editor", bx + 14, 20 - 4f, 9f, 0xFFFFFF, 1f);
		String ht = "Drag to move";
		Ui.ts(g, ht, bx + 80, 20 - 4f, 8f, 0xFFFFFF, 0.5f);
		int by = 11;
		btn(g, 0, bx + bw - 188, by, 52, snap() ? "Snap on" : "Snap off", false, mx, my, ac);
		btn(g, 1, bx + bw - 130, by, 56, "Reset all", false, mx, my, ac);
		btn(g, 2, bx + bw - 68, by, 56, "Done", true, mx, my, ac);
	}

	public boolean mouseClicked(MouseButtonEvent e, boolean tw) {
		int bw = 400;
		int bx = (width - bw) / 2;
		int by = 11;
		if (in(e.x(), e.y(), bx + bw - 188, by, 52, 18)) {
			Cfg.si("hudsnap", snap() ? 0 : 1);
			return true;
		}
		if (in(e.x(), e.y(), bx + bw - 130, by, 56, 18)) {
			for (Hud.El i : Hud.ELS) i.reset();
			return true;
		}
		if (in(e.x(), e.y(), bx + bw - 68, by, 56, 18)) {
			onClose();
			return true;
		}
		Hud.El i = at(e.x(), e.y());
		if (i == null) return super.mouseClicked(e, tw);
		if (e.button() == 1) {
			i.reset();
			return true;
		}
		sel = i;
		ox = e.x() - i.x;
		oy = e.y() - i.y;
		return true;
	}

	public boolean mouseDragged(MouseButtonEvent e, double dx, double dy) {
		if (sel == null) return super.mouseDragged(e, dx, dy);
		int px = (int) Math.round((e.x() - ox) / Math.max(1, width - sel.w) * 100);
		int py = (int) Math.round((e.y() - oy) / Math.max(1, height - sel.h) * 100);
		gx = false;
		gy = false;
		if (snap()) {
			if (Math.abs(px - 50) <= 2) {
				px = 50;
				gx = true;
			}
			if (Math.abs(py - 50) <= 2) {
				py = 50;
				gy = true;
			}
			if (px <= 1) px = 0;
			if (px >= 99) px = 100;
			if (py <= 1) py = 0;
			if (py >= 99) py = 100;
		}
		sel.pos(Math.max(0, Math.min(100, px)), Math.max(0, Math.min(100, py)));
		return true;
	}

	public boolean mouseReleased(MouseButtonEvent e) {
		sel = null;
		gx = false;
		gy = false;
		return super.mouseReleased(e);
	}

	public void onClose() {
		Cfg.sv();
		Minecraft.getInstance().setScreen(par);
	}

	public boolean isPauseScreen() {
		return false;
	}
}
