package asteroids.game_objects;

import asteroids.SoundStore;
import asteroids.utils.Segment2;
import asteroids.utils.TimeStamp;
import asteroids.utils.Vector2;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 *
 * @author Texhnolyze
 */
public class Shot {
    
    public static final int SPACE_SHIP_SHOT     = 0;
    public static final int SMALL_UFO_SHOT      = 1;
    public static final int BIG_UFO_SHOT        = 2;
    
    public static final int SHOT_VALID_TIME_MS = 850;
    
    public static final float[] SHOT_VELOCITY = {
        8F, 10F, 8F
    };
    
    private final Vector2 vel;
    
    private final Segment2 path;
    private final TimeStamp shotTime = new TimeStamp();
    
    private final int type;
    
    public Shot(Vector2 from, Vector2 dir, int type) {
        this.vel = dir.scaleLocal(SHOT_VELOCITY[type], SHOT_VELOCITY[type]);
        this.type = type;
        path = new Segment2(from, from.copy());
        if (type == SPACE_SHIP_SHOT) 
            SoundStore.BLAST.play(false);
    }
    
    public int getType() {
        return type;
    }
    
    public Segment2 getLastPath() {
        return path;
    }
    
    public Vector2 getVelVector() {
        return vel;
    }
    
    public void update() {
        Vector2 v1 = path.getV1(), v2 = path.getV2();
        v1.setLocal(v2);
        v2.addLocal(vel);
        path.closureLocalIfBothOfTheEndsAreInvalid();
    }
    
    public boolean isValid() {
        return !shotTime.passed(SHOT_VALID_TIME_MS);
    }
    
    public void draw(GraphicsContext gc) {
        gc.setFill(Color.WHITE);
        Vector2 v = path.getV2();
        gc.fillRect(v.getX(), v.getY(), 2, 2);
    }
    
}
