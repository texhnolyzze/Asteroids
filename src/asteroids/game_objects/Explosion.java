
package asteroids.game_objects;

import asteroids.SoundStore;
import static asteroids.utils.General.getRandomFloatInRange;
import static asteroids.utils.General.getRandomIntegerInRange;
import asteroids.utils.TimeStamp;
import asteroids.utils.Vector2;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 *
 * @author Texhnolyze
 */
public class Explosion {
    
    public static final int SMALL       = 0;
    public static final int MEDIUM   = 1;
    public static final int BIG           = 2;
    
    public static final int EXPLOSION_VALID_TIME_MS = 500;
    
    public static final int[][] MIN_MAX_PARTICLES_NUM = {
        { 5, 10 },
        { 10, 15 },
        { 15, 20 }
    };
    
    public static final float MIN_PARTICLE_V = 1F;
    public static final float MAX_PARTICLE_V = 2F;

    private final int type;
    
    private final Vector2[] particles;
    private final Vector2[] particlesVelocities;    
    
    private final TimeStamp boomTime = new TimeStamp();
    
    private Explosion(Vector2[] particles, Vector2[] particlesVelocities, int type) {
        this.type = type;
        this.particles = particles;
        this.particlesVelocities = particlesVelocities;
    }
    
    public void update() {
        for (int i = 0; i < particles.length; i++) {
            particles[i].addLocal(particlesVelocities[i]);
            particles[i].closureLocal();
        }
    }
    
    public void draw(GraphicsContext gc) {
        gc.setFill(Color.WHITE);
        for (Vector2 p : particles) gc.fillRect(p.getX(), p.getY(), 1, 1);
    }
    
    public boolean isValid() {
        return boomTime.getMsPassed() < EXPLOSION_VALID_TIME_MS;
    }
    
    public static Explosion getBoomIn(Vector2 center, int type) {
        SoundStore.BIG_BANG.play(false);
        int min = MIN_MAX_PARTICLES_NUM[type][0];
        int max = MIN_MAX_PARTICLES_NUM[type][1];
        int pNum = getRandomIntegerInRange(min, max);
        Vector2[] particles = new Vector2[pNum];
        Vector2[] particlesVelocities = new Vector2[pNum];
        for (int i = 0; i < pNum; i++) {
            particles[i] = center.copy();
            Vector2 vel = Vector2.getRandomUnitVector();
            float scale = getRandomFloatInRange(MIN_PARTICLE_V, MAX_PARTICLE_V);
            vel.scaleLocal(scale, scale);
            particlesVelocities[i] = vel;
        }
        return new Explosion(particles, particlesVelocities, type);
    } 
    
}
