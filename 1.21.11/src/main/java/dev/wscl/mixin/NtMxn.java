package dev.wscl.mixin;

import dev.wscl.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class NtMxn {
	@Inject(method = "getDisplayName", at = @At("RETURN"), cancellable = true)
	private void wsn(CallbackInfoReturnable<Component> ci) {
		Player p = (Player) (Object) this;
		if (!p.level().isClientSide()) return;

	}
}
