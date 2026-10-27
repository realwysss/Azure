package dev.wscl.mixin;

import dev.wscl.Fl;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(Camera.class)
public abstract class CmMxn {
	@ModifyArgs(method = "setup", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setRotation(FF)V"))
	private void wsr(Args a) {
		if (!Fl.on) return;
		boolean rev = Minecraft.getInstance().options.getCameraType() == net.minecraft.client.CameraType.THIRD_PERSON_FRONT;
		a.set(0, rev ? Fl.yaw + 180f : Fl.yaw);
		a.set(1, rev ? -Fl.pitch : Fl.pitch);
	}
}
