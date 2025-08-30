package packetlogger;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.minecraft.network.Packet;

public class PacketLoggerHandler extends ChannelDuplexHandler {

    // Note: PacketLoggerMod.interceptor is expected to be non-null and set up in init()
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            if (msg instanceof Packet && PacketLoggerMod.interceptor != null) {
                // inbound from server -> client
                Packet<?> p = (Packet<?>) msg;
                PacketLoggerMod.interceptor.logIncomingPacket(p);
            }
        } catch (Throwable t) {
            // don't break pipeline on logging errors
            t.printStackTrace();
        }

        // continue chain
        super.channelRead(ctx, msg);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        try {
            if (msg instanceof Packet && PacketLoggerMod.interceptor != null) {
                // outbound from client -> server
                Packet<?> p = (Packet<?>) msg;
                PacketLoggerMod.interceptor.logOutgoingPacket(p);
            }
        } catch (Throwable t) {
            // don't break pipeline on logging errors
            t.printStackTrace();
        }

        // continue chain
        super.write(ctx, msg, promise);
    }
}
