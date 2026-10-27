package dev.wscl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class Mod {
	public static final List<Mod> ALL = new ArrayList<>();
	static final java.util.HashMap<String, Mod> BY = new java.util.HashMap<>();
	public static final String[] CT = {"PvP", "HUD", "Visuals", "Player", "Wysss"};

	public final String id;
	public final String nm;
	public final String ds;
	public final int ct;
	public final String ic;
	public final List<Rw.R> st = new ArrayList<>();
	public final BooleanSupplier on;
	public final Consumer<Boolean> set;
	public final Runnable go;
	public boolean wy;
	public String grp;

	Mod(String id, String nm, String ds, int ct, String ic, BooleanSupplier on, Consumer<Boolean> set, Runnable go) {
		this.id = id;
		this.nm = nm;
		this.ds = ds;
		this.ct = ct;
		this.ic = ic;
		this.on = on;
		this.set = set;
		this.go = go;
	}

	public static Mod add(String id, String nm, String ds, int ct, String ic, BooleanSupplier on, Consumer<Boolean> set) {
		Mod m = new Mod(id, nm, ds, ct, ic, on, set, null);
		ALL.add(m);
		BY.put(id, m);
		return m;
	}

	public static Mod own(String id, String nm, String ds, int ct, String ic) {
		String k = id + ".on";
		boolean d = Cfg.DEF.contains(id);
		return add(id, nm, ds, ct, ic, () -> Cfg.gi(k, d ? 1 : 0) == 1, v -> Cfg.si(k, v ? 1 : 0));
	}

	public static Mod ext(String id, String nm, String ds, String ic, Runnable go) {
		Mod m = new Mod(id, nm, ds, 4, ic, () -> false, v -> {
		}, go);
		ALL.add(m);
		BY.put(id, m);
		return m;
	}

	public Mod g(String x) {
		grp = x;
		return this;
	}

	public void put(boolean v) {
		if (v && ct == 0 && Cfg.gi("pvpok", 0) == 0) {
			var mc = net.minecraft.client.Minecraft.getInstance();
			mc.setScreen(new AzWarn(mc.screen, nm, () -> {
				Cfg.si("pvpok", 1);
				put(true);
			}));
			return;
		}
		set.accept(v);
		if (v && grp != null) for (Mod o : ALL) if (o != this && grp.equals(o.grp) && o.on.getAsBoolean()) o.set.accept(false);
	}

	public Mod wy() {
		wy = true;
		return this;
	}

	public Mod a(Rw.R r) {
		st.add(r);
		return this;
	}

	public Mod tg(String l, String k, boolean d) {
		return a(Rw.tg(l, () -> Cfg.gi(id + "." + k, d ? 1 : 0) == 1, v -> Cfg.si(id + "." + k, v ? 1 : 0)));
	}

	public Mod sl(String l, String k, int d, int lo, int hi, String su) {
		return a(Rw.sl(l, () -> Cfg.gi(id + "." + k, d), v -> Cfg.si(id + "." + k, v), lo, hi, su));
	}

	public Mod ch(String l, String k, int d, String... o) {
		return a(new Rw.Ch(l, o, () -> Cfg.gi(id + "." + k, d), v -> Cfg.si(id + "." + k, v)));
	}

	public Mod cl(String l, String k, int d) {
		return a(new Rw.Cl(l, () -> Cfg.gi(id + "." + k, d), v -> Cfg.si(id + "." + k, v & 0xFFFFFF)));
	}

	public int i(String k, int d) {
		return Cfg.gi(id + "." + k, d);
	}

	public static boolean live(String id) {
		Mod m = get(id);
		return m != null && m.on.getAsBoolean();
	}

	public static Mod get(String id) {
		return BY.get(id);
	}
}
