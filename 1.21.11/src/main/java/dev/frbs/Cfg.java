package dev.frbs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.nio.file.Files;
import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;

public class Cfg {
	public boolean noyld = false;
	public int prio = 0;
	public boolean rthr = false;

	public static Cfg c = new Cfg();
	static final Gson G = new GsonBuilder().setPrettyPrinting().create();
	static final Path P = FabricLoader.getInstance().getConfigDir().resolve("frameboost.json");

	public static void load() {
		try {
			if (Files.exists(P)) c = G.fromJson(Files.readString(P), Cfg.class);
		} catch (Exception e) {
			c = new Cfg();
		}
		save();
	}

	public static void save() {
		try {
			Files.writeString(P, G.toJson(c));
		} catch (Exception e) {
		}
	}
}
