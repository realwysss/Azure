package dev.wscl.mixin;

import dev.wscl.AzOthers;
import dev.wscl.Cfg;
import dev.wscl.Gui;
import dev.wscl.Hud;
import dev.wscl.Skn;
import dev.wscl.Ui;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.packs.PackSelectionScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PauseScreen.class)
public abstract class PzMxn extends Screen {
	@Unique private Skn azs;
	@Unique private int azy;
	@Unique private int azx;
	@Unique private int azt;
	@Unique private int azw;
	@Unique private long azq;
	@Unique private Button azd;
	@Unique private Button azb;

	protected PzMxn(Component t) {
		super(t);
	}

	@Unique
	private static String key(AbstractWidget w) {
		return w.getMessage().getContents() instanceof TranslatableContents tc ? tc.getKey() : "";
	}

	@Inject(method = "init", at = @At("RETURN"))
	private void wsi(CallbackInfo ci) {
		if (!Cfg.c.btn) return;
		Screen self = this;
		List<AbstractWidget> keep = new ArrayList<>();
		List<AbstractWidget> others = new ArrayList<>();
		AbstractWidget back = null;
		AbstractWidget opt = null;
		AbstractWidget dis = null;
		for (GuiEventListener c : children()) {
			if (c instanceof StringWidget sw) {
				sw.visible = false;
				continue;
			}
			if (!(c instanceof AbstractWidget aw)) continue;
			if (aw.getMessage().getString().equals("Azure")) continue;
			String k = key(aw);
			if (k.equals("menu.returnToGame")) back = aw;
			else if (k.equals("menu.options")) opt = aw;
			else if (k.equals("menu.returnToMenu") || k.equals("menu.disconnect") || k.equals("menu.savingLevel")) dis = aw;
			else others.add(aw);
		}
		for (AbstractWidget o : others) {
			o.setX(-5000);
			o.setY(-5000);
		}
		List<AbstractWidget> rows = new ArrayList<>();
		if (back != null) rows.add(back);
		if (opt != null) rows.add(opt);
		rows.add(Button.builder(Component.literal("Resource Packs"), b -> minecraft.setScreen(new PackSelectionScreen(minecraft.getResourcePackRepository(), repo -> {
			minecraft.options.updateResourcePacks(repo);
			minecraft.setScreen(self);
		}, minecraft.getResourcePackDirectory(), Component.translatable("resourcePack.title")))).bounds(0, 0, 100, 20).build());
		if (net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded("modmenu")) rows.add(Button.builder(Component.literal("Mods"), b -> wsd(self)).bounds(0, 0, 100, 20).build());
		final List<AbstractWidget> oth = others;
		rows.add(Button.builder(Component.literal("Others"), b -> minecraft.setScreen(new AzOthers(self, oth))).bounds(0, 0, 100, 20).build());
		if (dis != null) rows.add(dis);
		for (AbstractWidget r : rows) if (!children().contains(r)) addRenderableWidget((Button) r);
		int n = rows.size();
		int rh = Math.max(16, Math.min(22, (height - 100) / n - 4));
		int total = n * (rh + 4) - 4;
		int lx = Math.max(24, Math.round(width * 0.09f));
		int rw = Math.min(170, Math.max(100, Math.round(width * 0.36f)));
		int y0 = Math.max(54, (height - total) / 2 + 8);
		azy = y0;
		azt = total;
		azw = rw;
		if (azq == 0) azq = System.currentTimeMillis();
		azx = lx;
		for (int i = 0; i < n; i++) {
			AbstractWidget b = rows.get(i);
			b.setX(lx);
			b.setY(y0 + i * (rh + 4));
			b.setWidth(rw);
			b.setHeight(rh);
		}
		azd = Button.builder(Component.empty(), b -> dev.wscl.Api.dc()).bounds(width - 30, 10, 20, 20).build();
		azb = Button.builder(Component.empty(), b -> minecraft.setScreen(Gui.open(self))).bounds(width - 56, 10, 20, 20).build();
		addRenderableWidget(azd);
		addRenderableWidget(azb);
		int sh = Math.round(height * 0.5f);
		azs = new Skn(Math.round(sh * 0.7f), sh, Math.round(width * 0.74f - sh * 0.35f), Math.round(height * 0.2f));
	}

	@Inject(method = "render", at = @At("HEAD"))
	private void wsh(GuiGraphics g, int mx, int my, float d, CallbackInfo ci) {
		if (!Cfg.c.btn) return;
		int ac = Hud.ac() & 0xFFFFFF;
		Ui.ts(g, "Paused", azx, azy - 32, 18f, 0xFFFFFF, 1f);
		Ui.rr(g, azx, azy - 11, 16, 2, 1, Ui.al(ac, 1f));
		long tsec = azq == 0 ? 0 : (System.currentTimeMillis() - azq) / 1000;
		Ui.tx(g, (tsec / 60) + ":" + (tsec % 60 < 10 ? "0" : "") + (tsec % 60), azx + Ui.w("Paused", 18f) + 10, azy - 22, ac, 0.8f);
		int lz = Math.min(height - 40, 190);
		g.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, net.minecraft.resources.Identifier.fromNamespaceAndPath("azure", "textures/gui/logo.png"), width - lz - 16, height - lz - 10, 0f, 0f, lz, lz, 256, 256, 256, 256, Ui.al(ac, 0.07f));
		int cy = azy + azt + 14;
		if (height - cy >= 28 && height - cy < 58 && width >= 380) {
			var pi2 = minecraft.getConnection() == null || minecraft.player == null ? null : minecraft.getConnection().getPlayerInfo(minecraft.player.getUUID());
			String sv2 = minecraft.getCurrentServer() != null ? minecraft.getCurrentServer().ip : "Singleplayer";
			Ui.rr(g, azx, cy, azw, 20, 8, Ui.al(0xFFFFFF, 0.05f));
			Ui.ro(g, azx, cy, azw, 20, 8, Ui.al(0xFFFFFF, 0.12f));
			String l2 = (sv2.length() > 14 ? sv2.substring(0, 14) : sv2) + "  " + minecraft.getFps() + " fps  " + (pi2 == null ? 0 : pi2.getLatency()) + " ms";
			Ui.tx(g, l2, azx + 8, cy + 6, ac, 0.9f);
		} else if (height - cy >= 58 && width >= 380) {
			var pi = minecraft.getConnection() == null || minecraft.player == null ? null : minecraft.getConnection().getPlayerInfo(minecraft.player.getUUID());
			String sv = minecraft.getCurrentServer() != null ? minecraft.getCurrentServer().ip : "Singleplayer";
			long sec = (System.currentTimeMillis() - azq) / 1000;
			Ui.rr(g, azx, cy, azw, 50, 9, Ui.al(0xFFFFFF, 0.05f));
			Ui.ro(g, azx, cy, azw, 50, 9, Ui.al(0xFFFFFF, 0.12f));
			Ui.tx(g, sv.length() > 22 ? sv.substring(0, 22) : sv, azx + 9, cy + 8, 0xFFFFFF, 0.95f);
			Ui.tx(g, minecraft.getFps() + " fps   " + (pi == null ? 0 : pi.getLatency()) + " ms", azx + 9, cy + 21, ac, 0.9f);
			Ui.tx(g, "Time paused " + sec / 60 + ":" + (sec % 60 < 10 ? "0" : "") + sec % 60, azx + 9, cy + 34, 0xFFFFFF, 0.45f);
		}
	}

	@Inject(method = "mouseClicked", at = @At("HEAD"))
	private void wsm(net.minecraft.client.input.MouseButtonEvent e, boolean dbl, org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable<Boolean> cir) {
		if (azs != null && azs.w.isMouseOver(e.x(), e.y())) azs.poke();
	}

	@Unique
	private void wsd(Screen s) {
		try {
			minecraft.setScreen((Screen) Class.forName("com.terraformersmc.modmenu.gui.ModsScreen").getConstructor(Screen.class).newInstance(s));
		} catch (Throwable e) {
		}
	}

	@Inject(method = "render", at = @At("RETURN"))
	private void wsr(GuiGraphics g, int mx, int my, float d, CallbackInfo ci) {
		if (!Cfg.c.btn) return;
		if (azd != null) dev.wscl.Ic.dr(g, "discord", azd.getX() + 10, azd.getY() + 10, 14, 0xFFFFFFFF);
		if (azb != null) g.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, net.minecraft.resources.Identifier.fromNamespaceAndPath("azure", "textures/gui/logo.png"), azb.getX() + 2, azb.getY() + 2, 0f, 0f, 16, 16, 256, 256, 256, 256, 0xFFFFFFFF);
		if (azs == null || width < 380) return;
		azs.render(g, mx, my, d);
	}
}
