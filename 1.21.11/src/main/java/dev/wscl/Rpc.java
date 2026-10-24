package dev.wscl;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import net.minecraft.client.Minecraft;

public class Rpc {
	static volatile String det = "In the menus";
	static volatile String stt = "Azure Client";
	static volatile boolean want;
	static Thread th;
	static long t0 = System.currentTimeMillis() / 1000;
	public static volatile String status = "Off";

	static String q(String s) {
		return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
	}

	static int rt;
	static String lastRsp = "";

	public static void tick(Minecraft m) {
		if (rt++ % 20 != 0 && th != null && th.isAlive()) return;
		String id = Cfg.c.rid == null || Cfg.c.rid.isBlank() ? "1561051516851191838" : Cfg.c.rid.trim();
		want = Cfg.on("rpc") && !id.isEmpty();
		if (!Cfg.on("rpc")) status = "Off";
		else if (id.isEmpty()) status = "Paste an application id";
		String sv = m.level == null ? "in the menus" : m.getCurrentServer() != null ? (Cfg.gi("rpc.ip", 1) == 1 ? "on " + m.getCurrentServer().ip : "multiplayer") : "singleplayer";
		String ac = Cfg.gi("rpc.acc", 1) == 1 ? m.getUser().getName() : "";
		det = Cfg.c.rd1.replace("{server}", sv).replace("{account}", ac).trim();
		stt = Cfg.c.rd2.replace("{server}", sv).replace("{account}", ac).trim();
		if (want && (th == null || !th.isAlive())) {
			th = new Thread(Rpc::loop, "azure-rpc");
			th.setDaemon(true);
			th.start();
		}
	}

	static void send(RandomAccessFile f, int op, String js) throws Exception {
		byte[] b = js.getBytes(StandardCharsets.UTF_8);
		ByteBuffer bb = ByteBuffer.allocate(8 + b.length).order(ByteOrder.LITTLE_ENDIAN);
		bb.putInt(op);
		bb.putInt(b.length);
		bb.put(b);
		f.write(bb.array());
	}

	static String read(RandomAccessFile f) throws Exception {
		byte[] h = new byte[8];
		f.readFully(h);
		ByteBuffer bb = ByteBuffer.wrap(h).order(ByteOrder.LITTLE_ENDIAN);
		bb.getInt();
		int n = bb.getInt();
		byte[] d = new byte[Math.max(0, Math.min(n, 65536))];
		f.readFully(d);
		return new String(d, StandardCharsets.UTF_8);
	}

	static void loop() {
		while (want) {
			RandomAccessFile f = null;
			try {
				for (int i = 0; i < 10 && f == null; i++) {
					try {
						f = new RandomAccessFile("\\\\.\\pipe\\discord-ipc-" + i, "rw");
					} catch (Exception e) {
					}
				}
				if (f == null) {
					status = "Discord not running";
					Thread.sleep(10000);
					continue;
				}
				send(f, 0, "{\"v\":1,\"client_id\":" + q(Cfg.c.rid == null || Cfg.c.rid.isBlank() ? "1561051516851191838" : Cfg.c.rid.trim()) + "}");
				String r = read(f);
				if (r.contains("\"error\"") || r.contains("Invalid")) {
					status = "Invalid application id";
					f.close();
					Thread.sleep(15000);
					continue;
				}
				status = "Connected";
				while (want) {
					String js = "{\"cmd\":\"SET_ACTIVITY\",\"args\":{\"pid\":" + ProcessHandle.current().pid() + ",\"activity\":{" + (det.length() > 1 ? "\"details\":" + q(det) + "," : "") + (stt.length() > 1 ? "\"state\":" + q(stt) + "," : "") + (Cfg.gi("rpc.time", 1) == 1 ? "\"timestamps\":{\"start\":" + t0 + "}," : "") + "\"assets\":{\"large_image\":\"https://raw.githubusercontent.com/realwysss/Azure/main/1.21.11/src/main/resources/assets/azure/icon.png?v=2\",\"large_text\":\"Azure\"}}},\"nonce\":" + q(UUID.randomUUID().toString()) + "}";
					send(f, 1, js);
					String rsp = read(f);
					if (!rsp.equals(lastRsp)) {
						lastRsp = rsp;
						System.out.println("[azure rpc] " + rsp);
					}
					if (rsp.contains("\"evt\":\"ERROR\"")) {
						int mi = rsp.indexOf("\"message\":\"");
						status = "Discord says: " + (mi >= 0 ? rsp.substring(mi + 11, Math.min(rsp.length(), mi + 60)).replaceAll("\".*", "") : "error");
					} else status = "Connected";
					Thread.sleep(15000);
				}
				send(f, 1, "{\"cmd\":\"SET_ACTIVITY\",\"args\":{\"pid\":" + ProcessHandle.current().pid() + "},\"nonce\":" + q(UUID.randomUUID().toString()) + "}");
			} catch (Exception e) {
				status = "Reconnecting";
				try {
					Thread.sleep(8000);
				} catch (Exception x) {
				}
			} finally {
				try {
					if (f != null) f.close();
				} catch (Exception e) {
				}
			}
		}
	}
}
