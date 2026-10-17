package dev.frbs.mixin;

import dev.frbs.Cfg;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Minecraft.class)
public class McMxn {
	@Redirect(method = "runTick", at = @At(value = "INVOKE", target = "Ljava/lang/Thread;yield()V"))
	private void frbs() {
		if (!Cfg.c.noyld) Thread.yield();
	}
}
