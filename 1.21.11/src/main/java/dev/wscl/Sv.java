package dev.wscl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import net.fabricmc.loader.api.FabricLoader;

public class Sv {
	static final java.util.Map<Path, String> L = new java.util.HashMap<>();

	public static void lg(String s) {
		try {
			Path d = FabricLoader.getInstance().getConfigDir().resolve("azure");
			Files.createDirectories(d);
			Path f = d.resolve("save.log");
			if (Files.exists(f) && Files.size(f) > 100000) Files.delete(f);
			Files.writeString(f, java.time.LocalDateTime.now() + " " + s + "\n", StandardOpenOption.CREATE, StandardOpenOption.APPEND);
		} catch (Throwable e) {
		}
	}

	public static void fw(Path p, String s) throws java.io.IOException {
		Path t = p.resolveSibling(p.getFileName() + ".tmp");
		Files.writeString(t, s);
		try {
			Files.move(t, p, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
		} catch (AtomicMoveNotSupportedException e) {
			Files.move(t, p, StandardCopyOption.REPLACE_EXISTING);
		}
		L.put(p, s);
	}

	public static void wr(Path p, String s) {
		if (s.equals(L.get(p))) return;
		if (!L.containsKey(p) && Files.exists(p)) return;
		try {
			Path t = p.resolveSibling(p.getFileName() + ".tmp");
			Files.writeString(t, s);
			try {
				Files.move(t, p, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
			} catch (AtomicMoveNotSupportedException e) {
				Files.move(t, p, StandardCopyOption.REPLACE_EXISTING);
			}
			L.put(p, s);
		} catch (Throwable e) {
			lg("save " + p.getFileName() + " failed: " + e);
		}
	}

	public static <T> T ld(Path p, Gson g, Class<T> k) {
		String s;
		try {
			if (!Files.exists(p)) return null;
			s = Files.readString(p);
			L.put(p, s);
		} catch (Throwable e) {
			lg("read " + p.getFileName() + " failed: " + e);
			return null;
		}
		try {
			return g.fromJson(s, k);
		} catch (Throwable e) {
			lg("bad " + p.getFileName() + ": " + e);
		}
		try {
			Files.copy(p, p.resolveSibling(p.getFileName() + ".bad"), StandardCopyOption.REPLACE_EXISTING);
			JsonObject o = JsonParser.parseString(s).getAsJsonObject();
			T n = k.getDeclaredConstructor().newInstance();
			for (var en : o.entrySet()) {
				try {
					JsonObject one = new JsonObject();
					one.add(en.getKey(), en.getValue());
					T t = g.fromJson(one, k);
					var f = k.getField(en.getKey());
					f.set(n, f.get(t));
				} catch (Throwable e) {
					lg("skipped " + en.getKey() + " in " + p.getFileName());
				}
			}
			return n;
		} catch (Throwable e) {
			lg("cant fix " + p.getFileName() + ": " + e);
			return null;
		}
	}
}
