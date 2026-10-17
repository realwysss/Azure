package dev.itop;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import net.fabricmc.loader.api.FabricLoader;

public class Cfg {
	public boolean on = true;
	public boolean inv = true;
	public Map<String, Integer> m = new LinkedHashMap<>();

	static final Gson G = new GsonBuilder().setPrettyPrinting().create();
	static final Path P = FabricLoader.getInstance().getConfigDir().resolve("itemsopacity.json");
	public static Cfg c = new Cfg();

	public static void ld() {
		try {
			if (Files.exists(P)) {
				Cfg n = G.fromJson(Files.readString(P), Cfg.class);
				if (n != null && n.m != null) c = n;
			}
		} catch (Exception e) {
			c = new Cfg();
		}
		Op.rb();
	}

	public static void sv() {
		try {
			Files.writeString(P, G.toJson(c));
		} catch (Exception e) {
		}
	}
}
