package dev.wscl.mixin;

import dev.wscl.Cfg;
import dev.wscl.Chip;
import dev.wscl.Ias;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(JoinMultiplayerScreen.class)
public abstract class MpMxn extends Screen {
	protected MpMxn(Component t) {
		super(t);
	}

	@Inject(method = "init", at = @At("HEAD"))
	private void wsh(CallbackInfo ci) {
		if (Cfg.c.btn) Ias.hide();
	}

	@Inject(method = "init", at = @At("RETURN"))
	private void wsi(CallbackInfo ci) {
		if (Cfg.c.btn) addRenderableWidget(new Chip(10, 8));
	}
}
