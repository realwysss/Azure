package dev.wscl;

import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.GuiGraphics;

public class Rw {
	static final int[] PS = {0x8B5CF6, 0xA78BFA, 0x6D5CFF, 0xC04CF0, 0xFF5FA8, 0x3D9BFF, 0x2EE6D6, 0x5CE08A, 0xFFD24A, 0xFFFFFF};

	public abstract static class R {
		String lb = "";
		String tp = "";
		int h = 26;
		float hv;
		int rx;
		int ry;
		int rw;

		public R tip(String s) {
			tp = s;
			return this;
		}

		abstract void dr(GuiGraphics g, int x, int y, int w, int mx, int my, int ac);

		boolean ck(int mx, int my) {
			return false;
		}

		void dg(int mx) {
		}
	}

	public static class Tg extends R {
		Supplier<Boolean> gt;
		Consumer<Boolean> st;
		float k = -1;

		Tg(String l, Supplier<Boolean> a, Consumer<Boolean> b) {
			lb = l;
			gt = a;
			st = b;
		}

		void dr(GuiGraphics g, int x, int y, int w, int mx, int my, int ac) {
			boolean on = gt.get();
			if (k < 0) k = on ? 1 : 0;
			k += ((on ? 1 : 0) - k) * 0.3f;
			int sw = 28;
			int sh = 12;
			int sx = x + w - sw - 8;
			int sy = y + (h - sh) / 2;
			if (k > 0.05f) Ui.glow(g, sx, sy, sw, sh, 6, ac, k * 0.7f);
			Ui.rr(g, sx, sy, sw, sh, 6, Ui.al(Vis.mix(0x2A3350, ac, k), 1f));
			int kx = sx + 2 + (int) ((sw - sh) * k);
			Ui.rr(g, kx, sy + 2, sh - 4, sh - 4, 4, Ui.al(Vis.mix(0xA8B2C8, 0xFFFFFF, k), 1f));
		}

		boolean ck(int mx, int my) {
			st.accept(!gt.get());
			return true;
		}
	}

	public static class Sl extends R {
		IntSupplier gt;
		IntConsumer st;
		int lo;
		int hi;
		String su;

		Sl(String l, IntSupplier a, IntConsumer b, int lo, int hi, String su) {
			lb = l;
			gt = a;
			st = b;
			this.lo = lo;
			this.hi = hi;
			this.su = su;
		}

		int tw() {
			return rw >= 330 ? 120 : rw >= 270 ? 100 : 78;
		}

		void dr(GuiGraphics g, int x, int y, int w, int mx, int my, int ac) {
			int tw = tw();
			int tx = x + w - 10 - tw;
			int ty = y + h / 2 - 2;
			float p = (gt.getAsInt() - lo) / (float) (hi - lo);
			int fw = (int) (tw * p);
			Ui.rr(g, tx, ty, tw, 4, 2, Ui.al(0xFFFFFF, 0.12f));
			if (fw > 1) Ui.rr(g, tx, ty, fw, 4, 2, Ui.al(ac, 1f));
			Ui.glow(g, tx + fw - 4, ty - 2, 8, 8, 4, ac, 0.6f);
			Ui.rr(g, tx + fw - 4, ty - 2, 8, 8, 4, 0xFFFFFFFF);
			String v = gt.getAsInt() + su;
			Ui.tx(g, v, tx - 10 - Ui.w(v), y + (h - 9) / 2, 0xFFFFFF, 0.85f);
		}

		boolean ck(int mx, int my) {
			dg(mx);
			return true;
		}

		void dg(int mx) {
			int tw = tw();
			int tx = rx + rw - 10 - tw;
			float p = Math.max(0f, Math.min(1f, (mx - tx) / (float) tw));
			st.accept(lo + Math.round(p * (hi - lo)));
		}
	}

	public static class Ch extends R {
		String[] o;
		IntSupplier gt;
		IntConsumer st;

		Ch(String l, String[] o, IntSupplier a, IntConsumer b) {
			lb = l;
			this.o = o;
			gt = a;
			st = b;
		}

		int cw() {
			int m = 0;
			for (String s : o) m = Math.max(m, Ui.w(s));
			return m + 34;
		}

		int sw(int i) {
			return Ui.w(o[i]) + 16;
		}

		int tot() {
			int t = 0;
			for (int i = 0; i < o.length; i++) t += sw(i);
			return t + 4;
		}

		boolean seg(int w) {
			return o.length <= 4 && tot() <= w * 0.62f;
		}

		void dr(GuiGraphics g, int x, int y, int w, int mx, int my, int ac) {
			int cy = y + (h - 18) / 2;
			int cur = Math.max(0, Math.min(o.length - 1, gt.getAsInt()));
			if (seg(w)) {
				int tw = tot();
				int cx = x + w - tw - 8;
				Ui.rr(g, cx, cy, tw, 18, 9, Ui.al(0x000000, 0.35f));
				Ui.ro(g, cx, cy, tw, 18, 9, Ui.al(0xFFFFFF, 0.1f));
				int px = cx + 2;
				for (int i = 0; i < o.length; i++) {
					int sw = sw(i);
					boolean on = i == cur;
					boolean ov = mx >= px && mx < px + sw && my >= cy && my < cy + 18;
					if (on) Ui.rr(g, px, cy + 2, sw, 14, 7, Ui.al(ac, 0.9f));
					else if (ov) Ui.rr(g, px, cy + 2, sw, 14, 7, Ui.al(0xFFFFFF, 0.08f));
					Ui.cx(g, o[i], px + sw / 2, cy + 5, 0xFFFFFF, on ? 1f : 0.6f);
					px += sw;
				}
				return;
			}
			int cw = cw();
			int cx = x + w - cw - 8;
			cy = y + (h - 16) / 2;
			Ui.rr(g, cx, cy, cw, 16, 8, Ui.al(0x000000, 0.35f));
			Ui.ro(g, cx, cy, cw, 16, 8, Ui.al(0xFFFFFF, 0.1f));
			String s = o[cur];
			Ui.cx(g, s, cx + cw / 2 + 2, cy + 4, 0xFFFFFF, 1f);
			Ui.tx(g, "<", cx + 8, cy + 4, ac, 1f);
			Ui.tx(g, ">", cx + cw - 8 - Ui.w(">"), cy + 4, ac, 1f);
			int dc = dot(s);
			if (dc >= 0) {
				int dx = cx + cw / 2 - Ui.w(s) / 2 - 7;
				Ui.rr(g, dx, cy + 6, 4, 4, 2, Ui.al(dc, 1f));
			}
		}

		static int dot(String s) {
			switch (s) {
				case "Purple": return 0xA06BFF;
				case "Blue": return 0x3D9BFF;
				case "Pink": return 0xFF5FA8;
				case "Green": return 0x5CE08A;
				case "Red": return 0xFF5050;
				case "Ice": return 0xA8E8FF;
				case "Black": return 0x30343F;
				case "White": return 0xFFFFFF;
				default: return -1;
			}
		}

		boolean ck(int mx, int my) {
			if (seg(rw)) {
				int px = rx + rw - tot() - 8 + 2;
				for (int i = 0; i < o.length; i++) {
					if (mx >= px && mx < px + sw(i)) {
						st.accept(i);
						return true;
					}
					px += sw(i);
				}
				return true;
			}
			int cw = cw();
			int cx = rx + rw - cw - 8;
			int n = gt.getAsInt() + (mx < cx + cw / 2 ? -1 : 1);
			st.accept((n + o.length) % o.length);
			return true;
		}
	}

	public static class Cl extends R {
		IntSupplier gt;
		IntConsumer st;

		Cl(String l, IntSupplier a, IntConsumer b) {
			lb = l;
			gt = a;
			st = b;
			h = 40;
		}

		void dr(GuiGraphics g, int x, int y, int w, int mx, int my, int ac) {
			int px = x + w - 8 - PS.length * 15;
			for (int i = 0; i < PS.length; i++) {
				int sx = px + i * 15;
				boolean on = (gt.getAsInt() & 0xFFFFFF) == PS[i];
				if (on) Ui.glow(g, sx, y + 6, 12, 12, 3, PS[i], 0.8f);
				Ui.rr(g, sx, y + 6, 12, 12, 3, Ui.al(PS[i], 1f));
				if (on) Ui.ro(g, sx - 1, y + 5, 14, 14, 4, 0xFFFFFFFF);
			}
			int sw = PS.length * 15 - 3;
			for (int i = 0; i < sw; i++) g.fill(px + i, y + 24, px + i + 1, y + 32, 0xFF000000 | java.awt.Color.HSBtoRGB(i / (float) sw, 0.7f, 1f));
			Ui.ro(g, px - 1, y + 23, sw + 2, 10, 2, Ui.al(0xFFFFFF, 0.2f));
			float[] hs = java.awt.Color.RGBtoHSB((gt.getAsInt() >> 16) & 255, (gt.getAsInt() >> 8) & 255, gt.getAsInt() & 255, null);
			g.fill(px + (int) (hs[0] * sw) - 1, y + 21, px + (int) (hs[0] * sw) + 1, y + 35, 0xFFFFFFFF);
			Ui.rr(g, x + 8, y + 24, 38, 9, 3, Ui.al(gt.getAsInt(), 1f));
			Ui.ro(g, x + 8, y + 24, 38, 9, 3, Ui.al(0xFFFFFF, 0.25f));
			String hx = String.format("#%06X", gt.getAsInt() & 0xFFFFFF);
			Ui.tx(g, hx, x + 52, y + 24, 0xFFFFFF, 0.55f);
		}

		boolean ck(int mx, int my) {
			int px = rx + rw - 8 - PS.length * 15;
			if (my - ry < 20) {
				int i = (mx - px) / 15;
				dragHue = false;
				if (mx >= px && i >= 0 && i < PS.length) st.accept(PS[i]);
				return true;
			}
			dragHue = true;
			dg(mx);
			return true;
		}

		void dg(int mx) {
			int px = rx + rw - 8 - PS.length * 15;
			int sw = PS.length * 15 - 3;
			if (dragHue) st.accept(java.awt.Color.HSBtoRGB(Math.max(0f, Math.min(0.999f, (mx - px) / (float) sw)), 0.7f, 1f) & 0xFFFFFF);
		}

		boolean dragHue = true;
	}

	public static class Bt extends R {
		Runnable r;
		String ch;

		Bt(String l, String ch, Runnable r) {
			lb = l;
			this.ch = ch;
			this.r = r;
		}

		void dr(GuiGraphics g, int x, int y, int w, int mx, int my, int ac) {
			int cw = Ui.w(ch) + 20;
			int cx = x + w - cw - 8;
			int cy = y + (h - 16) / 2;
			Ui.rr(g, cx, cy, cw, 16, 5, Ui.al(ac, 0.2f + 0.2f * hv));
			Ui.ro(g, cx, cy, cw, 16, 5, Ui.al(ac, 0.7f));
			Ui.cx(g, ch, cx + cw / 2, cy + 4, 0xFFFFFF, 1f);
		}

		boolean ck(int mx, int my) {
			r.run();
			return true;
		}
	}

	public static class Nt extends R {
		Nt(String l) {
			lb = l;
			h = 20;
		}

		void dr(GuiGraphics g, int x, int y, int w, int mx, int my, int ac) {
		}
	}

	public static class Pv extends R {
		Pv() {
			h = 70;
		}

		void dr(GuiGraphics g, int x, int y, int w, int mx, int my, int ac) {
			Ui.rr(g, x + 8, y + 6, w - 16, h - 12, 4, Ui.al(0x3A4A60, 1f));
			g.fillGradient(x + 10, y + 7, x + w - 10, y + h - 7, 0xFF52708F, 0xFF2A3A4C);
			for (int i = 0; i < 6; i++) g.fill(x + 12 + i * ((w - 24) / 6), y + h - 18, x + 12 + i * ((w - 24) / 6) + 18, y + h - 7, Ui.al(0x000000, 0.12f));
			Crs.at(g, x + w / 2, y + h / 2, true);
			Ui.ro(g, x + 8, y + 6, w - 16, h - 12, 4, Ui.al(0xFFFFFF, 0.2f));
		}
	}

	public static class Sw extends R {
		String[] o;
		IntSupplier gt;
		IntConsumer st;
		int cols = 3;

		Sw(String l, String[] o, IntSupplier a, IntConsumer b) {
			lb = l;
			this.o = o;
			gt = a;
			st = b;
			h = 24 + ((o.length + cols - 1) / cols) * 22;
		}

		void dr(GuiGraphics g, int x, int y, int w, int mx, int my, int ac) {
			int cw = (w - 16 - (cols - 1) * 4) / cols;
			for (int i = 0; i < o.length; i++) {
				int cx = x + 8 + (i % cols) * (cw + 4);
				int cy = y + 22 + (i / cols) * 22;
				boolean on = gt.getAsInt() == i;
				boolean ov = mx >= cx && mx < cx + cw && my >= cy && my < cy + 18;
				Ui.rr(g, cx, cy, cw, 18, 6, Ui.al(on ? ac : 0xFFFFFF, on ? 0.22f : ov ? 0.1f : 0.05f));
				Ui.ro(g, cx, cy, cw, 18, 6, Ui.al(on ? ac : 0xFFFFFF, on ? 0.9f : 0.1f));
				int dc = Ch.dot(o[i]);
				int tx = cx + (cw - Ui.w(o[i])) / 2 + (dc >= 0 ? 4 : 0);
				if (dc >= 0) Ui.rr(g, tx - 8, cy + 7, 5, 5, 2, Ui.al(dc, 1f));
				Ui.tx(g, o[i], tx, cy + 5, 0xFFFFFF, on ? 1f : 0.75f);
			}
		}

		boolean ck(int mx, int my) {
			int cw = (rw - 16 - (cols - 1) * 4) / cols;
			for (int i = 0; i < o.length; i++) {
				int cx = rx + 8 + (i % cols) * (cw + 4);
				int cy = ry + 22 + (i / cols) * 22;
				if (mx >= cx && mx < cx + cw && my >= cy && my < cy + 18) {
					st.accept(i);
					return true;
				}
			}
			return false;
		}
	}

	public static class Ed extends R {
		static Ed foc;
		Supplier<String> gt;
		Consumer<String> st;
		int mx;

		Ed(String l, int mx, Supplier<String> a, Consumer<String> b) {
			lb = l;
			this.mx = mx;
			gt = a;
			st = b;
			h = 42;
		}

		void dr(GuiGraphics g, int x, int y, int w, int mx2, int my, int ac) {
			boolean f = foc == this;
			int fx = x + 8;
			int fy = y + 21;
			int fw = w - 16;
			Ui.rr(g, fx, fy, fw, 17, 6, Ui.al(0x000000, 0.35f));
			Ui.ro(g, fx, fy, fw, 17, 6, Ui.al(f ? ac : 0xFFFFFF, f ? 0.9f : 0.14f));
			String s = gt.get();
			boolean car = f && (net.minecraft.util.Util.getMillis() / 500) % 2 == 0;
			String d = s;
			while (Ui.w(d + "_") > fw - 12 && d.length() > 1) d = d.substring(1);
			Ui.tx(g, d, fx + 6, fy + 8 - 4, 0xFFFFFF, 0.95f);
			if (car) g.fill(fx + 7 + Ui.w(d), fy + 3, fx + 8 + Ui.w(d), fy + 14, Ui.al(0xFFFFFF, 0.9f));
		}

		boolean ck(int mx2, int my) {
			foc = this;
			return false;
		}

		void ch(String s) {
			String v = gt.get();
			if (v.length() < mx) st.accept(v + s);
		}

		void bk() {
			String v = gt.get();
			if (!v.isEmpty()) st.accept(v.substring(0, v.length() - 1));
		}
	}

	public static class Kb extends R {
		static Kb foc;
		final Supplier<KeyMapping> km;

		Kb(String l, Supplier<KeyMapping> k) {
			lb = l;
			km = k;
		}

		void dr(GuiGraphics g, int x, int y, int w, int mx, int my, int ac) {
			boolean f = foc == this;
			KeyMapping kk = km.get();
			String s = f ? "Press a key..." : kk == null ? "-" : kk.getTranslatedKeyMessage().getString();
			int cw = Math.max(54, Ui.w(s) + 20);
			int cx = x + w - cw - 8;
			int cy = y + (h - 16) / 2;
			Ui.rr(g, cx, cy, cw, 16, 5, Ui.al(f ? ac : 0xFFFFFF, f ? 0.3f : 0.08f));
			Ui.ro(g, cx, cy, cw, 16, 5, Ui.al(f ? ac : 0xFFFFFF, f ? 0.9f : 0.2f));
			Ui.cx(g, s, cx + cw / 2, cy + 4, 0xFFFFFF, 1f);
		}

		boolean ck(int mx, int my) {
			foc = this;
			return false;
		}

		void bind(int key) {
			KeyMapping kk = km.get();
			if (kk == null || key == org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE) return;
			kk.setKey(com.mojang.blaze3d.platform.InputConstants.Type.KEYSYM.getOrCreate(key));
			net.minecraft.client.KeyMapping.resetMapping();
			net.minecraft.client.Minecraft.getInstance().options.save();
		}
	}

	public static class St extends R {
		St() {
			lb = "Status";
			h = 26;
		}

		void dr(GuiGraphics g, int x, int y, int w, int mx, int my, int ac) {
			String s = Rpc.status;
			boolean ok = s.equals("Connected");
			Ui.rr(g, x + w - 14, y + 11, 5, 5, 2, Ui.al(ok ? 0x5CE08A : 0xFF6060, 1f));
			Ui.tx(g, s, x + w - 18 - Ui.w(s), y + (h - 9) / 2, 0xFFFFFF, 0.8f);
		}
	}

	public static Tg tg(String l, Supplier<Boolean> a, Consumer<Boolean> b) {
		return new Tg(l, a, b);
	}

	public static Sl sl(String l, IntSupplier a, IntConsumer b, int lo, int hi, String su) {
		return new Sl(l, a, b, lo, hi, su);
	}
}
