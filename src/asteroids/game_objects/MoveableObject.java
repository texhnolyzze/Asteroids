package asteroids.game_objects;

import asteroids.TwoD.TwoDVector;

public abstract class MoveableObject extends AsteroidsGameObject {
    
    private final TwoDVector velocityVector;
    
    public MoveableObject(TwoDVector center, TwoDVector velocity) {
        super(center);
        velocityVector = velocity;
    }
    
    public void move(int rightBound, int bottomBound) {
        getCenter().transferWithPossibleClosure(
                velocityVector.getXComponent(), 
                velocityVector.getYComponent(),
                rightBound,
                bottomBound
        );
    }
    
    TwoDVector getVelocityVector() {
        return velocityVector;
    }
    
}
