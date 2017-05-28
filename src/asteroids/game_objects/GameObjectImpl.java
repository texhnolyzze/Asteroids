package asteroids.game_objects;

import asteroids.TwoD.TwoDVector;
import javafx.scene.canvas.GraphicsContext;

public abstract class GameObjectImpl implements GameObject {
    
    private final TwoDVector centerVector;
    private final TwoDVector velocityVector;
    
    public GameObjectImpl(TwoDVector center, TwoDVector velocity) {
        centerVector = center;
        velocityVector = velocity;
    }
    
    @Override
    public abstract void draw(GraphicsContext g);
    
    @Override
    public void move(int worldWidth, int worldHeight) {
        getCenter().transferWithPossibleClosure(
                velocityVector.getXComponent(), 
                velocityVector.getYComponent(),
                worldWidth,
                worldHeight
        );
    }
    
    TwoDVector getCenter() {
        return centerVector;
    }
    
    TwoDVector getVelocityVector() {
        return velocityVector;
    }

    @Override
    public String toString() {
        return getClass().toString() + '{' + "centerVector=" + centerVector + '}';
    }
    
}
