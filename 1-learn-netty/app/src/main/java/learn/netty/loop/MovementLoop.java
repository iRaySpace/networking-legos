package learn.netty.loop;

import java.util.ArrayList;
import java.util.List;

import learn.netty.Server;
import learn.netty.World;
import learn.netty.map.Map;
import learn.netty.player.KeyCode;
import learn.netty.player.Player;

public class MovementLoop {

    private World world;

    public MovementLoop(World world) {
        this.world = world;
    }

    public void run() {
        for (Player player : world.getPlayers()) {
            if (!player.isAlive()) {
                continue;
            }

            final int oldX = player.getX();
            final int oldY = player.getY();

            int newX = player.getX();
            int newY = player.getY();

            boolean dirty = false;

            if (player.isKeyPressed(KeyCode.ARROW_UP)) {
                dirty = true;
                newY = newY - Player.SPEED;
            } else if (player.isKeyPressed(KeyCode.ARROW_DOWN)) {
                dirty = true;
                newY = newY + Player.SPEED;
            } else if (player.isKeyPressed(KeyCode.ARROW_LEFT)) {
                dirty = true;
                newX = newX - Player.SPEED;
            } else if (player.isKeyPressed(KeyCode.ARROW_RIGHT)) {
                dirty = true;
                newX = newX + Player.SPEED;
            }

            if (dirty && coordinateIsValid(oldX, oldY, newX, newY)) {
                player.setX(newX);
                player.setY(newY);
                world.updateChannels(player.getId() + " newCoordinate " + newX + " " + newY);
            }
        }
    }

    private int getColumnOfMap(int x) {
        return x / Map.SIZE_SPRITE_MAP;
    }

    private int getLineOfMap(int y) {
        return y / Map.SIZE_SPRITE_MAP;
    }

    private boolean coordinateIsValid(int oldX, int oldY, int newX, int newY) {
        int x[] = new int[4], y[] = new int[4];
        int c[] = new int[4], l[] = new int[4];

        x[0] = Map.VAR_X_SPRITES + newX + Map.RESIZE;
        y[0] = Map.VAR_Y_SPRITES + newY + Map.RESIZE;

        x[1] = Map.VAR_X_SPRITES + newX + Map.SIZE_SPRITE_MAP - 2 * Map.RESIZE;
        y[1] = Map.VAR_Y_SPRITES + newY + Map.RESIZE;

        x[2] = Map.VAR_X_SPRITES + newX + Map.RESIZE;
        y[2] = Map.VAR_Y_SPRITES + newY + Map.SIZE_SPRITE_MAP - 2 * Map.RESIZE;

        x[3] = Map.VAR_X_SPRITES + newX + Map.SIZE_SPRITE_MAP - 2 * Map.RESIZE;
        y[3] = Map.VAR_Y_SPRITES + newY + Map.SIZE_SPRITE_MAP - 2 * Map.RESIZE;

        for (int i = 0; i < 4; i++) {
            c[i] = getColumnOfMap(x[i]);
            l[i] = getLineOfMap(y[i]);
        }

        if ((world.getCoordinate(l[0], c[0]).getImg().equals("floor-1")
                || world.getCoordinate(l[0], c[0]).getImg().contains("explosion")) &&
                (world.getCoordinate(l[1], c[1]).getImg().equals("floor-1")
                        || world.getCoordinate(l[1], c[1]).getImg().contains("explosion"))
                &&
                (world.getCoordinate(l[2], c[2]).getImg().equals("floor-1")
                        || world.getCoordinate(l[2], c[2]).getImg().contains("explosion"))
                &&
                (world.getCoordinate(l[3], c[3]).getImg().equals("floor-1")
                        || world.getCoordinate(l[3], c[3]).getImg().contains("explosion"))) {
            return true;
        }

        if ((world.getCoordinate(l[0], c[0]).getImg().contains("block")
                || world.getCoordinate(l[0], c[0]).getImg().contains("wall")) ||
                (world.getCoordinate(l[1], c[1]).getImg().contains("block")
                        || world.getCoordinate(l[1], c[1]).getImg().contains("wall"))
                ||
                (world.getCoordinate(l[2], c[2]).getImg().contains("block")
                        || world.getCoordinate(l[2], c[2]).getImg().contains("wall"))
                ||
                (world.getCoordinate(l[3], c[3]).getImg().contains("block")
                        || world.getCoordinate(l[3], c[3]).getImg().contains("wall"))) {
            return false;
        }

        // If bombs are planted
        x[0] = Map.VAR_X_SPRITES + oldX + Map.RESIZE;
        y[0] = Map.VAR_Y_SPRITES + oldY + Map.RESIZE;

        x[1] = Map.VAR_X_SPRITES + oldX + Map.SIZE_SPRITE_MAP - 2 * Map.RESIZE;
        y[1] = Map.VAR_Y_SPRITES + oldY + Map.RESIZE;

        x[2] = Map.VAR_X_SPRITES + oldX + Map.RESIZE;
        y[2] = Map.VAR_Y_SPRITES + oldY + Map.SIZE_SPRITE_MAP - 2 * Map.RESIZE;

        x[3] = Map.VAR_X_SPRITES + oldX + Map.SIZE_SPRITE_MAP - 2 * Map.RESIZE;
        y[3] = Map.VAR_Y_SPRITES + oldY + Map.SIZE_SPRITE_MAP - 2 * Map.RESIZE;

        for (int i = 0; i < 4; i++) {
            c[i] = getColumnOfMap(x[i]);
            l[i] = getLineOfMap(y[i]);
        }

        if (world.getCoordinate(l[0], c[0]).getImg().contains("bomb-planted") ||
                world.getCoordinate(l[1], c[1]).getImg().contains("bomb-planted") ||
                world.getCoordinate(l[2], c[2]).getImg().contains("bomb-planted") ||
                world.getCoordinate(l[3], c[3]).getImg().contains("bomb-planted")) {
            return true; // estava sobre uma bomba que acabou de platar, precisa sair
        }

        return false;
    }
}
