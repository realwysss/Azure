package dev.oldp.mixin;

import dev.oldp.Cfg;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.PotionContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public abstract class ItmMxn {
	@Inject(method = "isFoil", at = @At("RETURN"), cancellable = true)
	private void foil(ItemStack st, CallbackInfoReturnable<Boolean> ci) {
		if (ci.getReturnValueZ() || !Cfg.c.gl) return;
		Item i = st.getItem();
		boolean pot = i instanceof PotionItem;
		if (!pot && !(Cfg.c.arr && i == Items.TIPPED_ARROW)) return;
		PotionContents pc = st.get(DataComponents.POTION_CONTENTS);
		if (pc == null) return;
		if (pc.hasEffects() || (Cfg.c.wat && pot)) ci.setReturnValue(true);
	}
}
