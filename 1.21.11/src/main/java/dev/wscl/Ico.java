package dev.wscl;

import java.nio.ByteBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

public class Ico {
	static boolean dn;

	public static void set(long win) {
		if (dn) return;
		dn = true;
		int[] sz = {16, 32, 48, 64, 128, 256};
		ByteBuffer[] raw = new ByteBuffer[sz.length];
		ByteBuffer[] px = new ByteBuffer[sz.length];
		try (MemoryStack st = MemoryStack.stackPush()) {
			GLFWImage.Buffer b = GLFWImage.malloc(sz.length, st);
			for (int i = 0; i < sz.length; i++) {
				byte[] d;
				try (var in = Ico.class.getResourceAsStream("/assets/azure/icon_" + sz[i] + ".png")) {
					d = in.readAllBytes();
				}
				raw[i] = MemoryUtil.memAlloc(d.length);
				raw[i].put(d).flip();
				int[] w = new int[1];
				int[] h = new int[1];
				int[] c = new int[1];
				px[i] = STBImage.stbi_load_from_memory(raw[i], w, h, c, 4);
				b.get(i).set(w[0], h[0], px[i]);
			}
			GLFW.glfwSetWindowIcon(win, b);
			for (int i = 0; i < sz.length; i++) {
				STBImage.stbi_image_free(px[i]);
				MemoryUtil.memFree(raw[i]);
			}
		} catch (Throwable t) {
		}
	}
}
