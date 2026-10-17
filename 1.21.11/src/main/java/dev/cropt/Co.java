package dev.cropt;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class Co {
	public static boolean on = true;
	public static boolean remote;
	public static boolean allowed = true;
	public static int hits;
	public static int png = -1;
	static int mine;
	static Item last;

	public static boolean active() {
		return on && !(remote && !allowed);
	}

	public static void reset() {
		remote = false;
		allowed = true;
		hits = 0;
		mine = 0;
		last = null;
	}

	static int ping(Minecraft m) {
		if (png >= 0) return png;
		if (m.getConnection() == null) return 0;
		PlayerInfo i = m.getConnection().getPlayerInfo(m.player.getUUID());
		return i == null ? 0 : i.getLatency();
	}

	static boolean base(BlockState s) {
		return s.is(Blocks.OBSIDIAN) || s.is(Blocks.BEDROCK);
	}

	static BlockHitResult base(Minecraft m, BlockPos p, BlockHitResult src) {
		if (base(m.level.getBlockState(p))) return src;
		BlockPos b = p.below();
		if (base(m.level.getBlockState(b))) return new BlockHitResult(Vec3.atCenterOf(b).add(0, 0.5, 0), Direction.UP, b, false);
		return null;
	}

	public static BlockHitResult target(Minecraft m) {
		HitResult h = m.hitResult;
		if (h instanceof BlockHitResult b && b.getType() == HitResult.Type.BLOCK) {
			BlockHitResult r = base(m, b.getBlockPos(), b);
			if (r != null) return r;
		}
		Vec3 e = m.player.getEyePosition();
		BlockHitResult ray = m.level.clip(new ClipContext(e, e.add(m.player.getLookAngle().scale(4.5)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, m.player));
		return base(m, ray.getBlockPos(), ray);
	}

	public static boolean can(Minecraft m, BlockPos p) {
		if (!base(m.level.getBlockState(p))) return false;
		BlockPos a = p.above();
		if (!m.level.isEmptyBlock(a)) return false;
		return m.level.getEntities((Entity) null, new AABB(a.getX(), a.getY(), a.getZ(), a.getX() + 1, a.getY() + 2, a.getZ() + 1)).isEmpty();
	}

	static EndCrystal crystal(Minecraft m) {
		return m.hitResult instanceof EntityHitResult e && e.getEntity() instanceof EndCrystal c ? c : null;
	}

	public static void tick() {
		Minecraft m = Minecraft.getInstance();
		if (!active() || m.player == null || m.level == null || m.gameMode == null) return;
		Item it = m.player.getMainHandItem().getItem();
		if (it != last) {
			hits = 0;
			mine = 0;
			if (it == Items.END_CRYSTAL) ((CoAcc) m).cool();
			last = it;
		}
		boolean atk = m.options.keyAttack.isDown();
		mine = atk ? mine + 1 : 0;
		if (!atk) hits = 0;
		if (mine > 2) return;
		EndCrystal c = crystal(m);
		if (atk && c != null && hits < (ping(m) < 50 ? 1 : 2)) {
			if (hits >= 1) c.discard();
			hits++;
		}
		if (it != Items.END_CRYSTAL || !m.options.keyUse.isDown()) return;
		BlockHitResult t = target(m);
		if (t == null) return;
		((CoAcc) m).cool();
		m.gameMode.useItemOn(m.player, InteractionHand.MAIN_HAND, t);
		if (can(m, t.getBlockPos())) m.player.swing(InteractionHand.MAIN_HAND);
	}
}
