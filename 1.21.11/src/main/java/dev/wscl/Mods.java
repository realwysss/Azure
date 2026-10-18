package dev.wscl;

public class Mods {
	static boolean in;

	static Mod w(String id, String nm, String ds, int ct, String ic, java.util.function.BooleanSupplier g, java.util.function.Consumer<Boolean> st, String cls) {
		Mod m = Mod.add(id, nm, ds, ct, ic, g, st).wy();
		if (cls != null) m.a(new Rw.Bt("Settings", "Open", () -> Gui.go(cls)).tip("Opens the settings of this mod."));
		return m;
	}

	static Mod st(String id, String nm, String ds, String ic) {
		return Mod.own(id, nm, ds, 1, ic)
				.ch("Style", "st", 0, "Pill", "Text")
				.sl("Scale", "sc", 100, 50, 250, "%");
	}

	public static void init() {
		if (in) return;
		in = true;

		st("fps", "FPS", "Frames per second", "gauge").tg("Color by speed", "col", true);
		st("ping", "Ping", "Latency to the server", "wifi");
		st("cps", "CPS", "Clicks per second", "pointer");
		st("xyz", "Coordinates", "Where you are", "pin");
		st("clock", "Clock", "Your local time", "clock");
		Mod.own("keys", "Keystrokes", "The keys you press", 1, "keys")
				.ch("Layout", "lay", 2, "WASD", "WASD and mouse", "WASD, mouse, space", "Full keyboard")
				.sl("Key size", "kz", 18, 12, 26, "px")
				.tg("Show clicks", "cp", true)
				.sl("Scale", "sc", 100, 50, 250, "%");
		Mod.own("armor", "Armor HUD", "Armor and its durability", 1, "chest")
				.ch("Layout", "lay", 0, "Vertical", "Horizontal")
				.ch("Durability", "dur", 0, "Number and bar", "Percent", "Bar", "Hidden")
				.tg("Show held item", "held", false)
				.tg("Show offhand", "off", false)
				.sl("Scale", "sc", 100, 50, 250, "%");
		Mod.own("totem", "Totem Counter", "Totems in your inventory", 1, "totem")
				.ch("Style", "st", 0, "Icon and count", "Count", "Text")
				.tg("Count the offhand", "off", true)
				.tg("Flash at zero", "warn", true)
				.sl("Scale", "sc", 100, 50, 250, "%");
		Mod.own("invhud", "Inventory HUD", "Your inventory on screen", 1, "grid")
				.tg("Show hotbar row", "hb", false)
				.tg("Panel", "bgp", true)
				.tg("Slot backgrounds", "sl", true)
				.tg("Item counts", "cnt", true)
				.sl("Scale", "sc", 80, 40, 250, "%");
		Mod.own("pots", "Potion HUD", "Effects with time left", 1, "flask")
				.tg("Effect icons", "ico", true)
				.tg("Time bars", "bar", true)
				.sl("Scale", "sc", 100, 50, 250, "%");
		Mod.own("spotify", "Spotify", "Song, cover and progress", 1, "note")
				.tg("Album cover", "cov", true)
				.tg("Progress bar", "bar", true)
				.tg("Sound bars", "eq", true)
				.sl("Scale", "sc", 100, 50, 250, "%");
		st("reach", "Reach Display", "Distance of your last hit", "crosshair");
		st("combo", "Combo Display", "Hits in a row without being hit", "bolt");
		Mod.own("pops", "Totem Pops", "Totem pops per player", 1, "skull");
		Mod.own("mark", "Watermark", "A small Azure badge", 1, "badge");
		Mod.own("food", "Food Info", "Food and saturation held", 1, "apple");
		Mod.own("shield", "Shield Status", "Shield durability and state", 1, "shield");

		Mod.own("rpc", "Discord Presence", "Your status on Discord", 3, "discord")
				.a(new Rw.Ed("First line", 48, () -> Cfg.c.rd1, v -> Cfg.c.rd1 = v).tip("Write anything. {server} and {account} are replaced with where you play and who you are. Modrinth App shows its own status too: turn off Discord RPC in its Settings, Appearance to only show Azure."))
				.a(new Rw.Ed("Second line", 48, () -> Cfg.c.rd2, v -> Cfg.c.rd2 = v))
				.tg("Show server address", "ip", true)
				.tg("Show account name", "acc", true)
				.tg("Show time played", "time", true)
				.a(new Rw.St());
		Mod.own("sprint", "Toggle Sprint", "Keeps you sprinting while you walk", 3, "bolt");
		Mod.own("atext", "Auto Text", "Sends a message when you kill a player", 3, "lang")
				.ch("Message", "m", 0, "gg", "ez", "good fight", "nice one", "wp");
		Mod.own("cmsg", "Chat Keybinds", "Keys that send chat messages or commands", 3, "keys")
				.a(new Rw.Kb("Key 1", () -> Wc.mk[0]))
				.a(new Rw.Ed("Message 1", 80, () -> Cfg.c.m1, v -> Cfg.c.m1 = v))
				.a(new Rw.Kb("Key 2", () -> Wc.mk[1]))
				.a(new Rw.Ed("Message 2", 80, () -> Cfg.c.m2, v -> Cfg.c.m2 = v))
				.a(new Rw.Kb("Key 3", () -> Wc.mk[2]))
				.a(new Rw.Ed("Message 3", 80, () -> Cfg.c.m3, v -> Cfg.c.m3 = v))
				.a(new Rw.Kb("Key 4", () -> Wc.mk[3]))
				.a(new Rw.Ed("Message 4", 80, () -> Cfg.c.m4, v -> Cfg.c.m4 = v));
		Mod.own("snippet", "Snippet", "Screenshot buttons, gallery and viewer", 3, "tiles").wy()
				.a(new Rw.Bt("Gallery", "Open", () -> {
					var mc = net.minecraft.client.Minecraft.getInstance();
					mc.setScreen(new dev.snprb.gal(mc.screen));
				}))
				.a(new Rw.Kb("Gallery key", () -> dev.snprb.snprb.kgal))
				.a(new Rw.Kb("Screenshot key", () -> dev.snprb.snprb.kshot))
				.a(Rw.tg("Chat buttons", () -> dev.snprb.cfg.c.msg, v -> {
					dev.snprb.cfg.c.msg = v;
					dev.snprb.cfg.sv();
				}))
				.a(Rw.tg("Sharp thumbnails", () -> dev.snprb.cfg.c.dwn, v -> {
					dev.snprb.cfg.c.dwn = v;
					dev.snprb.cfg.sv();
				}));
		Mod.own("kbsearch", "Keybind Search", "A search bar in the controls screen", 3, "search").wy();
		Mod.own("hidesk", "Hide Skin In Inventory", "Hides your model in the inventory screen", 3, "ghost");
		Mod.own("namer", "Server Namer", "Names servers from the address", 3, "tag");
		Mod.own("freelook", "Freelook", "Look around without turning", 3, "eye")
				.a(new Rw.Kb("Freelook key", () -> Fl.key))
				.ch("Mode", "mode", 0, "Toggle", "Hold")
				.tg("Third person while active", "tp", true);
		Mod.own("nstp", "No Self Totem Particles", "Hides your own totem pop", 3, "totem").wy();
		Mod.own("nopd", "No Place Delay", "No wait between placements", 3, "hourglass").wy()
				.sl("Min delay", "min", 1, 0, 10, "t")
				.sl("Max delay", "max", 2, 0, 10, "t");
		Mod.own("nosd", "No Swap Delay", "No wait after switching slots", 3, "hourglass").wy()
				.sl("Min delay", "min", 1, 0, 10, "t")
				.sl("Max delay", "max", 2, 0, 10, "t");
		w("nobounce", "No Bounce", "Stops the hurt camera bounce", 3, "wave", () -> dev.nobn.Cfg.c.nb || dev.nobn.Cfg.c.fd, v -> {
			dev.nobn.Cfg.c.nb = v;
			dev.nobn.Cfg.c.fd = v;
			dev.nobn.Cfg.sv();
		}, "dev.nobn.Gui");
		w("noeb", "No Eat Bounce", "Stops the bounce when eating", 3, "wave", () -> dev.noeb.Cfg.c.on, v -> {
			dev.noeb.Cfg.c.on = v;
			dev.noeb.Cfg.sv();
		}, "dev.noeb.Gui");
		Mod.add("fpsboost", "FPS Boost", "Every fps trick in one place", 3, "rocket", () -> Cfg.on("fpsboost"), v -> {
			Cfg.si("fpsboost.on", v ? 1 : 0);
			Fps.tick(true);
		}).wy()
				.ch("Preset", "pr", 1, "Custom", "Balanced", "Aggressive", "Extreme")
				.sl("Render distance cap", "rd", 12, 0, 32, "")
				.sl("Simulation distance cap", "sd", 8, 0, 32, "")
				.ch("Particles", "pt", 1, "Keep", "Decreased", "Minimal")
				.tg("Clouds off", "cl", true)
				.tg("Smooth lighting off", "ao", false)
				.tg("Biome blend off", "bb", true)
				.sl("Entity distance", "ed", 100, 30, 100, "%")
				.tg("Entity shadows off", "sh", true)
				.tg("Mipmaps off", "mm", false)
				.tg("Skip hidden entities", "ft", true)
				.sl("Cull distance", "ftd", 100, 30, 150, "%")
				.tg("Skip far particles", "ftp", true)
				.tg("Skip far block entities", "ftb", true)
				.tg("No block break particles", "bk", false)
				.tg("Higher cpu priority", "fb", false)
				.tg("Skip thread yield", "fy", false);

		Mod.own("hitbox", "Hitboxes", "Shows entity hitboxes", 0, "layers");
		Mod.own("cropt", "Crystal Optimizer", "Instant crystal hits", 0, "gem").wy();
		Mod.own("wcrys", "Crystal Assist (Ways)", "Hits crystals along your aim", 0, "gem").wy()
				.sl("Min delay", "min", 1, 0, 10, "t")
				.sl("Max delay", "max", 2, 0, 10, "t");
		Mod.own("wanch", "Anchor Assist (Ways)", "Places anchors through entities", 0, "anchor").wy()
				.sl("Min delay", "min", 1, 0, 10, "t")
				.sl("Max delay", "max", 2, 0, 10, "t");
		w("totemglow", "Totem Particle Glow", "Glowing totem pop particles", 0, "totem", () -> dev.tpgl.Cfg.c.on, v -> {
			dev.tpgl.Cfg.c.on = v;
			dev.tpgl.Cfg.sv();
		}, "dev.tpgl.ui.Panel");
		w("anchorglow", "Anchor Glow", "Glow on respawn anchors", 0, "anchor", () -> dev.angl.Cfg.c.on, v -> {
			dev.angl.Cfg.c.on = v;
			dev.angl.Cfg.sv();
		}, "dev.angl.ui.Panel");
		w("hitglow", "Hit Glow", "Glow around players you hit", 0, "bolt", () -> dev.htgl.Cfg.c.on, v -> {
			dev.htgl.Cfg.c.on = v;
			dev.htgl.Cfg.sv();
		}, "dev.htgl.ui.Panel");
		w("hitcolor", "Hit Color", "Recolors the red hit flash", 0, "drop", () -> dev.htcl.Cfg.c.on, v -> {
			dev.htcl.Cfg.c.on = v;
			dev.htcl.Cfg.sv();
		}, "dev.htcl.ui.Panel");
		w("crystalglow", "Crystal Glow", "Glow on end crystals", 0, "gem", () -> dev.crgl.Cfg.c.on, v -> {
			dev.crgl.Cfg.c.on = v;
			dev.crgl.Cfg.sv();
		}, "dev.crgl.ui.Panel");
		w("xpglow", "XP Glow", "Glow on experience orbs", 2, "star", () -> dev.xpgl.Cfg.c.on, v -> {
			dev.xpgl.Cfg.c.on = v;
			dev.xpgl.Cfg.sv();
		}, "dev.xpgl.ui.Panel");
		w("klfx", "Kill Effects", "Effects and sounds on kills", 0, "skull", () -> dev.klfx.Cfg.c.on, v -> {
			dev.klfx.Cfg.c.on = v;
			dev.klfx.Cfg.sv();
		}, "dev.klfx.Gui");

		Mod.add("cinv", "Custom Inventory", "Glass inventory and hotbar", 2, "grid", () -> Cfg.on("cinv"), v -> {
			Cfg.si("cinv.on", v ? 1 : 0);
			Tx.pk("azure_inv", v);
		});
		Mod.own("tuffbright", "Tuff Bright", "See in the dark", 2, "sun").wy()
				.sl("Strength", "pct", 100, 0, 100, "%")
				.tg("Full bright", "fb", false);
		Mod.own("nohotbarname", "No Tooltips", "Hides the item name above the hotbar", 2, "eyeoff").wy();
		Mod.own("zoom", "Zoom", "Hold a key to zoom in", 2, "search")
				.a(new Rw.Kb("Zoom key", () -> Wc.zkey))
				.a(Rw.sl("Strength", () -> Cfg.c.zs, v -> Cfg.c.zs = v, 10, 95, "%"));
		Mod.own("cross", "Custom Crosshair", "Your own crosshair", 2, "crosshair")
				.a(new Rw.Ch("Style", new String[]{"Cross", "Dot", "Circle", "T"}, () -> Cfg.c.xs, v -> Cfg.c.xs = v))
				.a(new Rw.Cl("Color", () -> Cfg.c.xc, v -> Cfg.c.xc = v & 0xFFFFFF))
				.a(Rw.sl("Size", () -> Cfg.c.xz, v -> Cfg.c.xz = v, 1, 20, "px"))
				.a(Rw.sl("Thickness", () -> Cfg.c.xt, v -> Cfg.c.xt = v, 1, 6, "px"))
				.a(Rw.sl("Gap", () -> Cfg.c.xg, v -> Cfg.c.xg = v, 0, 12, "px"))
				.a(Rw.tg("Outline", () -> Cfg.c.xo, v -> Cfg.c.xo = v))
				.a(Rw.tg("Grows when you hit", () -> Cfg.c.xd, v -> Cfg.c.xd = v));
		Mod.own("weather", "Weather Changer", "Pick the weather you see", 2, "cloud")
				.ch("Weather", "w", 0, "Clear", "Rain", "Thunder");
		w("fireglow", "Fire Glow", "Recolor and lower the fire", 2, "flame", () -> hits.fire.Config.get().enabled, v -> {
			hits.fire.Config.get().enabled = v;
			hits.fire.Config.save();
		}, "hits.fire.ui.Panel");
		w("ogfog", "OG Fog", "The old fog look", 2, "cloud", () -> dev.ogfg.Cfg.c.on, v -> {
			dev.ogfg.Cfg.c.on = v;
			dev.ogfg.Cfg.sv();
		}, "dev.ogfg.Gui");
		w("blur", "Blur Reborn", "Motion blur", 2, "blur", () -> dev.blrb.Cfg.c.on, v -> {
			dev.blrb.Cfg.c.on = v;
			dev.blrb.Cfg.sv();
		}, "dev.blrb.Gui");
		w("oldpots", "Old Potions", "Old potion colors and glint", 2, "flask", () -> dev.oldp.Cfg.c.col || dev.oldp.Cfg.c.gl, v -> {
			dev.oldp.Cfg.c.col = v;
			dev.oldp.Cfg.c.gl = v;
			dev.oldp.Cfg.sv();
		}, "dev.oldp.Gui");
		w("itemsop", "Items Opacity", "Fade dropped items", 2, "ghost", () -> dev.itop.Cfg.c.on, v -> {
			dev.itop.Cfg.c.on = v;
			dev.itop.Cfg.sv();
		}, "dev.itop.ui.Gui");
		Mod.own("noshadow", "No Entity Shadows", "Removes every shadow", 2, "ghost").wy();
		Mod.own("time", "Time Changer", "Pick the time of day", 2, "moon")
				.ch("Preset", "pr", 2, "Custom", "Sunrise", "Day", "Noon", "Sunset", "Night", "Midnight")
				.sl("Custom time", "t", 6000, 0, 23999, "")
				.ch("Mode", "mode", 0, "Frozen", "Cycling")
				.sl("Cycle speed", "sp", 20, 1, 200, "x");
		Mod.own("sky", "Custom Sky", "Aurora and falling stars", 2, "sparkle")
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
			}).tip("Drag and group every hud element."));
		}
	}
}
