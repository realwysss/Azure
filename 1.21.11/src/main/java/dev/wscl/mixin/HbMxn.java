package dev.wscl.mixin;

import dev.wscl.Mod;
import net.minecraft.client.gui.components.debug.DebugScreenEntries;
import net.minecraft.client.gui.components.debug.DebugScreenEntryList;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DebugScreenEntryList.class)
public abstract class HbMxn {
	@Inject(method = "isCurrentlyEnabled", at = @At("HEAD"), cancellable = true)
	private void wsh(Identifier id, CallbackInfoReturnable<Boolean> ci) {
		if (id.equals(DebugScreenEntries.ENTITY_HITBOXES) && Mod.live("hitbox")) ci.setReturnValue(true);
	}
}
