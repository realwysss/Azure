package dev.tpgl.mixin;

import net.minecraft.client.particle.Particle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Particle.class)
public interface PtAcc {
	@Accessor("x")
	double x();

	@Accessor("y")
	double y();

	@Accessor("z")
	double z();

	@Accessor("xo")
	double xo();

	@Accessor("yo")
	double yo();

	@Accessor("zo")
	double zo();
}
