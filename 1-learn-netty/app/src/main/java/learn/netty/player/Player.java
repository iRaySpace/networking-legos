package learn.netty.player;

import java.util.HashSet;
import java.util.Set;

import io.netty.channel.Channel;
import learn.netty.map.Map;

public class Player {

    public static final int MAX_PLAYER = 4;
    public static final int WIDTH_SPRITE_PLAYER = 22 * Map.RESIZE;
    public static final int HEIGHT_SPRITE_PLAYER = 33 * Map.RESIZE;
    public static final int SPEED = 6;

    private int id;

    private Channel channel;
    private boolean isLogged;
    private boolean isAlive;

    private int x;
    private int y;

    private Set<Integer> pressedKeys;

    public Player(int x, int y) {
        this.x = x;
        this.y = y;
        this.id = -1;
        this.isLogged = false;
        this.isAlive = false;
        this.pressedKeys = new HashSet<>();
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public boolean isLogged() {
        return isLogged;
    }

    public void setLogged(boolean isLogged) {
        this.isLogged = isLogged;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean isAlive) {
        this.isAlive = isAlive;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void pressKey(int keyCode) {
        this.pressedKeys.add(keyCode);
    }

    public void releaseKey(int keyCode) {
        this.pressedKeys.remove(keyCode);
    }

    public boolean isKeyPressed(int keyCode) {
        return this.pressedKeys.contains(keyCode);
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public Channel getChannel() {
        return this.channel;
    }

}
