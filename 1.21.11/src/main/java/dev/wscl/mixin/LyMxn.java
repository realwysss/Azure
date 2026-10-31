package dev.wscl.mixin;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import dev.wscl.Lyr;
import dev.wscl.Mod;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.LevelRenderer;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LevelRenderer.class, priority = 1100)
public abstract class LyMxn {
	@Inject(method = "renderLevel", at = @At("TAIL"))
	private void ly(GraphicsResourceAllocator a, DeltaTracker d, boolean b, Camera cam, Matrix4f rot, Matrix4f proj, Matrix4f cull, GpuBufferSlice f, Vector4f fog, boolean sky, CallbackInfo ci) {
		if (Mod.live("rain")) dev.wscl.Rn.draw(rot, cam.position());
		if (Mod.live("trails")) dev.wscl.Trl.draw(rot, cam.position(), d.getGameTimeDeltaPartialTick(false));
		if (Mod.live("place")) dev.wscl.Plc.draw(rot, cam.position());
		if (Mod.live("amb")) dev.wscl.Amb.draw(rot, cam.position());
		if (Mod.live("lyrics")) Lyr.draw(rot, cam.position());
	}
}
