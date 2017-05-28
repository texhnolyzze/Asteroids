package asteroids.game_objects;

import asteroids.TwoD.TwoDSegment;
import asteroids.TwoD.TwoDVector;
import asteroids.sound.SoundEffect;
import javafx.scene.canvas.GraphicsContext;

public class Bullet extends GameObjectImpl implements LimitedLifeTime {

    private static final float BULLET_VELOCITY = 15F;
    private static final int BULLET_LIFE_TIME = 30;
    
    private int lifeTime;
    private final boolean playerShot;
    
    private boolean pathClosure;
    
    private final TwoDSegment path;
    private TwoDVector[] lastPathPositions;
    
    private Bullet(TwoDVector center, TwoDVector velocity, boolean shotByPlayer) {
        super(center, velocity);
        playerShot = shotByPlayer;
        path = new TwoDSegment(center.copy(), center);
        lastPathPositions = path.getAllPoints();
    }

    public boolean isPlayerShot() {
        return playerShot;
    }
    
    @Override
    public void move(int rightBound, int bottomBound) {
        pathClosure = false;
        path.getFirstPoint().setX(getCenter().getXComponent());
        path.getFirstPoint().setY(getCenter().getYComponent());
        if (getCenter().transferWithPossibleClosure(
                getVelocityVector().getXComponent(), 
                getVelocityVector().getYComponent(), 
                rightBound,
                bottomBound)) pathClosure = true;
        lastPathPositions = path.getAllPoints();
        lifeTime++;
    }
    
    public TwoDSegment getLastPath() {
        return path;
    }   
    
    public Explosion collisionWith(AreaGameObject o) {
        if (pathClosure) return null;
        for (TwoDVector v : lastPathPositions)
            if (o.polygon.isThePointInside(v))
                return Explosion.explose(v);
        return null;
    }
    
    @Override
    public boolean stillExists() {
        return lifeTime <= BULLET_LIFE_TIME;
    }

    @Override
    public void draw(GraphicsContext g) {
        g.fillRect(getCenter().getXComponent(), getCenter().getYComponent(), 2, 2);
    }
    
    static Bullet shoot(TwoDVector fromWhereToShoot, TwoDVector unitDirection, boolean byPlayer) {
        unitDirection.scale(BULLET_VELOCITY);
        SoundEffect.SHOT.playSound();
        return new Bullet(fromWhereToShoot, unitDirection, byPlayer);
    }
    
}
