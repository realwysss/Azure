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
		boolean mods = false;
		try {
			Class.forName("com.terraformersmc.modmenu.gui.ModsScreen");
			mods = true;
		} catch (Throwable t) {
		}
		List<AbstractWidget> rows = new ArrayList<>();
		if (back != null) rows.add(back);
		rows.add(Button.builder(Component.literal("Resource Packs"), b -> minecraft.setScreen(new PackSelectionScreen(minecraft.getResourcePackRepository(), repo -> {
			minecraft.options.updateResourcePacks(repo);
			minecraft.setScreen(self);
		}, minecraft.getResourcePackDirectory(), Component.translatable("resourcePack.title")))).bounds(0, 0, 100, 20).build());
		rows.add(Button.builder(Component.literal("Azure"), b -> minecraft.setScreen(Gui.open(self))).bounds(0, 0, 100, 20).build());
		if (opt != null) rows.add(opt);
		if (mods) rows.add(Button.builder(Component.literal("Mods"), b -> {
			try {
				minecraft.setScreen((Screen) Class.forName("com.terraformersmc.modmenu.gui.ModsScreen").getConstructor(Screen.class).newInstance(self));
			} catch (Throwable t) {
			}
		}).bounds(0, 0, 100, 20).build());
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
		azx = lx;
		for (int i = 0; i < n; i++) {
			AbstractWidget b = rows.get(i);
			b.setX(lx);
			b.setY(y0 + i * (rh + 4));
			b.setWidth(rw);
			b.setHeight(rh);
		}
		int sh = Math.round(height * 0.5f);
		azs = new Skn(Math.round(sh * 0.7f), sh, Math.round(width * 0.74f - sh * 0.35f), Math.round(height * 0.2f));
	}

	@Inject(method = "render", at = @At("HEAD"))
	private void wsh(GuiGraphics g, int mx, int my, float d, CallbackInfo ci) {
		if (!Cfg.c.btn) return;
		Ui.ts(g, "Paused", azx, azy - 32, 18f, 0xFFFFFF, 1f);
	}

	@Inject(method = "render", at = @At("RETURN"))
	private void wsr(GuiGraphics g, int mx, int my, float d, CallbackInfo ci) {
		if (!Cfg.c.btn || azs == null || width < 380) return;
		azs.render(g, mx, my, d);
	}
}
