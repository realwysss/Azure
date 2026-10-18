package dev.wscl.mixin;

import dev.wscl.Nam;
import java.util.List;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerList.class)
public abstract class SvMxn {
	@Shadow private List<ServerData> serverList;

	@Inject(method = "load", at = @At("RETURN"))
	private void wsl(CallbackInfo ci) {
		Nam.dedupe(serverList);
		Nam.fix(serverList);
	}

	@Inject(method = "save", at = @At("HEAD"))
	private void wss(CallbackInfo ci) {
		Nam.fix(serverList);
	}
}
