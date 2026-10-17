package dev.wscl;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.Base64;
import net.minecraft.client.Minecraft;

public class Prf {
	public static String out() {
		JsonObject o = Cfg.G.toJsonTree(Cfg.c).getAsJsonObject();
		JsonArray t = new JsonArray();
		for (int i = 0; i < Tx.CI.length; i++) t.add(Tx.get(i));
		o.add("tx", t);
		return "WC1" + Base64.getUrlEncoder().withoutPadding().encodeToString(o.toString().getBytes());
	}

	public static boolean in(String s) {
		try {
			s = s.trim();
			if (!s.startsWith("WC1")) return false;
			JsonObject o = Cfg.G.fromJson(new String(Base64.getUrlDecoder().decode(s.substring(3))), JsonObject.class);
			JsonArray t = o.has("tx") ? o.getAsJsonArray("tx") : null;
			o.remove("tx");
			Cfg.c = Cfg.G.fromJson(o, Cfg.class);
			Cfg.sv();
			if (t != null) for (int i = 0; i < Tx.CI.length && i < t.size(); i++) Tx.put(i, t.get(i).getAsInt());
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static void copy() {
		Minecraft.getInstance().keyboardHandler.setClipboard(out());
	}

	public static void paste() {
		in(Minecraft.getInstance().keyboardHandler.getClipboard());
	}
}
