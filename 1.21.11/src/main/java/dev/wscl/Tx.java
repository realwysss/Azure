package dev.wscl;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.fabric.api.resource.v1.pack.PackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.repository.PackRepository;

public class Tx {
	public static final String[] CI = {"crystal", "anchor", "stone", "deepslate", "snow", "glowstone", "obsidian", "sword", "pickaxe", "totem", "pearl", "cobweb", "gap", "darmor", "narmor", "iarmor", "axe", "mace", "hotbar", "inventory", "fire"};
	public static final String[] CN = {"End crystal", "Respawn anchor", "Stone", "Deepslate", "Snow", "Glowstone", "Obsidian", "Sword", "Pickaxe", "Totem", "Ender pearl", "Cobweb", "Golden apple", "Diamond armor", "Netherite armor", "Iron armor", "Axe", "Mace", "Hotbar", "Inventory", "Fire"};
	public static final String[] CL = {"purple", "blue", "pink", "green", "red", "ice", "black", "white"};
	public static final String[] FI = {"low", "ultralow", "none"};

	public static String[] opts(int c) {
		String[] s = c == CI.length - 1 ? FI : CL;
		String[] o = new String[s.length + 1];
		o[0] = "Off";
		for (int i = 0; i < s.length; i++) o[i + 1] = nm(s[i]);
		return o;
	}

	static String nm(String s) {
		return s.equals("ultralow") ? "Ultra low" : Character.toUpperCase(s.charAt(0)) + s.substring(1);
	}

	static String[] ids(int c) {
		String[] s = c == CI.length - 1 ? FI : CL;
		String[] o = new String[s.length];
		for (int i = 0; i < s.length; i++) o[i] = CI[c] + "_" + s[i];
		return o;
	}

	public static void reg() {
		var mc = FabricLoader.getInstance().getModContainer("azure").get();
		ResourceLoader.registerBuiltinPack(Identifier.fromNamespaceAndPath("azure", "azure_inv"), mc, Component.literal("Azure inventory"), PackActivationType.NORMAL);
		ResourceLoader.registerBuiltinPack(Identifier.fromNamespaceAndPath("azure", "azure_ui"), mc, Component.literal("Azure interface theme"), PackActivationType.DEFAULT_ENABLED);
	}

	static String pid(PackRepository r, String n) {
		for (String s : r.getAvailableIds()) if (s.contains("azure") && s.endsWith("/" + n) || s.equals("azure:" + n)) return s;
		for (String s : r.getAvailableIds()) if (s.contains("azure") && s.endsWith(n)) return s;
		return null;
	}

	public static int get(int c) {
		PackRepository r = Minecraft.getInstance().getResourcePackRepository();
		String[] d = ids(c);
		for (int i = 0; i < d.length; i++) {
			String p = pid(r, d[i]);
			if (p != null && r.getSelectedIds().contains(p)) return i + 1;
		}
		return 0;
	}

	public static void put(int c, int v) {
		Minecraft m = Minecraft.getInstance();
		PackRepository r = m.getResourcePackRepository();
		r.reload();
		String[] d = ids(c);
		List<String> l = new ArrayList<>(r.getSelectedIds());
		for (String o : d) {
			String p = pid(r, o);
			if (p != null) l.remove(p);
		}
		if (v > 0) {
			String p = pid(r, d[v - 1]);
			if (p == null) return;
			l.add(p);
		}
		r.setSelected(l);
		m.options.updateResourcePacks(r);
	}

	public static void all(int v) {
		Minecraft m = Minecraft.getInstance();
		PackRepository r = m.getResourcePackRepository();
		r.reload();
		List<String> l = new ArrayList<>(r.getSelectedIds());
		for (int c = 0; c < CI.length; c++) for (String o : ids(c)) {
			String p = pid(r, o);
			if (p != null) l.remove(p);
		}
		if (v > 0) for (int c = 0; c < CI.length - 1; c++) {
			String p = pid(r, ids(c)[v - 1]);
			if (p != null) l.add(p);
		}
		r.setSelected(l);
		m.options.updateResourcePacks(r);
	}

	public static void pk(String n, boolean on) {
		Minecraft m = Minecraft.getInstance();
		PackRepository r = m.getResourcePackRepository();
		r.reload();
		String p = pid(r, n);
		if (p == null) return;
		List<String> l = new ArrayList<>(r.getSelectedIds());
		l.remove(p);
		if (on) l.add(p);
		r.setSelected(l);
		m.options.updateResourcePacks(r);
	}

	public static void sync() {
		Minecraft m = Minecraft.getInstance();
		PackRepository r = m.getResourcePackRepository();
		String p = pid(r, "azure_inv");
		if (p == null) return;
		boolean sel = r.getSelectedIds().contains(p);
		if (sel != Mod.live("cinv")) pk("azure_inv", Mod.live("cinv"));
	}
}
