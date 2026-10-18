package dev.wscl.mixin;

import dev.wscl.AzTitle;
import dev.wscl.Cfg;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Minecraft.class)
public abstract class TsMxn {
	@ModifyVariable(method = "setScreen", at = @At("HEAD"), argsOnly = true)
	private Screen wss(Screen s) {
		if (s != null && s.getClass() == TitleScreen.class && Cfg.c.ttl) return new AzTitle();
		if (s != null && s.getClass() == net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen.class && Cfg.c.btn) return new dev.wscl.AzMulti(((JmAcc) s).par());
		return s;
	}
}
