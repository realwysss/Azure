package dev.wscl;

import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Trl {
	static class Tr {
		final ArrayDeque<double[]> p = new ArrayDeque<>();
		long seen, c;
	}

	static final Map<Integer, Tr> all = new HashMap<>();
	static final float[] AO = {-1f, -0.38f, 0f, 0.38f, 1f};
	static final float[] AA = {0f, 0.42f, 1f, 0.42f, 0f};

	static int gi(String k, int d) {
		return Cfg.gi("trails." + k, d);
	}

	static double hy() {
		int h = gi("ht", 0);
		return h == 0 ? 0.12 : h == 1 ? 0.9 : 1.7;
	}

	static void track(Player e, float pt, long r, long life, boolean idl) {
		Tr t = all.computeIfAbsent(e.getId(), k -> new Tr());
		t.seen = r;
		Vec3 p = e.getPosition(pt);
		double[] l = t.p.peekLast();
		if (l == null || Math.abs(l[0] - p.x) + Math.abs(l[1] - p.y) + Math.abs(l[2] - p.z) > 0.12 || idl && r - (long) l[3] > 110) {
			if (l != null && Math.abs(l[0] - p.x) + Math.abs(l[2] - p.z) > 6) t.p.clear();
			t.p.add(new double[]{p.x, p.y, p.z, r, t.c++});
		}
		while (!t.p.isEmpty() && r - t.p.peekFirst()[3] > life) t.p.pollFirst();
	}

	static int cl(int cm, double i, double tm) {
		int c1 = Rw.cv(gi("c1", Rw.AZ)) & 0xFFFFFF, c2 = Rw.cv(gi("c2", 0xFF4AD2)) & 0xFFFFFF;
		if (cm == 2) return java.awt.Color.HSBtoRGB((float) (((i * 0.018 - tm * 0.35) % 1 + 1) % 1), 0.75f, 1f) & 0xFFFFFF;
		if (cm == 3) return Vis.mix(c1, c2, 0.5f + 0.5f * (float) Math.sin(i * 0.12 - tm * 3));
		int b = cm == 1 ? c1 : Hud.ac() & 0xFFFFFF;
		return Vis.mix(0xFFFFFF, b, 0.82f + 0.18f * (float) Math.sin(i * 0.3 - tm * 7));
	}

	static void pv(VertexConsumer v, double[] p, int c) {
		v.addVertex((float) p[0], (float) p[1], (float) p[2]).setColor(c);
	}

	static final int[] TI = {0, 1, 2, 0, 2, 3, 0, 2, 1, 0, 3, 2};

	static void tq(VertexConsumer v, double[][] q, int[] c) {
		for (int k : TI) pv(v, q[k], c[k]);
	}

	static void strip(VertexConsumer v, double[][] c, double[][] s, double[] w, int[] col, float[] a, float core) {
		int n = c.length;
		for (int i = 0; i + 1 < n; i++) for (int j = 0; j + 1 < AO.length; j++) {
			double[][] q = new double[4][];
			int[] qc = new int[4];
			int[] ii = {i, i, i + 1, i + 1};
			int[] jj = {j, j + 1, j + 1, j};
			for (int k = 0; k < 4; k++) {
				int a1 = ii[k], b1 = jj[k];
				double o = AO[b1] * w[a1];
				q[k] = new double[]{c[a1][0] + s[a1][0] * o, c[a1][1] + s[a1][1] * o, c[a1][2] + s[a1][2] * o};
				int cc = b1 == 2 ? Vis.mix(0xFFFFFF, col[a1], core) : col[a1];
				qc[k] = Fx.ca(cc, a[a1] * AA[b1]);
			}
			tq(v, q, qc);
		}
	}

	static void pt(VertexConsumer v, Vector3f a, Vector3f b, double x, double y, double z, double u, double w, int c) {
		v.addVertex((float) (x + a.x * u + b.x * w), (float) (y + a.y * u + b.y * w), (float) (z + a.z * u + b.z * w)).setColor(c);
	}

	static void q4(VertexConsumer v, Vector3f a, Vector3f b, double x, double y, double z, double[] u, int[] c) {
		for (int k : TI) pt(v, a, b, x, y, z, u[k * 2], u[k * 2 + 1], c[k]);
	}

	static void star(VertexConsumer v, Vector3f a, Vector3f b, double x, double y, double z, float s, int col, float al, double rt) {
		int c0 = Fx.ca(Vis.mix(0xFFFFFF, col, 0.45f), al);
		int c1 = Fx.ca(col, al * 0.7f);
		int c2 = Fx.ca(col, 0f);
		int g0 = Fx.ca(col, al * 0.4f);
		double gr = s * 0.55;
		for (int k = 0; k < 8; k++) {
			double a0 = k * Math.PI / 4, a1 = (k + 1) * Math.PI / 4;
			q4(v, a, b, x, y, z, new double[]{0, 0, 0, 0, Math.cos(a0) * gr, Math.sin(a0) * gr, Math.cos(a1) * gr, Math.sin(a1) * gr}, new int[]{g0, g0, c2, c2});
		}
		for (int k = 0; k < 4; k++) {
			double an = rt + k * Math.PI / 2;
			double ux = Math.cos(an), uy = Math.sin(an);
			double sw = s * 0.13;
			q4(v, a, b, x, y, z, new double[]{0, 0, (ux * 0.25 - uy) * sw, (uy * 0.25 + ux) * sw, ux * s, uy * s, (ux * 0.25 + uy) * sw, (uy * 0.25 - ux) * sw}, new int[]{c0, c1, c2, c1});
		}
	}

	static double[][] sd(double[][] c) {
		int n = c.length;
		double[][] o = new double[n][];
		for (int i = 0; i < n; i++) {
			double[] p0 = c[Math.max(0, i - 1)], p1 = c[Math.min(n - 1, i + 1)];
			double tx = p1[0] - p0[0], ty = p1[1] - p0[1], tz = p1[2] - p0[2];
			double x = c[i][0], y = c[i][1], z = c[i][2];
			double sx = ty * z - tz * y, sy = tz * x - tx * z, sz = tx * y - ty * x;
			double sl = Math.sqrt(sx * sx + sy * sy + sz * sz);
			o[i] = sl < 1e-6 ? new double[]{0, 1, 0} : new double[]{sx / sl, sy / sl, sz / sl};
		}
		return o;
	}

	static double cr(double a, double b, double c, double d, double u) {
		return 0.5 * (2 * b + (c - a) * u + (2 * a - 5 * b + 4 * c - d) * u * u + (3 * b - a - 3 * c + d) * u * u * u);
	}

	static double[][] sm(double[][] p) {
		int n = p.length;
		if (n < 3) return p;
		double[][] o = new double[(n - 1) * 4 + 1][];
		int k = 0;
		for (int i = 0; i + 1 < n; i++) {
			double[] a = p[Math.max(0, i - 1)], b = p[i], c = p[i + 1], d = p[Math.min(n - 1, i + 2)];
			for (int s = 0; s < 4; s++) {
				double u = s / 4.0;
				o[k++] = new double[]{cr(a[0], b[0], c[0], d[0], u), cr(a[1], b[1], c[1], d[1], u), cr(a[2], b[2], c[2], d[2], u), b[3] + (c[3] - b[3]) * u, b[4]};
			}
		}
		o[k] = p[n - 1];
		return o;
	}

	public static void draw(Matrix4f view, Vec3 cam, float pt) {
		Minecraft m = Minecraft.getInstance();
		if (m.level == null || m.player == null) return;
		long r = System.currentTimeMillis();
		double tm = r / 1000.0;
		int st = gi("st", 0);
		long life = 1200L * gi("ln", 100) / 100 * (st == 2 ? 3 : 2) / 2;
		boolean idl = st == 2;
		track(m.player, pt, r, life, idl);
		if (gi("ot", 0) == 1) for (Player p : m.level.players()) if (p != m.player && !p.isInvisible() && p.distanceToSqr(cam) < 96 * 96) track(p, pt, r, life, idl);
		all.values().removeIf(t -> r - t.seen > 2000);
		int cm = gi("cm", 0);
		float op = gi("op", 85) / 100f;
		float wd = gi("wd", 100) / 100f;
		boolean gl = gi("gl", 1) == 1;
		double hy = hy();
		int c2 = Rw.cv(gi("c2", 0xFF4AD2)) & 0xFFFFFF;
		Fx.begin(view);
		Vector3f[] cv = Fx.cam();
		VertexConsumer va = Fx.BS.getBuffer(RenderTypes.dragonRays());
		for (Tr t : all.values()) {
			double[][] p = sm(t.p.toArray(new double[0][]));
			int n = p.length;
			if (n < 2) continue;
			double[][] c = new double[n][], s = new double[n][], hs = new double[n][], up = new double[n][];
			double[] w = new double[n];
			int[] col = new int[n];
			float[] a = new float[n];
			for (int i = 0; i < n; i++) {
				double[] p0 = p[Math.max(0, i - 1)], p1 = p[Math.min(n - 1, i + 1)];
				double tx = p1[0] - p0[0], ty = p1[1] - p0[1], tz = p1[2] - p0[2];
				double x = p[i][0] - cam.x, y = p[i][1] + hy - cam.y, z = p[i][2] - cam.z;
				float k = (r - (long) p[i][3]) / (float) life;
				float fa = (float) Math.pow(Math.max(0, 1 - k), 1.4) * Math.min(1f, (r - (long) p[i][3] + 20) / 80f);
				double cd = Math.sqrt(x * x + y * y + z * z);
				fa *= (float) Math.max(0, Math.min(1, (cd - 0.5) / 1.1));
				a[i] = fa * op;
				col[i] = cl(cm, (n - i) / 4.0, tm);
				c[i] = new double[]{x, y, z};
				double sx = ty * z - tz * y, sy = tz * x - tx * z, sz = tx * y - ty * x;
				double sl = Math.sqrt(sx * sx + sy * sy + sz * sz);
				s[i] = sl < 1e-6 ? new double[]{0, 1, 0} : new double[]{sx / sl, sy / sl, sz / sl};
				double hl = Math.sqrt(tx * tx + tz * tz);
				hs[i] = hl < 1e-6 ? new double[]{1, 0, 0} : new double[]{-tz / hl, 0, tx / hl};
				up[i] = new double[]{0, 1, 0};
				w[i] = wd * (0.4 + 0.6 * (1 - k));
			}
			if (st == 0) {
				double[] w1 = new double[n], w2 = new double[n], w3 = new double[n];
				float[] a1 = new float[n], a2 = new float[n], a3 = new float[n];
				for (int i = 0; i < n; i++) {
					float pu = (float) Math.pow(0.5 + 0.5 * Math.sin((n - i) * 0.08 - tm * 8), 6);
					w1[i] = w[i] * (0.16 + 0.08 * pu);
					w2[i] = w[i] * 0.6;
					w3[i] = w[i] * 1.1;
					a1[i] = Math.min(1f, a[i] * (0.75f + 0.6f * pu));
					a2[i] = a[i] * (0.32f + 0.2f * pu);
					a3[i] = a[i] * 0.12f;
				}
				if (gl) {
					strip(va, c, s, w3, col, a3, 1f);
					strip(va, c, s, w2, col, a2, 0.95f);
				}
				strip(va, c, s, w1, col, a1, 0.6f);
				double[][] hc = new double[n][];
				double[] hw = new double[n];
				int[] hcol = new int[n];
				float[] ha = new float[n];
				for (int h = 0; h < 2; h++) {
					for (int i = 0; i < n; i++) {
						double ph = (n - i) * 0.105 - tm * 5 + h * Math.PI;
						double rr = 0.22 * wd * (0.5 + 0.5 * Math.min(1, (n - i) / 32.0));
						double ox = Math.cos(ph) * rr, oy = Math.sin(ph) * rr;
						hc[i] = new double[]{c[i][0] + hs[i][0] * ox, c[i][1] + oy, c[i][2] + hs[i][2] * ox};
						hw[i] = w[i] * 0.04;
						hcol[i] = h == 0 ? c2 : col[i];
						ha[i] = a[i] * 0.6f;
					}
					strip(va, hc, sd(hc), hw, hcol, ha, 0.4f);
				}
			} else if (st == 1) {
				double[] off = {-0.3, 0, 0.3};
				double[] oy = {0.0, 0.34, 0.0};
				double[] lw = new double[n];
				for (int i = 0; i < n; i++) lw[i] = w[i] * 0.07;
				for (int q = 0; q < 3; q++) {
					double[][] lc = new double[n][];
					for (int i = 0; i < n; i++) lc[i] = new double[]{c[i][0] + hs[i][0] * off[q] * wd, c[i][1] + oy[q], c[i][2] + hs[i][2] * off[q] * wd};
					strip(va, lc, s, lw, col, a, 0.2f);
				}
			} else if (st == 3) {
				double[] y0 = new double[n], y1 = new double[n], ym = new double[n];
				float[] ry = new float[n];
				double[][] bt = new double[n][];
				double[] bw = new double[n];
				for (int i = 0; i < n; i++) {
					y0[i] = c[i][1] - hy + 0.03;
					y1[i] = y0[i] + 1.9 * wd * (0.75 + 0.25 * Math.sin((n - i) * 0.05 + tm * 1.7));
					ym[i] = y0[i] + (y1[i] - y0[i]) * 0.42;
					ry[i] = 0.45f + 0.55f * (float) Math.pow(0.5 + 0.5 * Math.sin((n - i) * 0.09 - tm * 3.2), 3);
					bt[i] = new double[]{c[i][0], y0[i], c[i][2]};
					bw[i] = 0.06 * wd;
				}
				for (int i = 0; i + 1 < n; i++) {
					int j = i + 1;
					int cb0 = Fx.ca(col[i], Math.min(1f, a[i] * 1.05f * ry[i])), cb1 = Fx.ca(col[j], Math.min(1f, a[j] * 1.05f * ry[j]));
					int cm0 = Fx.ca(Vis.mix(col[i], c2, 0.35f), a[i] * 0.72f * ry[i]), cm1 = Fx.ca(Vis.mix(col[j], c2, 0.35f), a[j] * 0.72f * ry[j]);
					int ct0 = Fx.ca(c2, 0f), ct1 = Fx.ca(c2, 0f);
					tq(va, new double[][]{{c[i][0], y0[i], c[i][2]}, {c[i][0], ym[i], c[i][2]}, {c[j][0], ym[j], c[j][2]}, {c[j][0], y0[j], c[j][2]}}, new int[]{cb0, cm0, cm1, cb1});
					tq(va, new double[][]{{c[i][0], ym[i], c[i][2]}, {c[i][0], y1[i], c[i][2]}, {c[j][0], y1[j], c[j][2]}, {c[j][0], ym[j], c[j][2]}}, new int[]{cm0, ct0, ct1, cm1});
				}
				strip(va, bt, sd(bt), bw, col, a, 0.5f);
			}
		}
		if (st == 0) {
			for (Tr t : all.values()) {
				for (double[] q : t.p) {
					long id = (long) q[4];
					if (id % 3 != 0) continue;
					long ag = r - (long) q[3];
					float k = ag / (float) life;
					float h1 = Lyr.hs(id * 7 + 1), h2 = Lyr.hs(id * 13 + 5), h3 = Lyr.hs(id * 29 + 11), h4 = Lyr.hs(id * 41 + 3);
					double ek = 1 - Math.pow(1 - Math.min(1, k), 2);
					double x = q[0] - cam.x + (h1 - 0.5) * 0.5 * ek, y = q[1] + hy - cam.y + (h3 - 0.3) * 0.5 * ek - 0.15 * k * k, z = q[2] - cam.z + (h2 - 0.5) * 0.5 * ek;
					double cd = Math.sqrt(x * x + y * y + z * z);
					float fa = Math.min(1f, ag / 120f) * (float) Math.pow(Math.max(0, 1 - k), 1.3) * (float) Math.max(0, Math.min(1, (cd - 0.5) / 1.1));
					if (fa < 0.02f) continue;
					float tw = 0.7f + 0.3f * (float) Math.sin(tm * 9 + h4 * 6.28);
					float s = 0.14f * wd * (0.6f + 0.4f * h4) * (1 - k * 0.5f);
					star(va, cv[0], cv[1], x, y, z, s, cl(cm, (int) (id % 100000), tm), fa * op * tw, h1 * 3 + tm * (h2 - 0.5) * 1.5);
				}
			}
		} else if (st == 2) {
			for (Tr t : all.values()) {
				for (double[] q : t.p) {
					for (int mm = 0; mm < 2; mm++) {
						long id = (long) q[4] * 2 + mm;
						long ag = r - (long) q[3] - mm * 90L;
						if (ag < 0) continue;
						float k = ag / (float) life;
						if (k >= 1) continue;
						float h1 = Lyr.hs(id * 7 + 1), h2 = Lyr.hs(id * 13 + 5), h3 = Lyr.hs(id * 29 + 11), h4 = Lyr.hs(id * 41 + 3), h5 = Lyr.hs(id * 53 + 9);
						double an = h1 * Math.PI * 2, rr = (0.15 + 0.35 * h2) * wd;
						double ek = 1 - Math.pow(1 - k, 3);
						double sw = Math.sin(tm * 2.2 + h5 * 6.28) * 0.06 * k;
						double x = q[0] - cam.x + Math.cos(an) * rr * (0.45 + 0.8 * ek) + sw;
						double y = q[1] - cam.y + 0.1 + h3 * 1.6 + 0.25 * ek - 0.6 * k * k;
						double z = q[2] - cam.z + Math.sin(an) * rr * (0.45 + 0.8 * ek) - sw;
						double cd = Math.sqrt(x * x + y * y + z * z);
						float pp = Math.min(1f, ag / 140f);
						float pop = Lyr.bk(pp);
						float fa = (float) Math.pow(1 - k, 1.1) * (float) Math.max(0, Math.min(1, (cd - 0.5) / 1.1));
						if (fa < 0.02f) continue;
						float tw = 0.45f + 0.55f * (float) Math.pow(0.5 + 0.5 * Math.sin(tm * (7 + h4 * 6) + h5 * 6.28), 2);
						float sz = 0.3f * wd * (0.45f + 0.75f * h4 * h4) * (1 - k * 0.45f) * pop;
						int cc = cl(cm, (int) (id % 100000), tm);
						star(va, cv[0], cv[1], x, y, z, sz, cc, Math.min(1f, fa * op * tw * 1.25f), h1 * 3 + tm * (h2 - 0.5) * 2);
						if (h5 > 0.7f) star(va, cv[0], cv[1], x, y, z, sz * 1.9f, cc, fa * op * tw * 0.25f, -h1 * 3 + Math.PI / 4);
					}
				}
			}
		}
		Fx.end();
	}
}
