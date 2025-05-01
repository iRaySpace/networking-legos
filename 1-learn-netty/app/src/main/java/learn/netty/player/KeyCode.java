package learn.netty.player;

public class KeyCode {
    public static final int ARROW_LEFT = 37;
    public static final int ARROW_UP = 38;
    public static final int ARROW_RIGHT = 39;
    public static final int ARROW_DOWN = 40;

    public static String getDirection(int keyCode) {
        switch (keyCode) {
            case ARROW_UP:
                return "up";
            case ARROW_DOWN:
                return "down";
            case ARROW_LEFT:
                return "left";
            case ARROW_RIGHT:
                return "right";
            default:
                return "";
        }
    }
}
