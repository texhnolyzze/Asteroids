package asteroids.game_objects;

import asteroids.TwoD.TwoDPolygon;
import asteroids.TwoD.TwoDVector;
import asteroids.Utils;
import java.awt.Graphics;
import java.util.Arrays;

public class UFO extends AreaGameObject {
    
    public static int SMALL_UFO = 0;
    public static int BIG_UFO = 1;
    
    public static final int FLY_OUT_OF_LEFT_BOUND = 0;
    public static final int FLY_OUT_OF_BOTTOM_BOUND = 1;
    public static final int FLY_OUT_OF_RIGHT_BOUND = 2;
    public static final int FLY_OUT_OF_UPPER_BOUND = 3;
    
    private static final int SHOTS_INTERVAL = 15;
    
    private static final float UFO_VELOCITY = 10F;
    private static final int VELOCITY_CHANGE_PROBABILITY = 4;
    private static final int[][] VELOCITY_CHANGES = {
        {1, -1},
        {-1, 1},
    };
    
    private static final TwoDVector[] SMALL_UFO_GEOMETRY = {
        new TwoDVector(2F, 4F),    // 0           7    0
        new TwoDVector(3F, 2F),    // 1           /▔▔\
        new TwoDVector(6F, 0F),    // 2      6   /     \      1
        new TwoDVector(4F, -2F),   // 3     ╱▔▔▔▔▔▔▔▔▔╲   
        new TwoDVector(-4F, -2F),  // 4  ╱5                   ╲ 2
        new TwoDVector(-6F, 0F),   // 5  ╲                    ╱ 
        new TwoDVector(-3F, 2F),   // 6    ╲               ╱   
        new TwoDVector(-2F, 4F),   // 7     4╲__________3╱     
    };
    
    private static final int[] UFO_GEOMETRY_SCALES = {
        1, 10 
    };
    
    private final int UFOType;
    private final int _flewOutFrom;
    private final int _maxLifeTime;

    private int lifeTime;
    
    private UFO(TwoDVector center, TwoDVector velocity, TwoDPolygon poly, 
            int type, int maxLifeTime, int flewOutFrom) {
        super(center, velocity, poly);
        UFOType = type;
        _maxLifeTime = maxLifeTime;
        _flewOutFrom = flewOutFrom;
    }

    @Override
    public void move(int rightBound, int bottomBound) {
        super.move(rightBound, bottomBound);
        if (Utils.conditionWithProbability(VELOCITY_CHANGE_PROBABILITY)) changeVelocity();
        lifeTime++;
    }
    
    public boolean stillExists() {
        return lifeTime <= _maxLifeTime;
    }

    public Bullet shoot(SpaceShip enemy) {
        if (UFOType == SMALL_UFO) {
            return Bullet.shoot(
                    getCenter(),
                    enemy.getCenter().getRelativeTo(getCenter()).getNormalize()
            );
        } else return Bullet.shoot(getCenter(), TwoDVector.getRandomUnitVector());
    }
    
    public boolean timeToShot() {
        return lifeTime % SHOTS_INTERVAL == 0;
    }
    
    @Override
    public void draw(Graphics g) {
        super.draw(g);
        g.translate(
                getCenter().getXAsInteger(), 
                getCenter().getYAsInteger()
        );
        g.drawLine(
                polygon.getVertices()[6].getXAsInteger(), 
                polygon.getVertices()[6].getYAsInteger(), 
                polygon.getVertices()[1].getXAsInteger(), 
                polygon.getVertices()[1].getYAsInteger()
        );
        g.drawLine(
                polygon.getVertices()[5].getXAsInteger(), 
                polygon.getVertices()[5].getYAsInteger(), 
                polygon.getVertices()[2].getXAsInteger(), 
                polygon.getVertices()[2].getYAsInteger()
        );
        g.translate(0, 0);
    }
    
    private void changeVelocity() {
        getVelocityVector().xScale(VELOCITY_CHANGES[_flewOutFrom % 2][0]);
        getVelocityVector().yScale(VELOCITY_CHANGES[_flewOutFrom % 2][1]);
    }
    
    public static UFO createUFO(int type, int flyFrom, int rightBound, int bottomBound) {
        TwoDVector[] copy = Arrays.copyOf(SMALL_UFO_GEOMETRY, SMALL_UFO_GEOMETRY.length);
        for (TwoDVector v : copy) v.scale(UFO_GEOMETRY_SCALES[type]);
        TwoDVector[] fromWhere = {
            new TwoDVector(0, Utils.getRandomIntegerInRange(0, bottomBound)),
            new TwoDVector(Utils.getRandomIntegerInRange(0, rightBound), 0),
            new TwoDVector(rightBound, Utils.getRandomIntegerInRange(0, bottomBound)),
            new TwoDVector(bottomBound, Utils.getRandomIntegerInRange(0, rightBound)),
        };
        TwoDVector center = fromWhere[flyFrom];
        float xVelocity = Utils.getRandomFloatInRange(0, 1);
        float yVelocity = 1 - xVelocity;
        TwoDVector[] velocityUnitVectors = {
            new TwoDVector(xVelocity, Utils.randomizeFloatNumberSign(yVelocity)),
            new TwoDVector(Utils.randomizeFloatNumberSign(xVelocity), yVelocity),
            new TwoDVector(-xVelocity, Utils.randomizeFloatNumberSign(yVelocity)),
            new TwoDVector(Utils.randomizeFloatNumberSign(xVelocity), -yVelocity),
        };
        TwoDVector velocity = velocityUnitVectors[flyFrom];
        velocity.scale(UFO_VELOCITY);
        int[] lifeTimes = {
            (int) (rightBound / Math.abs(velocity.getXComponent())),
            (int) (bottomBound / Math.abs(velocity.getYComponent()))
        };
        return new UFO(
                center, 
                velocity, 
                new TwoDPolygon(copy, center), 
                type, 
                lifeTimes[flyFrom % 2], 
                flyFrom
        );
    }
    
    
}
