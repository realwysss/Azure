package dev.wscl.mixin;

import dev.wscl.Mod;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryScreen.class)
public abstract class HsMxn {
	@Inject(method = "renderEntityInInventoryFollowsMouse", at = @At("HEAD"), cancellable = true)
	private static void wsh(GuiGraphics g, int a, int b, int c, int d, int e, float f, float h, float i, LivingEntity l, CallbackInfo ci) {
		if (Mod.live("hidesk")) ci.cancel();
	}
}
