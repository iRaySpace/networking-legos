package learn.netty;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.util.CharsetUtil;
import learn.netty.loop.BombLoop;
import learn.netty.loop.ExplodeLoop;
import learn.netty.loop.MovementLoop;
import learn.netty.map.Coordinate;
import learn.netty.map.Map;
import learn.netty.player.Bomb;
import learn.netty.player.Explosion;
import learn.netty.player.KeyCode;
import learn.netty.player.Player;

public class World {

    private Coordinate map[][];
    private Player players[];
    private List<Bomb> bombs;
    private List<Explosion> explosions;
    private int currentSlot;

    private MovementLoop movementLoop;
    private BombLoop bombLoop;
    private ExplodeLoop explodeLoop;

    public World() {
        map = new Coordinate[Map.MAX_ROW][Map.MAX_COL];
        players = new Player[Player.MAX_PLAYER];
        bombs = new ArrayList<>();
        explosions = new ArrayList<>();
        currentSlot = 0;

        // Game Loop System
        movementLoop = new MovementLoop(this);
        bombLoop = new BombLoop(this);
        explodeLoop = new ExplodeLoop(this);
    }

    public void setMap() {
        for (int i = 0; i < Map.MAX_ROW; i++) {
            for (int j = 0; j < Map.MAX_COL; j++) {
                map[i][j] = new Coordinate(
                        Map.SIZE_SPRITE_MAP * j,
                        Map.SIZE_SPRITE_MAP * i,
                        "block");
            }
        }

        for (int j = 1; j < Map.MAX_COL - 1; j++) {
            map[0][j].setImg("wall-center");
            map[Map.MAX_ROW - 1][j].setImg("wall-center");
        }
        for (int i = 1; i < Map.MAX_ROW - 1; i++) {
            map[i][0].setImg("wall-center");
            map[i][Map.MAX_COL - 1].setImg("wall-center");
        }

        map[0][0].setImg("wall-up-left");
        map[0][Map.MAX_COL - 1].setImg("wall-up-right");
        map[Map.MAX_ROW - 1][0].setImg("wall-down-left");
        map[Map.MAX_ROW - 1][Map.MAX_COL - 1].setImg("wall-down-right");

        for (int i = 2; i < Map.MAX_ROW - 2; i++) {
            for (int j = 2; j < Map.MAX_COL - 2; j++) {
                if (i % 2 == 0 && j % 2 == 0) {
                    map[i][j].setImg("wall-center");
                }
            }
        }

        map[1][1].setImg("floor-1");
        map[1][2].setImg("floor-1");
        map[2][1].setImg("floor-1");

        map[Map.MAX_ROW - 2][Map.MAX_COL - 2].setImg("floor-1");
        map[Map.MAX_ROW - 3][Map.MAX_COL - 2].setImg("floor-1");
        map[Map.MAX_ROW - 2][Map.MAX_COL - 3].setImg("floor-1");

        map[Map.MAX_ROW - 2][1].setImg("floor-1");
        map[Map.MAX_ROW - 3][1].setImg("floor-1");
        map[Map.MAX_ROW - 2][2].setImg("floor-1");

        map[1][Map.MAX_COL - 2].setImg("floor-1");
        map[2][Map.MAX_COL - 2].setImg("floor-1");
        map[1][Map.MAX_COL - 3].setImg("floor-1");
    }

    public void setPlayers() {
        players[0] = new Player(
                map[1][1].getX() - Map.VAR_X_SPRITES,
                map[1][1].getY() - Map.VAR_Y_SPRITES);

        players[1] = new Player(
                map[Map.MAX_ROW - 2][Map.MAX_COL - 2].getX() - Map.VAR_X_SPRITES,
                map[Map.MAX_ROW - 2][Map.MAX_COL - 2].getY() - Map.VAR_Y_SPRITES);

        players[2] = new Player(
                map[Map.MAX_ROW - 2][1].getX() - Map.VAR_X_SPRITES,
                map[Map.MAX_ROW - 2][1].getY() - Map.VAR_Y_SPRITES);

        players[3] = new Player(
                map[1][Map.MAX_COL - 2].getX() - Map.VAR_X_SPRITES,
                map[1][Map.MAX_COL - 2].getY() - Map.VAR_Y_SPRITES);
    }

    public Coordinate getCoordinate(int row, int col) {
        return map[row][col];
    }

    public void updateCoordinate(int row, int col, String img) {
        map[row][col].setImg(img);
        updateChannels("-1 mapUpdate " + img + " " + row + " " + col);
    }

    public Player getPlayer(int idx) {
        return players[idx];
    }

    public Player[] getPlayers() {
        return players;
    }

    public List<Bomb> getBombs() {
        return bombs;
    }

    public List<Explosion> getExplosions() {
        return explosions;
    }

    public int login(Channel channel) {
        final int id = generatePlayerId();
        players[id].setId(id);
        players[id].setAlive(true);
        players[id].setLogged(true);
        players[id].setChannel(channel);
        return id;
    }

    // TODO: make use of updateChannels
    public void notifyLogin(int newPlayerId) {
        for (Player player : players) {
            if (player.getId() == newPlayerId || player.getChannel() == null) {
                continue;
            }
            final String loginPackets = newPlayerId + " playerJoined";
            player.getChannel().writeAndFlush(Unpooled.copiedBuffer(loginPackets + "\r\n", CharsetUtil.UTF_8));
        }
    }

    public void handleAction(int playerId, String action, String... params) {
        if (action.equals("keyCodePressed")) {
            final int keyCode = Integer.parseInt(params[0]);
            this.players[playerId].pressKey(keyCode);
            updateChannels(playerId + " newStatus " + KeyCode.getDirection(keyCode));
        } else if (action.equals("keyCodeReleased")) {
            final int keyCode = Integer.parseInt(params[0]);
            this.players[playerId].releaseKey(keyCode);
            updateChannels(playerId + " stopStatusUpdate");
        } else if (action.equals("pressedSpace")) {
            final int x = this.players[playerId].getX() + (Map.WIDTH_SPRITE_PLAYER / 2);
            final int y = this.players[playerId].getY() + (2 * Map.HEIGHT_SPRITE_PLAYER / 3);

            final int col = x / Map.SIZE_SPRITE_MAP;
            final int row = y / Map.SIZE_SPRITE_MAP;

            bombs.add(new Bomb(col, row));
            updateCoordinate(row, col, "bomb-planted-1");
        }
    }

    public void start() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> this.handleGameLoop(), 0, 50, TimeUnit.MILLISECONDS);
    }

    public void updateChannels(String packets) {
        for (Player player : getPlayers()) {
            final Channel channel = player.getChannel();
            if (channel == null) {
                continue;
            }
            channel.writeAndFlush(Unpooled.copiedBuffer(packets + "\r\n", CharsetUtil.UTF_8));
        }
    }

    private int generatePlayerId() {
        final int id = this.currentSlot++;
        return id;
    }

    private void handleGameLoop() {
        movementLoop.run();
        bombLoop.run();
        explodeLoop.run();
    }

}
