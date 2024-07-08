package net.rsprox.proxy.client

import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import net.rsprox.proxy.binary.StreamDirection
import net.rsprox.proxy.channel.getBinaryBlob
import net.rsprox.proxy.client.prot.GameClientProt
import net.rsprox.proxy.util.Packet

public class ClientGameHandler(
    private val serverChannel: Channel,
) : SimpleChannelInboundHandler<Packet<GameClientProt>>() {
    override fun channelRead0(
        ctx: ChannelHandlerContext,
        msg: Packet<GameClientProt>,
    ) {
        ctx.channel().getBinaryBlob().append(StreamDirection.ClientToServer, msg.encode(ctx.alloc(), mod = false))
        serverChannel.writeAndFlush(msg.encode(ctx.alloc()))
    }
}
