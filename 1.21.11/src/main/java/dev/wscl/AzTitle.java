package dev.wscl;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlayerSkinWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.options.LanguageSelectScreen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;

public class AzTitle extends Screen {
	static class B {
		String lb;
		String ic;
		Runnable r;
		int x;
		int y;
		int w;
		int h;
	}

	static class Ht {
		int x;
		int y;
		int w;
		int h;
		Runnable r;
		boolean go;
	}

	static final String[] EN = {"Take the L", "Floss", "Dab", "Reanimated", "Fresh", "Worm", "Wiggle", "Jubilation", "Electro"};
	final List<B> bs = new ArrayList<>();
	final List<Ht> hs = new ArrayList<>();
	final Map<Object, float[]> an = new IdentityHashMap<>();
	long t0 = Util.getMillis();
	boolean ini;
	boolean mods;
	PlayerSkinWidget skin;
	Skn skn;
	java.util.function.Supplier<net.minecraft.world.entity.player.PlayerSkin> sk;
	float pyy = -1;
	long lt = Util.getMillis();
	long ck;
	Object cko;
	Runnable pend;
	boolean emo;
	boolean lun;
	long bwt;
	long dpt;
	int lx;
	int cy;
	int gap;
	int bot;
	int ax;

	public AzTitle() {
		super(Component.literal("Azure"));
	}

	protected void init() {
		long now = Util.getMillis();
		t0 = ini ? now - 5000L : now;
		ini = true;
		bs.clear();
		pend = null;
		ck = 0;
		mods = false;
		bot = height - 50;
		int sh = Math.max(60, Math.min(Math.round(height * 0.52f), bot - 74 - 6 - 30));
		sk = minecraft.getSkinManager().createLookup(minecraft.getGameProfile(), false);
		skn = new Skn(Math.round(sh * 0.7f), sh, 0, 0);
		skin = skn.w;
		int sw = Math.round(sh * 0.7f);
		ax = width - 12 - 188;
		int cxs = ax + 94;
		skin.setPosition(cxs - sw / 2, bot - 74 - 6 - sh);
		try {
			Class.forName("com.terraformersmc.modmenu.gui.ModsScreen");
			mods = true;
		} catch (Throwable t) {
		}
		add("Singleplayer", "solo", () -> minecraft.setScreen(new SelectWorldScreen(this)));
		add("Multiplayer", "multi", () -> minecraft.setScreen(new JoinMultiplayerScreen(this)));
		if (mods) add("Mods", "mods", () -> {
			try {
				minecraft.setScreen((Screen) Class.forName("com.terraformersmc.modmenu.gui.ModsScreen").getConstructor(Screen.class).newInstance(this));
			} catch (Throwable t) {
			}
		});
		add("Options", "gear", () -> minecraft.setScreen(new OptionsScreen(this, minecraft.options)));
		add("Quit", "power", () -> minecraft.stop());
		lx = Math.max(24, Math.round(width * 0.073f));
		cy = Math.max(40, Math.round(height * 0.2f));
		gap = Math.max(20, Math.min(26, (bot - 40 - (cy + 52)) / Math.max(1, bs.size())));
	}

	void add(String l, String i, Runnable r) {
		B b = new B();
		b.lb = l;
		b.ic = i;
		b.r = r;
		bs.add(b);
	}

	float an(Object o, boolean on, float sp) {
		float[] f = an.computeIfAbsent(o, k -> new float[]{0});
		f[0] += ((on ? 1f : 0f) - f[0]) * sp;
		return f[0];
	}

	static float eo(float t) {
		t = Math.max(0f, Math.min(1f, t));
		return 1f - (1f - t) * (1f - t) * (1f - t);
	}

	void hit(int x, int y, int w, int h, boolean go, Runnable r) {
		Ht t = new Ht();
		t.x = x;
		t.y = y;
		t.w = w;
		t.h = h;
		t.go = go;
		t.r = r;
		hs.add(t);
	}

	public void renderBackground(GuiGraphics g, int mx, int my, float d) {
	}

	boolean unseen() {
		return Upd.unseen() || News.unseen() > 0 || Rel.unseen() > 0;
	}

	float dip(Object o, long now) {
		return cko == o && now - ck < 90 ? 1f : 0f;
	}

	public void render(GuiGraphics g, int mx, int my, float d) {
		long now = Util.getMillis();
		hs.clear();
		float tt = (now % 1000000L) / 1000f;
		if (minecraft.getOverlay() == null && Intro.t0 == 0) Intro.start(now - 900L);
		float rv = (now - Math.max(t0, Intro.busy() ? Intro.ui() : 0L)) / 1000f;
		float ex = ck > 0 && pend != null ? eo((now - ck) / 180f) : 0f;
		float oa = 1f - ex;
		int od = Math.round(4 * ex);
		int ac = Th.ac();
		Vis.bgx(g, tt, 1f);
		var ps = g.pose();
		float p = eo(rv / 0.35f);
		Ui.GA = p * oa;
		ps.pushMatrix();
		ps.translate(0, Math.round(4 * (1 - p)) + od);
		Vis.logoAt(g, tt, 1f, lx, cy, 0.4f + rv);
		tag(g, lx + 2, cy + 20);
		ps.popMatrix();
		Ui.GA = 1f;
		menu(g, mx, my, now, rv, oa, od, ac);
		float kk = eo((rv - 0.08f) / 0.45f) * oa;
		if (skin != null && width >= 380) {
			Ui.GA = kk;
			ps.pushMatrix();
			ps.translate(0, Math.round(3 * (1 - eo((rv - 0.08f) / 0.45f))) + od);
			skn.render(g, mx, my, d);
			ps.popMatrix();
			Ui.GA = 1f;
			acct(g, mx, my, now, rv, oa, od, ac);
		}
		if (width >= 560) frn(g, mx, my, now, rv, oa, od, ac);
		clock(g, mx, my, now, tt, rv, oa, od, ac);
		float pg = eo((rv - 0.47f) / 0.3f) * oa;
		Ui.GA = pg;
		int gx = width >= 560 ? lx - 10 + 176 + 6 : 12;
		int gy = bot - 34 + 5 + Math.round(6 * (1 - eo((rv - 0.47f) / 0.3f))) + od;
		boolean ovg = in(mx, my, gx, gy, 23, 23);
		float hg = an("glb", ovg, 0.3f);
		gy += Math.round(dip("glb", now));
		pill(g, gx, gy, 23, 23, hg, ac);
		Ic.dr(g, "globe", gx + 11, gy + 11, 12, Ui.al(0xFFFFFF, 0.65f + 0.35f * hg));
		hit(gx, gy, 23, 23, true, () -> minecraft.setScreen(new LanguageSelectScreen(this, minecraft.options, minecraft.getLanguageManager())));
		ckreg("glb");
		float fa = eo((rv - 0.4f) / 0.3f) * oa;
		Ui.GA = fa;
		String l = "Azure " + Upd.ver() + "   Minecraft 1.21.11";
		Ui.ts(g, l, 12, height - 18, 8f, 0xFFFFFF, 0.4f);
		String r = "Not affiliated with Mojang AB";
		Ui.ts(g, r, width - Ui.w(r, 8f) - 12, height - 18, 8f, 0xFFFFFF, 0.3f);
		Ui.GA = 1f;
		if (pend != null && now - ck >= 180) {
			Runnable rn = pend;
			pend = null;
			ck = 0;
			rn.run();
		}
		if (minecraft.getOverlay() == null) Intro.draw(g);
	}

	void ckreg(Object o) {
		hs.get(hs.size() - 1).r = wrapr(o, hs.get(hs.size() - 1).r, hs.get(hs.size() - 1).go);
	}

	Runnable wrapr(Object o, Runnable r, boolean go) {
		return () -> {
			cko = o;
			ck = Util.getMillis();
			if (go) pend = r;
			else r.run();
		};
	}

	void pill(GuiGraphics g, int x, int y, int w, int h, float hv, int ac) {
		if (hv < 0.02f) return;
		float ga = Ui.GA;
		Ui.GA = ga * hv;
		Th.box(g, x, y, w, h, ac, 0.13f, ac, 0f);
		Ui.GA = ga;
	}

	void tag(GuiGraphics g, int x, int y) {
		String a = "pvp client";
		Ui.ts(g, a, x, y, 9f, 0xFFFFFF, 0.45f);
		int x2 = x + Ui.w(a) + 3;
		g.fill(x2, y + 4, x2 + 3, y + 5, Ui.al(0xFFFFFF, 0.45f));
		Ui.ts(g, Upd.ver(), x2 + 6, y, 9f, 0xFFFFFF, 0.45f);
	}

	void menu(GuiGraphics g, int mx, int my, long now, float rv, float oa, int od, int ac) {
		int n = bs.size();
		int y0 = cy + 52;
		int hx = lx - 10;
		int hov = -1;
		for (int i = 0; i < n; i++) {
			B b = bs.get(i);
			b.x = hx;
			b.y = y0 + i * gap - 11;
			b.w = 160;
			b.h = 22;
			if (pend == null && in(mx, my, b.x, b.y, b.w, b.h)) hov = i;
		}
		float dt = Math.min(0.1f, (now - lt) / 1000f);
		lt = now;
		float ty = hov >= 0 ? y0 + hov * gap : pyy;
		if (pyy < 0) pyy = ty < 0 ? y0 : ty;
		pyy += (ty - pyy) * Math.min(1f, dt * 18f);
		float pa = an("pill", hov >= 0 || (pend != null && cko instanceof B), 0.25f);
		float fl = ck > 0 && cko instanceof B ? Math.max(0f, 1f - (now - ck) / 300f) : 0f;
		float k0 = eo((rv - 0.12f) / 0.3f);
		if (pa > 0.01f) {
			int dp = cko instanceof B && now - ck < 90 ? 1 : 0;
			int py = Math.round(pyy) - 11 + dp + od;
			Ui.GA = pa * oa * k0;
			Th.box(g, hx, py, 160, 22, ac, 0.13f + 0.12f * fl, ac, 0.7f * fl);
			g.fill(hx + 4, py + 7, hx + 6, py + 15, Ui.al(ac, 1f));
			Ui.GA = 1f;
		}
		int lc = Th.lt();
		for (int i = 0; i < n; i++) {
			B b = bs.get(i);
			float k = eo((rv - 0.12f - 0.04f * i) / 0.3f);
			int rcy = y0 + i * gap + od + (cko == b && now - ck < 90 ? 1 : 0);
			float wgt = pa * Math.max(0f, 1f - Math.abs(pyy - (y0 + i * gap)) / gap);
			int c = Vis.mix(0xFFFFFF, lc, wgt);
			int sx = Math.round(-6 * (1 - k));
			Ui.GA = k * oa;
			Ic.dr(g, b.ic, lx + 6 + sx, rcy, 12, Ui.al(c, 0.75f + 0.25f * wgt));
			Th.lt(g, b.lb, lx + 18 + sx, rcy, 9f, c, 0.85f + 0.15f * wgt);
			Ui.GA = 1f;
			hit(b.x, b.y, b.w, b.h, true, b.r);
			ckreg(b);
		}
	}

	void acct(GuiGraphics g, int mx, int my, long now, float rv, float oa, int od, int ac) {
		int w = 188;
		float k = eo((rv - 0.37f) / 0.3f);
		int y = bot - 74 + Math.round(6 * (1 - k)) + od;
		Ui.GA = k * oa;
		boolean ov = pend == null && in(mx, my, ax, y, w, 30);
		float h = an("acc", ov, 0.3f);
		y += Math.round(dip("acc", now));
		pill(g, ax, y, w, 30, h, ac);
		net.minecraft.client.gui.components.PlayerFaceRenderer.draw(g, sk.get(), ax + 8, y + 7, 16, Ui.al(0xFFFFFF, 1f));
		Th.lt(g, minecraft.getUser().getName(), ax + 32, y + 10, 9f, 0xFFFFFF, 1f);
		Th.lt(g, "Switch account", ax + 32, y + 21, 9f, ac, 1f);
		Ic.dr(g, "chev", ax + w - 13, y + 15, 10, Ui.al(0xFFFFFF, 0.5f + 0.5f * h));
		hit(ax, y, w, 30, true, () -> minecraft.setScreen(new AzAcc(this)));
		ckreg("acc");
		String[][] bt = {{"Wardrobe", "shirt"}, {"Emotes", "hand"}, {"Capes", "cape"}};
		int bw = (w - 8) / 3;
		for (int j = 0; j < 3; j++) {
			float kb = eo((rv - 0.42f - 0.04f * j) / 0.3f);
			int bx = ax + j * (bw + 4);
			int by = bot - 38 + Math.round(6 * (1 - kb)) + od;
			Ui.GA = kb * oa;
			boolean ob = pend == null && in(mx, my, bx, by, bw, 38);
			String key = bt[j][0];
			float hb = an(key, ob || (j == 1 && emo), 0.3f);
			by += Math.round(dip(key, now));
			pill(g, bx, by, bw, 38, hb, ac);
			Ic.dr(g, bt[j][1], bx + bw / 2, by + 14, 12, Ui.al(hb > 0.5f ? Th.lt() : 0xFFFFFF, 0.8f + 0.2f * hb));
			Th.ct(g, bt[j][0], bx + bw / 2f, by + 28, 9f, 0xFFFFFF, 0.7f + 0.3f * hb);
			final int jj = j;
			hit(bx, by, bw, 38, j != 1, () -> {
				if (jj == 1) {
					emo = !emo;
					return;
				}
				AzSkin s = new AzSkin(this);
				s.tab = jj == 2 ? 1 : 0;
				minecraft.setScreen(s);
			});
			ckreg(key);
		}
		Ui.GA = 1f;
		float ea = an("emo", emo, 0.3f);
		if (ea > 0.02f) {
			int pw = w;
			int phh = 3 * 22 + 2 * 4 + 16;
			int px0 = ax;
			int py0 = bot - 74 - 6 - phh + Math.round(6 * (1 - ea));
			Ui.GA = ea * oa;
			Th.box(g, px0, py0, pw, phh, 0f);
			int cw = (pw - 16 - 8) / 3;
			for (int i = 0; i < EN.length; i++) {
				int ex = px0 + 8 + (i % 3) * (cw + 4);
				int ey = py0 + 8 + (i / 3) * 26;
				boolean oe = in(mx, my, ex, ey, cw, 22);
				float he = an(EN[i], oe, 0.3f);
				Th.box(g, ex, ey, cw, 22, Vis.mix(Th.pn(), ac, 0.25f * he), 0.6f, he > 0.02f ? ac : 0xFFFFFF, 0.1f + 0.5f * he);
				Th.ct(g, EN[i], ex + cw / 2f, ey + 11, 8f, 0xFFFFFF, 0.75f + 0.25f * he);
				final int ii = i;
				hit(ex, ey, cw, 22, false, () -> {
					skn.play(ii);
					emo = false;
				});
			}
			Ui.GA = 1f;
		}
	}

	void frn(GuiGraphics g, int mx, int my, long now, float rv, float oa, int od, int ac) {
		float k = eo((rv - 0.32f) / 0.3f);
		int x = lx - 10;
		int y = bot - 34 + Math.round(6 * (1 - k)) + od;
		Ui.GA = k * oa;
		Th.box(g, x, y, 176, 34, 0f);
		Th.box(g, x + 7, y + 7, 20, 20, Vis.mix(Th.pn(), ac, 0.2f), 0.9f, 0xFFFFFF, 0.1f);
		Ic.dr(g, "users3", x + 17, y + 17, 12, Ui.al(0xFFFFFF, 0.8f));
		Th.lt(g, "Friends", x + 34, y + 12, 9f, 0xFFFFFF, 1f);
		Th.lt(g, "Coming soon", x + 34, y + 23, 9f, 0xFFFFFF, Th.SUB);
		boolean ov = in(mx, my, x + 146, y + 6, 21, 21);
		float h = an("fpl", ov, 0.3f);
		Th.box(g, x + 146, y + 6, 21, 21, Vis.mix(Th.pn(), ac, 0.2f + 0.2f * h), 0.9f, ac, 0.35f + 0.3f * h);
		Th.plus(g, x + 156, y + 16, Ui.al(Th.lt(), 0.6f + 0.4f * h));
		Ui.GA = 1f;
	}

	void clock(GuiGraphics g, int mx, int my, long now, float tt, float rv, float oa, int od, int ac) {
		float k = eo((rv - 0.28f) / 0.3f);
		int w = 132;
		int x = width - 12 - w;
		int y = 12 - Math.round(4 * (1 - k)) - od;
		Ui.GA = k * oa;
		Th.box(g, x, y, w, 30, 0f);
		var lt = java.time.LocalTime.now();
		String hh = String.format("%02d", lt.getHour());
		String mi = String.format("%02d", lt.getMinute());
		boolean cl = (now % 1000L) < 500L;
		Th.lt(g, hh, x + 12, y + 10, 9f, 0xFFFFFF, 1f);
		Th.lt(g, ":", x + 12 + Ui.w(hh), y + 10, 9f, 0xFFFFFF, cl ? 1f : 0.25f);
		Th.lt(g, mi, x + 12 + Ui.w(hh) + Ui.w(":"), y + 10, 9f, 0xFFFFFF, 1f);
		String dt = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("EEE d MMM", java.util.Locale.ENGLISH)).toLowerCase(java.util.Locale.ROOT);
		Th.lt(g, dt, x + 12, y + 21, 9f, 0xFFFFFF, Th.SUB);
		g.fill(x + w - 54, y + 8, x + w - 53, y + 22, Ui.al(0xFFFFFF, 0.1f));
		boolean un = unseen();
		if (un && !lun) {
			bwt = now;
			dpt = now;
		}
		lun = un;
		int bx = x + w - 38;
		boolean ob = in(mx, my, bx - 9, y + 4, 18, 22);
		float hb = an("bel", ob, 0.3f);
		int sh = 0;
		long be = now - bwt;
		if (bwt > 0 && be < 600) sh = new int[]{1, -1, 1, -1, 0, 0}[(int) (be / 100)];
		Ic.dr(g, "bell", bx + sh, y + 15, 12, Ui.al(hb > 0.5f ? Th.lt() : 0xFFFFFF, 0.75f + 0.25f * hb));
		if (un) {
			float ds = Math.min(1f, (now - dpt) / 120f) * ((now - dpt) < 200 ? 1.25f : 1f);
			int r = Math.max(1, Math.round(4 * ds));
			Ui.rr(g, bx + 6 - r / 2, y + 9 - r / 2, r, r, Math.max(1, r / 2), Ui.al(ac, 1f));
		}
		hit(bx - 9, y + 4, 18, 22, true, () -> {
			Screen s = Gui.open(this);
			if (s instanceof Mnu mn) mn.obell();
			minecraft.setScreen(s);
		});
		ckreg("bel");
		int dx = x + w - 17;
		boolean od2 = in(mx, my, dx - 9, y + 4, 18, 22);
		float hd = an("dcb", od2, 0.3f);
		Ic.dr(g, "discord", dx, y + 15, 12, Ui.al(hd > 0.5f ? Th.lt() : 0xFFFFFF, 0.75f + 0.25f * hd));
		hit(dx - 9, y + 4, 18, 22, false, Api::dc);
		Ui.GA = 1f;
	}

	boolean in(double mx, double my, int x, int y, int w, int h) {
		return mx >= x && mx < x + w && my >= y && my < y + h;
	}

	public boolean mouseClicked(MouseButtonEvent e, boolean dbl) {
		if (Intro.busy()) {
			Intro.skip();
			return true;
		}
		if (pend != null) return true;
		double mx = e.x();
		double my = e.y();
		for (int i = hs.size() - 1; i >= 0; i--) {
			Ht h = hs.get(i);
			if (in(mx, my, h.x, h.y, h.w, h.h)) {
				Snd.p("click");
				h.r.run();
				return true;
			}
		}
		if (emo) {
			emo = false;
			return true;
		}
		if (skin != null && skn != null && in(mx, my, skin.getX(), skin.getY(), skin.getWidth(), skin.getHeight())) skn.poke();
		return super.mouseClicked(e, dbl);
	}

	public boolean keyPressed(net.minecraft.client.input.KeyEvent e) {
		if (Intro.busy()) {
			Intro.skip();
			return true;
		}
		if (Wc.key != null && Wc.key.matches(e)) {
			minecraft.setScreen(Gui.open(this));
			return true;
		}
		return super.keyPressed(e);
	}

	public boolean shouldCloseOnEsc() {
		return false;
	}
}
