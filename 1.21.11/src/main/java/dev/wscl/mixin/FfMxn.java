package dev.wscl.mixin;

import dev.wscl.Ffp;
import net.minecraft.client.Minecraft;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class FfMxn {
	@Shadow private static int fps;

	@Inject(method = "runTick", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;fps:I", opcode = Opcodes.PUTSTATIC, shift = At.Shift.AFTER))
	private void wsff(boolean b, CallbackInfo ci) {
		fps = Ffp.fake(fps);
	}
}
