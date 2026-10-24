package dev.wscl.mixin;

import dev.wscl.Em;
import net.minecraft.client.multiplayer.ClientPacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ClientPacketListener.class)
public abstract class EoMxn {
	@ModifyVariable(method = "sendChat", at = @At("HEAD"), argsOnly = true)
	private String wso(String s) {
		return Em.out(s);
	}
}
