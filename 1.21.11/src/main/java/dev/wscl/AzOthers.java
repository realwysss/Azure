package dev.wscl;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.network.chat.Component;

public class AzOthers extends Screen {
	final Screen par;
	final List<AbstractWidget> ws;
	final float[] an;
	int y0;
	int rw;
	int lx;
	int rh;

	public AzOthers(Screen p, List<AbstractWidget> ws) {
		super(Component.literal("More"));
		par = p;
		this.ws = new ArrayList<>(ws);
		an = new float[ws.size() + 1];
	}

	protected void init() {
		rw = Math.min(190, Math.round(width * 0.5f));
		lx = (width - rw) / 2;
		int n = ws.size() + 1;
		rh = Math.max(16, Math.min(22, (height - 90) / n - 4));
		y0 = Math.max(54, (height - (n * (rh + 4) - 4)) / 2 + 8);
	}

	public void renderBackground(GuiGraphics g, int mx, int my, float d) {
		Vis.bg(g, (net.minecraft.util.Util.getMillis() % 1000000L) / 1000f, 1f);
	}

	public void render(GuiGraphics g, int mx, int my, float d) {
		renderBackground(g, mx, my, d);
		int ac = Hud.ac() & 0xFFFFFF;
		Ui.ts(g, "More", lx, y0 - 32, 18f, 0xFFFFFF, 1f);
		for (int i = 0; i <= ws.size(); i++) {
			int y = y0 + i * (rh + 4);
			String t = i < ws.size() ? ws.get(i).getMessage().getString() : "Back";
			boolean ov = mx >= lx && mx < lx + rw && my >= y && my < y + rh;
			an[i] += ((ov ? 1f : 0f) - an[i]) * 0.3f;
			float h = an[i];
			if (h > 0.03f) Ui.glow(g, lx, y, rw, rh, Math.min(10, rh / 2), ac, h * 0.7f);
			Ui.rr(g, lx, y, rw, rh, Math.min(10, rh / 2), Ui.al(Vis.mix(0x0A161A, ac, 0.35f * h), 0.7f + 0.2f * h));
			Ui.ro(g, lx, y, rw, rh, Math.min(10, rh / 2), Ui.al(h > 0.05f ? ac : 0xFFFFFF, 0.16f + 0.8f * h));
			Ui.ts(g, t, lx + (rw - Ui.w(t, 9f)) / 2f, y + rh / 2f - 4f, 9f, 0xFFFFFF, 1f);
		}
	}

	public boolean mouseClicked(MouseButtonEvent e, boolean dbl) {
		for (int i = 0; i <= ws.size(); i++) {
			int y = y0 + i * (rh + 4);
			if (e.x() >= lx && e.x() < lx + rw && e.y() >= y && e.y() < y + rh) {
				Snd.p("click");
				if (i == ws.size()) minecraft.setScreen(par);
				else {
					AbstractWidget w = ws.get(i);
					w.mouseClicked(new MouseButtonEvent(w.getX() + 2, w.getY() + 2, new MouseButtonInfo(0, 0)), false);
				}
				return true;
			}
		}
		return super.mouseClicked(e, dbl);
	}

	public void onClose() {
		minecraft.setScreen(par);
	}
}
