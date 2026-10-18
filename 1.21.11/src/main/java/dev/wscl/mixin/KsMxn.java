package dev.wscl.mixin;

import dev.wscl.Cfg;
import dev.wscl.Kbf;
import dev.wscl.Mod;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.client.gui.screens.options.controls.KeyBindsList;
import net.minecraft.client.gui.screens.options.controls.KeyBindsScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyBindsScreen.class)
public abstract class KsMxn extends OptionsSubScreen {
	@Shadow private KeyBindsList keyBindsList;

	public KsMxn(net.minecraft.client.gui.screens.Screen p, net.minecraft.client.Options o, Component t) {
		super(p, o, t);
	}

	@Inject(method = "addContents", at = @At("RETURN"))
	private void wsi(CallbackInfo ci) {
		if (!Cfg.c.btn || !Mod.live("kbsearch")) return;
		EditBox e = new EditBox(font, width - 170, 8, 160, 18, Component.literal("Search"));
		e.setHint(Component.literal("Search keybinds"));
		e.setMaxLength(32);
		e.setResponder(s -> ((Kbf) keyBindsList).azf(s));
		addRenderableWidget(e);
	}
}
