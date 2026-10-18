package dev.wscl;

import java.nio.file.Files;
import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;

public class Dog {
	static volatile long beat = System.currentTimeMillis();
	static Thread th;

	public static void tick() {
		beat = System.currentTimeMillis();
		if (th != null) return;
		th = new Thread(Dog::loop, "azure-dog");
		th.setDaemon(true);
		th.start();
	}

	static void loop() {
		boolean dumped = false;
		while (true) {
			try {
				Thread.sleep(500);
			} catch (Exception e) {
				return;
			}
			long age = System.currentTimeMillis() - beat;
			if (age < 4000) {
				dumped = false;
				continue;
			}
			if (dumped) continue;
			dumped = true;
			try {
				StringBuilder b = new StringBuilder("stalled " + age + " ms at " + java.time.LocalDateTime.now() + "\n");
				for (var e : Thread.getAllStackTraces().entrySet()) {
					String n = e.getKey().getName();
					if (!n.contains("Render") && !n.contains("main") && !n.startsWith("azure") && !n.contains("Netty")) continue;
					b.append("\n").append(n).append("\n");
					for (var s : e.getValue()) b.append("  ").append(s).append("\n");
				}
				Path d = FabricLoader.getInstance().getConfigDir().resolve("azure");
				Files.createDirectories(d);
				Files.writeString(d.resolve("freeze.log"), b.toString(), java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
			} catch (Throwable t) {
			}
		}
	}
}
