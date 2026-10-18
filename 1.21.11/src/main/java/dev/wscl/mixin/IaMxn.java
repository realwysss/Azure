package dev.wscl.mixin;

import dev.wscl.AzSkin;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "ru.vidtu.ias.screen.AccountScreen", remap = false)
public abstract class IaMxn extends Screen {
	protected IaMxn(Component t) {
		super(t);
	}

	@Inject(method = "method_25426", at = @At("RETURN"), remap = false, require = 0)
	private void wsi(CallbackInfo ci) {
		Screen self = this;
		addRenderableWidget(Button.builder(Component.literal("Skin Changer"), b -> minecraft.setScreen(new AzSkin(self))).bounds(5, height / 2 + 64, 85, 20).build());
	}
}
