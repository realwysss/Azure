package dev.wscl;

import java.io.ByteArrayInputStream;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.DirectJoinServerScreen;
import net.minecraft.client.gui.screens.FaviconTexture;
import net.minecraft.client.gui.screens.ManageServerScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.multiplayer.ServerStatusPinger;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.network.EventLoopGroupHolder;
import net.minecraft.util.Util;
import org.lwjgl.glfw.GLFW;

public class AzMulti extends Screen {
	final Screen par;
	ServerList servers;
	ServerStatusPinger pinger = new ServerStatusPinger();
	final Map<ServerData, Long> pst = new HashMap<>();
	final Map<ServerData, FaviconTexture> ic = new HashMap<>();
	final Map<ServerData, byte[]> icb = new HashMap<>();
	final Map<Object, float[]> an = new HashMap<>();
	List<ServerData> vis = new ArrayList<>();
	ServerData sel;
	String q = "";
	boolean sf;
	float sa;
	int sbc;
	boolean favOnly;
	static java.util.List<Api.Sp> spSrc;
	static final List<ServerData> SPD = new ArrayList<>();
	static final Map<ServerData, Api.Sp> SPM = new HashMap<>();
	static final Map<String, byte[]> CIC = new java.util.concurrent.ConcurrentHashMap<>();

	static boolean sp(ServerData s) {
		return s != null && SPM.containsKey(s);
	}

	static int spc(ServerData s) {
		Api.Sp o = SPM.get(s);
		return o == null ? 0xF5C542 : o.col;
	}

	static void syn() {
		Api.url();
		var l = Api.sps;
		if (l == spSrc) return;
		spSrc = l;
		List<ServerData> nw = new ArrayList<>();
		Map<ServerData, Api.Sp> nm = new HashMap<>();
		for (Api.Sp o : l) {
			ServerData d = null;
			for (ServerData e : SPD) if (e.ip.equalsIgnoreCase(o.ip)) d = e;
			if (d == null) d = new ServerData(o.nm, o.ip, ServerData.Type.OTHER);
			d.name = o.nm;
			nw.add(d);
			nm.put(d, o);
			String k = o.ip.toLowerCase();
			if (!o.ic.isEmpty() && !CIC.containsKey(k)) {
				Thread t = new Thread(() -> {
					try {
						CIC.put(k, Api.H.send(java.net.http.HttpRequest.newBuilder(java.net.URI.create(o.ic)).timeout(java.time.Duration.ofSeconds(10)).GET().build(), java.net.http.HttpResponse.BodyHandlers.ofByteArray()).body());
					} catch (Throwable e) {
					}
				}, "azure-spicon");
				t.setDaemon(true);
				t.start();
			}
		}
		SPD.clear();
		SPD.addAll(nw);
		SPM.clear();
		SPM.putAll(nm);
	}
	int[] ipr;
	public static int[] ti;
	float sc;
	float tsc;
	boolean sbd;
	boolean sbh;
	public int dragIdx = -1;
	public double gox;
	public double goy;
	public boolean dragging;
	double dsx;
	double dsy;
	long lastClick;
	int lastIdx = -1;
	int lx;
	int lw;
	int ly0;
	int ly1;
	long t0 = Util.getMillis();
	int vers;
	boolean dr;

	public AzMulti(Screen p) {
		super(Component.literal("Multiplayer"));
		par = p;
	}

	boolean grid() {
		return Cfg.gi("mp.grid", 0) == 1;
	}

	static String fk(ServerData s) {
		return "fav." + (s.ip == null ? "" : s.ip.toLowerCase());
	}

	boolean fav(ServerData s) {
		return Cfg.gi(fk(s), 0) == 1;
	}

	protected void init() {
		if (servers == null) {
			servers = new ServerList(minecraft);
			servers.load();
			syn();
			for (int i = 0; i < servers.size(); i++) ping(servers.get(i));
			for (ServerData d : SPD) ping(d);
		} else if (dr) {
			syn();
			dr = false;
			for (int i = 0; i < servers.size(); i++) if (servers.get(i).state() == ServerData.State.PINGING) ping(servers.get(i));
			for (ServerData d : SPD) if (d.state() == ServerData.State.PINGING) ping(d);
		}
		lw = Math.min(width - 32, 660);
		lx = (width - lw) / 2;
		ly0 = 72;
		ly1 = height - 52;
		Chip chp = new Chip(0, 10);
		chp.setX(width - 12 - chp.getWidth());
		addRenderableWidget(chp);
		rebuild();
	}

	public static boolean test;
	static final Map<String, byte[]> ICO = new java.util.concurrent.ConcurrentHashMap<>();
	static final Map<String, Object[]> SNAP = new java.util.concurrent.ConcurrentHashMap<>();
	static final java.util.concurrent.ExecutorService POOL = java.util.concurrent.Executors.newFixedThreadPool(24, r -> {
		Thread t = new Thread(r, "azure-ping");
		t.setDaemon(true);
		return t;
	});

	void ping(ServerData sd) {
		if (test) {
			sd.motd = Component.literal("Survival   -   Join the fun").withColor(0xAAAAAA);
			sd.players = new net.minecraft.network.protocol.status.ServerStatus.Players(100, 12 + sd.ip.length() % 40, java.util.List.of());
			sd.ping = 20L + sd.ip.length() * 9L;
			sd.setState(ServerData.State.SUCCESSFUL);
			return;
		}
		Object[] sn = SNAP.get(sd.ip == null ? "" : sd.ip.toLowerCase());
		if (sn != null) {
			sd.motd = (Component) sn[0];
			sd.players = (net.minecraft.network.protocol.status.ServerStatus.Players) sn[1];
			sd.ping = (Long) sn[2];
			sd.version = (Component) sn[3];
			sd.protocol = (Integer) sn[4];
			sd.status = (Component) sn[5];
			byte[] sib = (byte[]) sn[6];
			if (sib == null && sd.ip != null) sib = ICO.get(sd.ip.toLowerCase());
			if (sib != null) sd.setIconBytes(sib);
			sd.setState(ServerData.State.SUCCESSFUL);
		} else {
			sd.setState(ServerData.State.PINGING);
			pst.put(sd, Util.getMillis());
			sd.ping = -2L;
			sd.motd = CommonComponents.EMPTY;
			sd.status = CommonComponents.EMPTY;
			sd.players = null;
		}
		final boolean nat = minecraft.options.useNativeTransport();
		final ServerStatusPinger pg = pinger;
		pst.put(sd, Util.getMillis());
		POOL.submit(() -> {
			try {
				pg.pingServer(sd, () -> {
				}, () -> sd.setState(sd.protocol == SharedConstants.getCurrentVersion().protocolVersion() ? ServerData.State.SUCCESSFUL : ServerData.State.INCOMPATIBLE), EventLoopGroupHolder.remote(nat));
			} catch (UnknownHostException e) {
				sd.setState(ServerData.State.UNREACHABLE);
				sd.motd = Component.translatable("multiplayer.status.cannot_resolve").withColor(-65536);
			} catch (Throwable e) {
				sd.setState(ServerData.State.UNREACHABLE);
				sd.motd = Component.translatable("multiplayer.status.cannot_connect").withColor(-65536);
			}
		});
	}

	List<ServerData> all() {
		List<ServerData> o = new ArrayList<>();
		for (int i = 0; i < servers.size(); i++) o.add(servers.get(i));
		o.addAll(SPD);
		return o;
	}

	void rebuild() {
		List<ServerData> a = new ArrayList<>();
		List<ServerData> b = new ArrayList<>();
		String k = q.toLowerCase();
		for (int i = 0; i < servers.size(); i++) {
			ServerData s = servers.get(i);
			if (!k.isEmpty() && !(s.name + " " + s.ip).toLowerCase().contains(k)) continue;
			if (fav(s)) a.add(s);
			else if (!favOnly) b.add(s);
		}
		a.addAll(b);
		if (!favOnly) for (int i = SPD.size() - 1; i >= 0; i--) {
			ServerData d = SPD.get(i);
			if (k.isEmpty() || (d.name + " " + d.ip).toLowerCase().contains(k)) a.add(0, d);
		}
		vis = a;
	}

	float an(Object o, boolean on, float sp) {
		float[] f = an.computeIfAbsent(o, kk -> new float[]{on ? 1 : 0});
		f[0] += ((on ? 1f : 0f) - f[0]) * sp;
		return f[0];
	}

	int cols() {
		return grid() ? Math.max(1, Math.min(4, (lw + 8) / 158)) : 1;
	}

	int rh() {
		return grid() ? 96 : 46;
	}

	int[] rect(int i) {
		int c = cols();
		if (c == 1) return new int[]{lx, ly0 + i * (rh() + 4) - Math.round(sc), lw, rh()};
		int gw = (lw - (c - 1) * 8) / c;
		return new int[]{lx + (i % c) * (gw + 8), ly0 + (i / c) * (rh() + 8) - Math.round(sc), gw, rh()};
	}

	int total() {
		int c = cols();
		int rows = (vis.size() + c - 1) / c;
		return rows * (rh() + (c == 1 ? 4 : 8));
	}

	int at(double mx, double my) {
		if (my < ly0 || my >= ly1) return -1;
		for (int i = 0; i < vis.size(); i++) {
			int[] r = rect(i);
			if (mx >= r[0] && mx < r[0] + r[2] && my >= r[1] && my < r[1] + r[3]) return i;
		}
		return -1;
	}

	FaviconTexture icon(ServerData s) {
		byte[] b = s.getIconBytes();
		if (sp(s) && s.ip != null) {
			byte[] cb = CIC.get(s.ip.toLowerCase());
			if (cb != null) b = cb;
		}
		FaviconTexture f = ic.get(s);
		if (f == null) {
			f = FaviconTexture.forServer(minecraft.getTextureManager(), s.ip);
			ic.put(s, f);
		}
		if (b != icb.get(s)) {
			icb.put(s, b);
			try {
				if (b != null) f.upload(com.mojang.blaze3d.platform.NativeImage.read(new ByteArrayInputStream(b)));
				else f.clear();
			} catch (Exception e) {
				f.clear();
			}
		}
		return f;
	}

	public void tick() {
		pinger.tick();
		long now = Util.getMillis();
		if (servers != null && !test) for (ServerData d : all()) {
			if (d.state() == ServerData.State.PINGING) {
				boolean bad = d.motd != null && d.motd.getContents() instanceof net.minecraft.network.chat.contents.TranslatableContents tc && tc.getKey().startsWith("multiplayer.status.cannot");
				Long t = pst.get(d);
				if (bad || (t != null && now - t > 12000)) {
					if (!bad) d.motd = Component.literal("Timed out").withColor(-65536);
					d.setState(ServerData.State.UNREACHABLE);
				}
			}
			if (d.state() == ServerData.State.SUCCESSFUL && d.ip != null) {
				String ik = d.ip.toLowerCase();
				byte[] ib = d.getIconBytes();
				if (ib != null) ICO.put(ik, ib);
				else if (ICO.containsKey(ik)) d.setIconBytes(ICO.get(ik));
			}
			if (d.state() == ServerData.State.SUCCESSFUL && d.ip != null && d.motd != null && d.version != null && d.status != null) SNAP.put(d.ip.toLowerCase(), new Object[]{d.motd, d.players, d.ping, d.version, d.protocol, d.status, d.getIconBytes()});
		}
	}

	void drop() {
		final ServerStatusPinger o = pinger;
		POOL.submit(() -> {
			try {
				o.removeAll();
			} catch (Throwable e) {
			}
		});
	}

	public void removed() {
		dr = true;
		drop();
		for (FaviconTexture f : ic.values()) f.close();
		ic.clear();
		icb.clear();
		if (servers != null) servers.save();
	}

	public void renderBackground(GuiGraphics g, int mx, int my, float d) {
		Vis.bg(g, (Util.getMillis() % 1000000L) / 1000f, 1f);
	}

	void pill(GuiGraphics g, int mx, int my, int ac, int x, int y, int w, int h, String ico, String t, boolean pri, boolean on) {
		boolean ov = mx >= x && mx < x + w && my >= y && my < y + h;
		float a = an("p" + x + "_" + y + t, ov, 0.3f);
		int r = Math.min(10, h / 2);
		if (a > 0.04f) Ui.glow(g, x, y, w, h, r, ac, a * 0.5f);
		Ui.rr(g, x, y, w, h, r, Ui.al(pri || on ? ac : 0x0C0A1A, pri ? 0.55f + 0.3f * a : on ? 0.3f + 0.2f * a : 0.7f + 0.2f * a));
		Ui.ro(g, x, y, w, h, r, Ui.al(pri || on || a > 0.05f ? ac : 0xFFFFFF, pri ? 0.9f : on ? 0.8f : 0.16f + 0.7f * a));
		float tw = t.isEmpty() ? 0f : Ui.w(t, 8.5f);
		float gw = (ico != null ? 12f : 0f) + (ico != null && !t.isEmpty() ? 6f : 0f) + tw;
		float gx = x + (w - gw) / 2f;
		if (ico != null) {
			Ic.dr(g, ico, Math.round(gx + 6f), y + h / 2, 12, Ui.al(0xFFFFFF, 0.9f));
			gx += 18f;
		}
		if (!t.isEmpty()) Ui.ts(g, t, gx, y + h / 2f - 3.7f, 8.5f, 0xFFFFFF, 1f);
	}

	public void render(GuiGraphics g, int mx, int my, float d) {
		renderBackground(g, mx, my, d);
		int ac = Hud.ac() & 0xFFFFFF;
		float k = Ui.ez((Util.getMillis() - t0) / 380f);
		Ui.GA = k;
		pill(g, mx, my, ac, 12, 12, 62, 22, "back", "Back", false, false);
		Ui.ts(g, "Multiplayer", 86, 15, 16f, 0xFFFFFF, 1f);
		String cnt = servers == null ? "" : servers.size() + " servers";
		Ui.tx(g, cnt, 88 + Ui.w("Multiplayer", 16f), 20, 0xFFFFFF, 0.4f);
		int sbw = Math.min(250, lw - 130);
		int sb0 = Ui.w("Search servers") + 42;
		sa += (((sf || !q.isEmpty()) ? 1f : 0f) - sa) * 0.22f;
		sbc = sb0 + Math.round((sbw - sb0) * Ui.ez(sa));
		if (sa > 0.02f) Ui.glow(g, lx, 42, sbc, 22, 11, ac, sa * 0.35f);
		Ui.rr(g, lx, 42, sbc, 22, 11, Ui.al(0xFFFFFF, sf ? 0.09f : 0.05f));
		Ui.ro(g, lx, 42, sbc, 22, 11, Ui.al(sf ? ac : 0xFFFFFF, sf ? 0.9f : 0.12f));
		Ic.dr(g, "search", lx + 14, 53, 12, Ui.al(0xFFFFFF, 0.55f));
		boolean caret = sf && (Util.getMillis() / 500) % 2 == 0;
		if (q.isEmpty() && !sf) Ui.tx(g, "Search servers", lx + 27, 49, 0xFFFFFF, 0.35f);
		else Ui.tx(g, q + (caret ? "|" : ""), lx + 27, 49, 0xFFFFFF, 1f);
		int bx = lx + lw - 2 * 26 + 4;
		pill(g, mx, my, ac, bx, 42, 22, 22, grid() ? "list" : "tiles", "", false, false);
		pill(g, mx, my, ac, bx + 26, 42, 22, 22, favOnly ? "starf" : "star", "", false, favOnly);
		int total = total();
		tsc = Math.max(0, Math.min(Math.max(0, total - (ly1 - ly0)), tsc));
		sc += (tsc - sc) * 0.3f;
		g.enableScissor(lx - 6, ly0, lx + lw + 8, ly1);
		int c = cols();
		for (int i = 0; i < vis.size(); i++) {
			int[] r = rect(i);
			if (r[1] + r[3] < ly0 || r[1] > ly1) continue;
			ServerData s = vis.get(i);
			boolean ov = mx >= r[0] && mx < r[0] + r[2] && my >= r[1] && my < r[1] + r[3] && my >= ly0 && my < ly1;
			boolean se = s == sel;
			boolean dr = dragging && i == dragIdx;
			float h = an(s, ov || se, 0.28f);
			float kk = Ui.ez((Util.getMillis() - t0 - 80 - i * 25) / 320f);
			Ui.GA = k * kk;
			int ry = r[1] + Math.round((1f - kk) * 10);
			if (sp(s)) {
				int sc2 = spc(s);
				int rd = c == 1 ? 10 : 12;
				Ui.rr(g, r[0], ry, r[2], r[3], rd, Ui.al(Vis.mix(0x0B0814, sc2, 0.1f + 0.05f * h), 0.95f));
				Ui.ro(g, r[0], ry, r[2], r[3], rd, Ui.al(sc2, 0.3f + 0.3f * h));
			}
			if (c == 1) {
				if (h > 0.02f) Ui.rr(g, r[0], ry, r[2], r[3], 10, Ui.al(se ? Vis.mix(0xFFFFFF, ac, 0.5f) : 0xFFFFFF, (se ? 0.1f : 0.05f) * h + (dr ? 0.06f : 0f)));
				if (se) Ui.rr(g, r[0] + 1, ry + 10, 2, r[3] - 20, 1, Ui.al(ac, 1f));
				if (i < vis.size() - 1 && !dr) g.fill(r[0] + 56, ry + r[3] + 1, r[0] + r[2] - 8, ry + r[3] + 2, Ui.al(0xFFFFFF, 0.05f));
				row(g, s, r[0] + 10, ry + 7, r[2] - 20, ac, ov, h);
			} else {
				if (h > 0.02f || se) Ui.rr(g, r[0], ry, r[2], r[3], 12, Ui.al(se ? Vis.mix(0xFFFFFF, ac, 0.5f) : 0xFFFFFF, (se ? 0.1f : 0.05f) * Math.max(h, se ? 1f : 0f) + (dr ? 0.06f : 0f)));
				tile(g, s, r[0], ry, r[2], r[3], ac, h);
			}
		}
		Ui.GA = k;
		g.disableScissor();
		if (vis.isEmpty()) {
			String a = favOnly ? "No favourites yet" : q.isEmpty() ? "No servers yet" : "No match";
			String b = favOnly ? "Click the star on a server to add it" : "Press Add to put your first server here";
			Ui.ts(g, a, width / 2f - Ui.w(a, 13f) / 2f, ly0 + 60, 13f, 0xFFFFFF, 0.9f);
			Ui.ts(g, b, width / 2f - Ui.w(b, 9f) / 2f, ly0 + 80, 9f, 0xFFFFFF, 0.4f);
		}
		if (ly1 > ly0 && total > ly1 - ly0) {
			int th = Math.max(18, (ly1 - ly0) * (ly1 - ly0) / total);
			int ty = ly0 + Math.round((ly1 - ly0 - th) * (sc / (total - (ly1 - ly0))));
			Ui.rr(g, lx + lw + 6, ly0, sbd || sbh ? 5 : 3, ly1 - ly0, 2, Ui.al(0xFFFFFF, 0.07f));
			Ui.rr(g, lx + lw + 6, ty, sbd || sbh ? 5 : 3, th, 2, Ui.al(ac, sbd || sbh ? 1f : 0.7f));
		}
		String[] ln = acts();
		String[] li = vfp() ? new String[]{"play", "link", "plus", "edit", "trash", "globe"} : new String[]{"play", "link", "plus", "edit", "trash"};
		int na = ln.length;
		int av = lw - 96;
		int bw = Math.min(86, (av - (na - 1) * 6) / na);
		int tw = na * bw + (na - 1) * 6;
		int ax = lx + 96 + (av - tw) / 2;
		int ay = height - 40;
		for (int i = 0; i < na; i++) {
			boolean dis = sel == null && (i == 0 || i == 3 || i == 4);
			Ui.GA = k * (dis ? 0.4f : 1f);
			pill(g, dis ? -999 : mx, dis ? -999 : my, ac, ax + i * (bw + 6), ay, bw, 24, li[i], bw >= 66 ? ln[i] : "", i == 0, false);
		}
		Ui.GA = k;
		pill(g, mx, my, ac, lx, ay, 86, 24, "refresh", "Refresh", false, false);
		Ui.GA = 1f;
		if (dragging && dragIdx >= 0 && dragIdx < vis.size()) {
			ServerData s = vis.get(dragIdx);
			int[] r = rect(dragIdx);
			int gx = (int) (mx - gox);
			int gy = (int) (my - goy);
			Ui.GA = 0.62f;
			Ui.glow(g, gx, gy, r[2], r[3], 10, ac, 0.35f);
			Ui.rr(g, gx, gy, r[2], r[3], 10, Ui.al(0x120E26, 0.85f));
			if (c == 1) row(g, s, gx + 10, gy + 7, r[2] - 20, ac, false, 0f);
			else tile(g, s, gx, gy, r[2], r[3], ac, 0f);
			Ui.ro(g, gx, gy, r[2], r[3], 10, Ui.al(ac, 1f));
			Ui.GA = 1f;
		}
		if (ipr != null && mx >= ipr[0] - 2 && mx < ipr[0] + ipr[2] + 2 && my >= ipr[1] - 2 && my < ipr[1] + ipr[3] + 2) {
			String a1 = "Partner server";
			String a2 = "Want your server here too?";
			String a3 = "Join the Azure Discord.";
			int ttw = Math.max(Ui.w(a2, 8f), Math.max(Ui.w(a3, 8f), Ui.w(a1, 8.5f))) + 20;
			int tx = Math.min(width - ttw - 6, mx + 10);
			int ty = my + 12;
			Ui.rr(g, tx, ty, ttw, 46, 8, Ui.al(0x0E0B1E, 0.97f));
			Ui.ro(g, tx, ty, ttw, 46, 8, Ui.al(0xF5C542, 0.7f));
			Ui.ts(g, a1, tx + 10, ty + 7, 8.5f, 0xFFD870, 1f);
			Ui.ts(g, a2, tx + 10, ty + 20, 8f, 0xFFFFFF, 0.85f);
			Ui.ts(g, a3, tx + 10, ty + 31, 8f, 0xFFFFFF, 0.85f);
		}
		ipr = null;
		super.render(g, mx, my, d);
	}

	void ping(GuiGraphics g, ServerData s, int x, int y) {
		int hs[] = {3, 5, 7, 9};
		if (s.state() == ServerData.State.UNREACHABLE) {
			for (int i = 0; i < 4; i++) g.fill(x + i * 4, y + 10 - hs[i], x + i * 4 + 3, y + 10, Ui.al(0xFFFFFF, 0.12f));
			Ui.rr(g, x + 5, y + 1, 7, 7, 3, Ui.al(0xFF5050, 1f));
			g.fill(x + 8, y + 2, x + 9, y + 5, Ui.al(0xFFFFFF, 1f));
			g.fill(x + 8, y + 6, x + 9, y + 7, Ui.al(0xFFFFFF, 1f));
			return;
		}
		if (s.state() == ServerData.State.PINGING) {
			int a = (int) ((Util.getMillis() / 140) % 5);
			for (int i = 0; i < 4; i++) g.fill(x + i * 4, y + 10 - hs[i], x + i * 4 + 3, y + 10, Ui.al(0xFFFFFF, i < a ? 0.7f : 0.15f));
			return;
		}
		long p = s.ping;
		int bars = p < 0 ? 1 : p < 120 ? 4 : p < 220 ? 3 : p < 400 ? 2 : 1;
		int col = bars == 4 ? 0x5CE08A : bars == 3 ? 0xB5E05C : bars == 2 ? 0xFFD24A : 0xFF8040;
		for (int i = 0; i < 4; i++) g.fill(x + i * 4, y + 10 - hs[i], x + i * 4 + 3, y + 10, Ui.al(i < bars ? col : 0xFFFFFF, i < bars ? 1f : 0.13f));
	}

	String players(ServerData s) {
		if (s.state() == ServerData.State.PINGING) return "Pinging...";
		if (s.state() == ServerData.State.UNREACHABLE) return "Can't connect";
		if (s.state() == ServerData.State.INCOMPATIBLE) return s.version == null ? "Other version" : s.version.getString();
		if (s.players == null) return "";
		return s.players.online() + "/" + s.players.max();
	}

	void row(GuiGraphics g, ServerData s, int x, int y, int w, int ac, boolean ov, float h) {
		g.blit(RenderPipelines.GUI_TEXTURED, icon(s).textureLocation(), x, y, 0f, 0f, 32, 32, 64, 64, 64, 64);
		String nm = s.name == null || s.name.isEmpty() ? s.ip : s.name;
		int rx = x + w;
		int star = rx - 14;
		boolean fv = fav(s);
		if (!sp(s)) Ic.dr(g, fv ? "starf" : "star", star + 6, y + 8, 12, Ui.al(fv ? 0xFFD24A : 0xFFFFFF, fv ? 1f : 0.25f + 0.5f * h));
		ping(g, s, rx - 36, y + 3);
		String pc = players(s);
		Tf.ds(g, pc, rx - 44 - Ui.w(pc), y + 5, s.state() == ServerData.State.UNREACHABLE ? 0xFF77758A : 0xFFB8B4CC, false);
		String fn = fit(nm, w - 160 - Ui.w(pc));
		Ui.tx(g, fn, x + 42, y + 5, 0xFFFFFF, 1f);
		int bwd = sp(s) ? Ui.w("Partner", 8f) + 22 : 0;
		if (sp(s)) badge(g, s, rx - bwd, y + 17);
		if (sp(s) && s.motd != null) {
			String mt = fit(s.motd.getString().trim(), w - 60 - bwd - 6);
			Tf.ds(g, mt, x + 42, y + 18, 0xFFAAAAAA, false);
		} else if (s.motd != null) {
			var l = font.split(s.motd, Math.max(40, w - 60));
			if (!l.isEmpty()) g.drawString(font, l.get(0), x + 42, y + 18, 0xFFAAAAAA, false);
		}
	}

	void tile(GuiGraphics g, ServerData s, int x, int y, int w, int h, int ac, float hv) {
		int is = 44;
		g.blit(RenderPipelines.GUI_TEXTURED, icon(s).textureLocation(), x + (w - is) / 2, y + 10, 0f, 0f, is, is, 64, 64, 64, 64);
		String nm = s.name == null || s.name.isEmpty() ? s.ip : s.name;
		String t = fit(nm, w - 16);
		Ui.tx(g, t, x + (w - Ui.w(t)) / 2, y + 60, 0xFFFFFF, 1f);
		String pc = players(s);
		Ui.tx(g, pc, x + 10, y + 76, 0xFFFFFF, 0.6f);
		ping(g, s, x + w - 24, y + 73);
		boolean fv = fav(s);
		if (!sp(s)) Ic.dr(g, fv ? "starf" : "star", x + w - 14, y + 14, 12, Ui.al(fv ? 0xFFD24A : 0xFFFFFF, fv ? 1f : 0.2f + 0.6f * hv));
		else badge(g, s, x + 8, y + 8);
	}

	void badge(GuiGraphics g, ServerData s, int x, int y) {
		String t = "Partner";
		int bc = spc(s);
		int w = Ui.w(t, 8f) + 22;
		Ui.rr(g, x, y, w, 12, 6, Ui.al(bc, 0.2f));
		Ui.ro(g, x, y, w, 12, 6, Ui.al(bc, 0.7f));
		Ic.dr(g, "starf", x + 8, y + 6, 8, Ui.al(bc, 1f));
		Ui.ts(g, t, x + 14, y + 2.6f, 8f, bc, 1f);
		ipr = new int[]{x, y, w, 12};
		ti = ipr;
	}

	String fit(String s, int max) {
		if (Ui.w(s) <= max) return s;
		while (s.length() > 1 && Ui.w(s + "..") > max) s = s.substring(0, s.length() - 1);
		return s + "..";
	}

	void join(ServerData s) {
		ConnectScreen.startConnecting(this, minecraft, ServerAddress.parseString(s.ip), s, false, null);
	}

	void refresh() {
		drop();
		pinger = new ServerStatusPinger();
		for (int i = 0; i < servers.size(); i++) ping(servers.get(i));
		for (ServerData d : SPD) ping(d);
	}

	void add() {
		ServerData n = new ServerData("Minecraft Server", "", ServerData.Type.OTHER);
		minecraft.setScreen(new ManageServerScreen(this, Component.translatable("manageServer.add.title"), ok -> {
			if (ok) {
				servers.add(n, false);
				servers.save();
				ping(n);
				rebuild();
			}
			minecraft.setScreen(this);
		}, n));
	}

	void edit() {
		if (sel == null || sp(sel)) return;
		ServerData o = sel;
		ServerData n = new ServerData(o.name, o.ip, ServerData.Type.OTHER);
		n.copyFrom(o);
		minecraft.setScreen(new ManageServerScreen(this, Component.translatable("manageServer.edit.title"), ok -> {
			if (ok) {
				o.name = n.name;
				o.ip = n.ip;
				o.copyFrom(n);
				servers.save();
				ping(o);
				rebuild();
			}
			minecraft.setScreen(this);
		}, n));
	}

	void del() {
		if (sel == null || sp(sel)) return;
		ServerData o = sel;
		minecraft.setScreen(new ConfirmScreen(ok -> {
			if (ok) {
				servers.remove(o);
				servers.save();
				sel = null;
				rebuild();
			}
			minecraft.setScreen(this);
		}, Component.translatable("selectServer.deleteQuestion"), Component.translatable("selectServer.deleteWarning", o.name), Component.translatable("selectServer.deleteButton"), CommonComponents.GUI_CANCEL));
	}

	void direct() {
		ServerData n = new ServerData(Component.translatable("selectServer.defaultName").getString(), "", ServerData.Type.OTHER);
		minecraft.setScreen(new DirectJoinServerScreen(this, ok -> {
			if (ok) join(n);
			else minecraft.setScreen(this);
		}, n));
	}

	static boolean vfp() {
		return net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded("viafabricplus");
	}

	String[] acts() {
		return vfp() ? new String[]{"Join", "Direct", "Add", "Edit", "Delete", "Versions"} : new String[]{"Join", "Direct", "Add", "Edit", "Delete"};
	}

	void versions() {
		try {
			Class<?> k = Class.forName("com.viaversion.viafabricplus.screen.impl.ProtocolSelectionScreen");
			Object in = k.getField("INSTANCE").get(null);
			k.getMethod("open", Screen.class).invoke(in, this);
		} catch (Throwable t) {
		}
	}

	void move(int from, int to) {
		ServerData a = vis.get(from);
		ServerData b = vis.get(to);
		if (fav(a) != fav(b)) return;
		int ia = -1;
		int ib = -1;
		for (int i = 0; i < servers.size(); i++) {
			if (servers.get(i) == a) ia = i;
			if (servers.get(i) == b) ib = i;
		}
		if (ia < 0 || ib < 0) return;
		while (ia < ib) {
			servers.swap(ia, ia + 1);
			ia++;
		}
		while (ia > ib) {
			servers.swap(ia, ia - 1);
			ia--;
		}
		rebuild();
		dragIdx = to;
	}

	public boolean mouseClicked(MouseButtonEvent e, boolean dbl) {
		double mx = e.x();
		double my = e.y();
		sf = false;
		if (onBar(mx, my)) {
			sbd = true;
			barTo(my);
			return true;
		}
		if (super.mouseClicked(e, dbl)) return true;
		if (mx >= 12 && mx < 74 && my >= 12 && my < 34) {
			Snd.p("click");
			onClose();
			return true;
		}
		int sbw = Math.min(250, lw - 130);
		if (mx >= lx && mx < lx + Math.max(sbc, sbw / 2) && my >= 42 && my < 64) {
			sf = true;
			return true;
		}
		int bx = lx + lw - 2 * 26 + 4;
		if (my >= 42 && my < 64) {
			if (mx >= bx && mx < bx + 22) {
				Snd.p("click");
				Cfg.si("mp.grid", grid() ? 0 : 1);
				tsc = 0;
				sc = 0;
				return true;
			}
			if (mx >= bx + 26 && mx < bx + 48) {
				Snd.p("click");
				favOnly = !favOnly;
				rebuild();
				return true;
			}
		}
		int na = acts().length;
		int av = lw - 96;
		int bw = Math.min(86, (av - (na - 1) * 6) / na);
		int tw = na * bw + (na - 1) * 6;
		int ax = lx + 96 + (av - tw) / 2;
		int ay = height - 40;
		if (mx >= lx && mx < lx + 86 && my >= ay && my < ay + 24) {
			Snd.p("click");
			refresh();
			return true;
		}
		if (my >= ay && my < ay + 24 && mx >= ax && mx < ax + tw) {
			int i = (int) ((mx - ax) / (bw + 6));
			if (i < na && mx < ax + i * (bw + 6) + bw) {
				Snd.p("click");
				if (i == 0 && sel != null) join(sel);
				else if (i == 1) direct();
				else if (i == 2) add();
				else if (i == 3) edit();
				else if (i == 4) del();
				else if (i == 5) versions();
				return true;
			}
		}
		int i = at(mx, my);
		if (i >= 0) {
			ServerData s = vis.get(i);
			int[] r = rect(i);
			boolean starHit = grid() ? (mx >= r[0] + r[2] - 22 && my < r[1] + 30) : (mx >= r[0] + r[2] - 24 && mx < r[0] + r[2] - 4 && my < r[1] + 24);
			if (starHit && !sp(s)) {
				Snd.p("click");
				Cfg.si(fk(s), fav(s) ? 0 : 1);
				rebuild();
				return true;
			}
			long now = Util.getMillis();
			if (i == lastIdx && now - lastClick < 320) {
				join(s);
				return true;
			}
			lastClick = now;
			lastIdx = i;
			sel = s;
			dragIdx = sp(s) ? -1 : i;
			dsx = mx;
			dsy = my;
			int[] gr = rect(i);
			gox = mx - gr[0];
			goy = my - gr[1];
			dragging = false;
			return true;
		}
		return false;
	}

	boolean onBar(double mx, double my) {
		return ly1 > ly0 && total() > ly1 - ly0 && mx >= lx + lw + 1 && mx < lx + lw + 18 && my >= ly0 && my < ly1;
	}

	void barTo(double my) {
		int vh = ly1 - ly0;
		int th = Math.max(18, vh * vh / Math.max(1, total()));
		float f = (float) ((my - ly0 - th / 2.0) / Math.max(1, vh - th));
		tsc = Math.max(0f, Math.min(total() - vh, f * (total() - vh)));
		sc = tsc;
	}

	public void mouseMoved(double mx, double my) {
		sbh = onBar(mx, my);
		super.mouseMoved(mx, my);
	}

	public boolean mouseDragged(MouseButtonEvent e, double dx, double dy) {
		if (sbd) {
			barTo(e.y());
			return true;
		}
		if (dragIdx >= 0 && dragIdx < vis.size()) {
			if (!dragging && Math.abs(e.y() - dsy) + Math.abs(e.x() - dsx) > 6) dragging = true;
			if (dragging) {
				int t = at(e.x(), e.y());
				if (t >= 0 && t != dragIdx) move(dragIdx, t);
				if (e.y() < ly0 + 14) tsc -= 6;
				if (e.y() > ly1 - 14) tsc += 6;
				return true;
			}
		}
		return super.mouseDragged(e, dx, dy);
	}

	public boolean mouseReleased(MouseButtonEvent e) {
		sbd = false;
		if (dragging) servers.save();
		dragging = false;
		dragIdx = -1;
		return super.mouseReleased(e);
	}

	public boolean mouseScrolled(double mx, double my, double sx, double sy) {
		tsc -= (float) sy * 30f;
		return true;
	}

	public boolean charTyped(CharacterEvent e) {
		String s = e.codepointAsString();
		if (!s.isEmpty() && s.charAt(0) >= 32 && (sf || (s.charAt(0) > 32 && q.length() < 40))) {
			sf = true;
			q += s;
			rebuild();
			return true;
		}
		return super.charTyped(e);
	}

	public boolean keyPressed(KeyEvent e) {
		if (sf) {
			if (e.key() == GLFW.GLFW_KEY_BACKSPACE && !q.isEmpty()) {
				q = q.substring(0, q.length() - 1);
				rebuild();
				return true;
			}
			if (e.key() == GLFW.GLFW_KEY_ESCAPE || e.key() == GLFW.GLFW_KEY_ENTER) {
				sf = false;
				return true;
			}
		}
		if (e.key() == GLFW.GLFW_KEY_BACKSPACE && !q.isEmpty()) {
			q = q.substring(0, q.length() - 1);
			rebuild();
			return true;
		}
		int i = sel == null ? -1 : vis.indexOf(sel);
		if (e.key() == GLFW.GLFW_KEY_DOWN && !vis.isEmpty()) {
			sel = vis.get(Math.min(vis.size() - 1, i + cols()));
			return true;
		}
		if (e.key() == GLFW.GLFW_KEY_UP && !vis.isEmpty()) {
			sel = vis.get(Math.max(0, i - cols()));
			return true;
		}
		if (e.key() == GLFW.GLFW_KEY_ENTER && sel != null) {
			join(sel);
			return true;
		}
		if (e.key() == GLFW.GLFW_KEY_F5) {
			refresh();
			return true;
		}
		return super.keyPressed(e);
	}

	public void onClose() {
		minecraft.setScreen(par);
	}
}
