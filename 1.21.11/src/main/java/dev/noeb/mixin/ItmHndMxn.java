package dev.noeb.mixin;

import dev.noeb.Cfg;
import net.minecraft.client.renderer.ItemInHandRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ItemInHandRenderer.class)
public abstract class ItmHndMxn {
	@ModifyConstant(method = "applyEatTransform", constant = @Constant(floatValue = 0.1F))
	private float bnc(float v) {
		return v * Cfg.fx();
	}
}