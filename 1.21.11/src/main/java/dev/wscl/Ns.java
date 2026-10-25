package dev.wscl;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.level.block.Blocks;

public class Ns {
	static final String[][] G = {
			{"pearl", "Ender pearl throw", "entity.ender_pearl.throw"},
			{"tele", "Ender pearl teleport", "entity.player.teleport,entity.enderman.teleport"},
			{"totem", "Totem pop", "item.totem.use"},
			{"shield", "Shield", "item.shield.block,item.shield.break"},
			{"hit", "Hit sounds", "entity.player.attack"},
			{"hurt", "Hurt sounds", "entity.player.hurt,entity.player.death"},
			{"armor", "Armor equip", "item.armor.equip"},
			{"elytra", "Elytra", "item.elytra.flying,item.armor.equip_elytra"},
			{"potion", "Potion throw and splash", "entity.splash_potion"},
			{"eat", "Eating and drinking", "entity.generic.eat,entity.generic.drink,entity.player.burp"},
			{"mace", "Mace", "item.mace"},
			{"anchor", "Anchor charge and blow", "block.respawn_anchor"},
			{"steps", "Footsteps", ".step"},
			{"expl", "All other explosions", "entity.generic.explode"}
	};

	static final List<double[]> crystals = new ArrayList<>();
	static int tk;

	public static String ex(String s) {
		return s == null ? "" : s;
	}

	public static void tick(Minecraft m) {
		if (!Mod.live("nosnd") || m.level == null || m.player == null) {
			crystals.clear();
			return;
		}
		tk++;
		crystals.removeIf(c -> tk - c[3] > 12);
		for (EndCrystal e : m.level.getEntitiesOfClass(EndCrystal.class, m.player.getBoundingBox().inflate(64))) crystals.add(new double[]{e.getX(), e.getY(), e.getZ(), tk});
	}

	static boolean near(double x, double y, double z) {
		for (double[] c : crystals) if (Math.abs(c[0] - x) < 3 && Math.abs(c[1] - y) < 3 && Math.abs(c[2] - z) < 3) return true;
		return false;
	}

	static boolean anchor(Minecraft m, double x, double y, double z) {
		if (m.level == null) return false;
		BlockPos b = BlockPos.containing(x, y, z);
		for (int dx = -1; dx <= 1; dx++) for (int dy = -1; dy <= 1; dy++) for (int dz = -1; dz <= 1; dz++) if (m.level.getBlockState(b.offset(dx, dy, dz)).is(Blocks.RESPAWN_ANCHOR)) return true;
		return false;
	}

	public static boolean mute(SoundInstance s) {
		if (!Mod.live("nosnd")) return false;
		var id = s.getIdentifier();
		if (!"minecraft".equals(id.getNamespace())) return false;
		String p = id.getPath();
		boolean ex = p.contains("generic.explode");
		if (ex) {
			Minecraft m = Minecraft.getInstance();
			if (Cfg.gi("nosnd.crys", 0) == 1 && near(s.getX(), s.getY(), s.getZ())) return true;
			if (Cfg.gi("nosnd.anch", 0) == 1 && anchor(m, s.getX(), s.getY(), s.getZ())) return true;
		}
		for (String[] g : G) {
			if (Cfg.gi("nosnd." + g[0], 0) != 1) continue;
			for (String k : g[2].split(",")) if (p.contains(k)) return true;
		}
		String x = Cfg.c.nsx;
		if (x != null && !x.isBlank()) for (String k : x.split(",")) if (!k.isBlank() && p.contains(k.trim())) return true;
		return false;
	}
}
