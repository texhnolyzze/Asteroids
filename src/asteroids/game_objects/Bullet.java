package asteroids.game_objects;

import asteroids.TwoD.TwoDVector;
import java.awt.Graphics;

public class Bullet extends MoveableObject {

    private static final float BULLET_VELOCITY = 30F;
    private static final int BULLET_LIFE_TIME = 150;
    
    private int lifeTime;
    
    private Bullet(TwoDVector center, TwoDVector velocity) {
        super(center, velocity);
    }

    @Override
    public void move(int rightBound, int bottomBound) {
        super.move(rightBound, bottomBound); 
        lifeTime++;
    }
    
    public boolean stillExists() {
        return lifeTime <= BULLET_LIFE_TIME;
    }

    @Override
    public void draw(Graphics g) {
        g.fillRect(getCenter().getXAsInteger(), getCenter().getYAsInteger(), 4, 4);
    }
    
    static Bullet shoot(TwoDVector fromWhereToShoot, TwoDVector unitDirection) {
        unitDirection.scale(BULLET_VELOCITY);
        return new Bullet(fromWhereToShoot, unitDirection);
    }
    
}
