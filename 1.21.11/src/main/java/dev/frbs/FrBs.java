package dev.frbs;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinDef;
import net.fabricmc.api.ClientModInitializer;

public class FrBs implements ClientModInitializer {
	static final int[] CLS = {0x20, 0x8000, 0x80};

	@Override
	public void onInitializeClient() {
		Cfg.load();
		if (Cfg.c.rthr) Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
		prio(Cfg.c.prio);
	}

	public static void prio(int p) {
		if (!System.getProperty("os.name", "").toLowerCase().contains("win")) return;
		try {
			Kernel32 k = Kernel32.INSTANCE;
			k.SetPriorityClass(k.GetCurrentProcess(), new WinDef.DWORD(CLS[Math.max(0, Math.min(2, p))]));
		} catch (Throwable t) {
		}
	}
}
