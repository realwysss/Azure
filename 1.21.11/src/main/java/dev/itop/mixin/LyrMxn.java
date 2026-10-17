package dev.itop.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.itop.Op;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStackRenderState.LayerRenderState.class)
public abstract class LyrMxn {
	@Shadow
	@Final
	@Mutable
	private List<BakedQuad> quads;
	@Shadow
	private int[] tintLayers;
	@Shadow
	private RenderType renderType;
	@Unique
	private List<BakedQuad> oq;
	@Unique
	private int[] ot;
	@Unique
	private RenderType or;
	@Unique
	private boolean md;

	@Inject(method = "submit", at = @At("HEAD"))
	private void a(PoseStack ps, SubmitNodeCollector c, int l, int o, int ol, CallbackInfo ci) {
		md = false;
		int p = Op.cur;
		if (p >= 100 || renderType == null) return;
		md = true;
		oq = quads;
		ot = tintLayers;
		or = renderType;
		int[] tl = ot == null ? new int[0] : ot;
		int a = p * 255 / 100 << 24;
		int[] nt = new int[tl.length + 1];
		for (int i = 0; i < tl.length; i++) nt[i] = a | (tl[i] & 0xffffff);
		nt[tl.length] = a | 0xffffff;
		List<BakedQuad> nq = new ArrayList<>(oq.size());
		for (BakedQuad q : oq) {
			nq.add(q.isTinted() ? q : new BakedQuad(q.position0(), q.position1(), q.position2(), q.position3(), q.packedUV0(), q.packedUV1(), q.packedUV2(), q.packedUV3(), tl.length, q.direction(), q.sprite(), q.shade(), q.lightEmission()));
		}
		boolean blk = or == Sheets.solidBlockSheet() || or == Sheets.cutoutBlockSheet() || or == Sheets.translucentBlockItemSheet();
		quads = nq;
		tintLayers = nt;
		renderType = blk ? Sheets.translucentBlockItemSheet() : Sheets.translucentItemSheet();
	}

	@Inject(method = "submit", at = @At("RETURN"))
	private void b(PoseStack ps, SubmitNodeCollector c, int l, int o, int ol, CallbackInfo ci) {
		if (!md) return;
		md = false;
		quads = oq;
		tintLayers = ot;
		renderType = or;
	}
}
