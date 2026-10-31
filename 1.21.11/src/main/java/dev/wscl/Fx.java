package dev.wscl;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.fog.FogRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Fx {
	public static Identifier glw, bar, edg, vig;
	static final MultiBufferSource.BufferSource BS = MultiBufferSource.immediate(new ByteBufferBuilder(1 << 19));
	static Object old;

	static void tex() {
		if (glw != null) return;
		var tm = Minecraft.getInstance().getTextureManager();
		NativeImage g = new NativeImage(64, 64, false);
		for (int y = 0; y < 64; y++) for (int x = 0; x < 64; x++) {
			double dx = (x - 31.5) / 32, dy = (y - 31.5) / 32;
			double d = dx * dx + dy * dy;
			double v = Math.exp(-d * 5) * 0.75 + Math.exp(-d * 40) * 0.6;
			g.setPixel(x, y, (int) Math.round(255 * Math.min(1, Math.max(0, v - 0.01))) << 24 | 0xFFFFFF);
		}
		glw = Identifier.fromNamespaceAndPath("azure", "fx_glow");
		tm.register(glw, new DynamicTexture(() -> "azure fx glow", g));
		NativeImage b = new NativeImage(4, 64, false);
		for (int y = 0; y < 64; y++) for (int x = 0; x < 4; x++) {
			double v = (y - 31.5) / 32;
			double a = Math.exp(-v * v * 6) * 0.55 + Math.exp(-v * v * 60) * 0.7;
			b.setPixel(x, y, (int) Math.round(255 * Math.min(1, a)) << 24 | 0xFFFFFF);
		}
		bar = Identifier.fromNamespaceAndPath("azure", "fx_bar");
		tm.register(bar, new DynamicTexture(() -> "azure fx bar", b));
		NativeImage e = new NativeImage(64, 64, false);
		for (int y = 0; y < 64; y++) for (int x = 0; x < 64; x++) {
			double ex = Math.min(x, 63 - x) / 32.0, ey = Math.min(y, 63 - y) / 32.0;
			double d = Math.min(ex, ey);
			double a = Math.exp(-d * 18) * 0.95 + 0.06;
			e.setPixel(x, y, (int) Math.round(255 * Math.min(1, a)) << 24 | 0xFFFFFF);
		}
		edg = Identifier.fromNamespaceAndPath("azure", "fx_edge");
		tm.register(edg, new DynamicTexture(() -> "azure fx edge", e));
		NativeImage vg = new NativeImage(128, 128, false);
		for (int y = 0; y < 128; y++) for (int x = 0; x < 128; x++) {
			double dx = (x - 63.5) / 64, dy = (y - 63.5) / 64;
			double d = Math.sqrt(dx * dx * 0.8 + dy * dy);
			double a = Math.pow(Math.max(0, Math.min(1, (d - 0.45) / 0.75)), 1.6);
			vg.setPixel(x, y, (int) Math.round(255 * a) << 24);
		}
		vig = Identifier.fromNamespaceAndPath("azure", "fx_vig");
		tm.register(vig, new DynamicTexture(() -> "azure fx vignette", vg));
	}

	static void vx(VertexConsumer v, double x, double y, double z, int c, float u, float w) {
		v.addVertex((float) x, (float) y, (float) z).setColor(c).setUv(u, w).setOverlay(OverlayTexture.NO_OVERLAY).setLight(0xF000F0).setNormal(0, 1, 0);
	}

	static void q2(VertexConsumer v, double[] a, double[] b, double[] c, double[] d, int ca, int cb) {
		vx(v, a[0], a[1], a[2], ca, 0, 0);
		vx(v, b[0], b[1], b[2], ca, 0, 1);
		vx(v, c[0], c[1], c[2], cb, 1, 1);
		vx(v, d[0], d[1], d[2], cb, 1, 0);
		vx(v, d[0], d[1], d[2], cb, 1, 0);
		vx(v, c[0], c[1], c[2], cb, 1, 1);
		vx(v, b[0], b[1], b[2], ca, 0, 1);
		vx(v, a[0], a[1], a[2], ca, 0, 0);
	}

	static void bb(VertexConsumer v, Vector3f r, Vector3f u, double x, double y, double z, float s, int c) {
		double rx = r.x * s, ry = r.y * s, rz = r.z * s, ux = u.x * s, uy = u.y * s, uz = u.z * s;
		vx(v, x - rx - ux, y - ry - uy, z - rz - uz, c, 0, 1);
		vx(v, x + rx - ux, y + ry - uy, z + rz - uz, c, 1, 1);
		vx(v, x + rx + ux, y + ry + uy, z + rz + uz, c, 1, 0);
		vx(v, x - rx + ux, y - ry + uy, z - rz + uz, c, 0, 0);
		vx(v, x - rx + ux, y - ry + uy, z - rz + uz, c, 0, 0);
		vx(v, x + rx + ux, y + ry + uy, z + rz + uz, c, 1, 0);
		vx(v, x + rx - ux, y + ry - uy, z + rz - uz, c, 1, 1);
		vx(v, x - rx - ux, y - ry - uy, z - rz - uz, c, 0, 1);
	}

	static Vector3f[] cam() {
		var q = Minecraft.getInstance().gameRenderer.getMainCamera().rotation();
		return new Vector3f[]{q.transform(new Vector3f(1, 0, 0)), q.transform(new Vector3f(0, 1, 0))};
	}

	static void box(VertexConsumer v, double x0, double y0, double z0, double x1, double y1, double z1, int c) {
		double[][] p = {{x0, y0, z0}, {x1, y0, z0}, {x1, y1, z0}, {x0, y1, z0}, {x0, y0, z1}, {x1, y0, z1}, {x1, y1, z1}, {x0, y1, z1}};
		int[][] f = {{0, 1, 2, 3}, {5, 4, 7, 6}, {4, 0, 3, 7}, {1, 5, 6, 2}, {3, 2, 6, 7}, {4, 5, 1, 0}};
		for (int[] k : f) {
			double[] a = p[k[0]], b = p[k[1]], cc = p[k[2]], d = p[k[3]];
			vx(v, a[0], a[1], a[2], c, 0, 0);
			vx(v, b[0], b[1], b[2], c, 1, 0);
			vx(v, cc[0], cc[1], cc[2], c, 1, 1);
			vx(v, d[0], d[1], d[2], c, 0, 1);
			vx(v, d[0], d[1], d[2], c, 0, 1);
			vx(v, cc[0], cc[1], cc[2], c, 1, 1);
			vx(v, b[0], b[1], b[2], c, 1, 0);
			vx(v, a[0], a[1], a[2], c, 0, 0);
		}
	}

	static void begin(Matrix4f view) {
		tex();
		var mv = RenderSystem.getModelViewStack();
		mv.pushMatrix();
		mv.mul(view);
		var fr = ((dev.wscl.mixin.GaAcc) Minecraft.getInstance().gameRenderer).azfg();
		old = RenderSystem.getShaderFog();
		RenderSystem.setShaderFog(fr.getBuffer(FogRenderer.FogMode.NONE));
	}

	static void end() {
		BS.endBatch();
		RenderSystem.setShaderFog((com.mojang.blaze3d.buffers.GpuBufferSlice) old);
		RenderSystem.getModelViewStack().popMatrix();
	}

	static int ac(int cm, int cc, float h) {
		if (cm == 1) return Rw.cv(cc) & 0xFFFFFF;
		if (cm == 2) return java.awt.Color.HSBtoRGB(((System.currentTimeMillis() / 4000f + h) % 1f + 1f) % 1f, 0.65f, 1f) & 0xFFFFFF;
		return Hud.ac() & 0xFFFFFF;
	}

	static int ca(int rgb, float a) {
		return Math.round(Math.max(0, Math.min(1, a)) * 255) << 24 | rgb & 0xFFFFFF;
	}
}
