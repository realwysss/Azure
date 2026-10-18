package dev.wscl.mixin;

import dev.wscl.Mod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RespawnAnchorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
public abstract class GmMxn {
	@Unique private BlockPos azp;
	@Unique private BlockState azs;
	@Unique private boolean azf;

	@Inject(method = "attack", at = @At("RETURN"))
	private void wsa(Player p, Entity e, CallbackInfo ci) {
		Minecraft m = Minecraft.getInstance();
		if (!Mod.live("mcrys") || p != m.player || !(e instanceof EndCrystal) || !(e.level() instanceof ClientLevel cl)) return;
		cl.removeEntity(e.getId(), Entity.RemovalReason.KILLED);
	}

	@Inject(method = "useItemOn", at = @At("HEAD"))
	private void wsh(LocalPlayer p, InteractionHand h, BlockHitResult r, CallbackInfoReturnable<InteractionResult> cir) {
		azs = null;
		if (!Mod.live("hranch") && !Mod.live("cbanch")) return;
		BlockState st = p.level().getBlockState(r.getBlockPos());
		if (!st.is(Blocks.RESPAWN_ANCHOR)) return;
		azp = r.getBlockPos();
		azs = st;
		azf = p.getItemInHand(h).is(Items.GLOWSTONE);
	}

	@Inject(method = "useItemOn", at = @At("RETURN"))
	private void wsr(LocalPlayer p, InteractionHand h, BlockHitResult r, CallbackInfoReturnable<InteractionResult> cir) {
		if (azs == null || !cir.getReturnValue().consumesAction()) return;
		Level l = p.level();
		int ch = azs.getValue(RespawnAnchorBlock.CHARGE);
		boolean nether = l.dimension() == Level.NETHER;
		if (azf && ch < 4) {
			if (Mod.live("cbanch")) l.setBlock(azp, azs.setValue(RespawnAnchorBlock.CHARGE, ch + 1), 11);
		} else if (!nether && ch > 0) {
			l.setBlock(azp, Blocks.AIR.defaultBlockState(), 11);
		}
		azs = null;
	}
}
