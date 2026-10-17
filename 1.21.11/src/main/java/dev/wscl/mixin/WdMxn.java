package dev.wscl.mixin;

import dev.wscl.Cfg;
import dev.wscl.Snd;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.sounds.SoundManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractWidget.class)
public abstract class WdMxn {
	@Inject(method = "playDownSound", at = @At("HEAD"), cancellable = true)
	private void wsc(SoundManager s, CallbackInfo ci) {
		if (Cfg.c.sfx > 0 && (net.minecraft.client.Minecraft.getInstance().screen instanceof dev.wscl.Mnu || net.minecraft.client.Minecraft.getInstance().screen instanceof dev.wscl.AzTitle)) {
			Snd.p("click");
			ci.cancel();
		}
	}
}
