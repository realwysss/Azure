package dev.wscl.mixin;

import dev.wscl.Cfg;
import dev.wscl.Hud;
import dev.wscl.Mod;
import dev.wscl.Ui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public abstract class CcMxn<T extends AbstractContainerMenu> {
	@Shadow protected int leftPos;
	@Shadow protected int topPos;
	@Shadow protected int imageWidth;
	@Shadow protected int imageHeight;
	@Shadow protected T menu;

	@Inject(method = "renderBackground", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;renderBg(Lnet/minecraft/client/gui/GuiGraphics;FII)V"))
	private void wsc(GuiGraphics g, int mx, int my, float d, CallbackInfo ci) {
		Object o = this;
		if (!Cfg.c.btn || !Mod.live("cinv") || o instanceof CreativeModeInventoryScreen) return;
		int ac = Hud.ac() & 0xFFFFFF;
		int x = leftPos - 5;
		int y = topPos - 5;
		int w = imageWidth + 10;
		int h = imageHeight + 10;
		Ui.glow(g, x, y, w, h, 10, ac, 0.5f);
		Ui.rr(g, x, y, w, h, 10, Ui.al(0x070F14, 0.94f));
		Ui.ro(g, x, y, w, h, 10, Ui.al(0xFFFFFF, 0.14f));
		for (Slot s : menu.slots) {
			Ui.rr(g, leftPos + s.x - 1, topPos + s.y - 1, 18, 18, 4, Ui.al(0x000000, 0.4f));
			Ui.ro(g, leftPos + s.x - 1, topPos + s.y - 1, 18, 18, 4, Ui.al(0xFFFFFF, 0.08f));
		}
	}

	@ModifyConstant(method = "renderLabels", constant = @Constant(intValue = -12566464))
	private int wsl(int c) {
		return 0xFFC4E4F5;
	}
}
