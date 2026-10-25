package dev.wscl.mixin;

import dev.wscl.Tag;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PlayerTabOverlay.class, priority = 3000)
public abstract class NlMxn {
	@Inject(method = "getNameForDisplay", at = @At("RETURN"), cancellable = true)
	private void wsn(PlayerInfo i, CallbackInfoReturnable<Component> ci) {
		ci.setReturnValue(Tag.add(i.getProfile().name(), ci.getReturnValue()));
	}
}
