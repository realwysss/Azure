package dev.wscl;

public class Ffp {
	public static int real;
	static int lst = -1;
	static final java.util.Random R = new java.util.Random();

	public static int fake(int r) {
		real = r;
		if (!Mod.live("fakefps")) {
			lst = -1;
			return r;
		}
		int lo = Cfg.gi("fakefps.lo", 240);
		int hi = Math.max(lo, Cfg.gi("fakefps.hi", 300));
		int sp = Math.max(1, (hi - lo) / 4);
		int v = lst < lo || lst > hi ? lo + R.nextInt(hi - lo + 1) : lst + R.nextInt(sp * 2 + 1) - sp;
		lst = Math.max(lo, Math.min(hi, v));
		return lst;
	}
}
