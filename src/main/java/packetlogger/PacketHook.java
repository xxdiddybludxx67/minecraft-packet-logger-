package packetlogger;

import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import net.minecraft.network.NetworkManager;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import java.lang.reflect.Field;

public class PacketHook {

    private static final String HANDLER_NAME = "packetlogger_handler";

    @SubscribeEvent
    public void onClientConnect(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        try {
            NetworkManager nm = event.manager;
            Channel ch = getChannelFromNetworkManager(nm);
            if (ch == null) return;

            ChannelPipeline p = ch.pipeline();
            if (p.get(HANDLER_NAME) != null) {
                p.remove(HANDLER_NAME);
            }

            if (p.get("packet_handler") != null) {
                p.addBefore("packet_handler", HANDLER_NAME, new PacketLoggerHandler());
            } else {
                p.addLast(HANDLER_NAME, new PacketLoggerHandler());
            }

            System.out.println("[PacketLogger] Injected handler into channel: " + ch);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @SubscribeEvent
    public void onClientDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        try {
            NetworkManager nm = event.manager;
            Channel ch = getChannelFromNetworkManager(nm);
            if (ch == null) return;

            ChannelPipeline p = ch.pipeline();
            if (p.get(HANDLER_NAME) != null) {
                p.remove(HANDLER_NAME);
                System.out.println("[PacketLogger] Removed handler from channel.");
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private Channel getChannelFromNetworkManager(NetworkManager nm) {
        if (nm == null) return null;
        try {
            try {
                return (Channel) NetworkManager.class.getMethod("channel").invoke(nm);
            } catch (NoSuchMethodException ignored) { /* fallback to field */ }

            Field f = NetworkManager.class.getDeclaredField("channel");
            f.setAccessible(true);
            return (Channel) f.get(nm);
        } catch (Throwable t) {
            t.printStackTrace();
            return null;
        }
    }
}

