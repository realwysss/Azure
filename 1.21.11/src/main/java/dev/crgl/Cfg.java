package dev.crgl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.nio.file.Files;
import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;

public class Cfg extends Lay {
	public Lay core = Lay.of(0xff7ae8, 60, 50, 1, 64, 64);

	public static long T0 = System.nanoTime();
	static final Gson G = new GsonBuilder().setPrettyPrinting().create();
	static final Path P = FabricLoader.getInstance().getConfigDir().resolve("crystalglow.json");
	public static Cfg c = new Cfg();

	public static String json() {
		return G.toJson(c);
	}

	public Lay[] layers() {
		return core.on ? new Lay[]{this, core} : new Lay[]{this};
	}

	public Cfg copy() {
		return G.fromJson(G.toJson(this), Cfg.class);
	}

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
		c.clamp();
		c.core.clamp();
	}

	public static void sv() {
		fx();
		try {
			Files.writeString(P, G.toJson(c));
		} catch (Exception e) {
		}
	}
}
