package dev.tpgl;

import dev.tpgl.mixin.PtAcc;
import dev.tpgl.mixin.QdAcc;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import net.minecraft.client.particle.Particle;
import net.minecraft.util.Mth;

public class Em {
	static final class Gh {
		double x, y, z;
		int col;
		float a;
		long t0;
		int id;
	}

	static final ArrayList<Gh> GH = new ArrayList<>();
	static final IdentityHashMap<Particle, Float> PK = new IdentityHashMap<>();

	public static void gather() {
		long now = System.currentTimeMillis();
		long life = Cfg.c.dur * 100L;
		for (int i = Tt.list.size() - 1; i >= 0; i--) {
			Particle p = Tt.list.get(i);
			PtAcc a = (PtAcc) p;
			QdAcc q = (QdAcc) p;
			int col = Cfg.c.pc ? (int) (Math.min(1f, q.r()) * 255) << 16 | (int) (Math.min(1f, q.g()) * 255) << 8 | (int) (Math.min(1f, q.b()) * 255) : Gk.cur.col;
			if (!p.isAlive()) {
				Float pk = PK.remove(p);
				if (life > 0) {
					Gh h = new Gh();
					h.x = a.x();
					h.y = a.y();
					h.z = a.z();
					h.col = col;
					h.a = pk == null ? 0.6f : Math.min(1f, pk * 0.8f);
					h.t0 = now;
					h.id = System.identityHashCode(p);
					GH.add(h);
				}
				Tt.list.remove(i);
				continue;
			}
			double x = Mth.lerp(Gk.pt, a.xo(), a.x()), y = Mth.lerp(Gk.pt, a.yo(), a.y()), z = Mth.lerp(Gk.pt, a.zo(), a.z());
			float al = Math.max(0f, q.al());
			if (life > 0) PK.merge(p, al, Math::max);
			Gk.add(x, y, z, 0.7f, col, al, System.identityHashCode(p));
		}
		for (int i = GH.size() - 1; i >= 0; i--) {
			Gh h = GH.get(i);
			float f = 1f - (now - h.t0) / (float) Math.max(1L, life);
			if (f <= 0f || life == 0) {
				GH.remove(i);
				continue;
			}
			Gk.add(h.x, h.y, h.z, 0.7f, h.col, h.a * f * f, h.id);
		}
	}
}
