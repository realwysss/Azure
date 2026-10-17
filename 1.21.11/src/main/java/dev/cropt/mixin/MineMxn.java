package dev.cropt.mixin;

import dev.cropt.Co;
import dev.cropt.CoAcc;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MineMxn implements CoAcc {
	@Shadow
	private int rightClickDelay;

	@Override
	public void cool() {
		rightClickDelay = 0;
	}

	@Inject(method = "startUseItem", at = @At("HEAD"), cancellable = true)
	private void use(CallbackInfo ci) {
		Minecraft m = (Minecraft) (Object) this;
		if (Co.active() && m.player != null && m.player.getMainHandItem().is(Items.END_CRYSTAL)) ci.cancel();
	}
}
