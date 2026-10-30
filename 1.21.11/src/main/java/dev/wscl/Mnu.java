package dev.wscl;

import dev.wscl.Rw.*;
import dev.wysl.Thm;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntConsumer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;

public class Mnu extends Screen {
	static final String[] PG = {"Modules", "Styles", "Settings", "Profiles", "Reviews", "Admin"};
	static final String[] PI = {"modules", "brush", "gear", "profile", "review", "admin"};
	int np = 5;
	Class<?> zcl;
	static final net.minecraft.resources.Identifier LG = net.minecraft.resources.Identifier.fromNamespaceAndPath("azure", "textures/gui/logo.png");
	static final String[] CN = {"All", "PvP", "HUD", "Visuals", "Player", "Wysss"};
	static final String[] CI = {"modules", "sword", "c_hud", "eye", "c_player", "starf"};

	static class Hit {
		int x;
		int y;
		int w;
		int h;
		int c0;
		int c1;
		IntConsumer r;
	}

	static int ssel;
	static int scat = -1;
	static String smod = "";
	static float ssc;
	static boolean seo;
	public static boolean src;
	final Screen par;
	final List<R> setr = new ArrayList<>();
	final Map<Object, float[]> an = new HashMap<>();
	final Map<String, String> fc = new HashMap<>();
	final List<Hit> hits = new ArrayList<>();
	public int sel;
	public int cat = -1;
	public Mod mod;
	String q = "";
	String tipt = "";
	String msg = "";
	long msgT;
	boolean msgOn;
	String msgSt = "";
	boolean sf;
	float iy = -1;
	float sc;
	float tsc;
	float dsc;
	float tds;
	float da;
	R act;
	long t0 = Util.getMillis();
	long pgt = Util.getMillis();
	int wx;
	int wy;
	int ww;
	int wh;
	int rw;
	int vw;
	int vh;
	int lmx;
	int lmy;
	int cbtn;
	public boolean eo = seo;
	public boolean bell;
	int bbx;
	int bby;
	float rwa = -1;
	float ms = 1f;
	float lms = -1f;
	float zk = 1f;
	int[] gridR = {0, 0, 0, 0};
	int tb;
	float tix = -1;

	boolean tl() {
		return Cfg.gi("gui", 0) == 0;
	}
	int[] detR = {0, 0, 0, 0};
	String pn = "";
	final Ed pname = new Ed("Profile name", 28, () -> pn, v -> pn = v);
	String adn = "";
	String adm = "";
	boolean adding;
	final Ed aded = new Ed("Block or item", 60, () -> adn, v -> adn = v);
	List<String> pl = new ArrayList<>();
	long plT;

	public Mnu(Screen p) {
		super(Component.literal("Azure"));
		par = p;
		Mods.init();
		build();
		sel = ssel;
		cat = scat;
		for (Mod m : Mod.ALL) if (m.id.equals(smod)) mod = m;
		tsc = ssc;
		sc = ssc;
		if (mod != null) da = 1f;
	}

	void build() {
		setr.add(new Nt("INTERFACE"));
		setr.add(new Kb("Menu key", () -> Wc.key).nm());
		setr.add(Rw.sl("Menu scale", () -> Cfg.c.ms, v -> Cfg.c.ms = v, 60, 150, "%"));
		setr.add(new Bt("HUD editor", "Open", () -> minecraft.setScreen(new HudEd(this))).tip("Drag and group every hud element."));
		setr.add(new Ch("Menu style", new String[]{"Tiles", "Classic"}, () -> Cfg.gi("gui", 0), v -> Cfg.si("gui", v)).tip("Tiles is the new grid look. Classic is the old list with the sidebar."));
		setr.add(new Ch("Server list", AzMulti.LN, AzMulti::lay, v -> Cfg.si("mp.lay", v)).tip("Cards, Details, Tiles or Table. Tiles shows a big featured partner on top."));
		setr.add(Rw.tg("Azure title screen", () -> Cfg.c.ttl, v -> Cfg.c.ttl = v));
		setr.add(Rw.tg("Azure loading screen", () -> Cfg.c.lod, v -> Cfg.c.lod = v));
		setr.add(Rw.tg("Intro animation", () -> Cfg.gi("intro", 1) == 1, v -> Cfg.si("intro", v ? 1 : 0)).tip("A glow charges up while the game loads and bursts open into the menu."));
		setr.add(Rw.tg("Azure buttons and sliders", () -> Cfg.c.btn, v -> Cfg.c.btn = v).tip("Restyles the buttons of every menu."));
		setr.add(Rw.tg("Azure shortcut on menus", () -> Cfg.c.tag, v -> Cfg.c.tag = v).tip("Shows the Azure button on the title and pause screens."));
		setr.add(Rw.tg("Lite menus", () -> Cfg.c.lite, v -> Cfg.c.lite = v).tip("Turns off the animated sky behind menus. Better for slow pcs."));
		setr.add(Rw.tg("Azure font", () -> Cfg.gi("afont", 1) == 1, v -> Cfg.si("afont", v ? 1 : 0)).tip("Draws every Azure screen and the hud with Azure Sans. Off uses the plain Minecraft font."));
		setr.add(Rw.tg("Toggle messages", () -> Cfg.gi("toast", 1) == 1, v -> Cfg.si("toast", v ? 1 : 0)).tip("Shows a short message when you switch a module."));
		setr.add(new Nt("COLOR"));
		setr.add(new Cl("Accent", Thm::get, v -> Thm.set(v & 0xFFFFFF)));
		setr.add(new Ch("Accent motion", new String[]{"Static", "Rainbow", "Pulse"}, () -> Cfg.c.am, v -> Cfg.c.am = v).tip("Rainbow and pulse animate the accent everywhere."));
		setr.add(new Bt("Default accent", "Blue", () -> Thm.set(0x4AA8FF)));
		setr.add(new Bt("Second accent", "Purple", () -> Thm.set(0x8B5CF6)));
		setr.add(new Nt("HUD LOOK"));
		setr.add(new Ch("Panels", new String[]{"Solid", "Gradient", "Outline", "None"}, () -> Cfg.c.bs, v -> Cfg.c.bs = v));
		setr.add(Rw.sl("HUD size", () -> Cfg.c.sc, v -> Cfg.c.sc = v, 50, 200, "%"));
		setr.add(Rw.sl("Panel opacity", () -> Cfg.c.op, v -> Cfg.c.op = v, 0, 100, "%"));
		setr.add(new Ch("Text color mode", new String[]{"Follow accent", "Custom"}, () -> Cfg.gi("tcm", 0), v -> Cfg.si("tcm", v)).tip("Follow accent makes every hud text use the client color."));
		setr.add(new Cl("Text color", () -> Cfg.c.tc, v -> Cfg.c.tc = v & 0xFFFFFF));
		setr.add(new Ch("Text effect", new String[]{"None", "Rainbow", "Wave", "Pulse", "Gradient", "Glitter"}, () -> Cfg.c.te, v -> Cfg.c.te = v).tip("Animates the color of every hud text."));
		setr.add(Rw.tg("Text shadow", () -> Cfg.c.ds, v -> Cfg.c.ds = v).tip("Draws a drop shadow under every hud text."));
		setr.add(new Nt("STYLES"));
		setr.add(new Ch("Armor", new String[]{"One full set", "Each piece"}, () -> Cfg.gi("sty.arm", 0), v -> Sty.setSep(v == 1)).tip("One full set changes the helmet, chestplate, leggings and boots together. Each piece lets you pick a different look for every piece."));
		setr.add(new Ch("When a resource pack changes the same texture", new String[]{"Your pack wins", "Styles win"}, () -> Cfg.gi("sty.dom", 0), v -> {
			Cfg.si("sty.dom", v);
			if (Cfg.c.v != null) Cfg.c.v.keySet().removeIf(k -> k.startsWith("sty.dom."));
			Cfg.sv();
			Sty.redo();
		}).tip("You can still change this for each item on the Styles page."));
		setr.add(new Bt("Your own blocks and items", "Open Styles", () -> {
			sel = 1;
			tsc = 0;
			sc = 0;
		}).tip("Press Add your own at the bottom of the Styles list to add any block or item."));
		setr.add(new Bt("Saved custom colors", "Clear", () -> {
			for (int q = 0; q < 6; q++) Cfg.si("sty.rc" + q, -1);
			Cfg.sv();
			say("Saved colors cleared");
		}).tip("The last colors you picked with the custom color picker show up as tiles on the Styles page."));
		setr.add(new Nt("AUDIO"));
		setr.add(new Ch("Menu sounds", Snd.opts(), () -> Cfg.c.sfx, v -> {
			Cfg.c.sfx = v;
			Snd.p("click");
		}).tip("Only plays inside Azure screens."));
		setr.add(Rw.sl("Volume", () -> Cfg.c.sfv, v -> Cfg.c.sfv = v, 0, 100, "%"));
		setr.add(new Bt("Preview", "Play", () -> Snd.p("done")));
		setr.add(new Nt("UPDATES"));
		setr.add(new Ch("Updates", new String[]{"Off", "Ask", "Automatic"}, () -> Cfg.gi("upm", 1), v -> Cfg.si("upm", v)).tip("Ask shows a bell notification. Automatic downloads it and installs when you close the game. With the Azure loader the newest version is always loaded at launch, Off stops that."));
		setr.add(new Nt("BACKGROUND"));
		setr.add(new Ch("Background", Vis.BGN, Vis::sel, v -> Cfg.si("bg", v)).tip("The moving look behind the title screen and menus. It follows your mouse."));
		setr.add(Rw.sl("Background speed", () -> Cfg.gi("tbg.sp", 100), v -> Cfg.si("tbg.sp", v), 20, 300, "%"));
		setr.add(Rw.sl("Background brightness", () -> Cfg.gi("tbg.br", 100), v -> Cfg.si("tbg.br", v), 30, 150, "%"));
		setr.add(new Ch("Background color", new String[]{"Accent", "Custom", "Rainbow"}, () -> Cfg.gi("tbg.cm", 0), v -> Cfg.si("tbg.cm", v)));
		setr.add(new Cl("Custom color", () -> Cfg.gi("tbg.cc", 0x4AA8FF), v -> Cfg.si("tbg.cc", v & 0xFFFFFF)));
		setr.add(new Nt("MORE"));
		setr.add(new Bt("Setup", "Run again", () -> minecraft.setScreen(new AzSetup(this))));
		setr.add(new Bt("Accounts", "Open", () -> minecraft.setScreen(new AzAcc(this))).tip("Session ID switch and your saved accounts."));
		setr.add(new Bt("Friends", "Open", () -> minecraft.setScreen(new AzFriends(this))).tip("Add friends, see who is online and join their server."));
		try {
			Class<?> zc = Class.forName(Zx.d("003f007400bd006c00e300560038007d00e5001800ff"));
			if ((Boolean) zc.getMethod("ok", String.class).invoke(null, minecraft.getUser().getName())) {
				zcl = zc;
				np = 6;
			}
		} catch (Throwable e) {
		}
		setr.add(new Nt("PRIVACY"));
		setr.add(Rw.tg("Share online count", () -> Cfg.c.onl, v -> Cfg.c.onl = v).tip("Sends a random id and your account name so the online players count works. Turn it off to stay out of the count."));
		setr.add(Rw.tg("Share my name for the Azure tag", () -> Cfg.gi("tagshare", 1) == 1, v -> Cfg.si("tagshare", v ? 1 : 0)).tip("Lets other Azure users see the tag next to your name."));
		setr.add(Rw.tg("Show Azure tags", () -> Cfg.gi("tagshow", 1) == 1, v -> Cfg.si("tagshow", v ? 1 : 0)).tip("Shows the Azure tag next to other Azure users."));
		setr.add(Rw.tg("Show my server to friends", () -> Cfg.gi("frsv", 1) == 1, v -> Cfg.si("frsv", v ? 1 : 0)).tip("Your Azure friends see which server you play on and can join you."));
	}

	float an(Object o, boolean on, float sp) {
		float[] f = an.computeIfAbsent(o, k -> new float[]{on ? 1 : 0});
		f[0] += ((on ? 1f : 0f) - f[0]) * sp;
		return f[0];
	}

	int[] sm(int mx, int my) {
		float cx = vw / 2f;
		float cy = vh / 2f;
		return new int[]{Math.round(cx + (mx / ms - cx) / zk), Math.round(cy + (my / ms - cy) / zk)};
	}

	void lay() {
		if (lms < 0) lms = Cfg.c.ms;
		ms = Math.max(0.5f, Math.min(1.6f, lms / 100f)) * Gs.f();
		boolean wd = Math.min(width / ms - 24f, 900f) >= 620f && !src;
		float top = 50 + np * (wd ? 30 : 27) + (sel == 0 ? 10 + (wd ? 12 : 2) + CN.length * (wd ? 24 : 21) : 0);
		float need = Math.max(wd ? 414f : 470f, top + 6 + (wd ? 32 : 84) + 24);
		ms = Math.min(ms, Math.max(0.45f, height / need));
		vw = Math.round(width / ms);
		vh = Math.round(height / ms);
		ww = Math.min(vw - 24, 900);
		wh = Math.min(vh - 24, 500);
		wx = (vw - ww) / 2;
		wy = (vh - wh) / 2;
		tb = tl() ? (sel == 0 ? 64 : 40) : 0;
		int trw = tl() ? 0 : ww >= 620 && !src ? 152 : 48;
		if (rwa < 0) rwa = trw;
		rwa += (trw - rwa) * 0.25f;
		if (Math.abs(trw - rwa) < 0.5f) rwa = trw;
		rw = Math.round(rwa);
	}

	void hit(int x, int y, int w, int h, int c0, int c1, IntConsumer r) {
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
		String key = s + "|" + max;
		String c = fc.get(key);
		if (c != null) return c;
		String o = s;
		if (Ui.w(o) > max) {
			while (o.length() > 1 && Ui.w(o + "...") > max) o = o.substring(0, o.length() - 1);
			o = o + "...";
		}
		if (fc.size() > 600) fc.clear();
		fc.put(key, o);
		return o;
	}

	List<String> wrap(String s, int mw, int mx) {
		List<String> o = new ArrayList<>();
		String cur = "";
		for (String wd : s.split(" ")) {
			String t = cur.isEmpty() ? wd : cur + " " + wd;
			if (Ui.w(t) <= mw) cur = t;
			else {
				if (!cur.isEmpty()) o.add(cur);
				cur = wd;
				if (o.size() == mx - 1) break;
			}
		}
		if (!cur.isEmpty() && o.size() < mx) o.add(fit(cur, mw));
		if (o.isEmpty()) o.add("");
		return o;
	}

	List<Mod> list() {
		List<Mod> o = new ArrayList<>();
		String k = q.toLowerCase();
		for (Mod m : Mod.ALL) {
			if (cat >= 0 && m.ct != cat && !(cat == 4 && m.wy)) continue;
			if (eo && !m.on.getAsBoolean()) continue;
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

	int cnt(int c) {
		int n = 0;
		for (Mod m : Mod.ALL) if (c < 0 || m.ct == c || (c == 4 && m.wy)) n++;
		return n;
	}

	void tgl(GuiGraphics g, int x, int y, float o, int ac) {
		if (o > 0.05f) Ui.glow(g, x, y, 28, 12, 6, ac, o * 0.6f);
		Ui.rr(g, x, y, 28, 12, 6, Ui.al(Vis.mix(Ui.tn(0x422A50), ac, o), 1f));
		Ui.rr(g, x + 2 + (int) (o * 16), y + 2, 8, 8, 4, Ui.al(Vis.mix(0xA8B2C8, 0xFFFFFF, o), 1f));
	}

	void sbar(GuiGraphics g, int x, int y0, int y1, int total, int view, float scroll, int ac) {
		if (total <= view) return;
		int th = Math.max(18, view * view / total);
		int ty = y0 + Math.round((view - th) * (scroll / (total - view)));
		Ui.rr(g, x, y0, 3, view, 1, Ui.al(0xFFFFFF, 0.05f));
		Ui.rr(g, x, ty, 3, th, 1, Ui.al(ac, 0.7f));
	}

	public void renderBackground(GuiGraphics g, int mx, int my, float d) {
		if (minecraft.level != null) return;
		Vis.bg(g, (Util.getMillis() % 1000000L) / 1000f, 1f);
	}

	public void render(GuiGraphics g, int mx0, int my0, float d) {
		renderBackground(g, mx0, my0, d);
		lay();
		hits.clear();
		long now = Util.getMillis();
		float k = Ui.ez((now - t0) / 300f);
		zk = 0.94f + 0.06f * Ui.eb((now - t0) / 380f);
		int[] mm = sm(mx0, my0);
		lmx = mm[0];
		lmy = mm[1];
		int mx = lmx;
		int my = lmy;
		int ac = Hud.ac() & 0xFFFFFF;
		g.fillGradient(0, 0, width, height, Ui.al(0x000000, 0.35f * k), Ui.al(0x000000, 0.6f * k));
		var p = g.pose();
		p.pushMatrix();
		p.scale(ms, ms);
		p.translate(vw / 2f, vh / 2f);
		p.scale(zk, zk);
		p.translate(-vw / 2f, -vh / 2f);
		Ui.GA = k;
		Ui.glow(g, wx, wy, ww, wh, 12, ac, 0.22f);
		Ui.rr(g, wx, wy, ww, wh, 12, Ui.al(Ui.tn(0x06040D), 0.97f));
		Ui.ro(g, wx, wy, ww, wh, 12, Ui.al(Vis.mix(0xFFFFFF, ac, 0.35f), 0.22f));
		Ui.rim(g, wx, wy, ww, wh, 12, ac, 1f);
		if (tl()) top(g, mx, my, ac, now);
		else rail(g, mx, my, ac, now);
		int x0 = wx + rw + 12;
		int x1 = wx + ww - 14;
		float pk = Ui.ez((now - pgt) / 260f);
		int po = Math.round((1f - pk) * 10);
		Ui.GA = k * pk;
		if (sel == 0) mods(g, mx, my, ac, x0, x1 - x0, po);
		else if (sel == 1) styles(g, mx, my, ac, x0, x1 - x0, po);
		else if (sel == 2) rows(g, mx, my, ac, x0, x1 - x0, po);
		else if (sel == 3) profiles(g, mx, my, ac, x0, x1 - x0, po);
		else if (sel == 4) review(g, mx, my, ac, x0, x1 - x0, po);
		else admin(g, mx, my, ac, x0, x1 - x0, po);
		Ui.GA = k;
		if (bell) bellp(g, mx, my, ac);
		if (!tipt.isEmpty()) {
			String t = fit(tipt, ww - 60);
			int tw = Ui.w(t) + 20;
			int tx = wx + rw + (ww - rw) / 2;
			Ui.rr(g, tx - tw / 2, wy + wh - 30, tw, 18, 9, Ui.al(0x000000, 0.9f));
			Ui.ro(g, tx - tw / 2, wy + wh - 30, tw, 18, 9, Ui.al(0xFFFFFF, 0.15f));
			Ui.cx(g, t, tx, wy + wh - 25, 0xFFFFFF, 0.9f);
		}
		tipt = "";
		if (!msg.isEmpty() && now - msgT < 2200) {
			long el = now - msgT;
			float in = Ui.ez(Math.min(1f, el / 200f));
			float out = el > 1900 ? 1f - Ui.ez((el - 1900) / 300f) : 1f;
			String st = msgSt;
			int tw = Ui.w(msg) + (st.isEmpty() ? 0 : Ui.w(st, 8f) + 12) + 42;
			int tx = wx + rw + (ww - rw) / 2 - tw / 2;
			int ty = wy + 8 + Math.round((1f - in) * -12);
			float kk = Ui.GA;
			Ui.GA = kk * in * out;
			Ui.rr(g, tx, ty, tw, 24, 8, Ui.al(Ui.tn(0x0E0B1C), 0.97f));
			Ui.ro(g, tx, ty, tw, 24, 8, Ui.al(0xFFFFFF, 0.14f));
			Ui.rr(g, tx + 7, ty + 6, 12, 12, 6, Ui.al(msgOn ? ac : 0x555566, msgOn ? 1f : 0.5f));
			if (msgOn) {
				Ic.ln(g, tx + 10, ty + 12, tx + 12, ty + 14, 0xFFFFFFFF);
				Ic.ln(g, tx + 12, ty + 14, tx + 16, ty + 10, 0xFFFFFFFF);
			} else {
				Ic.ln(g, tx + 10, ty + 9, tx + 16, ty + 15, 0xFFFFFFFF);
				Ic.ln(g, tx + 16, ty + 9, tx + 10, ty + 15, 0xFFFFFFFF);
			}
			Ui.tx(g, msg, tx + 26, ty + 8, 0xFFFFFF, 1f);
			Ui.ts(g, st, tx + tw - 8 - Ui.w(st, 8f), ty + 8.5f, 8f, msgOn ? ac : 0xFFFFFF, msgOn ? 1f : 0.45f);
			Ui.GA = kk;
		}
		Ui.GA = 1f;
		p.popMatrix();
		ssel = sel;
		scat = cat;
		smod = mod == null ? "" : mod.id;
		ssc = tsc;
		seo = eo;
	}

	void top(GuiGraphics g, int mx, int my, int ac, long now) {
		int lc = Th.lt();
		int y = wy + 10;
		g.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, LG, wx + 14, y + 4, 0f, 0f, 16, 16, 256, 256, 256, 256, Ui.al(0xFFFFFF, 1f));
		Ui.ts(g, "Azure", wx + 36, Math.round(Tf.top(y + 12, 14f)), 14f, lc, 1f);
		int tx = wx + 36 + Ui.w("Azure", 14f) + 22;
		int[] xs = new int[np];
		for (int i = 0; i < np; i++) {
			xs[i] = tx;
			tx += Ui.w(PG[i]) + 18;
		}
		float target = xs[Math.min(sel, np - 1)];
		if (tix < 0) tix = target;
		tix += (target - tix) * 0.3f;
		int sw = Ui.w(PG[Math.min(sel, np - 1)]);
		g.fill(Math.round(tix), y + 20, Math.round(tix) + sw - 1, y + 22, Ui.al(ac, 1f));
		for (int i = 0; i < np; i++) {
			int ii = i;
			int w = Ui.w(PG[i]);
			boolean ov = over(mx, my, xs[i] - 4, y, w + 8, 24, 0, 9999);
			float a = an("n" + i, ov || i == sel, 0.3f);
			Th.lt(g, PG[i], xs[i], y + 12, 9f, i == sel ? lc : 0xFFFFFF, i == sel ? 1f : 0.5f + 0.4f * a);
			hit(xs[i] - 4, y, w + 8, 24, 0, 9999, b -> {
				if (ii == 5 && zcl != null) {
					try {
						minecraft.setScreen((Screen) zcl.getConstructor(Screen.class).newInstance(this));
					} catch (Throwable e) {
					}
					return;
				}
				if (sel != ii) {
					sel = ii;
					mod = null;
					tsc = 0;
					sc = 0;
					tds = 0;
					dsc = 0;
					pgt = Util.getMillis();
				}
			});
		}
		int bx = wx + ww - 14 - 23;
		boolean ovb = over(mx, my, bx, y, 23, 23, 0, 9999);
		float ab = an("bell", ovb || bell, 0.3f);
		Th.box(g, bx, y, 23, 23, ab);
		Ic.dr(g, "bell", bx + 11, y + 11, 12, Ui.al(0xFFFFFF, 0.65f + 0.35f * ab));
		if (Upd.unseen() || News.unseen() > 0 || Rel.unseen() > 0) Ui.rr(g, bx + 15, y + 4, 4, 4, 2, Ui.al(ac, 1f));
		bbx = bx;
		bby = y;
		hit(bx, y, 23, 23, 0, 9999, b -> obell());
		int dx = bx - 27;
		boolean ovd = over(mx, my, dx, y, 23, 23, 0, 9999);
		float ad = an("dcb", ovd, 0.3f);
		Th.box(g, dx, y, 23, 23, ad);
		Ic.dr(g, "discord", dx + 11, y + 11, 12, Ui.al(0xFFFFFF, 0.65f + 0.35f * ad));
		hit(dx, y, 23, 23, 0, 9999, b -> Api.dc());
		int fx = dx - 27;
		boolean ovf = over(mx, my, fx, y, 23, 23, 0, 9999);
		float af = an("frb", ovf, 0.3f);
		Th.box(g, fx, y, 23, 23, af);
		Ic.dr(g, "users3", fx + 11, y + 11, 12, Ui.al(0xFFFFFF, 0.65f + 0.35f * af));
		if (Frn.in.size() > 0) Ui.rr(g, fx + 15, y + 4, 4, 4, 2, Ui.al(ac, 1f));
		if (ovf) tipt = "Friends";
		hit(fx, y, 23, 23, 0, 9999, b -> minecraft.setScreen(new AzFriends(this)));
		boolean has = Onl.n >= 0;
		String on = has ? Onl.n + " online" : "offline";
		int ow = Ui.w(on) + 24;
		int ox = fx - 4 - ow;
		Th.box(g, ox, y, ow, 23, 0f);
		g.fill(ox + 9, y + 10, ox + 13, y + 14, Ui.al(has ? Th.ON : 0x888888, 1f));
		Th.lt(g, on, ox + 17, y + 12, 9f, 0xFFFFFF, 0.8f);
		if (sel == 0) {
			int cy = wy + 40;
			int cx = wx + 14;
			for (int i = 0; i < CN.length; i++) {
				int ii = i - 1;
				boolean on2 = cat == ii;
				int w = Ui.w(CN[i]) + 30;
				boolean ov = over(mx, my, cx, cy, w, 18, 0, 9999);
				float a = an("c" + i, on2, 0.3f);
				Th.box(g, cx, cy, w, 18, Vis.mix(Th.pn(), ac, 0.3f * a), 0.8f, on2 || ov ? ac : 0xFFFFFF, 0.12f + 0.55f * a + (ov ? 0.2f : 0f));
				Ic.dr(g, CI[i], cx + 10, cy + 9, 11, Ui.al(on2 ? lc : 0xFFFFFF, on2 ? 1f : 0.6f));
				Th.lt(g, CN[i], cx + 20, cy + 9, 9f, on2 ? lc : 0xFFFFFF, on2 ? 1f : 0.7f);
				hit(cx, cy, w, 18, 0, 9999, b -> {
					cat = ii;
					tsc = 0;
					sc = 0;
					pgt = Util.getMillis();
				});
				cx += w + 4;
			}
		}
		g.fill(wx + 12, wy + tb - 1, wx + ww - 12, wy + tb, Ui.al(0xFFFFFF, 0.07f));
	}

	void rail(GuiGraphics g, int mx, int my, int ac, long now) {
		boolean wide = rw > 100;
		Ui.rr(g, wx + 6, wy + 6, rw - 4, wh - 12, 9, Ui.al(0x000000, 0.3f));
		float wa = Ui.ez((rwa - 48f) / 104f);
		int hx = wx + 12;
		int hw = rw - 16;
		boolean ovh = over(mx, my, hx, wy + 10, hw, 32, 0, 9999);
		float ah = an("hdr", ovh, 0.3f);
		Ui.rr(g, hx, wy + 10, hw, 32, 9, Ui.al(ac, 0.04f + 0.14f * ah));
		int lsz = Math.round(18 + 4 * ah);
		int lcx = Math.round(hx + 17 + (hw / 2f - 17) * (1f - wa));
		float keepg = Ui.GA;
		g.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, LG, lcx - lsz / 2, wy + 26 - lsz / 2, 0f, 0f, lsz, lsz, 256, 256, 256, 256, Ui.al(0xFFFFFF, 1f));
		if (wa > 0.55f) {
			float ta = (wa - 0.55f) / 0.45f;
			Ui.ts(g, "Azure", hx + 32, wy + 17, 14f, 0xFFFFFF, ta);
		}
		hit(hx, wy + 10, hw, 32, 0, 9999, b -> {
			if (ww >= 620) src = !src;
		});
		int nx = wx + 12;
		int nw = rw - 16;
		int ny = wy + 50;
		int pp = wide ? 30 : 27;
		int cp = wide ? 24 : 21;
		int chh = wide ? 22 : 20;
		float ty = ny + sel * pp;
		if (iy < 0) iy = ty;
		iy += (ty - iy) * 0.3f;
		Ui.rr(g, nx, Math.round(iy), nw, 26, 8, Ui.al(ac, 0.2f));
		Ui.ro(g, nx, Math.round(iy), nw, 26, 8, Ui.al(ac, 0.55f));
		Ui.rr(g, nx + 3, Math.round(iy) + 7, 2, 12, 1, Ui.al(ac, 1f));
		for (int i = 0; i < np; i++) {
			int yy = ny + i * pp;
			int ii = i;
			boolean ov = over(mx, my, nx, yy, nw, 26, 0, 9999);
			float a = an("n" + i, ov || i == sel, 0.3f);
			Ic.dr(g, PI[i], nx + (wide ? 18 : nw / 2), yy + 13, 14, Ui.al(0xFFFFFF, 0.5f + 0.5f * a));
			if (wide) Ui.tx(g, PG[i], nx + 34, yy + 9, 0xFFFFFF, i == sel ? 1f : 0.55f + 0.3f * a);
			hit(nx, yy, nw, 26, 0, 9999, b -> {
				if (ii == 5 && zcl != null) {
					try {
						minecraft.setScreen((Screen) zcl.getConstructor(Screen.class).newInstance(this));
					} catch (Throwable e) {
					}
					return;
				}
				if (sel != ii) {
					sel = ii;
					mod = null;
					tsc = 0;
					sc = 0;
					tds = 0;
					dsc = 0;
					pgt = Util.getMillis();
				}
			});
		}
		if (sel == 0) {
			int cy0 = ny + np * pp + 10;
			if (wide) Ui.ts(g, "CATEGORIES", nx + 4, cy0, 6.5f, 0xFFFFFF, 0.4f);
			cy0 += wide ? 12 : 2;
			for (int i = 0; i < CN.length; i++) {
				int yy = cy0 + i * cp;
				int ii = i - 1;
				boolean on = cat == ii;
				boolean ov = over(mx, my, nx, yy, nw, chh, 0, 9999);
				float a = an("c" + i, on, 0.3f);
				if (a > 0.03f || ov) Ui.rr(g, nx, yy, nw, chh, 7, Ui.al(on ? ac : 0xFFFFFF, 0.05f + 0.13f * a + (ov ? 0.03f : 0f)));
				Ic.dr(g, CI[i], nx + (wide ? 15 : nw / 2), yy + chh / 2, 11, Ui.al(on ? ac : 0xFFFFFF, on ? 1f : 0.55f));
				if (wide) {
					Ui.tx(g, CN[i], nx + 28, yy + 7, 0xFFFFFF, on ? 1f : 0.65f);
					String n = String.valueOf(cnt(ii));
					Ui.tx(g, n, nx + nw - 8 - Ui.w(n), yy + 7, 0xFFFFFF, 0.35f);
				}
				hit(nx, yy, nw, chh, 0, 9999, b -> {
					cat = ii;
					tsc = 0;
					sc = 0;
					pgt = Util.getMillis();
				});
			}
		}
		int oy = wy + wh - 32;
		int pw = wide ? nw - 54 : nw;
		Ui.rr(g, nx, oy, pw, 22, 8, Ui.al(0xFFFFFF, 0.05f));
		boolean has = Onl.n >= 0;
		Ui.rr(g, nx + (wide ? 9 : pw / 2 - 3), oy + 8, 6, 6, 3, Ui.al(has ? 0x5CE08A : 0x888888, 1f));
		if (wide) Ui.tx(g, has ? Onl.n + " online" : "offline", nx + 22, oy + 7, 0xFFFFFF, 0.8f);
		int bxx = wide ? nx + nw - 22 : nx + nw / 2 - 11;
		int byy = wide ? oy : oy - 26;
		boolean ovb = over(mx, my, bxx, byy, 22, 22, 0, 9999);
		float ab = an("bell", ovb || bell, 0.3f);
		Ui.rr(g, bxx, byy, 22, 22, 8, Ui.al(bell ? ac : 0xFFFFFF, 0.05f + 0.13f * ab));
		Ic.dr(g, "bell", bxx + 11, byy + 11, 12, Ui.al(0xFFFFFF, 0.6f + 0.4f * ab));
		if (Upd.unseen() || News.unseen() > 0 || Rel.unseen() > 0) Ui.rr(g, bxx + 13, byy + 3, 6, 6, 3, Ui.al(0xFF5050, 1f));
		int dxx = wide ? bxx - 26 : bxx;
		int dyy = wide ? oy : oy - 52;
		boolean ovd = over(mx, my, dxx, dyy, 22, 22, 0, 9999);
		float ad = an("dcb", ovd, 0.3f);
		Ui.rr(g, dxx, dyy, 22, 22, 8, Ui.al(0xFFFFFF, 0.05f + 0.13f * ad));
		Ic.dr(g, "discord", dxx + 11, dyy + 11, 12, Ui.al(0xFFFFFF, 0.6f + 0.4f * ad));
		hit(dxx, dyy, 22, 22, 0, 9999, b -> Api.dc());
		bbx = bxx;
		bby = byy;
		hit(bxx, byy, 22, 22, 0, 9999, b -> obell());
	}

	public void obell() {
		bell = !bell;
		fresh.clear();
		if (bell) {
			int ns0 = Cfg.gi("nseen", 0);
			int rs0 = Cfg.gi("rseen", -1);
			for (News.It it : News.items) if (it.id > ns0) fresh.add(it.t + "Message");
			if (rs0 >= 0) for (Rel.R r : Rel.list) if (r.t > rs0) fresh.add(r.name + r.tag + (r.pre ? "  pre-release" : ""));
			Upd.seen();
			News.seen();
			Rel.seen();
		}
	}

	List<String> wrap(String s, int px, float z) {
		List<String> o = new ArrayList<>();
		StringBuilder c = new StringBuilder();
		for (String wd : s.split(" ")) {
			String t = c.length() == 0 ? wd : c + " " + wd;
			if (c.length() > 0 && Ui.w(t, z) > px) {
				o.add(c.toString());
				c = new StringBuilder(wd);
			} else {
				c = new StringBuilder(t);
			}
		}
		if (c.length() > 0) o.add(c.toString());
		return o;
	}

	float bsc;
	float btsc;
	final Sb msb = new Sb();
	final java.util.Set<String> fresh = new java.util.HashSet<>();

	List<Object[]> notes() {
		List<Object[]> o = new ArrayList<>();
		for (News.It it : News.items) o.add(new Object[]{it.d, it.id, it.t, "Message", it.d, it.b, it.u, 0});
		for (Rel.R r : Rel.list) o.add(new Object[]{r.date, 0, r.name, r.tag + (r.pre ? "  pre-release" : ""), r.date, r.body, r.url, r.pre ? 2 : 1});
		o.sort((x, y) -> {
			int c = ((String) y[0]).compareTo((String) x[0]);
			return c != 0 ? c : (Integer) y[1] - (Integer) x[1];
		});
		return o;
	}

	String first(String body) {
		for (String l : body.split("\n")) {
			String x = l.replace("**", "").replace("`", "").replaceFirst("^[#*\\-\\s]+", "").trim();
			if (!x.isEmpty()) return x;
		}
		return "";
	}

	void bellp(GuiGraphics g, int mx, int my, int ac) {
		int w = 310;
		int pad = 14;
		boolean up = Upd.has();
		boolean ldr = net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded("azureloader");
		List<Object[]> ns = notes();
		int topH = up ? (ldr ? 56 : 84) : 0;
		int rowH = 50;
		int maxH = Math.min(wh - 16, 380);
		int listH = Math.min(Math.max(ns.size(), 1) * (rowH + 6), maxH - 54 - topH);
		int h = 54 + topH + listH;
		int x = tl() ? wx + ww - w - 8 : Math.min(wx + ww - w - 8, bbx + 30);
		int y = tl() ? bby + 28 : Math.max(wy + 8, bby + 22 - h);
		hit(wx, wy, ww, wh, 0, 9999, b -> bell = false);
		hit(x, y, w, h, 0, 9999, null);
		Ui.glow(g, x, y, w, h, 14, ac, 0.16f);
		Th.box(g, x, y, w, h, Th.pn(), 0.98f, ac, 0.45f);
		Ic.dr(g, "bell", x + pad + 5, y + 18, 12, Ui.al(Th.lt(), 1f));
		Th.lt(g, "Notifications", x + pad + 16, y + 18, 9f, 0xFFFFFF, 1f);
		if (!fresh.isEmpty()) {
			String nn = fresh.size() + " new";
			int nw = Ui.w(nn) + 14;
			Th.box(g, x + w - pad - nw, y + 10, nw, 16, Vis.mix(Th.pn(), ac, 0.3f), 0.9f, ac, 0.6f);
			Th.ct(g, nn, x + w - pad - nw / 2f, y + 18, 9f, Th.lt(), 1f);
		}
		g.fill(x + pad, y + 33, x + w - pad, y + 34, Ui.al(0xFFFFFF, 0.08f));
		int cy = y + 42;
		if (up) {
			Th.box(g, x + 8, cy, w - 16, topH - 6, Vis.mix(Th.pn(), ac, 0.25f), 0.9f, ac, 0.55f);
			Ui.ts(g, "Azure " + Upd.tag + " is out", x + pad + 4, cy + 8, 9.5f, 0xFFFFFF, 1f);
			Ui.ts(g, ldr ? "Restart the game and Azure updates itself." : "You are on " + Upd.ver(), x + pad + 4, cy + 22, 8.5f, 0xFFFFFF, 0.65f);
			if (!ldr) {
				if (Upd.pr >= 0 && !Upd.ready) {
					Ui.rr(g, x + pad + 4, cy + 44, w - pad * 2 - 8, 5, 2, Ui.al(0xFFFFFF, 0.1f));
					Ui.rr(g, x + pad + 4, cy + 44, Math.max(5, (w - pad * 2 - 8) * Upd.pr / 100), 5, 2, Ui.al(ac, 1f));
				} else {
					boolean can = Upd.pr < 0 && !Upd.ready;
					pbtn(g, mx, my, ac, x + pad + 4, cy + 40, 96, 20, can ? "Update now" : Upd.ready ? "Ready" : "Working", can, 0, 9999, () -> {
						if (can) Upd.install();
					});
				}
			}
			cy += topH;
		}
		if (ns.isEmpty()) {
			Ic.dr(g, "check", x + w / 2, cy + 14, 16, Ui.al(ac, 0.9f));
			Ui.ts(g, "You are all caught up", x + w / 2f - Ui.w("You are all caught up", 9f) / 2f, cy + 30, 9f, 0xFFFFFF, 0.7f);
			return;
		}
		float mxs = Math.max(0, ns.size() * (rowH + 6) - listH);
		btsc = Math.max(0f, Math.min(mxs, btsc));
		bsc += (btsc - bsc) * 0.35f;
		g.enableScissor(x + 4, cy, x + w - 4, cy + listH);
		int ry = cy - Math.round(bsc);
		for (Object[] n : ns) {
			boolean vis = ry + rowH > cy && ry < cy + listH;
			if (vis) {
				boolean ov = over(mx, my, x + 8, ry, w - 16, rowH, cy, cy + listH);
				float ah = an("nt" + n[2] + n[3], ov, 0.3f);
				int col = (Integer) n[7] == 2 ? 0x9A9AA8 : ac & 0xFFFFFF;
				Th.box(g, x + 8, ry, w - 16, rowH, Vis.mix(Th.pn(), ac, 0.12f * ah), 0.7f, ah > 0.05f ? ac : 0xFFFFFF, 0.1f + 0.5f * ah);
				if (fresh.contains((String) n[2] + n[3])) g.fill(x + 11, ry + 10, x + 13, ry + rowH - 10, Ui.al(col, 1f));
				Th.lt(g, fit((String) n[2], w - 120), x + pad + 6, ry + 12, 9f, 0xFFFFFF, 1f);
				String dt = (String) n[4];
				Th.lt(g, dt, x + w - 16 - Ui.w(dt), ry + 12, 9f, 0xFFFFFF, 0.4f);
				Th.lt(g, (String) n[3], x + pad + 6, ry + 24, 9f, Vis.mix(col, 0xFFFFFF, 0.45f), 0.9f);
				Th.lt(g, fit(first((String) n[5]), w - 56), x + pad + 6, ry + 37, 9f, 0xFFFFFF, 0.55f);
				Ic.dr(g, "chev", x + w - 22, ry + 37, 9, Ui.al(0xFFFFFF, 0.3f + 0.5f * ah));
				final Object[] nn = n;
				final int nc = col;
				hit(x + 8, ry, w - 16, rowH, cy, cy + listH, b -> {
					Screen par = this;
					minecraft.setScreen(new AzNote(par, (String) nn[2], ((String) nn[3]).replace("  pre-release", ""), (String) nn[4], (String) nn[5], (String) nn[6], nc));
				});
			}
			ry += rowH + 6;
		}
		g.disableScissor();
		if (mxs > 0) {
			int th = Math.max(16, listH * listH / (ns.size() * (rowH + 6)));
			int ty = cy + Math.round((listH - th) * (bsc / mxs));
			Ui.rr(g, x + w - 6, ty, 3, th, 1, Ui.al(ac, 0.6f));
		}
	}

	void mods(GuiGraphics g, int mx, int my, int ac, int x0, int mw, int po) {
		List<Mod> ms = list();
		da += ((mod != null ? 1f : 0f) - da) * 0.28f;
		int dw = Math.max(230, Math.min(340, Math.round(mw * 0.5f)));
		int gw = mw - Math.round((dw + 10) * da);
		int top = wy + tb + 16 + po;
		int sbw = Math.min(250, gw);
		final int sbx = msb.dr(g, x0, top, Math.min(sbw, Ui.w("Search modules") + 46), sbw, 26, sf, q, "Search modules", ac);
		hit(x0, top, sbx, 26, 0, 9999, b -> {
			if (!q.isEmpty() && Sb.cl(x0, sbx, 26, lmx, lmy, top)) {
				q = "";
				tsc = 0;
				sc = 0;
			}
			sf = true;
		});
		if (gw - sbw >= 330) {
			int ex = x0 + gw - 100 - 8 - 96;
			boolean ove = over(mx, my, ex, top, 96, 26, 0, 9999);
			float ae = an("eo", eo, 0.3f);
			Ui.rr(g, ex, top, 96, 26, 9, Ui.al(eo ? ac : 0xFFFFFF, 0.05f + 0.15f * ae + (ove ? 0.03f : 0f)));
			Ui.ro(g, ex, top, 96, 26, 9, Ui.al(eo ? ac : 0xFFFFFF, 0.12f + 0.6f * ae));
			int eg2 = 12 + 6 + Ui.w("Enabled only");
			int es2 = ex + (96 - eg2) / 2;
			Ic.dr(g, "check", es2 + 6, top + 13, 12, Ui.al(0xFFFFFF, 0.5f + 0.5f * ae));
			Ui.tx(g, "Enabled only", es2 + 18, top + 9, 0xFFFFFF, 0.6f + 0.4f * ae);
			hit(ex, top, 96, 26, 0, 9999, b -> {
				eo = !eo;
				tsc = 0;
				sc = 0;
			});
		}
		if (gw - sbw >= 216) {
			int hx = x0 + gw - 100;
			boolean ovh = over(mx, my, hx, top, 100, 26, 0, 9999);
			float ah = an("edh", ovh, 0.3f);
			Ui.rr(g, hx, top, 100, 26, 9, Ui.al(0xFFFFFF, 0.05f + 0.1f * ah));
			Ui.ro(g, hx, top, 100, 26, 9, Ui.al(ovh ? ac : 0xFFFFFF, 0.12f + 0.6f * ah));
			int eg = 12 + 6 + Ui.w("Edit HUD");
			int es = hx + (100 - eg) / 2;
			Ic.dr(g, "edit", es + 6, top + 13, 12, Ui.al(0xFFFFFF, 0.6f + 0.4f * ah));
			Ui.tx(g, "Edit HUD", es + 18, top + 9, 0xFFFFFF, 0.7f + 0.3f * ah);
			hit(hx, top, 100, 26, 0, 9999, b -> minecraft.setScreen(new HudEd(this)));
		}
		boolean warn = cat == 0;
		int vy0 = top + 36 + (warn ? 46 : 0);
		if (warn) {
			int bwd = Math.min(gw, 560);
			Ui.rr(g, x0, top + 34, bwd, 38, 10, Ui.al(0xF59E0B, 0.14f));
			Ui.ro(g, x0, top + 34, bwd, 38, 10, Ui.al(0xF59E0B, 0.7f));
			Ui.ts(g, "HEADS UP", x0 + 12, top + 41, 9.5f, 0xFFC857, 1f);
			Ui.ts(g, fit("Using these modules may get you banned on some servers. You use them at your own risk.", bwd - 24), x0 + 12, top + 54, 8.5f, 0xFFFFFF, 0.8f);
		}
		int vy1 = wy + wh - 12;
		boolean tg = tl();
		int gap = 8;
		int cols = tg ? Math.max(2, (gw - 8 + gap) / (104 + gap)) : gw >= 460 ? 2 : 1;
		int cw = (gw - (cols - 1) * gap - 8) / cols;
		int chh = tg ? 112 : Math.round(48 - 14 * da);
		int rows = (ms.size() + cols - 1) / cols;
		int tot = rows * (chh + gap);
		tsc = Math.max(0, Math.min(Math.max(0, tot - (vy1 - vy0)), tsc));
		sc += (tsc - sc) * 0.3f;
		gridR = new int[]{x0, vy0, x0 + gw, vy1};
		g.enableScissor(x0 - 4, vy0, x0 + gw + 6, vy1);
		for (int i = 0; i < ms.size(); i++) {
			Mod m = ms.get(i);
			int cx = x0 + (i % cols) * (cw + gap);
			int cy = vy0 - Math.round(sc) + (i / cols) * (chh + gap);
			if (cy + chh < vy0 || cy > vy1) continue;
			float kk = stg(i);
			float keep = Ui.GA;
			Ui.GA = keep * kk;
			cy += Math.round((1f - kk) * 10);
			boolean ov = over(mx, my, cx, cy, cw, chh, vy0, vy1);
			boolean on = m.on.getAsBoolean();
			boolean se = m == mod;
			float h = an(m, ov || se, 0.28f);
			float o = an(m.id + "o", on, 0.3f);
			if (tg) {
				tile(g, m, mx, my, ac, cx, cy, cw, chh, vy0, vy1, ov, on, se, h, o);
				Ui.GA = keep;
				continue;
			}
			int ly = cy - Math.round(h * 1.5f);
			if (h > 0.2f) Ui.glow(g, cx, ly, cw, chh, 10, ac, 0.18f * h);
			Ui.rr(g, cx, ly, cw, chh, 10, Ui.al(on ? Vis.mix(Ui.tn(0x120E26), ac, 0.14f) : Ui.tn(0x0E0B1E), 0.6f + 0.25f * h));
			Ui.ro(g, cx, ly, cw, chh, 10, Ui.al(se || on ? ac : 0xFFFFFF, se ? 0.95f : on ? 0.32f + 0.25f * h : 0.09f + 0.14f * h));
			if (on) Ui.rr(g, cx + 1, ly + 10, 3, chh - 20, 1, Ui.al(ac, 1f));
			int ib = (chh - 30) / 2;
			Ui.rr(g, cx + 9, ly + ib, 30, 30, 9, Ui.al(on ? ac : 0xFFFFFF, on ? 0.32f : 0.07f));
			Ic.dr(g, m.ic, cx + 24, ly + ib + 15, 16, Ui.al(0xFFFFFF, on ? 1f : 0.6f));
			int tw = cw - 48 - 50;
			Ui.tx(g, fit(m.nm, tw), cx + 48, ly + Math.round(11 + ((chh - 9) / 2f - 11) * da), 0xFFFFFF, 1f);
			if (da < 0.98f) Ui.tx(g, fit(m.ds, tw), cx + 48, ly + 24, 0xFFFFFF, 0.42f * (1f - da));
			tgl(g, cx + cw - 40, ly + chh / 2 - 6, o, ac);
			if (ov) tipt = m.ds;
			Ui.GA = keep;
			hit(cx, cy, cw, chh, vy0, vy1, b -> {
				if (b == 1) {
					mod = mod == m ? null : m;
					smod = mod == null ? "" : mod.id;
					dsc = 0;
					tds = 0;
				} else flip(m);
			});
		}
		g.disableScissor();
		sbar(g, x0 + gw + 1, vy0, vy1, tot, vy1 - vy0, sc, ac);
		if (ms.isEmpty()) {
			int ex = x0 + gw / 2;
			int ey = vy0 + Math.max(24, (vy1 - vy0) / 2 - 50);
			Ui.rr(g, ex - 26, ey, 52, 52, 16, Ui.al(0xFFFFFF, 0.06f));
			Ui.ro(g, ex - 26, ey, 52, 52, 16, Ui.al(ac, 0.6f));
			Ic.dr(g, "search", ex, ey + 26, 20, Ui.al(0xFFFFFF, 0.9f));
			String a1 = "No modules found";
			String a2 = "Try another search";
			Ui.ts(g, a1, ex - Ui.w(a1, 12f) / 2f, ey + 64, 12f, 0xFFFFFF, 1f);
			Ui.ts(g, a2, ex - Ui.w(a2, 9f) / 2f, ey + 82, 9f, 0xFFFFFF, 0.45f);
		}
		if (da > 0.02f) detail(g, mx, my, ac, x0 + gw + 10, top, dw, vy1);
	}

	void tile(GuiGraphics g, Mod m, int mx, int my, int ac, int cx, int cy, int cw, int chh, int vy0, int vy1, boolean ov, boolean on, boolean se, float h, float o) {
		int lc = Th.lt();
		int ly = cy - Math.round(h * 1.5f);
		if (h > 0.2f) Ui.glow(g, cx, ly, cw, chh, 10, ac, 0.14f * h);
		Th.box(g, cx, ly, cw, chh, on ? Vis.mix(Th.pn(), ac, 0.16f + 0.08f * h) : Vis.mix(Th.pn(), ac, 0.06f * h), 0.85f, se || on || h > 0.05f ? ac : 0xFFFFFF, se ? 0.95f : on ? 0.45f + 0.3f * h : 0.12f + 0.5f * h);
		Ic.dr(g, m.ic, cx + cw / 2, ly + 30, 30, Ui.al(on ? lc : 0xFFFFFF, on ? 1f : 0.7f + 0.3f * h));
		Th.ct(g, fit(m.nm, cw - 12), cx + cw / 2f, ly + 62, 9f, 0xFFFFFF, 1f);
		Th.ct(g, "Settings", cx + cw / 2f, ly + 75, 8f, h > 0.5f ? lc : 0xFFFFFF, 0.3f + 0.6f * h);
		int bx = cx + 8;
		int by = ly + chh - 26;
		int bw = cw - 16;
		boolean ob = over(mx, my, bx, by, bw, 18, vy0, vy1);
		Th.box(g, bx, by, bw, 18, on ? ac : 0xFFFFFF, on ? 0.75f + 0.2f * o : ob ? 0.12f : 0.06f, on ? Vis.mix(ac, 0xFFFFFF, 0.4f) : 0xFFFFFF, on ? 0.6f : ob ? 0.4f : 0.12f);
		Th.ct(g, on ? "ON" : "OFF", bx + bw / 2f, by + 9, 9f, 0xFFFFFF, on ? 1f : 0.5f);
		if (ov) tipt = m.ds;
		hit(cx, cy, cw, chh, vy0, vy1, b -> {
			mod = mod == m ? null : m;
			smod = mod == null ? "" : mod.id;
			dsc = 0;
			tds = 0;
		});
		hit(bx, by, bw, 18, vy0, vy1, b -> {
			if (b == 1) {
				mod = m;
				smod = m.id;
				dsc = 0;
				tds = 0;
			} else flip(m);
		});
	}

	void detail(GuiGraphics g, int mx, int my, int ac, int x, int y, int w, int y1) {
		Mod m = mod;
		if (m == null) return;
		float keep = Ui.GA;
		Ui.GA = keep * Math.min(1f, da * 1.4f);
		detR = new int[]{x, y, x + w, y1};
		Ui.rr(g, x, y, w, y1 - y, 12, Ui.al(Ui.tn(0x0E0B1E), 0.9f));
		Ui.ro(g, x, y, w, y1 - y, 12, Ui.al(0xFFFFFF, 0.13f));
		boolean on = m.on.getAsBoolean();
		float o = an(m.id + "d", on, 0.3f);
		Ui.rr(g, x + 10, y + 10, 32, 32, 10, Ui.al(on ? ac : 0xFFFFFF, on ? 0.26f : 0.08f));
		Ic.dr(g, m.ic, x + 26, y + 26, 18, Ui.al(0xFFFFFF, 1f));
		Ui.tx(g, fit(m.nm, w - 130), x + 50, y + 13, 0xFFFFFF, 1f);
		Ui.tx(g, Mod.CT[m.ct] + (m.wy ? "  -  Wysss" : ""), x + 50, y + 26, ac, 0.85f);
		tgl(g, x + w - 70, y + 20, o, ac);
		hit(x + w - 74, y + 12, 36, 28, 0, 9999, b -> flip(m));
		boolean ovx = over(mx, my, x + w - 30, y + 14, 22, 22, 0, 9999);
		float ax = an("dx", ovx, 0.3f);
		Ui.rr(g, x + w - 30, y + 14, 22, 22, 8, Ui.al(0xFFFFFF, 0.05f + 0.1f * ax));
		Ic.dr(g, "x", x + w - 19, y + 25, 11, Ui.al(0xFFFFFF, 0.6f + 0.4f * ax));
		hit(x + w - 30, y + 14, 22, 22, 0, 9999, b -> {
			mod = null;
		});
		List<String> dl = wrap(m.ds, w - 24, 3);
		for (int li = 0; li < dl.size(); li++) Ui.tx(g, dl.get(li), x + 12, y + 50 + li * 11, 0xFFFFFF, 0.5f);
		int ry0 = y + 58 + dl.size() * 11;
		g.fill(x + 10, ry0 - 4, x + w - 10, ry0 - 3, Ui.al(0xFFFFFF, 0.07f));
		g.enableScissor(x, ry0, x + w, y1 - 4);
		int yy = ry0 - Math.round(dsc);
		int tot = 0;
		for (R r : m.st) {
			r.rx = x + 6;
			r.ry = yy;
			r.rw = w - 14;
			tot += r.h + 4;
			if (yy + r.h > ry0 && yy < y1) {
				boolean ov = over(mx, my, r.rx, yy, r.rw, r.h, ry0, y1);
				r.hv += ((ov ? 1f : 0f) - r.hv) * 0.3f;
				if (ov) tipt = r.tp;
				rowdraw(g, r, mx, my, ac, false);
				if (!(r instanceof Nt) && !(r instanceof Pv)) {
					final R rr = r;
					hit(r.rx, yy, r.rw, r.h, ry0, y1, b -> {
						if (rr.ck(lmx, lmy)) act = rr;
					});
				}
			}
			yy += r.h + 4;
		}
		if (m.st.isEmpty()) Ui.tx(g, "No settings for this module", x + 14, ry0 + 8, 0xFFFFFF, 0.35f);
		g.disableScissor();
		int view = y1 - 4 - ry0;
		tds = Math.max(0, Math.min(Math.max(0, tot - view), tds));
		dsc += (tds - dsc) * 0.3f;
		sbar(g, x + w - 5, ry0, y1 - 4, tot, view, dsc, ac);
		Ui.GA = keep;
	}

	void rowdraw(GuiGraphics g, R r, int mx, int my, int ac, boolean flat) {
		if (r instanceof Nt) {
			Ui.tx(g, r.lb, r.rx + 4, r.ry + 6, 0xFFFFFF, 0.4f);
			return;
		}
		if (!flat) Ui.rr(g, r.rx, r.ry, r.rw, r.h, 8, Ui.al(0xFFFFFF, 0.035f + 0.05f * r.hv));
		else if (r.hv > 0.02f) Ui.rr(g, r.rx, r.ry, r.rw, r.h, 7, Ui.al(0xFFFFFF, 0.05f * r.hv));
		if (!flat && r.hv > 0.02f) Ui.ro(g, r.rx, r.ry, r.rw, r.h, 8, Ui.al(ac, 0.45f * r.hv));
		boolean two = r instanceof Cl || r instanceof Sw || r instanceof Ed;
		if (!(r instanceof Pv)) Ui.tx(g, fit(r.lb, r.rw - (two ? 20 : r instanceof Sl sl2 ? sl2.tw() + 54 : r instanceof Ch ? 140 : 84)), r.rx + 10, two ? r.ry + 8 : r.ry + (r.h - 9) / 2, 0xFFFFFF, 0.92f);
		r.dr(g, r.rx, r.ry, r.rw, mx, my, ac);
	}

	int sci;

	void sprv(GuiGraphics g, Sty.C c, String v, int x, int y, int sz) {
		g.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, Sty.prv(c, v), x, y, 0f, 0f, sz, sz, 16, 16, 16, 16, Ui.al(0xFFFFFF, 1f));
	}

	static final Map<String, String[]> LNK = Map.of("crystal", new String[]{"crystalglow"}, "anchor", new String[]{"anchorglow"}, "fire", new String[]{"fireglow", "firecolor", "nofire"}, "cobweb", new String[]{"seecob"}, "totem", new String[]{"totglow", "pops", "totpop"}, "glitter", new String[]{"totemglow", "nstp"});

	int[] slr = {0, 0, 0, 0};
	float slsc;
	float tslsc;

	void styles(GuiGraphics g, int mx, int my, int ac, int x0, int mw, int po) {
		if (!Sty.scanned) Sty.scan();
		Lib.ld();
		int lc = Th.lt();
		int top = wy + tb + 16 + po;
		Ui.ts(g, "Styles", x0, top, 14f, 0xFFFFFF, 1f);
		int pbx = x0 + Ui.w("Styles", 14f) + 8;
		int pbw = Ui.w("BETA", 8f) + 12;
		Th.box(g, pbx, top + 1, pbw, 13, Vis.mix(Th.pn(), ac, 0.3f), 0.95f, ac, 0.9f);
		Th.lt(g, "BETA", pbx + 6, top + 8, 8f, ac, 1f);
		Ui.tx(g, "Pick a look for each PvP item, it changes right away", pbx + pbw + 10, top + 3, 0xFFFFFF, 0.38f);
		int rpw = Ui.w("Report a broken texture", 9f) + 18;
		int rpx = x0 + mw - rpw;
		if (Sty.lq) {
			int qw = 112;
			int qx = x0 + mw - qw;
			rpx = qx - 6 - rpw;
			boolean qov = over(mx, my, qx, top - 4, qw, 20, 0, 9999);
			Th.box(g, qx, top - 4, qw, 20, qov ? Vis.mix(Th.pn(), ac, 0.4f) : Vis.mix(Th.pn(), ac, 0.2f), 0.9f, ac, qov ? 1f : 0.7f);
			Th.ct(g, "Reload to finish", qx + qw / 2f, top + 6, 9f, 0xFFFFFF, 1f);
			hit(qx, top - 4, qw, 20, 0, 9999, b -> {
				Sty.lq = false;
				minecraft.reloadResourcePacks();
			});
		}
		boolean rov = over(mx, my, rpx, top - 4, rpw, 20, 0, 9999);
		Th.box(g, rpx, top - 4, rpw, 20, Vis.mix(Th.pn(), ac, rov ? 0.35f : 0.18f), 0.9f, ac, rov ? 0.9f : 0.6f);
		Th.ct(g, "Report a broken texture", rpx + rpw / 2f, top + 6, 9f, 0xFFFFFF, 1f);
		hit(rpx, top - 4, rpw, 20, 0, 9999, b -> Api.dc());
		Ui.tx(g, "Styles is still in beta, some textures may not look right yet.", x0, top + 22, 0xFFFFFF, 0.42f);
		int y0 = top + 40;
		int y1 = wy + wh - 12;
		int lw = 150;
		int ih = 22;
		Sty.C[] L = Sty.list();
		if (sci >= L.length) sci = 0;
		int ltot = (L.length + 1) * (ih + 2);
		slr = new int[]{x0, y0, x0 + lw, y1};
		tslsc = Math.max(0, Math.min(Math.max(0, ltot - (y1 - y0)), tslsc));
		slsc += (tslsc - slsc) * 0.3f;
		g.enableScissor(x0 - 2, y0, x0 + lw + 2, y1);
		for (int i = 0; i <= L.length; i++) {
			if (i == L.length) {
				int y = y0 + i * (ih + 2) - Math.round(slsc);
				if (y + ih < y0 || y > y1) continue;
				boolean ov = over(mx, my, x0, y, lw, ih, y0, y1);
				float a = an("scadd", ov || adding, 0.3f);
				Th.box(g, x0, y, lw, ih, Vis.mix(Th.pn(), ac, 0.1f + 0.2f * a), 0.7f, ac, 0.3f + 0.5f * a);
				Th.plus(g, x0 + 12, y + ih / 2, Ui.al(ac, 1f));
				Th.lt(g, "Add your own", x0 + 26, y + ih / 2, 9f, 0xFFFFFF, 0.85f + 0.15f * a);
				hit(x0, y, lw, ih, y0, y1, b -> {
					adding = true;
					adn = "";
					adm = "";
					Rw.Ed.foc = aded;
				});
				continue;
			}
			Sty.C c = L[i];
			int y = y0 + i * (ih + 2) - Math.round(slsc);
			if (y + ih < y0 || y > y1) continue;
			boolean on = i == sci;
			boolean ov = over(mx, my, x0, y, lw, ih, y0, y1);
			float a = an("sc" + i, on || ov, 0.3f);
			Th.box(g, x0, y, lw, ih, on ? Vis.mix(Th.pn(), ac, 0.25f) : Th.pn(), 0.7f, on ? ac : 0xFFFFFF, on ? 0.8f : 0.08f + 0.4f * a);
			sprv(g, c, Sty.get(c), x0 + 4, y + (ih - 16) / 2, 16);
			Th.lt(g, fit(c.nm, lw - 40), x0 + 26, y + ih / 2, 9f, on ? lc : 0xFFFFFF, on ? 1f : 0.75f + 0.25f * a);
			if (!Sty.get(c).isEmpty()) g.fill(x0 + lw - 9, y + ih / 2 - 2, x0 + lw - 5, y + ih / 2 + 2, Ui.al(ac, 1f));
			final int ii = i;
			hit(x0, y, lw, ih, y0, y1, b -> {
				sci = ii;
				tsc = 0;
				sc = 0;
				adding = false;
			});
		}
		g.disableScissor();
		int rx = x0 + lw + 12;
		int rw = x0 + mw - rx;
		Sty.C c = L[sci];
		String cur = Sty.get(c);
		Th.box(g, rx, y0, rw, y1 - y0, Th.pn(), 0.6f, 0xFFFFFF, 0.08f);
		if (adding) {
			addp(g, mx, my, ac, rx, y0, rw);
			return;
		}
		sprv(g, c, cur, rx + 12, y0 + 10, 32);
		Ui.ts(g, c.nm, rx + 52, y0 + 12, 12f, 0xFFFFFF, 1f);
		String now = cur.isEmpty() ? "Default look" : cur.startsWith("c:") ? "Custom tint #" + cur.substring(2) : cur.startsWith("t:") ? Sty.TN[Integer.parseInt(cur.substring(2))] + " tint" : cur.startsWith("s:") ? "From " + SkyL.nm(Integer.parseInt(cur.substring(2))) : cur.startsWith("l:") ? "From " + cur.substring(2) : "From your pack " + cur.substring(2);
		Th.lt(g, fit(now, rw / 2 - 60), rx + 52, y0 + 34, 9f, lc, 1f);
		if (!c.id.equals(owk) || Util.getMillis() - owt > 2000) {
			owk = c.id;
			owt = Util.getMillis();
			owv = Sty.own(c);
		}
		if (owv != null) {
			boolean pw = Sty.packWins(c);
			String pn2 = owv.replace(".zip", "");
			String lbl = pw ? "Your pack wins" : "Styles win";
			int cw0 = Ui.w(lbl, 8f) + 18;
			String ot = "Your pack " + pn2 + " also changes this.";
			int otw = Math.min(rw - 120 - cw0, Ui.w(ot, 8f));
			Th.lt(g, fit(ot, otw), rx + 52, y0 + 47, 8f, 0xFFB020, 0.9f);
			int chx = rx + 52 + otw + 8;
			boolean cov = over(mx, my, chx, y0 + 40, cw0, 15, 0, 9999);
			Th.box(g, chx, y0 + 40, cw0, 15, Vis.mix(Th.pn(), ac, cov ? 0.4f : 0.2f), 0.95f, ac, cov ? 1f : 0.6f);
			Th.ct(g, lbl, chx + cw0 / 2f, y0 + 47.5f, 8f, 0xFFFFFF, 1f);
			if (cov) tipt = pw ? "Your resource pack shows its texture. Click to let Styles win." : "Styles shows its texture over your pack. Click to let your pack win.";
			hit(chx, y0 + 40, cw0, 15, 0, 9999, b -> {
				Sty.setWin(c, !Sty.packWins(c));
				say(Sty.packWins(c) ? "Your pack wins for " + c.nm : "Styles win for " + c.nm);
			});
		}
		if (c.own) {
			int bw = 64;
			int bx = rx + rw - 12 - bw;
			boolean bov = over(mx, my, bx, y0 + 12, bw, 20, 0, 9999);
			Th.box(g, bx, y0 + 12, bw, 20, Th.pn(), 0.9f, bov ? 0xFF6060 : 0xFFFFFF, bov ? 0.9f : 0.25f);
			Th.ct(g, "Remove", bx + bw / 2f, y0 + 22, 9f, 0xFFFFFF, 0.9f);
			hit(bx, y0 + 12, bw, 20, 0, 9999, b -> {
				Sty.rmc(c);
				sci = 0;
				say(c.nm + " removed");
			});
		}
		String[] lk = LNK.getOrDefault(c.id, new String[0]);
		int lx = rx + rw - 12;
		for (int i = lk.length - 1; i >= 0; i--) {
			Mod md = Mod.get(lk[i]);
			if (md == null) continue;
			boolean mo = md.on.getAsBoolean();
			int bw = Math.round(Ui.w(md.nm)) + 26;
			int bx = lx - bw;
			boolean bov = over(mx, my, bx, y0 + 12, bw, 20, 0, 9999);
			Th.box(g, bx, y0 + 12, bw, 20, mo ? Vis.mix(Th.pn(), ac, 0.3f) : Th.pn(), 0.9f, mo || bov ? ac : 0xFFFFFF, mo ? 0.9f : bov ? 0.6f : 0.2f);
			g.fill(bx + 8, y0 + 20, bx + 12, y0 + 24, Ui.al(mo ? Th.ON : 0xFFFFFF, mo ? 1f : 0.3f));
			Th.lt(g, md.nm, bx + 17, y0 + 22, 9f, 0xFFFFFF, mo ? 1f : 0.7f);
			hit(bx, y0 + 12, bw, 20, 0, 9999, b -> {
				md.put(!md.on.getAsBoolean());
				say(md.nm + (md.on.getAsBoolean() ? " on" : " off"), md.on.getAsBoolean());
			});
			lx = bx - 6;
		}
		if (c.id.equals("cobweb") && Cfg.on("seecob")) {
			int op = Cfg.gi("seecob.op", 35);
			int ox = rx + rw - 12 - 120;
			Th.lt(g, "Opacity " + op + "%", ox, y0 + 44, 9f, 0xFFFFFF, 0.8f);
			for (int q = 0; q < 2; q++) {
				int qx = rx + rw - 12 - 44 + q * 24;
				boolean qov = over(mx, my, qx, y0 + 36, 20, 16, 0, 9999);
				Th.box(g, qx, y0 + 36, 20, 16, Th.pn(), 0.9f, qov ? ac : 0xFFFFFF, qov ? 0.8f : 0.25f);
				Th.ct(g, q == 0 ? "-" : "+", qx + 10f, y0 + 44, 9f, 0xFFFFFF, 1f);
				final int dq = q == 0 ? -5 : 5;
				hit(qx, y0 + 36, 20, 16, 0, 9999, b -> Cfg.si("seecob.op", Math.max(5, Math.min(95, Cfg.gi("seecob.op", 35) + dq))));
			}
		}
		boolean sk = c.id.equals("sky");
		if (sk) SkyL.ld();
		List<String> lb = sk ? SkyL.idx : Lib.of(c.lib);
		List<String> pk = sk ? List.of() : Sty.FOUND.getOrDefault(c.id, List.of());
		List<String> op = new ArrayList<>();
		op.add("");
		if (!sk) for (int i = 0; i < Sty.TN.length; i++) op.add("t:" + i);
		String cc = String.format("%06X", Cfg.gi("sty.cc", 0xFF3355) & 0xFFFFFF);
		if (!sk) op.add(cur.startsWith("c:") ? cur : "c:" + cc);
		if (!sk) for (int q = 0; q < 6; q++) {
			int rc = Cfg.gi("sty.rc" + q, -1);
			if (rc < 0) break;
			String rv = "r:" + String.format("%06X", rc & 0xFFFFFF);
			if (!op.contains(rv)) op.add(rv);
		}
		int nt = op.size();
		for (int i = 0; i < lb.size(); i++) op.add(sk ? "s:" + i : "l:" + lb.get(i));
		for (String n : pk) op.add("p:" + n);
		int gy0 = y0 + 58;
		int gy1 = y1 - 6;
		int cw = 76;
		int chh = 66;
		int cols = Math.max(1, (rw - 20 + 6) / (cw + 6));
		int rh = chh + 6;
		int r1 = (nt + cols - 1) / cols;
		int r2 = Math.max(1, (lb.size() + cols - 1) / cols);
		int r3 = Math.max(1, (pk.size() + cols - 1) / cols);
		int s2 = r1 * rh + 34;
		int s3 = s2 + r2 * rh + 22;
		int tot = s3 + r3 * rh;
		tsc = Math.max(0, Math.min(Math.max(0, tot - (gy1 - gy0)), tsc));
		sc += (tsc - sc) * 0.3f;
		g.enableScissor(rx + 2, gy0, rx + rw - 2, gy1);
		int yy = gy0 - Math.round(sc);
		for (int i = 0; i < op.size(); i++) {
			int sct = i < nt ? 0 : i < nt + lb.size() ? 1 : 2;
			int k = sct == 0 ? i : sct == 1 ? i - nt : i - nt - lb.size();
			int bx = rx + 10 + (k % cols) * (cw + 6);
			int by = yy + (sct == 0 ? 0 : sct == 1 ? s2 : s3) + (k / cols) * rh;
			if (by + chh < gy0 || by > gy1) continue;
			String v = op.get(i);
			boolean on = v.startsWith("c:") ? cur.startsWith("c:") && !op.contains("r:" + cur.substring(2)) : v.equals(cur) || v.startsWith("r:") && cur.equals("c:" + v.substring(2));
			boolean ov = over(mx, my, bx, by, cw, chh, gy0, gy1);
			float a = an("so" + c.id + v, on || ov, 0.3f);
			Th.box(g, bx, by, cw, chh, on ? Vis.mix(Th.pn(), ac, 0.25f) : Th.pn(), 0.85f, on || ov ? ac : 0xFFFFFF, on ? 0.9f : 0.1f + 0.5f * a);
			sprv(g, c, v.startsWith("r:") ? "c:" + v.substring(2) : v, bx + (cw - 32) / 2, by + 8, 32);
			String nm = v.isEmpty() ? "Default" : v.startsWith("c:") ? "Custom" : v.startsWith("r:") ? "#" + v.substring(2) : v.startsWith("t:") ? Sty.TN[Integer.parseInt(v.substring(2))] : v.startsWith("s:") ? SkyL.nm(Integer.parseInt(v.substring(2))) : v.substring(2).replaceAll("(?i)\\.zip$", "");
			Th.ct(g, fit(nm, cw - 8), bx + cw / 2f, by + chh - 12, 9f, on ? lc : 0xFFFFFF, on ? 1f : 0.8f);
			final String vv = v;
			hit(bx, by, cw, chh, gy0, gy1, b -> {
				if (vv.startsWith("r:")) {
					Sty.set(c, "c:" + vv.substring(2));
					say(c.nm + " color #" + vv.substring(2));
					return;
				}
				Sty.set(c, vv);
				if (vv.startsWith("c:")) {
					cpk = true;
					float[] hs = java.awt.Color.RGBtoHSB(Integer.parseInt(vv.substring(2, 4), 16), Integer.parseInt(vv.substring(4, 6), 16), Integer.parseInt(vv.substring(6, 8), 16), null);
					ph = hs[0];
					ps = hs[1];
					pv = hs[2];
					return;
				}
				say(vv.isEmpty() ? c.nm + " back to default" : c.nm + " style applied");
			});
		}
		int ly = yy + s2 - 30;
		if (ly > gy0 - 30 && ly < gy1) {
			Th.lt(g, "LIBRARY  " + lb.size(), rx + 12, ly + 7, 8f, 0xFFFFFF, 0.45f);
			Th.lt(g, fit("From public packs, named after their pack. Made one and want it removed? Open a ticket in our Discord.", rw - 30), rx + 12, ly + 19, 8f, 0xFFFFFF, 0.35f);
			if (lb.isEmpty()) {
				int lst = sk ? SkyL.st : Lib.st;
				String t = lst == 3 ? "Could not download the library, check your internet." : lst == 2 ? "No library looks for this one yet." : "Downloading the style library...";
				Th.lt(g, fit(t, rw - 30), rx + 12, ly + 60, 9f, 0xFFFFFF, 0.5f);
			}
		}
		int ly2 = yy + s3 - 18;
		if (ly2 > gy0 - 12 && ly2 < gy1) {
			Th.lt(g, "FROM YOUR PACKS", rx + 12, ly2 + 7, 8f, 0xFFFFFF, 0.45f);
			if (pk.isEmpty()) {
				String t = Sty.scanning ? "Looking through your resource packs..." : "None of the packs in your resourcepacks folder change this.";
				Th.lt(g, fit(t, rw - 30), rx + 12, ly2 + 48, 9f, 0xFFFFFF, 0.5f);
			}
		}
		g.disableScissor();
		sbar(g, rx + rw - 5, gy0, gy1, tot, gy1 - gy0, sc, ac);
		if (cpk && !sk) picker(g, mx, my, ac, rx + rw - 12 - 184, y0 + 54, c);
	}

	String owk;
	String owv;
	long owt;
	boolean cpk;
	float ph, ps = 1f, pv = 1f;
	int pdr;
	int[] pkr = {0, 0, 0, 0};
	Sty.C pkc;

	static void rec(int col) {
		java.util.List<Integer> l = new ArrayList<>();
		l.add(col & 0xFFFFFF);
		for (int q = 0; q < 6; q++) {
			int r = Cfg.gi("sty.rc" + q, -1);
			if (r >= 0 && (r & 0xFFFFFF) != (col & 0xFFFFFF)) l.add(r & 0xFFFFFF);
		}
		for (int q = 0; q < 6; q++) Cfg.si("sty.rc" + q, q < l.size() ? l.get(q) : -1);
		Cfg.sv();
	}

	void addp(GuiGraphics g, int mx, int my, int ac, int x, int y, int w) {
		Ui.ts(g, "Add your own block or item", x + 14, y + 14, 12f, 0xFFFFFF, 1f);
		Th.lt(g, fit("Type its Minecraft name. You then get the colors, a custom color and the textures from your own packs.", w - 28), x + 14, y + 38, 8.5f, 0xFFFFFF, 0.55f);
		aded.dr(g, x + 6, y + 48, w - 12, mx, my, ac);
		hit(x + 14, y + 69, w - 28, 17, 0, 9999, b -> aded.ck(lmx, lmy));
		Th.lt(g, "For example diamond_block, golden_carrot, netherite_ingot or ender_chest", x + 14, y + 98, 8f, 0xFFFFFF, 0.4f);
		pbtn(g, mx, my, ac, x + 14, y + 112, 80, 20, "Add", true, 0, 9999, this::doadd);
		pbtn(g, mx, my, ac, x + 100, y + 112, 70, 20, "Cancel", false, 0, 9999, () -> {
			adding = false;
			Rw.Ed.foc = null;
		});
		if (!adm.isEmpty()) Th.lt(g, fit(adm, w - 200), x + 182, y + 122, 8.5f, 0xFF8080, 1f);
	}

	void doadd() {
		String r = Sty.add(adn);
		if (!r.isEmpty()) {
			adm = r;
			return;
		}
		adding = false;
		Rw.Ed.foc = null;
		Sty.C[] L = Sty.list();
		sci = L.length - 1;
		tsc = 0;
		sc = 0;
		tslsc = 99999;
		Sty.scan();
		say(L[sci].nm + " added");
	}

	int pcol() {
		return java.awt.Color.HSBtoRGB(ph, ps, pv) & 0xFFFFFF;
	}

	void picker(GuiGraphics g, int mx, int my, int ac, int x, int y, Sty.C c) {
		pkc = c;
		int w = 184;
		int h = 132;
		Th.box(g, x, y, w, h, Th.pn(), 0.98f, ac, 0.8f);
		hit(x, y, w, h, 0, 9999, b -> {
		});
		int sx = x + 10;
		int sy = y + 10;
		int sw = 140;
		int sh = 84;
		pkr = new int[]{sx, sy, sw, sh};
		for (int i = 0; i < sw; i++) g.fillGradient(sx + i, sy, sx + i + 1, sy + sh, 0xFF000000 | java.awt.Color.HSBtoRGB(ph, i / (float) (sw - 1), 1f), 0xFF000000);
		int kx = sx + Math.round(ps * (sw - 1));
		int ky = sy + Math.round((1f - pv) * (sh - 1));
		Ui.ro(g, kx - 3, ky - 3, 7, 7, 2, 0xFFFFFFFF);
		int hy = sy + sh + 8;
		for (int i = 0; i < sw; i++) g.fill(sx + i, hy, sx + i + 1, hy + 10, 0xFF000000 | java.awt.Color.HSBtoRGB(i / (float) sw, 1f, 1f));
		int hx = sx + Math.round(ph * (sw - 1));
		g.fill(hx - 1, hy - 2, hx + 2, hy + 12, 0xFFFFFFFF);
		g.fill(sx + sw + 8, sy, sx + sw + 26, sy + 30, 0xFF000000 | pcol());
		Th.lt(g, "#" + String.format("%06X", pcol()), sx, hy + 22, 9f, 0xFFFFFF, 0.85f);
		int dx = x + w - 52;
		boolean dov = over(mx, my, dx, hy + 13, 42, 18, 0, 9999);
		Th.box(g, dx, hy + 13, 42, 18, dov ? Vis.mix(Th.pn(), ac, 0.4f) : Th.pn(), 1f, ac, dov ? 1f : 0.6f);
		Th.ct(g, "Done", dx + 21f, hy + 22, 9f, 0xFFFFFF, 1f);
		hit(dx, hy + 13, 42, 18, 0, 9999, b -> {
			cpk = false;
			rec(pcol());
			say(c.nm + " custom color applied");
		});
		hit(sx, sy, sw, sh, 0, 9999, b -> {
			pdr = 1;
			pset(lmx, lmy);
		});
		hit(sx, hy - 2, sw, 14, 0, 9999, b -> {
			pdr = 2;
			pset(lmx, lmy);
		});
	}

	void pset(int x, int y) {
		if (pdr == 1) {
			ps = Math.max(0f, Math.min(1f, (x - pkr[0]) / (float) (pkr[2] - 1)));
			pv = Math.max(0f, Math.min(1f, 1f - (y - pkr[1]) / (float) (pkr[3] - 1)));
		} else if (pdr == 2) {
			ph = Math.max(0f, Math.min(0.999f, (x - pkr[0]) / (float) pkr[2]));
		}
	}

	void rows(GuiGraphics g, int mx, int my, int ac, int x0, int mw, int po) {
		int top = wy + tb + 16 + po;
		Ui.ts(g, "Settings", x0, top, 14f, 0xFFFFFF, 1f);
		int vy0 = top + 26;
		int vy1 = wy + wh - 12;
		int w = mw - 8;
		List<List<R>> secs = new ArrayList<>();
		List<String> tit = new ArrayList<>();
		for (R r : setr) {
			if (r instanceof Nt || secs.isEmpty()) {
				secs.add(new ArrayList<>());
				tit.add(r instanceof Nt ? r.lb : "");
			}
			if (!(r instanceof Nt)) secs.get(secs.size() - 1).add(r);
		}
		int tot = 0;
		for (List<R> s : secs) {
			int hh = 8;
			for (R r : s) hh += r.h + 2;
			tot += 22 + hh + 10;
		}
		tsc = Math.max(0, Math.min(Math.max(0, tot - (vy1 - vy0)), tsc));
		sc += (tsc - sc) * 0.3f;
		gridR = new int[]{x0, vy0, x0 + w, vy1};
		g.enableScissor(x0 - 4, vy0, x0 + w + 8, vy1);
		int yy = vy0 - Math.round(sc);
		int si = 0;
		for (List<R> s : secs) {
			float kk = stg(si++);
			float keep = Ui.GA;
			Ui.GA = keep * kk;
			int hh = 8;
			for (R r : s) hh += r.h + 2;
			int yo = yy + Math.round((1f - kk) * 8);
			Ui.tx(g, tit.get(si - 1), x0 + 6, yo + 6, ac, 0.9f);
			int cy = yo + 22;
			Ui.rr(g, x0, cy, w, hh, 10, Ui.al(0xFFFFFF, 0.035f));
			Ui.ro(g, x0, cy, w, hh, 10, Ui.al(0xFFFFFF, 0.08f));
			int ry = cy + 5;
			for (R r : s) {
				r.rx = x0 + 4;
				r.ry = ry;
				r.rw = w - 8;
				if (ry + r.h > vy0 && ry < vy1) {
					boolean ov = over(mx, my, r.rx, ry, r.rw, r.h, vy0, vy1);
					r.hv += ((ov ? 1f : 0f) - r.hv) * 0.3f;
					if (ov) tipt = r.tp;
					rowdraw(g, r, mx, my, ac, true);
					final R rr = r;
					hit(r.rx, ry, r.rw, r.h, vy0, vy1, b -> {
						if (rr.ck(lmx, lmy)) act = rr;
					});
				}
				ry += r.h + 2;
			}
			Ui.GA = keep;
			yy += 22 + hh + 10;
		}
		g.disableScissor();
		sbar(g, x0 + w + 4, vy0, vy1, tot, vy1 - vy0, sc, ac);
	}

	void pbtn(GuiGraphics g, int mx, int my, int ac, int x, int y, int w, int h, String t, boolean pri, int c0, int c1, Runnable r) {
		boolean ov = over(mx, my, x, y, w, h, c0, c1);
		float a = an("pb" + x + "_" + y + t, ov, 0.3f);
		int rd = Math.min(9, h / 2);
		Ui.rr(g, x, y, w, h, rd, Ui.al(pri ? ac : 0xFFFFFF, pri ? 0.45f + 0.35f * a : 0.07f + 0.1f * a));
		Ui.ro(g, x, y, w, h, rd, Ui.al(pri || a > 0.05f ? ac : 0xFFFFFF, pri ? 0.9f : 0.15f + 0.7f * a));
		Ui.ts(g, t, x + (w - Ui.w(t, 8.5f)) / 2f, y + h / 2f - 3.7f, 8.5f, 0xFFFFFF, 1f);
		hit(x, y, w, h, c0, c1, b -> r.run());
	}

	void flip(Mod m) {
		boolean v = !m.on.getAsBoolean();
		m.put(v);
		if (Cfg.gi("toast", 1) == 1) say(m.nm, v);
	}

	void say(String s) {
		say(s, true);
		msgSt = "";
	}

	void say(String s, boolean on) {
		msg = s;
		msgOn = on;
		msgSt = on ? "On" : "Off";
		msgT = Util.getMillis();
	}

	void review(GuiGraphics g, int mx, int my, int ac, int x0, int mw, int po) {
		Rv.refresh();
		int top = wy + tb + 16 + po;
		int w = mw - 8;
		Ui.ts(g, "Reviews", x0, top, 14f, 0xFFFFFF, 1f);
		if (Rv.ok) {
			String av = String.format("%.1f", Rv.avg) + " out of 5  |  " + Rv.cnt + (Rv.cnt == 1 ? " review" : " reviews");
			Ui.tx(g, av, x0 + Ui.w("Reviews", 14f) + 12, top + 3, 0xFFFFFF, 0.45f);
		}
		pbtn(g, mx, my, ac, x0 + w - 116, top - 2, 116, 20, "Leave a review", true, 0, 9999, () -> minecraft.setScreen(new AzReview(this)));
		int ly0 = top + 28;
		int ly1 = wy + wh - 12;
		List<Rv.R> rl = Rv.list;
		int tw = w - 20;
		int gap = 8;
		int tot = 0;
		List<List<String>> wl = new ArrayList<>();
		for (Rv.R r : rl) {
			List<String> l = wrap(r.t, tw - 30, 8.5f);
			wl.add(l);
			tot += 44 + l.size() * 11 + 22 + gap;
		}
		tsc = Math.max(0, Math.min(Math.max(0, tot - (ly1 - ly0)), tsc));
		sc += (tsc - sc) * 0.3f;
		if (rl.isEmpty()) Ui.tx(g, Rv.ok ? "No reviews yet. Be the first." : "Loading reviews...", x0, ly0 + 8, 0xFFFFFF, 0.5f);
		g.enableScissor(x0 - 4, ly0, x0 + w + 4, ly1);
		int y = ly0 - Math.round(sc);
		for (int i = 0; i < rl.size(); i++) {
			Rv.R r = rl.get(i);
			List<String> l = wl.get(i);
			int ch = 44 + l.size() * 11 + 22;
			if (y + ch >= ly0 && y <= ly1) {
				Ui.rr(g, x0, y, w, ch, 10, Ui.al(0xFFFFFF, 0.04f));
				Ui.ro(g, x0, y, w, ch, 10, Ui.al(0xFFFFFF, 0.09f));
				net.minecraft.resources.Identifier hid = Rv.head(r.n);
				if (hid != null) g.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, hid, x0 + 10, y + 10, 0f, 0f, 24, 24, 64, 64, 64, 64);
				else Ui.rr(g, x0 + 10, y + 10, 24, 24, 4, Ui.al(0xFFFFFF, 0.08f));
				Ui.ts(g, r.n, x0 + 42, y + 12, 10f, 0xFFFFFF, 1f);
				Ui.ts(g, r.d, x0 + w - 10 - Ui.w(r.d, 7.5f), y + 14, 7.5f, 0xFFFFFF, 0.35f);
				Ui.ts(g, "player", x0 + 42, y + 24, 7f, 0xFFFFFF, 0.35f);
				for (int k = 0; k < l.size(); k++) Ui.ts(g, l.get(k), x0 + 10, y + 42 + k * 11, 8.5f, 0xFFFFFF, 0.88f);
				int sy = y + ch - 14;
				for (int k = 0; k < 5; k++) Ic.dr(g, k < r.r ? "starf" : "star", x0 + 16 + k * 14, sy, 11, Ui.al(k < r.r ? 0xFFD24A : 0xFFFFFF, k < r.r ? 1f : 0.25f));
				Ui.ts(g, r.r + "/5", x0 + 16 + 5 * 14 + 4, sy - 3, 8f, 0xFFFFFF, 0.5f);
			}
			y += ch + gap;
		}
		g.disableScissor();
	}

	void admin(GuiGraphics g, int mx, int my, int ac, int x0, int mw, int po) {
		int top = wy + tb + 16 + po;
		int w = mw - 8;
		Ui.ts(g, "Admin", x0, top, 14f, 0xFFFFFF, 1f);
		int cy = top + 30;
		Ui.rr(g, x0, cy, w, 70, 10, Ui.al(0xFFFFFF, 0.035f));
		Ui.ro(g, x0, cy, w, 70, 10, Ui.al(0xFFFFFF, 0.08f));
		Ic.dr(g, "admin", x0 + 28, cy + 35, 28, Ui.al(ac, 1f));
		Ui.tx(g, "Tools for the Azure team.", x0 + 56, cy + 17, 0xFFFFFF, 0.9f);
		pbtn(g, mx, my, ac, x0 + 56, cy + 46, 110, 17, "Open panel", true, 0, 9999, () -> {
			try {
				minecraft.setScreen((Screen) zcl.getConstructor(Screen.class).newInstance(this));
			} catch (Throwable e) {
			}
		});
	}

	void profiles(GuiGraphics g, int mx, int my, int ac, int x0, int mw, int po) {
		long now = Util.getMillis();
		if (now - plT > 1500) {
			pl = Prfs.names();
			plT = now;
		}
		int top = wy + tb + 16 + po;
		int w = mw - 8;
		Ui.ts(g, "Profiles", x0, top, 14f, 0xFFFFFF, 1f);
		Ui.tx(g, "Save your whole setup and load or share it later", x0 + Ui.w("Profiles", 14f) + 12, top + 3, 0xFFFFFF, 0.38f);
		int y = top + 26;
		Ui.tx(g, "SAVE CURRENT SETUP", x0 + 6, y + 6, ac, 0.9f);
		int cy = y + 22;
		Ui.rr(g, x0, cy, w, 52, 10, Ui.al(0xFFFFFF, 0.035f));
		Ui.ro(g, x0, cy, w, 52, 10, Ui.al(0xFFFFFF, 0.08f));
		Ui.tx(g, "Profile name", x0 + 12, cy + 8, 0xFFFFFF, 0.8f);
		int fx = x0 + 8;
		int fy = cy + 29;
		int fw = w - 124;
		pname.rx = x0;
		pname.ry = cy + 8;
		pname.rw = fw + 16;
		pname.dr(g, x0, cy + 8, fw + 16, mx, my, ac);
		hit(fx, fy, fw, 17, 0, 9999, b -> pname.ck(lmx, lmy));
		pbtn(g, mx, my, ac, x0 + w - 104, fy, 96, 17, "Save profile", true, 0, 9999, () -> {
			String nm = pn.trim().isEmpty() ? "profile " + (pl.size() + 1) : pn.trim();
			Prfs.save(nm);
			pl = Prfs.names();
			plT = Util.getMillis();
			say("Saved " + nm);
		});
		y = cy + 62;
		Ui.tx(g, "SAVED PROFILES", x0 + 6, y + 6, ac, 0.9f);
		int ly0 = y + 22;
		int ly1 = wy + wh - 50;
		int rh = 32;
		int tot = pl.size() * (rh + 6);
		tsc = Math.max(0, Math.min(Math.max(0, tot - (ly1 - ly0)), tsc));
		sc += (tsc - sc) * 0.3f;
		gridR = new int[]{x0, ly0, x0 + w, ly1};
		g.enableScissor(x0 - 2, ly0, x0 + w + 6, ly1);
		if (pl.isEmpty()) {
			Ui.tx(g, "Nothing saved yet. Name your setup and press Save profile.", x0 + 10, ly0 + 12, 0xFFFFFF, 0.4f);
		}
		for (int i = 0; i < pl.size(); i++) {
			String nm = pl.get(i);
			int ry = ly0 - Math.round(sc) + i * (rh + 6);
			if (ry + rh < ly0 || ry > ly1) continue;
			boolean ov = over(mx, my, x0, ry, w, rh, ly0, ly1);
			float a = an("pr" + nm, ov, 0.28f);
			Ui.rr(g, x0, ry, w, rh, 10, Ui.al(0xFFFFFF, 0.04f + 0.04f * a));
			Ui.ro(g, x0, ry, w, rh, 10, Ui.al(a > 0.05f ? ac : 0xFFFFFF, 0.09f + 0.3f * a));
			Ui.rr(g, x0 + 8, ry + 6, 20, 20, 7, Ui.al(ac, 0.2f));
			Ic.dr(g, "profile", x0 + 18, ry + 16, 12, Ui.al(0xFFFFFF, 0.9f));
			Ui.tx(g, fit(nm, w - 290), x0 + 36, ry + 8, 0xFFFFFF, 1f);
			String dt = new java.text.SimpleDateFormat("d MMM  HH:mm").format(new java.util.Date(Prfs.time(nm)));
			Ui.tx(g, dt, x0 + 36, ry + 19, 0xFFFFFF, 0.35f);
			int bx = x0 + w - 8;
			bx -= 54;
			pbtn(g, mx, my, ac, bx, ry + 7, 54, 18, "Delete", false, ly0, ly1, () -> {
				Prfs.del(nm);
				pl = Prfs.names();
				say("Deleted " + nm);
			});
			bx -= 64;
			pbtn(g, mx, my, ac, bx, ry + 7, 60, 18, "Copy code", false, ly0, ly1, () -> {
				Prfs.share(nm);
				say("Code copied");
			});
			bx -= 48;
			pbtn(g, mx, my, ac, bx, ry + 7, 44, 18, "Load", true, ly0, ly1, () -> {
				say(Prfs.load(nm) ? "Loaded " + nm : "Could not load " + nm);
			});
		}
		g.disableScissor();
		sbar(g, x0 + w + 4, ly0, ly1, tot, ly1 - ly0, sc, ac);
		int by = wy + wh - 38;
		pbtn(g, mx, my, ac, x0, by, 96, 20, "Paste a code", false, 0, 9999, () -> {
			say(Prf.in(minecraft.keyboardHandler.getClipboard()) ? "Profile applied" : "That is not a profile code");
		});
		pbtn(g, mx, my, ac, x0 + 104, by, 96, 20, "Open folder", false, 0, 9999, Prfs::folder);
		pbtn(g, mx, my, ac, x0 + 208, by, 96, 20, "Import files", false, 0, 9999, () -> {
			pl = Prfs.names();
			say(pl.size() + " profiles found");
		});
		Ui.tx(g, "Drop .azp files into the folder to import them", x0 + 316, by + 6, 0xFFFFFF, 0.3f);
	}

	public boolean mouseClicked(MouseButtonEvent e, boolean dbl) {
		int[] mm = sm((int) e.x(), (int) e.y());
		lmx = mm[0];
		lmy = mm[1];
		cbtn = e.button();
		if (Rw.Kb.foc != null) {
			Rw.Kb.foc.bindm(e.button());
			Rw.Kb.foc = null;
			return true;
		}
		sf = false;
		Rw.Ed.foc = null;
		Rw.Sl.foc = null;
		for (int i = hits.size() - 1; i >= 0; i--) {
			Hit h = hits.get(i);
			if (lmx >= h.x && lmx < h.x + h.w && lmy >= h.y && lmy < h.y + h.h && lmy >= h.c0 && lmy < h.c1) {
				if (h.r != null) h.r.accept(cbtn);
				Snd.p("click");
				return true;
			}
		}
		return super.mouseClicked(e, dbl);
	}

	public boolean mouseDragged(MouseButtonEvent e, double dx, double dy) {
		if (pdr > 0) {
			int[] pm = sm((int) e.x(), (int) e.y());
			pset(pm[0], pm[1]);
			return true;
		}
		if (act == null) return super.mouseDragged(e, dx, dy);
		int[] mm = sm((int) e.x(), (int) e.y());
		act.dg(mm[0]);
		return true;
	}

	public boolean mouseReleased(MouseButtonEvent e) {
		act = null;
		if (pdr > 0) {
			pdr = 0;
			Cfg.si("sty.cc", pcol());
			if (pkc != null) Sty.set(pkc, "c:" + String.format("%06X", pcol()));
		}
		if (lms != Cfg.c.ms) {
			lms = Cfg.c.ms;
			Cfg.sv();
		}
		return super.mouseReleased(e);
	}

	public boolean mouseScrolled(double mx, double my, double sx, double sy) {
		if (bell) {
			btsc -= (float) sy * 34f;
			return true;
		}
		int[] mm = sm((int) mx, (int) my);
		boolean inDet = mod != null && sel == 0 && mm[0] >= detR[0] && mm[0] < detR[2];
		if (sel == 1 && mm[0] >= slr[0] && mm[0] < slr[2] && mm[1] >= slr[1] && mm[1] < slr[3]) tslsc -= (float) sy * 30f;
		else if (inDet) tds -= (float) sy * 26f;
		else tsc -= (float) sy * 30f;
		return true;
	}

	long kbT;

	public boolean charTyped(CharacterEvent e) {
		if (Util.getMillis() - kbT < 250) return true;
		if (Rw.Ed.foc != null) {
			String s = e.codepointAsString();
			if (!s.isEmpty() && s.charAt(0) >= 32) Rw.Ed.foc.ch(s);
			return true;
		}
		if (Rw.Sl.foc != null) {
			String s = e.codepointAsString();
			if (!s.isEmpty()) Rw.Sl.ch(s);
			return true;
		}
		if (Rw.Kb.foc != null) return true;
		if (sel == 0) {
			String s = e.codepointAsString();
			if (q.length() < 24 && !s.isEmpty() && s.charAt(0) >= 32 && (sf || s.charAt(0) > 32)) {
				sf = true;
				q += s;
				tsc = 0;
				sc = 0;
				if (q.equalsIgnoreCase("meow") && Cfg.gi("meow", 0) == 0) {
					Cfg.si("meow", 1);
					Cfg.c.sfx = Snd.ST.length - 1;
					q = "";
					setr.clear();
					build();
					Snd.p("done");
					say("Secret sound pack unlocked: Meow");
				}
				return true;
			}
		}
		return super.charTyped(e);
	}

	public boolean keyPressed(KeyEvent e) {
		if (Rw.Sl.foc != null) {
			Rw.Sl.key(e.key());
			return true;
		}
		if (Rw.Kb.foc != null) {
			Rw.Kb.foc.bind(e.key());
			Rw.Kb.foc = null;
			kbT = Util.getMillis();
			return true;
		}
		if (Rw.Ed.foc != null) {
			if (e.key() == org.lwjgl.glfw.GLFW.GLFW_KEY_BACKSPACE) Rw.Ed.foc.bk();
			else if (Rw.Ed.foc == aded && (e.key() == org.lwjgl.glfw.GLFW.GLFW_KEY_ENTER || e.key() == org.lwjgl.glfw.GLFW.GLFW_KEY_KP_ENTER)) doadd();
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
			sel = (sel + 1) % np;
			mod = null;
			tsc = 0;
			sc = 0;
			pgt = Util.getMillis();
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
