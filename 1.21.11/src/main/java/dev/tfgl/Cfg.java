package dev.tfgl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.nio.file.Files;
import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;

public class Cfg {
	public boolean on = true;
	public int pre = 0;
	public int str = 100;
	public int spd = 100;
	public int siz = 100;
	public int ang = 10;
	public int col = 0xffffff;
	public boolean itm = true;
	public boolean arm = true;

	static final Gson G = new GsonBuilder().setPrettyPrinting().create();
	static final Path D = FabricLoader.getInstance().getConfigDir().resolve("tuffglint");
	static final Path P = FabricLoader.getInstance().getConfigDir().resolve("tuffglint.json");
	public static Cfg c = new Cfg();

	public static void ld() {
		try {
			if (Files.exists(P)) {
				Cfg n = G.fromJson(Files.readString(P), Cfg.class);
				if (n != null) c = n;
			}
			Files.createDirectories(D);
		} catch (Exception e) {
			c = new Cfg();
		}
		fx();
	}

	public static void fx() {
		c.pre = Math.max(0, Math.min(4, c.pre));
		c.str = Math.max(0, Math.min(1000, c.str));
		c.spd = Math.max(10, Math.min(1000, c.spd));
		c.siz = Math.max(25, Math.min(400, c.siz));
		c.ang = Math.max(-180, Math.min(180, c.ang));
		c.col &= 0xffffff;
	}

	public static void sv() {
		fx();
		try {
			Files.writeString(P, G.toJson(c));
		} catch (Exception e) {
		}
	}
}
