package dev.snprb;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.nio.file.Files;
import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;

public class cfg {
	public static cfg c = new cfg();
	public boolean dwn = true;
	public boolean msg = true;

	static final Gson gs = new GsonBuilder().setPrettyPrinting().create();
	static final Path pth = FabricLoader.getInstance().getConfigDir().resolve("snprb.json");

	public static void ld() {
		try {
			if (Files.exists(pth)) {
				cfg n = gs.fromJson(Files.readString(pth), cfg.class);
				if (n != null) c = n;
			} else sv();
		} catch (Exception e) {
			c = new cfg();
		}
	}

	public static void sv() {
		try {
			Files.writeString(pth, gs.toJson(c));
		} catch (Exception e) {
		}
	}
}
