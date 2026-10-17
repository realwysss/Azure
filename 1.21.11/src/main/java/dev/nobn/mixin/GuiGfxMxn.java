package dev.nobn.mixin;

import dev.nobn.Cfg;
import dev.nobn.NoBn;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiGraphics.class)
public abstract class GuiGfxMxn {
	@Inject(method = "renderItemCount", at = @At("HEAD"), cancellable = true)
	private void cnt(Font f, ItemStack st, int x, int y, String s, CallbackInfo ci) {
		if (Cfg.c.fd && s == null && NoBn.fade((GuiGraphics) (Object) this, f, st, x, y)) ci.cancel();
	}
}
