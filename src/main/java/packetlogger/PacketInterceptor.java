package packetlogger;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import net.minecraft.network.Packet;
import net.minecraft.network.NetworkManager;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PacketInterceptor {

    private final LogWriter logWriter;
    private boolean loggingEnabled = false;

    public PacketInterceptor(LogWriter logWriter) {
        this.logWriter = logWriter;
    }

    @SubscribeEvent
    public void onConnect(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        NetworkManager manager = event.manager;
        inject(manager);
    }

    @SubscribeEvent
    public void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        remove(event.manager);
    }

    private void inject(NetworkManager manager) {
        try {
            ChannelPipeline pipeline = manager.channel().pipeline();
            if (pipeline.get("packet_logger") == null) {
                pipeline.addBefore("packet_handler", "packet_logger", new ChannelDuplexHandler() {
                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        if (msg instanceof Packet) {
                            logIncomingPacket((Packet<?>) msg);
                        }
                        super.channelRead(ctx, msg);
                    }

                    @Override
                    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                        if (msg instanceof Packet) {
                            logOutgoingPacket((Packet<?>) msg);
                        }
                        super.write(ctx, msg, promise);
                    }
                });
                System.out.println("[PacketLogger] Netty pipeline injected!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void remove(NetworkManager manager) {
        try {
            ChannelPipeline pipeline = manager.channel().pipeline();
            if (pipeline.get("packet_logger") != null) {
                pipeline.remove("packet_logger");
                System.out.println("[PacketLogger] Netty pipeline removed!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void logIncomingPacket(Packet<?> packet) {
        if (loggingEnabled) {
            ensureSessionOpen();
            logPacket("SERVER → CLIENT", packet);
        }
    }

    public void logOutgoingPacket(Packet<?> packet) {
        if (loggingEnabled) {
            ensureSessionOpen();
            logPacket("CLIENT → SERVER", packet);
        }
    }

    private void logPacket(String direction, Packet<?> packet) {
        JsonObject obj = new JsonObject();
        obj.addProperty("time", new SimpleDateFormat("HH:mm:ss.SSS", Locale.US).format(new Date()));
        obj.addProperty("direction", direction);
        obj.addProperty("packet", packet.getClass().getName());

        JsonObject fieldsObj = new JsonObject();
        try {
            for (Field field : packet.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                Object value = field.get(packet);
                fieldsObj.add(field.getName(), new JsonPrimitive(value != null ? value.toString() : "null"));
            }
        } catch (Exception e) {
            fieldsObj.addProperty("error", "Failed to extract fields: " + e.getMessage());
        }
        obj.add("fields", fieldsObj);

        logWriter.write(obj);
    }

    private void ensureSessionOpen() {
        if (logWriter.getCurrentPath().equals("(none)")) {
            logWriter.openNewSession();
        }
    }

    public boolean isLoggingEnabled() {
        return loggingEnabled;
    }

    public void setLoggingEnabled(boolean enabled) {
        this.loggingEnabled = enabled;
        if (enabled) {
            logWriter.openNewSession();
        } else {
            logWriter.close();
        }
    }
}
