package dev.htcl.mixin;

import dev.htcl.HcSt;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LivingEntityRenderState.class)
public abstract class LvStMxn implements HcSt {
	@Unique
	private float hcp = 1f;
	@Unique
	private boolean hcs;

	@Override
	public float hcP() {
		return hcp;
	}

	@Override
	public boolean hcSelf() {
		return hcs;
	}

	@Unique
	private boolean hcf;

	@Override
	public boolean hcFig() {
		return hcf;
	}

	@Override
	public void hcFig(boolean f) {
		hcf = f;
	}

	@Override
	public void hcSet(float p, boolean s) {
		hcp = p;
		hcs = s;
	}
}
