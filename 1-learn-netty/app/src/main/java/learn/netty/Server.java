package learn.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.MultiThreadIoEventLoopGroup;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class Server {

    private int port;

    public Server(int port) {
        this.port = port;
    }

    public void start() {
        final EventLoopGroup parentGroup = new MultiThreadIoEventLoopGroup(NioIoHandler.newFactory());
        final EventLoopGroup childGroup = new MultiThreadIoEventLoopGroup(NioIoHandler.newFactory());
        final ServerBootstrap bootstrap = new ServerBootstrap()
            .group(parentGroup, childGroup)
            .channel(NioServerSocketChannel.class)
            .childHandler(new ServerInitializer());

        try {
            final Channel serverChannel = bootstrap.bind(port).sync().channel();
            System.out.println("Server is running...");

            serverChannel.closeFuture().sync();
            System.out.println("Server has been shut down...");
        } catch (InterruptedException ex) {
            System.out.println("Something is wrong!");
        } finally {
            parentGroup.shutdownGracefully();
            childGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new Server(8383).start();
    }

}
