package dev.wscl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;

public class Prfs {
	public static Path dir() {
		Path p = FabricLoader.getInstance().getConfigDir().resolve("azure").resolve("profiles");
		try {
			Files.createDirectories(p);
		} catch (IOException e) {
		}
		return p;
	}

	static String clean(String n) {
		String s = n == null ? "" : n.trim().replaceAll("[^A-Za-z0-9 _.-]", "");
		return s.isEmpty() ? "profile" : s.substring(0, Math.min(32, s.length()));
	}

	public static List<String> names() {
		List<Path> l = new ArrayList<>();
		try (var st = Files.list(dir())) {
			st.filter(p -> p.getFileName().toString().endsWith(".azp")).forEach(l::add);
		} catch (IOException e) {
		}
		l.sort(Comparator.comparingLong((Path p) -> p.toFile().lastModified()).reversed());
		List<String> o = new ArrayList<>();
		for (Path p : l) {
			String f = p.getFileName().toString();
			o.add(f.substring(0, f.length() - 4));
		}
		return o;
	}

	public static void save(String name) {
		try {
			Files.writeString(dir().resolve(clean(name) + ".azp"), Prf.out());
		} catch (IOException e) {
		}
	}

	public static boolean load(String name) {
		try {
			return Prf.in(Files.readString(dir().resolve(name + ".azp")));
		} catch (IOException e) {
			return false;
		}
	}

	public static void del(String name) {
		try {
			Files.deleteIfExists(dir().resolve(name + ".azp"));
		} catch (IOException e) {
		}
	}

	public static long time(String name) {
		return dir().resolve(name + ".azp").toFile().lastModified();
	}

	public static void share(String name) {
		try {
			Minecraft.getInstance().keyboardHandler.setClipboard(Files.readString(dir().resolve(name + ".azp")).trim());
		} catch (IOException e) {
		}
	}

	public static void folder() {
		net.minecraft.util.Util.getPlatform().openPath(dir());
	}
}
