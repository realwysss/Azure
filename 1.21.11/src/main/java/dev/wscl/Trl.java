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
		long seen;
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

	static void track(Player e, float pt, long r, long life) {
		Tr t = all.computeIfAbsent(e.getId(), k -> new Tr());
		t.seen = r;
		Vec3 p = e.getPosition(pt);
		double[] l = t.p.peekLast();
		if (l == null || Math.abs(l[0] - p.x) + Math.abs(l[1] - p.y) + Math.abs(l[2] - p.z) > 0.12) {
			if (l != null && Math.abs(l[0] - p.x) + Math.abs(l[2] - p.z) > 6) t.p.clear();
			t.p.add(new double[]{p.x, p.y, p.z, r});
		}
		while (!t.p.isEmpty() && r - t.p.peekFirst()[3] > life) t.p.pollFirst();
	}

	static int cl(int cm, int i, double tm) {
		int c1 = Rw.cv(gi("c1", Rw.AZ)) & 0xFFFFFF, c2 = Rw.cv(gi("c2", 0xFF4AD2)) & 0xFFFFFF;
		if (cm == 2) return java.awt.Color.HSBtoRGB((float) (((i * 0.018 - tm * 0.35) % 1 + 1) % 1), 0.75f, 1f) & 0xFFFFFF;
		if (cm == 3) return Vis.mix(c1, c2, 0.5f + 0.5f * (float) Math.sin(i * 0.12 - tm * 3));
		int b = cm == 1 ? c1 : Hud.ac() & 0xFFFFFF;
		return Vis.mix(0xFFFFFF, b, 0.82f + 0.18f * (float) Math.sin(i * 0.3 - tm * 7));
	}

	static void pv(VertexConsumer v, double[] p, int c) {
		v.addVertex((float) p[0], (float) p[1], (float) p[2]).setColor(c);
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
			for (int k = 0; k < 4; k++) pv(v, q[k], qc[k]);
			for (int k = 3; k >= 0; k--) pv(v, q[k], qc[k]);
		}
	}

	public static void draw(Matrix4f view, Vec3 cam, float pt) {
		Minecraft m = Minecraft.getInstance();
		if (m.level == null || m.player == null) return;
		long r = System.currentTimeMillis();
		double tm = r / 1000.0;
		long life = 1200L * gi("ln", 100) / 100;
		track(m.player, pt, r, life);
		if (gi("ot", 0) == 1) for (Player p : m.level.players()) if (p != m.player && !p.isInvisible() && p.distanceToSqr(cam) < 96 * 96) track(p, pt, r, life);
		all.values().removeIf(t -> r - t.seen > 2000);
		int st = gi("st", 0);
		int cm = gi("cm", 0);
		float op = gi("op", 85) / 100f;
		float wd = gi("wd", 100) / 100f;
		boolean gl = gi("gl", 1) == 1;
		double hy = hy();
		int c2 = Rw.cv(gi("c2", 0xFF4AD2)) & 0xFFFFFF;
		Fx.begin(view);
		Vector3f[] cv = Fx.cam();
		VertexConsumer va = Fx.BS.getBuffer(RenderTypes.lightning());
		for (Tr t : all.values()) {
			double[][] p = t.p.toArray(new double[0][]);
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
				col[i] = cl(cm, n - i, tm);
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
				double[] w1 = new double[n], w2 = new double[n];
				float[] a2 = new float[n];
				for (int i = 0; i < n; i++) {
					w1[i] = w[i] * 0.2;
					w2[i] = w[i] * 0.55;
					a2[i] = a[i] * 0.35f;
				}
				if (gl) strip(va, c, s, w2, col, a2, 0.6f);
				strip(va, c, s, w1, col, a, 0.25f);
				double[][] hc = new double[n][];
				double[] hw = new double[n];
				int[] hcol = new int[n];
				float[] ha = new float[n];
				for (int h = 0; h < 2; h++) {
					for (int i = 0; i < n; i++) {
						double ph = (n - i) * 0.42 - tm * 5 + h * Math.PI;
						double rr = 0.22 * wd * (0.5 + 0.5 * Math.min(1, (n - i) / 8.0));
						double ox = Math.cos(ph) * rr, oy = Math.sin(ph) * rr;
						hc[i] = new double[]{c[i][0] + hs[i][0] * ox, c[i][1] + oy, c[i][2] + hs[i][2] * ox};
						hw[i] = w[i] * 0.04;
						hcol[i] = h == 0 ? c2 : col[i];
						ha[i] = a[i] * 0.6f;
					}
					strip(va, hc, s, hw, hcol, ha, 0.4f);
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
				double[][] mc = new double[n][];
				double[] mw = new double[n];
				for (int i = 0; i < n; i++) {
					mc[i] = new double[]{c[i][0], c[i][1] - hy + 0.9, c[i][2]};
					mw[i] = 0.95;
				}
				float[] a3 = new float[n];
				for (int i = 0; i < n; i++) a3[i] = a[i] * 0.38f;
				strip(va, mc, up, mw, col, a3, 0.85f);
			}
		}
		if (st == 0 || st == 2) {
			VertexConsumer vg = Fx.BS.getBuffer(RenderTypes.entityTranslucentEmissive(Fx.glw));
			for (Tr t : all.values()) {
				int i = 0;
				int n = t.p.size();
				for (double[] q : t.p) {
					i++;
					if (st == 0 && i % 3 != 0) continue;
					long ag = r - (long) q[3];
					float k = ag / (float) life;
					long sd = (long) q[3] * 31 + i;
					double jx = ((sd * 2654435761L >> 8) & 255) / 255.0 - 0.5, jz = ((sd * 40503L >> 4) & 255) / 255.0 - 0.5, jy = ((sd * 69069L >> 6) & 255) / 255.0;
					double sp = st == 2 ? 0.9 : 0.6;
					double x = q[0] - cam.x + jx * sp * k, y = q[1] + hy - cam.y + (jy * 0.7 + 0.2) * k, z = q[2] - cam.z + jz * sp * k;
					double cd = Math.sqrt(x * x + y * y + z * z);
					float fa = (float) Math.pow(Math.max(0, 1 - k), 1.2) * (float) Math.max(0, Math.min(1, (cd - 0.5) / 1.1));
					float tw = 0.6f + 0.4f * (float) Math.sin(tm * 14 + i * 1.7);
					float s = (st == 2 ? 0.3f : 0.12f) * wd * (1 - k * 0.6f);
					int cc = cl(cm, n - i, tm);
					Fx.bb(vg, cv[0], cv[1], x, y, z, s, Fx.ca(cc, fa * op * tw * 0.8f));
					Fx.bb(vg, cv[0], cv[1], x, y, z, s * 0.3f, Fx.ca(0xFFFFFF, fa * op * tw));
				}
			}
		}
		Fx.end();
	}
}
