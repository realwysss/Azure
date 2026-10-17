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
		public float x;
		public float y;
		public float w;
		public float h;

		El(String id, String nm, IntSupplier gx, IntConsumer sx, IntSupplier gy, IntConsumer sy, int dx, int dy) {
			this.id = id;
			this.nm = nm;
			this.gx = gx;
			this.gy = gy;
			this.sx = sx;
			this.sy = sy;
			this.dx = dx;
			this.dy = dy;
		}

		static El mk(String id, String nm, int dx, int dy) {
			return new El(id, nm, () -> Cfg.gi("x." + id, dx), v -> Cfg.si("x." + id, v), () -> Cfg.gi("y." + id, dy), v -> Cfg.si("y." + id, v), dx, dy);
		}

		public void pos(int px, int py) {
			sx.accept(px);
			sy.accept(py);
		}

		public void reset() {
			sx.accept(dx);
			sy.accept(dy);
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
	public static final El POTS = add(El.mk("pots", "Potions", 1, 34));
	public static final El KEYS = add(El.mk("keys", "Keystrokes", 1, 98));
	public static final El SPOT = add(El.mk("spotify", "Spotify", 99, 1));
	public static final El INV = add(El.mk("invhud", "Inventory", 99, 22));
	public static final El ARMOR = add(El.mk("armor", "Armor", 99, 62));
	public static final El TOTEM = add(El.mk("totem", "Totems", 99, 96));
	public static final El POPS = add(El.mk("pops", "Totem pops", 62, 14));
	public static final El MARK = add(El.mk("mark", "Watermark", 50, 0));

	static El add(El e) {
		ELS.add(e);
		return e;
	}

	public static boolean ed;
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

	public static int ac() {
		Thm.ld();
		int b = Thm.ac;
		int m = Cfg.c.am;
		if (m == 1) return 0xFF000000 | java.awt.Color.HSBtoRGB((System.currentTimeMillis() % 6000) / 6000f, 0.6f, 1f);
		if (m == 2) return 0xFF000000 | Vis.mix(b & 0xFFFFFF, 0xFFFFFF, 0.5f + 0.5f * (float) Math.sin(System.currentTimeMillis() / 500.0));
		return b;
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

	static void dt(GuiGraphics g, String s, int x, int y, int col) {
		g.drawString(Minecraft.getInstance().font, s, x, y, col, Cfg.c.ds);
	}

	static void dv(GuiGraphics g, String s, int x, int y, int base) {
		Font f = Minecraft.getInstance().font;
		int te = Cfg.c.te;
		if (te == 0) {
			g.drawString(f, s, x, y, 0xFF000000 | (base & 0xFFFFFF), Cfg.c.ds);
			return;
		}
		if (te == 1 || te == 3) {
			g.drawString(f, s, x, y, fx(0, 1, base), Cfg.c.ds);
			return;
		}
		int cx = x;
		for (int i = 0; i < s.length(); i++) {
			String ch = String.valueOf(s.charAt(i));
			g.drawString(f, ch, cx, y, fx(i, s.length(), base), Cfg.c.ds);
			cx += f.width(ch);
		}
	}

	static int tw(String s) {
		return Minecraft.getInstance().font.width(s);
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
		if (c.bs == 2) {
			Ui.rr(g, x, y, w, h, 4, Ui.al(0x050810, o * 0.35f));
			Ui.ro(g, x, y, w, h, 4, Ui.al(ac, 0.9f));
			return;
		}
		Ui.rr(g, x, y, w, h, 4, Ui.al(0x0A0F1C, o));
		g.fill(x + 5, y + 1, x + w - 5, y + 2, Ui.al(0xFFFFFF, c.bs == 1 ? 0.2f : 0.12f));
		Ui.ro(g, x, y, w, h, 4, Ui.al(0xFFFFFF, 0.13f));
		if (c.bs == 1) g.fill(x + 5, y + h - 1, x + w - 5, y + h, Ui.al(ac, 0.8f));
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
		int dim = Vis.mix(0xFFFFFF, ac() & 0xFFFFFF, 0.55f);
		int bw = tw(label) + tw(val) + (pill ? 18 : 6);
		int bh = pill ? 14 : 10;
		float s = es(e);
		int[] p = place(e, bw, bh, w, h, s);
		var ps = g.pose();
		ps.pushMatrix();
		ps.scale(s, s);
		if (pill) pn(g, p[0], p[1], bw, bh);
		int tx = p[0] + (pill ? 6 : 1);
		int ty = p[1] + (pill ? 3 : 1);
		dt(g, label, tx, ty, Ui.al(dim, 0.9f));
		dv(g, val, tx + tw(label) + 6, ty, vc);
		ps.popMatrix();
	}

	static String roman(int n) {
		String[] r = {"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"};
		return n >= 0 && n < r.length ? r[n] : String.valueOf(n);
	}

	public static void draw(GuiGraphics g, DeltaTracker dtk) {
		Cfg c = Cfg.c;
		Minecraft m = Minecraft.getInstance();
		for (El e : ELS) e.w = 0;
		if (!c.hud || m.player == null || m.options.hideGui) return;
		clicks(m);
		int w = g.guiWidth();
		int h = g.guiHeight();
		int tc = c.tc;
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
		if (live("xyz")) stat(g, XYZ, "XYZ", (int) Math.floor(m.player.getX()) + " " + (int) Math.floor(m.player.getY()) + " " + (int) Math.floor(m.player.getZ()), tc, w, h);
		if (live("clock")) stat(g, CLOCK, "TIME", LocalTime.now().format(T), tc, w, h);
		if (live("pots")) pots(g, m, w, h);
		if (live("keys")) keys(g, m, w, h);
		if (live("spotify")) spot(g, m, w, h);
		if (live("invhud")) invhud(g, m, w, h);
		if (live("armor")) armor(g, m, w, h);
		if (live("totem")) totem(g, m, w, h);
		extras(g, m, w, h);
	}

	static void pots(GuiGraphics g, Minecraft m, int w, int h) {
		int tc = Cfg.c.tc;
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
		int bw = cols * k + (cols - 1) * gp;
		int bh;
		if (lay == 0) bh = 2 * k + gp;
		else if (lay == 1) bh = 3 * k + 2 * gp;
		else if (lay == 2) bh = 3 * k + 3 * gp + 10;
		else bh = 4 * k + 3 * gp;
		int[] p = place(KEYS, bw, bh, w, h, s);
		var ps = g.pose();
		ps.pushMatrix();
		ps.scale(s, s);
		int ix = p[0];
		int iy = p[1];
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
		Ui.rr(g, x, y, w, h, 4, d ? Ui.al(ac, 0.9f) : Ui.al(0x0A0F1C, Cfg.c.op / 100f + 0.1f));
		if (!d) Ui.ro(g, x, y, w, h, 4, Ui.al(0xFFFFFF, 0.14f));
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
		g.drawString(Minecraft.getInstance().font, t, -tw / 2, -4, d ? 0xFF05080F : 0xFF000000 | Cfg.c.tc, false);
		ps.popMatrix();
	}

	static void armor(GuiGraphics g, Minecraft m, int w, int h) {
		int lay = gi("armor", "lay", 0);
		int dm = gi("armor", "dur", 0);
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
		int tc = Cfg.c.tc;
		int n = its.size();
		if (lay == 0) {
			int bw = dm == 3 ? 26 : dm == 2 ? 50 : 58;
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
				g.renderItem(it, p[0] + 1, ay + 1);
				if (it.isDamageableItem() && dm != 3) {
					int mx = it.getMaxDamage();
					int left = mx - it.getDamageValue();
					float pr = left / (float) mx;
					String t = dm == 1 ? Math.round(pr * 100) + "%" : String.valueOf(left);
					if (dm != 2) dv(g, t, p[0] + bw - 5 - tw(t), ay + 5, pc(pr));
					if (dm == 2 || dm == 0) {
						int bx = p[0] + 20;
						int bwid = bw - 25;
						g.fill(bx, ay + 14, bx + bwid, ay + 15, 0x40FFFFFF);
						g.fill(bx, ay + 14, bx + (int) (bwid * pr), ay + 15, 0xFF000000 | pc(pr));
					}
				} else if (!it.isDamageableItem() && it.getCount() > 1) {
					String t = String.valueOf(it.getCount());
					dv(g, t, p[0] + bw - 5 - tw(t), ay + 5, tc);
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
						g.drawString(m.font, t, -tw / 2, 0, 0xFF000000 | pc(pr), Cfg.c.ds);
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
		int col = n == 0 ? (gb("totem", "warn", true) && System.currentTimeMillis() / 400 % 2 == 0 ? 0xFFFF4040 : 0xFFFF8080) : Cfg.c.tc;
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

	static void spot(GuiGraphics g, Minecraft m, int w, int h) {
		Spot.upd();
		int ac = ac() & 0xFFFFFF;
		int tc = Cfg.c.tc;
		int dim = Vis.mix(0xFFFFFF, ac, 0.55f);
		boolean smp = !Spot.on() && ed;
		boolean cov = gb("spotify", "cov", true);
		boolean bar = gb("spotify", "bar", true);
		boolean eq = gb("spotify", "eq", true);
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
		int ch = 36;
		int left = cov ? ch + 9 : 7;
		int bw = Math.max(120, Math.min(190, Math.max(tw(sn), tw(ar)) + left + (eq ? 30 : 10)));
		int bh = bar ? 46 : 32;
		if (cov && bh < ch + 8) bh = ch + 8;
		int[] p = place(SPOT, bw, bh, w, h, s);
		ps.pushMatrix();
		ps.scale(s, s);
		pn(g, p[0], p[1], bw, bh);
		if (cov) {
			int cx = p[0] + 4;
			int cy = p[1] + 4;
			if (Spot.tex != null && !smp) g.blit(RenderPipelines.GUI_TEXTURED, Spot.tex, cx, cy, 0f, 0f, ch, ch, 64, 64, 64, 64);
			else {
				Ui.rr(g, cx, cy, ch, ch, 4, Ui.al(ac, 0.25f));
				Ic.draw(g, "music", cx + ch / 2, cy + ch / 2, Ui.al(0xFFFFFF, 0.8f));
			}
			Ui.ro(g, cx, cy, ch, ch, 4, Ui.al(0xFFFFFF, 0.2f));
		}
		int tx = p[0] + left;
		int tw2 = bw - left - (eq ? 24 : 6);
		dv(g, fit(sn, tw2), tx, p[1] + 5, tc);
		dt(g, fit(ar, tw2), tx, p[1] + 15, Ui.al(dim, 0.85f));
		if (eq) {
			float t = tm();
			for (int i = 0; i < 5; i++) {
				float wv = 0.5f + 0.5f * (float) Math.sin(t * (7f + i * 2.3f) + i * 1.7f);
				float lv = smp ? 0.5f * wv : Spot.lvl * (0.45f + 0.55f * wv);
				int bhh = 2 + Math.round(9 * Math.min(1f, lv));
				int bx = p[0] + bw - 6 - (5 - i) * 3 - 1;
				g.fill(bx, p[1] + 16 - bhh, bx + 2, p[1] + 16, Ui.al(pl ? ac : 0xFFFFFF, pl ? 1f : 0.3f));
			}
		}
		if (bar) {
			int by = p[1] + 30;
			int bx = tx;
			int bwid = bw - left - 6;
			float pr = dur > 0 ? Math.max(0f, Math.min(1f, pos / (float) dur)) : 0f;
			Ui.rr(g, bx, by, bwid, 3, 1, Ui.al(0xFFFFFF, 0.15f));
			if (pr > 0.01f) Ui.rr(g, bx, by, Math.max(3, Math.round(bwid * pr)), 3, 1, Ui.al(ac, 1f));
			g.fill(bx + Math.round(bwid * pr) - 1, by - 1, bx + Math.round(bwid * pr) + 1, by + 4, 0xFFFFFFFF);
			String a = Spot.tm(pos);
			String b = Spot.tm(dur);
			var f = m.font;
			ps.pushMatrix();
			ps.translate(bx, by + 6);
			ps.scale(0.8f, 0.8f);
			g.drawString(f, a, 0, 0, Ui.al(0xFFFFFF, 0.7f), false);
			g.drawString(f, b, Math.round(bwid / 0.8f) - f.width(b), 0, Ui.al(0xFFFFFF, 0.7f), false);
			ps.popMatrix();
		}
		ps.popMatrix();
	}

	static void extras(GuiGraphics g, Minecraft m, int w, int h) {
		int tc = Cfg.c.tc;
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
			String t = "Azure";
			int bw = tw(t) + 22;
			int bh = 16;
			int[] p = place(MARK, bw, bh, w, h, s);
			pose.pushMatrix();
			pose.scale(s, s);
			pn(g, p[0], p[1], bw, bh);
			Ui.rr(g, p[0] + 6, p[1] + 6, 4, 4, 2, Ui.al(ac, 1f));
			dv(g, t, p[0] + 14, p[1] + 4, tc);
			pose.popMatrix();
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
			ItemStack st = m.player.getOffhandItem().is(Items.SHIELD) ? m.player.getOffhandItem() : m.player.getMainHandItem();
			if (st.is(Items.SHIELD)) {
				boolean cd = m.player.getCooldowns().isOnCooldown(st);
				String t = "Shield " + (st.getMaxDamage() - st.getDamageValue()) + (cd ? "  disabled" : m.player.isBlocking() ? "  blocking" : "");
				int tw = tw(t);
				pn(g, w / 2 - tw / 2 - 6, h - 84, tw + 12, 14);
				dt(g, t, w / 2 - tw / 2, h - 81, cd ? 0xFFFF6060 : 0xFF000000 | tc);
			}
		}
	}
}
