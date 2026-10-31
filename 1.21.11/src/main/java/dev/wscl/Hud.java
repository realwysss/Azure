package dev.wscl;

import dev.wysl.Thm;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.lwjgl.glfw.GLFW;

public class Hud {
	public static class El {
		public final String id;
		public final String nm;
		final IntSupplier gx;
		final IntSupplier gy;
		final IntConsumer sx;
		final IntConsumer sy;
		final int dx;
		final int dy;
		final int cx;
		final int cy;
		public float x;
		public float y;
		public float w;
		public float h;

		El(String id, String nm, IntSupplier gx, IntConsumer sx, IntSupplier gy, IntConsumer sy, int dx, int dy, int cx, int cy) {
			this.id = id;
			this.nm = nm;
			this.gx = gx;
			this.gy = gy;
			this.sx = sx;
			this.sy = sy;
			this.dx = dx;
			this.dy = dy;
			this.cx = cx;
			this.cy = cy;
		}

		static final java.util.Map<String, int[]> GRID = java.util.Map.ofEntries(
				java.util.Map.entry("fps", new int[]{24, 11}), java.util.Map.entry("ping", new int[]{50, 11}), java.util.Map.entry("cps", new int[]{76, 11}),
				java.util.Map.entry("xyz", new int[]{24, 19}), java.util.Map.entry("spotify", new int[]{50, 19}), java.util.Map.entry("clock", new int[]{76, 19}),
				java.util.Map.entry("keys", new int[]{6, 32}), java.util.Map.entry("armor", new int[]{6, 66}),
				java.util.Map.entry("pots", new int[]{94, 30}), java.util.Map.entry("invhud", new int[]{94, 62}),
				java.util.Map.entry("pops", new int[]{50, 28}), java.util.Map.entry("totem", new int[]{50, 60}), java.util.Map.entry("shield", new int[]{50, 67}),
				java.util.Map.entry("reach", new int[]{42, 75}), java.util.Map.entry("combo", new int[]{58, 75}),
				java.util.Map.entry("mark", new int[]{50, 0}), java.util.Map.entry("fig", new int[]{80, 45}), java.util.Map.entry("lyrics", new int[]{50, 82}));

		static El mk(String id, String nm, int cx, int cy) {
			int i = ELS.size();
			int[] gp = GRID.get(id);
			int dx = gp != null ? gp[0] : i % 3 == 0 ? 24 : i % 3 == 1 ? 50 : 76;
			int dy = gp != null ? gp[1] : Math.min(90, 8 + i / 3 * 14);
			return new El(id, nm, () -> Cfg.gi("x." + id, Cfg.gi("hdc", 0) == 1 ? dx : cx), v -> Cfg.si("x." + id, v), () -> Cfg.gi("y." + id, Cfg.gi("hdc", 0) == 1 ? dy : cy), v -> Cfg.si("y." + id, v), dx, dy, cx, cy);
		}

		public void classic() {
			sx.accept(cx);
			sy.accept(cy);
		}

		public void pos(int px, int py) {
			sx.accept(px);
			sy.accept(py);
		}

		public void reset() {
			boolean n = Cfg.gi("hdc", 0) == 1;
			sx.accept(n ? dx : cx);
			sy.accept(n ? dy : cy);
		}

		public boolean has(double mx, double my) {
			return w > 0 && mx >= x && mx <= x + w && my >= y && my <= y + h;
		}
	}

	public static final List<El> ELS = new ArrayList<>();
	public static final El FPS = add(El.mk("fps", "FPS", 1, 1));
	public static final El PING = add(El.mk("ping", "Ping", 1, 7));
	public static final El CPS = add(El.mk("cps", "CPS", 1, 13));
	public static final El XYZ = add(El.mk("xyz", "Coordinates", 1, 19));
	public static final El CLOCK = add(El.mk("clock", "Clock", 1, 25));
	public static final El DIR = add(El.mk("dir", "Direction", 50, 6));
	public static final El POTS = add(El.mk("pots", "Potions", 1, 34));
	public static final El KEYS = add(El.mk("keys", "Keystrokes", 1, 98));
	public static final El SPOT = add(El.mk("spotify", "Spotify", 99, 1));
	public static final El INV = add(El.mk("invhud", "Inventory", 99, 22));
	public static final El ARMOR = add(El.mk("armor", "Armor", 99, 62));
	public static final El TOTEM = add(El.mk("totem", "Totems", 99, 96));
	public static final El POPS = add(El.mk("pops", "Totem pops", 62, 14));
	public static final El REACH = add(El.mk("reach", "Reach", 44, 76));
	public static final El COMBO = add(El.mk("combo", "Combo", 56, 76));
	public static final El MARK = add(El.mk("mark", "Watermark", 50, 0));
	public static final El FIG = add(El.mk("fig", "3D Player", 90, 40));
	public static final El SHIELD = add(El.mk("shield", "Shield", 50, 68));
	public static final El LYR = add(El.mk("lyrics", "Lyrics", 50, 82));

	static El add(El e) {
		ELS.add(e);
		return e;
	}

	public static boolean ed;
	static El cur;
	public static float reach = -1;
	public static int combo;
	static final ArrayDeque<Long> L = new ArrayDeque<>();
	static final ArrayDeque<Long> R = new ArrayDeque<>();
	static boolean lp;
	static boolean rp;
	static final DateTimeFormatter T = DateTimeFormatter.ofPattern("HH:mm");
	static final Map<String, Integer> MX = new HashMap<>();

	static void clicks(Minecraft m) {
		long n = System.currentTimeMillis();
		boolean l = m.mouseHandler.isLeftPressed();
		boolean r = m.mouseHandler.isRightPressed();
		if (l && !lp) L.add(n);
		if (r && !rp) R.add(n);
		lp = l;
		rp = r;
		while (!L.isEmpty() && n - L.peekFirst() > 1000) L.poll();
		while (!R.isEmpty() && n - R.peekFirst() > 1000) R.poll();
	}

	static long tl;

	public static int ac() {
		long t = System.currentTimeMillis();
		if (t - tl > 400) {
			tl = t;
			Thm.ld();
		}
		int b = Thm.ac;
		int m = Cfg.c.am;
		if (m == 1) return 0xFF000000 | java.awt.Color.HSBtoRGB((System.currentTimeMillis() % 6000) / 6000f, 0.6f, 1f);
		if (m == 2) return 0xFF000000 | Vis.mix(b & 0xFFFFFF, 0xFFFFFF, 0.5f + 0.5f * (float) Math.sin(System.currentTimeMillis() / 500.0));
		return b;
	}

	static int tc() {
		return Cfg.gi("tcm", 0) == 0 ? ac() & 0xFFFFFF : Cfg.c.tc;
	}

	static float tm() {
		return (System.currentTimeMillis() % 1000000L) / 1000f;
	}

	static int fx(int i, int n, int base) {
		int te = Cfg.c.te;
		int ac = ac() & 0xFFFFFF;
		float t = tm();
		switch (te) {
			case 1:
				return 0xFF000000 | java.awt.Color.HSBtoRGB((t * 0.2f) % 1f, 0.6f, 1f);
			case 2:
				return 0xFF000000 | java.awt.Color.HSBtoRGB((t * 0.25f + i * 0.07f) % 1f, 0.6f, 1f);
			case 3:
				return 0xFF000000 | Vis.mix(base & 0xFFFFFF, ac, 0.5f + 0.5f * (float) Math.sin(t * 3f));
			case 4:
				return 0xFF000000 | Vis.mix(ac, base & 0xFFFFFF, n <= 1 ? 0f : i / (float) (n - 1));
			case 5: {
				int hsh = (i * 7919 + (int) (t * 7f) * 104729) & 0x7FFFFFFF;
				return hsh % 9 == 0 ? 0xFFFFFFFF : 0xFF000000 | (base & 0xFFFFFF);
			}
			default:
				return 0xFF000000 | (base & 0xFFFFFF);
		}
	}

	static int ta(int col) {
		if (cur == null) return col;
		int t = gi(cur.id, "top", 100);
		if (t >= 100) return col;
		int a = Math.round(((col >>> 24) & 255) * t / 100f);
		return (a << 24) | (col & 0xFFFFFF);
	}

	static void dt(GuiGraphics g, String s, int x, int y, int col) {
		Tf.ds(g, s, x, y, ta(col), Cfg.c.ds);
	}

	static void dv(GuiGraphics g, String s, int x, int y, int base) {
		Font f = Minecraft.getInstance().font;
		int te = Cfg.c.te;
		if (te == 0) {
			Tf.ds(g, s, x, y, ta(0xFF000000 | (base & 0xFFFFFF)), Cfg.c.ds);
			return;
		}
		if (te == 1 || te == 3) {
			Tf.ds(g, s, x, y, ta(fx(0, 1, base)), Cfg.c.ds);
			return;
		}
		int cx = x;
		for (int i = 0; i < s.length(); i++) {
			String ch = String.valueOf(s.charAt(i));
			Tf.ds(g, ch, cx, y, ta(fx(i, s.length(), base)), Cfg.c.ds);
			cx += Tf.fw(ch);
		}
	}

	static int tw(String s) {
		return Tf.fw(s);
	}

	static String fit(String s, int mx) {
		if (tw(s) <= mx) return s;
		while (s.length() > 1 && tw(s + "..") > mx) s = s.substring(0, s.length() - 1);
		return s + "..";
	}

	static void pn(GuiGraphics g, int x, int y, int w, int h) {
		Cfg c = Cfg.c;
		float o = c.op / 100f;
		int ac = ac() & 0xFFFFFF;
		if (c.bs == 3) return;
		if (cur != null) {
			if (gi(cur.id, "pnl", 1) == 0) return;
			o *= gi(cur.id, "pop", 100) / 100f;
		}
		if (c.bs == 2) {
			Ui.rr(g, x, y, w, h, 4, Ui.al(Ui.tn(0x0C0510), o * 0.35f));
			Ui.ro(g, x, y, w, h, 4, Ui.al(ac, 0.9f));
			return;
		}
		int rd = Math.min(h / 2, 8);
		Ui.glow(g, x, y, w, h, rd, ac, 0.16f * o);
		Ui.rr(g, x, y, w, h, rd, Ui.al(Ui.tn(0x0A0714), o));
		if (h >= 12) g.fillGradient(x + rd, y + 1, x + w - rd, y + 1 + h / 2, Ui.al(ac, 0.12f * o), Ui.al(ac, 0f));
		Ui.ro(g, x, y, w, h, rd, Ui.al(ac, 0.34f));
		if (c.bs == 1) Ui.rr(g, x + rd, y + h - 2, w - rd * 2, 2, 1, Ui.al(ac, 0.95f));
	}

	static int pc(float p) {
		return p > 0.5f ? Vis.mix(0xFFD24A, 0x5CE08A, (p - 0.5f) * 2f) : Vis.mix(0xFF5050, 0xFFD24A, p * 2f);
	}

	static int gi(String id, String k, int d) {
		return Cfg.gi(id + "." + k, d);
	}

	static boolean gb(String id, String k, boolean d) {
		return Cfg.gi(id + "." + k, d ? 1 : 0) == 1;
	}

	static float es(El e) {
		return Cfg.c.sc / 100f * gi(e.id, "sc", 100) / 100f;
	}

	static int[] place(El e, int bw, int bh, int w, int h, float s) {
		cur = e;
		float x = (w / s - bw) * e.gx.getAsInt() / 100f;
		float y = (h / s - bh) * e.gy.getAsInt() / 100f;
		e.x = x * s;
		e.y = y * s;
		e.w = bw * s;
		e.h = bh * s;
		return new int[]{(int) x, (int) y};
	}

	static boolean live(String id) {
		return Mod.live(id);
	}

	static void stat(GuiGraphics g, El e, String label, String val, int vc, int w, int h) {
		boolean pill = gi(e.id, "st", 0) == 0;
		int lb = gi(e.id, "lb", 0);
		int gap = lb == 2 ? 0 : tw(label) + 5;
		int bw = gap + tw(val) + (pill ? 30 : 1);
		int bh = pill ? 16 : 10;
		float s = es(e);
		int[] p = place(e, bw, bh, w, h, s);
		var ps = g.pose();
		ps.pushMatrix();
		ps.scale(s, s);
		if (pill) pn(g, p[0], p[1], bw, bh);
		int tx = p[0] + (pill ? 8 : 1);
		int ty = p[1] + (pill ? 4 : 1);
		if (pill) {
			Ui.rr(g, tx, p[1] + 6, 4, 4, 2, 0xFF000000 | (vc & 0xFFFFFF));
			tx += 9;
		}
		int la = Ui.al(0xFFFFFF, pill ? 0.55f : 0.9f);
		if (lb == 0) {
			dt(g, label, tx, ty, la);
			dv(g, val, tx + tw(label) + 5, ty, vc);
		} else {
			dv(g, val, tx, ty, vc);
			if (lb == 1) dt(g, label, tx + tw(val) + 5, ty, la);
		}
		ps.popMatrix();
	}

	static final String[] DN = {"South", "South West", "West", "North West", "North", "North East", "East", "South East"};
	static final String[] AX = {"+Z", "-X", "-Z", "+X"};

	static String fm(double v, boolean dc) {
		return dc ? String.format(java.util.Locale.ROOT, "%.1f", v) : String.valueOf((int) Math.floor(v));
	}

	static void xyz(GuiGraphics g, Minecraft m, int w, int h) {
		var p = m.player;
		boolean dc = gb("xyz", "dc", false);
		if (gi("xyz", "lk", 0) == 1) {
			stat(g, XYZ, "XYZ", fm(p.getX(), dc) + " " + fm(p.getY(), dc) + " " + fm(p.getZ(), dc), tc(), w, h);
			return;
		}
		float yw = net.minecraft.util.Mth.wrapDegrees(p.getYRot());
		List<String[]> rw = new ArrayList<>();
		rw.add(new String[]{"X", fm(p.getX(), dc)});
		rw.add(new String[]{"Y", fm(p.getY(), dc)});
		rw.add(new String[]{"Z", fm(p.getZ(), dc)});
		if (gb("xyz", "nt", false) && m.level != null) {
			boolean nth = m.level.dimension() == net.minecraft.world.level.Level.NETHER;
			double k = nth ? 8 : 0.125;
			rw.add(new String[]{nth ? "OW" : "NE", fm(p.getX() * k, false) + " " + fm(p.getZ() * k, false)});
		}
		if (gb("xyz", "fc", true)) rw.add(new String[]{"F", DN[Math.floorMod(Math.round(yw / 45f), 8)] + "  " + AX[Math.floorMod(Math.round(yw / 90f), 4)]});
		if (gb("xyz", "bi", false) && m.level != null) {
			var bk = m.level.getBiome(p.blockPosition()).unwrapKey();
			String bn = bk.isPresent() ? bk.get().identifier().getPath().replace('_', ' ') : "?";
			rw.add(new String[]{"B", bn.isEmpty() ? bn : Character.toUpperCase(bn.charAt(0)) + bn.substring(1)});
		}
		int lw = 0, vw = 0;
		for (String[] q : rw) {
			lw = Math.max(lw, tw(q[0]));
			vw = Math.max(vw, tw(q[1]));
		}
		int rh = 11;
		int bw = lw + vw + 22;
		int bh = rw.size() * rh + 8;
		float s = es(XYZ);
		int[] pp = place(XYZ, bw, bh, w, h, s);
		var ps = g.pose();
		ps.pushMatrix();
		ps.scale(s, s);
		pn(g, pp[0], pp[1], bw, bh);
		int ac = ac() & 0xFFFFFF;
		int tc = tc();
		for (int i = 0; i < rw.size(); i++) {
			int y = pp[1] + 5 + i * rh;
			Ui.rr(g, pp[0] + 6, y + 2, 3, 3, 1, 0xFF000000 | ac);
			dt(g, rw.get(i)[0], pp[0] + 12, y, Ui.al(Vis.mix(0xFFFFFF, ac, 0.5f), 0.8f));
			dv(g, rw.get(i)[1], pp[0] + 16 + lw, y, tc);
		}
		ps.popMatrix();
	}

	static final String[] CP = {"N", "NE", "E", "SE", "S", "SW", "W", "NW"};

	static void dir(GuiGraphics g, Minecraft m, int w, int h) {
		float yw = net.minecraft.util.Mth.wrapDegrees(m.player.getYRot());
		float hd = ((yw + 180) % 360 + 360) % 360;
		boolean dg = gb("dir", "dg", true);
		int ac = ac() & 0xFFFFFF;
		int tc = tc();
		String ds = Math.round(hd) % 360 + "°";
		if (gi("dir", "lk", 0) == 1) {
			String t = CP[Math.floorMod(Math.round(hd / 45f), 8)] + (dg ? "  " + ds : "");
			stat(g, DIR, "FACING", t, tc, w, h);
			return;
		}
		int bw = gi("dir", "wd", 180);
		int bh = dg ? 30 : 20;
		float s = es(DIR);
		int[] p = place(DIR, bw, bh, w, h, s);
		var ps = g.pose();
		ps.pushMatrix();
		ps.scale(s, s);
		pn(g, p[0], p[1], bw, 20);
		int cx = p[0] + bw / 2;
		float px = (bw - 16) / 180f;
		g.enableScissor(p[0] + 2, p[1], p[0] + bw - 2, p[1] + 20);
		for (int d = 0; d < 360; d += 15) {
			float o = d - hd;
			if (o > 180) o -= 360;
			if (o < -180) o += 360;
			if (Math.abs(o) > 92) continue;
			float a = 1f - Math.abs(o) / 92f;
			a = a * a * (3 - 2 * a);
			int x = Math.round(cx + o * px);
			if (d % 45 == 0) {
				String l = CP[d / 45];
				boolean big = d % 90 == 0;
				int col = d == 0 ? Vis.mix(0xFF5A5A, 0xFFFFFF, 0.2f) : big ? tc : Vis.mix(tc, 0xFFFFFF, 0.3f);
				dt(g, l, x - tw(l) / 2, p[1] + 6, Ui.al(col, (big ? 1f : 0.7f) * a));
			} else g.fill(x, p[1] + 8, x + 1, p[1] + 12, Ui.al(0xFFFFFF, 0.35f * a));
		}
		g.disableScissor();
		g.fill(cx - 4, p[1] + 17, cx + 5, p[1] + 18, Ui.al(ac, 0.95f));
		g.fill(cx - 1, p[1] + 15, cx + 2, p[1] + 17, Ui.al(ac, 0.95f));
		if (dg) {
			int dw = tw(ds) + 10;
			Ui.rr(g, cx - dw / 2, p[1] + 20, dw, 10, 4, Ui.al(0x0A0714, 0.55f * Cfg.c.op / 100f));
			dv(g, ds, cx - tw(ds) / 2, p[1] + 21, tc);
		}
		ps.popMatrix();
	}

	static void fig(GuiGraphics g, Minecraft m, int w, int h, float pt) {
		float s = es(FIG);
		int bh = gi("fig", "sz", 100);
		int bw = Math.round(bh * 0.62f);
		int[] p = place(FIG, bw, bh, w, h, s);
		if (gb("fig", "pn", true)) {
			var ps = g.pose();
			ps.pushMatrix();
			ps.scale(s, s);
			pn(g, p[0], p[1], bw, bh);
			ps.popMatrix();
		}
		if (gb("fig", "dsh", false)) {
			var ps = g.pose();
			ps.pushMatrix();
			ps.scale(s, s);
			float da = gi("fig", "dss", 50) / 100f;
			int fcx = p[0] + bw / 2;
			int fcy = p[1] + Math.round(bh * 0.92f);
			for (int i = 0; i < 5; i++) {
				int ew = Math.round(bw * (1.05f - i * 0.15f));
				int eh = Math.max(2, Math.round(bh * (0.1f - i * 0.016f)));
				Ui.rr(g, fcx - ew / 2, fcy - eh / 2, ew, eh, eh / 2, Ui.al(0x000000, 0.2f * da));
			}
			ps.popMatrix();
		}
		try {
			var st = m.getEntityRenderDispatcher().extractEntity(m.player, pt);
			if (st instanceof dev.htcl.HcSt hf) hf.hcFig(true);
			float ang = gi("fig", "ang", 0);
			if (gb("fig", "spin", false)) ang += (System.currentTimeMillis() % 6000L) / 6000f * 360f;
			if (st instanceof net.minecraft.client.renderer.entity.state.LivingEntityRenderState ls) {
				ls.bodyRot = 180f + ang;
				ls.lightCoords = 0xF000F0;
				ls.outlineColor = 0;
				ls.shadowPieces.clear();
				ls.displayFireAnimation = false;
			}
			var q = new org.joml.Quaternionf().rotateZ((float) Math.PI);
			var cam = new org.joml.Quaternionf().rotateX(-0.12f);
			q.mul(cam);
			int x0 = Math.round(p[0] * s);
			int y0 = Math.round(p[1] * s);
			int x1 = Math.round((p[0] + bw) * s);
			int y1 = Math.round((p[1] + bh) * s);
			int ex = Math.round((x1 - x0) * 0.9f);
			int ey = Math.round((y1 - y0) * 0.45f);
			g.submitEntityRenderState(st, (y1 - y0) / 2.15f, new org.joml.Vector3f(0f, m.player.getBbHeight() / 2f + 0.0625f, 0f), q, cam, x0 - ex, y0 - ey, x1 + ex, y1 + ey);
		} catch (Throwable e) {
		}
	}

	static String roman(int n) {
		String[] r = {"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"};
		return n >= 0 && n < r.length ? r[n] : String.valueOf(n);
	}

	public static void draw(GuiGraphics g, DeltaTracker dtk) {
		Cfg c = Cfg.c;
		Minecraft m = Minecraft.getInstance();
		for (El e : ELS) e.w = 0;
		if (m.player != null && live("amb")) Amb.hud(g, g.guiWidth(), g.guiHeight());
		if (!c.hud || m.player == null || m.options.hideGui) return;
		clicks(m);
		int w = g.guiWidth();
		int h = g.guiHeight();
		int tc = tc();
		if (live("fps")) {
			int f = m.getFps();
			int col = gb("fps", "col", true) ? (f >= 120 ? 0x5CE08A : f >= 60 ? 0xFFD24A : 0xFF5050) : tc;
			stat(g, FPS, "FPS", String.valueOf(f), col, w, h);
		}
		if (live("ping")) {
			PlayerInfo pi = m.getConnection() == null ? null : m.getConnection().getPlayerInfo(m.player.getUUID());
			stat(g, PING, "PING", (pi == null ? 0 : pi.getLatency()) + " ms", tc, w, h);
		}
		if (live("cps")) stat(g, CPS, "CPS", L.size() + " | " + R.size(), tc, w, h);
		if (live("xyz")) xyz(g, m, w, h);
		if (live("dir")) dir(g, m, w, h);
		if (live("clock")) stat(g, CLOCK, "TIME", LocalTime.now().format(T), tc, w, h);
		if (live("reach")) stat(g, REACH, "REACH", reach < 0 ? "-" : String.format("%.2f", reach), tc, w, h);
		if (live("combo")) stat(g, COMBO, "COMBO", String.valueOf(combo), tc, w, h);
		if (live("pots")) pots(g, m, w, h);
		if (live("keys")) keys(g, m, w, h);
		if (live("spotify")) spot(g, m, w, h);
		if (live("lyrics")) Lyr.hud(g, m, w, h);
		if (live("fig")) fig(g, m, w, h, dtk.getGameTimeDeltaPartialTick(false));
		if (live("invhud")) invhud(g, m, w, h);
		if (live("armor")) armor(g, m, w, h);
		if (live("totem")) totem(g, m, w, h);
		extras(g, m, w, h);
	}

	static void pots(GuiGraphics g, Minecraft m, int w, int h) {
		int tc = tc();
		int ac = ac() & 0xFFFFFF;
		List<MobEffectInstance> mi = new ArrayList<>(m.player.getActiveEffects());
		Set<String> keep = new HashSet<>();
		for (MobEffectInstance e : mi) {
			String k = e.getEffect().getRegisteredName();
			keep.add(k);
			if (!e.isInfiniteDuration()) MX.merge(k, e.getDuration(), Math::max);
		}
		MX.keySet().retainAll(keep);
		boolean smp = mi.isEmpty() && ed;
		if (mi.isEmpty() && !smp) return;
		boolean ico = gb("pots", "ico", true);
		boolean bar = gb("pots", "bar", true);
		int rh = 20;
		int n = smp ? 2 : mi.size();
		String[] nm = new String[n];
		String[] tmx = new String[n];
		int[] ds = new int[n];
		float[] pr = new float[n];
		int lw = 0;
		for (int i = 0; i < n; i++) {
			if (smp) {
				nm[i] = i == 0 ? "Speed II" : "Strength";
				tmx[i] = i == 0 ? "3:20" : "0:08";
				ds[i] = i == 0 ? 4000 : 160;
				pr[i] = i == 0 ? 0.7f : 0.1f;
			} else {
				MobEffectInstance e = mi.get(i);
				String nn = e.getEffect().value().getDisplayName().getString();
				if (e.getAmplifier() > 0) nn += " " + roman(e.getAmplifier() + 1);
				nm[i] = nn;
				int sec = e.getDuration() / 20;
				tmx[i] = e.isInfiniteDuration() ? "inf" : (sec / 60) + ":" + String.format("%02d", sec % 60);
				ds[i] = e.isInfiniteDuration() ? 99999 : e.getDuration();
				int mx = MX.getOrDefault(e.getEffect().getRegisteredName(), Math.max(1, e.getDuration()));
				pr[i] = e.isInfiniteDuration() ? 1f : Math.max(0f, Math.min(1f, e.getDuration() / (float) mx));
			}
			lw = Math.max(lw, tw(nm[i]) + tw(tmx[i]) + 12);
		}
		int bw = lw + (ico ? 28 : 12);
		int bh = n * rh + 4;
		float s = es(POTS);
		int[] p = place(POTS, bw, bh, w, h, s);
		var ps = g.pose();
		ps.pushMatrix();
		ps.scale(s, s);
		pn(g, p[0], p[1], bw, bh);
		for (int i = 0; i < n; i++) {
			int ry = p[1] + 2 + i * rh;
			int tx = p[0] + 6;
			if (ico) {
				if (!smp) g.blitSprite(RenderPipelines.GUI_TEXTURED, net.minecraft.client.gui.Gui.getMobEffectSprite(mi.get(i).getEffect()), p[0] + 4, ry + 2, 16, 16);
				else Ui.rr(g, p[0] + 5, ry + 3, 14, 14, 4, Ui.al(i == 0 ? 0x33C1FF : 0xFFB020, 0.9f));
				tx = p[0] + 24;
			}
			dv(g, nm[i], tx, ry + 4, tc);
			boolean low = ds[i] < 200;
			int tcol = low ? (System.currentTimeMillis() / 300 % 2 == 0 ? 0xFFFF5050 : 0xFFFFA0A0) : Ui.al(Vis.mix(0xFFFFFF, ac, 0.5f), 0.95f);
			dt(g, tmx[i], p[0] + bw - 6 - tw(tmx[i]), ry + 4, tcol);
			if (bar) {
				int bx = tx;
				int bwid = bw - (tx - p[0]) - 6;
				g.fill(bx, ry + 15, bx + bwid, ry + 16, Ui.al(0xFFFFFF, 0.12f));
				g.fill(bx, ry + 15, bx + Math.round(bwid * pr[i]), ry + 16, Ui.al(low ? 0xFF5050 : ac, 0.95f));
			}
		}
		ps.popMatrix();
	}

	static final String[] KR1 = {"Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P"};
	static final String[] KR2 = {"A", "S", "D", "F", "G", "H", "J", "K", "L"};
	static final String[] KR3 = {"Z", "X", "C", "V", "B", "N", "M"};

	static boolean kd(Minecraft m, int code) {
		return GLFW.glfwGetKey(m.getWindow().handle(), code) == GLFW.GLFW_PRESS;
	}

	static void keys(GuiGraphics g, Minecraft m, int w, int h) {
		int lay = gi("keys", "lay", 2);
		int k = gi("keys", "kz", 18);
		int gp = 2;
		boolean cp = gb("keys", "cp", true);
		float s = es(KEYS);
		int cols = lay == 3 ? 10 : 3;
		int bw0 = cols * k + (cols - 1) * gp;
		int bw = bw0;
		int bh;
		if (lay == 0) bh = 2 * k + gp;
		else if (lay == 1) bh = 3 * k + 2 * gp;
		else if (lay == 2) bh = 3 * k + 3 * gp + 10;
		else bh = 4 * k + 3 * gp;
		boolean nm = gb("keys", "num", false);
		int nk = lay == 3 ? k : Math.max(12, k * 2 / 3);
		int ex = nm ? nk + gp : 0;
		int ox = 0;
		if (nm) {
			bw = Math.max(bw0, 9 * nk + 8 * gp);
			ox = (bw - bw0) / 2;
			bh += ex;
		}
		int[] p = place(KEYS, bw, bh, w, h, s);
		var ps = g.pose();
		ps.pushMatrix();
		ps.scale(s, s);
		int ix = p[0];
		int iy = p[1];
		if (nm) {
			int nx = ix + (bw - (9 * nk + 8 * gp)) / 2;
			for (int i = 0; i < 9; i++) key(g, m.options.keyHotbarSlots[i].isDown(), String.valueOf(i + 1), nx + i * (nk + gp), iy, nk, nk);
			iy += ex;
		}
		ix += ox;
		bw = bw0;
		if (lay < 3) {
			key(g, m.options.keyUp.isDown(), "W", ix + k + gp, iy, k, k);
			key(g, m.options.keyLeft.isDown(), "A", ix, iy + k + gp, k, k);
			key(g, m.options.keyDown.isDown(), "S", ix + k + gp, iy + k + gp, k, k);
			key(g, m.options.keyRight.isDown(), "D", ix + (k + gp) * 2, iy + k + gp, k, k);
			if (lay >= 1) {
				int hw = (bw - gp) / 2;
				key(g, m.options.keyAttack.isDown(), cp ? "L " + L.size() : "L", ix, iy + (k + gp) * 2, hw, k);
				key(g, m.options.keyUse.isDown(), cp ? "R " + R.size() : "R", ix + hw + gp, iy + (k + gp) * 2, bw - hw - gp, k);
			}
			if (lay >= 2) key(g, m.options.keyJump.isDown(), "", ix, iy + (k + gp) * 3, bw, 10);
		} else {
			String[][] rs = {KR1, KR2, KR3};
			int[] off = {0, k / 2, k + k / 2};
			for (int r = 0; r < 3; r++) for (int i = 0; i < rs[r].length; i++) {
				int code = GLFW.GLFW_KEY_A + (rs[r][i].charAt(0) - 'A');
				key(g, kd(m, code), rs[r][i], ix + off[r] + i * (k + gp), iy + r * (k + gp), k, k);
			}
			int by = iy + 3 * (k + gp);
			int cw = k * 2 + gp;
			key(g, kd(m, GLFW.GLFW_KEY_LEFT_CONTROL), "Ctrl", ix, by, cw, k);
			key(g, kd(m, GLFW.GLFW_KEY_LEFT_SHIFT), "Shift", ix + cw + gp, by, cw, k);
			int sw = k * 3 + gp * 2;
			key(g, kd(m, GLFW.GLFW_KEY_SPACE), "", ix + (cw + gp) * 2, by, sw, k);
			int mw = Math.max(k, (bw - (cw + gp) * 2 - sw - gp * 2) / 2);
			key(g, m.options.keyAttack.isDown(), cp ? "L" + L.size() : "L", ix + (cw + gp) * 2 + sw + gp, by, mw, k);
			key(g, m.options.keyUse.isDown(), cp ? "R" + R.size() : "R", ix + (cw + gp) * 2 + sw + gp * 2 + mw, by, mw, k);
		}
		ps.popMatrix();
	}

	static void key(GuiGraphics g, boolean d, String t, int x, int y, int w, int h) {
		int ac = ac() & 0xFFFFFF;
		if (d) Ui.glow(g, x, y, w, h, 4, ac, 0.9f);
		Ui.rr(g, x, y, w, h, 5, d ? Ui.al(ac, 0.9f) : Ui.al(Ui.tn(0x0B0916), Cfg.c.op / 100f + 0.1f));
		if (!d) Ui.ro(g, x, y, w, h, 5, Ui.al(0xFFFFFF, 0.1f));
		if (t.isEmpty()) {
			g.fill(x + w / 2 - 8, y + h / 2, x + w / 2 + 8, y + h / 2 + 1, d ? 0xFFFFFFFF : Ui.al(ac, 0.7f));
			return;
		}
		int tw = tw(t);
		float sc = tw > w - 3 ? Math.max(0.5f, (w - 3f) / tw) : 1f;
		var ps = g.pose();
		ps.pushMatrix();
		ps.translate(x + w / 2f, y + h / 2f);
		ps.scale(sc, sc);
		Tf.ds(g, t, -tw / 2, -4, d ? 0xFF05080F : 0xFF000000 | tc(), false);
		ps.popMatrix();
	}

	static void armor(GuiGraphics g, Minecraft m, int w, int h) {
		int lay = gi("armor", "lay", 0);
		int dm = gi("armor", "dur", 0);
		boolean lf = gi("armor", "side", 0) == 1;
		boolean held = gb("armor", "held", false);
		boolean off = gb("armor", "off", false);
		List<ItemStack> its = new ArrayList<>();
		for (EquipmentSlot e : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) its.add(m.player.getItemBySlot(e));
		if (held) its.add(m.player.getMainHandItem());
		if (off) its.add(m.player.getOffhandItem());
		boolean any = false;
		for (ItemStack i : its) if (!i.isEmpty()) any = true;
		if (!any && !ed) return;
		float s = es(ARMOR);
		var ps = g.pose();
		int tc = tc();
		int n = its.size();
		if (lay == 0) {
			int gap = gi("armor", "gap", 6);
			int bw = dm == 3 ? 26 : dm == 2 ? 50 : 20 + gap + tw(dm == 1 ? "100%" : "2031") + 6;
			int rh = 19;
			int bh = n * rh - 1;
			int[] p = place(ARMOR, bw, bh, w, h, s);
			ps.pushMatrix();
			ps.scale(s, s);
			for (int i = 0; i < n; i++) {
				ItemStack it = its.get(i);
				int ay = p[1] + i * rh;
				pn(g, p[0], ay, bw, 18);
				if (it.isEmpty()) {
					Ui.ro(g, p[0] + 2, ay + 2, 14, 14, 3, Ui.al(0xFFFFFF, 0.1f));
					continue;
				}
				int ix = lf ? p[0] + bw - 17 : p[0] + 1;
				g.renderItem(it, ix, ay + 1);
				if (it.isDamageableItem() && dm != 3) {
					int mx = it.getMaxDamage();
					int left = mx - it.getDamageValue();
					float pr = left / (float) mx;
					String t = dm == 1 ? Math.round(pr * 100) + "%" : String.valueOf(left);
					if (dm != 2) dv(g, t, lf ? p[0] + 5 : p[0] + 20 + gap, ay + 5, pc(pr));
					if (dm == 2 || dm == 0) {
						int bx = lf ? p[0] + 5 : p[0] + 20;
						int bwid = bw - 25;
						g.fill(bx, ay + 14, bx + bwid, ay + 15, 0x40FFFFFF);
						g.fill(bx, ay + 14, bx + (int) (bwid * pr), ay + 15, 0xFF000000 | pc(pr));
					}
				} else if (!it.isDamageableItem() && it.getCount() > 1) {
					String t = String.valueOf(it.getCount());
					dv(g, t, lf ? p[0] + 5 : p[0] + bw - 5 - tw(t), ay + 5, tc);
				}
			}
			ps.popMatrix();
		} else {
			int cw = 22;
			int bw = n * cw + 2;
			int bh = dm == 3 ? 22 : 31;
			int[] p = place(ARMOR, bw, bh, w, h, s);
			ps.pushMatrix();
			ps.scale(s, s);
			pn(g, p[0], p[1], bw, bh);
			for (int i = 0; i < n; i++) {
				ItemStack it = its.get(i);
				int ax = p[0] + 3 + i * cw;
				if (it.isEmpty()) {
					Ui.ro(g, ax, p[1] + 3, 16, 16, 3, Ui.al(0xFFFFFF, 0.1f));
					continue;
				}
				g.renderItem(it, ax, p[1] + 3);
				if (it.isDamageableItem() && dm != 3) {
					int mx = it.getMaxDamage();
					int left = mx - it.getDamageValue();
					float pr = left / (float) mx;
					if (dm == 2) {
						g.fill(ax, p[1] + 21, ax + 16, p[1] + 23, 0x40FFFFFF);
						g.fill(ax, p[1] + 21, ax + (int) (16 * pr), p[1] + 23, 0xFF000000 | pc(pr));
					} else {
						String t = dm == 1 ? Math.round(pr * 100) + "" : String.valueOf(left);
						int tw = tw(t);
						float sc = tw > 20 ? 20f / tw : 1f;
						ps.pushMatrix();
						ps.translate(ax + 8f, p[1] + 21f);
						ps.scale(sc, sc);
						Tf.ds(g, t, -tw / 2, 0, 0xFF000000 | pc(pr), Cfg.c.ds);
						ps.popMatrix();
					}
				}
			}
			ps.popMatrix();
		}
	}

	static void totem(GuiGraphics g, Minecraft m, int w, int h) {
		var inv = m.player.getInventory();
		int n = 0;
		boolean off = gb("totem", "off", true);
		for (int j = 0; j < inv.getContainerSize(); j++) {
			ItemStack it = inv.getItem(j);
			if (it.is(Items.TOTEM_OF_UNDYING)) n += it.getCount();
		}
		if (!off && m.player.getOffhandItem().is(Items.TOTEM_OF_UNDYING)) n -= m.player.getOffhandItem().getCount();
		int st = gi("totem", "st", 0);
		String t = st == 2 ? "Totems " + n : "x" + n;
		int bw = (st == 0 ? 22 : 0) + tw(t) + 10;
		int bh = 20;
		float s = es(TOTEM);
		int[] p = place(TOTEM, bw, bh, w, h, s);
		var ps = g.pose();
		ps.pushMatrix();
		ps.scale(s, s);
		pn(g, p[0], p[1], bw, bh);
		int tx = p[0] + 5;
		if (st == 0) {
			g.renderItem(new ItemStack(Items.TOTEM_OF_UNDYING), p[0] + 3, p[1] + 2);
			tx = p[0] + 22;
		}
		int col = n == 0 ? (gb("totem", "warn", true) && System.currentTimeMillis() / 400 % 2 == 0 ? 0xFFFF4040 : 0xFFFF8080) : tc();
		dv(g, t, tx, p[1] + 6, col);
		ps.popMatrix();
	}

	static void invhud(GuiGraphics g, Minecraft m, int w, int h) {
		boolean hb = gb("invhud", "hb", false);
		boolean bg = gb("invhud", "bgp", true);
		boolean sl = gb("invhud", "sl", true);
		boolean cn = gb("invhud", "cnt", true);
		var inv = m.player.getInventory();
		int rows = hb ? 4 : 3;
		int bw = 9 * 18 + 6;
		int bh = rows * 18 + 6;
		float s = es(INV);
		int[] p = place(INV, bw, bh, w, h, s);
		var ps = g.pose();
		ps.pushMatrix();
		ps.scale(s, s);
		if (bg) pn(g, p[0], p[1], bw, bh);
		for (int r = 0; r < rows; r++) for (int cI = 0; cI < 9; cI++) {
			int idx = r < 3 ? 9 + r * 9 + cI : cI;
			int x = p[0] + 3 + cI * 18;
			int y = p[1] + 3 + r * 18;
			if (sl) Ui.rr(g, x, y, 17, 17, 3, Ui.al(0xFFFFFF, 0.06f));
			ItemStack it = inv.getItem(idx);
			if (it.isEmpty()) continue;
			g.renderItem(it, x + 1, y + 1);
			if (cn) g.renderItemDecorations(m.font, it, x + 1, y + 1);
		}
		ps.popMatrix();
	}

	static final float[] SB = new float[6];

	static void spot(GuiGraphics g, Minecraft m, int w, int h) {
		Spot.upd();
		int ac = ac() & 0xFFFFFF;
		int tc = tc();
		int dim = Vis.mix(0xFFFFFF, ac, 0.55f);
		boolean smp = !Spot.on() && ed;
		boolean cov = gb("spotify", "cov", true);
		boolean bar = gb("spotify", "bar", true);
		boolean eq = gb("spotify", "eq", true);
		boolean np = gb("spotify", "np", true);
		float s = es(SPOT);
		var ps = g.pose();
		if (!Spot.on() && !smp) {
			String t = "Nothing is playing";
			int bw = tw(t) + 34;
			int[] p = place(SPOT, bw, 20, w, h, s);
			ps.pushMatrix();
			ps.scale(s, s);
			pn(g, p[0], p[1], bw, 20);
			for (int i = 0; i < 3; i++) g.fill(p[0] + 7 + i * 4, p[1] + 12, p[0] + 9 + i * 4, p[1] + 14, Ui.al(0xFFFFFF, 0.35f));
			dt(g, t, p[0] + 24, p[1] + 6, Ui.al(0xFFFFFF, 0.55f));
			ps.popMatrix();
			return;
		}
		String sn = smp ? "Song name" : Spot.song;
		String ar = smp ? "Artist" : Spot.art;
		boolean pl = smp || Spot.playing;
		long pos = smp ? 83000 : Spot.now();
		long dur = smp ? 191000 : Spot.dur;
		int ch = 42;
		int pad = 7;
		int left = cov ? pad + ch + 9 : pad + 2;
		int bw = Math.max(170, Math.min(230, Math.max(tw(sn), tw(ar)) + left + 12));
		int bh = pad * 2 + ch + (bar ? 13 : 0);
		int[] p = place(SPOT, bw, bh, w, h, s);
		ps.pushMatrix();
		ps.scale(s, s);
		pn(g, p[0], p[1], bw, bh);
		if (cov) {
			int cx = p[0] + pad;
			int cy = p[1] + pad;
			if (pl) Ui.glow(g, cx, cy, ch, ch, 6, ac, 0.35f);
			if (Spot.tex != null && !smp) g.blit(RenderPipelines.GUI_TEXTURED, Spot.tex, cx, cy, 0f, 0f, ch, ch, Spot.CS, Spot.CS, Spot.CS, Spot.CS);
			else {
				Ui.rr(g, cx, cy, ch, ch, 6, Ui.al(ac, 0.25f));
				Ic.dr(g, "spotify", cx + ch / 2, cy + ch / 2, 24, Ui.al(0xFFFFFF, 0.9f));
			}
			if (!pl) {
				g.fill(cx, cy, cx + ch, cy + ch, Ui.al(0x000000, 0.55f));
				g.fill(cx + ch / 2 - 5, cy + ch / 2 - 6, cx + ch / 2 - 2, cy + ch / 2 + 6, Ui.al(0xFFFFFF, 0.95f));
				g.fill(cx + ch / 2 + 2, cy + ch / 2 - 6, cx + ch / 2 + 5, cy + ch / 2 + 6, Ui.al(0xFFFFFF, 0.95f));
			}
			Ui.ro(g, cx, cy, ch, ch, 4, Ui.al(0xFFFFFF, 0.18f));
		}
		int tx = p[0] + left;
		int tw2 = bw - left - pad;
		int hy = p[1] + pad + 3;
		int lx = tx;
		if (eq) {
			float t = tm();
			for (int i = 0; i < 4; i++) {
				float wv = 0.5f + 0.25f * (float) Math.sin(t * (5f + i * 1.7f) + i * 1.3f) + 0.25f * (float) Math.sin(t * (9f + i * 0.9f) + i * 2.1f);
				float tg = !pl ? 0f : smp ? 0.55f * wv : Spot.lvl * (0.3f + 0.7f * wv);
				SB[i] += (tg - SB[i]) * (tg > SB[i] ? 0.5f : 0.15f);
				int bhh = 1 + Math.round(6f * Math.min(1f, SB[i]));
				g.fill(tx + i * 3, hy + 7 - bhh, tx + i * 3 + 2, hy + 7, Ui.al(ac, pl ? 1f : 0.4f));
			}
			lx = tx + 15;
		}
		if (np) Ui.ts(g, pl ? "NOW PLAYING" : "PAUSED", lx, hy, 7f, pl ? ac : 0xFFFFFF, pl ? 1f : 0.5f);
		int sy = np || eq ? hy + 11 : hy + 5;
		dv(g, fit(sn, tw2), tx, sy, tc);
		dt(g, fit(ar, tw2), tx, sy + 12, Ui.al(dim, 0.85f));
		if (bar) {
			int by = p[1] + pad + ch + 9;
			float pr = dur > 0 ? Math.max(0f, Math.min(1f, pos / (float) dur)) : 0f;
			String a = Spot.tm(pos);
			String b = Spot.tm(dur);
			int x0 = p[0] + pad;
			int x1 = p[0] + bw - pad;
			float aw = Ui.w(a, 7f);
			float bwd = Ui.w(b, 7f);
			Ui.ts(g, a, x0, by - 3, 7f, 0xFFFFFF, 0.6f);
			Ui.ts(g, b, x1 - bwd, by - 3, 7f, 0xFFFFFF, 0.6f);
			int bx = x0 + Math.round(aw) + 5;
			int bwid = x1 - Math.round(bwd) - 5 - bx;
			Ui.rr(g, bx, by, bwid, 2, 1, Ui.al(0xFFFFFF, 0.14f));
			int fw = Math.max(2, Math.round(bwid * pr));
			if (pr > 0.005f) Ui.rr(g, bx, by, fw, 2, 1, Ui.al(ac, 1f));
			g.fill(bx + fw - 2, by - 1, bx + fw + 1, by + 3, 0xFFFFFFFF);
		}
		ps.popMatrix();
	}

	static void extras(GuiGraphics g, Minecraft m, int w, int h) {
		int tc = tc();
		int ac = ac() & 0xFFFFFF;
		int dim = Vis.mix(0xFFFFFF, ac, 0.55f);
		float s = Cfg.c.sc / 100f;
		var pose = g.pose();
		if (live("pops")) {
			List<String[]> pl = Pops.top(5);
			if (!pl.isEmpty()) {
				int lw = 0;
				for (String[] t : pl) lw = Math.max(lw, tw(t[0]) + tw(t[1]) + 14);
				int bw = Math.max(lw + 12, 80);
				int bh = pl.size() * 11 + 17;
				int[] p = place(POPS, bw, bh, w, h, s);
				pose.pushMatrix();
				pose.scale(s, s);
				pn(g, p[0], p[1], bw, bh);
				dt(g, "TOTEM POPS", p[0] + 6, p[1] + 4, Ui.al(dim, 0.7f));
				int yy = p[1] + 16;
				for (String[] t : pl) {
					dt(g, t[0], p[0] + 6, yy, 0xFF000000 | tc);
					dv(g, t[1], p[0] + bw - 6 - tw(t[1]), yy, ac);
					yy += 11;
				}
				pose.popMatrix();
			}
		}
		if (live("mark")) {
			int st = gi("mark", "st", 0);
			boolean vr = gb("mark", "vr", true);
			String v0 = Upd.ver();
			String ver = v0.contains("-") ? v0.substring(v0.indexOf('-') + 1) : "v" + v0;
			String t = "AZURE";
			Identifier lgt = Identifier.fromNamespaceAndPath("azure", "textures/gui/logo.png");
			if (st == 2) {
				int bs = 32;
				int[] p = place(MARK, bs, bs, w, h, s);
				pose.pushMatrix();
				pose.scale(s, s);
				Ui.glow(g, p[0], p[1], bs, bs, 12, ac, 0.5f);
				Ui.rr(g, p[0], p[1], bs, bs, 12, Ui.al(Ui.tn(0x0A0714), 0.85f));
				Ui.ro(g, p[0], p[1], bs, bs, 12, Ui.al(ac, 0.6f));
				g.blit(RenderPipelines.GUI_TEXTURED, lgt, p[0] + 5, p[1] + 5, 0f, 0f, bs - 10, bs - 10, 256, 256, 256, 256, Ui.al(ac, 1f));
				pose.popMatrix();
			} else if (st == 1) {
				int vw = vr ? tw(ver) + 10 : 0;
				int bw = Math.round(tw(t) * 1.5f) + vw;
				int[] p = place(MARK, bw, 20, w, h, s);
				pose.pushMatrix();
				pose.scale(s, s);
				pose.pushMatrix();
				pose.translate(p[0], p[1]);
				pose.scale(1.5f, 1.5f);
				dv(g, t, 0, 0, tc);
				pose.popMatrix();
				if (vr) dt(g, ver, p[0] + Math.round(tw(t) * 1.5f) + 6, p[1] + 3, 0x99000000 | (tc & 0xFFFFFF));
				Ui.rr(g, p[0], p[1] + 16, bw, 2, 1, Ui.al(ac, 0.95f));
				Ui.rr(g, p[0] + bw / 2, p[1] + 16, bw - bw / 2, 2, 1, Ui.al(Ui.tn(0x0A0714), 0.5f));
				pose.popMatrix();
			} else {
				int lg = 14;
				int vw = vr ? tw(ver) + 12 : 0;
				int bw = 6 + lg + 6 + tw(t) + 8 + vw + 4;
				int bh = 22;
				int[] p = place(MARK, bw, bh, w, h, s);
				pose.pushMatrix();
				pose.scale(s, s);
				pn(g, p[0], p[1], bw, bh);
				Ui.rr(g, p[0] + 3, p[1] + 3, lg + 6, bh - 6, 8, Ui.al(ac, 0.22f));
				g.blit(RenderPipelines.GUI_TEXTURED, lgt, p[0] + 6, p[1] + 4, 0f, 0f, lg, lg, 256, 256, 256, 256, Ui.al(ac, 1f));
				dv(g, t, p[0] + 6 + lg + 7, p[1] + 7, tc);
				if (vr) {
					int vx = p[0] + 6 + lg + 7 + tw(t) + 7;
					Ui.rr(g, vx, p[1] + 5, vw, bh - 10, 6, Ui.al(ac, 0.28f));
					dt(g, ver, vx + 6, p[1] + 7, Ui.al(0xFFFFFF, 0.9f));
				}
				pose.popMatrix();
			}
		}
		if (live("appleskin") && !m.player.isCreative() && !m.player.isSpectator()) {
			var afp = m.player.getMainHandItem().get(net.minecraft.core.component.DataComponents.FOOD);
			if (afp == null) afp = m.player.getOffhandItem().get(net.minecraft.core.component.DataComponents.FOOD);
			int af = m.player.getFoodData().getFoodLevel();
			if (afp != null && af < 20) {
				int af2 = Math.min(20, af + afp.nutrition());
				float pu = 0.4f + 0.3f * (float) Math.sin(net.minecraft.util.Util.getMillis() / 220.0);
				for (int i = 0; i < 10; i++) {
					int lo = 2 * i;
					int ax = w / 2 + 91 - i * 8 - 9;
					int ay = h - 39;
					if (af2 >= lo + 2 && af < lo + 2) g.blitSprite(RenderPipelines.GUI_TEXTURED, Identifier.withDefaultNamespace("hud/food_full"), ax, ay, 9, 9, Ui.al(0xFFFFFF, pu));
					else if (af2 == lo + 1 && af < lo + 1) g.blitSprite(RenderPipelines.GUI_TEXTURED, Identifier.withDefaultNamespace("hud/food_half"), ax, ay, 9, 9, Ui.al(0xFFFFFF, pu));
				}
			}
		}
		if (live("food")) {
			ItemStack st = m.player.getMainHandItem();
			var fp = st.get(net.minecraft.core.component.DataComponents.FOOD);
			if (fp != null) {
				String t = "+" + fp.nutrition() + " food   +" + String.format("%.1f", fp.saturation()) + " sat";
				int tw = tw(t);
				pn(g, w / 2 - tw / 2 - 6, h - 68, tw + 12, 14);
				dv(g, t, w / 2 - tw / 2, h - 65, tc);
			}
		}
		if (live("shield")) {
			ItemStack sh = m.player.getOffhandItem().is(Items.SHIELD) ? m.player.getOffhandItem() : m.player.getMainHandItem();
			boolean has = sh.is(Items.SHIELD);
			if (has || ed) {
				boolean cd = has && m.player.getCooldowns().isOnCooldown(sh);
				String v = has ? (sh.getMaxDamage() - sh.getDamageValue()) + (cd ? " disabled" : m.player.isBlocking() ? " blocking" : " ready") : "336 ready";
				stat(g, SHIELD, "SHIELD", v, cd ? 0xFF6060 : tc, w, h);
			}
		}
	}
}
