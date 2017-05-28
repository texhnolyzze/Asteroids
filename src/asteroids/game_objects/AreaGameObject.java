package asteroids.game_objects;

import asteroids.TwoD.TwoDPolygon;
import asteroids.TwoD.TwoDSegment;
import asteroids.TwoD.TwoDVector;
import javafx.scene.canvas.GraphicsContext;

public abstract class AreaGameObject extends GameObjectImpl {
    
    protected final TwoDPolygon polygon;

    public AreaGameObject(TwoDVector center, TwoDVector velocity, TwoDPolygon poly) {
        super(center, velocity);
        polygon = poly;
    }

    @Override
    public void draw(GraphicsContext g) {
        g.translate(getCenter().getXComponent(), getCenter().getYComponent());
        for (TwoDSegment edge : polygon) {
            g.strokeLine(
                    edge.getFirstPoint().getXComponent(), 
                    edge.getFirstPoint().getYComponent(), 
                    edge.getSecondPoint().getXComponent(),
                    edge.getSecondPoint().getYComponent()
            );
        }
        g.translate(-getCenter().getXComponent(), -getCenter().getYComponent());
    }
    
    public Explosion collisionWith(AreaGameObject other) {
        float squareDistanceBetweenCenters = 
                getCenter().getRelativeTo(other.getCenter()).getSquareLength();
        if (squareDistanceBetweenCenters >
                polygon.getMaxSquareDistanceFromCenter() + other.polygon.getMaxSquareDistanceFromCenter())
            return null;
        for (TwoDSegment s : other.polygon) {
            TwoDVector intersection = polygon.intersectWith(s.getAbsolute(other.getCenter()));
            if (intersection != null)
                return Explosion.explose(intersection);
        }
        return null;
    }
    
    public Explosion collisionWith(Bullet bullet) {
        return bullet.collisionWith(this);
    }
    
}
