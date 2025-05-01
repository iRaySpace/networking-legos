package learn.netty;

import java.util.Arrays;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import learn.netty.map.Map;
import learn.netty.player.Player;

public class ChannelHandler extends ChannelInboundHandlerAdapter {

    private World world;
    private int playerId;

    public ChannelHandler(World world) {
        this.world = world;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("New connection: " + ctx.channel().remoteAddress());
        playerId = world.login(ctx.channel());
        world.notifyLogin(playerId);
        sendState(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        final ByteBuf byteBuf = (ByteBuf) msg;
        handlePackets(byteBuf.toString(CharsetUtil.UTF_8));
        byteBuf.release(); // RELEASEEE THE KRAKEEEN!
    }

    private void sendState(ChannelHandlerContext ctx) {
        final StringBuilder sb = new StringBuilder();

        // Player Id
        sb.append("0");

        for (int i = 0; i < Map.MAX_ROW; i++) {
            for (int j = 0; j < Map.MAX_COL; j++) {
                sb.append(" " + world.getCoordinate(i, j).getImg());
            }
        }

        for (int i = 0; i < Player.MAX_PLAYER; i++) {
            sb.append(" " + world.getPlayer(i).isAlive());
        }

        for (int i = 0; i < Player.MAX_PLAYER; i++) {
            final Player player = world.getPlayer(i);
            sb.append(" " + player.getX() + " " + player.getY());
        }

        sb.append("\r\n");

        ctx.writeAndFlush(Unpooled.copiedBuffer(sb, CharsetUtil.UTF_8));
    }

    private void handlePackets(String packets) {
        final String[] splitPackets = packets.trim().split(" ");
        final String action = splitPackets[0];
        final String[] params = Arrays.copyOfRange(splitPackets, 1, splitPackets.length);
        world.handleAction(playerId, action, params);
    }

}
