package packetlogger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LogWriter {

    private final Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
    private FileWriter writer;
    private String currentPath;

    public synchronized void openNewSession() {
        close();

        File base = PacketLoggerMod.logFolder;
        if (base == null || !base.exists()) {
            base = new File(System.getProperty("user.home"), "MinecraftPacketLoggerLogs");
            if (!base.exists()) {
                boolean created = base.mkdirs();
                System.out.println("[PacketLogger] Folder created at: " + base.getAbsolutePath() + " -> " + created);
            }
            PacketLoggerMod.logFolder = base;
        }

        String stamp = new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US).format(new Date());
        File out = new File(base, "session-" + stamp + ".json");

        try {
            writer = new FileWriter(out, true);
            currentPath = out.getAbsolutePath();

            JsonObject header = new JsonObject();
            header.addProperty("_session", stamp);
            header.addProperty("logFolder", base.getAbsolutePath());
            write(header);

            System.out.println("[PacketLogger] Logging session started: " + currentPath);
        } catch (IOException e) {
            currentPath = null;
            e.printStackTrace();
        }
    }

    public synchronized void write(JsonObject obj) {
        if (writer == null) return;
        try {
            writer.write(gson.toJson(obj));
            writer.write(",\n");
            writer.flush();
            System.out.println("[PacketLogger DEBUG] Packet logged: " + obj.get("packet").getAsString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void close() {
        if (writer != null) {
            try {
                writer.flush();
                writer.close();
                System.out.println("[PacketLogger] Logging session closed: " + currentPath);
            } catch (IOException ignored) {}
            writer = null;
        }
    }

    public synchronized String getCurrentPath() {
        return currentPath == null ? "(none)" : currentPath;
    }

    public synchronized File getLogFolder() {
        return PacketLoggerMod.logFolder;
    }
}
