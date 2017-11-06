package asteroids.game_objects;

import asteroids.App;
import asteroids.SoundStore;
import asteroids.utils.General;
import static asteroids.utils.General.PI;
import asteroids.utils.Polygon2;
import asteroids.utils.TimeStamp;
import asteroids.utils.Vector2;
import javafx.scene.canvas.GraphicsContext;

/**
 *
 * @author Texhnolyze
 */
public class UFO {
    
    private static final Vector2[] UFO = {
        new Vector2(1F, -2F),
        new Vector2(2F, -1F),
        new Vector2(4F, 0F),
        new Vector2(2F, 1F),
        new Vector2(-2F, 1F),
        new Vector2(-4F, 0F),
        new Vector2(-2F, -1F),
        new Vector2(-1F, -2F)
    };
    
    public static final int SMALL_UFO = 0;
    public static final int BIG_UFO     = 1;
    
    private static final float[] UFO_V = { 1.3F, 1.5F };
    private static final float[] UFO_SCALE = { 2.5F, 3.5F };
    
    private static final int TIME_BETWEEN_SHOTS_MS = 1000;
    
    private final Polygon2 p;
    private final Vector2 vel;
    
    private final int type;
    
    private final int appearsFrom;
    
    private UFO(Polygon2 p, Vector2 vel, int type, int appearsFrom) {
        this.p = p;
        this.vel = vel;
        this.type = type;
        this.appearsFrom = appearsFrom;
    }
    
    public int getType() {
        return type;
    }
    
    public Polygon2 getPoly() {
        return p;
    }
    
    private Vector2 destroyedAt;
    
    public Vector2 getWhereWasDestroyed() {
        return destroyedAt;
    }
    
    public boolean isDestroyedBy(Shot s) {
        if (s.getType() == Shot.SPACE_SHIP_SHOT) {
            Vector2 v = p.getIntersectionPointWith(s.getLastPath());
            if (v != null) {
                destroyedAt = v;
                return true;
            }
        }
        return false;
    }
    
    public boolean isValid() {
        float x = p.getCenter().getX();
        if (appearsFrom == APPEARS_FROM_LEFT) 
            return x + p.getVerticeBy(5).getX() <= App.WIDTH;
        else 
            return x + p.getVerticeBy(2).getX() >= 0;
    }
    
    public void update() {
        Vector2 v = p.getCenter();
        v.addLocal(vel);
        v.closureLocalY();
        boolean b = Math.random() > 0.98D;
        if (b) vel.setLocalY(-vel.getY());
    }   
    
    public void draw(GraphicsContext gc) {
        p.draw(gc);
        gc.translate(p.getCenter().getX(), p.getCenter().getY());
        Vector2 p1 = p.getVerticeBy(1);
        Vector2 p6 = p.getVerticeBy(6);
        gc.strokeLine(p1.getX(), p1.getY(), p6.getX(), p6.getY());
        Vector2 p2 = p.getVerticeBy(2);
        Vector2 p5 = p.getVerticeBy(5);
        gc.strokeLine(p2.getX(), p2.getY(), p5.getX(), p5.getY());
        gc.translate(-p.getCenter().getX(), -p.getCenter().getY());
    }
    
    private final TimeStamp lastShot = new TimeStamp();
    
    public Shot blast(SpaceShip ss) {
        if (lastShot.passed(TIME_BETWEEN_SHOTS_MS)) {
            lastShot.reset();
            Vector2 v = ss.getCenter().sub(p.getCenter()).normalizeLocal();
            if (type == BIG_UFO) v.rotateLocal(General.getRandomFloatInRange(-PI, PI));
            return new Shot(p.getCenter().copy(), v, type + 1);
        }
        return null;
    }
    
    private static final int APPEARS_FROM_LEFT      = 0;
    private static final int APPEARS_FROM_RIGHT    = 1;
    
    private static final int[] APPEARS_FROM_X = {
        0, App.WIDTH - 1
    };
    
    private static final Vector2[] APPEARS_FROM_V = {
        new Vector2(1F, 0F),
        new Vector2(-1F, 0F)
    };
    
    public static UFO getUFO(int type) {
        int appearsFrom = General.getRandomIntegerInRange(
                APPEARS_FROM_LEFT, 
                APPEARS_FROM_RIGHT
        );
        float scale = UFO_SCALE[type];
        float x = APPEARS_FROM_X[appearsFrom];
        float y = (float) (Math.random() * App.HEIGHT);
        Vector2 v = new Vector2(x, y);
        Vector2[] vertices = new Vector2[UFO.length];
        for (int i = 0; i < UFO.length; i++) {
            vertices[i] = UFO[i].copy();
            vertices[i].scaleLocal(scale, scale);
        }
        Polygon2 p = new Polygon2(v, vertices);
        Vector2 vel = APPEARS_FROM_V[appearsFrom].copy();
        vel.scaleLocal(UFO_V[type], UFO_V[type]);
        vel.rotateLocal(General.getRandomFloatInRange(-PI / 4F, PI / 4F));
        SoundStore.UFO_SIRENS[type].play(true);
        return new UFO(p, vel, type, appearsFrom);
    }
    
}
