package dev.wscl.mixin;

import net.minecraft.client.gui.render.GuiRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GuiRenderer.class)
public interface GiAcc {
	@Invoker("invalidateItemAtlas")
	void azinv();
}
