package asteroids.game_objects;

import asteroids.TwoD.TwoDPolygon;
import asteroids.TwoD.TwoDSegment;
import asteroids.TwoD.TwoDVector;
import java.awt.Graphics;

public abstract class AreaGameObject extends MoveableObject {
    
    protected final TwoDPolygon polygon;

    public AreaGameObject(TwoDVector center, TwoDVector velocity, TwoDPolygon poly) {
        super(center, velocity);
        polygon = poly;
    }

    @Override
    public void draw(Graphics g) {
        g.translate(getCenter().getXAsInteger(), getCenter().getYAsInteger());
        for (TwoDSegment edge : polygon) {
            g.drawLine(
                    edge.getFirstPoint().getXAsInteger(), 
                    edge.getFirstPoint().getYAsInteger(), 
                    edge.getSecondPoint().getXAsInteger(),
                    edge.getSecondPoint().getYAsInteger()
            );
        }
        g.translate(0, 0);
    }
    
    public Explosion collisionWith(AreaGameObject other) {
        for (TwoDVector v : other.polygon.getVertices())
            if (polygon.isThePointInside(v.getAbsolute(other.getCenter()))) 
                return Explosion.explose(v.getAbsolute(other.getCenter()));
        return null;
    }
    
    public Explosion collisionWith(Bullet bullet) {
        if (polygon.isThePointInside(bullet.getCenter()))
            return Explosion.explose(
                    bullet.getCenter(), 
                    bullet.getVelocityVector().getReversed().getNormalize());
        return null;
    }
    
}
