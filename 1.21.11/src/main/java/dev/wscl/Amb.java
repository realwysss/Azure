package dev.wscl;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MappableRingBuffer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Amb {
	static class Pt {
		double x, y, z;
		long born;
		int life;
		float sz, hu, ph;
		int kd;
	}

	static final List<Pt> ps = new ArrayList<>();
	static final Random RN = new Random();
	static final int SZ = new Std140SizeCalculator().putMat4f().putMat4f().putMat4f().putMat4f().putVec4().putVec4().putVec4().putVec4().get();
	static long lt;
	static float nt;
	static RenderPipeline pp;
	static TextureTarget sc;
	static MappableRingBuffer ub;

	static int gi(String k, int d) {
		return Cfg.gi("amb." + k, d);
	}

	static float night() {
		Minecraft m = Minecraft.getInstance();
		if (m.level == null || !m.level.dimensionType().hasSkyLight()) return 0f;
		long t = m.level.getDayTime() % 24000;
		if (t < 0) t += 24000;
		if (t > 13000 && t < 23000) return 1f;
		if (t >= 12000 && t <= 13000) return (t - 12000) / 1000f;
		if (t >= 23000) return 1 - (t - 23000) / 1000f;
		return 0f;
	}

	public static float gloss() {
		return Mod.live("amb") && gi("gs", 1) == 1 ? gi("gp", 35) / 100f * 0.5f : 0f;
	}

	public static void tick() {
		Minecraft m = Minecraft.getInstance();
		if (!Mod.live("amb") || m.level == null || m.player == null) {
			ps.clear();
			return;
		}
		nt = night();
		Vec3 c = m.player.getEyePosition();
		float am = gi("n", 100) / 100f;
		int[] wn = {gi("pt", 1) == 1 ? Math.round(34 * am) : 0, 0, gi("ff", 1) == 1 ? Math.round(26 * am * nt) : 0};
		int[] have = new int[3];
		long r = System.currentTimeMillis();
		ps.removeIf(p -> r - p.born > p.life || p.x - c.x > 18 || c.x - p.x > 18 || p.z - c.z > 18 || c.z - p.z > 18 || Math.abs(p.y - c.y) > 12);
		for (Pt p : ps) have[p.kd]++;
		for (int k = 0; k < 3; k += 2) for (int i = have[k]; i < wn[k] && i < have[k] + 3; i++) {
			Pt p = new Pt();
			double rr = 3 + RN.nextDouble() * 11;
			double a = RN.nextDouble() * Math.PI * 2;
			p.x = c.x + Math.cos(a) * rr;
			p.z = c.z + Math.sin(a) * rr;
			p.y = c.y + (k == 2 ? -1.5 + RN.nextDouble() * 1.8 : -1.2 + RN.nextDouble() * 4.5);
			BlockPos bp = BlockPos.containing(p.x, p.y, p.z);
			if (!m.level.getBlockState(bp).getCollisionShape(m.level, bp).isEmpty()) continue;
			p.born = r;
			p.life = 9000 + RN.nextInt(9000);
			p.sz = k == 2 ? 0.05f + RN.nextFloat() * 0.03f : 0.1f + RN.nextFloat() * 0.14f;
			p.hu = RN.nextFloat();
			p.ph = RN.nextFloat() * 100;
			p.kd = k;
			ps.add(p);
		}
	}

	public static void draw(Matrix4f view, Vec3 cam) {
		if (ps.isEmpty()) return;
		long r = System.currentTimeMillis();
		float dt = lt == 0 ? 0.016f : Math.min(0.1f, (r - lt) / 1000f);
		lt = r;
		double t = r / 1000.0;
		int cm = gi("cm", 0);
		float br = gi("br", 80) / 100f;
		float sm = gi("sz", 100) / 100f;
		Fx.begin(view);
		Vector3f[] cv = Fx.cam();
		VertexConsumer v = Fx.BS.getBuffer(RenderTypes.entityTranslucentEmissive(Fx.glw));
		for (Pt p : ps) {
			double fx = Math.sin(p.y * 0.7 + t * 0.31 + p.ph) + Math.sin(p.z * 0.45 + t * 0.17);
			double fz = Math.cos(p.x * 0.6 + t * 0.27 + p.ph) + Math.cos(p.y * 0.5 - t * 0.21);
			double fy = Math.sin(p.x * 0.3 + p.z * 0.3 + t * 0.4 + p.ph) * 0.6 + 0.1;
			float sp = p.kd == 2 ? 0.45f : 0.12f;
			p.x += fx * sp * dt;
			p.y += fy * sp * dt * 0.6f;
			p.z += fz * sp * dt;
			float k = (r - p.born) / (float) p.life;
			float fa = Math.min(1f, k * 5f) * Math.min(1f, (1 - k) * 4f);
			fa = fa * fa * (3 - 2 * fa);
			double x = p.x - cam.x, y = p.y - cam.y, z = p.z - cam.z;
			double d = Math.sqrt(x * x + y * y + z * z);
			fa *= (float) Math.max(0, Math.min(1, (d - 1.5) / 2.5)) * (float) Math.max(0, Math.min(1, (16 - d) / 4));
			if (fa <= 0.01f) continue;
			float s = p.sz * sm;
			if (p.kd == 2) {
				int c = Vis.mix(0xFFE27A, 0xC8FF7A, p.hu);
				float tw = Math.max(0f, (float) Math.sin(t * 1.3 + p.ph));
				tw = tw * tw;
				Fx.bb(v, cv[0], cv[1], x, y, z, s * 4f, Fx.ca(c, fa * tw * br * 0.3f));
				Fx.bb(v, cv[0], cv[1], x, y, z, s, Fx.ca(Vis.mix(0xFFFFFF, c, 0.5f), fa * tw * br));
			} else {
				int c = Vis.mix(0xFFFFFF, Fx.ac(cm, gi("c1", Rw.AZ), p.hu * 0.1f), 0.7f);
				float tw = 0.8f + 0.2f * (float) Math.sin(t * 0.8 + p.ph);
				Fx.bb(v, cv[0], cv[1], x, y, z, s * 2.2f, Fx.ca(c, fa * tw * br * 0.22f));
			}
		}
		Fx.end();
	}

	public static void post(Matrix4f rot, Matrix4f proj) {
		if (!Mod.live("amb")) return;
		Minecraft mc = Minecraft.getInstance();
		if (mc.level == null) return;
		boolean bl = gi("bl", 1) == 1, sr = gi("sr", 1) == 1, hz = gi("hz", 1) == 1, cg = gi("cg", 1) == 1, vg = gi("vg", 1) == 1;
		if (!bl && !sr && !hz && !cg && !vg) return;
		if (pp == null) {
			pp = RenderPipeline.builder(RenderPipelines.POST_PROCESSING_SNIPPET)
					.withLocation(Identifier.fromNamespaceAndPath("azure", "pipeline/amb"))
					.withVertexShader(Identifier.withDefaultNamespace("core/screenquad"))
					.withFragmentShader(Identifier.fromNamespaceAndPath("azure", "post/amb"))
					.withSampler("SceneSampler")
					.withSampler("DepthSampler")
					.withUniform("AmbInfo", UniformType.UNIFORM_BUFFER)
					.withBlend(BlendFunction.TRANSLUCENT)
					.build();
			ub = new MappableRingBuffer(() -> "azure amb ubo", 130, SZ);
		}
		RenderTarget m = mc.getMainRenderTarget();
		if (sc == null || sc.width != m.width || sc.height != m.height) {
			if (sc != null) sc.destroyBuffers();
			sc = new TextureTarget("azure amb scene", m.width, m.height, false);
		}
		CommandEncoder ce = RenderSystem.getDevice().createCommandEncoder();
		ce.copyTextureToTexture(m.getColorTexture(), sc.getColorTexture(), 0, 0, 0, 0, 0, m.width, m.height);
		int ac = Fx.ac(gi("cm", 0), gi("c1", Rw.AZ), 0f);
		double dd = mc.level.getDayTime() % 24000 / 24000.0 - 0.25;
		dd -= Math.floor(dd);
		double tod = (dd * 2 + (0.5 - Math.cos(dd * Math.PI) / 2)) / 3;
		float sa = (float) (tod * Math.PI * 2);
		float sy = (float) Math.cos(sa);
		boolean ow = mc.level.dimensionType().hasSkyLight() && !mc.level.dimensionType().hasCeiling();
		float day = ow ? Math.max(0f, Math.min(1f, (sy + 0.05f) / 0.3f)) * (1f - mc.level.getRainLevel(1f)) : 0f;
		try (GpuBuffer.MappedView mv = ce.mapBuffer(ub.currentBuffer(), false, true)) {
			Std140Builder.intoBuffer(mv.data())
					.putMat4f(proj.invert(new Matrix4f()))
					.putMat4f(rot.invert(new Matrix4f()))
					.putMat4f(rot)
					.putMat4f(proj)
					.putVec4((ac >> 16 & 255) / 255f, (ac >> 8 & 255) / 255f, (ac & 255) / 255f, 0f)
					.putVec4((float) -Math.sin(sa), sy, 0f, sr ? day : 0f)
					.putVec4(bl ? gi("bs", 60) / 100f : 0f, sr ? 1f : 0f, hz ? 1f : 0f, cg ? 1f : 0f)
					.putVec4(1.62f, ow ? 0.3f + 0.7f * day : 0.6f, vg ? Math.min(0.9f, gi("vs", 35) / 100f * (1f + 0.3f * nt)) : 0f, 0f);
		}
		try (RenderPass rp = ce.createRenderPass(() -> "azure amb", m.getColorTextureView(), OptionalInt.empty())) {
			rp.setPipeline(pp);
			RenderSystem.bindDefaultUniforms(rp);
			rp.setUniform("AmbInfo", ub.currentBuffer());
			rp.bindTexture("SceneSampler", sc.getColorTextureView(), RenderSystem.getSamplerCache().getClampToEdge(FilterMode.LINEAR));
			rp.bindTexture("DepthSampler", m.getDepthTextureView(), RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST));
			rp.draw(0, 3);
		}
		ub.rotate();
	}

	public static void hud(GuiGraphics g, int w, int h) {
	}
}
