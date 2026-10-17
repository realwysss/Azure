package dev.itop.mixin;

import dev.itop.Op;
import dev.itop.OpAcc;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemModelResolver.class)
public abstract class ItmResMxn {
	@Inject(method = "updateForTopItem", at = @At("TAIL"))
	private void t(ItemStackRenderState s, ItemStack st, ItemDisplayContext c, @Nullable Level lv, @Nullable ItemOwner ow, int i, CallbackInfo ci) {
		int o = Op.ofItem(st);
		((OpAcc) s).itopSet(o);
		if (o < 100) {
			s.appendModelIdentityElement(o);
		}
	}
}
