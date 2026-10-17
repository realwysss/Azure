package dev.nsfov;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class Nsf {
	public static final Identifier ID = Identifier.withDefaultNamespace("effect.speed");
	public static final Identifier SL = Identifier.withDefaultNamespace("effect.slowness");

	static boolean sk(AttributeModifier m) {
		return m.id().equals(ID) || m.id().equals(SL);
	}

	public static boolean has(AttributeInstance in) {
		return in.getModifier(ID) != null || in.getModifier(SL) != null;
	}

	public static double val(AttributeInstance in) {
		double b = in.getBaseValue();
		for (AttributeModifier m : in.getModifiers()) if (m.operation() == Operation.ADD_VALUE && !sk(m)) b += m.amount();
		double v = b;
		for (AttributeModifier m : in.getModifiers()) if (m.operation() == Operation.ADD_MULTIPLIED_BASE && !sk(m)) v += b * m.amount();
		for (AttributeModifier m : in.getModifiers()) if (m.operation() == Operation.ADD_MULTIPLIED_TOTAL && !sk(m)) v *= 1.0 + m.amount();
		return in.getAttribute().value().sanitizeValue(v);
	}
}
