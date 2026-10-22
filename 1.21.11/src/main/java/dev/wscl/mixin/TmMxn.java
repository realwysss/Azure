package dev.wscl.mixin;

import dev.wscl.Cfg;
import dev.wscl.Mod;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Level.class)
public abstract class TmMxn {
	@Inject(method = "getDayTime", at = @At("RETURN"), cancellable = true)
	private void wst(CallbackInfoReturnable<Long> ci) {
		if ((Object) this instanceof ClientLevel && Mod.live("time")) ci.setReturnValue(dev.wscl.Tc.now);
	}

	@Inject(method = "getRainLevel", at = @At("RETURN"), cancellable = true)
	private void wsr(float p, CallbackInfoReturnable<Float> ci) {
		if ((Object) this instanceof ClientLevel && Mod.live("weather")) ci.setReturnValue(Cfg.gi("weather.w", 0) == 0 ? 0f : 1f);
	}

	@Inject(method = "getThunderLevel", at = @At("RETURN"), cancellable = true)
	private void wsh(float p, CallbackInfoReturnable<Float> ci) {
		if ((Object) this instanceof ClientLevel && Mod.live("weather")) ci.setReturnValue(Cfg.gi("weather.w", 0) == 2 ? 1f : 0f);
	}
}
