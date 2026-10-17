package dev.wscl;

import dev.wscl.Rw.*;

public class Mods {
	static boolean in;
	public static final boolean BASE = true;

	static Mod cf(String id, String nm, String ds, int ct, String ic, java.util.function.BooleanSupplier g, java.util.function.Consumer<Boolean> s) {
		return Mod.add(id, nm, ds, ct, ic, g, s);
	}

	public static void init() {
		if (in) return;
		in = true;
		Mod.own("rpc", "Discord Presence", "Shows what you play on your Discord profile", 3, "chat")
				.a(new Rw.Ed("First line", 48, () -> Cfg.c.rd1, v -> Cfg.c.rd1 = v).tip("Write anything. {server} and {account} are replaced with where you play and who you are."))
				.a(new Rw.Ed("Second line", 48, () -> Cfg.c.rd2, v -> Cfg.c.rd2 = v))
				.tg("Show server address", "ip", true)
				.tg("Show account name", "acc", true)
				.tg("Show time played", "time", true)
				.a(new St());
		Mod.add("cinv", "Custom Inventory", "Glass inventory panels and hotbar", 2, "layers", () -> Cfg.on("cinv"), v -> {
			Cfg.si("cinv.on", v ? 1 : 0);
			Tx.pk("azure_inv", v);
		});
		Mod.own("namer", "Server Namer", "Names servers from their address when you leave the name empty", 3, "tag");
		Mod.own("tuffbright", "Tuff Bright", "Brightens the world so caves and nights stay readable", 2, "sun")
				.sl("Strength", "pct", 100, 0, 100, "%");
		if (BASE) return;
		cf("pvp", "Pvp Items", "Totems, crystals, obsidian, apples and pearls you carry", 0, "box", () -> Cfg.c.pvp, v -> Cfg.c.pvp = v);
		Mod.own("pops", "Totem Pops", "Totem pops per player", 0, "ghost");
		cf("shield", "Shield Tweaks", "Shield state and durability", 0, "shield", () -> Cfg.c.shd, v -> Cfg.c.shd = v);
		cf("cross", "Crosshair", "Draw your own crosshair", 0, "cross", () -> Cfg.c.xh, v -> Cfg.c.xh = v)
				.a(new Pv())
				.a(new Ch("Style", new String[]{"Cross", "Dot", "Circle", "T"}, () -> Cfg.c.xs, v -> Cfg.c.xs = v))
				.a(Rw.sl("Size", () -> Cfg.c.xz, v -> Cfg.c.xz = v, 1, 30, ""))
				.a(Rw.sl("Thickness", () -> Cfg.c.xt, v -> Cfg.c.xt = v, 1, 8, ""))
				.a(Rw.sl("Gap", () -> Cfg.c.xg, v -> Cfg.c.xg = v, 0, 20, ""))
				.a(new Cl("Color", () -> Cfg.c.xc, v -> Cfg.c.xc = v & 0xFFFFFF))
				.a(Rw.tg("Outline", () -> Cfg.c.xo, v -> Cfg.c.xo = v))
				.a(Rw.tg("Dynamic gap", () -> Cfg.c.xd, v -> Cfg.c.xd = v).tip("The crosshair opens up for a moment when you hit something."));
		cf("zoom", "Zoom", "Hold a key to zoom", 0, "zoom", () -> Cfg.c.zm, v -> Cfg.c.zm = v)
				.a(Rw.sl("Strength", () -> Cfg.c.zs, v -> Cfg.c.zs = v, 10, 95, "%"));

		cf("fps", "FPS", "Framerate readout", 1, "gauge", () -> Cfg.c.fps, v -> Cfg.c.fps = v);
		cf("ping", "Ping", "Server latency", 1, "wifi", () -> Cfg.c.png, v -> Cfg.c.png = v);
		cf("cps", "CPS", "Clicks per second", 1, "bolt", () -> Cfg.c.cps, v -> Cfg.c.cps = v);
		cf("xyz", "Coordinates", "XYZ position and direction", 1, "pin", () -> Cfg.c.xyz, v -> Cfg.c.xyz = v);
		cf("clock", "Clock", "Your local time", 1, "clock", () -> Cfg.c.clk, v -> Cfg.c.clk = v);
		cf("keys", "Keystrokes", "Key and click display", 1, "keys", () -> Cfg.c.key, v -> Cfg.c.key = v);
		cf("armor", "Armor HUD", "Armor durability display", 1, "shield", () -> Cfg.c.arm, v -> Cfg.c.arm = v);
		Mod.own("move", "Movement Status", "Sprint, sneak and speed readout", 1, "chev");
		Mod.own("reach", "Reach Display", "Distance of your last hit", 1, "sword");
		Mod.own("combo", "Combo Counter", "Hits in a row without being hit", 1, "star");
		Mod.own("mem", "Memory", "Ram used by the game", 1, "bars");
		Mod.own("pots", "Potion HUD", "Active effect timers", 1, "flask");
		cf("food", "Food Info", "Food and saturation of what you hold", 1, "heart", () -> Cfg.c.fod, v -> Cfg.c.fod = v);
		Mod.own("mark", "Watermark", "Small Azure badge", 1, "star");

		Mod.add("hitbox", "Hitboxes", "Boxes on players, crystals and projectiles", 2, "eye", () -> net.minecraft.client.Minecraft.getInstance().debugEntries.getStatus(net.minecraft.client.gui.components.debug.DebugScreenEntries.ENTITY_HITBOXES) == net.minecraft.client.gui.components.debug.DebugScreenEntryStatus.ALWAYS_ON, v -> net.minecraft.client.Minecraft.getInstance().debugEntries.setStatus(net.minecraft.client.gui.components.debug.DebugScreenEntries.ENTITY_HITBOXES, v ? net.minecraft.client.gui.components.debug.DebugScreenEntryStatus.ALWAYS_ON : net.minecraft.client.gui.components.debug.DebugScreenEntryStatus.NEVER));
		Mod.own("fullbright", "Fullbright", "Max brightness everywhere", 2, "sun");
		Mod.own("time", "Time Changer", "Override the world time", 2, "clock")
				.sl("Time of day", "t", 6000, 0, 24000, "");
		Mod.own("weather", "Weather Changer", "Override the world weather", 2, "cloud")
				.ch("Weather", "w", 0, "Clear", "Rain", "Thunder");
		cf("shadow", "No Entity Shadows", "Removes the shadow under entities", 2, "ghost", () -> Cfg.c.nsh, v -> Cfg.c.nsh = v);

		cf("sprint", "Toggle Sprint", "Hold forward once and keep sprinting", 3, "chev", () -> Cfg.c.tsp, v -> Cfg.c.tsp = v);
		Mod.own("atext", "Auto Text", "Sends a chat message when you kill a player", 3, "chat")
				.ch("Message", "m", 0, "gg", "ez", "good fight", "nice one", "wp");

		String[][] md = Gui.MD;
		String[] ics = {"flame", "palette", "star", "gem", "cube", "flame", "star", "bolt", "layers", "eye", "cloud", "gem", "sun", "eye", "flask", "chev", "chev", "skull"};
		for (int i = 0; i < md.length; i++) {
			String cls = md[i][1];
			Mod.ext("w" + i, md[i][0], "Opens the settings of this wysss mod", ics[i % ics.length], () -> Gui.go(cls));
		}
	}
}
