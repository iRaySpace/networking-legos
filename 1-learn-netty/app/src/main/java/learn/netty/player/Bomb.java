package learn.netty.player;

public class Bomb {

    public static final String BOMB_PLANTED_ANIMS[] = {
            "1", "2", "3", "2", "1", "2", "3", "2", "1", "2", "3", "2", "1", "2",
            "red-3", "red-2", "red-1", "red-2", "red-3", "red-2", "red-3", "red-2", "red-3", "red-2", "red-3"
    };

    private int x;
    private int y;
    private int animIdx;
    private boolean isExploded;

    public Bomb(int x, int y) {
        this.x = x;
        this.y = y;
        this.animIdx = 0;
        this.isExploded = false;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getX() {
        return x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getY() {
        return y;
    }

    public int getAnimIdx() {
        return this.animIdx;
    }

    public void setAnimIdx(int animIdx) {
        this.animIdx = animIdx;
    }

    public void explode() {
        this.isExploded = true;
    }

    public boolean isExploded() {
        return this.isExploded;
    }

}
