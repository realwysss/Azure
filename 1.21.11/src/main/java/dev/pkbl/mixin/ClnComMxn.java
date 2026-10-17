package dev.pkbl.mixin;

import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundResourcePackPushPacket;
import net.minecraft.network.protocol.common.ServerboundResourcePackPacket;
import net.minecraft.network.protocol.common.ServerboundResourcePackPacket.Action;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientCommonPacketListenerImpl.class)
public abstract class ClnComMxn {
	@Shadow
	public abstract void send(Packet<?> p);

	@Inject(method = "handleResourcePackPush", at = @At("HEAD"), cancellable = true)
	private void blk(ClientboundResourcePackPushPacket p, CallbackInfo ci) {
		ci.cancel();
		send(new ServerboundResourcePackPacket(p.id(), Action.ACCEPTED));
		send(new ServerboundResourcePackPacket(p.id(), Action.DOWNLOADED));
		send(new ServerboundResourcePackPacket(p.id(), Action.SUCCESSFULLY_LOADED));
	}
}
