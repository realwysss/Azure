package dev.wscl.mixin;

import dev.wscl.Fl;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EtMxn {
	@Inject(method = "turn", at = @At("HEAD"), cancellable = true)
	private void wst(double yr, double xr, CallbackInfo ci) {
		if (!Fl.on || (Object) this != Minecraft.getInstance().player) return;
		Fl.yaw += (float) yr * 0.15f;
		Fl.pitch = Math.max(-90f, Math.min(90f, Fl.pitch + (float) xr * 0.15f));
		ci.cancel();
	}
}
