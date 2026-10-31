package dev.wscl.mixin;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import dev.wscl.Cfg;
import java.util.Set;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiGraphics.class)
public abstract class BsMxn {
	@Unique private static final Set<String> HD = Set.of("button", "button_disabled", "button_highlighted", "checkbox", "checkbox_highlighted", "checkbox_selected", "checkbox_selected_highlighted", "scroller_background", "slider", "slider_highlighted", "slider_handle", "slider_handle_highlighted", "tab_selected", "tab_selected_highlighted", "text_field", "text_field_highlighted");

	@Unique
	private static boolean hd(Identifier id) {
		return Cfg.c.btn && id.getNamespace().equals("minecraft") && id.getPath().startsWith("widget/") && HD.contains(id.getPath().substring(7));
	}

	@Inject(method = "blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V", at = @At("HEAD"), cancellable = true)
	private void b0(RenderPipeline p, Identifier id, int x, int y, int w, int h, CallbackInfo ci) {
		if (hd(id)) ci.cancel();
	}

	@Inject(method = "blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIIIF)V", at = @At("HEAD"), cancellable = true)
	private void b1(RenderPipeline p, Identifier id, int x, int y, int w, int h, float a, CallbackInfo ci) {
		if (hd(id)) ci.cancel();
	}

	@Inject(method = "blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIIII)V", at = @At("HEAD"), cancellable = true)
	private void b2(RenderPipeline p, Identifier id, int x, int y, int w, int h, int c, CallbackInfo ci) {
		if (hd(id)) ci.cancel();
	}

	@Inject(method = "blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIIIIIII)V", at = @At("HEAD"), cancellable = true)
	private void b3(RenderPipeline p, Identifier id, int a, int b, int u, int v, int x, int y, int w, int h, CallbackInfo ci) {
		if (hd(id)) ci.cancel();
	}

	@Inject(method = "blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIIIIIIII)V", at = @At("HEAD"), cancellable = true)
	private void b4(RenderPipeline p, Identifier id, int a, int b, int u, int v, int x, int y, int w, int h, int c, CallbackInfo ci) {
		if (hd(id)) ci.cancel();
	}
}
