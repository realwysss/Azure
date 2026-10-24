package dev.wscl.mixin;

import dev.wscl.Tf;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(targets = "net.minecraft.client.gui.font.FontManager$CachedFontProvider")
public abstract class FtMxn {
	private static final FontDescription AZ = new FontDescription.Resource(Identifier.fromNamespaceAndPath("azure", "azure"));

	@ModifyVariable(method = "getGlyphSource", at = @At("HEAD"), argsOnly = true)
	private FontDescription wsf(FontDescription d) {
		return d == FontDescription.DEFAULT && Tf.on() ? AZ : d;
	}
}
