package dev.wscl;

import dev.wysl.Thm;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

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
	public static final El STATS = add(new El("stats", "Stats", () -> Cfg.c.hx, v -> Cfg.c.hx = v, () -> Cfg.c.hy, v -> Cfg.c.hy = v, 1, 1));
	public static final El ARMOR = add(new El("armor", "Armor", () -> Cfg.c.ax, v -> Cfg.c.ax = v, () -> Cfg.c.ay, v -> Cfg.c.ay = v, 99, 40));
	public static final El KEYS = add(new El("keys", "Keys", () -> Cfg.c.kx, v -> Cfg.c.kx = v, () -> Cfg.c.ky, v -> Cfg.c.ky = v, 99, 99));
	public static final El ITEMS = add(new El("items", "Items", () -> Cfg.c.px, v -> Cfg.c.px = v, () -> Cfg.c.py, v -> Cfg.c.py = v, 1, 50));
	public static final El POTS = add(El.mk("pots", "Potions", 27, 1));
	public static final El POPS = add(El.mk("pops", "Totem pops", 62, 1));
	public static final El MARK = add(El.mk("mark", "Watermark", 99, 1));
	public static final El SPOT = add(El.mk("spot", "Spotify", 99, 12));

	static El add(El e) {
		ELS.add(e);
		return e;
	}

	public static float reach = -1;
	public static int combo;
	static final ArrayDeque<Long> L = new ArrayDeque<>();
	static final ArrayDeque<Long> R = new ArrayDeque<>();
	static boolean lp;
	static boolean rp;
	static final DateTimeFormatter T = DateTimeFormatter.ofPattern("HH:mm");

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

	static void dt(GuiGraphics g, String s, int x, int y, int col) {
		if (Cfg.c.hf) Tf.dr(g, s, x, y, 9f, col);
		else g.drawString(Minecraft.getInstance().font, s, x, y, col, false);
	}

	static String fit(String s, int mx) {
		if (tw(s) <= mx) return s;
		while (s.length() > 1 && tw(s + "..") > mx) s = s.substring(0, s.length() - 1);
		return s + "..";
	}

	static int tw(String s) {
		return Cfg.c.hf ? Tf.w(s, 9f) : Minecraft.getInstance().font.width(s);
	}

	static Component tx(String s) {
		return Component.literal(s);
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

	static void rect(El e, float ix, float iy, int bw, int bh, float s) {
		e.x = ix * s;
		e.y = iy * s;
		e.w = bw * s;
		e.h = bh * s;
	}

	public static void draw(GuiGraphics g, DeltaTracker dt) {
		Cfg c = Cfg.c;
		Minecraft m = Minecraft.getInstance();
		for (El e : ELS) e.w = 0;
		if (!c.hud || m.player == null || m.options.hideGui) return;
		clicks(m);
		Font f = m.font;
		float s = c.sc / 100f;
		int w = g.guiWidth();
		int h = g.guiHeight();
		int ac = ac() & 0xFFFFFF;
		int tc = c.tc;
		int dim = Vis.mix(0xFFFFFF, ac, 0.55f);
		var pose = g.pose();
		List<String[]> ls = new ArrayList<>();
		if (Mod.live("fps")) ls.add(new String[]{"FPS", String.valueOf(m.getFps())});
		if (Mod.live("ping")) {
			PlayerInfo pi = m.getConnection() == null ? null : m.getConnection().getPlayerInfo(m.player.getUUID());
			ls.add(new String[]{"PING", (pi == null ? 0 : pi.getLatency()) + " ms"});
		}
		if (Mod.live("cps")) ls.add(new String[]{"CPS", L.size() + " | " + R.size()});
		if (Mod.live("xyz")) {
			ls.add(new String[]{"POS", (int) Math.floor(m.player.getX()) + " " + (int) Math.floor(m.player.getY()) + " " + (int) Math.floor(m.player.getZ())});
			ls.add(new String[]{"DIR", m.player.getDirection().getName().toUpperCase()});
		}
		if (Mod.live("clock")) ls.add(new String[]{"TIME", LocalTime.now().format(T)});
		if (Mod.live("move")) {
			var dm = m.player.getDeltaMovement();
			String st = m.player.isCrouching() ? "Sneaking" : m.player.isSprinting() ? "Sprinting" : "Walking";
			ls.add(new String[]{"MOVE", st + "  " + String.format("%.1f", Math.sqrt(dm.x * dm.x + dm.z * dm.z) * 20) + " b/s"});
		}
		if (Mod.live("reach")) ls.add(new String[]{"REACH", reach < 0 ? "-" : String.format("%.2f", reach)});
		if (Mod.live("combo")) ls.add(new String[]{"COMBO", String.valueOf(combo)});
		if (Mod.live("mem")) {
			Runtime rt = Runtime.getRuntime();
			ls.add(new String[]{"MEM", ((rt.totalMemory() - rt.freeMemory()) / 1048576) + " / " + (rt.maxMemory() / 1048576) + " MB"});
		}
		if (!ls.isEmpty()) {
			int lw = 0;
			int vw = 0;
			for (String[] t : ls) {
				lw = Math.max(lw, tw(t[0]));
				vw = Math.max(vw, tw(t[1]));
			}
			int bw = lw + vw + 20;
			int bh = ls.size() * 11 + 6;
			float x = (w / s - bw) * STATS.gx.getAsInt() / 100f;
			float y = (h / s - bh) * STATS.gy.getAsInt() / 100f;
			rect(STATS, x, y, bw, bh, s);
			pose.pushMatrix();
			pose.scale(s, s);
			int ix = (int) x;
			int iy = (int) y;
			pn(g, ix, iy, bw, bh);
			int yy = iy + 4;
			for (String[] t : ls) {
				dt(g, t[0], ix + 6, yy, Ui.al(dim, 0.85f));
				dt(g, t[1], ix + bw - 6 - tw(t[1]), yy, 0xFF000000 | tc);
				yy += 11;
			}
			pose.popMatrix();
		}
		if (Mod.live("armor")) {
			int bw = 56;
			int ay = (int) ((h / s - 95) * ARMOR.gy.getAsInt() / 100f);
			int ax = (int) ((w / s - bw) * ARMOR.gx.getAsInt() / 100f);
			rect(ARMOR, ax, ay, bw, 95, s);
			pose.pushMatrix();
			pose.scale(s, s);
			EquipmentSlot[] es = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET, EquipmentSlot.MAINHAND};
			for (EquipmentSlot e : es) {
				ItemStack it = m.player.getItemBySlot(e);
				if (it.isEmpty()) continue;
				pn(g, ax, ay, bw, 18);
				g.renderItem(it, ax + 1, ay + 1);
				if (it.isDamageableItem()) {
					int mx = it.getMaxDamage();
					int left = mx - it.getDamageValue();
					float p = left / (float) mx;
					String t = String.valueOf(left);
					dt(g, t, ax + bw - 5 - tw(t), ay + 3, 0xFF000000 | pc(p));
					g.fill(ax + 20, ay + 14, ax + bw - 5, ay + 15, 0x40FFFFFF);
					g.fill(ax + 20, ay + 14, ax + 20 + (int) ((bw - 25) * p), ay + 15, 0xFF000000 | pc(p));
				}
				ay += 19;
			}
			pose.popMatrix();
		}
		if (Mod.live("pvp")) {
			net.minecraft.world.item.Item[] it = {Items.TOTEM_OF_UNDYING, Items.END_CRYSTAL, Items.OBSIDIAN, Items.GOLDEN_APPLE, Items.ENDER_PEARL};
			var inv = m.player.getInventory();
			int bw = 44;
			int bh = it.length * 18 + 2;
			int ix = (int) ((w / s - bw) * ITEMS.gx.getAsInt() / 100f);
			int iy = (int) ((h / s - bh) * ITEMS.gy.getAsInt() / 100f);
			rect(ITEMS, ix, iy, bw, bh, s);
			pose.pushMatrix();
			pose.scale(s, s);
			pn(g, ix, iy, bw, bh);
			for (int i = 0; i < it.length; i++) {
				int n = 0;
				for (int j = 0; j < inv.getContainerSize(); j++) if (inv.getItem(j).is(it[i])) n += inv.getItem(j).getCount();
				int ry = iy + 1 + i * 18;
				g.renderItem(new ItemStack(it[i]), ix + 4, ry + 1);
				String t = String.valueOf(n);
				dt(g, t, ix + bw - 5 - tw(t), ry + 4, n == 0 ? 0x80FF6060 : 0xFF000000 | tc);
			}
			pose.popMatrix();
		}
		if (Mod.live("pots")) {
			List<String[]> ef = new ArrayList<>();
			List<Integer> cl = new ArrayList<>();
			for (MobEffectInstance mi : m.player.getActiveEffects()) {
				String nm = mi.getEffect().value().getDisplayName().getString();
				if (mi.getAmplifier() > 0) nm += " " + (mi.getAmplifier() + 1);
				int sec = mi.getDuration() / 20;
				ef.add(new String[]{nm, mi.isInfiniteDuration() ? "inf" : (sec / 60) + ":" + String.format("%02d", sec % 60)});
				cl.add(mi.getEffect().value().getColor());
			}
			if (!ef.isEmpty()) {
				int lw = 0;
				for (String[] t : ef) lw = Math.max(lw, tw(t[0]) + tw(t[1]) + 14);
				int bw = lw + 22;
				int bh = ef.size() * 12 + 6;
				float x = (w / s - bw) * POTS.gx.getAsInt() / 100f;
				float y = (h / s - bh) * POTS.gy.getAsInt() / 100f;
				rect(POTS, x, y, bw, bh, s);
				pose.pushMatrix();
				pose.scale(s, s);
				int ix = (int) x;
				int iy = (int) y;
				pn(g, ix, iy, bw, bh);
				int yy = iy + 4;
				for (int i = 0; i < ef.size(); i++) {
					Ui.rr(g, ix + 6, yy + 2, 5, 5, 2, Ui.al(cl.get(i), 1f));
					dt(g, ef.get(i)[0], ix + 15, yy, 0xFF000000 | tc);
					dt(g, ef.get(i)[1], ix + bw - 6 - tw(ef.get(i)[1]), yy, Ui.al(dim, 0.9f));
					yy += 12;
				}
				pose.popMatrix();
			}
		}
		if (Mod.live("pops")) {
			List<String[]> pl = Pops.top(5);
			if (!pl.isEmpty()) {
				int lw = 0;
				for (String[] t : pl) lw = Math.max(lw, tw(t[0]) + tw(t[1]) + 14);
				int bw = Math.max(lw + 12, 80);
				int bh = pl.size() * 11 + 17;
				float x = (w / s - bw) * POPS.gx.getAsInt() / 100f;
				float y = (h / s - bh) * POPS.gy.getAsInt() / 100f;
				rect(POPS, x, y, bw, bh, s);
				pose.pushMatrix();
				pose.scale(s, s);
				int ix = (int) x;
				int iy = (int) y;
				pn(g, ix, iy, bw, bh);
				dt(g, "TOTEM POPS", ix + 6, iy + 4, Ui.al(dim, 0.7f));
				int yy = iy + 16;
				for (String[] t : pl) {
					dt(g, t[0], ix + 6, yy, 0xFF000000 | tc);
					dt(g, t[1], ix + bw - 6 - tw(t[1]), yy, 0xFF000000 | ac);
					yy += 11;
				}
				pose.popMatrix();
			}
		}
		if (Mod.live("mark")) {
			String t = "Azure";
			int bw = tw(t) + 22;
			int bh = 16;
			float x = (w / s - bw) * MARK.gx.getAsInt() / 100f;
			float y = (h / s - bh) * MARK.gy.getAsInt() / 100f;
			rect(MARK, x, y, bw, bh, s);
			pose.pushMatrix();
			pose.scale(s, s);
			int ix = (int) x;
			int iy = (int) y;
			pn(g, ix, iy, bw, bh);
			Ui.rr(g, ix + 6, iy + 6, 4, 4, 2, Ui.al(ac, 1f));
			dt(g, t, ix + 14, iy + 4, 0xFF000000 | tc);
			pose.popMatrix();
		}
		if (Mod.live("spotify") && Spot.on()) {
			String sn = fit(Spot.song, 120);
			String ar = fit(Spot.art, 120);
			int bw = Math.max(86, Math.max(tw(sn), tw(ar)) + 34);
			int bh = 26;
			float x = (w / s - bw) * SPOT.gx.getAsInt() / 100f;
			float y = (h / s - bh) * SPOT.gy.getAsInt() / 100f;
			rect(SPOT, x, y, bw, bh, s);
			pose.pushMatrix();
			pose.scale(s, s);
			int ix = (int) x;
			int iy = (int) y;
			pn(g, ix, iy, bw, bh);
			float ts = (System.currentTimeMillis() % 100000L) / 1000f;
			for (int i = 0; i < 3; i++) {
				int bhh = 3 + (int) (7 * (0.5f + 0.5f * Math.sin(ts * (4f + i * 1.3f) + i * 2f)));
				g.fill(ix + 7 + i * 4, iy + 18 - bhh, ix + 9 + i * 4, iy + 18, Ui.al(ac, 1f));
			}
			dt(g, sn, ix + 23, iy + 4, 0xFF000000 | tc);
			dt(g, ar, ix + 23, iy + 14, Ui.al(dim, 0.8f));
			pose.popMatrix();
		}
		if (Mod.live("food")) {
			ItemStack st = m.player.getMainHandItem();
			var fp = st.get(net.minecraft.core.component.DataComponents.FOOD);
			if (fp != null) {
				String t = "+" + fp.nutrition() + " food   +" + String.format("%.1f", fp.saturation()) + " sat";
				int tw = tw(t);
				pn(g, w / 2 - tw / 2 - 6, h - 68, tw + 12, 14);
				dt(g, t, w / 2 - tw / 2, h - 65, 0xFF000000 | tc);
			}
		}
		if (Mod.live("shield")) {
			ItemStack st = m.player.getOffhandItem().is(Items.SHIELD) ? m.player.getOffhandItem() : m.player.getMainHandItem();
			if (st.is(Items.SHIELD)) {
				boolean cd = m.player.getCooldowns().isOnCooldown(st);
				String t = "Shield " + (st.getMaxDamage() - st.getDamageValue()) + (cd ? "  disabled" : m.player.isBlocking() ? "  blocking" : "");
				int tw = tw(t);
				pn(g, w / 2 - tw / 2 - 6, h - 84, tw + 12, 14);
				dt(g, t, w / 2 - tw / 2, h - 81, cd ? 0xFFFF6060 : 0xFF000000 | tc);
			}
		}
		if (Mod.live("keys")) {
			int k = 19;
			int gp = 2;
			int bw = k * 3 + gp * 2;
			int bh = k * 3 + gp * 3 + 9;
			int ix = (int) ((w / s - bw) * KEYS.gx.getAsInt() / 100f);
			int iy = (int) ((h / s - bh) * KEYS.gy.getAsInt() / 100f);
			rect(KEYS, ix, iy, bw, bh, s);
			pose.pushMatrix();
			pose.scale(s, s);
			key(g, f, m.options.keyUp, "W", ix + k + gp, iy, k, k);
			key(g, f, m.options.keyLeft, "A", ix, iy + k + gp, k, k);
			key(g, f, m.options.keyDown, "S", ix + k + gp, iy + k + gp, k, k);
			key(g, f, m.options.keyRight, "D", ix + (k + gp) * 2, iy + k + gp, k, k);
			int hw = (bw - gp) / 2;
			key(g, f, m.options.keyAttack, "L  " + L.size(), ix, iy + (k + gp) * 2, hw, k);
			key(g, f, m.options.keyUse, "R  " + R.size(), ix + hw + gp, iy + (k + gp) * 2, bw - hw - gp, k);
			key(g, f, m.options.keyJump, "", ix, iy + (k + gp) * 3, bw, 9);
			pose.popMatrix();
		}
	}

	static void key(GuiGraphics g, Font f, KeyMapping k, String t, int x, int y, int w, int h) {
		boolean d = k.isDown();
		int ac = ac() & 0xFFFFFF;
		if (d) Ui.glow(g, x, y, w, h, 4, ac, 1f);
		Ui.rr(g, x, y, w, h, 4, d ? Ui.al(Vis.mix(ac, 0x000000, 0.1f), 0.92f) : Ui.al(0x0A0F1C, Cfg.c.op / 100f + 0.15f));
		if (!d) Ui.ro(g, x, y, w, h, 4, Ui.al(0xFFFFFF, 0.12f));
		if (t.isEmpty()) {
			g.fill(x + w / 2 - 8, y + h / 2, x + w / 2 + 8, y + h / 2 + 1, d ? 0xFFFFFFFF : Ui.al(ac, 0.6f));
			return;
		}
		dt(g, t, x + (w - tw(t)) / 2, y + (h - 8) / 2 - 1, d ? 0xFF05080F : 0xFF000000 | Cfg.c.tc);
	}
}
