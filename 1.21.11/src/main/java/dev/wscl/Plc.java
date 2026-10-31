package dev.wscl;

import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Plc {
	static class An {
		AABB b;
		long st;
		float hu;
		double[][] sk;
	}

	static final List<An> an = new ArrayList<>();
	static final Random RN = new Random();
	static BlockPos[] cp = new BlockPos[2];
	static BlockState[] cs = new BlockState[2];

	static int gi(String k, int d) {
		return Cfg.gi("place." + k, d);
	}

	public static void pre(BlockHitResult r) {
		Minecraft m = Minecraft.getInstance();
		if (!Mod.live("place") || m.level == null) {
			cp[0] = null;
			return;
		}
		cp[0] = r.getBlockPos();
		cp[1] = r.getBlockPos().relative(r.getDirection());
		for (int i = 0; i < 2; i++) cs[i] = m.level.getBlockState(cp[i]);
	}

	public static void post() {
		Minecraft m = Minecraft.getInstance();
		if (cp[0] == null || m.level == null) return;
		for (int i = 0; i < 2; i++) {
			BlockState s = m.level.getBlockState(cp[i]);
			if (s != cs[i] && !s.isAir()) {
				add(cp[i], s);
				break;
			}
		}
		cp[0] = null;
	}

	public static void add(BlockPos p, BlockState s) {
		Minecraft m = Minecraft.getInstance();
		VoxelShape sh = s.getShape(m.level, p);
		AABB b = sh.isEmpty() ? new AABB(p) : sh.bounds().move(p);
		An a = new An();
		a.b = b;
		a.st = System.currentTimeMillis();
		a.hu = RN.nextFloat();
		a.sk = new double[12][];
		for (int i = 0; i < a.sk.length; i++) {
			double th = RN.nextDouble() * Math.PI * 2;
			double sp = 1.5 + RN.nextDouble() * 2.5;
			a.sk[i] = new double[]{Math.cos(th) * sp, 1.5 + RN.nextDouble() * 3, Math.sin(th) * sp};
		}
		an.add(a);
		if (an.size() > 24) an.remove(0);
	}

	static float bk(float p) {
		float c = 1.70158f, q = p - 1;
		return 1 + (c + 1) * q * q * q + c * q * q;
	}

	public static void draw(Matrix4f view, Vec3 cam) {
		if (an.isEmpty()) return;
		long r = System.currentTimeMillis();
		float sp = gi("sp", 100) / 100f;
		long du = (long) (520 / sp);
		an.removeIf(a -> r - a.st > du * 2);
		if (an.isEmpty()) return;
		int st = gi("st", 0);
		int cm = gi("cm", 0);
		Fx.begin(view);
		Rn.tex();
		Vector3f[] cv = Fx.cam();
		VertexConsumer ve = Fx.BS.getBuffer(RenderTypes.entityTranslucentEmissive(Fx.edg));
		for (An a : an) {
			float p = Math.min(1f, (r - a.st) / (float) du);
			int c = Fx.ac(cm, gi("c1", Rw.AZ), a.hu);
			float sc, al;
			if (st == 0) {
				sc = 0.55f + 0.45f * bk(p);
				al = 1 - p * p;
			} else if (st == 1) {
				float e = p * p;
				sc = 1.6f - 0.6f * Math.min(1f, e * 1.6f);
				al = p < 0.62f ? p / 0.62f : 1 - (p - 0.62f) / 0.38f;
			} else {
				sc = 1f + 0.18f * p;
				al = (1 - p) * (1 - p);
			}
			if (p >= 1) continue;
			Vec3 ct = a.b.getCenter();
			double hx = a.b.getXsize() / 2 * sc + 0.003, hy = a.b.getYsize() / 2 * sc + 0.003, hz = a.b.getZsize() / 2 * sc + 0.003;
			double x = ct.x - cam.x, y = ct.y - cam.y, z = ct.z - cam.z;
			if (st == 1) y += (1 - Math.min(1f, p * 1.6f)) * 0.35;
			Fx.box(ve, x - hx, y - hy, z - hz, x + hx, y + hy, z + hz, Fx.ca(c, al * 0.9f));
			if (st == 2) {
				double o = 0.02 + 0.5 * p;
				Fx.box(ve, x - hx - o, y - hy - o, z - hz - o, x + hx + o, y + hy + o, z + hz + o, Fx.ca(c, al * 0.4f));
			}
		}
		if (gi("rg", 1) == 1) {
			VertexConsumer vr = Fx.BS.getBuffer(RenderTypes.entityTranslucentEmissive(Rn.rtx));
			for (An a : an) {
				float p = Math.min(1f, (r - a.st - (gi("st", 0) == 1 ? du * 0.4f : 0)) / (float) du);
				if (p <= 0 || p >= 1) continue;
				Vec3 ct = a.b.getCenter();
				double rad = 0.5 + 1.3 * Math.sqrt(p);
				double x = ct.x - cam.x, y = a.b.minY - cam.y + 0.02, z = ct.z - cam.z;
				int c = Fx.ca(Fx.ac(cm, gi("c1", Rw.AZ), a.hu), (1 - p) * (1 - p) * 0.8f);
				Fx.q2(vr, new double[]{x - rad, y, z - rad}, new double[]{x - rad, y, z + rad}, new double[]{x + rad, y, z + rad}, new double[]{x + rad, y, z - rad}, c, c);
			}
		}
		if (gi("sk", 1) == 1) {
			VertexConsumer vg = Fx.BS.getBuffer(RenderTypes.entityTranslucentEmissive(Fx.glw));
			for (An a : an) {
				float tt = (r - a.st) / 1000f * sp;
				float p = (r - a.st) / (float) (du * 2);
				if (p >= 1) continue;
				Vec3 ct = a.b.getCenter();
				int c = Fx.ac(cm, gi("c1", Rw.AZ), a.hu);
				for (double[] s : a.sk) {
					double x = ct.x + s[0] * tt * 0.35 - cam.x, y = a.b.minY + 0.1 + s[1] * tt - 7 * tt * tt - cam.y, z = ct.z + s[2] * tt * 0.35 - cam.z;
					Fx.bb(vg, cv[0], cv[1], x, y, z, 0.06f * (1 - p * 0.5f), Fx.ca(Vis.mix(0xFFFFFF, c, 0.6f), (1 - p) * 0.9f));
				}
			}
		}
		Fx.end();
	}
}
