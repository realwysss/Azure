package dev.tpgl;

import java.util.ArrayList;
import net.minecraft.client.particle.Particle;

public class Tt {
	public static final ArrayList<Particle> list = new ArrayList<>();

	public static void add(Particle p) {
		if (list.size() > 400) list.remove(0);
		list.add(p);
	}
}
