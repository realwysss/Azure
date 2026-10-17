package dev.htgl;

import net.minecraft.resources.Identifier;

public final class Glow {
	public static final String ID = "hitglow";

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(ID, path);
	}
}
