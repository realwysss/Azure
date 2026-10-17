package dev.ogfg.mixin;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import dev.ogfg.Fg;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LevelTargetBundle;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.state.LevelRenderState;
import net.minecraft.util.profiling.ProfilerFiller;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LevelRenderer.class, priority = 900)
public abstract class LvlRdrMxn {
	@Shadow
	private LevelTargetBundle targets;

	@Inject(method = "addMainPass", at = @At("HEAD"))
	private void a(FrameGraphBuilder b, Frustum fr, Matrix4f m, GpuBufferSlice s, boolean x, LevelRenderState st, DeltaTracker d, ProfilerFiller pf, CallbackInfo ci) {
		Fg.pass(b, targets);
	}

	@Inject(method = "renderLevel", at = @At("TAIL"))
	private void b(GraphicsResourceAllocator a, DeltaTracker d, boolean b, Camera cam, Matrix4f rot, Matrix4f proj, Matrix4f cull, GpuBufferSlice f, Vector4f fog, boolean sky, CallbackInfo ci) {
		Fg.run(rot, proj);
	}
}
