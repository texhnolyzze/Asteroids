package asteroids.game_objects;

import asteroids.App;
import asteroids.utils.General;
import static asteroids.utils.General.getRandomFloatInRange;
import asteroids.utils.Polygon2;
import asteroids.utils.Vector2;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 *
 * @author Texhnolyze
 */
public class Asteroid {

    public static final int SMALL       = 0;
    public static final int MEDIUM      = 1;
    public static final int BIG         = 2;
    
    public static final float[][]  MIN_MAX_V = {
        { 
            1.5F, 2F 
        }, {
            1.0F, 1.5F
        }, { 
            0.5F, 1.0F
        }, {
            //these values for cases when an asteroid collides 
            //not with a bullet, but with another polygonal object
            0.1F, 0.2F
        }
    };
    
    public static final float[][] SCALE = {
        {
            3F, 5F, 6.5F
        }, {
            2F, 3F, 4.5F
        }, {
            2.5F, 3F, 4.5F
        }
    };
    
    public static final Vector2[][] ASTEROIDS = {
        {
            new Vector2(0, -3),
            new Vector2(1, -2),
            new Vector2(2, -3),
            new Vector2(3, -2),
            new Vector2(2, -1),
            new Vector2(3, -1),
            new Vector2(2, 2),
            new Vector2(0, 2),
            new Vector2(-2, 1),
            new Vector2(-2, -2),
        }, {
            new Vector2(0, -2),
            new Vector2(2, -3),
            new Vector2(3, -1),
            new Vector2(1, 0),
            new Vector2(3, 1),
            new Vector2(2, 4),
            new Vector2(-1, 3),
            new Vector2(-2, 4),
            new Vector2(-4, 2),
            new Vector2(-3, 0),
            new Vector2(-4, -1),
            new Vector2(-2, -3)
        }, {
            new Vector2(1, -3),
            new Vector2(3, 0),
            new Vector2(3, 1),
            new Vector2(1, 3),
            new Vector2(0, 3),
            new Vector2(0, 1),
            new Vector2(-1, 4),
            new Vector2(-4, 2),
            new Vector2(-2, 1),
            new Vector2(-4, 0),
            new Vector2(-1, -3)
        }
    };
    
    private final Polygon2 p;
    private final Vector2 vel;
    private final int type;
    
    private Asteroid(Polygon2 p, Vector2 vel, int type) {
        this.p = p;
        this.vel = vel;
        this.type = type;
    }
    
    public Vector2 getCenter() {
        return p.getCenter();
    }
    
    public float getMaxDistance2FromCenter() {
        return p.getMaxDistance2FromCenter();
    }
    
    public void update() {
        p.translateCenter(vel);
    }
    
    public void draw(GraphicsContext gc) {
        gc.setStroke(Color.WHITE);
        p.draw(gc);
    }
    
    public int getType() {
        return type;
    }
    
    public static final int SPLITS_COUNT = 2;
    
    private Shot destroyedBy;
    private Vector2 destroyedAt;
    
    public Vector2 getWhereWasDestroyed() {
        return destroyedAt;
    }
    
    public boolean isDestroyedBy(Shot s) {
        Vector2 v = p.getIntersectionPointWith(s.getLastPath());
        if (v != null) {
            destroyedBy = s;
            destroyedAt = v;
            return true;
        }
        return false;
    }
    
    public boolean isCollidedWith(SpaceShip ss) {
        if (!ss.isInvulnerable()) {
            Vector2 v = p.getIntersectionPointWith(ss.getPoly());
            if (v != null) {
                destroyedAt = v;
                return true;
            }
        }
        return false;
    }
    
    public boolean isCollidedWith(UFO ufo) {
        Vector2 v = p.getIntersectionPointWith(ufo.getPoly());
        if (v != null) {
            destroyedAt = v;
            return true;
        }
        return false;
    }
    
    public Asteroid[] split() {
        final Vector2 sVel = destroyedBy == null ? null : destroyedBy.getVelVector();
        if (type != 0) {
            Asteroid[] splits = new Asteroid[SPLITS_COUNT];
            float piDiv8 = (float) (Math.PI / 8);
            for (int i = 0; i < SPLITS_COUNT; i++) {
                if (sVel == null) 
                    splits[i] = getRandomAsteroidIn(
                            p.getCenter().copy(), Vector2.getRandomUnitVector().scaleLocal(
                                    MIN_MAX_V[MIN_MAX_V.length - 1][0], 
                                    MIN_MAX_V[MIN_MAX_V.length - 1][1]
                            ), type - 1
                    );
                else {
                    float scale = General.getRandomFloatInRange(
                            MIN_MAX_V[type - 1][0], 
                            MIN_MAX_V[type - 1][1]
                    );
                    Vector2 v = sVel.rotate(General.getRandomFloatInRange(-piDiv8, piDiv8)
                    ).normalizeLocal().scaleLocal(scale, scale);
                    splits[i] = getRandomAsteroidIn(p.getCenter().copy(), v, type - 1);
                }
            }
            return splits;
        }
        return null;
    }
    
    public static Asteroid getRandomBigAsteroid() {
        float x = (float) (Math.random() * App.WIDTH);
        float y =  (float) (Math.random() * App.HEIGHT);
        return getRandomAsteroidIn(new Vector2(x, y).closureLocal(), BIG);
    }
    
    public static Asteroid getRandomBigAsteroidAwayFrom(Vector2 v, float r) {
        if (v == null) return getRandomBigAsteroid();
        float rSqr = r * r;
        float x, y;
        do {
            x = (float) (Math.random() * App.WIDTH);
            y =  (float) (Math.random() * App.HEIGHT);
        } while (v.distance2Between(x, y) < rSqr); //Very stupid, but it works.
        return getRandomAsteroidIn(new Vector2(x, y).closureLocal(), BIG);
    }
    
    private static Asteroid getRandomAsteroidIn(Vector2 center, int type) {
        float scale = getRandomFloatInRange(MIN_MAX_V[type][0], MIN_MAX_V[type][1]);
        Vector2 vel = Vector2.getRandomUnitVector().scaleLocal(scale, scale);
        return getRandomAsteroidIn(center, vel, type);
    }
    
    private static Asteroid getRandomAsteroidIn(Vector2 center, Vector2 vel, int type) {
        int gIdx = General.getRandomIntegerInRange(0, 2);
        Vector2[] verts = new Vector2[ASTEROIDS[gIdx].length];
        for (int i = 0; i < verts.length; i++) 
            verts[i] = ASTEROIDS[gIdx][i].copy().scaleLocal(SCALE[gIdx][type], SCALE[gIdx][type]);
        Polygon2 p = new Polygon2(center, verts);
        p.rotate(General.getRandomFloatInRange(0, (float) (2 * Math.PI)));
        return new Asteroid(p, vel, type);
    }
    
}
