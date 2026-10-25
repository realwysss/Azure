package dev.wscl.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.wscl.Mod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.EquipmentAsset;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EquipmentLayerRenderer.class)
public abstract class ArMxn {
	private static final String M = "renderLayers(Lnet/minecraft/client/resources/model/EquipmentClientInfo$LayerType;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/world/item/ItemStack;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/resources/Identifier;II)V";
	private static int cur = 100;

	@Inject(method = M, at = @At("HEAD"))
	private <S> void wsh(EquipmentClientInfo.LayerType t, ResourceKey<EquipmentAsset> k, Model<? super S> m, S s, ItemStack i, PoseStack p, SubmitNodeCollector c, int l, Identifier id, int o, int ov, CallbackInfo ci) {
		cur = 100;
		if (!Mod.live("armorop")) return;
		Mod md = Mod.get("armorop");
		boolean self = s instanceof dev.htcl.HcSt h && h.hcSelf();
		if (self) cur = Minecraft.getInstance().screen != null ? md.i("io", 100) : md.i("so", 50);
		else cur = md.i("oo", 100);
	}

	@Redirect(method = M, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/rendertype/RenderTypes;armorCutoutNoCull(Lnet/minecraft/resources/Identifier;)Lnet/minecraft/client/renderer/rendertype/RenderType;"))
	private RenderType wsr(Identifier id) {
		return cur < 100 ? RenderTypes.armorTranslucent(id) : RenderTypes.armorCutoutNoCull(id);
	}

	@ModifyArg(method = M, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/OrderedSubmitNodeCollector;submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/rendertype/RenderType;IIILnet/minecraft/client/renderer/texture/TextureAtlasSprite;ILnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V", ordinal = 0), index = 6)
	private int wst(int c) {
		if (cur >= 100) return c;
		return (Math.max(0, Math.min(255, Math.round(cur * 2.55f))) << 24) | (c & 0xFFFFFF);
	}
}
