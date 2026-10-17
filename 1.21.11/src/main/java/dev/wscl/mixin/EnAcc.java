package dev.wscl.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(targets = "net.minecraft.client.gui.components.AbstractSelectionList$Entry")
public interface EnAcc {
	@Invoker("getX")
	int ex();

	@Invoker("getY")
	int ey();

	@Invoker("getWidth")
	int ew();

	@Invoker("getHeight")
	int eh();
}
