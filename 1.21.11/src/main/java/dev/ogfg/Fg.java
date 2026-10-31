package dev.ogfg;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.framegraph.FramePass;
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
import java.util.OptionalInt;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelTargetBundle;
import net.minecraft.client.renderer.MappableRingBuffer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FogType;
import org.joml.Matrix4f;

public class Fg {
	static final int SZ = new Std140SizeCalculator().putMat4f().putMat4f().putMat4f().putMat4f().putVec4().putVec4().putVec4().putVec4().putVec4().get();
	static final float BIG = 1000000f;
	public static boolean ok;
	public static boolean live;
	public static int caps;
	static boolean got;
	static boolean has;
	public static int skips;
	static float hh;
	public static float fr = 0.6f, fg = 0.7f, fb = 0.9f;
	static RenderPipeline pp;
	static TextureTarget sky;
	static MappableRingBuffer ub;

	public static void st(Camera cam, int rd, ClientLevel lvl) {
		hh = rd * 16f;
		ok = Cfg.c.on && cam.getFluidInCamera() == FogType.NONE && (Cfg.c.dims || lvl.dimension() == Level.OVERWORLD || lvl.dimensionType().hasSkyLight() && !lvl.dimensionType().hasCeiling());
		if (ok && cam.entity() instanceof LivingEntity e && (e.hasEffect(MobEffects.BLINDNESS) || e.hasEffect(MobEffects.DARKNESS))) ok = false;
		live = ok;
		got = false;
	}

	static void init() {
		pp = RenderPipeline.builder(RenderPipelines.POST_PROCESSING_SNIPPET)
				.withLocation(Identifier.fromNamespaceAndPath("ogfog", "pipeline/fog"))
				.withVertexShader(Identifier.withDefaultNamespace("core/screenquad"))
				.withFragmentShader(Identifier.fromNamespaceAndPath("ogfog", "post/fog"))
				.withSampler("SkySampler")
				.withSampler("DepthSampler")
				.withUniform("FogInfo", UniformType.UNIFORM_BUFFER)
				.withBlend(BlendFunction.TRANSLUCENT)
				.build();
		ub = new MappableRingBuffer(() -> "og fog ubo", 130, SZ);
		try {
			RenderSystem.getDevice().precompilePipeline(pp);
		} catch (Exception e) {
		}
	}

	public static void pass(FrameGraphBuilder b, LevelTargetBundle t) {
		if (!ok) return;
		FramePass p = b.addPass("og fog sky");
		t.main = p.readsAndWrites(t.main);
		p.executes(Fg::cap);
	}

	public static void cap() {
		if (!ok) return;
		if (pp == null) init();
		RenderTarget m = Minecraft.getInstance().getMainRenderTarget();
		if (sky == null || sky.width != m.width || sky.height != m.height) {
			if (sky != null) sky.destroyBuffers();
			sky = new TextureTarget("og fog sky", m.width, m.height, false);
		}
		RenderSystem.getDevice().createCommandEncoder().copyTextureToTexture(m.getColorTexture(), sky.getColorTexture(), 0, 0, 0, 0, 0, m.width, m.height);
		got = true;
		has = true;
		caps++;
	}

	public static void run(Matrix4f rot, Matrix4f proj) {
		if (!ok) return;
		if (!got) skips++;
		got = false;
		if (pp == null) init();
		RenderTarget m = Minecraft.getInstance().getMainRenderTarget();
		if (Cfg.c.mode == 0 && (!has || sky == null || sky.width != m.width || sky.height != m.height)) return;
		float en = Math.min(Cfg.c.en, hh * 0.97f);
		float st = Math.min(Cfg.c.st, en - 1f);
		int k = Cfg.c.col;
		Minecraft mc = Minecraft.getInstance();
		net.minecraft.world.phys.Vec3 cp = mc.gameRenderer.getMainCamera().position();
		double wx = cp.x - Math.floor(cp.x / 2048) * 2048, wz = cp.z - Math.floor(cp.z / 2048) * 2048;
		float tm = (System.currentTimeMillis() % 3600000L) / 1000f;
		double dd = (mc.level == null ? 6000 : mc.level.getDayTime() % 24000) / 24000.0 - 0.25;
		dd -= Math.floor(dd);
		double tod = (dd * 2 + (0.5 - Math.cos(dd * Math.PI) / 2)) / 3;
		float sa = (float) (tod * Math.PI * 2);
		boolean ow = mc.level != null && mc.level.dimension() == Level.OVERWORLD;
		CommandEncoder ce = RenderSystem.getDevice().createCommandEncoder();
		try (GpuBuffer.MappedView mv = ce.mapBuffer(ub.currentBuffer(), false, true)) {
			Std140Builder.intoBuffer(mv.data())
					.putMat4f(proj.invert(new Matrix4f()))
					.putMat4f(rot.invert(new Matrix4f()))
					.putMat4f(rot)
					.putMat4f(proj)
					.putVec4(st, en, Cfg.c.str / 100f, Cfg.c.soft ? 1f : 0f)
					.putVec4(Cfg.c.mode, Cfg.c.mode == 0 ? fr : (k >> 16 & 255) / 255f, Cfg.c.mode == 0 ? fg : (k >> 8 & 255) / 255f, Cfg.c.mode == 0 ? fb : (k & 255) / 255f)
					.putVec4((float) wx, (float) cp.y, (float) wz, tm)
					.putVec4((float) -Math.sin(sa), (float) Math.cos(sa), 0.15f, ow ? Cfg.c.sun / 100f : 0f)
					.putVec4(Cfg.c.mist / 100f, Cfg.c.hgt / 100f, (float) cp.y - 3f, 0f);
		}
		try (RenderPass rp = ce.createRenderPass(() -> "og fog", m.getColorTextureView(), OptionalInt.empty())) {
			rp.setPipeline(pp);
			RenderSystem.bindDefaultUniforms(rp);
			rp.setUniform("FogInfo", ub.currentBuffer());
			rp.bindTexture("SkySampler", sky.getColorTextureView(), RenderSystem.getSamplerCache().getClampToEdge(FilterMode.LINEAR));
			rp.bindTexture("DepthSampler", m.getDepthTextureView(), RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST));
			rp.draw(0, 3);
		}
		ub.rotate();
	}

	public static float off(float x, int i) {
		if (!live || i > 3) return x;
		return i % 2 == 0 ? BIG : BIG + 1f;
	}
}
