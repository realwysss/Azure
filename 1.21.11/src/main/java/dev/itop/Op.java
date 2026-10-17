package dev.itop;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class Op {
	static volatile Object2IntMap<Block> bm = new Object2IntOpenHashMap<>();
	static volatile Object2IntMap<Item> im = new Object2IntOpenHashMap<>();
	public static volatile boolean any;
	public static volatile int cur = 100;

	public static void rb() {
		Object2IntMap<Block> b = new Object2IntOpenHashMap<>();
		Object2IntMap<Item> i = new Object2IntOpenHashMap<>();
		for (Map.Entry<String, Integer> e : Cfg.c.m.entrySet()) {
			Identifier id = Identifier.tryParse(e.getKey());
			if (id == null) continue;
			int v = Math.max(0, Math.min(100, e.getValue()));
			if (v >= 100) continue;
			Block bl = BuiltInRegistries.BLOCK.getOptional(id).orElse(null);
			if (bl != null) b.put(bl, v);
			Item it = BuiltInRegistries.ITEM.getOptional(id).orElse(null);
			if (it != null) i.put(it, v);
		}
		b.defaultReturnValue(100);
		i.defaultReturnValue(100);
		bm = b;
		im = i;
		any = Cfg.c.on && (!b.isEmpty() || !i.isEmpty());
	}

	public static int of(BlockState s) {
		return any ? bm.getInt(s.getBlock()) : 100;
	}

	public static int ofItem(ItemStack s) {
		return any && Cfg.c.inv ? im.getInt(s.getItem()) : 100;
	}

	public static String key(Item i) {
		return BuiltInRegistries.ITEM.getKey(i).toString();
	}

	public static int get(String k) {
		Integer v = Cfg.c.m.get(k);
		return v == null ? 100 : v;
	}

	public static void set(String k, int v) {
		if (v >= 100) Cfg.c.m.remove(k);
		else Cfg.c.m.put(k, v);
		rb();
	}

	public static void flush() {
		Cfg.sv();
		rb();
		Minecraft m = Minecraft.getInstance();
		if (m.levelRenderer != null && m.level != null) m.levelRenderer.allChanged();
	}
}
