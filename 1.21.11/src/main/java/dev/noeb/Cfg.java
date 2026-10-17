package dev.noeb;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.nio.file.Files;
import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;

public class Cfg {
	public boolean on = true;
	public int amt = 0;

	static final Gson G = new GsonBuilder().setPrettyPrinting().create();
	static final Path P = FabricLoader.getInstance().getConfigDir().resolve("noeb.json");
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
		c.amt = Math.max(0, Math.min(100, c.amt));
	}

	public static void sv() {
		c.amt = Math.max(0, Math.min(100, c.amt));
		try {
			Files.writeString(P, G.toJson(c));
		} catch (Exception e) {
		}
	}

	public static float fx() {
		return c.on ? c.amt / 100f : 1f;
	}
}