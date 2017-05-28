package asteroids.game_objects;

import asteroids.TwoD.TwoDPolygon;
import asteroids.TwoD.TwoDVector;
import asteroids.Utils;

public class Asteroid extends AreaGameObject implements LimitedLifeTime {

    private static final int SMALL_ASTEROID = 0;
    private static final int MEDIUM_ASTEROID = 1;
    private static final int BIG_ASTEROID = 2;
    
    private static final TwoDVector[][] RANDOM_ASTEROIDS_GEOMETRY = {
        {
            new TwoDVector(0, -4),
            new TwoDVector(7, -7),
            new TwoDVector(11, -1),
            new TwoDVector(9, 4),
            new TwoDVector(5, 6),
            new TwoDVector(0, 3),
            new TwoDVector(-4, 5),
            new TwoDVector(-9, 2),
            new TwoDVector(-9, -2),
            new TwoDVector(-7, -6),
            new TwoDVector(-3, -7),
            new TwoDVector(-2, -10)
        },
        {
            new TwoDVector(1.4F, -1.4F),
            new TwoDVector(2.8F, 1.4F),
            new TwoDVector(8.4F, 1.4F),
            new TwoDVector(1.4F, 11.2F),
            new TwoDVector(-2.8F, 7),
            new TwoDVector(-8.4F, 8.4F),
            new TwoDVector(-9.8F, 1.4F),
            new TwoDVector(-5.6F, -2.8F),
            new TwoDVector(-7, -5.6F)
        }
    };
    
    
    private static final float[] ASTEROID_GEOMETRY_SCALE = {
        2F, 3F, 4F 
    };
    
    private static final float[][] ASTEROID_MIN_MAX_VELOCITY = {
        {4F, 10F},
        {3.5F, 7.5F},
        {3F, 6F}
    };
    
    private static final int[] SCORE_INCREMENT = {
        10, 20, 30
    };
    
    private static final int ASTEROID_SPLITS_COUNT = 2;
    
    private boolean crushed;
    private final int asteroidType;
    
    private Asteroid(TwoDVector center, TwoDVector velocity, TwoDPolygon polygon, int type) {
        super(center, velocity, polygon);
        asteroidType = type;
    }
    
    public int getScoreIncrement() {
        return SCORE_INCREMENT[asteroidType];
    }
    
    @Override
    public boolean stillExists() {
        return !crushed;
    }
    
    public Asteroid[] split(GameObjectImpl collapsed) {
        if (asteroidType == SMALL_ASTEROID) return null;
        Asteroid[] derivatives = new Asteroid[ASTEROID_SPLITS_COUNT];
        TwoDVector velocity = 
                collapsed.getVelocityVector().getNormalize();
        for (int i = 0; i < derivatives.length; i++) {
            TwoDVector copy = velocity.copy();
            copy.rotate(
                    Utils.randomizeFloatNumberSign(
                            Utils.getRandomFloatInRange(0, (float) (Math.PI / 4)
                            )
                    )
            );
            derivatives[i] = randomAsteroid(
                    getCenter().copy(), 
                    copy, 
                    asteroidType - 1
            );
        }
        return derivatives;
    }
    
    public Asteroid[] split() {
        if (asteroidType == SMALL_ASTEROID) return null;
        Asteroid[] derivatives = new Asteroid[ASTEROID_SPLITS_COUNT];
        for (int i = 0; i < derivatives.length; i++) {
            derivatives[i] = randomAsteroid(
                    getCenter().copy(),
                    TwoDVector.getRandomUnitVector(),
                    asteroidType - 1
            );
        }
        return derivatives;
    }
    
    private static Asteroid randomAsteroid(TwoDVector center, TwoDVector velocityDirection, int type) {
        int randomGeometry = Utils.getRandomIntegerInRange(0, RANDOM_ASTEROIDS_GEOMETRY.length - 1);
        TwoDVector[] vertices = new TwoDVector[RANDOM_ASTEROIDS_GEOMETRY[randomGeometry].length];
        for (int i = 0; i < RANDOM_ASTEROIDS_GEOMETRY[randomGeometry].length; i++) 
            vertices[i] = RANDOM_ASTEROIDS_GEOMETRY[randomGeometry][i].copy();
        for (TwoDVector v : vertices) v.scale(ASTEROID_GEOMETRY_SCALE[type]);
        TwoDPolygon poly = new TwoDPolygon(
                vertices,
                center
        );
        poly.rotate(Utils.getRandomFloatInRange(0, (float) (2 * Math.PI)));
        velocityDirection.scale(
                Utils.getRandomFloatInRange(
                        ASTEROID_MIN_MAX_VELOCITY[type][0], 
                        ASTEROID_MIN_MAX_VELOCITY[type][1]
                )
        );
        return new Asteroid(center, velocityDirection, poly, type);
    }
    
    public static Asteroid getRandomBigAsteroid(TwoDVector center) {
        return randomAsteroid(center, TwoDVector.getRandomUnitVector(), BIG_ASTEROID);
    }
    
}
