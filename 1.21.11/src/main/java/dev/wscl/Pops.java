package dev.wscl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class Pops {
	static final Map<UUID, Integer> N = new HashMap<>();
	static final Map<UUID, String> NM = new HashMap<>();

	public static void add(Entity e) {
		if (!(e instanceof Player p)) return;
		N.merge(p.getUUID(), 1, Integer::sum);
		NM.put(p.getUUID(), p.getName().getString());
	}

	public static void clear() {
		N.clear();
		NM.clear();
	}

	static List<String[]> top(int n) {
		List<Map.Entry<UUID, Integer>> es = new ArrayList<>(N.entrySet());
		es.sort((a, b) -> b.getValue() - a.getValue());
		List<String[]> o = new ArrayList<>();
		for (int i = 0; i < Math.min(n, es.size()); i++) o.add(new String[]{NM.get(es.get(i).getKey()), String.valueOf(es.get(i).getValue())});
		return o;
	}
}
