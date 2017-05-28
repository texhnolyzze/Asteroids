package asteroids.game_objects;

import asteroids.TwoD.TwoDPolygon;
import asteroids.TwoD.TwoDVector;
import asteroids.Utils;
import asteroids.sound.SoundEffect;
import javafx.scene.canvas.GraphicsContext;

public class UFO extends AreaGameObject implements LimitedLifeTime {
    
    private static final int SMALL_UFO = 0;
    private static final int BIG_UFO = 1;
    
    private static final int[] SCORE_INCREMENT = {
        50, 25
    };
    
    private static final SoundEffect[] UFO_SOUND = {
        SoundEffect.SMALL_UFO_SIREN,
        SoundEffect.BIG_UFO_SIREN
    };
    
    private static final int FLY_OUT_OF_LEFT_BOUND = 0;
    private static final int FLY_OUT_OF_BOTTOM_BOUND = 3;
    
    private static final int SHOTS_INTERVAL = 50;
    
    private static final float UFO_VELOCITY = 3F;
    private static final int VELOCITY_CHANGE_PROBABILITY = 300;
    private static final int[][] VELOCITY_CHANGES = {
        {1, -1},
        {-1, 1},
    };
    
    private static final TwoDVector[] UFO_GEOMETRY = {
        new TwoDVector(2F, -4F),    // 0           7    0
        new TwoDVector(3F, -2F),    // 1           /▔▔\
        new TwoDVector(6F, 0F),    // 2      6   /     \      1
        new TwoDVector(4F, 2F),   // 3     ╱▔▔▔▔▔▔▔▔▔╲   
        new TwoDVector(-4F, 2F),  // 4  ╱5                   ╲ 2
        new TwoDVector(-6F, 0F),   // 5  ╲                    ╱ 
        new TwoDVector(-3F, -2F),   // 6    ╲               ╱   
        new TwoDVector(-2F, -4F),   // 7     4╲__________3╱     
    };
    
    private static final int[] UFO_GEOMETRY_SCALES = {
        2, 5 
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
    public Explosion collisionWith(Bullet bullet) {
        if (bullet.isPlayerShot()) return super.collisionWith(bullet); 
        else return null;
    }
    
    @Override
    public void move(int rightBound, int bottomBound) {
        super.move(rightBound, bottomBound);
        if (Utils.conditionWithProbability(VELOCITY_CHANGE_PROBABILITY)) changeVelocity();
        lifeTime++;
    }
    
    @Override
    public boolean stillExists() {
        boolean exists = lifeTime < _maxLifeTime;
        if (!exists) UFO_SOUND[UFOType].stop();
        return exists;
    }
    
    public void crush() {
        lifeTime = _maxLifeTime;
    }

    public Bullet shoot(SpaceShip enemy) {
        if (UFOType == SMALL_UFO) {
            return Bullet.shoot(
                    getCenter().copy(),
                    enemy.getCenter().getRelativeTo(getCenter()).getNormalize(),
                    false
            );
        } else return Bullet.shoot(getCenter().copy(), TwoDVector.getRandomUnitVector(), false);
    }
    
    public boolean timeToShot() {
        return lifeTime % SHOTS_INTERVAL == 0;
    }
    
    public int getScoreIncrement() {
        return SCORE_INCREMENT[UFOType];
    }
    
    @Override
    public void draw(GraphicsContext g) {
        super.draw(g);
        g.translate(
                getCenter().getXComponent(), 
                getCenter().getYComponent()
        );
        g.strokeLine(
                polygon.getVertices()[6].getXComponent(), 
                polygon.getVertices()[6].getYComponent(), 
                polygon.getVertices()[1].getXComponent(), 
                polygon.getVertices()[1].getYComponent()
        );
        g.strokeLine(
                polygon.getVertices()[5].getXComponent(), 
                polygon.getVertices()[5].getYComponent(), 
                polygon.getVertices()[2].getXComponent(), 
                polygon.getVertices()[2].getYComponent()
        );
        g.translate(-getCenter().getXComponent(), -getCenter().getYComponent());
    }
    
    private void changeVelocity() {
        getVelocityVector().xScale(VELOCITY_CHANGES[_flewOutFrom % 2][0]);
        getVelocityVector().yScale(VELOCITY_CHANGES[_flewOutFrom % 2][1]);
    }
    
    private static UFO createUFO(int type, int flyFrom, int rightBound, int bottomBound) {
        TwoDVector[] copy = new TwoDVector[UFO_GEOMETRY.length];
        for (int i = 0; i < copy.length; i++)
            copy[i] = UFO_GEOMETRY[i].copy();
        for (TwoDVector v : copy) v.scale(UFO_GEOMETRY_SCALES[type]);
        TwoDVector[] fromWhere = {
            new TwoDVector(0, Utils.getRandomIntegerInRange(0, bottomBound)),
            new TwoDVector(Utils.getRandomIntegerInRange(0, rightBound), 0),
            new TwoDVector(rightBound, Utils.getRandomIntegerInRange(0, bottomBound)),
            new TwoDVector(Utils.getRandomIntegerInRange(0, rightBound), bottomBound),
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
        UFO_SOUND[type].playLooply();
        return new UFO(
                center, 
                velocity, 
                new TwoDPolygon(copy, center), 
                type, 
                lifeTimes[flyFrom % 2], 
                flyFrom
        );
    }
    
    public static UFO createUFO(int rightBound, int bottomBound) {
        return createUFO(
                Utils.getRandomIntegerInRange(SMALL_UFO, BIG_UFO),
                Utils.getRandomIntegerInRange(FLY_OUT_OF_LEFT_BOUND, FLY_OUT_OF_BOTTOM_BOUND),
                rightBound,
                bottomBound);
    }
    
}
