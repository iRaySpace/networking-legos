package chat.server;

import java.util.logging.Logger;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.GlobalEventExecutor;

@Sharable
public class ServerHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOG = Logger.getLogger(ServerHandler.class.getName());

    private ChannelGroup channels;

    public ServerHandler() {
        channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        final Channel channel = ctx.channel();
        channels.add(channel);

        LOG.info("New connection from " + channel.remoteAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        final Channel channel = ctx.channel();
        channels.remove(channel);

        LOG.info("Disconnected from " + channel.remoteAddress());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        final ByteBuf buf = (ByteBuf) msg;
        try {
            final String data = buf.toString(CharsetUtil.UTF_8).trim();
            LOG.info("Data received: " + data);

            updateChannels(data);
            LOG.info("Sent data to all clients: " + data);
        } finally {
            ReferenceCountUtil.release(buf);
        }
    }

    private void updateChannels(String data) {
        final String sendData = data + "\r\n";
        channels.writeAndFlush(Unpooled.copiedBuffer(sendData, CharsetUtil.UTF_8));
    }
}
