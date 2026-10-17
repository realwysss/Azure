package dev.tpgl;

import net.minecraft.resources.Identifier;

public final class Glow {
	public static final String ID = "totemparticleglow";

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(ID, path);
	}
}
