package dev.snprb.mxn;

import dev.snprb.cfg;
import dev.snprb.snprb;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ChatComponent.class)
public class chmx {
	@ModifyVariable(method = "addMessage(Lnet/minecraft/network/chat/Component;)V", at = @At("HEAD"), argsOnly = true)
	private Component snprbmsg(Component t) {
		if (cfg.c.msg && dev.wscl.Mod.live("snippet") && t.getContents() instanceof TranslatableContents tc && tc.getKey().equals("screenshot.success") && tc.getArgs().length > 0) {
			Object a = tc.getArgs()[0];
			String n = a instanceof Component k ? k.getString() : String.valueOf(a);
			if (snprb.fl(n) != null) return snprb.bar(n);
		}
		return t;
	}
}
