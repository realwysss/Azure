package dev.tfgl.mixin;

import dev.tfgl.Tg;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RenderSetup.class)
public abstract class RndStpMxn {
	@Redirect(method = "getTextures", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/texture/TextureManager;getTexture(Lnet/minecraft/resources/Identifier;)Lnet/minecraft/client/renderer/texture/AbstractTexture;"))
	private AbstractTexture tex(TextureManager tm, Identifier id) {
		return Tg.tex(id, tm);
	}
}
