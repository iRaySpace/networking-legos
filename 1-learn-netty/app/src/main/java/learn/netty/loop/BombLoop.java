package learn.netty.loop;

import learn.netty.World;
import learn.netty.map.Coordinate;
import learn.netty.player.Bomb;
import learn.netty.player.Explosion;

public class BombLoop {

    private World world;

    public BombLoop(World world) {
        this.world = world;
    }

    public void run() {
        world.getBombs().forEach(bomb -> {
            if (bomb.isExploded()) {
                return;
            }

            // Display
            final int animIdx = bomb.getAnimIdx();
            final String anim = Bomb.BOMB_PLANTED_ANIMS[animIdx];
            world.updateCoordinate(bomb.getY(), bomb.getX(), "bomb-planted-" + anim);

            // Update Anim
            if (animIdx < Bomb.BOMB_PLANTED_ANIMS.length - 1) {
                bomb.setAnimIdx(animIdx + 1);
            } else {
                explode(bomb);
            }
        });
    }

    private void explode(Bomb bomb) {
        bomb.explode();

        final Explosion explosion = new Explosion(bomb.getX(), bomb.getY(), "center-explosion");
        world.getExplosions().add(explosion);

        // Up Floor
        if (world.getCoordinate(bomb.getY() - 1, bomb.getX()).getImg().equals("floor-1")) {
            world.getExplosions().add(new Explosion(bomb.getX(), bomb.getY() - 1, "up-explosion"));
        }

        // Down Floor
        if (world.getCoordinate(bomb.getY() + 1, bomb.getX()).getImg().equals("floor-1")) {
            world.getExplosions().add(new Explosion(bomb.getX(), bomb.getY() + 1, "down-explosion"));
        }

        // Left Floor
        if (world.getCoordinate(bomb.getY(), bomb.getX() - 1).getImg().equals("floor-1")) {
            world.getExplosions().add(new Explosion(bomb.getX() - 1, bomb.getY(), "left-explosion"));
        }

        // Right Floor
        if (world.getCoordinate(bomb.getY(), bomb.getX() + 1).getImg().equals("floor-1")) {
            world.getExplosions().add(new Explosion(bomb.getX() + 1, bomb.getY(), "right-explosion"));
        }

        // Up Block
        if (world.getCoordinate(bomb.getY() - 1, bomb.getX()).getImg().equals("block")) {
            world.getExplosions().add(new Explosion(bomb.getX(), bomb.getY() - 1, "block-on-fire"));
        }

        // Bot Block
        if (world.getCoordinate(bomb.getY() + 1, bomb.getX()).getImg().equals("block")) {
            world.getExplosions().add(new Explosion(bomb.getX(), bomb.getY() + 1, "block-on-fire"));
        }

        // Left Block
        if (world.getCoordinate(bomb.getY(), bomb.getX() - 1).getImg().equals("block")) {
            world.getExplosions().add(new Explosion(bomb.getX() - 1, bomb.getY(), "block-on-fire"));
        }

        // Right Block
        if (world.getCoordinate(bomb.getY(), bomb.getX() + 1).getImg().equals("block")) {
            world.getExplosions().add(new Explosion(bomb.getX() + 1, bomb.getY(), "block-on-fire"));
        }

    }

}
