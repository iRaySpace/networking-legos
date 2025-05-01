package learn.netty.player;

public class Explosion {

    public static final String EXPLOSION_ANIMS[] = {
            "1", "2", "3", "4", "5", "4", "3", "4", "5", "4", "3", "4", "5", "4", "3", "4", "5", "4", "3", "2", "1"
    };

    public static final String EXPLOSION_BLOCK_ANIMS[] = {
            "1", "2", "1", "2", "1", "2", "3", "4", "5", "6"
    };

    private int x;
    private int y;
    private int animIdx;
    private String type;
    private boolean isFinished;

    public Explosion(int x, int y, String type) {
        this.x = x;
        this.y = y;
        this.type = type;

        this.animIdx = 0;
        this.isFinished = false;
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

    public String getType() {
        return type;
    }

    public int getAnimIdx() {
        return this.animIdx;
    }

    public void setAnimIdx(int animIdx) {
        this.animIdx = animIdx;
    }

    public void finish() {
        this.isFinished = true;
    }

    public boolean isFinished() {
        return this.isFinished;
    }

}
