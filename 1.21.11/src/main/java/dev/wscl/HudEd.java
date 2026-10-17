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

	public HudEd(Screen p) {
		super(Component.literal("Hud editor"));
		par = p;
	}

	Hud.El at(double mx, double my) {
		for (int i = Hud.ELS.size() - 1; i >= 0; i--) if (Hud.ELS.get(i).has(mx, my)) return Hud.ELS.get(i);
		return null;
	}

	public void renderBackground(GuiGraphics g, int mx, int my, float d) {
	}

	public void render(GuiGraphics g, int mx, int my, float d) {
		g.fill(0, 0, width, height, 0x55000000);
		int ac = Hud.ac() & 0xFFFFFF;
		for (int x = 0; x < width; x += 20) g.fill(x, 0, x + 1, height, Ui.al(0xFFFFFF, 0.03f));
		for (int y = 0; y < height; y += 20) g.fill(0, y, width, y + 1, Ui.al(0xFFFFFF, 0.03f));
		g.fill(width / 2, 0, width / 2 + 1, height, Ui.al(ac, 0.15f));
		g.fill(0, height / 2, width, height / 2 + 1, Ui.al(ac, 0.15f));
		Hud.El hv = at(mx, my);
		for (Hud.El e : Hud.ELS) {
			if (e.w <= 0) continue;
			int x = (int) e.x;
			int y = (int) e.y;
			int w = (int) e.w;
			int h = (int) e.h;
			boolean on = e == sel;
			if (on || e == hv) Ui.glow(g, x - 2, y - 2, w + 4, h + 4, 5, ac, on ? 1f : 0.6f);
			Ui.rr(g, x - 2, y - 2, w + 4, h + 4, 5, Ui.al(ac, on ? 0.25f : 0.1f));
			Ui.ro(g, x - 2, y - 2, w + 4, h + 4, 5, Ui.al(on ? 0xFFFFFF : ac, on ? 1f : 0.7f));
			int tw = Ui.w(e.nm) + 10;
			int tx = Math.max(2, Math.min(x, width - tw - 2));
			int ty = y < 16 ? y + h + 5 : y - 16;
			Ui.rr(g, tx, ty, tw, 12, 4, Ui.al(0x060A15, 0.9f));
			Ui.ro(g, tx, ty, tw, 12, 4, Ui.al(ac, 0.6f));
			Ui.tx(g, e.nm, tx + 5, ty + 2, 0xFFFFFF, 1f);
		}
		String t = "Drag to move   Right click resets   Esc saves";
		int tw = Ui.w(t) + 20;
		int ty = height - 44;
		Ui.glow(g, (width - tw) / 2, ty, tw, 18, 9, ac, 0.8f);
		Ui.rr(g, (width - tw) / 2, ty, tw, 18, 9, Ui.al(0x060A15, 0.92f));
		Ui.ro(g, (width - tw) / 2, ty, tw, 18, 9, Ui.al(ac, 0.7f));
		Ui.cx(g, t, width / 2, ty + 5, 0xFFFFFF, 1f);
	}

	public boolean mouseClicked(MouseButtonEvent e, boolean tw) {
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
		sel.pos(Math.max(0, Math.min(100, px)), Math.max(0, Math.min(100, py)));
		return true;
	}

	public boolean mouseReleased(MouseButtonEvent e) {
		sel = null;
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
