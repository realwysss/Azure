package dev.wscl;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.framegraph.FramePass;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.OptionalInt;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelTargetBundle;
import net.minecraft.client.renderer.MappableRingBuffer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.material.FogType;
import org.joml.Matrix4f;

public class Sky {
	static final int SZ = new Std140SizeCalculator().putMat4f().putMat4f().putVec4().putVec4().putVec4().putVec4().putVec4().get();
	public static String sig = "";
	public static String last = "";
	static final Matrix4f RT = new Matrix4f(), PJ = new Matrix4f();
	static RenderPipeline pp;
	static MappableRingBuffer ub;
	static float nf = -1;
	public static int runs;

	static int gi(String k, int d) {
		return Cfg.gi("sky." + k, d);
	}

	public static void tick() {
	}

	public static void mat(Matrix4f rot, Matrix4f proj) {
		RT.set(rot);
		PJ.set(proj);
	}

	static boolean on() {
		Minecraft m = Minecraft.getInstance();
		ClientLevel l = m.level;
		return Mod.live("sky") && l != null && l.dimensionType().hasSkyLight() && !l.dimensionType().hasCeiling() && !Sty.skyOn() && m.gameRenderer.getMainCamera().getFluidInCamera() == FogType.NONE;
	}

	public static void pass(FrameGraphBuilder b, LevelTargetBundle t) {
		if (!on()) return;
		FramePass p = b.addPass("azure sky");
		t.main = p.readsAndWrites(t.main);
		p.executes(Sky::run);
	}

	static void init() {
		pp = RenderPipeline.builder(RenderPipelines.POST_PROCESSING_SNIPPET)
				.withLocation(Identifier.fromNamespaceAndPath("azure", "pipeline/sky"))
				.withVertexShader(Identifier.withDefaultNamespace("core/screenquad"))
				.withFragmentShader(Identifier.fromNamespaceAndPath("azure", "post/sky"))
				.withUniform("SkyInfo", UniformType.UNIFORM_BUFFER)
				.withBlend(BlendFunction.TRANSLUCENT)
				.build();
		ub = new MappableRingBuffer(() -> "azure sky ubo", 130, SZ);
		try {
			RenderSystem.getDevice().precompilePipeline(pp);
		} catch (Exception e) {
		}
	}

	static void run() {
		Minecraft mc = Minecraft.getInstance();
		if (mc.level == null) return;
		if (pp == null) init();
		int rgb = gi("col", 0) == 0 ? Hud.ac() & 0xFFFFFF : Rw.cv(gi("cc", 0x4AA8FF)) & 0xFFFFFF;
		double dd = mc.level.getDayTime() % 24000 / 24000.0 - 0.25;
		dd -= Math.floor(dd);
		double tod = (dd * 2 + (0.5 - Math.cos(dd * Math.PI) / 2)) / 3;
		float sa = (float) (tod * Math.PI * 2);
		float sx = (float) -Math.sin(sa), sy = (float) Math.cos(sa);
		float tg = 1f;
		if (gi("wn", 0) == 1) tg = Math.max(0f, Math.min(1f, (0.12f - sy) / 0.3f)) * (1f - mc.level.getRainLevel(1f) * 0.85f);
		nf = nf < 0 ? tg : nf + (tg - nf) * 0.05f;
		if (nf < 0.005f) return;
		float tm = (System.currentTimeMillis() % 3600000L) / 1000f;
		int sa2 = Math.max(1, Math.min(3, gi("sa", 3)));
		RenderTarget m = mc.getMainRenderTarget();
		CommandEncoder ce = RenderSystem.getDevice().createCommandEncoder();
		try (GpuBuffer.MappedView mv = ce.mapBuffer(ub.currentBuffer(), false, true)) {
			Std140Builder.intoBuffer(mv.data())
					.putMat4f(PJ.invert(new Matrix4f()))
					.putMat4f(RT.invert(new Matrix4f()))
					.putVec4((rgb >> 16 & 255) / 255f, (rgb >> 8 & 255) / 255f, (rgb & 255) / 255f, gi("bg", 22) / 100f)
					.putVec4(gi("au", 1) == 1 ? gi("as", 70) / 100f : 0f, gi("am", 1), gi("st", 1) == 1 ? sa2 : 0, gi("mw", 1))
					.putVec4(gi("fs", 1), Math.max(2, gi("ff", 8)), gi("mn", 1), nf)
					.putVec4(sx, sy, 0f, tm)
					.putVec4(gi("wn", 0) == 1 ? sa : 0f, 0f, 0f, 0f);
		}
		try (RenderPass rp = ce.createRenderPass(() -> "azure sky", m.getColorTextureView(), OptionalInt.empty())) {
			rp.setPipeline(pp);
			RenderSystem.bindDefaultUniforms(rp);
			rp.setUniform("SkyInfo", ub.currentBuffer());
			rp.draw(0, 3);
		}
		ub.rotate();
		runs++;
	}
}
