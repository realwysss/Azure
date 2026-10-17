package dev.wscl.mixin;

import dev.wscl.Snd;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class PkMxn {
	@Inject(method = "handleEntityEvent", at = @At("HEAD"))
	private void wsp(ClientboundEntityEventPacket p, CallbackInfo ci) {
		if (p.getEventId() == 35) {
			Snd.p("pop");
			var lv = net.minecraft.client.Minecraft.getInstance().level;
			if (lv != null) dev.wscl.Pops.add(p.getEntity(lv));
		}
	}
}
