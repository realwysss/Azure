package dev.tfgl.mixin;

import dev.tfgl.Cfg;
import net.minecraft.client.renderer.GlobalSettingsUniform;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(GlobalSettingsUniform.class)
public abstract class GlbSetMxn {
	@ModifyVariable(method = "update", at = @At("HEAD"), argsOnly = true, ordinal = 0)
	private double str(double d) {
		return Cfg.c.on ? d * Cfg.c.str / 100.0 : d;
	}
}
