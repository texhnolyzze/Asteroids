package asteroids.game_objects;

import javafx.scene.canvas.GraphicsContext;

public interface GameObject {

    void draw(GraphicsContext gc);
    
    void move(int screenRightBound, int screenBottomBound);
    
}
