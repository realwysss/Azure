package dev.wscl.mixin;

import dev.wscl.AzTitle;
import dev.wscl.Cfg;
import dev.wscl.Gs;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Minecraft.class)
public abstract class TsMxn {
	@ModifyVariable(method = "setScreen", at = @At("HEAD"), argsOnly = true)
	private Screen wss(Screen s) {
		if (s != null && s.getClass() == TitleScreen.class && Cfg.c.ttl) s = new AzTitle();
		else if (s != null && s.getClass() == net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen.class && Cfg.c.btn) s = new dev.wscl.AzMulti(((JmAcc) s).par());
		Gs.ap((Minecraft) (Object) this, s);
		return s;
	}

	@Redirect(method = "resizeDisplay", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/Window;calculateScale(IZ)I"))
	private int gsc(Window w, int g, boolean u) {
		Minecraft m = (Minecraft) (Object) this;
		return Gs.sc(m, m.screen, w.calculateScale(g, u));
	}
}
