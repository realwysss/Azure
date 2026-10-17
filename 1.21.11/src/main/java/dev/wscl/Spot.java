package dev.wscl;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.ptr.IntByReference;

public class Spot {
	public static volatile String song = "";
	public static volatile String art = "";
	static Thread th;
	public static boolean test;

	static String scan() {
		String[] r = {""};
		User32 u = User32.INSTANCE;
		u.EnumWindows((h, d) -> {
			char[] t = new char[512];
			u.GetWindowText(h, t, 512);
			String s = Native.toString(t);
			if (s.isEmpty() || (!r[0].isEmpty() && r[0].contains(" - "))) return true;
			IntByReference pid = new IntByReference();
			u.GetWindowThreadProcessId(h, pid);
			String cmd = ProcessHandle.of(pid.getValue()).flatMap(ph -> ph.info().command()).orElse("");
			if (cmd.toLowerCase().endsWith("spotify.exe") && (r[0].isEmpty() || s.contains(" - "))) r[0] = s;
			return true;
		}, null);
		return r[0];
	}

	public static void tick() {
		if (th != null && th.isAlive()) return;
		if (!System.getProperty("os.name", "").toLowerCase().contains("win")) return;
		th = new Thread(() -> {
			while (true) {
				try {
					if (test) {
						Thread.sleep(2500);
						continue;
					}
					String s = scan();
					int i = s.indexOf(" - ");
					if (i > 0 && !s.startsWith("Spotify")) {
						art = s.substring(0, i);
						song = s.substring(i + 3);
					} else {
						art = "";
						song = "";
					}
				} catch (Throwable e) {
					song = "";
				}
				try {
					Thread.sleep(2500);
				} catch (Exception e) {
					return;
				}
			}
		}, "azure-spotify");
		th.setDaemon(true);
		th.start();
	}

	public static boolean on() {
		return !song.isEmpty();
	}
}
