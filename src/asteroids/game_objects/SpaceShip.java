package asteroids.game_objects;

import asteroids.TwoD.TwoDPolygon;
import asteroids.TwoD.TwoDSegment;
import asteroids.TwoD.TwoDVector;
import java.awt.Color;
import java.awt.Graphics;
import java.util.Arrays;
import java.util.Iterator;

public class SpaceShip extends AreaGameObject {

    public static final int CLOCKWISE_ROTATION = 0;
    public static final int COUNTER_CLOCKWISE_ROTATION = 1;
    
    private static final TwoDVector[] SHIP_RELATIVE_TO_CENTER = {
        new TwoDVector(0F, 6F),  //0           // /\ 0  
        new TwoDVector(5F, -4F), //1         // / 2 \
        new TwoDVector(0F, -2F), //2       // / ╱╲ \
        new TwoDVector(-5F, -4F),//3     // /╱     ╲\ 
    };                                 //   3         1


    private static final float REACTIVE_FORCE = 1.3F;
    private static final float FRICTION_FORCE = 1.1F;
    
    private static final int DRAW_GASES_SCALE = 6;
    
    private static final int INVULNERABLE_TIME = 20;
    
    private static final float[] ROTATION_ANGLES = {
        0.174533F, 
        -0.174533F
    };
    
    private final TwoDVector accelerationVector;
    private final TwoDVector reactiveForce;
    private final TwoDVector frictionForce;
    
    private boolean engineTurnOn;
    private boolean frictionTurnOn;
    
    private boolean gasesDrawedOnThePreviousTick;
    
    private int lifeTime;
    
    private int rotationDirection;
    
    private SpaceShip(TwoDVector center, TwoDVector velocity, TwoDPolygon poly) {
        super(center, velocity, poly);
        accelerationVector = new TwoDVector(0, 0);        // relative 
        reactiveForce = new TwoDVector(0, 0);           //   to
        frictionForce = new TwoDVector(0, 0);         //     center
        rotationDirection = -1;
    }

    @Override
    public Explosion collisionWith(AreaGameObject other) {
        if (isInvulnerable()) return null;
        return super.collisionWith(other); 
    }

    @Override
    public Explosion collisionWith(Bullet bullet) {
        if (isInvulnerable()) return null;
        return super.collisionWith(bullet); 
    }

    @Override
    public void draw(Graphics g) {
        Color srcColor = g.getColor();
        if (isInvulnerable()) {
            draw0(g);
            g.setColor(Color.BLACK);
            draw0(g);
            g.setColor(srcColor);
            draw0(g);
        } else draw0(g);
    }
    
    private void draw0(Graphics g) {
        super.draw(g); 
        if (engineTurnOn) { 
            g.translate(getCenter().getXAsInteger(), getCenter().getYAsInteger());
            drawGases(g);
            g.translate(0, 0);
        }
    }
    
    private void drawGases(Graphics g) {
        if (!gasesDrawedOnThePreviousTick) {
            TwoDVector backward = getBackwardDirection();
            backward.scale(DRAW_GASES_SCALE);
            drawGases(g, backward, 1);
            drawGases(g, backward, 2);
            gasesDrawedOnThePreviousTick = true;
        } else gasesDrawedOnThePreviousTick = false;
    }
    
    private void drawGases(Graphics g, TwoDVector backward, int fromEdge) {
        g.drawLine(
                polygon.getMidpointOf(fromEdge).getXAsInteger(), 
                polygon.getMidpointOf(fromEdge).getYAsInteger(), 
                backward.getXAsInteger(), 
                backward.getYAsInteger()
        );
    }
    
    private boolean isInvulnerable() {
        return lifeTime <= INVULNERABLE_TIME;
    }
    
    public Bullet shoot() {
        return Bullet.shoot(polygon.getVertices()[0].getAbsolute(getCenter()), getForwardDirection());
    }

    @Override
    public void move(int rightBound, int bottomBound) {
        super.move(rightBound, bottomBound);
        velocityChange();
        accelerationChange();
        if (frictionTurnOn && getVelocityVector().isNullVector()) turnOffFriction();
        else if (!frictionTurnOn) turnOnFriction();
        if (rotationDirection != -1) rotate(ROTATION_ANGLES[rotationDirection]);
    }
    
    public void rotate(int direction) {
        rotationDirection = direction;
    }
    
    public void stopRotate() {
        rotationDirection = -1;
    }
    
    private void rotate(float radiansAngle) {
        polygon.rotate(radiansAngle);
        if (engineTurnOn) reactiveForce.rotate(radiansAngle);
        if (frictionTurnOn) frictionForce.rotate(radiansAngle);
    }

    private TwoDVector getForwardDirection() {
        return polygon.getVertices()[0].getNormalize();
    }
    
    private TwoDVector getBackwardDirection() {
        return getForwardDirection().getReversed();
    }
    
    public void turnOnEngine() {
        TwoDVector unit = getForwardDirection();
        unit.scale(REACTIVE_FORCE);
        reactiveForce.transfer(
                unit.getXComponent(), unit.getYComponent()
        );
        engineTurnOn = true;
    }
    
    public void turnOffEngine() {
        reactiveForce.scale(0F);
        engineTurnOn = false;
    }
    
    private void turnOnFriction() {
        TwoDVector unit = getBackwardDirection();
        unit.scale(FRICTION_FORCE);
        frictionForce.transfer(
                unit.getXComponent(), unit.getYComponent()
        );
        frictionTurnOn = true;
    }
    
    private void turnOffFriction() {
        frictionForce.scale(0F);
        frictionTurnOn = false;
    }
    
    private void velocityChange() {
        getVelocityVector().transfer(
                accelerationVector.getXComponent(),
                accelerationVector.getYComponent()
        );
    }
    
    private void accelerationChange() {
        if (engineTurnOn) accelerationVector.transfer(
                reactiveForce.getXComponent(), reactiveForce.getYComponent()
        );
        if (frictionTurnOn) accelerationVector.transfer(
                frictionForce.getXComponent(), frictionForce.getYComponent()
        );
    }
    
    public static SpaceShip createShip(TwoDVector center) {
        return new SpaceShip(
                center, 
                new TwoDVector(0, 0), 
                new TwoDPolygon(Arrays.copyOf(
                        SHIP_RELATIVE_TO_CENTER,
                        SHIP_RELATIVE_TO_CENTER.length
                ), center)
        );
    }
    
    public SpaceShipDeath shipDeath() {
        return new SpaceShipDeath(this);
    }
    
    class SpaceShipDeath extends MoveableObject {

        private static final float SPLINTERS_VELOCITY = 3F;
        
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
                        copy.getMidpoint(), 
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
        public void draw(Graphics g) {
            g.translate(getCenter().getXAsInteger(), getCenter().getYAsInteger());
            for (Splinter s : shipSplinters) s.draw(g);
            g.translate(0, 0);
        }
        
        class Splinter extends MoveableObject {
            
            private final TwoDSegment shipEdge;

            private Splinter(TwoDVector center, TwoDVector velocity, TwoDSegment edge) {
                super(center, velocity);
                shipEdge = edge;
            }
            
            @Override
            public void draw(Graphics g) {
                g.drawLine(
                        shipEdge.getFirstPoint().getXAsInteger(), 
                        shipEdge.getFirstPoint().getYAsInteger(), 
                        shipEdge.getSecondPoint().getXAsInteger(),
                        shipEdge.getSecondPoint().getYAsInteger()
                );
            }
            
        }
    
    }
    
}
