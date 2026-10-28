package dev.wscl;

import dev.wysl.Thm;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;

public class AzSetup extends Screen {
	static final int[] CO = {0x4AA8FF, 0x8B5CF6, 0x6366F1, 0x06B6D4, 0x10B981, 0xF59E0B, 0xEF4444, 0xEC4899};

	static class Op {
		String id;
		String lb;
		String ds;

		Op(String id, String lb, String ds) {
			this.id = id;
			this.lb = lb;
			this.ds = ds;
		}
	}

	static class Pg {
		String t;
		String s;
		List<Op> o = new ArrayList<>();

		Pg(String t, String s, Op... o) {
			this.t = t;
			this.s = s;
			for (Op x : o) this.o.add(x);
		}
	}

	static class Ht {
		int x;
		int y;
		int w;
		int h;
		Runnable r;
	}

	static final String[] REC = {"fps", "cps", "armor", "pots", "zoom", "tuffbright", "cinv"};
	final Screen par;
	final List<Pg> pgs = new ArrayList<>();
	final Map<String, Boolean> sel = new HashMap<>();
	final List<Ht> hits = new ArrayList<>();
	int step;
	boolean lis;
	long t0 = Util.getMillis();
	long pt = Util.getMillis();
	int cx;
	int cy;
	int cw = 440;
	int ch = 340;
	float f = 1f;

	public AzSetup(Screen p) {
		this(p, false);
	}

	public AzSetup(Screen p, boolean first) {
		super(Component.literal("Azure setup"));
		par = p;
		Mods.init();
		if (first) clean();
		pgs.add(new Pg("Color", ""));
		pgs.add(new Pg("Look", ""));
		pgs.add(new Pg("Server list", ""));
		pgs.add(new Pg("Menu key", ""));
		pgs.add(new Pg("Your HUD", "Pick your HUD.",
				new Op("fps", "FPS", "Frames per second"), new Op("ping", "Ping", "Latency to the server"),
				new Op("cps", "CPS", "Clicks per second"), new Op("xyz", "Coordinates", "Where you are"),
				new Op("keys", "Keystrokes", "The keys you press"), new Op("armor", "Armor HUD", "Armor and durability"),
				new Op("totem", "Totem Counter", "Totems you carry"), new Op("pots", "Potion HUD", "Effects with time left"),
				new Op("clock", "Clock", "Your local time"), new Op("mark", "Watermark", "The Azure logo"),
				new Op("sb", "Scoreboard", "Restyle the sidebar"), new Op("spotify", "Spotify", "Song and cover")));
		pgs.add(new Pg("Gameplay", "Pick the tools you actually use.",
				new Op("zoom", "Zoom", "Hold a key to zoom"),
				new Op("freelook", "Freelook", "Look around freely"), new Op("tuffbright", "Tuff Bright", "See in the dark"),
				new Op("cross", "Custom Crosshair", "Your own crosshair"), new Op("hitcolor", "Hit Color", "Recolor the hit flash"),
				new Op("nopd", "No Place Delay", "No wait to place"), new Op("nstp", "No Self Totem Particles", "Hide your own pop")));
		pgs.add(new Pg("Look and speed", "Style and performance.",
				new Op("cinv", "Custom Inventory", "Glass inventory and hotbar"), new Op("sky", "Custom Sky", "Aurora and stars"),
				new Op("fpsboost", "FPS Boost", "Lower settings for more frames"), new Op("nohotbarname", "No Tooltips", "Hide the item name"),
				new Op("snippet", "Snippet", "Screenshot buttons and gallery")));
		for (Pg g : pgs) for (Op o : g.o) sel.put(o.id, Mod.live(o.id));
	}

	static void clean() {
		for (Mod m : Mod.ALL) {
			if (m.id.equals("skinchg") || m.id.equals("rpc") || m.id.equals("snippet") || m.id.equals("kbsearch")) continue;
			try {
				if (m.on.getAsBoolean()) m.set.accept(false);
			} catch (Throwable e) {
			}
		}
		Mod r = Mod.get("rpc");
		if (r != null) r.set.accept(true);
		for (String k : new String[]{"snippet", "kbsearch"}) {
			Mod d = Mod.get(k);
			if (d != null) d.set.accept(true);
		}
		Cfg.c.onl = true;
		Cfg.si("tagshare", 1);
		Cfg.si("tagshow", 1);
		Cfg.si("upm", 2);
	}

	public void renderBackground(GuiGraphics g, int mx, int my, float d) {
	}

	boolean in(int mx, int my, int x, int y, int w, int h) {
		return mx >= x && mx < x + w && my >= y && my < y + h;
	}

	void hit(int x, int y, int w, int h, Runnable r) {
		Ht t = new Ht();
		t.x = x;
		t.y = y;
		t.w = w;
		t.h = h;
		t.r = r;
		hits.add(t);
	}

	void btn(GuiGraphics g, int mx, int my, int ac, int x, int y, int w, int h, String t, boolean pri, Runnable r) {
		boolean ov = in(mx, my, x, y, w, h);
		Ui.rr(g, x, y, w, h, 9, Ui.al(pri ? ac : Ui.tn(0x120E26), pri ? 0.55f + (ov ? 0.25f : 0f) : 0.8f));
		Ui.ro(g, x, y, w, h, 9, Ui.al(pri || ov ? ac : 0xFFFFFF, pri ? 0.9f : ov ? 0.8f : 0.16f));
		Ui.ts(g, t, x + (w - Ui.w(t, 9f)) / 2f, y + h / 2f - 3.7f, 9f, 0xFFFFFF, 1f);
		hit(x, y, w, h, r);
	}

	public void render(GuiGraphics g, int mx, int my, float d) {
		hits.clear();
		float t = (Util.getMillis() % 1000000L) / 1000f;
		Vis.bg(g, t, 1f);
		int ac = Hud.ac() & 0xFFFFFF;
		float k = Ui.ez((Util.getMillis() - t0) / 400f);
		float pk = Ui.ez((Util.getMillis() - pt) / 250f);
		Ui.GA = k;
		f = Math.min(1f, (height - 8f) / ch);
		int rx = (int) (mx / f);
		int ry = (int) (my / f);
		var ps = g.pose();
		ps.pushMatrix();
		ps.scale(f, f);
		cx = (int) ((width / f - cw) / 2);
		cy = (int) ((height / f - ch) / 2) + Math.round((1f - k) * 12);
		Ui.glow(g, cx, cy, cw, ch, 14, ac, 0.4f);
		Ui.rr(g, cx, cy, cw, ch, 14, Ui.al(Ui.tn(0x090714), 0.95f));
		Ui.ro(g, cx, cy, cw, ch, 14, Ui.al(0xFFFFFF, 0.14f));
		Ui.rim(g, cx, cy, cw, ch, 14, ac, 1f);
		Pg pg = pgs.get(step);
		String h = step == 0 ? "Welcome to Azure" : pg.t;
		Ui.ts(g, h, cx + 24, cy + 22, 16f, 0xFFFFFF, 1f);
		String sc = "Step " + (step + 1) + " of " + pgs.size();
		Ui.tx(g, sc, cx + cw - 24 - Ui.w(sc), cy + 27, 0xFFFFFF, 0.4f);
		float old = Ui.GA;
		Ui.GA = old * (0.4f + 0.6f * pk);
		if (step == 0) colors(g, rx, ry, ac);
		else if (step == 1) look(g, rx, ry, ac);
		else if (step == 2) slist(g, rx, ry, ac);
		else if (step == 3) key(g, rx, ry, ac);
		else options(g, rx, ry, ac, pg);
		Ui.GA = old;
		for (int i = 0; i < pgs.size(); i++) Ui.rr(g, cx + cw / 2 - pgs.size() * 7 + i * 14, cy + ch - 12, 8, 4, 2, Ui.al(i == step ? ac : 0xFFFFFF, i == step ? 1f : 0.2f));
		btn(g, rx, ry, ac, cx + 24, cy + ch - 44, 80, 26, "Skip", false, this::fin);
		if (step > 0) btn(g, rx, ry, ac, cx + 112, cy + ch - 44, 70, 26, "Back", false, () -> go(step - 1));
		btn(g, rx, ry, ac, cx + cw - 24 - 110, cy + ch - 44, 110, 26, step == pgs.size() - 1 ? "Done" : "Next", true, () -> {
			if (step == pgs.size() - 1) fin();
			else go(step + 1);
		});
		Ui.GA = 1f;
		ps.popMatrix();
	}

	void go(int s) {
		step = s;
		lis = false;
		pt = Util.getMillis();
	}

	void wrp(GuiGraphics g, String s, int y, float a) {
		String ln = "";
		for (String wd : s.split(" ")) {
			String nx = ln.isEmpty() ? wd : ln + " " + wd;
			if (Ui.w(nx) > cw - 56 && !ln.isEmpty()) {
				Ui.cx(g, ln, cx + cw / 2, y, 0xFFFFFF, a);
				y += 13;
				nx = wd;
			}
			ln = nx;
		}
		Ui.cx(g, ln, cx + cw / 2, y, 0xFFFFFF, a);
	}

	void colors(GuiGraphics g, int mx, int my, int ac) {
		Ui.tx(g, "Pick your color. You can change it any time in Settings.", cx + 24, cy + 56, 0xFFFFFF, 0.6f);
		int sw = 38;
		int gp = 12;
		int tw = CO.length * sw + (CO.length - 1) * gp;
		int sx = cx + (cw - tw) / 2;
		int sy = cy + 112;
		for (int i = 0; i < CO.length; i++) {
			int x = sx + i * (sw + gp);
			boolean on = (Thm.get() & 0xFFFFFF) == CO[i];
			boolean ov = in(mx, my, x, sy, sw, sw);
			if (on || ov) Ui.glow(g, x, sy, sw, sw, 10, CO[i], on ? 0.8f : 0.4f);
			Ui.rr(g, x, sy, sw, sw, 10, Ui.al(CO[i], 1f));
			if (on) Ui.ro(g, x - 3, sy - 3, sw + 6, sw + 6, 12, Ui.al(0xFFFFFF, 0.9f));
			final int c = CO[i];
			hit(x, sy, sw, sw, () -> {
				Thm.set(c);
				Cfg.sv();
				Snd.p("click");
			});
		}
		wrp(g, "Your accent is used for buttons, glows and the HUD.", cy + 182, 0.4f);
		wrp(g, "Azure starts with everything off. The next steps let you turn on what you need.", cy + 210, 0.5f);
	}

	void look(GuiGraphics g, int mx, int my, int ac) {
		Ui.tx(g, "Pick a background and how the Azure menu looks.", cx + 24, cy + 52, 0xFFFFFF, 0.6f);
		Ui.ts(g, "BACKGROUND", cx + 24, cy + 68, 7.5f, 0xFFFFFF, 0.45f);
		int pw = (cw - 48 - 16) / 3;
		int ph = Math.round(pw * 270f / 480f);
		float t = (Util.getMillis() % 1000000L) / 1000f;
		for (int i = 0; i < Vis.BGN.length; i++) {
			int x = cx + 24 + (i % 3) * (pw + 8);
			int y = cy + 80 + (i / 3) * (ph + 8);
			boolean on = Vis.sel() == i;
			boolean ov = in(mx, my, x, y, pw, ph);
			float ga = Ui.GA;
			var ps = g.pose();
			g.enableScissor(x + 1, y + 1, x + pw - 1, y + ph - 1);
			ps.pushMatrix();
			ps.translate(x, y);
			ps.scale(pw / 480f, pw / 480f);
			Vis.draw(g, i, t, ga, 480, 270, false);
			ps.popMatrix();
			g.disableScissor();
			Ui.GA = ga;
			g.fillGradient(x + 1, y + ph - 16, x + pw - 1, y + ph - 1, Ui.al(0x000000, 0f), Ui.al(0x000000, 0.7f));
			Th.lt(g, Vis.BGN[i], x + 7, y + ph - 8, 9f, on ? Th.lt() : 0xFFFFFF, 1f);
			Th.box(g, x, y, pw, ph, 0, 0f, on || ov ? ac : 0xFFFFFF, on ? 1f : ov ? 0.6f : 0.15f);
			final int ii = i;
			hit(x, y, pw, ph, () -> Cfg.si("bg", ii));
		}
		int my0 = cy + 80 + 2 * (ph + 8) + 8;
		Ui.ts(g, "MENU STYLE", cx + 24, my0, 7.5f, 0xFFFFFF, 0.45f);
		String[][] st = {{"Tiles", "The new grid look"}, {"Classic", "The old list with a sidebar"}};
		int bw = (cw - 48 - 8) / 2;
		for (int i = 0; i < 2; i++) {
			int x = cx + 24 + i * (bw + 8);
			int y = my0 + 12;
			boolean on = Cfg.gi("gui", 0) == i;
			boolean ov = in(mx, my, x, y, bw, 34);
			Th.box(g, x, y, bw, 34, on ? Vis.mix(Th.pn(), ac, 0.25f) : Th.pn(), 0.9f, on || ov ? ac : 0xFFFFFF, on ? 0.9f : ov ? 0.5f : 0.14f);
			Th.lt(g, st[i][0], x + 12, y + 12, 9f, on ? Th.lt() : 0xFFFFFF, 1f);
			Th.lt(g, st[i][1], x + 12, y + 23, 8f, 0xFFFFFF, 0.5f);
			final int ii = i;
			hit(x, y, bw, 34, () -> Cfg.si("gui", ii));
		}
	}

	void slist(GuiGraphics g, int mx, int my, int ac) {
		Ui.tx(g, "How should your server list look? Partner servers are always on top.", cx + 24, cy + 52, 0xFFFFFF, 0.6f);
		String[] ds = {"Partner cards, then rows", "List with a big info panel", "Featured partner, then tiles", "Compact table with columns"};
		int bw = (cw - 48 - 10) / 2;
		int bh = 104;
		for (int i = 0; i < 4; i++) {
			int x = cx + 24 + (i % 2) * (bw + 10);
			int y = cy + 72 + (i / 2) * (bh + 8);
			boolean on = AzMulti.lay() == i;
			boolean ov = in(mx, my, x, y, bw, bh);
			Th.box(g, x, y, bw, bh, on ? Vis.mix(Th.pn(), ac, 0.2f) : Th.pn(), 0.9f, on || ov ? ac : 0xFFFFFF, on ? 0.9f : ov ? 0.5f : 0.14f);
			int px = x + 10;
			int py = y + 10;
			int pw = bw - 20;
			int pa = Ui.al(Vis.mix(ac, 0xFFFFFF, 0.3f), 0.55f);
			int pb = Ui.al(0xFFFFFF, 0.14f);
			if (i == 0) {
				int cw2 = (pw - 8) / 3;
				for (int k = 0; k < 3; k++) Ui.rr(g, px + k * (cw2 + 4), py, cw2, 20, 3, pa);
				for (int k = 0; k < 4; k++) Ui.rr(g, px, py + 26 + k * 10, pw, 7, 2, pb);
			} else if (i == 1) {
				for (int k = 0; k < 6; k++) Ui.rr(g, px, py + k * 10, pw * 55 / 100, 7, 2, k == 1 ? pa : pb);
				Ui.rr(g, px + pw * 55 / 100 + 4, py, pw - pw * 55 / 100 - 4, 58, 3, pb);
			} else if (i == 2) {
				Ui.rr(g, px, py, pw, 18, 3, pa);
				int tw2 = (pw - 12) / 4;
				for (int k = 0; k < 8; k++) Ui.rr(g, px + (k % 4) * (tw2 + 4), py + 22 + (k / 4) * 18, tw2, 15, 3, pb);
			} else {
				g.fill(px, py + 4, px + pw, py + 5, pb);
				for (int k = 0; k < 7; k++) {
					Ui.rr(g, px, py + 8 + k * 7, pw, 5, 1, k % 2 == 0 ? pb : Ui.al(0xFFFFFF, 0.07f));
					g.fill(px + pw - 12, py + 9 + k * 7, px + pw - 4, py + 12 + k * 7, pa);
				}
			}
			Th.lt(g, AzMulti.LN[i] + (i == 2 ? "  (default)" : ""), x + 10, y + bh - 22, 9f, on ? Th.lt() : 0xFFFFFF, 1f);
			Th.lt(g, ds[i], x + 10, y + bh - 10, 8f, 0xFFFFFF, 0.5f);
			final int ii = i;
			hit(x, y, bw, bh, () -> Cfg.si("mp.lay", ii));
		}
	}

	void key(GuiGraphics g, int mx, int my, int ac) {
		wrp(g, "This key opens the Azure menu in game.", cy + 56, 0.6f);
		String s = lis ? "Press a key..." : Wc.key.getTranslatedKeyMessage().getString();
		int kw = Math.max(110, Ui.w(s) + 40);
		int kx = cx + (cw - kw) / 2;
		int ky = cy + 120;
		boolean ov = in(mx, my, kx, ky, kw, 34);
		Ui.rr(g, kx, ky, kw, 34, 10, Ui.al(lis ? ac : 0xFFFFFF, lis ? 0.3f : 0.08f + (ov ? 0.05f : 0f)));
		Ui.ro(g, kx, ky, kw, 34, 10, Ui.al(lis ? ac : 0xFFFFFF, lis ? 0.9f : 0.22f));
		Ui.cx(g, s, cx + cw / 2, ky + 13, 0xFFFFFF, 1f);
		hit(kx, ky, kw, 34, () -> lis = true);
		wrp(g, "Click the key to change it", cy + 172, 0.4f);
		wrp(g, "Every other Azure key starts unbound. Set them in the Minecraft controls if you want them.", cy + 210, 0.4f);
	}

	void options(GuiGraphics g, int mx, int my, int ac, Pg pg) {
		Ui.tx(g, pg.s, cx + 24, cy + 56, 0xFFFFFF, 0.6f);
		int cols = 2;
		int gw = (cw - 48 - 10) / cols;
		int rh = 30;
		for (int i = 0; i < pg.o.size(); i++) {
			Op o = pg.o.get(i);
			int x = cx + 24 + (i % cols) * (gw + 10);
			int y = cy + 78 + (i / cols) * (rh + 4);
			boolean on = sel.get(o.id);
			boolean ov = in(mx, my, x, y, gw, rh);
			Ui.rr(g, x, y, gw, rh, 10, Ui.al(on ? ac : 0xFFFFFF, on ? 0.2f : 0.05f + (ov ? 0.05f : 0f)));
			Ui.ro(g, x, y, gw, rh, 10, Ui.al(on ? ac : 0xFFFFFF, on ? 0.8f : 0.12f));
			Ui.rr(g, x + 10, y + 8, 14, 14, 4, Ui.al(on ? ac : 0xFFFFFF, on ? 1f : 0.12f));
			if (on) Ic.dr(g, "check", x + 17, y + 15, 10, Ui.al(0xFFFFFF, 1f));
			Ui.ts(g, o.lb, x + 32, y + 5, 9f, 0xFFFFFF, 1f);
			String ds = o.ds;
			while (ds.length() > 3 && Ui.w(ds, 7.5f) > gw - 40) ds = ds.substring(0, ds.length() - 1);
			Ui.ts(g, ds, x + 32, y + 17, 7.5f, 0xFFFFFF, 0.5f);
			hit(x, y, gw, rh, () -> {
				sel.put(o.id, !sel.get(o.id));
				Snd.p("click");
			});
		}
		if (step == 4) {
			btn(g, mx, my, ac, cx + cw - 24 - 90, cy + 50, 90, 20, "Clear all", false, () -> {
				for (Pg p : pgs) for (Op o : p.o) sel.put(o.id, false);
			});
			btn(g, mx, my, ac, cx + cw - 24 - 90 - 8 - 120, cy + 50, 120, 20, "Pick the basics", false, this::basics);
		}
	}

	void basics() {
		for (String s : REC) if (sel.containsKey(s)) sel.put(s, true);
	}

	void fin() {
		for (Pg p : pgs) for (Op o : p.o) {
			boolean v = sel.get(o.id);
			Mod m = Mod.get(o.id);
			try {
				if (m != null && m.on.getAsBoolean() != v) m.put(v);
			} catch (Throwable e) {
			}
		}
		Cfg.c.fs = true;
		Cfg.sv();
		minecraft.setScreen(par);
	}

	public boolean mouseClicked(MouseButtonEvent e, boolean dbl) {
		int mx = (int) (e.x() / f);
		int my = (int) (e.y() / f);
		boolean was = lis;
		for (int i = hits.size() - 1; i >= 0; i--) {
			Ht h = hits.get(i);
			if (in(mx, my, h.x, h.y, h.w, h.h)) {
				Snd.p("click");
				h.r.run();
				return true;
			}
		}
		if (was) lis = false;
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
