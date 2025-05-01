package learn.netty.loop;

import learn.netty.Server;
import learn.netty.World;
import learn.netty.map.Map;
import learn.netty.player.Bomb;
import learn.netty.player.Explosion;
import learn.netty.player.Player;

public class ExplodeLoop {

    private World world;

    public ExplodeLoop(World world) {
        this.world = world;
    }

    public void run() {
        world.getExplosions().forEach(explosion -> {
            if (explosion.isFinished()) {
                return;
            }

            final int animIdx = explosion.getAnimIdx();
            final String[] anims = getAnims(explosion.getType());
            final String anim = anims[animIdx];

            world.updateCoordinate(explosion.getY(), explosion.getX(), explosion.getType() + "-" + anim);
            checkNearbyPlayers(explosion.getX(), explosion.getY());

            // Update
            if (animIdx < anims.length - 1) {
                explosion.setAnimIdx(animIdx + 1);
            } else {
                world.updateCoordinate(explosion.getY(), explosion.getX(), "floor-1");
                explosion.finish();
            }

        });
    }

    private String[] getAnims(String type) {
        if (type.equals("block-on-fire")) {
            return Explosion.EXPLOSION_BLOCK_ANIMS;
        }
        return Explosion.EXPLOSION_ANIMS;
    }

    private void checkNearbyPlayers(int explosionX, int explosionY) {
        for (Player player : world.getPlayers()) {
            if (!player.isAlive()) {
                continue;
            }

            final int x = player.getX() + Map.WIDTH_SPRITE_PLAYER / 2;
            final int y = player.getY() + 2 * Map.HEIGHT_SPRITE_PLAYER / 3;

            final int col = x / Map.SIZE_SPRITE_MAP;
            final int row = y / Map.SIZE_SPRITE_MAP;

            if (col == explosionX &&  row == explosionY) {
                player.setAlive(false);
                world.updateChannels(player.getId() + " newStatus dead");
            }
        }
    }

}
