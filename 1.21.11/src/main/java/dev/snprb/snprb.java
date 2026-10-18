package dev.snprb;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.brigadier.arguments.StringArgumentType;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.CompletableFuture;
import javax.imageio.ImageIO;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Util;

public class snprb {
	public static KeyMapping kgal, kshot;

	public static void init(KeyMapping.Category ct) {
		System.setProperty("java.awt.headless", "false");
		cfg.ld();
		kgal = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.snprb.gal", InputConstants.Type.KEYSYM, -1, ct));
		kshot = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.snprb.shot", InputConstants.Type.KEYSYM, -1, ct));
		ClientTickEvents.END_CLIENT_TICK.register(m -> {
			while (kgal.consumeClick()) if (dev.wscl.Mod.live("snippet")) m.setScreen(new gal(m.screen));
			while (kshot.consumeClick())
				if (dev.wscl.Mod.live("snippet") && m.options.keyScreenshot.isUnbound() || !m.options.keyScreenshot.same(kshot))
					Screenshot.grab(m.gameDirectory, m.getMainRenderTarget(), t -> m.execute(() -> m.gui.getChat().addMessage(t)));
		});
		ClientCommandRegistrationCallback.EVENT.register((d, r) -> d.register(ClientCommandManager.literal("snprb")
				.then(ClientCommandManager.argument("a", StringArgumentType.word())
						.then(ClientCommandManager.argument("n", StringArgumentType.greedyString()).executes(x -> {
							go(StringArgumentType.getString(x, "a"), StringArgumentType.getString(x, "n"));
							return 1;
						})))));
	}

	public static File fl(String n) {
		File f = new File(new File(Minecraft.getInstance().gameDirectory, "screenshots"), n);
		return n.equals(f.getName()) && n.endsWith(".png") && f.isFile() ? f : null;
	}

	public static void go(String a, String n) {
		File f = fl(n);
		if (f == null) return;
		Minecraft m = Minecraft.getInstance();
		m.execute(() -> {
			switch (a) {
				case "cp" -> CompletableFuture.runAsync(() -> {
					String er = null;
					try {
						cp(f);
					} catch (Throwable e) {
						er = e.toString();
					}
					String fer = er;
					m.execute(() -> m.gui.getChat().addMessage(fer == null ? Component.literal("Copied to clipboard").withStyle(ChatFormatting.GREEN)
							: Component.literal("Copy failed: " + fer).withStyle(ChatFormatting.RED)));
				});
				case "vw" -> m.setScreen(new viw(null, f));
				case "op" -> Util.getPlatform().openFile(f.getParentFile());
				case "dl" -> m.setScreen(new del(null, f));
				default -> {
				}
			}
		});
	}

	public static void cp(File f) throws Exception {
		if (!java.awt.GraphicsEnvironment.isHeadless()) {
			BufferedImage bi = ImageIO.read(f);
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new Transferable() {
				@Override
				public DataFlavor[] getTransferDataFlavors() {
					return new DataFlavor[] {DataFlavor.imageFlavor};
				}

				@Override
				public boolean isDataFlavorSupported(DataFlavor d) {
					return DataFlavor.imageFlavor.equals(d);
				}

				@Override
				public Object getTransferData(DataFlavor d) throws UnsupportedFlavorException {
					if (!isDataFlavorSupported(d)) throw new UnsupportedFlavorException(d);
					return bi;
				}
			}, null);
			return;
		}
		if (!System.getProperty("os.name", "").toLowerCase().contains("win")) throw new Exception("clipboard needs a desktop session");
		String p = f.getAbsolutePath().replace("'", "''");
		Process pr = new ProcessBuilder("powershell", "-NoProfile", "-NonInteractive", "-STA", "-WindowStyle", "Hidden", "-Command", "Add-Type -AssemblyName System.Windows.Forms,System.Drawing; $i = [System.Drawing.Image]::FromFile('" + p + "'); [System.Windows.Forms.Clipboard]::SetImage($i); $i.Dispose()").redirectErrorStream(true).start();
		if (!pr.waitFor(15, java.util.concurrent.TimeUnit.SECONDS)) {
			pr.destroyForcibly();
			throw new Exception("clipboard timed out");
		}
		if (pr.exitValue() != 0) throw new Exception("clipboard failed");
	}

	public static Component bar(String n) {
		return Component.literal("Screenshot saved  ").append(btn("[Copy]", "cp", n, "Copy to clipboard", 0x55FF55)).append(" ")
				.append(btn("[View]", "vw", n, "View in the viewer", 0x55FFFF)).append(" ")
				.append(btn("[Open]", "op", n, "Open the screenshots folder", 0xFFFF55)).append(" ")
				.append(btn("[Delete]", "dl", n, "Delete this screenshot", 0xFF5555));
	}

	static MutableComponent btn(String t, String a, String n, String h, int c) {
		return Component.literal(t).withStyle(s -> s.withClickEvent(new ClickEvent.RunCommand("snprb " + a + " " + n))
				.withHoverEvent(new HoverEvent.ShowText(Component.literal(h))).withColor(c).withUnderlined(true));
	}
}
