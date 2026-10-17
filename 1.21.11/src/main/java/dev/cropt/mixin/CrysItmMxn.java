package dev.cropt.mixin;

import dev.cropt.Co;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.EndCrystalItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EndCrystalItem.class)
public abstract class CrysItmMxn {
	@Inject(method = "useOn", at = @At("HEAD"))
	private void keep(UseOnContext c, CallbackInfoReturnable<?> ci) {
		Minecraft m = Minecraft.getInstance();
		if (!Co.active() || m.player == null || m.level == null || !c.getLevel().isClientSide() || m.player.hasInfiniteMaterials()) return;
		if (Co.can(m, c.getClickedPos())) c.getItemInHand().grow(1);
	}
}
