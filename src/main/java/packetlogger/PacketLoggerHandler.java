package packetlogger;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.minecraft.network.Packet;

public class PacketLoggerHandler extends ChannelDuplexHandler {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            if (msg instanceof Packet && PacketLoggerMod.interceptor != null) {
                // inbound from server -> client
                Packet<?> p = (Packet<?>) msg;
                PacketLoggerMod.interceptor.logIncomingPacket(p);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }

        super.channelRead(ctx, msg);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        try {
            if (msg instanceof Packet && PacketLoggerMod.interceptor != null) {
                Packet<?> p = (Packet<?>) msg;
                PacketLoggerMod.interceptor.logOutgoingPacket(p);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }

        super.write(ctx, msg, promise);
    }
}

