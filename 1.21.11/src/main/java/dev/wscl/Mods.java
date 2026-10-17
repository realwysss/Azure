package dev.wscl;

import dev.wscl.Rw.*;

public class Mods {
	static boolean in;
	public static final boolean BASE = true;

	static Mod cf(String id, String nm, String ds, int ct, String ic, java.util.function.BooleanSupplier g, java.util.function.Consumer<Boolean> s) {
		return Mod.add(id, nm, ds, ct, ic, g, s);
	}

	static Mod w(String id, String nm, String ds, int ct, String ic, java.util.function.BooleanSupplier g, java.util.function.Consumer<Boolean> st, String cls) {
		Mod m = Mod.add(id, nm, ds, ct, ic, g, st).wy();
		if (cls != null) m.a(new Rw.Bt("Settings", "Open", () -> Gui.go(cls)).tip("Opens the settings of this mod."));
		return m;
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
		Mod.own("fps", "FPS", "Your frames per second", 1, "gauge")
				.ch("Style", "st", 0, "Pill", "Text")
				.tg("Color by speed", "col", true)
				.sl("Scale", "sc", 100, 50, 250, "%");
		Mod.own("ping", "Ping", "Your latency to the server", 1, "wifi")
				.ch("Style", "st", 0, "Pill", "Text")
				.sl("Scale", "sc", 100, 50, 250, "%");
		Mod.own("cps", "CPS", "Clicks per second, left and right", 1, "hand")
				.ch("Style", "st", 0, "Pill", "Text")
				.sl("Scale", "sc", 100, 50, 250, "%");
		Mod.own("xyz", "Coordinates", "Your position in the world", 1, "pin")
				.ch("Style", "st", 0, "Pill", "Text")
				.sl("Scale", "sc", 100, 50, 250, "%");
		Mod.own("clock", "Clock", "The time of your pc", 1, "clock")
				.ch("Style", "st", 0, "Pill", "Text")
				.sl("Scale", "sc", 100, 50, 250, "%");
		Mod.own("keys", "Keystrokes", "Shows the keys you press", 1, "keys")
				.ch("Layout", "lay", 2, "WASD", "WASD and mouse", "WASD, mouse, space", "Full keyboard")
				.sl("Key size", "kz", 18, 12, 26, "px")
				.tg("Show clicks", "cp", true)
				.sl("Scale", "sc", 100, 50, 250, "%");
		Mod.own("armor", "Armor HUD", "Your armor and its durability", 1, "shield")
				.ch("Layout", "lay", 0, "Vertical", "Horizontal")
				.ch("Durability", "dur", 0, "Number and bar", "Percent", "Bar", "Hidden")
				.tg("Show held item", "held", false)
				.tg("Show offhand", "off", false)
				.sl("Scale", "sc", 100, 50, 250, "%");
		Mod.own("totem", "Totem Counter", "How many totems you carry", 1, "star")
				.ch("Style", "st", 0, "Icon and count", "Count", "Text")
				.tg("Count the offhand", "off", true)
				.tg("Flash at zero", "warn", true)
				.sl("Scale", "sc", 100, 50, 250, "%");
		Mod.own("invhud", "Inventory HUD", "Your inventory as an overlay you can move", 1, "box")
				.tg("Show hotbar row", "hb", false)
				.tg("Panel", "bgp", true)
				.tg("Slot backgrounds", "sl", true)
				.tg("Item counts", "cnt", true)
				.sl("Scale", "sc", 80, 40, 250, "%");
		Mod.own("pots", "Potion HUD", "Every effect with its time left", 1, "flask")
				.tg("Effect icons", "ico", true)
				.tg("Time bars", "bar", true)
				.sl("Scale", "sc", 100, 50, 250, "%");
		Mod.own("spotify", "Spotify", "The song you play, with cover and progress", 1, "music")
				.tg("Album cover", "cov", true)
				.tg("Progress bar", "bar", true)
				.tg("Sound bars", "eq", true)
				.sl("Scale", "sc", 100, 50, 250, "%");
		Mod.own("pops", "Totem Pops", "Totem pops per player", 1, "ghost");
		Mod.own("mark", "Watermark", "Small Azure badge", 1, "star");
		Mod.own("food", "Food Info", "Food and saturation of what you hold", 1, "heart");
		Mod.own("shield", "Shield Status", "Shield durability and state", 1, "shield");
		Mod.own("namer", "Server Namer", "Names servers from their address when you leave the name empty", 3, "tag");
		Mod.own("tuffbright", "Tuff Bright", "Brightens the world so caves and nights stay readable", 2, "sun").wy()
				.sl("Strength", "pct", 100, 0, 100, "%");
		w("hitglow", "Hit Glow", "Glow around the players you hit", 0, "bolt", () -> dev.htgl.Cfg.c.on, v -> {
			dev.htgl.Cfg.c.on = v;
			dev.htgl.Cfg.sv();
		}, "dev.htgl.ui.Panel");
		w("hitcolor", "Hit Color", "Changes the color of the red hit flash", 0, "drop", () -> dev.htcl.Cfg.c.on, v -> {
			dev.htcl.Cfg.c.on = v;
			dev.htcl.Cfg.sv();
		}, "dev.htcl.ui.Panel");
		w("crystalglow", "Crystal Glow", "Glow on end crystals", 0, "gem", () -> dev.crgl.Cfg.c.on, v -> {
			dev.crgl.Cfg.c.on = v;
			dev.crgl.Cfg.sv();
		}, "dev.crgl.ui.Panel");
		w("anchorglow", "Anchor Glow", "Glow on respawn anchors", 0, "gem", () -> dev.angl.Cfg.c.on, v -> {
			dev.angl.Cfg.c.on = v;
			dev.angl.Cfg.sv();
		}, "dev.angl.ui.Panel");
		w("totemglow", "Totem Glow", "Glow on totem particles", 0, "star", () -> dev.tpgl.Cfg.c.on, v -> {
			dev.tpgl.Cfg.c.on = v;
			dev.tpgl.Cfg.sv();
		}, "dev.tpgl.ui.Panel");
		w("xpglow", "XP Glow", "Glow on experience orbs", 2, "star", () -> dev.xpgl.Cfg.c.on, v -> {
			dev.xpgl.Cfg.c.on = v;
			dev.xpgl.Cfg.sv();
		}, "dev.xpgl.ui.Panel");
		w("fireglow", "Fire Glow", "Recolors and lowers the fire", 2, "flame", () -> hits.fire.Config.get().enabled, v -> {
			hits.fire.Config.get().enabled = v;
			hits.fire.Config.save();
		}, "hits.fire.ui.Panel");
		w("klfx", "Kill Effects", "Effects and sounds when you kill", 0, "skull", () -> dev.klfx.Cfg.c.on, v -> {
			dev.klfx.Cfg.c.on = v;
			dev.klfx.Cfg.sv();
		}, "dev.klfx.Gui");
		Mod.own("cropt", "Crystal Optimizer", "Faster end crystal hits", 0, "gem").wy();
		w("frameboost", "Frame Boost", "Lower cpu use for higher fps", 3, "bolt", () -> dev.frbs.Cfg.c.noyld || dev.frbs.Cfg.c.rthr, v -> {
			dev.frbs.Cfg.c.noyld = v;
			dev.frbs.Cfg.c.rthr = v;
			dev.frbs.Cfg.c.prio = v ? 1 : 0;
			dev.frbs.Cfg.save();
		}, null);
		w("frametrim", "Frame Trim", "Skips drawing what you cannot see", 3, "layers", () -> dev.frtr.Cfg.c.on, v -> {
			dev.frtr.Cfg.c.on = v;
			dev.frtr.Cfg.sv();
		}, "dev.frtr.ui.Panel");
		w("blur", "Blur Reborn", "Motion blur", 2, "eye", () -> dev.blrb.Cfg.c.on, v -> {
			dev.blrb.Cfg.c.on = v;
			dev.blrb.Cfg.sv();
		}, "dev.blrb.Gui");
		w("ogfog", "OG Fog", "The old fog look", 2, "cloud", () -> dev.ogfg.Cfg.c.on, v -> {
			dev.ogfg.Cfg.c.on = v;
			dev.ogfg.Cfg.sv();
		}, "dev.ogfg.Gui");
		w("tuffglint", "Tuff Glint", "Custom enchant glint", 2, "sparkle", () -> dev.tfgl.Cfg.c.on, v -> {
			dev.tfgl.Cfg.c.on = v;
			dev.tfgl.Cfg.sv();
		}, "dev.tfgl.Gui");
		w("itemsop", "Items Opacity", "Fades dropped items", 2, "eye", () -> dev.itop.Cfg.c.on, v -> {
			dev.itop.Cfg.c.on = v;
			dev.itop.Cfg.sv();
		}, "dev.itop.ui.Gui");
		w("oldpots", "Old Potions", "Old potion colors and glint", 2, "flask", () -> dev.oldp.Cfg.c.col || dev.oldp.Cfg.c.gl, v -> {
			dev.oldp.Cfg.c.col = v;
			dev.oldp.Cfg.c.gl = v;
			dev.oldp.Cfg.sv();
		}, "dev.oldp.Gui");
		w("nobounce", "No Bounce", "Removes the hurt bounce of the screen", 3, "shield", () -> dev.nobn.Cfg.c.nb || dev.nobn.Cfg.c.fd, v -> {
			dev.nobn.Cfg.c.nb = v;
			dev.nobn.Cfg.c.fd = v;
			dev.nobn.Cfg.sv();
		}, "dev.nobn.Gui");
		w("noeb", "No Eat Bounce", "Removes the bounce when eating", 3, "shield", () -> dev.noeb.Cfg.c.on, v -> {
			dev.noeb.Cfg.c.on = v;
			dev.noeb.Cfg.sv();
		}, "dev.noeb.Gui");
		Mod.own("nsfov", "No Speed FOV", "Speed and slowness do not change your fov", 3, "zoom").wy();
		Mod.own("nohotbarname", "No Hotbar Name", "Hides the item name above the hotbar", 3, "eye").wy();
		Mod.own("packblocker", "Pack Blocker", "Blocks server resource packs", 3, "shield").wy();
		Mod.own("wcrys", "Crystal Optimizer (Ways)", "Hits crystals along your look ray and places them faster", 0, "gem").wy()
				.sl("Min delay", "min", 1, 0, 10, "t")
				.sl("Max delay", "max", 2, 0, 10, "t");
		Mod.own("wanch", "Anchor Optimizer (Ways)", "Places anchors through entities and faster", 0, "gem").wy()
				.sl("Min delay", "min", 1, 0, 10, "t")
				.sl("Max delay", "max", 2, 0, 10, "t");
		Mod.own("nopd", "No Place Delay", "Cuts the delay between block placements", 3, "bolt").wy()
				.sl("Min delay", "min", 1, 0, 10, "t")
				.sl("Max delay", "max", 2, 0, 10, "t");
		Mod.own("nosd", "No Swap Delay", "Cuts the delay after switching hotbar slots", 3, "bolt").wy()
				.sl("Min delay", "min", 1, 0, 10, "t")
				.sl("Max delay", "max", 2, 0, 10, "t");
		Mod.own("nstp", "No Self Totem Particles", "Hides your own totem pop particles", 2, "eye").wy();
		Mod.own("noshadow", "No Entity Shadows", "Removes the shadow under every entity", 2, "eye").wy();
		Mod.add("lowend", "Low End Mode", "Lowers distances, particles and effects for weak pcs", 3, "bolt", () -> Cfg.on("lowend"), v -> {
			if (v && Cfg.on("ultralow")) {
				Cfg.si("ultralow.on", 0);
				Low.apply(false, 0);
			}
			Cfg.si("lowend.on", v ? 1 : 0);
			Low.apply(v, 0);
		}).wy();
		Mod.add("ultralow", "Ultra Low Mode", "Same as low end mode but as small as it gets", 3, "bolt", () -> Cfg.on("ultralow"), v -> {
			if (v && Cfg.on("lowend")) {
				Cfg.si("lowend.on", 0);
				Low.apply(false, 0);
			}
			Cfg.si("ultralow.on", v ? 1 : 0);
			Low.apply(v, 1);
		}).wy();
		Mod.own("sky", "Custom Sky", "A night sky with aurora, stars and falling stars in your color", 2, "sparkle")
				.ch("Color", "col", 0, "Follow accent", "Custom")
				.cl("Custom color", "cc", 0x8B5CF6)
				.tg("Aurora", "au", true)
				.tg("Moving aurora", "am", true)
				.sl("Aurora strength", "as", 60, 10, 100, "%")
				.tg("Stars", "st", true)
				.sl("Star amount", "sa", 3, 1, 3, "")
				.tg("Falling stars", "fs", true)
				.sl("Falling star gap", "ff", 12, 3, 60, "s")
				.sl("Sky brightness", "bg", 22, 5, 60, "%");
		for (Mod hm : Mod.ALL) {
			if (hm.ct == 1) hm.a(new Rw.Bt("Edit HUD", "Open", () -> {
				var mc = net.minecraft.client.Minecraft.getInstance();
				mc.setScreen(new HudEd(mc.screen));
			}).tip("Drag every hud element where you want it."));
		}
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
