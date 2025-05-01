package learn.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

public class ServerInitializer extends ChannelInitializer<SocketChannel> {

    private World world;

    public ServerInitializer() {
        world = new World();
        world.setMap();
        world.setPlayers();
        world.start();
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(new ChannelHandler(world));
    }

}
