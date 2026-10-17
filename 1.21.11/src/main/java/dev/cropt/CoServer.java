package dev.cropt;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.nio.file.Files;
import java.nio.file.Path;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;

public class CoServer implements DedicatedServerModInitializer {
	public boolean allow = false;

	public void onInitializeServer() {
		Gson g = new GsonBuilder().setPrettyPrinting().create();
		Path p = FabricLoader.getInstance().getConfigDir().resolve("crystalopt_server.json");
		CoServer c = new CoServer();
		try {
			if (Files.exists(p)) c = g.fromJson(Files.readString(p), CoServer.class);
			else Files.writeString(p, g.toJson(c));
		} catch (Exception e) {
		}
		boolean ok = c != null && c.allow;
		ServerPlayConnectionEvents.JOIN.register((h, s, m) -> ServerPlayNetworking.send(h.getPlayer(), new CoPayload(ok)));
	}
}
