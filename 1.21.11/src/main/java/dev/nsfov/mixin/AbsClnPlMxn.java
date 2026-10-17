package dev.nsfov.mixin;

import dev.nsfov.Nsf;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractClientPlayer.class)
public abstract class AbsClnPlMxn {
	@Redirect(method = "getFieldOfViewModifier", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/AbstractClientPlayer;getAttributeValue(Lnet/minecraft/core/Holder;)D"))
	private double spd(AbstractClientPlayer p, Holder<Attribute> a) {
		AttributeInstance in = p.getAttribute(a);
		if (in == null || !Nsf.has(in)) return p.getAttributeValue(a);
		return Nsf.val(in);
	}
}
