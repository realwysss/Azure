package dev.wscl;

public class Mods {
	static boolean in;

	static Mod w(String id, String nm, String ds, int ct, String ic, java.util.function.BooleanSupplier g, java.util.function.Consumer<Boolean> st, String cls) {
		Mod m = Mod.add(id, nm, ds, ct, ic, g, st).wy();
		if (cls != null) m.a(new Rw.Bt("Settings", "Open", () -> Gui.go(cls)).tip("Opens the settings of this mod."));
		return m;
	}

	static boolean tgOn() {
		try {
			Object c = Class.forName("hits.glow.Config").getMethod("get").invoke(null);
			return c.getClass().getField("enabled").getBoolean(c);
		} catch (Throwable e) {
			return false;
		}
	}

	static void tgSet(boolean v) {
		try {
			Class<?> k = Class.forName("hits.glow.Config");
			Object c = k.getMethod("get").invoke(null);
			k.getField("enabled").setBoolean(c, v);
			k.getMethod("save").invoke(null);
		} catch (Throwable e) {
		}
	}

	static Mod st(String id, String nm, String ds, String ic) {
		return Mod.own(id, nm, ds, 1, ic)
				.ch("Style", "st", 0, "Pill", "Text")
				.ch("Label", "lb", 0, "Left", "Right", "Hidden")
				.sl("Scale", "sc", 100, 50, 250, "%");
	}

	public static void init() {
		if (in) return;
		in = true;
		net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.END_CLIENT_TICK.register(Ns::tick);

		st("fps", "FPS", "Frames per second", "m_fps").tg("Color by speed", "col", true);
		st("ping", "Ping", "Latency to the server", "m_ping");
		st("cps", "CPS", "Clicks per second", "m_cps");
		st("xyz", "Coordinates", "Where you are", "m_xyz");
		st("clock", "Clock", "Your local time", "m_clock");
		Mod.own("keys", "Keystrokes", "The keys you press", 1, "m_keys")
				.ch("Layout", "lay", 2, "WASD", "WASD and mouse", "WASD, mouse, space", "Full keyboard")
				.sl("Key size", "kz", 18, 12, 26, "px")
				.tg("Show clicks", "cp", true)
				.tg("Number keys", "num", false)
				.sl("Scale", "sc", 100, 50, 250, "%");
		Mod.own("armor", "Armor HUD", "Armor and its durability", 1, "m_armor")
				.ch("Layout", "lay", 0, "Vertical", "Horizontal")
				.ch("Durability", "dur", 0, "Number and bar", "Percent", "Bar", "Hidden")
				.ch("Text side", "side", 0, "Right", "Left")
				.sl("Text gap", "gap", 6, 0, 30, "px")
				.tg("Show held item", "held", false)
				.tg("Show offhand", "off", false)
				.sl("Scale", "sc", 100, 50, 250, "%");
		Mod.own("totem", "Totem Counter", "Totems in your inventory", 1, "m_totem")
				.ch("Style", "st", 0, "Icon and count", "Count", "Text")
				.tg("Count the offhand", "off", true)
				.tg("Flash at zero", "warn", true)
				.sl("Scale", "sc", 100, 50, 250, "%");
		Mod.own("invhud", "Inventory HUD", "Your inventory", 1, "m_inv")
				.tg("Show hotbar row", "hb", false)
				.tg("Panel", "bgp", true)
				.tg("Slot backgrounds", "sl", true)
				.tg("Item counts", "cnt", true)
				.sl("Scale", "sc", 80, 40, 250, "%");
		Mod.own("pots", "Potion HUD", "Effects and time left", 1, "m_pots")
				.tg("Effect icons", "ico", true)
				.tg("Time bars", "bar", true)
				.sl("Scale", "sc", 100, 50, 250, "%");
		Mod.own("spotify", "Spotify", "Song, cover and progress", 1, "spotify")
				.tg("Album cover", "cov", true)
				.tg("Progress bar", "bar", true)
				.tg("Sound bars", "eq", true)
				.tg("Now playing text", "np", true)
				.sl("Scale", "sc", 100, 50, 250, "%");
		st("reach", "Reach Display", "Distance of your last hit", "m_reach");
		st("combo", "Combo Display", "Hits in a row without being hit", "m_combo");
		Mod.own("fig", "3D Player", "Your player as a 3D figure", 1, "figure")
				.sl("Angle", "ang", 0, -180, 180, "deg")
				.sl("Size", "sz", 100, 40, 200, "px")
				.tg("Spin", "spin", false)
				.tg("Panel", "pn", true)
				.tg("Drop shadow", "dsh", false)
				.sl("Shadow strength", "dss", 50, 10, 100, "%")
				.sl("Scale", "sc", 100, 50, 250, "%");
		Mod.own("pops", "Totem Pops", "Totem pops per player", 1, "m_pops");
		Mod.own("mark", "Watermark", "The Azure logo", 1, "m_mark")
				.ch("Style", "st", 0, "Badge", "Text", "Logo")
				.tg("Show version", "vr", true)
				.sl("Scale", "sc", 100, 50, 250, "%");
		Mod.own("sb", "Scoreboard", "Restyle the sidebar", 1, "m_sb")
				.tg("Hide scoreboard", "hide", false)
				.tg("Remove background", "nobg", false)
				.sl("Background opacity", "bgo", 40, 0, 100, "%")
				.tg("Black and white", "bw", false)
				.tg("Show numbers", "nums", true)
				.tg("Text shadow", "sh", false)
				.sl("Scale", "sc", 100, 50, 200, "%")
				.sl("Move left or right", "ox", 0, -200, 200, "px")
				.sl("Move up or down", "oy", 0, -200, 200, "px");
		Mod.own("food", "Food Info", "Food and saturation", 1, "m_food");
		Mod.own("shield", "Shield Status", "Red on cooldown, green when ready", 1, "shieldst");
		Mod.own("appleskin", "AppleSkin", "Shows what held food restores on the hunger bar", 3, "appleskin");

		Mod rpc = Mod.own("rpc", "Discord Presence", "Your status on Discord", 3, "discord");
		if (Zx.q()) rpc.a(new Rw.Ed("First line", 48, () -> Cfg.c.rd1, v -> Cfg.c.rd1 = v).tip("Write anything. {server} and {account} are replaced with where you play and who you are."))
				.a(new Rw.Ed("Second line", 48, () -> Cfg.c.rd2, v -> Cfg.c.rd2 = v))
				.a(new Rw.Bt("Reset lines", "Reset", () -> {
					Cfg.c.rd1 = "Playing {server}";
					Cfg.c.rd2 = "{account}";
				}).tip("Puts both lines back to the default."));
		rpc.tg("Show server address", "ip", true)
				.tg("Show account name", "acc", true)
				.tg("Show time played", "time", true)
				.a(new Rw.St());
		Mod.own("sprint", "Toggle Sprint", "Keeps you sprinting, even after you die", 0, "m_sprint")
				.tg("Always sprint", "al", true);
		Mod.own("atext", "Auto Text", "Message on a kill", 3, "m_atext")
				.ch("Message", "m", 0, "gg", "ez", "good fight", "nice one", "wp");
		Mod.own("cmsg", "Chat Keybinds", "Keys that send chat", 3, "m_cmsg")
				.a(new Rw.Kb("Key 1", () -> Wc.mk[0]))
				.a(new Rw.Ed("Message 1", 80, () -> Cfg.c.m1, v -> Cfg.c.m1 = v))
				.a(new Rw.Kb("Key 2", () -> Wc.mk[1]))
				.a(new Rw.Ed("Message 2", 80, () -> Cfg.c.m2, v -> Cfg.c.m2 = v))
				.a(new Rw.Kb("Key 3", () -> Wc.mk[2]))
				.a(new Rw.Ed("Message 3", 80, () -> Cfg.c.m3, v -> Cfg.c.m3 = v))
				.a(new Rw.Kb("Key 4", () -> Wc.mk[3]))
				.a(new Rw.Ed("Message 4", 80, () -> Cfg.c.m4, v -> Cfg.c.m4 = v));
		Mod.own("snippet", "Snippet", "Screenshots and gallery", 3, "m_snip").wy()
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
		Mod.own("skinchg", "Skin Changer", "Change skin and cape", 3, "shirt")
				.a(new Rw.Bt("Skin changer", "Open", () -> {
					var mc = net.minecraft.client.Minecraft.getInstance();
					mc.setScreen(new AzSkin(mc.screen));
				}).tip("Pick a PNG or copy a player skin, then apply it to your account."));
		Mod.own("kbsearch", "Keybind Search", "Search your keybinds", 3, "m_kbs").wy();
		Mod.own("hidesk", "Hide Skin In Inventory", "Hide model in inventory", 3, "m_hide");
		Mod.own("namer", "Server Namer", "Names servers", 3, "m_namer");
		Mod.own("freelook", "Freelook", "Look around without turning", 3, "m_look")
				.a(new Rw.Kb("Freelook key", () -> Fl.key))
				.ch("Mode", "mode", 0, "Toggle", "Hold")
				.tg("Third person while active", "tp", true);
		Mod.own("noprt", "Disable Particles", "Removes all particles", 3, "m_noprt")
				.tg("Keep totem pops", "tot", false)
				.tg("Keep critical hits", "crit", true);
		Mod.own("nofire", "No Fire Overlay", "Hides the fire on your screen when you burn", 3, "flame");
		Mod.own("seecob", "See Thru Cobwebs", "Cobwebs you can see through", 2, "m_itop")
				.sl("Opacity", "op", 35, 5, 95, "%");
		Mod.own("noanim", "Disable Animations", "Freezes animated textures", 3, "m_noanim")
				.tg("Water", "wat", true)
				.tg("Lava", "lav", true)
				.tg("Fire", "fir", true)
				.tg("Portals", "por", true)
				.tg("Everything else", "oth", true);
		Mod nsm = Mod.own("nosnd", "No Sounds", "Mute single sounds", 3, "nosnd");
		nsm.a(new Rw.Nt("Pick what to mute. Everything else stays."));
		nsm.tg("End crystal explosions", "crys", false);
		nsm.tg("Respawn anchor explosions", "anch", false);
		for (String[] g : Ns.G) nsm.tg(g[1], g[0], false);
		nsm.a(new Rw.Ed("Extra sounds to mute", 120, () -> Cfg.c.nsx, v -> Cfg.c.nsx = v).tip("Sound names separated by commas, for example entity.arrow.hit_player"));
		Mod.own("emoji", "Chat Emojis", "Type :heart: or <3 in chat", 3, "m_emoji").wy().a(new Rw.Nt(Em.names())).a(new Rw.Nt("Also :) :( B) and <3. Everyone sees a symbol, Azure users see a colored picture."));
		Mod.own("nstp", "No Self Totem Particles", "Hides your own totem pop", 3, "m_nstp").wy();
		Mod.own("nopd", "No Place Delay", "No wait between placements", 0, "m_nopd").wy()
				.sl("Min delay", "min", 1, 0, 10, "t")
				.sl("Max delay", "max", 2, 0, 10, "t");
		w("nobounce", "No Bounce", "No hurt camera bounce", 3, "m_nobounce", () -> dev.nobn.Cfg.c.nb || dev.nobn.Cfg.c.fd, v -> {
			dev.nobn.Cfg.c.nb = v;
			dev.nobn.Cfg.c.fd = v;
			dev.nobn.Cfg.sv();
		}, "dev.nobn.Gui");
		w("noeb", "No Eat Bounce", "Stops the bounce when eating", 3, "m_noeb", () -> dev.noeb.Cfg.c.on, v -> {
			dev.noeb.Cfg.c.on = v;
			dev.noeb.Cfg.sv();
		}, "dev.noeb.Gui");
		Mod.add("fpsboost", "FPS Boost", "Every fps trick", 3, "rocket", () -> Cfg.on("fpsboost"), v -> {
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

		w("totglow", "Totem Glow", "Glowing totem eyes", 4, "totem", Mods::tgOn, Mods::tgSet, "hits.glow.ui.Panel");
		w("totemglow", "Totem Particle Glow", "Glowing totem pop particles", 4, "m_totemglow", () -> dev.tpgl.Cfg.c.on, v -> {
			dev.tpgl.Cfg.c.on = v;
			dev.tpgl.Cfg.sv();
		}, "dev.tpgl.ui.Panel");
		w("anchorglow", "Anchor Glow", "Glow on respawn anchors", 4, "m_ancglow", () -> dev.angl.Cfg.c.on, v -> {
			dev.angl.Cfg.c.on = v;
			dev.angl.Cfg.sv();
		}, "dev.angl.ui.Panel");
		w("hitglow", "Hit Glow", "Glow on players you hit", 4, "m_hitglow", () -> dev.htgl.Cfg.c.on, v -> {
			dev.htgl.Cfg.c.on = v;
			dev.htgl.Cfg.sv();
		}, "dev.htgl.ui.Panel");
		w("hitcolor", "Hit Color", "Recolors the red hit flash", 4, "drop", () -> dev.htcl.Cfg.c.on, v -> {
			dev.htcl.Cfg.c.on = v;
			dev.htcl.Cfg.sv();
		}, "dev.htcl.ui.Panel");
		w("crystalglow", "Crystal Glow", "Glow on end crystals", 4, "m_cryglow", () -> dev.crgl.Cfg.c.on, v -> {
			dev.crgl.Cfg.c.on = v;
			dev.crgl.Cfg.sv();
		}, "dev.crgl.ui.Panel");
		w("xpglow", "XP Glow", "Glow on experience orbs", 4, "m_xp", () -> dev.xpgl.Cfg.c.on, v -> {
			dev.xpgl.Cfg.c.on = v;
			dev.xpgl.Cfg.sv();
		}, "dev.xpgl.ui.Panel");
		w("klfx", "Kill Effects", "Effects and sounds on kills", 4, "skull", () -> dev.klfx.Cfg.c.on, v -> {
			dev.klfx.Cfg.c.on = v;
			dev.klfx.Cfg.sv();
		}, "dev.klfx.Gui");

		Mod.add("cinv", "Custom Inventory", "Glass inventory and hotbar", 2, "m_cinv", () -> Cfg.on("cinv"), v -> {
			Cfg.si("cinv.on", v ? 1 : 0);
			Tx.pk("azure_inv", v);
		});
		Mod.own("tuffbright", "Tuff Bright", "See in the dark", 2, "sun").wy()
				.sl("Strength", "pct", 100, 0, 100, "%")
				.tg("Full bright", "fb", false);
		Mod.own("nohotbarname", "No Tooltips", "Hides the item name", 2, "m_notip").wy();
		if (Zx.q()) Mod.own("fakefps", "Fake FPS", "Shows the fps you pick everywhere", 2, "rocket")
				.a(new Rw.Rg("FPS range", () -> Cfg.gi("fakefps.lo", 240), v -> Cfg.si("fakefps.lo", v), () -> Cfg.gi("fakefps.hi", 300), v -> Cfg.si("fakefps.hi", v), 1, 1000, " fps").tip("The fps counter, F3, Sodium and every other mod show a number between these two."));
		Mod.own("zoom", "Zoom", "Hold a key to zoom in", 2, "m_zoom")
				.a(new Rw.Kb("Zoom key", () -> Wc.zkey))
				.ch("Mode", "mode", 0, "Smooth slide", "Instant")
				.a(Rw.sl("Strength", () -> Cfg.c.zs, v -> Cfg.c.zs = v, 10, 95, "%"));
		Mod.own("cross", "Custom Crosshair", "Your own crosshair", 2, "crosshair")
				.a(new Rw.Ch("Style", new String[]{"Cross", "Dot", "Circle", "T"}, () -> Cfg.c.xs, v -> Cfg.c.xs = v))
				.a(new Rw.Cl("Color", () -> Cfg.c.xc, v -> Cfg.c.xc = v & 0xFFFFFF))
				.a(Rw.sl("Size", () -> Cfg.c.xz, v -> Cfg.c.xz = v, 1, 20, "px"))
				.a(Rw.sl("Thickness", () -> Cfg.c.xt, v -> Cfg.c.xt = v, 1, 6, "px"))
				.a(Rw.sl("Gap", () -> Cfg.c.xg, v -> Cfg.c.xg = v, 0, 12, "px"))
				.a(Rw.tg("Outline", () -> Cfg.c.xo, v -> Cfg.c.xo = v))
				.a(Rw.tg("Grows when you hit", () -> Cfg.c.xd, v -> Cfg.c.xd = v));
		Mod.own("weather", "Weather Changer", "Pick the weather you see", 2, "m_weather")
				.ch("Weather", "w", 0, "Clear", "Rain", "Thunder");
		Mod.own("rain", "Real Rain", "Real rain that makes the ground wet and shiny", 2, "m_weather")
				.ch("When", "al", 0, "When it rains", "Always rain")
				.sl("Rain amount", "dn", 100, 20, 200, "%")
				.sl("Wind", "wn", 25, 0, 100, "%")
				.tg("Splashes", "sp", true)
				.a(new Rw.Nt("Wet ground"))
				.tg("Wet ground", "wg", true)
				.sl("Reflections", "rf", 90, 0, 100, "%")
				.sl("Puddles", "pd", 60, 0, 100, "%")
				.tg("Ripples", "rp", true)
				.tg("Keep normal rain too", "vr", false);
		Mod.own("trails", "Trails", "Glowing lines behind you", 2, "sparkle")
				.ch("Style", "st", 0, "Aura ribbon", "Speed lines", "Sparkles", "Ghost curtain")
				.ch("Color", "cm", 0, "Azure accent", "Custom", "Rainbow", "Two color flow")
				.cl("Color", "c1", Rw.AZ)
				.cl("Second color", "c2", 0xFF4AD2)
				.sl("Length", "ln", 100, 20, 400, "%")
				.sl("Width", "wd", 100, 30, 300, "%")
				.ch("Height", "ht", 0, "Feet", "Waist", "Head")
				.sl("Opacity", "op", 85, 10, 100, "%")
				.tg("Glow", "gl", true)
				.tg("Other players too", "ot", false);
		Mod.own("amb", "Ambience", "Makes the whole world around you look better", 2, "sparkle")
				.ch("Color", "cm", 0, "Azure accent", "Custom", "Rainbow")
				.cl("Color", "c1", Rw.AZ)
				.tg("Glow on bright lights", "bl", true)
				.sl("Glow strength", "bs", 60, 10, 150, "%")
				.tg("Sun rays", "sr", true)
				.tg("Shiny ground", "gs", true)
				.sl("Shine", "gp", 35, 5, 100, "%")
				.tg("Ground haze", "hz", true)
				.tg("Color grade", "cg", true)
				.tg("Soft vignette", "vg", true)
				.sl("Vignette strength", "vs", 35, 5, 90, "%")
				.tg("Floating lights", "pt", true)
				.sl("Amount", "n", 100, 10, 300, "%")
				.sl("Size", "sz", 100, 40, 250, "%")
				.sl("Brightness", "br", 80, 10, 150, "%")
				.tg("Fireflies at night", "ff", true);
		Mod.own("place", "Block Place Animation", "Blocks pop in when you place them", 2, "tiles")
				.ch("Style", "st", 0, "Pop", "Slam", "Glow pulse")
				.ch("Color", "cm", 0, "Azure accent", "Custom", "Rainbow")
				.cl("Color", "c1", Rw.AZ)
				.sl("Speed", "sp", 100, 30, 300, "%")
				.tg("Sparks", "sk", true)
				.tg("Ring on the ground", "rg", true);
		Mod.own("totpop", "Totem Pop Size", "Size of the totem that flies up when you pop", 2, "totem")
				.sl("Size", "sz", 60, 0, 100, "%")
				.a(new Rw.Nt("Keeps big pack totems small."));
		Mod.own("bhl", "Block Highlight", "Your own look for the block you aim at", 2, "tiles")
				.ch("Show", "mode", 2, "Outline", "Inside", "Outline and inside", "Nothing")
				.ch("Color mode", "cm", 0, "Custom", "Azure accent", "Rainbow", "Chroma", "Two color fade", "Pulse")
				.cl("Outline color", "oc", 0xFFFFFF)
				.cl("Second color", "c2", 0x4AA8FF)
				.sl("Outline opacity", "oa", 100, 5, 100, "%")
				.sl("Line width", "lw", 2, 1, 10, "px")
				.ch("Line style", "ost", 0, "Full", "Corners only")
				.ch("Inside color", "sm", 0, "Same as outline", "Custom", "Chroma")
				.cl("Inside custom color", "sc", 0x4AA8FF)
				.sl("Inside opacity", "sa", 22, 0, 100, "%")
				.ch("Shape", "shp", 0, "Real block shape", "Always full block")
				.sl("Grow", "gr", 0, 0, 25, "%")
				.sl("Rainbow and chroma speed", "rs", 50, 5, 200, "%")
				.sl("Rainbow saturation", "cs", 70, 10, 100, "%")
				.tg("Glide between blocks", "smv", true)
				.tg("Fade in", "fi", true)
				.tg("Breathing", "br", false)
				.sl("Breathing speed", "bs", 50, 10, 200, "%")
				.a(new Rw.Nt("Mining animation"))
				.tg("Show mining progress", "mp", true)
				.ch("Mining style", "ms", 0, "Grow from the middle", "Fill from the bottom", "Shrink")
				.ch("Mining color", "mcm", 0, "Red to green", "Custom", "Chroma", "Same as outline")
				.cl("Mining custom color", "mc", 0xFF4AD2)
				.sl("Mining opacity", "ma", 40, 5, 100, "%")
				.tg("Mining outline", "mo", true);
		Mod.own("lyrics", "Spotify Lyrics", "Live lyrics floating in the air", 2, "note")
				.a(new Rw.Nt("Plays along with Spotify on this pc."))
				.ch("Show", "wh", 0, "In the air", "On screen", "Both")
				.ch("Placement", "pl", 0, "Sides of your view", "Middle", "Right side", "Left side")
				.ch("Animation", "an", 6, "Rise", "Pop", "Typewriter", "Fade", "Wave", "Drop", "Cinematic")
				.ch("End animation", "ex", 0, "Zoom in", "Zoom out", "Fade", "Shatter")
				.tg("Sparks on each word", "sk", true)
				.tg("Line progress glow", "pg", true)
				.tg("Bounce with the beat", "bb", true)
				.ch("Color", "cm", 0, "Azure accent", "Custom", "Chroma")
				.cl("Text color", "tc", 0xFFFFFF)
				.tg("Light up sung words", "hl", true)
				.cl("Sung words color", "hc", 0xFFFFFF)
				.a(new Rw.Nt("Glow"))
				.tg("Glow", "gl", true)
				.cl("Glow color", "gc", Rw.AZ)
				.sl("Glow strength", "gs", 60, 5, 150, "%")
				.sl("Glow size", "gz", 100, 30, 250, "%")
				.tg("Light rays", "ry", true)
				.sl("Ray length", "rl", 100, 30, 250, "%")
				.tg("Pulse with the music", "pm", true)
				.a(new Rw.Nt("Look"))
				.sl("Size", "sz", 100, 40, 300, "%")
				.sl("Distance", "ds", 6, 2, 20, " blocks")
				.sl("Height", "ht", 1, -4, 8, "")
				.ch("Facing", "fc", 0, "Stay where they appear", "Always face me")
				.tg("Float up when done", "fu", true)
				.sl("Fade out time (Fade end)", "fo", 4, 1, 15, " s")
				.tg("Text shadow", "sh", true)
				.tg("Dark outline", "ol", false)
				.tg("See through walls", "sw", false)
				.tg("Song title when a song starts", "tt", true)
				.a(new Rw.Nt("On screen"))
				.tg("Line before and after", "ctx", true)
				.tg("Panel", "pnl", false)
				.sl("Scale", "sc", 100, 50, 250, "%")
				.sl("Timing offset", "off", 0, -3000, 3000, " ms");
		w("fireglow", "Fire Glow", "Recolor and lower the fire", 4, "flame", Fcl::glow, v -> {
			Fcl.glow(v);
			hits.fire.Config.save();
		}, "hits.fire.ui.Panel");
		Mod.own("firecolor", "Fire Color", "Just recolors the fire, no glow", 4, "flame")
				.cl("Fire", "fc", 0xFF7A1F)
				.cl("Soul fire", "sc", 0x3FD8FF);
		w("ogfog", "OG Fog", "The old fog look", 2, "m_fog", () -> dev.ogfg.Cfg.c.on, v -> {
			dev.ogfg.Cfg.c.on = v;
			dev.ogfg.Cfg.sv();
		}, "dev.ogfg.Gui");
		w("blur", "Blur Reborn", "Motion blur", 2, "blur", () -> dev.blrb.Cfg.c.on, v -> {
			dev.blrb.Cfg.c.on = v;
			dev.blrb.Cfg.sv();
		}, "dev.blrb.Gui");
		w("oldpots", "Old Potions", "Old potion colors and glint", 2, "m_oldpot", () -> dev.oldp.Cfg.c.col || dev.oldp.Cfg.c.gl, v -> {
			dev.oldp.Cfg.c.col = v;
			dev.oldp.Cfg.c.gl = v;
			dev.oldp.Cfg.sv();
		}, "dev.oldp.Gui");
		w("itemsop", "Items Opacity", "Fade dropped items", 2, "m_itop", () -> dev.itop.Cfg.c.on, v -> {
			dev.itop.Cfg.c.on = v;
			dev.itop.Cfg.sv();
		}, "dev.itop.ui.Gui");
		Mod.own("armorop", "Armor Opacity", "Fade the armor you see on players", 2, "armorop")
				.sl("Your armor", "so", 50, 0, 100, "%")
				.sl("Other players' armor", "oo", 100, 0, 100, "%")
				.sl("Your armor in menus", "io", 100, 0, 100, "%")
				.sl("Your elytra", "es", 100, 0, 100, "%")
				.sl("Other players' elytra", "eo", 100, 0, 100, "%")
				.a(new Rw.Bt("Settings", "Open", () -> Gui.go("dev.wscl.ArGui")).tip("Opens the settings with a preview of you."));
		Mod.own("noshadow", "No Entity Shadows", "Removes every shadow", 2, "m_noshadow").wy();
		Mod.own("time", "Time Changer", "Pick the time of day", 2, "moon")
				.ch("Preset", "pr", 2, "Custom", "Sunrise", "Day", "Noon", "Sunset", "Night", "Midnight")
				.sl("Custom time", "t", 6000, 0, 23999, "")
				.ch("Mode", "mode", 0, "Frozen", "Cycling")
				.sl("Cycle speed", "sp", 20, 1, 200, "x");
		Mod.own("sky", "Custom Sky", "Aurora, stars and a glowing night sky", 2, "sparkle")
				.ch("Color", "col", 0, "Follow accent", "Custom")
				.cl("Custom color", "cc", 0x4AA8FF)
				.ch("When", "wn", 0, "Always night", "Only at night")
				.tg("Aurora", "au", true)
				.tg("Moving aurora", "am", true)
				.sl("Aurora strength", "as", 70, 10, 150, "%")
				.tg("Stars", "st", true)
				.sl("Star amount", "sa", 3, 1, 3, "")
				.tg("Milky way", "mw", true)
				.tg("Falling stars", "fs", true)
				.sl("Falling star gap", "ff", 8, 2, 60, "s")
				.tg("Moon", "mn", true)
				.sl("Sky brightness", "bg", 22, 5, 60, "%");

		for (Mod hm : Mod.ALL) {
			if (hm.ct == 1) hm.a(new Rw.Bt("Edit HUD", "Open", () -> {
				var mc = net.minecraft.client.Minecraft.getInstance();
				mc.setScreen(new HudEd(mc.screen));
			}).tip("Drag and group every hud element."));
		}
	}
}
