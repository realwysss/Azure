package dev.ogfg;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.nio.file.Files;
import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;

public class Cfg {
	public boolean on = true;
	public int mode = 0;
	public int col = 0x3a1f5c;
	public int st = 20;
	public int en = 48;
	public int str = 100;
	public boolean soft = true;
	public boolean dims = false;

	static final Gson G = new GsonBuilder().setPrettyPrinting().create();
	static final Path P = FabricLoader.getInstance().getConfigDir().resolve("ogfog.json");
	public static Cfg c = new Cfg();

	public static void ld() {
		try {
			if (Files.exists(P)) {
				Cfg n = G.fromJson(Files.readString(P), Cfg.class);
				if (n != null) c = n;
			}
		} catch (Exception e) {
			c = new Cfg();
		}
		fx();
	}

	public static void fx() {
		c.mode = Math.max(0, Math.min(1, c.mode));
		c.col &= 0xffffff;
		c.st = Math.max(0, Math.min(250, c.st));
		c.en = Math.max(c.st + 2, Math.min(256, c.en));
		c.str = Math.max(0, Math.min(100, c.str));
	}

	public static void sv() {
		fx();
		try {
			Files.writeString(P, G.toJson(c));
		} catch (Exception e) {
		}
	}
}
