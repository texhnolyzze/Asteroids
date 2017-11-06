
package asteroids.game_objects;

import asteroids.App;
import asteroids.SoundStore;
import asteroids.utils.General;
import static asteroids.utils.General.NONE;
import asteroids.utils.Polygon2;
import asteroids.utils.Segment2;
import asteroids.utils.TickOscillator;
import asteroids.utils.TimeStamp;
import asteroids.utils.Vector2;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 *
 * @author Texhnolyze
 */
public class SpaceShip {
    
    public static final Vector2[] SHIP = {
        new Vector2(0F, -2F),
        new Vector2(1F, 1F),
        new Vector2(0F, 0.75F),
        new Vector2(-1F, 1F)
    };
    
    public static final Vector2[] GASES = {
        new Vector2(-0.46875F, 0.84375F),
        new Vector2(0F, 1.625F),
        new Vector2(0.46875F, 0.84375F)
    };
    
    //Movement attributes.
    public static boolean FRICTION_MODE = true;
    public static final float MAX_V = 6.5F;
    public static final float REACTIVE_FORCE = 0.09F;
    public static final float FRICTION_FORCE = 0.004F;
    public static final float[] ANGULAR_V_RAD = {
        0.08F, -0.08F
    };

    public static final int RELOAD_TIME_MS = 1300;
    public static final int BULLETS_IN_HOLDER = 4;
    public static final int INVULNERABLE_TIME_MS = 1500;
    
    private final Polygon2 p;
    private final Vector2 vel;
    private final Vector2 dir;
    private final Vector2 reactiveForce;
    private final Vector2[] gases;

    private final Vector2 initPosition;
    private final Vector2 initDirection;
    
    private boolean engine;
    private boolean isDead;
    private boolean isInvulnerable;
    
    private final TimeStamp resetTime = new TimeStamp();
    
    private int rotateDirection;
    
    private SpaceShip(Polygon2 p, Vector2[] gases) {
        this.p = p;
        this.gases = gases;
        this.vel = new Vector2(0F, 0F);
        this.initPosition = p.getCenter().copy();
        this.reactiveForce = new Vector2(0F, 0F);
        this.initDirection = new Vector2(p.getVerticeBy(0).normalize());
        this.dir = initDirection.copy();
    }
    
    public boolean isDead() {
        return isDead;
    }
    
    public boolean isInvulnerable() {
        return isInvulnerable;
    }
    
    public void reset() {
        engine = false;
        isDead = false;
        rotateDirection = NONE;
        vel.setLocal(0, 0);
        dir.setLocal(initDirection);
        reactiveForce.setLocal(0, 0);
        p.getCenter().setLocal(initPosition);
        for (int i = 0; i < SHIP.length; i++) {
            float x = SHIP[i].getX() * SCALE;
            float y = SHIP[i].getY() * SCALE;
            p.getVerticeBy(i).setLocal(x, y);
        }
        for (int i = 0; i < GASES.length; i++) {
            float x = GASES[i].getX() * SCALE;
            float y = GASES[i].getY() * SCALE;
            gases[i].setLocal(x, y);
        }
        resetTime.reset();
        isInvulnerable = true;
    }
    
    public Vector2 getCenter() {
        return p.getCenter();
    }
    
    public Vector2 getInitPosition() {
        return initPosition;
    }
    
    public Polygon2 getPoly() {
        return p;
    }
    
    public void update() {
        if (!isDead) {
            if (isInvulnerable && resetTime.passed(INVULNERABLE_TIME_MS)) 
                isInvulnerable = false;
            if (lastShotTime.passed(RELOAD_TIME_MS)) 
                bulletsInHolder = BULLETS_IN_HOLDER;
            updateMovement();
        } else {
            for (int i = 0; i < splinters.length; i++) {
                splinters[i].getV1().addLocal(splintersVelocities[i]);
                splinters[i].getV2().addLocal(splintersVelocities[i]);
            }
        }
    }
    
    private void updateMovement() {
        if (rotateDirection != NONE) {
            float alpha = ANGULAR_V_RAD[rotateDirection - 1];
            p.rotate(alpha);
            dir.rotateLocal(alpha);
            for (Vector2 v : gases) v.rotateLocal(alpha);
            if (engine) reactiveForce.rotateLocal(alpha);
        }
        p.translateCenter(vel);
        if (engine) {
            vel.addLocal(reactiveForce);
        }
        float v = vel.len();
        if (v > MAX_V) {
            v = MAX_V;
            vel.scaleLocalTo(MAX_V);
        }
        if (FRICTION_MODE) 
            vel.scaleLocalTo(Math.max(0, v * (1F - FRICTION_FORCE)));
    }

    private Vector2 destroyedAt;

    public Vector2 getWhereWasDestroyed() {
        return destroyedAt;
    }
    
    public boolean isDestroyedBy(Shot s) {
        if (!isInvulnerable) {
            if (s.getType() != Shot.SPACE_SHIP_SHOT) {
                Vector2 v = p.getIntersectionPointWith(s.getLastPath());
                if (v != null) {
                    destroyedAt = v;
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean isCollidedWith(UFO ufo) {
        if (!isInvulnerable) {
            Vector2 v = p.getIntersectionPointWith(ufo.getPoly());
            if (v != null) {
                destroyedAt = v;
                return true;
            }
        }
        return false;
    }
    
    private final Segment2[] splinters = new Segment2[SHIP.length];
    private final Vector2[] splintersVelocities = new Vector2[SHIP.length];
    
    {
        for (int i = 0; i < splinters.length; i++) splinters[i] = new Segment2();
        for (int i = 0; i < splintersVelocities.length; i++) splintersVelocities[i] = new Vector2();
    }
    
    public void destroy() {
        SoundStore.JET_ENGINE.stop();
        isDead = true;
        for (int i = 0; i < SHIP.length; i++) {
            splinters[i].getV1().setLocal(p.getVerticeBy(i));
            splinters[i].getV2().setLocal(p.getVerticeBy((i + 1) % SHIP.length));
            splinters[i].getV1().addLocal(p.getCenter());
            splinters[i].getV2().addLocal(p.getCenter());
            float vx = General.randomizeSign(General.getRandomFloatInRange(0.05F, 0.1F));
            float vy = General.randomizeSign(General.getRandomFloatInRange(0.05F, 0.1F));
            splintersVelocities[i].setLocal(vx, vy);
        }
    }
    
    private final TickOscillator to1 = new TickOscillator(3);
    private final TickOscillator to2 = new TickOscillator(5);
    
    public void draw(GraphicsContext gc) {
        gc.setStroke(Color.WHITE);
        if (!isDead) {
            boolean draw;
            if (isInvulnerable) {
                draw = to2.getState();
                to2.tick();
            } else 
                draw = true;
            if (draw) {
                p.draw(gc);
                gc.translate(p.getCenter().getX(), p.getCenter().getY());
                if (engine) {
                    to1.tick();
                    if (to1.getState()) {
                        for (int i = 0; i < gases.length - 1; i++) 
                            gc.strokeLine(gases[i].getX(), gases[i].getY(), gases[i + 1].getX(), gases[i + 1].getY());
                    }
                }
                gc.translate(-p.getCenter().getX(), -p.getCenter().getY());
            }
        } else {
            for (Segment2 s : splinters) 
                gc.strokeLine(s.getV1().getX(), s.getV1().getY(), s.getV2().getX(), s.getV2().getY());
        }
    }
    
    public void setRotateDirection(int rotateDirection) {
        this.rotateDirection = rotateDirection;
    }
    
    public void turnOnTheEngine() {
        if (!engine) {
            SoundStore.JET_ENGINE.play(true);
            to1.reset();
            engine = true;
            reactiveForce.setLocal(dir.getX() * REACTIVE_FORCE, dir.getY() * REACTIVE_FORCE);
        }
    }
    
    public void turnOffTheEngine() {                
        engine = false;
        SoundStore.JET_ENGINE.stop();
        reactiveForce.setLocal(0, 0);
    }
    
    private int bulletsInHolder = BULLETS_IN_HOLDER;
    private final TimeStamp lastShotTime = new TimeStamp();
    
    public Shot blast() {
        if (bulletsInHolder != 0) {
            bulletsInHolder--;
            lastShotTime.reset();
            float x = p.getCenter().getX() + p.getVerticeBy(0).getX();
            float y = p.getCenter().getY() + p.getVerticeBy(0).getY();
            return new Shot(
                new Vector2(x, y), 
                dir.copy(),
                Shot.SPACE_SHIP_SHOT
            );
        }
        return null;
    }
    
    public void hyperSpaceJump() {
        float x = General.getRandomFloatInRange(0, App.WIDTH - 1);
        float y = General.getRandomFloatInRange(0, App.HEIGHT - 1);
        p.getCenter().setLocal(x, y);
    }
    
    private static final float SCALE = 5F;
 
    public static SpaceShip getSpaceShip(Vector2 center) {
        Vector2[] ship = new Vector2[SHIP.length];
        for (int i = 0; i < SHIP.length; i++) ship[i] = new Vector2(SHIP[i]);
        for (Vector2 v : ship) v.scaleLocal(SCALE, SCALE);
        Polygon2 p = new Polygon2(center, ship);
        Vector2[] gases = new Vector2[GASES.length];
        for (int i = 0; i < GASES.length; i++) gases[i] = new Vector2(GASES[i]);
        for (Vector2 v : gases) v.scaleLocal(SCALE, SCALE);
        return new SpaceShip(p, gases);
    }
    
}
