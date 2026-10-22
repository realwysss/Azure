package dev.wscl.mixin;

import dev.wscl.Cfg;
import dev.wscl.Mod;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public abstract class WyMxn {
	@Shadow private int rightClickDelay;
	@Shadow public LocalPlayer player;
	@Shadow public ClientLevel level;
	@Shadow public MultiPlayerGameMode gameMode;
	@Shadow public HitResult hitResult;
	@Unique private int wsl = -1;

	@Unique
	private int wsr(String id) {
		int lo = Cfg.gi(id + ".min", 1);
		int hi = Math.max(lo, Cfg.gi(id + ".max", 2));
		return lo + (hi > lo ? (int) (Math.random() * (hi - lo + 1)) : 0);
	}

	@Unique
	private boolean wsh(ItemStack a, ItemStack b, java.util.function.Predicate<ItemStack> p) {
		return p.test(a) || p.test(b);
	}

	@Inject(method = "tick", at = @At("HEAD"))
	private void wst(CallbackInfo ci) {
		if (player == null || level == null) return;
		ItemStack mh = player.getMainHandItem();
		ItemStack oh = player.getOffhandItem();
		if (Mod.live("nopd") && rightClickDelay > 0 && !wsh(mh, oh, s -> s.has(DataComponents.EQUIPPABLE))) rightClickDelay = Math.min(rightClickDelay, wsr("nopd"));
		if (Mod.live("nosd")) {
			int sl = player.getInventory().getSelectedSlot();
			if (wsl != -1 && wsl != sl && rightClickDelay > 0) rightClickDelay = Math.min(rightClickDelay, wsr("nosd"));
			wsl = sl;
		}
		if (Mod.live("wcrys") && rightClickDelay > 0 && wsh(mh, oh, s -> s.is(Items.END_CRYSTAL))) rightClickDelay = Math.min(rightClickDelay, wsr("wcrys"));
		if (Mod.live("wanch") && rightClickDelay > 0 && wsh(mh, oh, s -> s.is(Items.RESPAWN_ANCHOR) || s.is(Items.GLOWSTONE))) rightClickDelay = Math.min(rightClickDelay, wsr("wanch"));
	}

	@Inject(method = "startAttack", at = @At("HEAD"), cancellable = true)
	private void wsa(CallbackInfoReturnable<Boolean> cir) {
		if (!Mod.live("wcrys") || player == null || level == null || hitResult == null || hitResult.getType() != HitResult.Type.BLOCK) return;
		Vec3 eye = player.getEyePosition(1f);
		Vec3 end = eye.add(player.getViewVector(1f).scale(4.5));
		AABB box = new AABB(eye, end).inflate(1.0);
		List<Entity> es = level.getEntities(player, box, e -> e instanceof EndCrystal);
		for (Entity e : es) {
			if (e.getBoundingBox().inflate(0.2).clip(eye, end).isPresent()) {
				gameMode.attack(player, e);
				player.swing(InteractionHand.MAIN_HAND);
				cir.setReturnValue(true);
				return;
			}
		}
	}

	@Inject(method = "startUseItem", at = @At("HEAD"))
	private void wsu(CallbackInfo ci) {
		if (!Mod.live("wanch") || player == null || level == null) return;
		if (hitResult == null || hitResult.getType() != HitResult.Type.ENTITY) return;
		HitResult h = player.pick(4.5, 1f, false);
		if (!(h instanceof BlockHitResult bh) || h.getType() != HitResult.Type.BLOCK) return;
		ItemStack mh = player.getMainHandItem();
		ItemStack oh = player.getOffhandItem();
		boolean hold = wsh(mh, oh, s -> s.is(Items.RESPAWN_ANCHOR) || s.is(Items.GLOWSTONE));
		if (hold || level.getBlockState(bh.getBlockPos()).is(Blocks.RESPAWN_ANCHOR)) hitResult = h;
	}
}
