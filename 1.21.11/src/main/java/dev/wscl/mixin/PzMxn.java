package dev.wscl.mixin;

import dev.wscl.Cfg;
import dev.wscl.Hud;
import dev.wscl.Ui;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.PlayerSkinWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PauseScreen.class)
public abstract class PzMxn extends Screen {
	@Unique private PlayerSkinWidget azs;
	@Unique private float yaw = 20f;
	@Unique private float pit;
	@Unique private int lmx = -1;
	@Unique private int lmy = -1;
	@Unique private long lmv;
	@Unique private long lft;
	@Unique private int azy;
	@Unique private int azx;

	protected PzMxn(Component t) {
		super(t);
	}

	@Inject(method = "init", at = @At("RETURN"))
	private void wsi(CallbackInfo ci) {
		if (!Cfg.c.btn) return;
		List<AbstractWidget> bs = new ArrayList<>();
		for (GuiEventListener c : children()) {
			if (c instanceof StringWidget sw) sw.visible = false;
			else if (c instanceof AbstractWidget aw && !aw.getMessage().getString().equals("Azure")) bs.add(aw);
		}
		int n = Math.max(1, bs.size());
		int rh = Math.max(16, Math.min(22, (height - 100) / n - 4));
		int total = n * (rh + 4) - 4;
		int lx = Math.max(24, Math.round(width * 0.09f));
		int rw = Math.min(170, Math.max(100, Math.round(width * 0.36f)));
		int y0 = Math.max(54, (height - total) / 2 + 8);
		azy = y0;
		azx = lx;
		for (int i = 0; i < bs.size(); i++) {
			AbstractWidget b = bs.get(i);
			b.setX(lx);
			b.setY(y0 + i * (rh + 4));
			b.setWidth(rw);
			b.setHeight(rh);
		}
		int sh = Math.round(height * 0.5f);
		var m = minecraft;
		azs = new PlayerSkinWidget(Math.round(sh * 0.7f), sh, m.getEntityModels(), m.getSkinManager().createLookup(m.getGameProfile(), false));
		azs.setPosition(Math.round(width * 0.74f - sh * 0.35f), Math.round(height * 0.2f));
		lmv = Util.getMillis();
		lft = lmv;
	}

	@Inject(method = "render", at = @At("HEAD"))
	private void wsh(GuiGraphics g, int mx, int my, float d, CallbackInfo ci) {
		if (!Cfg.c.btn) return;
		int ac = Hud.ac() & 0xFFFFFF;
		Ui.ts(g, "Paused", azx, azy - 32, 18f, 0xFFFFFF, 1f);
		Ui.rr(g, azx, azy - 11, 16, 2, 1, Ui.al(ac, 1f));
	}

	@Inject(method = "render", at = @At("RETURN"))
	private void wsr(GuiGraphics g, int mx, int my, float d, CallbackInfo ci) {
		if (!Cfg.c.btn || azs == null || width < 380) return;
		if (mx != lmx || my != lmy) {
			lmx = mx;
			lmy = my;
			lmv = Util.getMillis();
		}
		long now = Util.getMillis();
		float dt = Math.min(0.1f, (now - lft) / 1000f);
		lft = now;
		boolean idle = now - lmv > 350;
		float cx = azs.getX() + azs.getWidth() / 2f;
		float cy = azs.getY() + azs.getHeight() * 0.35f;
		if (idle) yaw += dt * 38f;
		else {
			float tg = 20f + (mx - cx) * 0.55f;
			float df = ((tg - yaw) % 360f + 540f) % 360f - 180f;
			yaw += df * Math.min(1f, dt * 9f);
		}
		float pt = Math.max(-25f, Math.min(25f, idle ? -4f : (my - cy) * 0.2f));
		pit += (pt - pit) * Math.min(1f, dt * 9f);
		((PsMxn) azs).ry(yaw);
		((PsMxn) azs).rx(pit);
		azs.render(g, mx, my, d);
	}
}
