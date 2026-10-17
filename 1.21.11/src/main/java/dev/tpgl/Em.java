package dev.tpgl;

import dev.tpgl.mixin.PtAcc;
import dev.tpgl.mixin.QdAcc;
import net.minecraft.client.particle.Particle;
import net.minecraft.util.Mth;

public class Em {
	public static void gather() {
		for (int i = Tt.list.size() - 1; i >= 0; i--) {
			Particle p = Tt.list.get(i);
			if (!p.isAlive()) {
				Tt.list.remove(i);
				continue;
			}
			PtAcc a = (PtAcc) p;
			QdAcc q = (QdAcc) p;
			double x = Mth.lerp(Gk.pt, a.xo(), a.x()), y = Mth.lerp(Gk.pt, a.yo(), a.y()), z = Mth.lerp(Gk.pt, a.zo(), a.z());
			int col = Cfg.c.pc ? (int) (Math.min(1f, q.r()) * 255) << 16 | (int) (Math.min(1f, q.g()) * 255) << 8 | (int) (Math.min(1f, q.b()) * 255) : Gk.cur.col;
			Gk.add(x, y, z, 0.7f, col, Math.max(0f, q.al()), System.identityHashCode(p));
		}
	}
}
