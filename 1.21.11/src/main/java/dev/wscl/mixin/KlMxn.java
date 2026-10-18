package dev.wscl.mixin;

import dev.wscl.Kbf;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.screens.options.controls.KeyBindsList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyBindsList.class)
public abstract class KlMxn extends ContainerObjectSelectionList<KeyBindsList.Entry> implements Kbf {
	@Unique private List<KeyBindsList.Entry> azall;

	protected KlMxn(Minecraft m, int a, int b, int c, int d) {
		super(m, a, b, c, d);
	}

	@Inject(method = "<init>", at = @At("RETURN"))
	private void wsi(CallbackInfo ci) {
		azall = new ArrayList<>(children());
	}

	@Override
	public void azf(String q) {
		if (azall == null) return;
		String k = q.toLowerCase().trim();
		List<KeyBindsList.Entry> o = new ArrayList<>();
		KeyBindsList.Entry cat = null;
		for (KeyBindsList.Entry e : azall) {
			if (e instanceof KeAcc ke) {
				String nm = ke.name().getString().toLowerCase();
				String ky = ke.key().getTranslatedKeyMessage().getString().toLowerCase();
				if (k.isEmpty() || nm.contains(k) || ky.contains(k)) {
					if (cat != null) {
						o.add(cat);
						cat = null;
					}
					o.add(e);
				}
			} else {
				cat = e;
				if (k.isEmpty()) {
					o.add(e);
					cat = null;
				}
			}
		}
		replaceEntries(o);
		setScrollAmount(0);
	}
}
