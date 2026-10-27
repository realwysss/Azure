package dev.wscl;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public class AzWarn extends Screen {
	final Screen par;
	final Runnable ok;
	final String nm;
	int px;
	int py;
	int pw = 360;
	int ph = 200;

	public AzWarn(Screen p, String nm, Runnable ok) {
		super(Component.literal("Warning"));
		par = p;
		this.nm = nm;
		this.ok = ok;
	}

	protected void init() {
		pw = Math.min(360, width - 30);
		px = (width - pw) / 2;
		py = (height - ph) / 2;
	}

	public void renderBackground(GuiGraphics g, int mx, int my, float d) {
		if (par != null) par.render(g, -1, -1, d);
		g.fill(0, 0, width, height, Ui.al(0x000000, 0.65f));
	}

	boolean in(double mx, double my, int x, int y, int w, int h) {
		return mx >= x && mx < x + w && my >= y && my < y + h;
	}

	public void render(GuiGraphics g, int mx, int my, float d) {
		renderBackground(g, mx, my, d);
		int ac = Hud.ac() & 0xFFFFFF;
		Ui.glow(g, px, py, pw, ph, 14, 0xFF6060, 0.3f);
		Ui.rr(g, px, py, pw, ph, 14, Ui.al(Ui.tn(0x0C0A1A), 0.97f));
		Ui.ro(g, px, py, pw, ph, 14, Ui.al(0xFF6060, 0.5f));
		Ic.dr(g, "warn", px + 28, py + 30, 22, Ui.al(0xFF6060, 1f));
		Ui.ts(g, "PvP module", px + 48, py + 22, 14f, 0xFFFFFF, 1f);
		String all = "\"" + nm + "\" can count as an unfair advantage on some servers. Servers can ban you for using it. If that happens it is your fault and your risk, not the fault of Azure or its team. Only turn it on where it is allowed.";
		var wl = net.minecraft.client.Minecraft.getInstance().font.split(Component.literal(all), pw - 40);
		for (int i = 0; i < wl.size(); i++) g.drawString(font, wl.get(i), px + 20, py + 54 + i * 11, i == 0 ? 0xFFFFFFFF : 0xFFC8C8D8, false);
		btn(g, mx, my, ac, px + 20, py + ph - 40, 150, 24, "I understand, turn it on", true);
		btn(g, mx, my, ac, px + pw - 20 - 90, py + ph - 40, 90, 24, "Cancel", false);
	}

	void btn(GuiGraphics g, int mx, int my, int ac, int x, int y, int w, int h, String t, boolean pri) {
		boolean ov = in(mx, my, x, y, w, h);
		int c = pri ? 0xFF6060 : ac;
		Ui.rr(g, x, y, w, h, 10, Ui.al(Vis.mix(Ui.tn(0x0C0A1A), c, ov ? 0.5f : pri ? 0.3f : 0.1f), 0.95f));
		Ui.ro(g, x, y, w, h, 10, Ui.al(pri ? c : 0xFFFFFF, ov ? 0.9f : 0.35f));
		Ui.ts(g, t, x + (w - Ui.w(t, 9f)) / 2f, y + h / 2f - 4f, 9f, 0xFFFFFF, 1f);
	}

	public boolean mouseClicked(MouseButtonEvent e, boolean dbl) {
		if (in(e.x(), e.y(), px + 20, py + ph - 40, 150, 24)) {
			Snd.p("click");
			minecraft.setScreen(par);
			ok.run();
			return true;
		}
		if (in(e.x(), e.y(), px + pw - 110, py + ph - 40, 90, 24)) {
			Snd.p("click");
			minecraft.setScreen(par);
			return true;
		}
		return super.mouseClicked(e, dbl);
	}

	public void onClose() {
		minecraft.setScreen(par);
	}
}
