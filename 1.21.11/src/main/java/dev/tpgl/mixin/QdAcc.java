package dev.tpgl.mixin;

import net.minecraft.client.particle.SingleQuadParticle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SingleQuadParticle.class)
public interface QdAcc {
	@Accessor("rCol")
	float r();

	@Accessor("gCol")
	float g();

	@Accessor("bCol")
	float b();

	@Accessor("alpha")
	float al();
}
