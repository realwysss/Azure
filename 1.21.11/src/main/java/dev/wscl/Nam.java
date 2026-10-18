package dev.wscl;

public class Nam {
	public static boolean blank(String n) {
		return n == null || n.isBlank() || n.equals("Minecraft Server");
	}

	public static String nm(String ip) {
		String h = ip == null ? "" : ip.trim().toLowerCase();
		int c = h.indexOf(':');
		if (c >= 0) h = h.substring(0, c);
		String[] p = h.split("[.]");
		if (p.length > 1) h = p[p.length - 2];
		else h = p.length == 1 ? p[0] : "";
		if (h.isEmpty()) return "Server";
		String sx = "";
		if (h.length() > 3 && h.endsWith("smp")) {
			sx = "SMP";
			h = h.substring(0, h.length() - 3);
		} else if (h.length() > 2 && h.endsWith("mc")) {
			sx = "MC";
			h = h.substring(0, h.length() - 2);
		}
		return Character.toUpperCase(h.charAt(0)) + h.substring(1) + sx;
	}

	public static void dedupe(java.util.List<net.minecraft.client.multiplayer.ServerData> l) {
		java.util.Set<String> seen = new java.util.HashSet<>();
		l.removeIf(s -> !seen.add((s.ip == null ? "" : s.ip.trim().toLowerCase()) + "|" + (s.name == null ? "" : s.name)));
	}

	public static void fix(java.util.List<net.minecraft.client.multiplayer.ServerData> l) {
		if (!Mod.live("namer")) return;
		for (var s : l) if (blank(s.name) && s.ip != null) s.name = nm(s.ip);
	}
}
