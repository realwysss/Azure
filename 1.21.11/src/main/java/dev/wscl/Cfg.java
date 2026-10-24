package dev.wscl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.nio.file.Files;
import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;

public class Cfg {
	public boolean hud = true;
	public boolean fps = true;
	public boolean png = true;
	public boolean cps = true;
	public boolean xyz = false;
	public boolean key = false;
	public boolean arm = true;
	public boolean clk = false;
	public int ver = 2;
	public int te;
	public int ms = 100;
	public boolean ds = true;
	public boolean lite;
	public boolean onl = true;
	public String api = "";
	public String uid = "";
	public String rd1 = "Playing {server}";
	public String rd2 = "{account}";
	public String rid = "1561051516851191838";
	public java.util.Map<String, Integer> v = new java.util.HashMap<>();
	public int hx = 1;
	public int hy = 1;
	public int kx = 99;
	public int ky = 99;
	public int ax = 99;
	public int ay = 40;
	public boolean pvp = false;
	public boolean fod = true;
	public boolean shd = true;
	public int px = 1;
	public int py = 50;
	public boolean lod = true;
	public int sfx = 0;
	public int sfv = 80;
	public boolean ttl = true;
	public boolean btn = true;
	public boolean xh = true;
	public int xs = 0;
	public int xz = 6;
	public int xt = 2;
	public int xg = 3;
	public int xc = 0xFFFFFF;
	public boolean xo = true;
	public boolean xd = true;
	public boolean hf = true;
	public int tc = 0xFFFFFF;
	public int bs = 0;
	public int am = 0;
	public int sc = 100;
	public int op = 45;
	public boolean tsp = false;
	public boolean zm = true;
	public int zs = 70;
	public String lv = "";
	public String useen = "";
	public boolean fs = false;
	public String m1 = "gg";
	public String m2 = "";
	public String m3 = "";
	public String m4 = "";
	public boolean nsh = false;
	public boolean tag = true;

	static final Gson G = new GsonBuilder().setPrettyPrinting().create();
	static final Path P = FabricLoader.getInstance().getConfigDir().resolve("azure.json");
	public static Cfg c = new Cfg();
	public static boolean first;

	public static void ld() {
		first = !Files.exists(P);
		try {
			if (Files.exists(P)) {
				Cfg n = G.fromJson(Files.readString(P), Cfg.class);
				if (n != null) {
					c = n;
					if (c.ver < 2) {
						Cfg d = new Cfg();
						c.ver = 2;
						c.hx = d.hx;
						c.hy = d.hy;
						c.kx = d.kx;
						c.ky = d.ky;
						c.ax = d.ax;
						c.ay = d.ay;
						c.px = d.px;
						c.py = d.py;
					}
				}
			}
		} catch (Exception e) {
			c = new Cfg();
		}
		fx();
	}

	public static int gi(String k, int d) {
		if (c.v == null) c.v = new java.util.HashMap<>();
		return c.v.getOrDefault(k, d);
	}

	public static void si(String k, int x) {
		if (c.v == null) c.v = new java.util.HashMap<>();
		c.v.put(k, x);
	}

	static final java.util.Set<String> DEF = java.util.Set.of("rpc", "skinchg", "snippet", "kbsearch");

	public static boolean on(String id) {
		return gi(id + ".on", DEF.contains(id) ? 1 : 0) == 1;
	}

	public static void fx() {
		c.sc = Math.max(50, Math.min(200, c.sc));
		c.op = Math.max(0, Math.min(100, c.op));
		c.zs = Math.max(10, Math.min(95, c.zs));
		c.hx = Math.max(0, Math.min(100, c.hx));
		c.hy = Math.max(0, Math.min(100, c.hy));
		c.kx = Math.max(0, Math.min(100, c.kx));
		c.ky = Math.max(0, Math.min(100, c.ky));
		c.xs = Math.max(0, Math.min(3, c.xs));
		c.xz = Math.max(1, Math.min(30, c.xz));
		c.xt = Math.max(1, Math.min(8, c.xt));
		c.xg = Math.max(0, Math.min(20, c.xg));
		c.bs = Math.max(0, Math.min(3, c.bs));
		c.am = Math.max(0, Math.min(2, c.am));
		c.sfx = Math.max(0, Math.min(4, c.sfx));
		c.sfv = Math.max(0, Math.min(100, c.sfv));
		c.px = Math.max(0, Math.min(100, c.px));
		c.py = Math.max(0, Math.min(100, c.py));
		c.ax = Math.max(0, Math.min(100, c.ax));
		c.ay = Math.max(0, Math.min(100, c.ay));
	}

	public static String js() {
		return G.toJson(c);
	}

	public static void sv() {
		fx();
		try {
			Files.writeString(P, G.toJson(c));
		} catch (Exception e) {
		}
	}
}
