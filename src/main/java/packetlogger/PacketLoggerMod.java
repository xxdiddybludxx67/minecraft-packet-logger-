package packetlogger;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.common.MinecraftForge;

import java.io.File;

@Mod(modid = PacketLoggerMod.MODID, name = PacketLoggerMod.NAME, version = PacketLoggerMod.VERSION)
public class PacketLoggerMod {

    public static final String MODID = "packetlogger";
    public static final String NAME = "PacketLogger";
    public static final String VERSION = "1.0";

    public static File logFolder;

    public static LogWriter logWriter = new LogWriter();
    public static PacketInterceptor interceptor;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        String userHome = System.getProperty("user.home");
        logFolder = new File(userHome, "MinecraftPacketLoggerLogs");

        if (!logFolder.exists()) {
            if (logFolder.mkdirs()) {
                System.out.println("[PacketLogger] Created packetlogs folder at: " + logFolder.getAbsolutePath());
            } else {
                System.out.println("[PacketLogger] Failed to create packetlogs folder at: " + logFolder.getAbsolutePath());
            }
        } else {
            System.out.println("[PacketLogger] Using existing packetlogs folder at: " + logFolder.getAbsolutePath());
        }

        interceptor = new PacketInterceptor(logWriter);
        MinecraftForge.EVENT_BUS.register(interceptor);

        MinecraftForge.EVENT_BUS.register(new ChatCommandHandler());

        System.out.println("[PacketLogger] Initialized successfully.");
    }

    public static void log(String message) {
        System.out.println("[PacketLogger] " + message);
    }
}

