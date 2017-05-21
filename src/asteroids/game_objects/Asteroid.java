package asteroids.game_objects;

import asteroids.TwoD.TwoDPolygon;
import asteroids.TwoD.TwoDVector;
import asteroids.Utils;

public class Asteroid extends AreaGameObject {

    public static final int SMALL_ASTEROID = 0;
    public static final int MEDIUM_ASTEROID = 1;
    public static final int BIG_ASTEROID = 2;
    
    private static final float[] ASTEROID_RADIUS = {
        5F, 15F, 20F 
    };
    
    private static final float[] ASTEROID_VELOCITY = {
        20F, 15F, 5F 
    };
    
    private static final int MIN_VERTICES_COUNT = 9;
    private static final int MAX_VERTICES_COUNT = 13;
    
    private static final float MIN_DISTANCE_FROM_CENTER = 3F;
    
    private static final int ASTEROID_SPLITS_COUNT = 2;
    
    private final int asteroidType;
    
    Asteroid(TwoDVector center, TwoDVector velocity, TwoDPolygon polygon, int type) {
        super(center, velocity, polygon);
        asteroidType = type;
    }
    
    public Asteroid[] split() {
        if (asteroidType == SMALL_ASTEROID) return null;
        Asteroid[] derivatives = new Asteroid[ASTEROID_SPLITS_COUNT];
        for (int i = 0; i < derivatives.length; i++)
            derivatives[i] = randomAsteroid(getCenter(), asteroidType - 1);
        return derivatives;
    }
    
    private static Asteroid randomAsteroid(TwoDVector center, int type) {
        TwoDPolygon poly = TwoDPolygon.randomPolygone(
                Utils.getRandomIntegerInRange(
                        MIN_VERTICES_COUNT, 
                        MAX_VERTICES_COUNT
                ), 
                center, 
                MIN_DISTANCE_FROM_CENTER, 
                ASTEROID_RADIUS[type]
        );
        TwoDVector velocity = TwoDVector.getRandomUnitVector();
        velocity.scale(ASTEROID_VELOCITY[type]);
        return new Asteroid(center, velocity, poly, type);
    }
    
    public static Asteroid getRandomBigAsteroid(TwoDVector center) {
        return randomAsteroid(center, BIG_ASTEROID);
    }
    
}
