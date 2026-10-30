package dev.wscl;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.BlockOutlineRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class Bhl {
	static double cx, cy, cz;
	static BlockPos lp;
	static long ft;
	static long lt;
	static float mpv;
	static int cm;
	static int bc;
	static float ca;
	static double wx, wy, wz;

	static int gi(String k, int d) {
		return Cfg.gi("bhl." + k, d);
	}

	static void use(int mode, int base, float a) {
		cm = mode;
		bc = base & 0xFFFFFF;
		ca = a;
	}

	static int vc(double x, double y, double z) {
		long t = System.currentTimeMillis();
		float sp = gi("rs", 50) / 50f;
		int c;
		switch (cm) {
			case 1:
				c = Hud.ac() & 0xFFFFFF;
				break;
			case 2:
				c = java.awt.Color.HSBtoRGB((t * sp / 4000f) % 1f, gi("cs", 70) / 100f, 1f) & 0xFFFFFF;
				break;
			case 3: {
				double d = (x + wx) * 0.35 + (y + wy) * 0.5 + (z + wz) * 0.35;
				c = java.awt.Color.HSBtoRGB((float) ((t * sp / 3000.0 + d) % 1.0 + 1.0) % 1f, gi("cs", 70) / 100f, 1f) & 0xFFFFFF;
				break;
			}
			case 4:
				c = Vis.mix(bc, gi("c2", 0x4AA8FF) & 0xFFFFFF, 0.5f + 0.5f * (float) Math.sin(t * sp / 600.0 + (x + y + z + wx + wy + wz) * 1.2));
				break;
			case 5:
				c = Vis.mix(bc, 0xFFFFFF, 0.5f + 0.5f * (float) Math.sin(t * sp / 300.0));
				break;
			default:
				c = bc;
		}
		return Math.max(0, Math.min(255, Math.round(ca * 255))) << 24 | c;
	}

	public static void draw(MultiBufferSource.BufferSource bs, PoseStack ps, BlockOutlineRenderState st, Vec3 cam) {
		long now = System.currentTimeMillis();
		float dt = lt == 0 ? 0.016f : Math.min(0.1f, (now - lt) / 1000f);
		lt = now;
		int mode = gi("mode", 2);
		if (mode == 3) return;
		BlockPos p = st.pos();
		boolean sm = gi("smv", 1) == 1;
		if (lp == null || !sm || Math.abs(cx - p.getX()) + Math.abs(cy - p.getY()) + Math.abs(cz - p.getZ()) > 6) {
			cx = p.getX();
			cy = p.getY();
			cz = p.getZ();
		}
		if (!p.equals(lp)) {
			if (lp == null || now - ft > 400) ft = now;
			lp = p;
			mpv = 0;
		}
		float k = Math.min(1f, dt * 22f);
		cx += (p.getX() - cx) * k;
		cy += (p.getY() - cy) * k;
		cz += (p.getZ() - cz) * k;
		wx = cam.x;
		wy = cam.y;
		wz = cam.z;
		float fa = gi("fi", 1) == 1 ? Math.min(1f, (now - ft) / 160f) : 1f;
		if (gi("br", 0) == 1) fa *= 0.55f + 0.45f * (float) Math.sin(now * (gi("bs", 50) / 50.0) / 260.0);
		double g = gi("gr", 0) / 100.0;
		java.util.List<AABB> bx = gi("shp", 0) == 1 || st.shape().isEmpty() ? java.util.List.of(new AABB(0, 0, 0, 1, 1, 1)) : st.shape().toAabbs();
		double ox = cx - cam.x;
		double oy = cy - cam.y;
		double oz = cz - cam.z;
		PoseStack.Pose pe = ps.last();
		int ocm = gi("cm", 0);
		int oc = gi("oc", 0xFFFFFF);
		float lw = gi("lw", 2) * Minecraft.getInstance().getWindow().getAppropriateLineWidth() / 2f;
		if (mode == 1 || mode == 2) {
			int sm2 = gi("sm", 0);
			use(sm2 == 0 ? ocm : sm2 == 2 ? 3 : 0, sm2 == 1 ? gi("sc", 0x4AA8FF) : oc, gi("sa", 22) / 100f * fa);
			VertexConsumer q = bs.getBuffer(RenderTypes.debugQuads());
			for (AABB b : bx) fill(q, pe, b.inflate(g + 0.002).move(ox, oy, oz));
			bs.endBatch(RenderTypes.debugQuads());
		}
		var gm = Minecraft.getInstance().gameMode;
		int ds = gm == null || !gm.isDestroying() ? -1 : gm.getDestroyStage();
		float tg = ds < 0 ? 0f : (ds + 1) / 10f;
		mpv += (tg - mpv) * Math.min(1f, dt * 14f);
		if (gi("mp", 1) == 1 && mpv > 0.01f) {
			AABB u = bx.get(0);
			for (AABB b : bx) u = u.minmax(b);
			double f = mpv;
			int ms = gi("ms", 0);
			AABB a;
			if (ms == 1) a = new AABB(u.minX, u.minY, u.minZ, u.maxX, u.minY + (u.maxY - u.minY) * f, u.maxZ);
			else {
				double s = ms == 2 ? 1 - f * 0.9 : f;
				Vec3 c = u.getCenter();
				double hx = (u.maxX - u.minX) * s / 2, hy = (u.maxY - u.minY) * s / 2, hz = (u.maxZ - u.minZ) * s / 2;
				a = new AABB(c.x - hx, c.y - hy, c.z - hz, c.x + hx, c.y + hy, c.z + hz);
			}
			a = a.inflate(g + 0.003).move(ox, oy, oz);
			int mc = gi("mcm", 0);
			int base = mc == 0 ? Vis.mix(0xFF5050, 0x5CE08A, (float) f) : mc == 1 ? gi("mc", 0xFF4AD2) : oc;
			use(mc == 2 ? 3 : mc == 3 ? ocm : 0, base, gi("ma", 40) / 100f);
			VertexConsumer q = bs.getBuffer(RenderTypes.debugQuads());
			fill(q, pe, a);
			bs.endBatch(RenderTypes.debugQuads());
			if (gi("mo", 1) == 1) {
				use(cm, base, Math.min(1f, gi("ma", 40) / 100f + 0.4f));
				VertexConsumer v = bs.getBuffer(RenderTypes.lines());
				box(v, pe, a, lw, false);
				bs.endBatch(RenderTypes.lines());
			}
		}
		if (mode == 0 || mode == 2) {
			use(ocm, oc, gi("oa", 100) / 100f * fa);
			boolean cr = gi("ost", 0) == 1;
			VertexConsumer v = bs.getBuffer(RenderTypes.lines());
			for (AABB b : bx) box(v, pe, b.inflate(g + 0.004).move(ox, oy, oz), lw, cr);
			bs.endBatch(RenderTypes.lines());
		}
	}

	static void fill(VertexConsumer q, PoseStack.Pose pe, AABB a) {
		float x0 = (float) a.minX, y0 = (float) a.minY, z0 = (float) a.minZ, x1 = (float) a.maxX, y1 = (float) a.maxY, z1 = (float) a.maxZ;
		float[][] f = {
				{x0, y0, z0, x1, y0, z0, x1, y0, z1, x0, y0, z1},
				{x0, y1, z0, x0, y1, z1, x1, y1, z1, x1, y1, z0},
				{x0, y0, z0, x0, y1, z0, x1, y1, z0, x1, y0, z0},
				{x0, y0, z1, x1, y0, z1, x1, y1, z1, x0, y1, z1},
				{x0, y0, z0, x0, y0, z1, x0, y1, z1, x0, y1, z0},
				{x1, y0, z0, x1, y1, z0, x1, y1, z1, x1, y0, z1}};
		for (float[] s : f) for (int j = 0; j < 12; j += 3) q.addVertex(pe, s[j], s[j + 1], s[j + 2]).setColor(vc(s[j], s[j + 1], s[j + 2]));
	}

	static void ln(VertexConsumer v, PoseStack.Pose pe, double ax, double ay, double az, double bx, double by, double bz, float w) {
		Vector3f n = new Vector3f((float) (bx - ax), (float) (by - ay), (float) (bz - az)).normalize();
		v.addVertex(pe, (float) ax, (float) ay, (float) az).setColor(vc(ax, ay, az)).setNormal(pe, n).setLineWidth(w);
		v.addVertex(pe, (float) bx, (float) by, (float) bz).setColor(vc(bx, by, bz)).setNormal(pe, n).setLineWidth(w);
	}

	static void edge(VertexConsumer v, PoseStack.Pose pe, double ax, double ay, double az, double bx, double by, double bz, float w, boolean cr) {
		if (!cr) {
			ln(v, pe, ax, ay, az, bx, by, bz, w);
			return;
		}
		double f = 0.25;
		ln(v, pe, ax, ay, az, ax + (bx - ax) * f, ay + (by - ay) * f, az + (bz - az) * f, w);
		ln(v, pe, bx, by, bz, bx + (ax - bx) * f, by + (ay - by) * f, bz + (az - bz) * f, w);
	}

	static void box(VertexConsumer v, PoseStack.Pose pe, AABB a, float w, boolean cr) {
		double x0 = a.minX, y0 = a.minY, z0 = a.minZ, x1 = a.maxX, y1 = a.maxY, z1 = a.maxZ;
		edge(v, pe, x0, y0, z0, x1, y0, z0, w, cr);
		edge(v, pe, x0, y0, z1, x1, y0, z1, w, cr);
		edge(v, pe, x0, y1, z0, x1, y1, z0, w, cr);
		edge(v, pe, x0, y1, z1, x1, y1, z1, w, cr);
		edge(v, pe, x0, y0, z0, x0, y1, z0, w, cr);
		edge(v, pe, x1, y0, z0, x1, y1, z0, w, cr);
		edge(v, pe, x0, y0, z1, x0, y1, z1, w, cr);
		edge(v, pe, x1, y0, z1, x1, y1, z1, w, cr);
		edge(v, pe, x0, y0, z0, x0, y0, z1, w, cr);
		edge(v, pe, x1, y0, z0, x1, y0, z1, w, cr);
		edge(v, pe, x0, y1, z0, x0, y1, z1, w, cr);
		edge(v, pe, x1, y1, z0, x1, y1, z1, w, cr);
	}
}
