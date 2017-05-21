package asteroids.game_objects;

import asteroids.TwoD.TwoDVector;
import java.awt.Graphics;

public abstract class AsteroidsGameObject {

    private final TwoDVector centerVector;
    
    public AsteroidsGameObject(TwoDVector center) {
        centerVector = center;
    }
    
    public abstract void draw(Graphics g);
    
    TwoDVector getCenter() {
        return centerVector;
    }
    
}
