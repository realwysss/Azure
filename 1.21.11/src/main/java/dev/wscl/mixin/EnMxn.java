package dev.wscl.mixin;

import dev.wscl.Tag;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = EntityRenderer.class, priority = 3000)
public abstract class EnMxn {
	@Inject(method = "getNameTag", at = @At("RETURN"), cancellable = true)
	private void wsn(Entity e, CallbackInfoReturnable<Component> ci) {
		if (e instanceof Player p) ci.setReturnValue(Tag.add(p.getGameProfile().name(), ci.getReturnValue()));
	}
}
