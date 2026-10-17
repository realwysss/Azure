package dev.wscl;

import dev.wscl.Rw.*;
import dev.wysl.Thm;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;

public class Mnu extends Screen {
	static final String[] PG = {"Modules", "Styles", "Settings", "Profiles"};
	static final String[] PI = {"modules", "brush", "gear", "profile"};
	static final int SW = 0;

	static class Hit {
		int x;
		int y;
		int w;
		int h;
		int c0;
		int c1;
		Runnable r;
	}

	final Screen par;
	final List<R> setr = new ArrayList<>();
	final List<R> prof = new ArrayList<>();
	final Map<Object, float[]> an = new IdentityHashMap<>();
	final List<Hit> hits = new ArrayList<>();
	public int sel;
	public int cat = -1;
	public Mod mod;
	public int sty = -1;
	String q = "";
	String tipt = "";
	boolean sf;
	float iy = -1;
	float sc;
	float tsc;
	float dsc;
	float tds;
	float da;
	R act;
	Sw stsw;
	int stsi = -1;
	long t0 = Util.getMillis();
	int wx;
	int wy;
	int ww;
	int wh;
	int lmx;
	int lmy;
	float zk = 1f;

	public Mnu(Screen p) {
		super(Component.literal("Azure"));
		par = p;
		Mods.init();
		build();
	}

	void build() {
		Cfg c = Cfg.c;
		setr.add(new Nt("INTERFACE"));
		setr.add(Rw.tg("Azure title screen", () -> c.ttl, v -> c.ttl = v));
		setr.add(Rw.tg("Azure loading screen", () -> c.lod, v -> c.lod = v));
		setr.add(Rw.tg("Azure buttons and sliders", () -> c.btn, v -> c.btn = v).tip("Restyles the buttons of every menu."));
		setr.add(Rw.tg("Azure shortcut on menus", () -> c.tag, v -> c.tag = v).tip("Shows the Azure button on the title and pause screens."));
		setr.add(new Nt("COLOR"));
		setr.add(new Cl("Accent", Thm::get, v -> Thm.set(v & 0xFFFFFF)));
		setr.add(new Ch("Accent motion", new String[]{"Static", "Rainbow", "Pulse"}, () -> c.am, v -> c.am = v).tip("Rainbow and pulse animate the accent everywhere."));
		setr.add(new Bt("Default accent", "Reset", () -> Thm.set(0x8B5CF6)));
		setr.add(new Nt("HUD LOOK"));
		setr.add(new Ch("Panels", new String[]{"Solid", "Gradient", "Outline", "None"}, () -> c.bs, v -> c.bs = v));
		setr.add(Rw.sl("Size", () -> c.sc, v -> c.sc = v, 50, 200, "%"));
		setr.add(Rw.sl("Panel opacity", () -> c.op, v -> c.op = v, 0, 100, "%"));
		setr.add(new Cl("Text color", () -> c.tc, v -> c.tc = v & 0xFFFFFF));
		setr.add(new Nt("AUDIO"));
		setr.add(new Ch("Menu sounds", Snd.ST, () -> c.sfx, v -> {
			c.sfx = v;
			Snd.p("click");
		}).tip("Only plays inside Azure screens."));
		setr.add(Rw.sl("Volume", () -> c.sfv, v -> c.sfv = v, 0, 100, "%"));
		setr.add(new Bt("Preview", "Play", () -> Snd.p("done")));
		prof.add(new Nt("Share your whole setup as a code"));
		prof.add(new Bt("Copy my profile code", "Copy", Prf::copy).tip("Copies a code with your hud, modules and styles to the clipboard."));
		prof.add(new Bt("Paste a profile code", "Paste", Prf::paste).tip("Reads a code from the clipboard and applies it."));
	}

	float an(Object o, boolean on, float sp) {
		float[] f = an.computeIfAbsent(o, k -> new float[]{on ? 1 : 0});
		f[0] += ((on ? 1f : 0f) - f[0]) * sp;
		return f[0];
	}

	int[] sm(int mx, int my) {
		int cx = width / 2;
		int cy = height / 2;
		return new int[]{Math.round(cx + (mx - cx) / zk), Math.round(cy + (my - cy) / zk)};
	}

	void lay() {
		ww = Math.min(width - 20, 800);
		wh = Math.min(height - 20, 450);
		wx = (width - ww) / 2;
		wy = (height - wh) / 2;
	}

	void hit(int x, int y, int w, int h, int c0, int c1, Runnable r) {
		Hit t = new Hit();
		t.x = x;
		t.y = y;
		t.w = w;
		t.h = h;
		t.c0 = c0;
		t.c1 = c1;
		t.r = r;
		hits.add(t);
	}

	String fit(String s, int max) {
		if (Ui.w(s) <= max) return s;
		while (s.length() > 1 && Ui.w(s + "...") > max) s = s.substring(0, s.length() - 1);
		return s + "...";
	}

	List<Mod> list() {
		List<Mod> o = new ArrayList<>();
		String k = q.toLowerCase();
		for (Mod m : Mod.ALL) {
			if (cat >= 0 && m.ct != cat) continue;
			if (!k.isEmpty() && !m.nm.toLowerCase().contains(k) && !m.ds.toLowerCase().contains(k)) continue;
			o.add(m);
		}
		return o;
	}

	boolean over(int mx, int my, int x, int y, int w, int h, int c0, int c1) {
		return mx >= x && mx < x + w && my >= y && my < y + h && my >= c0 && my < c1;
	}

	float stg(int i) {
		return Ui.ez((Util.getMillis() - Math.max(pgt, t0) - 60 - i * 22) / 300f);
	}

	long pgt = Util.getMillis();

	int cnt(int c) {
		int n = 0;
		for (Mod m : Mod.ALL) if (c < 0 || m.ct == c) n++;
		return n;
	}

	void tgl(GuiGraphics g, int x, int y, float o, int ac) {
		if (o > 0.05f) Ui.glow(g, x, y, 28, 12, 6, ac, o * 0.7f);
		Ui.rr(g, x, y, 28, 12, 6, Ui.al(Vis.mix(0x2A3350, ac, o), 1f));
		Ui.rr(g, x + 2 + (int) (o * 16), y + 2, 8, 8, 4, Ui.al(Vis.mix(0xA8B2C8, 0xFFFFFF, o), 1f));
	}

	public void render(GuiGraphics g, int mx0, int my0, float d) {
		lay();
		hits.clear();
		float k = Ui.ez((Util.getMillis() - t0) / 300f);
		zk = 0.9f + 0.1f * Ui.eb((Util.getMillis() - t0) / 380f);
		int[] mm = sm(mx0, my0);
		lmx = mm[0];
		lmy = mm[1];
		int mx = lmx;
		int my = lmy;
		int ac = Hud.ac() & 0xFFFFFF;
		g.fill(0, 0, width, height, Ui.al(0x000000, 0.4f * k));
		var p = g.pose();
		p.pushMatrix();
		p.translate(width / 2f, height / 2f);
		p.scale(zk, zk);
		p.translate(-width / 2f, -height / 2f);
		Ui.glow(g, wx, wy, ww, wh, 10, ac, k * 0.7f);
		Ui.rr(g, wx, wy, ww, wh, 10, Ui.al(0x080C18, 0.93f * k));
		g.fillGradient(wx + 10, wy + 1, wx + ww - 10, wy + 90, Ui.al(ac, 0.1f * k), Ui.al(ac, 0f));
		Ui.ro(g, wx, wy, ww, wh, 10, Ui.al(0xFFFFFF, 0.16f * k));
		g.fill(wx + 12, wy, wx + ww - 12, wy + 1, Ui.al(0xFFFFFF, 0.3f * k));
		side(g, mx, my, ac);
		int x0 = wx + SW + 16;
		int mw = ww - SW - 32;
		if (sel == 0) mods(g, mx, my, ac, x0, mw);
		else if (sel == 1) styles(g, mx, my, ac, x0, mw);
		else if (sel == 2) rows(g, mx, my, ac, x0, mw, "Settings", setr);
		else rows(g, mx, my, ac, x0, mw, "Profiles", prof);
		if (!tipt.isEmpty()) {
			int tw = Ui.w(tipt) + 20;
			int tx = wx + SW + (ww - SW) / 2;
			Ui.rr(g, tx - tw / 2, wy + wh - 30, tw, 18, 9, Ui.al(0x000000, 0.85f));
			Ui.ro(g, tx - tw / 2, wy + wh - 30, tw, 18, 9, Ui.al(0xFFFFFF, 0.15f));
			Ui.cx(g, tipt, tx, wy + wh - 25, 0xFFFFFF, 0.9f);
		}
		tipt = "";
		p.popMatrix();
	}

	float inx = -1;
	float inw;

	void side(GuiGraphics g, int mx, int my, int ac) {
		boolean wide = ww >= 600;
		int ty = wy + 10;
		int nx = wx + 16;
		float tt = (Util.getMillis() % 1000000L) / 1000f;
		if (ww >= 520) {
			String az = "Azure";
			float xx = nx;
			for (int i = 0; i < az.length(); i++) {
				String c = String.valueOf(az.charAt(i));
				float sw = 0.5f + 0.5f * (float) Math.sin(tt * 2.6f - i * 0.85f);
				Ui.ts(g, c, xx, wy + 14 + (float) Math.sin(tt * 2.2f + i * 0.9f) * 0.8f, 14f, Vis.mix(0xFFFFFF, Vis.mix(ac, 0xFFFFFF, 0.35f), sw), 1f);
				xx += Tf.wf(c, 14f);
			}
			nx += 58;
		}
		int[] pw = new int[PG.length];
		int tot = 0;
		for (int i = 0; i < PG.length; i++) {
			pw[i] = (wide || i == sel) ? 30 + Ui.w(PG[i]) : 28;
			tot += pw[i] + 3;
		}
		Ui.rr(g, nx - 3, ty - 2, tot + 3, 28, 12, Ui.al(0x000000, 0.3f));
		Ui.ro(g, nx - 3, ty - 2, tot + 3, 28, 12, Ui.al(0xFFFFFF, 0.08f));
		float px = nx;
		float sx = 0;
		for (int i = 0; i < PG.length; i++) {
			if (i == sel) sx = px;
			px += pw[i] + 3;
		}
		if (inx < 0) {
			inx = sx;
			inw = pw[sel];
		}
		inx += (sx - inx) * 0.3f;
		inw += (pw[sel] - inw) * 0.3f;
		Ui.glow(g, Math.round(inx), ty, Math.round(inw), 24, 10, ac, 0.5f);
		Ui.rr(g, Math.round(inx), ty, Math.round(inw), 24, 10, Ui.al(Vis.mix(0x14102A, ac, 0.55f), 0.95f));
		px = nx;
		for (int i = 0; i < PG.length; i++) {
			int ii = i;
			int xi = Math.round(px);
			boolean ov = over(mx, my, xi, ty, pw[i], 24, 0, 9999);
			float a = an(PG[i], ov || i == sel, 0.3f);
			Ic.dr(g, PI[i], xi + 14, ty + 12, 13, Ui.al(0xFFFFFF, 0.5f + 0.5f * a));
			if (wide || i == sel) Ui.tx(g, PG[i], xi + 26, ty + 8, 0xFFFFFF, i == sel ? 1f : 0.5f + 0.3f * a);
			hit(xi, ty, pw[i], 24, 0, 9999, () -> {
				if (sel != ii) {
					sel = ii;
					tsc = 0;
					sc = 0;
					pgt = Util.getMillis();
				}
			});
			px += pw[i] + 3;
		}
		int ew = wide ? 78 : 26;
		int ex = wx + ww - 16 - ew;
		boolean ov = over(mx, my, ex, ty, ew, 24, 0, 9999);
		float ea = an("edit", ov, 0.3f);
		Ui.rr(g, ex, ty, ew, 24, 10, Ui.al(ac, 0.14f + 0.2f * ea));
		Ui.ro(g, ex, ty, ew, 24, 10, Ui.al(ac, 0.5f + 0.4f * ea));
		Ic.dr(g, "edit", ex + 13, ty + 12, 13, Ui.al(0xFFFFFF, 0.95f));
		if (wide) Ui.tx(g, "Edit HUD", ex + 26, ty + 8, 0xFFFFFF, 1f);
		hit(ex, ty, ew, 24, 0, 9999, () -> minecraft.setScreen(new HudEd(this)));
		String on = Onl.n < 0 ? "-" : String.valueOf(Onl.n);
		String ot = wide ? on + " online" : on;
		int ow = 30 + Ui.w(ot);
		int ox = ex - 8 - ow;
		Ui.rr(g, ox, ty, ow, 24, 10, Ui.al(0x000000, 0.3f));
		Ui.ro(g, ox, ty, ow, 24, 10, Ui.al(0xFFFFFF, 0.1f));
		Ui.rr(g, ox + 10, ty + 9, 6, 6, 3, Ui.al(Onl.n < 0 ? 0x888888 : 0x5CE08A, 1f));
		Ui.tx(g, ot, ox + 22, ty + 8, 0xFFFFFF, 0.9f);
		g.fill(wx + 14, wy + 42, wx + ww - 14, wy + 43, Ui.al(0xFFFFFF, 0.08f));
	}

	void mods(GuiGraphics g, int mx, int my, int ac, int x0, int mw) {
		List<Mod> ms = list();
		da += ((mod != null ? 1f : 0f) - da) * 0.28f;
		int dw = Math.min(236, mw * 62 / 100);
		int gw = mw - Math.round((dw + 10) * da);
		int top = wy + 54;
		int sbw = Math.min(210, gw - 8);
		Ui.rr(g, x0, top, sbw, 24, 8, Ui.al(0xFFFFFF, sf ? 0.1f : 0.06f));
		Ui.ro(g, x0, top, sbw, 24, 8, Ui.al(sf ? ac : 0xFFFFFF, sf ? 0.9f : 0.12f));
		Ic.draw(g, "search", x0 + 14, top + 12, Ui.al(0xFFFFFF, 0.5f));
		boolean caret = sf && (Util.getMillis() / 500) % 2 == 0;
		if (q.isEmpty() && !sf) Ui.tx(g, "Search modules", x0 + 28, top + 8, 0xFFFFFF, 0.35f);
		else Ui.tx(g, fit(q, sbw - 40) + (caret ? "|" : ""), x0 + 28, top + 8, 0xFFFFFF, 1f);
		hit(x0, top, sbw, 24, 0, 9999, () -> sf = true);
		String[] pn = {"All", "PvP", "HUD", "Visuals", "Player", "Wysss"};
		int px = x0 + sbw + 12;
		int avail = gw - sbw - 12;
		int need = 0;
		for (int i = 0; i < pn.length; i++) need += Ui.w(pn[i] + " " + cnt(i - 1)) + 21;
		boolean num = need <= avail;
		for (int i = 0; i < pn.length; i++) {
			String s = num ? pn[i] + " " + cnt(i - 1) : pn[i];
			int pw = Ui.w(s) + 16;
			if (px + pw > x0 + gw) break;
			int ii = i - 1;
			boolean on = cat == ii;
			boolean ov = over(mx, my, px, top + 2, pw, 20, 0, 9999);
			float a = an("p" + i, on, 0.3f);
			Ui.rr(g, px, top + 2, pw, 20, 10, Ui.al(on ? ac : 0xFFFFFF, 0.06f + 0.2f * a + (ov ? 0.05f : 0f)));
			Ui.ro(g, px, top + 2, pw, 20, 10, Ui.al(on ? ac : 0xFFFFFF, 0.12f + 0.7f * a));
			Ui.tx(g, s, px + 8, top + 8, 0xFFFFFF, on ? 1f : 0.6f);
			hit(px, top + 2, pw, 20, 0, 9999, () -> {
				cat = ii;
				tsc = 0;
				sc = 0;
			});
			px += pw + 5;
		}
		int vy0 = top + 36;
		int vy1 = wy + wh - 14;
		int cols = gw >= 330 ? 2 : 1;
		int cw = (gw - (cols - 1) * 8) / cols;
		int chh = 46;
		int tot = ((ms.size() + cols - 1) / cols) * (chh + 8);
		tsc = Math.max(0, Math.min(Math.max(0, tot - (vy1 - vy0)), tsc));
		sc += (tsc - sc) * 0.3f;
		g.enableScissor(x0 - 4, vy0, x0 + gw + 4, vy1);
		for (int i = 0; i < ms.size(); i++) {
			Mod m = ms.get(i);
			int cx = x0 + (i % cols) * (cw + 8);
			int cy = vy0 - Math.round(sc) + (i / cols) * (chh + 8);
			if (cy + chh < vy0 || cy > vy1) continue;
			float kk = stg(i);
			Ui.GA = kk;
			cy += Math.round((1f - kk) * 10);
			boolean ov = over(mx, my, cx, cy, cw, chh, vy0, vy1);
			boolean on = m.on.getAsBoolean() && m.ct != 4;
			boolean se = m == mod;
			float h = an(m, ov || se, 0.28f);
			float o = an(m.id + "o", on, 0.3f);
			Ui.rr(g, cx, cy, cw, chh, 9, Ui.al(0xFFFFFF, 0.045f + 0.045f * h));
			if (on) g.fill(cx + 8, cy + 1, cx + cw - 8, cy + 2, Ui.al(ac, 0.6f));
			Ui.ro(g, cx, cy, cw, chh, 9, Ui.al(se || on ? ac : 0xFFFFFF, se ? 0.95f : on ? 0.4f : 0.1f + 0.12f * h));
			Ui.rr(g, cx + 8, cy + 9, 28, 28, 8, Ui.al(on ? ac : 0xFFFFFF, on ? 0.22f : 0.07f));
			Ic.draw(g, m.ic, cx + 22, cy + 23, Ui.al(0xFFFFFF, on || m.ct == 4 ? 0.95f : 0.6f));
			boolean ds = mod == null;
			int tw = cw - 44 - (m.ct == 4 ? 18 : 48);
			Ui.tx(g, fit(m.nm, tw), cx + 44, ds ? cy + 11 : cy + 17, 0xFFFFFF, 1f);
			if (ds) Ui.tx(g, fit(m.ds, tw), cx + 44, cy + 24, 0xFFFFFF, 0.45f);
			hit(cx, cy, cw, chh, vy0, vy1, () -> {
				if (m.ct == 4) m.go.run();
				else {
					mod = mod == m ? null : m;
					dsc = 0;
					tds = 0;
				}
			});
			if (m.ct == 4) Ic.draw(g, "chev", cx + cw - 16, cy + 23, Ui.al(0xFFFFFF, 0.4f));
			else {
				tgl(g, cx + cw - 38, cy + 17, o, ac);
				hit(cx + cw - 42, cy, 42, chh, vy0, vy1, () -> m.set.accept(!m.on.getAsBoolean()));
			}
			if (ov) tipt = m.ds;
			Ui.GA = 1f;
		}
		g.disableScissor();
		if (ms.isEmpty()) {
			int ex = x0 + gw / 2;
			int ey = vy0 + Math.max(24, (vy1 - vy0) / 2 - 50);
			Ui.glow(g, ex - 26, ey, 52, 52, 16, ac, 0.5f);
			Ui.rr(g, ex - 26, ey, 52, 52, 16, Ui.al(0xFFFFFF, 0.06f));
			Ui.ro(g, ex - 26, ey, 52, 52, 16, Ui.al(ac, 0.6f));
			Ic.draw(g, "grid", ex, ey + 26, Ui.al(0xFFFFFF, 0.9f));
			String a1 = Mod.ALL.isEmpty() ? "No modules yet" : "No modules found";
			String a2 = Mod.ALL.isEmpty() ? "Modules you add will show up here" : "Try another search";
			Ui.ts(g, a1, ex - Ui.w(a1, 12f) / 2f, ey + 64, 12f, 0xFFFFFF, 1f);
			Ui.ts(g, a2, ex - Ui.w(a2, 9f) / 2f, ey + 82, 9f, 0xFFFFFF, 0.45f);
		}
		if (da > 0.02f) detail(g, mx, my, ac, x0 + gw + 10, top, dw, vy1);
	}

	void detail(GuiGraphics g, int mx, int my, int ac, int x, int y, int w, int y1) {
		Mod m = mod;
		if (m == null) return;
		Ui.rr(g, x, y, w, y1 - y, 10, Ui.al(0xFFFFFF, 0.04f));
		Ui.ro(g, x, y, w, y1 - y, 10, Ui.al(0xFFFFFF, 0.12f));
		boolean on = m.on.getAsBoolean();
		float o = an(m.id + "d", on, 0.3f);
		Ui.rr(g, x + 10, y + 10, 28, 28, 8, Ui.al(on ? ac : 0xFFFFFF, on ? 0.25f : 0.08f));
		Ic.draw(g, m.ic, x + 24, y + 24, Ui.al(0xFFFFFF, 0.95f));
		Ui.tx(g, fit(m.nm, w - 100), x + 46, y + 13, 0xFFFFFF, 1f);
		Ui.tx(g, Mod.CT[m.ct], x + 46, y + 26, ac, 0.8f);
		tgl(g, x + w - 40, y + 17, o, ac);
		hit(x + w - 46, y + 8, 40, 30, 0, 9999, () -> m.set.accept(!m.on.getAsBoolean()));
		Ui.tx(g, fit(m.ds, w - 20), x + 12, y + 44, 0xFFFFFF, 0.45f);
		int ry0 = y + 62;
		g.enableScissor(x, ry0, x + w, y1 - 2);
		int yy = ry0 - Math.round(dsc);
		int tot = 0;
		for (R r : m.st) {
			r.rx = x + 6;
			r.ry = yy;
			r.rw = w - 12;
			tot += r.h + 4;
			if (yy + r.h > ry0 && yy < y1) {
				boolean ov = over(mx, my, r.rx, yy, r.rw, r.h, ry0, y1);
				r.hv += ((ov ? 1f : 0f) - r.hv) * 0.3f;
				if (ov) tipt = r.tp;
				rowdraw(g, r, mx, my, ac);
				if (!(r instanceof Nt) && !(r instanceof Pv)) {
					final R rr = r;
					hit(r.rx, yy, r.rw, r.h, ry0, y1, () -> {
						if (rr.ck(lmx, lmy)) act = rr;
					});
				}
			}
			yy += r.h + 4;
		}
		if (m.st.isEmpty()) Ui.tx(g, "No settings for this module", x + 12, ry0 + 6, 0xFFFFFF, 0.35f);
		g.disableScissor();
		tds = Math.max(0, Math.min(Math.max(0, tot - (y1 - ry0)), tds));
		dsc += (tds - dsc) * 0.3f;
	}

	void rowdraw(GuiGraphics g, R r, int mx, int my, int ac) {
		if (r instanceof Nt) {
			Ui.tx(g, r.lb, r.rx + 4, r.ry + 6, 0xFFFFFF, 0.4f);
			return;
		}
		Ui.rr(g, r.rx, r.ry, r.rw, r.h, 7, Ui.al(0xFFFFFF, 0.035f + 0.05f * r.hv));
		if (r.hv > 0.02f) Ui.ro(g, r.rx, r.ry, r.rw, r.h, 7, Ui.al(ac, 0.5f * r.hv));
		boolean two = r instanceof Cl || r instanceof Sw || r instanceof Rw.Ed;
		if (!(r instanceof Pv)) Ui.tx(g, fit(r.lb, r.rw - (two ? 20 : 100)), r.rx + 10, two ? r.ry + 8 : r.ry + (r.h - 9) / 2, 0xFFFFFF, 0.92f);
		r.dr(g, r.rx, r.ry, r.rw, mx, my, ac);
	}

	void styles(GuiGraphics g, int mx, int my, int ac, int x0, int mw) {
		int top = wy + 54;
		Ui.tx(g, "Styles", x0, top + 2, 0xFFFFFF, 1f);
		int cy = wy + wh / 2;
		String a = "Coming soon";
		Ui.ts(g, a, x0 + (mw - Ui.w(a, 16f)) / 2f, cy - 18, 16f, 0xFFFFFF, 1f);
		String b = "Wysss Styles are being rebuilt";
		Ui.ts(g, b, x0 + (mw - Ui.w(b, 9f)) / 2f, cy + 6, 9f, 0xFFFFFF, 0.45f);
	}

	void rows(GuiGraphics g, int mx, int my, int ac, int x0, int mw, String title, List<R> rs) {
		int top = wy + 54;
		Ui.tx(g, title, x0, top + 2, 0xFFFFFF, 1f);
		int vy0 = top + 28;
		int vy1 = wy + wh - 14;
		int w = Math.min(mw, 560);
		int tot = 0;
		for (R r : rs) tot += r.h + 4;
		tsc = Math.max(0, Math.min(Math.max(0, tot - (vy1 - vy0)), tsc));
		sc += (tsc - sc) * 0.3f;
		g.enableScissor(x0 - 4, vy0, x0 + w + 4, vy1);
		int yy = vy0 - Math.round(sc);
		for (R r : rs) {
			r.rx = x0;
			r.ry = yy;
			r.rw = w;
			if (yy + r.h > vy0 && yy < vy1) {
				boolean ov = over(mx, my, x0, yy, w, r.h, vy0, vy1);
				r.hv += ((ov ? 1f : 0f) - r.hv) * 0.3f;
				if (ov) tipt = r.tp;
				rowdraw(g, r, mx, my, ac);
				if (!(r instanceof Nt)) {
					final R rr = r;
					hit(x0, yy, w, r.h, vy0, vy1, () -> {
						if (rr.ck(lmx, lmy)) act = rr;
					});
				}
			}
			yy += r.h + 4;
		}
		g.disableScissor();
	}

	public boolean mouseClicked(MouseButtonEvent e, boolean dbl) {
		int[] mm = sm((int) e.x(), (int) e.y());
		lmx = mm[0];
		lmy = mm[1];
		sf = false;
		Rw.Ed.foc = null;
		for (int i = hits.size() - 1; i >= 0; i--) {
			Hit h = hits.get(i);
			if (lmx >= h.x && lmx < h.x + h.w && lmy >= h.y && lmy < h.y + h.h && lmy >= h.c0 && lmy < h.c1) {
				if (h.r != null) h.r.run();
				Snd.p("click");
				return true;
			}
		}
		return super.mouseClicked(e, dbl);
	}

	public boolean mouseDragged(MouseButtonEvent e, double dx, double dy) {
		if (act == null) return super.mouseDragged(e, dx, dy);
		int[] mm = sm((int) e.x(), (int) e.y());
		act.dg(mm[0]);
		return true;
	}

	public boolean mouseReleased(MouseButtonEvent e) {
		act = null;
		return super.mouseReleased(e);
	}

	public boolean mouseScrolled(double mx, double my, double sx, double sy) {
		int[] mm = sm((int) mx, (int) my);
		boolean right = mm[0] > wx + ww - 260;
		if (sel == 0 && mod != null && right) tds -= (float) sy * 26f;
		else if (sel == 1 && sty >= 0 && right) return true;
		else tsc -= (float) sy * 30f;
		return true;
	}

	public boolean charTyped(CharacterEvent e) {
		if (Rw.Ed.foc != null) {
			String s = e.codepointAsString();
			if (!s.isEmpty() && s.charAt(0) >= 32) Rw.Ed.foc.ch(s);
			return true;
		}
		if (sf && sel == 0) {
			String s = e.codepointAsString();
			if (q.length() < 24 && !s.isEmpty() && s.charAt(0) >= 32) {
				q += s;
				tsc = 0;
				sc = 0;
			}
			return true;
		}
		return super.charTyped(e);
	}

	public boolean keyPressed(KeyEvent e) {
		if (Rw.Ed.foc != null) {
			if (e.key() == org.lwjgl.glfw.GLFW.GLFW_KEY_BACKSPACE) Rw.Ed.foc.bk();
			else if (e.key() == org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE || e.key() == org.lwjgl.glfw.GLFW.GLFW_KEY_ENTER) Rw.Ed.foc = null;
			return true;
		}
		if (Wc.key != null && Wc.key.matches(e) && !sf) {
			onClose();
			return true;
		}
		if (sf) {
			if (e.key() == org.lwjgl.glfw.GLFW.GLFW_KEY_BACKSPACE && !q.isEmpty()) {
				q = q.substring(0, q.length() - 1);
				return true;
			}
			if (e.key() == org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE || e.key() == org.lwjgl.glfw.GLFW.GLFW_KEY_ENTER) {
				sf = false;
				return true;
			}
		}
		if (e.key() == org.lwjgl.glfw.GLFW.GLFW_KEY_TAB) {
			sel = (sel + 1) % PG.length;
			tsc = 0;
			sc = 0;
			return true;
		}
		return super.keyPressed(e);
	}

	public void onClose() {
		Cfg.sv();
		minecraft.setScreen(par);
	}

	public void removed() {
		Cfg.sv();
	}

	public boolean isPauseScreen() {
		return false;
	}
}
