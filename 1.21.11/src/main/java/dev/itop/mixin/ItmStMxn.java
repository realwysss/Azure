package dev.itop.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.itop.Op;
import dev.itop.OpAcc;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStackRenderState.class)
public abstract class ItmStMxn implements OpAcc {
	@Unique
	private int itopOp = 100;

	@Override
	public int itopGet() {
		return itopOp;
	}

	@Override
	public void itopSet(int v) {
		itopOp = v;
	}

	@Inject(method = "submit", at = @At("HEAD"), cancellable = true)
	private void a(PoseStack ps, SubmitNodeCollector c, int l, int o, int ol, CallbackInfo ci) {
		if (itopOp <= 0) {
			ci.cancel();
			return;
		}
		Op.cur = itopOp;
	}

	@Inject(method = "submit", at = @At("RETURN"))
	private void b(PoseStack ps, SubmitNodeCollector c, int l, int o, int ol, CallbackInfo ci) {
		Op.cur = 100;
	}
}
