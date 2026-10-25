package dev.wscl.mixin;

import dev.wscl.Mod;
import dev.wscl.Ui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiGraphics.class)
public abstract class ShdMxn {
	@Inject(method = "renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;III)V", at = @At("TAIL"))
	private void wss(LivingEntity e, Level l, ItemStack s, int x, int y, int seed, CallbackInfo ci) {
		if (s.isEmpty() || !s.is(Items.SHIELD) || !Mod.live("shield")) return;
		var p = Minecraft.getInstance().player;
		if (p == null) return;
		boolean cd = p.getCooldowns().isOnCooldown(s);
		((GuiGraphics) (Object) this).fill(x, y, x + 16, y + 16, cd ? Ui.al(0xFF2A2A, 0.38f) : Ui.al(0x2AFF5A, 0.22f));
	}
}
