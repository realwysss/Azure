package dev.wscl.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.wscl.Lv;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public abstract class ItMxn {
	@Inject(method = "renderItem", at = @At("HEAD"), cancellable = true)
	private static void hd(ItemDisplayContext cx, PoseStack ps, MultiBufferSource bs, int light, int ov, int[] tn, List<BakedQuad> qs, RenderType rt, ItemStackRenderState.FoilType ft, CallbackInfo ci) {
		if (Lv.HD.isEmpty() || Lv.inhd) return;
		List<BakedQuad> rest = null;
		for (int i = 0; i < qs.size(); i++) {
			BakedQuad q = qs.get(i);
			Identifier h = Lv.HD.get(q.sprite().contents().name());
			if (h == null) continue;
			if (rest == null) rest = new ArrayList<>();
			int col = q.isTinted() && q.tintIndex() < tn.length ? tn[q.tintIndex()] : -1;
			Lv.quad(ps.last(), bs.getBuffer(RenderTypes.itemEntityTranslucentCull(h)), q, col, light, ov);
		}
		if (rest == null) return;
		for (BakedQuad q : qs) if (!Lv.HD.containsKey(q.sprite().contents().name())) rest.add(q);
		Lv.inhd = true;
		try {
			ItemRenderer.renderItem(cx, ps, bs, light, ov, tn, rest, rt, ft);
		} finally {
			Lv.inhd = false;
		}
		ci.cancel();
	}
}
