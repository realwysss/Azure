package dev.wscl;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MappableRingBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.fog.FogRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class Rn {
	static final int HN = 96;
	static final int MX = 3200;
	static final int SZ = new Std140SizeCalculator().putMat4f().putMat4f().putMat4f().putMat4f().putVec4().putVec4().putVec4().putVec4().get();
	public static float wet;
	static int ox, oy, oz;
	static final int[] hh = new int[HN * HN];
	static final boolean[] ok = new boolean[HN * HN];
	static boolean hok;
	static int tk;
	static RenderPipeline pp;
	static TextureTarget sc;
	static MappableRingBuffer ub;
	static DynamicTexture hm;
	static Identifier stx, rtx;
	static final double[] dx = new double[MX], dy = new double[MX], dz = new double[MX];
	static final float[] dv = new float[MX], dl = new float[MX];
	static int dn;
	static final List<double[]> spl = new ArrayList<>();
	static final Random RN = new Random();
	static long lt;
	static final MultiBufferSource.BufferSource BS = MultiBufferSource.immediate(new ByteBufferBuilder(1 << 19));

	static int gi(String k, int d) {
		return Cfg.gi("rain." + k, d);
	}

	static boolean sky(ClientLevel l) {
		return l != null && l.dimensionType().hasSkyLight() && !l.dimensionType().hasCeiling();
	}

	public static boolean always() {
		return Mod.live("rain") && gi("al", 0) == 1;
	}

	public static boolean hide() {
		Minecraft m = Minecraft.getInstance();
		if (!Mod.live("rain") || gi("vr", 0) == 1 || m.player == null || m.level == null) return false;
		BlockPos p = m.player.blockPosition();
		return m.level.getBiome(p).value().getPrecipitationAt(p, m.level.getSeaLevel()) != Biome.Precipitation.SNOW;
	}

	public static void tick() {
		Minecraft m = Minecraft.getInstance();
		ClientLevel l = m.level;
		boolean on = Mod.live("rain") && sky(l) && m.player != null;
		float tg = on ? l.getRainLevel(1f) : 0f;
		wet += (tg - wet) * (tg > wet ? 0.025f : 0.005f);
		if (wet < 0.001f) wet = 0;
		if (!on || tg <= 0 && wet <= 0) {
			hok = false;
			return;
		}
		if (tk++ % 10 != 0 && hok) return;
		BlockPos pp = m.player.blockPosition();
		ox = pp.getX() - HN / 2;
		oz = pp.getZ() - HN / 2;
		oy = pp.getY();
		int sl = l.getSeaLevel();
		BlockPos.MutableBlockPos q = new BlockPos.MutableBlockPos();
		for (int z = 0; z < HN; z++) for (int x = 0; x < HN; x++) {
			int wx = ox + x, wz = oz + z;
			int h = l.getHeight(Heightmap.Types.MOTION_BLOCKING, wx, wz);
			hh[z * HN + x] = h;
			if ((x & 3) == 0 && (z & 3) == 0) {
				q.set(wx, h, wz);
				boolean r = l.getBiome(q).value().getPrecipitationAt(q, sl) == Biome.Precipitation.RAIN;
				for (int b = 0; b < 4; b++) for (int a = 0; a < 4; a++) if (x + a < HN && z + b < HN) ok[(z + b) * HN + x + a] = r;
			}
		}
		hok = true;
		if (hm == null) {
			hm = new DynamicTexture(() -> "azure rain height", new NativeImage(HN, HN, false));
			m.getTextureManager().register(Identifier.fromNamespaceAndPath("azure", "rain_h"), hm);
		}
		NativeImage im = hm.getPixels();
		for (int z = 0; z < HN; z++) for (int x = 0; x < HN; x++) {
			int i = z * HN + x;
			int v = Math.max(0, Math.min(255, hh[i] - oy + 128));
			im.setPixel(x, z, 0xFF000000 | v << 16 | (ok[i] ? 255 : 0) << 8);
		}
		hm.upload();
	}

	static int col(int x, int z) {
		int a = x - ox, b = z - oz;
		if (a < 0 || b < 0 || a >= HN || b >= HN) return Integer.MIN_VALUE;
		return ok[b * HN + a] ? hh[b * HN + a] : Integer.MIN_VALUE;
	}

	static void init() {
		pp = RenderPipeline.builder(RenderPipelines.POST_PROCESSING_SNIPPET)
				.withLocation(Identifier.fromNamespaceAndPath("azure", "pipeline/wet"))
				.withVertexShader(Identifier.withDefaultNamespace("core/screenquad"))
				.withFragmentShader(Identifier.fromNamespaceAndPath("azure", "post/wet"))
				.withSampler("SceneSampler")
				.withSampler("DepthSampler")
				.withSampler("HeightSampler")
				.withUniform("RainInfo", UniformType.UNIFORM_BUFFER)
				.withBlend(BlendFunction.TRANSLUCENT)
				.build();
		ub = new MappableRingBuffer(() -> "azure rain ubo", 130, SZ);
		try {
			RenderSystem.getDevice().precompilePipeline(pp);
		} catch (Exception e) {
		}
	}

	public static void post(Matrix4f rot, Matrix4f proj) {
		float rw = Mod.live("rain") && gi("wg", 1) == 1 && hok ? wet : 0f;
		float gs = Amb.gloss();
		if (rw < 0.01f && gs < 0.01f) return;
		Minecraft mc = Minecraft.getInstance();
		if (hm == null) {
			hm = new DynamicTexture(() -> "azure rain height", new NativeImage(HN, HN, false));
			mc.getTextureManager().register(Identifier.fromNamespaceAndPath("azure", "rain_h"), hm);
		}
		if (pp == null) init();
		RenderTarget m = mc.getMainRenderTarget();
		if (sc == null || sc.width != m.width || sc.height != m.height) {
			if (sc != null) sc.destroyBuffers();
			sc = new TextureTarget("azure rain scene", m.width, m.height, false);
		}
		CommandEncoder ce = RenderSystem.getDevice().createCommandEncoder();
		ce.copyTextureToTexture(m.getColorTexture(), sc.getColorTexture(), 0, 0, 0, 0, 0, m.width, m.height);
		Vec3 cp = mc.gameRenderer.getMainCamera().position();
		double wx = cp.x - Math.floor(cp.x / 2048) * 2048, wz = cp.z - Math.floor(cp.z / 2048) * 2048;
		float tm = (System.currentTimeMillis() % 3600000L) / 1000f;
		try (GpuBuffer.MappedView mv = ce.mapBuffer(ub.currentBuffer(), false, true)) {
			Std140Builder.intoBuffer(mv.data())
					.putMat4f(proj.invert(new Matrix4f()))
					.putMat4f(rot.invert(new Matrix4f()))
					.putMat4f(rot)
					.putMat4f(proj)
					.putVec4((float) (cp.x - ox), (float) (cp.y - oy), (float) (cp.z - oz), tm)
					.putVec4((float) wx, (float) cp.y, (float) wz, HN)
					.putVec4(rw, gi("pd", 60) / 100f, gi("rf", 90) / 100f, gi("rp", 1) == 1 ? rw : 0f)
					.putVec4(dev.ogfg.Fg.fr, dev.ogfg.Fg.fg, dev.ogfg.Fg.fb, gs);
		}
		try (RenderPass rp = ce.createRenderPass(() -> "azure wet", m.getColorTextureView(), OptionalInt.empty())) {
			rp.setPipeline(pp);
			RenderSystem.bindDefaultUniforms(rp);
			rp.setUniform("RainInfo", ub.currentBuffer());
			rp.bindTexture("SceneSampler", sc.getColorTextureView(), RenderSystem.getSamplerCache().getClampToEdge(FilterMode.LINEAR));
			rp.bindTexture("DepthSampler", m.getDepthTextureView(), RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST));
			rp.bindTexture("HeightSampler", hm.getTextureView(), RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST));
			rp.draw(0, 3);
		}
		ub.rotate();
	}

	static void tex() {
		if (stx != null) return;
		var tm = Minecraft.getInstance().getTextureManager();
		NativeImage s = new NativeImage(8, 64, false);
		for (int y = 0; y < 64; y++) for (int x = 0; x < 8; x++) {
			double u = (x - 3.5) / 3.5;
			double v = y / 63.0;
			double a = Math.exp(-u * u * 3) * Math.pow(v, 1.4) * Math.min(1, (1 - v) * 10);
			s.setPixel(x, y, (int) Math.round(255 * a) << 24 | 0xFFFFFF);
		}
		stx = Identifier.fromNamespaceAndPath("azure", "rain_streak");
		tm.register(stx, new DynamicTexture(() -> "azure rain streak", s));
		NativeImage r = new NativeImage(32, 32, false);
		for (int y = 0; y < 32; y++) for (int x = 0; x < 32; x++) {
			double ddx = (x - 15.5) / 16, ddy = (y - 15.5) / 16;
			double d = Math.sqrt(ddx * ddx + ddy * ddy);
			double a = Math.exp(-Math.pow((d - 0.78) * 9, 2)) + 0.5 * Math.exp(-d * d * 30);
			r.setPixel(x, y, (int) Math.round(255 * Math.min(1, a)) << 24 | 0xFFFFFF);
		}
		rtx = Identifier.fromNamespaceAndPath("azure", "rain_ring");
		tm.register(rtx, new DynamicTexture(() -> "azure rain ring", r));
	}

	static void vx(VertexConsumer v, double x, double y, double z, int c, float u, float w) {
		v.addVertex((float) x, (float) y, (float) z).setColor(c).setUv(u, w).setOverlay(OverlayTexture.NO_OVERLAY).setLight(0xF000F0).setNormal(0, 1, 0);
	}

	static void spawn(int i, Vec3 cam, boolean top) {
		double r = 22 * Math.sqrt(RN.nextDouble());
		double a = RN.nextDouble() * Math.PI * 2;
		dx[i] = cam.x + Math.cos(a) * r;
		dz[i] = cam.z + Math.sin(a) * r;
		dy[i] = cam.y + (top ? 12 + RN.nextDouble() * 8 : -6 + RN.nextDouble() * 26);
		dv[i] = 15 + RN.nextFloat() * 6;
		dl[i] = 0.7f + RN.nextFloat() * 0.6f;
	}

	public static void draw(Matrix4f view, Vec3 cam) {
		Minecraft m = Minecraft.getInstance();
		ClientLevel l = m.level;
		long r = System.currentTimeMillis();
		float dt = lt == 0 ? 0.016f : Math.min(0.1f, (r - lt) / 1000f);
		lt = r;
		if (l == null || !sky(l) || !hok) return;
		float rl = l.getRainLevel(1f);
		if (rl <= 0.01f && spl.isEmpty()) {
			dn = 0;
			return;
		}
		int want = Math.min(MX, (int) (rl * gi("dn", 100) / 100f * 1600));
		while (dn < want) spawn(dn++, cam, false);
		if (dn > want) dn = want;
		float wn = gi("wn", 25) / 100f * 5f;
		double wa = r / 9000.0;
		float wvx = (float) Math.cos(wa) * wn, wvz = (float) Math.sin(wa * 0.7 + 1) * wn;
		boolean sp = gi("sp", 1) == 1;
		for (int i = 0; i < dn; i++) {
			dy[i] -= dv[i] * dt;
			dx[i] += wvx * dt;
			dz[i] += wvz * dt;
			double hx = dx[i] - cam.x, hz = dz[i] - cam.z;
			int h = col((int) Math.floor(dx[i]), (int) Math.floor(dz[i]));
			if (hx * hx + hz * hz > 26 * 26 || h == Integer.MIN_VALUE || dy[i] < cam.y - 24) {
				spawn(i, cam, true);
				continue;
			}
			if (dy[i] < h) {
				BlockPos bp = BlockPos.containing(dx[i], h - 1, dz[i]);
				var sh = l.getBlockState(bp).getShape(l, bp);
				double top = sh.isEmpty() ? h - 0.12 : bp.getY() + sh.max(net.minecraft.core.Direction.Axis.Y);
				if (dy[i] >= top) continue;
				if (sp && spl.size() < 600 && hx * hx + hz * hz < 16 * 16 && dy[i] > top - 0.6) spl.add(new double[]{dx[i], top + 0.02, dz[i], r, 0.6 + RN.nextDouble() * 0.6});
				spawn(i, cam, true);
				if (h > dy[i] + 8) dy[i] = -1e9;
			}
		}
		spl.removeIf(s -> r - s[3] > 420);
		tex();
		var mv = RenderSystem.getModelViewStack();
		mv.pushMatrix();
		mv.mul(view);
		int fc = Vis.mix(0xDCE8FF, (Math.round(dev.ogfg.Fg.fr * 255) << 16) | (Math.round(dev.ogfg.Fg.fg * 255) << 8) | Math.round(dev.ogfg.Fg.fb * 255), 0.45f);
		RenderType st = RenderTypes.entityTranslucentEmissive(stx);
		VertexConsumer v = BS.getBuffer(st);
		float ln = (float) Math.sqrt(wvx * wvx + wvz * wvz + 18 * 18);
		float ux = wvx / ln, uy = -18 / ln, uz = wvz / ln;
		int ca = Math.round(Math.min(1f, rl) * 42) << 24 | fc;
		for (int i = 0; i < dn; i++) {
			if (dy[i] < -1e8) continue;
			double px = dx[i] - cam.x, py = dy[i] - cam.y, pz = dz[i] - cam.z;
			double dd = px * px + py * py + pz * pz;
			if (dd < 0.36) continue;
			double len = dl[i] * (dv[i] / 18.0);
			double tx = px - ux * len, ty = py - uy * len, tz = pz - uz * len;
			double sx = uy * pz - uz * py, sy = uz * px - ux * pz, sz = ux * py - uy * px;
			double sl = Math.sqrt(sx * sx + sy * sy + sz * sz);
			if (sl < 1e-4) continue;
			double w = 0.008 + Math.sqrt(dd) * 0.0009;
			sx *= w / sl;
			sy *= w / sl;
			sz *= w / sl;
			int c = dd > 400 ? Math.round(((ca >>> 24) * (float) Math.max(0, 1 - (Math.sqrt(dd) - 20) / 8))) << 24 | fc : ca;
			vx(v, px - sx, py - sy, pz - sz, c, 0, 1);
			vx(v, px + sx, py + sy, pz + sz, c, 1, 1);
			vx(v, tx + sx, ty + sy, tz + sz, c, 1, 0);
			vx(v, tx - sx, ty - sy, tz - sz, c, 0, 0);
			vx(v, tx - sx, ty - sy, tz - sz, c, 0, 0);
			vx(v, tx + sx, ty + sy, tz + sz, c, 1, 0);
			vx(v, px + sx, py + sy, pz + sz, c, 1, 1);
			vx(v, px - sx, py - sy, pz - sz, c, 0, 1);
		}
		var fr = ((dev.wscl.mixin.GaAcc) m.gameRenderer).azfg();
		var old = RenderSystem.getShaderFog();
		RenderSystem.setShaderFog(fr.getBuffer(FogRenderer.FogMode.NONE));
		BS.endBatch(st);
		if (!spl.isEmpty()) {
			RenderType rt = RenderTypes.entityTranslucentEmissive(rtx);
			VertexConsumer w = BS.getBuffer(rt);
			for (double[] s : spl) {
				float k = (r - (long) s[3]) / 420f;
				double rad = (0.04 + 0.2 * Math.sqrt(k)) * s[4];
				int c = Math.round((1 - k) * (1 - k) * 70) << 24 | fc;
				double px = s[0] - cam.x, py = s[1] - cam.y, pz = s[2] - cam.z;
				vx(w, px - rad, py, pz - rad, c, 0, 0);
				vx(w, px - rad, py, pz + rad, c, 0, 1);
				vx(w, px + rad, py, pz + rad, c, 1, 1);
				vx(w, px + rad, py, pz - rad, c, 1, 0);
				vx(w, px + rad, py, pz - rad, c, 1, 0);
				vx(w, px + rad, py, pz + rad, c, 1, 1);
				vx(w, px - rad, py, pz + rad, c, 0, 1);
				vx(w, px - rad, py, pz - rad, c, 0, 0);
			}
			BS.endBatch(rt);
		}
		BS.endBatch();
		RenderSystem.setShaderFog(old);
		mv.popMatrix();
	}
}
