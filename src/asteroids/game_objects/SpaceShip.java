package asteroids.game_objects;

import asteroids.BinaryOscillator;
import asteroids.TwoD.TwoDPolygon;
import asteroids.TwoD.TwoDSegment;
import asteroids.TwoD.TwoDVector;
import asteroids.sound.SoundEffect;
import java.util.Iterator;
import javafx.scene.canvas.GraphicsContext;

public class SpaceShip extends AreaGameObject {
    
    public static final int CLOCKWISE_ROTATION = 0;
    public static final int COUNTER_CLOCKWISE_ROTATION = 1;
    
    private static final TwoDVector[] SHIP_RELATIVE_TO_CENTER = {
        new TwoDVector(0F, -12F),  //0           // /\ 0  
        new TwoDVector(10F, 8F), //1         // / 2 \
        new TwoDVector(0F, 4F), //2       // / ╱╲ \
        new TwoDVector(-10F, 8F),//3     // /╱     ╲\ 
    };                                 //   3         1


    private static final float REACTIVE_FORCE = 0.09F;
    private static final float FRICTION_FORCE = 0.01F;
    private static final float MAX_SQUARE_VELOCITY = 64F;
    
    private static final int DRAW_GASES_SCALE = 12;
    
    private static final int INVULNERABLE_TIME = 200;
    
    private static final float[] ROTATION_ANGLES = {
        0.139626F, 
        -0.139626F
    };
    
    private final BinaryOscillator invulnerableFlickering;
    private final BinaryOscillator reactiveGasesFlickering;
    private final BinaryOscillator delayBeforeRevival;
    
    private final TwoDVector occurrenceVector;
    
    
    private boolean engineTurnOn;
    
    private int lifeTime;
    
    private boolean isDead;
    private SpaceShipDeath death;
    
    private int rotationDirection;
    
    private SpaceShip(TwoDVector center, TwoDVector velocity, TwoDPolygon poly) {
        super(center, velocity, poly);
        occurrenceVector = center.copy();

        rotationDirection = -1;
        invulnerableFlickering = new BinaryOscillator(10, false);
        reactiveGasesFlickering = new BinaryOscillator(2, false);
        delayBeforeRevival = new BinaryOscillator(200, true);
    }

    @Override
    public Explosion collisionWith(AreaGameObject other) {
        if (isInvulnerable() || isDead) return null;
        return super.collisionWith(other); 
    }

    @Override
    public Explosion collisionWith(Bullet bullet) {
        if (isInvulnerable() || isDead || bullet.isPlayerShot()) return null;
        return super.collisionWith(bullet); 
    }
    
    public boolean isDead() {
        return isDead;
    }

    @Override
    public void draw(GraphicsContext g) {
        if (isDead) death.draw(g);
        else if (isInvulnerable()) {
            if (invulnerableFlickering.needToPerformAction()) draw0(g);
        }
        else draw0(g);
    }
    
    
    private void draw0(GraphicsContext g) {
        super.draw(g); 
        if (engineTurnOn && reactiveGasesFlickering.needToPerformAction()) { 
            g.translate(getCenter().getXComponent(), getCenter().getYComponent());
            drawGases(g);
            g.translate(-getCenter().getXComponent(), -getCenter().getYComponent());
        }
    }
    
    private void drawGases(GraphicsContext g) {
        TwoDVector backward = getBackwardDirection();
        backward.scale(DRAW_GASES_SCALE);
        drawGases(g, backward, 1);
        drawGases(g, backward, 2);
    }
    
    private void drawGases(GraphicsContext g, TwoDVector backward, int fromEdge) {
        g.strokeLine(
                polygon.getMidpointOf(fromEdge).getXComponent(), 
                polygon.getMidpointOf(fromEdge).getYComponent(), 
                backward.getXComponent(), 
                backward.getYComponent()
        );
    }
    
    private boolean isInvulnerable() {
        return lifeTime <= INVULNERABLE_TIME;
    }
    
    public Bullet shoot() {
        if (isDead) return null;
        return Bullet.shoot(
                polygon.getVertices()[0].getAbsolute(getCenter()), 
                getForwardDirection(), 
                true
        );
    }

    @Override
    public void move(int rightBound, int bottomBound) {
        if (isDead) {
            if (delayBeforeRevival.needToPerformAction()) reset();
            else death.move(rightBound, bottomBound);
        } else {
            if (engineTurnOn || isMoving()) updateVelocity();
            super.move(rightBound, bottomBound);
            if (rotationDirection != -1) rotate(ROTATION_ANGLES[rotationDirection]);
            
        }
        lifeTime++;
    }
    
    public void rotate(int direction) {
        rotationDirection = direction;
    }
    
    public void stopRotate() {
        rotationDirection = -1;
    }
    
    private void rotate(float radiansAngle) {
        polygon.rotate(radiansAngle);
    }

    private TwoDVector getForwardDirection() {
        return polygon.getVertices()[0].getNormalize();
    }
    
    private TwoDVector getBackwardDirection() {
        return getForwardDirection().getReversed();
    }
    
    private void updateVelocity() {
        if (getVelocityVector().getSquareLength() > MAX_SQUARE_VELOCITY)
            getVelocityVector().scale(MAX_SQUARE_VELOCITY / getVelocityVector().getSquareLength());
        else if (getVelocityVector().isNullVector() && !isMoving()) {
            getVelocityVector().scale(0F);
        }
        if (engineTurnOn) {
            TwoDVector forward = getForwardDirection();
            forward.scale(REACTIVE_FORCE);
            getVelocityVector().transfer(
                    forward.getXComponent(), 
                    forward.getYComponent()
            );
        }
        if (isMoving()) {
            TwoDVector backward = getVelocityVector().getReversed().getNormalize();
            backward.scale(FRICTION_FORCE);
            getVelocityVector().transfer(
                    backward.getXComponent(), 
                    backward.getYComponent()
            );
        }
    }
    
    private boolean isMoving() {
        return getVelocityVector().getSquareLength() > 0.00001F;
    }
    
    public void turnOnEngine() {
        engineTurnOn = true;
        SoundEffect.JET_ENGINE.playLooply();
    }
    
    public void turnOffEngine() {
        engineTurnOn = false;
        reactiveGasesFlickering.reset();
        SoundEffect.JET_ENGINE.stop();
    }
    
    public static SpaceShip createShip(TwoDVector center) {
        TwoDVector[] copy = new TwoDVector[SHIP_RELATIVE_TO_CENTER.length];
        for (int i = 0; i < copy.length; i++) copy[i] = SHIP_RELATIVE_TO_CENTER[i].copy();
        return new SpaceShip(
                center, 
                new TwoDVector(0, 0), 
                new TwoDPolygon(copy, center)
        );
    }
    
    public void shipDeath() {
        isDead = true;
        death = new SpaceShipDeath(this);
        if (engineTurnOn) turnOffEngine();
    }
    
    private void reset() {
        getCenter().transfer(
                occurrenceVector.getXComponent() - getCenter().getXComponent(), 
                occurrenceVector.getYComponent() - getCenter().getYComponent()
        );
        getVelocityVector().scale(0F);
        isDead = false;
        lifeTime = 0;
        invulnerableFlickering.reset();
        delayBeforeRevival.reset();
    }
    
    class SpaceShipDeath extends GameObjectImpl {

        private static final float SPLINTERS_VELOCITY = 1.5F;
        
        private final Splinter[] shipSplinters;

        private SpaceShipDeath(SpaceShip ship) {
            super(ship.getCenter(), new TwoDVector(0, 0));
            shipSplinters = new Splinter[ship.polygon.edgesCount()];
            int edge = 0;
            for (Iterator<TwoDSegment> it = ship.polygon.iterator(); it.hasNext(); edge++) {
                TwoDSegment copy = it.next().copy();
                TwoDVector velocity = TwoDVector.getRandomUnitVector();
                velocity.scale(SPLINTERS_VELOCITY);
                shipSplinters[edge] = new Splinter(
                        copy.getMidpoint().getAbsolute(ship.getCenter()), 
                        velocity,
                        copy
                );
            }
        }

        @Override
        public void move(int rightBound, int bottomBound) {
            for (Splinter s : shipSplinters) s.move(rightBound, bottomBound);
        }

        @Override
        public void draw(GraphicsContext g) {
            for (Splinter s : shipSplinters) s.draw(g);
        }
        
        class Splinter extends GameObjectImpl {
            
            private final TwoDSegment shipEdge;

            private Splinter(TwoDVector center, TwoDVector velocity, TwoDSegment edge) {
                super(center, velocity);
                shipEdge = edge;
            }
            
            @Override
            public void draw(GraphicsContext g) {
                g.translate(getCenter().getXComponent(), getCenter().getYComponent());
                g.strokeLine(
                        shipEdge.getFirstPoint().getXComponent(), 
                        shipEdge.getFirstPoint().getYComponent(), 
                        shipEdge.getSecondPoint().getXComponent(),
                        shipEdge.getSecondPoint().getYComponent()
                );
                g.translate(-getCenter().getXComponent(), -getCenter().getYComponent());
            }
            
        }
    
    }
    
}
