package dev.wscl;

import dev.klfx.Clr;
import dev.klfx.Fx;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Util;

public class KlGui extends Screen {
	final Screen par;
	final List<Rw.R> rows = new ArrayList<>();
	static class Pt {
		float x;
		float y;
		float vx;
		float vy;
		float life;
		float max;
		int col;
		float sz;
	}

	final List<Pt> ps = new ArrayList<>();
	long t0 = Util.getMillis();
	long boom;
	Rw.R act;
	float sc;
	float tsc;
	int wx;
	int wy;
	int ww;
	int wh;
	int lw;
	int mob;
	final float[] an = new float[3];
	final java.util.Random rnd = new java.util.Random();

	public KlGui(Screen p) {
		super(Component.literal("Kill Effects"));
		par = p;
		mob = rnd.nextInt(2000);
		var c = dev.klfx.Cfg.c;
		rows.add(new Rw.Nt("EFFECTS"));
		rows.add(Rw.tg("Kill effects on", () -> c.on, v -> c.on = v));
		for (Fx x : Fx.values()) rows.add(Rw.tg(x.toString(), () -> c.fxs.contains(x), v -> {
			if (v) c.fxs.add(x);
			else c.fxs.remove(x);
		}));
		rows.add(Rw.tg("Random effect", () -> c.rnd, v -> c.rnd = v).tip("Picks one random effect on every kill instead of all the ticked ones."));
		rows.add(new Rw.Nt("TARGETS"));
		rows.add(Rw.tg("Effects on players", () -> c.plrs, v -> c.plrs = v));
		rows.add(Rw.tg("Effects on mobs", () -> c.mobs, v -> c.mobs = v));
		rows.add(new Rw.Nt("FIREWORK"));
		Clr[] cl = Clr.values();
		String[] cn = new String[cl.length];
		for (int i = 0; i < cl.length; i++) cn[i] = cl[i].toString();
		rows.add(new Rw.Ch("Color", cn, () -> c.clr.ordinal(), v -> c.clr = cl[v]));
		rows.add(new Rw.Cl("Custom color", () -> c.cus, v -> c.cus = v & 0xFFFFFF).tip("Used when the color above is Custom."));
		rows.add(new Rw.Nt("SOUND"));
		rows.add(Rw.tg("Kill sound", () -> c.snd, v -> c.snd = v));
		dev.klfx.Snd[] sn = dev.klfx.Snd.values();
		String[] sm = new String[sn.length];
		for (int i = 0; i < sn.length; i++) sm[i] = sn[i].toString();
		rows.add(new Rw.Ch("Sound", sm, () -> c.sndT.ordinal(), v -> {
			c.sndT = sn[v];
			sound();
		}));
		rows.add(Rw.sl("Volume", () -> c.vol, v -> c.vol = v, 0, 100, "%"));
	}

	void sound() {
		var c = dev.klfx.Cfg.c;
		SoundEvent s = switch (c.sndT) {
			case LVLUP -> SoundEvents.PLAYER_LEVELUP;
			case ORB -> SoundEvents.EXPERIENCE_ORB_PICKUP;
			case TOTM -> SoundEvents.TOTEM_USE;
			case THNDR -> SoundEvents.LIGHTNING_BOLT_THUNDER;
		};
		minecraft.getSoundManager().play(SimpleSoundInstance.forUI(s, 1f, c.vol / 100f));
	}

	void test(float cx, float cy) {
		var c = dev.klfx.Cfg.c;
		boom = Util.getMillis();
		List<Fx> fs = new ArrayList<>();
		if (c.rnd) fs.add(Fx.values()[rnd.nextInt(Fx.values().length)]);
		else for (Fx f : Fx.values()) if (c.fxs.contains(f)) fs.add(f);
		for (Fx f : fs) {
			int n = 36;
			for (int i = 0; i < n; i++) {
				Pt p = new Pt();
				p.x = cx + (rnd.nextFloat() - .5f) * 8;
				p.y = cy + (rnd.nextFloat() - .5f) * 8;
				p.max = 0.8f + rnd.nextFloat() * 0.5f;
				p.life = p.max;
				p.sz = 2f;
				float a = rnd.nextFloat() * 6.2832f;
				float s = 40f + rnd.nextFloat() * 70f;
				switch (f) {
					case FRWRK -> {
						p.vx = (float) Math.cos(a) * s * 1.2f;
						p.vy = (float) Math.sin(a) * s * 1.2f;
						p.col = c.clr == Clr.CSTM ? Rw.cv(c.cus) : colOf(c.clr);
						p.sz = 2.5f;
					}
					case TOTM -> {
						p.vx = (float) Math.cos(a) * s * 0.7f;
						p.vy = -s * (0.5f + rnd.nextFloat());
						p.col = rnd.nextBoolean() ? 0x4CFF5C : 0xFFE24C;
					}
					case BLD -> {
						p.vx = (float) Math.cos(a) * s * 0.6f;
						p.vy = (float) Math.sin(a) * s * 0.6f + 20f;
						p.col = 0xB01010;
						p.sz = 3f;
					}
					case SOUL -> {
						p.vx = (rnd.nextFloat() - .5f) * 20f;
						p.vy = -40f - rnd.nextFloat() * 50f;
						p.col = 0x4CD9FF;
						p.sz = 3f;
					}
					default -> {
						p.life = 0f;
					}
				}
				if (p.life > 0f) ps.add(p);
			}
		}
		if (c.snd) sound();
	}

	static int colOf(Clr c) {
		return switch (c) {
			case RED -> 0xE02020;
			case ORNG -> 0xFF8000;
			case YLW -> 0xFFE020;
			case LIME -> 0x80FF20;
			case GRN -> 0x20B020;
			case CYAN -> 0x20E0E0;
			case BLUE -> 0x2060FF;
			case PRPL -> 0x9020E0;
			case PINK -> 0xFF60C0;
			default -> 0xFFFFFF;
		};
	}

	protected void init() {
		ww = Math.min(width - 24, 680);
		wh = Math.min(height - 24, 360);
		wx = (width - ww) / 2;
		wy = (height - wh) / 2;
		lw = Math.min(330, ww - 220);
	}

	public void renderBackground(GuiGraphics g, int mx, int my, float d) {
		Vis.bg(g, (Util.getMillis() % 1000000L) / 1000f, 1f);
	}

	boolean in(double mx, double my, int x, int y, int w, int h) {
		return mx >= x && mx < x + w && my >= y && my < y + h;
	}

	public void render(GuiGraphics g, int mx, int my, float d) {
		renderBackground(g, mx, my, d);
		int ac = Hud.ac() & 0xFFFFFF;
		float k = Ui.ez((Util.getMillis() - t0) / 300f);
		Ui.GA = k;
		Ui.rr(g, wx, wy, ww, wh, 12, Ui.al(0x090714, 0.95f));
		Ui.ro(g, wx, wy, ww, wh, 12, Ui.al(0xFFFFFF, 0.14f));
		Ui.ts(g, "Kill Effects", wx + 16, wy + 14, 14f, 0xFFFFFF, 1f);
		int ry0 = wy + 40;
		int ry1 = wy + wh - 14;
		int x = wx + 12;
		int tot = 0;
		for (Rw.R r : rows) tot += r.h + 3;
		tsc = Math.max(0, Math.min(Math.max(0, tot - (ry1 - ry0)), tsc));
		sc += (tsc - sc) * 0.3f;
		g.enableScissor(x, ry0, x + lw, ry1);
		int yy = ry0 - Math.round(sc);
		for (Rw.R r : rows) {
			r.rx = x;
			r.ry = yy;
			r.rw = lw - 8;
			if (yy + r.h > ry0 && yy < ry1) {
				boolean ov = in(mx, my, x, yy, r.rw, r.h) && my >= ry0 && my < ry1;
				r.hv += ((ov ? 1f : 0f) - r.hv) * 0.3f;
				if (r instanceof Rw.Nt) Ui.tx(g, r.lb, x + 4, yy + 8, ac, 0.9f);
				else {
					Ui.rr(g, x, yy, r.rw, r.h, 8, Ui.al(0xFFFFFF, 0.035f + 0.05f * r.hv));
					boolean two = r instanceof Rw.Cl;
					String lb = r.lb;
					int mxw = r.rw - (two ? 20 : r instanceof Rw.Sl sl2 ? sl2.tw() + 54 : r instanceof Rw.Ch ? 150 : 84);
					while (lb.length() > 1 && Ui.w(lb) > mxw) lb = lb.substring(0, lb.length() - 1);
					Ui.tx(g, lb, x + 10, two ? yy + 8 : yy + (r.h - 9) / 2, 0xFFFFFF, 0.92f);
					r.dr(g, x, yy, r.rw, mx, my, ac);
				}
			}
			yy += r.h + 3;
		}
		g.disableScissor();
		int px = wx + lw + 24;
		int pw = wx + ww - 14 - px;
		int ph = wh - 100;
		int py = wy + 46;
		Ui.rr(g, px, py, pw, ph, 12, Ui.al(0xFFFFFF, 0.04f));
		Ui.ro(g, px, py, pw, ph, 12, Ui.al(0xFFFFFF, 0.1f));
		Ui.tx(g, "Preview", px + 10, py + 9, 0xFFFFFF, 0.5f);
		float age = (Util.getMillis() - t0) / 50f;
		String kind = mob % 2 == 0 ? "axolotl" : "parrot";
		float cx = px + pw / 2f;
		float cy = py + ph * 0.55f;
		float yaw = (float) Math.PI + (float) Math.sin(age * 0.05f) * 0.6f;
		float sh = Math.min(ph - 40, pw - 20);
		Prv.ent(g, kind, mob / 2, cx - sh / 2f, cy - sh / 2f, sh, sh, yaw, age);
		long now = Util.getMillis();
		for (int i = ps.size() - 1; i >= 0; i--) {
			Pt p = ps.get(i);
			p.life -= 0.016f;
			if (p.life <= 0f) {
				ps.remove(i);
				continue;
			}
			p.x += p.vx * 0.016f;
			p.y += p.vy * 0.016f;
			p.vy += 60f * 0.016f * (p.col == 0x4CD9FF || p.col == 0x4CFF5C || p.col == 0xFFE24C ? -0.4f : 1f);
			float a = Math.min(1f, p.life / p.max * 1.6f);
			g.fill((int) p.x, (int) p.y, (int) (p.x + p.sz), (int) (p.y + p.sz), Ui.al(p.col, a));
		}
		if (dev.klfx.Cfg.c.fxs.contains(Fx.LGHTNG) && now - boom < 260 && boom != 0) {
			float a = 1f - (now - boom) / 260f;
			g.fill(px + 2, py + 2, px + pw - 2, py + ph - 2, Ui.al(0xFFFFFF, 0.25f * a));
			int bx = (int) cx + 6;
			for (int i = 0; i < 7; i++) {
				int nx = bx + (int) ((rnd.nextFloat() - .5f) * 14 * (i % 2 == 0 ? 1 : 0.4f));
				g.fill(Math.min(bx, nx), py + 6 + i * (ph / 8), Math.max(bx, nx) + 2, py + 6 + (i + 1) * (ph / 8), Ui.al(0xFFF6C0, a));
				bx = nx;
			}
		}
		btn(g, 0, px + 10, py + ph + 10, pw / 2 - 14, "Test effect", true, mx, my, ac);
		btn(g, 1, px + pw / 2 + 4, py + ph + 10, pw / 2 - 14, "New mob", false, mx, my, ac);
		btn(g, 2, wx + ww - 70, wy + 12, 56, "Done", true, mx, my, ac);
		Ui.GA = 1f;
		boolean tgOn = false;
		for (Rw.R r : rows) if (in(mx, my, r.rx, r.ry, r.rw, r.h) && r.tp != null && !r.tp.isEmpty()) {
			String t = r.tp;
			int tw = Ui.w(t) + 20;
			Ui.rr(g, (width - tw) / 2, wy + wh - 6, tw, 18, 9, Ui.al(0x000000, 0.9f));
			Ui.cx(g, t, width / 2, wy + wh - 1, 0xFFFFFF, 0.9f);
			tgOn = true;
		}
	}

	void btn(GuiGraphics g, int i, int x, int y, int w, String t, boolean pri, int mx, int my, int ac) {
		boolean ov = in(mx, my, x, y, w, 20);
		an[i] += ((ov ? 1f : 0f) - an[i]) * 0.3f;
		float a = an[i];
		Ui.rr(g, x, y, w, 20, 10, Ui.al(pri ? ac : 0xFFFFFF, pri ? 0.5f + 0.35f * a : 0.07f + 0.1f * a));
		Ui.ro(g, x, y, w, 20, 10, Ui.al(pri || a > 0.05f ? ac : 0xFFFFFF, pri ? 0.9f : 0.15f + 0.7f * a));
		Ui.ts(g, t, x + (w - Ui.w(t, 9f)) / 2f, y + 10 - 4f, 9f, 0xFFFFFF, 1f);
	}

	public boolean mouseClicked(MouseButtonEvent e, boolean dbl) {
		double mx = e.x();
		double my = e.y();
		int px = wx + lw + 24;
		int pw = wx + ww - 14 - px;
		int ph = wh - 100;
		int py = wy + 46;
		if (in(mx, my, px + 10, py + ph + 10, pw / 2 - 14, 20)) {
			Snd.p("click");
			test(px + pw / 2f, py + ph * 0.5f);
			return true;
		}
		if (in(mx, my, px + pw / 2 + 4, py + ph + 10, pw / 2 - 14, 20)) {
			Snd.p("click");
			mob = rnd.nextInt(2000);
			return true;
		}
		if (in(mx, my, wx + ww - 70, wy + 12, 56, 20)) {
			onClose();
			return true;
		}
		for (Rw.R r : rows) {
			if (!(r instanceof Rw.Nt) && in(mx, my, r.rx, r.ry, r.rw, r.h) && my >= wy + 40 && my < wy + wh - 14) {
				if (r.ck((int) mx, (int) my)) act = r;
				return true;
			}
		}
		return super.mouseClicked(e, dbl);
	}

	public boolean mouseDragged(MouseButtonEvent e, double dx, double dy) {
		if (act != null) {
			act.dg((int) e.x());
			return true;
		}
		return super.mouseDragged(e, dx, dy);
	}

	public boolean mouseReleased(MouseButtonEvent e) {
		act = null;
		return super.mouseReleased(e);
	}

	public boolean mouseScrolled(double mx, double my, double sx, double sy) {
		tsc -= (float) sy * 28f;
		return true;
	}

	public void onClose() {
		dev.klfx.Cfg.sv();
		minecraft.setScreen(par);
	}
}
