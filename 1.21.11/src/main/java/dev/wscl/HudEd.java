package dev.wscl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public class HudEd extends Screen {
	final Screen par;
	final Set<Hud.El> msel = new LinkedHashSet<>();
	final Map<Hud.El, float[]> st = new HashMap<>();
	Hud.El prim;
	double ox;
	double oy;
	boolean gx;
	boolean gy;
	final float[] an = new float[8];
	boolean io;
	List<Rw.R> insp = new ArrayList<>();
	Hud.El inspFor;
	float isc;
	float tisc;
	Rw.R act;
	int px0;
	int py0;
	int pw;
	int ph;

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

	int grp(Hud.El e) {
		return Cfg.gi("grp." + e.id, 0);
	}

	void build(Hud.El e) {
		inspFor = e;
		insp = new ArrayList<>();
		Mod m = Mod.get(e.id);
		if (m != null) for (Rw.R r : m.st) if (!(r instanceof Rw.Bt) && !(r instanceof Rw.St) && !(r instanceof Rw.Ed)) insp.add(r);
		insp.add(Rw.tg("Show panel", () -> Cfg.gi(e.id + ".pnl", 1) == 1, v -> Cfg.si(e.id + ".pnl", v ? 1 : 0)));
		insp.add(Rw.sl("Panel opacity", () -> Cfg.gi(e.id + ".pop", 100), v -> Cfg.si(e.id + ".pop", v), 0, 100, "%"));
		insp.add(Rw.sl("Text opacity", () -> Cfg.gi(e.id + ".top", 100), v -> Cfg.si(e.id + ".top", v), 20, 100, "%"));
		insp.add(Rw.sl("Position X", () -> e.gx.getAsInt(), v -> e.sx.accept(v), 0, 100, "%"));
		insp.add(Rw.sl("Position Y", () -> e.gy.getAsInt(), v -> e.sy.accept(v), 0, 100, "%"));
		insp.add(new Rw.Bt("Position", "Reset", () -> e.reset()));
		isc = 0;
		tisc = 0;
	}

	void btn(GuiGraphics g, int i, int x, int y, int w, String t, boolean pri, boolean dis, int mx, int my, int ac) {
		boolean ov = !dis && in(mx, my, x, y, w, 18);
		an[i] += ((ov ? 1f : 0f) - an[i]) * 0.3f;
		float a = an[i];
		float keep = Ui.GA;
		if (dis) Ui.GA = keep * 0.4f;
		if (pri) {
			Ui.rr(g, x, y, w, 18, 9, Ui.al(ac, 0.55f + 0.35f * a));
			Ui.ro(g, x, y, w, 18, 9, Ui.al(ac, 0.9f));
		} else {
			Ui.rr(g, x, y, w, 18, 9, Ui.al(Ui.tn(0x0C0A1A), 0.8f));
			Ui.ro(g, x, y, w, 18, 9, Ui.al(a > 0.05f ? ac : 0xFFFFFF, 0.2f + 0.7f * a));
		}
		Ui.ts(g, t, x + (w - Ui.w(t, 9f)) / 2f, y + 9 - 4f, 9f, 0xFFFFFF, 1f);
		Ui.GA = keep;
	}

	int bx() {
		int bw = 430;
		return (width - bw) / 2;
	}

	public void render(GuiGraphics g, int mx, int my, float d) {
		g.fill(0, 0, width, height, 0x66000000);
		int ac = Hud.ac() & 0xFFFFFF;
		for (int x = 0; x < width; x += 20) g.fill(x, 0, x + 1, height, Ui.al(0xFFFFFF, 0.03f));
		for (int y = 0; y < height; y += 20) g.fill(0, y, width, y + 1, Ui.al(0xFFFFFF, 0.03f));
		g.fill(width / 2, 0, width / 2 + 1, height, Ui.al(ac, gx ? 0.7f : 0.14f));
		g.fill(0, height / 2, width, height / 2 + 1, Ui.al(ac, gy ? 0.7f : 0.14f));
		Hud.El hv = at(mx, my);
		int[] gcol = {0x5CE08A, 0xFFD24A, 0xFF7A9A, 0x5CC8FF, 0xC084FC, 0xFF9A3C};
		for (Hud.El e : Hud.ELS) {
			if (e.w <= 0) continue;
			int x = (int) e.x;
			int y = (int) e.y;
			int w = (int) e.w;
			int h = (int) e.h;
			boolean on = msel.contains(e);
			boolean hh = e == hv;
			int gg = grp(e);
			int oc = gg > 0 ? gcol[gg % gcol.length] : ac;
			Ui.rr(g, x - 2, y - 2, w + 4, h + 4, 5, Ui.al(oc, on ? 0.2f : hh ? 0.14f : 0.06f));
			Ui.ro(g, x - 2, y - 2, w + 4, h + 4, 5, Ui.al(on ? 0xFFFFFF : oc, on ? 1f : hh ? 0.9f : 0.5f));
			if (gg > 0) Ic.dr(g, "link", x + w + 3, y - 1, 8, Ui.al(oc, 1f));
			if (on || hh) {
				int tw = Ui.w(e.nm) + 12;
				int tx = Math.max(2, Math.min(x, width - tw - 2));
				int ty = y < 18 ? y + h + 5 : y - 17;
				Ui.rr(g, tx, ty, tw, 13, 6, Ui.al(Ui.tn(0x0C0A1A), 0.95f));
				Ui.ro(g, tx, ty, tw, 13, 6, Ui.al(oc, 0.8f));
				Ui.ts(g, e.nm, tx + 6, ty + 6.5f - 4f, 9f, 0xFFFFFF, 1f);
			}
		}
		int bw = 430;
		int bx = bx();
		Ui.glow(g, bx, 8, bw, 24, 12, ac, 0.5f);
		Ui.rr(g, bx, 8, bw, 24, 12, Ui.al(Ui.tn(0x0C0A1A), 0.92f));
		Ui.ro(g, bx, 8, bw, 24, 12, Ui.al(0xFFFFFF, 0.14f));
		Ui.ts(g, "HUD editor", bx + 14, 20 - 4f, 9f, 0xFFFFFF, 1f);
		int by = 11;
		boolean can = msel.size() >= 2;
		boolean gr = false;
		for (Hud.El e : msel) if (grp(e) > 0) gr = true;
		btn(g, 0, bx + 76, by, 52, snap() ? "Snap on" : "Snap off", false, false, mx, my, ac);
		btn(g, 1, bx + 134, by, 50, "Group", false, !can, mx, my, ac);
		btn(g, 2, bx + 190, by, 58, "Ungroup", false, !gr, mx, my, ac);
		btn(g, 3, bx + 254, by, 62, "Reset all", false, false, mx, my, ac);
		btn(g, 5, bx + 320, by, 46, "Classic", false, false, mx, my, ac);
		btn(g, 4, bx + bw - 62, by, 52, "Done", true, false, mx, my, ac);
		String hint = msel.isEmpty() ? "Drag to move. Right click for settings. Shift click picks more." : msel.size() + " selected";
		int hw = Ui.w(hint, 8f) + 20;
		int hy = height - 72;
		Ui.rr(g, (width - hw) / 2, hy, hw, 16, 6, Ui.al(Ui.tn(0x0C0A1A), 0.82f));
		Ui.ts(g, hint, (width - Ui.w(hint, 8f)) / 2f, hy + 4, 8f, 0xFFFFFF, 0.8f);
		if (io && prim != null && msel.contains(prim) && msel.size() == 1) {
			if (inspFor != prim) build(prim);
			pw = 214;
			px0 = width - pw - 8;
			py0 = 50;
			ph = height - py0 - 12;
			Ui.rr(g, px0, py0, pw, ph, 12, Ui.al(Ui.tn(0x0C0A1A), 0.94f));
			Ui.ro(g, px0, py0, pw, ph, 12, Ui.al(0xFFFFFF, 0.14f));
			Ui.ts(g, prim.nm, px0 + 12, py0 + 10, 11f, 0xFFFFFF, 1f);
			Ui.tx(g, "Edit this element", px0 + 12, py0 + 24, ac, 0.8f);
			int ry0 = py0 + 40;
			g.enableScissor(px0, ry0, px0 + pw, py0 + ph - 6);
			int yy = ry0 - Math.round(isc);
			int tot = 0;
			for (Rw.R r : insp) {
				r.rx = px0 + 6;
				r.ry = yy;
				r.rw = pw - 12;
				tot += r.h + 3;
				if (yy + r.h > ry0 && yy < py0 + ph) {
					boolean ov = in(mx, my, r.rx, yy, r.rw, r.h) && my >= ry0 && my < py0 + ph - 6;
					r.hv += ((ov ? 1f : 0f) - r.hv) * 0.3f;
					Ui.rr(g, r.rx, yy, r.rw, r.h, 8, Ui.al(0xFFFFFF, 0.035f + 0.05f * r.hv));
					boolean two = r instanceof Rw.Cl || r instanceof Rw.Sw || r instanceof Rw.Ed;
					String lb = r.lb;
					int mxw = r.rw - (two ? 20 : r instanceof Rw.Sl sl2 ? sl2.tw() + 54 : r instanceof Rw.Ch ? 130 : 84);
					while (lb.length() > 1 && Ui.w(lb) > mxw) lb = lb.substring(0, lb.length() - 1);
					Ui.tx(g, lb, r.rx + 10, two ? yy + 8 : yy + (r.h - 9) / 2, 0xFFFFFF, 0.92f);
					r.dr(g, r.rx, yy, r.rw, mx, my, ac);
				}
				yy += r.h + 3;
			}
			g.disableScissor();
			tisc = Math.max(0, Math.min(Math.max(0, tot - (ph - 46)), tisc));
			isc += (tisc - isc) * 0.3f;
		} else {
			inspFor = null;
			pw = 0;
		}
	}

	boolean over(double mx, double my) {
		return pw > 0 && in(mx, my, px0, py0, pw, ph);
	}

	public boolean mouseClicked(MouseButtonEvent e, boolean tw) {
		int bx = bx();
		int by = 11;
		int bw = 430;
		double mx = e.x();
		double my = e.y();
		if (in(mx, my, bx + 76, by, 52, 18)) {
			Cfg.si("hudsnap", snap() ? 0 : 1);
			return true;
		}
		if (in(mx, my, bx + 134, by, 50, 18) && msel.size() >= 2) {
			int id = 1;
			for (Hud.El i : Hud.ELS) id = Math.max(id, grp(i) + 1);
			for (Hud.El i : msel) Cfg.si("grp." + i.id, id);
			return true;
		}
		if (in(mx, my, bx + 190, by, 58, 18)) {
			for (Hud.El i : msel) Cfg.si("grp." + i.id, 0);
			return true;
		}
		if (in(mx, my, bx + 254, by, 62, 18)) {
			for (Hud.El i : Hud.ELS) i.reset();
			return true;
		}
		if (in(mx, my, bx + 320, by, 46, 18)) {
			for (Hud.El i : Hud.ELS) i.classic();
			return true;
		}
		if (in(mx, my, bx + bw - 62, by, 52, 18)) {
			onClose();
			return true;
		}
		if (over(mx, my)) {
			for (Rw.R r : insp) {
				if (in(mx, my, r.rx, r.ry, r.rw, r.h) && my >= py0 + 40 && my < py0 + ph - 6) {
					if (r.ck((int) mx, (int) my)) act = r;
					return true;
				}
			}
			return true;
		}
		Hud.El i = at(mx, my);
		if (i == null) {
			msel.clear();
			prim = null;
			io = false;
			return super.mouseClicked(e, tw);
		}
		if (e.button() == 1) {
			boolean same = io && prim == i;
			msel.clear();
			msel.add(i);
			prim = i;
			io = !same;
			return true;
		}
		if (i != prim) io = false;
		long wh = Minecraft.getInstance().getWindow().handle();
		boolean shift = org.lwjgl.glfw.GLFW.glfwGetKey(wh, org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SHIFT) == org.lwjgl.glfw.GLFW.GLFW_PRESS || org.lwjgl.glfw.GLFW.glfwGetKey(wh, org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT_SHIFT) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
		if (shift) {
			if (!msel.add(i)) msel.remove(i);
		} else if (!msel.contains(i)) {
			msel.clear();
			msel.add(i);
		}
		int g = grp(i);
		if (g > 0) for (Hud.El o : Hud.ELS) if (grp(o) == g) msel.add(o);
		prim = i;
		ox = mx - i.x;
		oy = my - i.y;
		st.clear();
		for (Hud.El o : msel) st.put(o, new float[]{o.x, o.y});
		return true;
	}

	public boolean mouseDragged(MouseButtonEvent e, double dx, double dy) {
		if (act != null) {
			act.dg((int) e.x());
			return true;
		}
		if (prim == null || !msel.contains(prim)) return super.mouseDragged(e, dx, dy);
		float[] s0 = st.get(prim);
		if (s0 == null) return true;
		double ddx = (e.x() - ox) - s0[0];
		double ddy = (e.y() - oy) - s0[1];
		gx = false;
		gy = false;
		for (Hud.El o : msel) {
			float[] s = st.get(o);
			if (s == null) continue;
			int px = (int) Math.round((s[0] + ddx) / Math.max(1, width - o.w) * 100);
			int py = (int) Math.round((s[1] + ddy) / Math.max(1, height - o.h) * 100);
			if (snap() && o == prim) {
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
			o.pos(Math.max(0, Math.min(100, px)), Math.max(0, Math.min(100, py)));
		}
		return true;
	}

	public boolean mouseReleased(MouseButtonEvent e) {
		act = null;
		gx = false;
		gy = false;
		return super.mouseReleased(e);
	}

	public boolean mouseScrolled(double mx, double my, double sx, double sy) {
		if (over(mx, my)) {
			tisc -= (float) sy * 24f;
			return true;
		}
		return super.mouseScrolled(mx, my, sx, sy);
	}

	public void onClose() {
		Cfg.sv();
		Minecraft.getInstance().setScreen(par);
	}

	public boolean isPauseScreen() {
		return false;
	}
}
