
package asteroids.utils;

import static asteroids.utils.General.BOUNDS;
import static asteroids.utils.General.EPS;
import static asteroids.utils.General.GLOBAL_BOUND;
import static asteroids.utils.General.LEFT_BOUND;
import static asteroids.utils.General.RIGHT_BOUND;
import static asteroids.utils.General.TOP_BOUND;
import javafx.scene.canvas.GraphicsContext;
import javafx.util.Pair;

/**
 *
 * @author Texhnolyze
 */
public class Segment2 {
    
    private final Vector2 v1, v2;
    
    public Segment2() {
        this.v1 = new Vector2();
        this.v2 = new Vector2();
    }
    
    public Segment2(Vector2 v1, Vector2 v2) {
        this.v1 = v1;
        this.v2 = v2;
    }
    
    public Vector2 getV1() {
        return v1;
    }
    
    public Vector2 getV2() {
        return v2;
    }
    
    public float len2() {
        return v1.distance2Between(v2);
    }
    
    public void draw(GraphicsContext gc) {
        gc.strokeLine(v1.getX(), v1.getY(), v2.getX(), v2.getY());
    }
    
    
//  This method is needed for cases when one of the ends of a bullet path 
//  is outside the playing field, but it works with errors and I do not know why ...
    public Pair<Segment2, Segment2> getPartsIfOneOfTheEndsIsInvalid() {
        boolean v1v = v1.isValid();
        boolean v2v = v2.isValid();
        if (v1v) {
            if (v2v) return null;
            else {
                for (Segment2 b : BOUNDS) {
                    Vector2 v = this.getIntersectionPointWith(b);
                    if (v != null) {
                        Segment2 s1 = new Segment2(v1, v), s2;
                        Vector2 vv;
                        if (b == LEFT_BOUND)
                            vv = v.setX(GLOBAL_BOUND.getX() - EPS);
                        else if (b == TOP_BOUND)
                            vv = v.setY(GLOBAL_BOUND.getY() - EPS);
                        else if (b == RIGHT_BOUND) 
                            vv = v.setX(0);
                        else 
                            vv = v.setY(0);
                        s2 = new Segment2(vv, v2.closure());
                        return new Pair<>(s1, s2);
                    }
                }
            }
        } else if (v2v) {
            for (Segment2 b : BOUNDS) {
                    Vector2 v = this.getIntersectionPointWith(b);
                    if (v != null) {
                        Segment2 s1 = new Segment2(v2, v), s2;
                        Vector2 vv;
                        if (b == LEFT_BOUND)
                            vv = v.setX(GLOBAL_BOUND.getX() - EPS);
                        else if (b == TOP_BOUND)
                            vv = v.setY(GLOBAL_BOUND.getY() - EPS);
                        else if (b == RIGHT_BOUND) 
                            vv = v.setX(0);
                        else 
                            vv = v.setY(0);
                        s2 = new Segment2(vv, v1.closure());
                        return new Pair<>(s1, s2);
                    }
                }
        } else {
            v1.closureLocal();
            v2.closureLocal();
        }
        return null;
    }
    
    public Segment2 closureLocalIfBothOfTheEndsAreInvalid() {
        if (!v1.isValid() && !v2.isValid()) {
            v1.closureLocal();
            v2.closureLocal();
        }
        return this;
    }
    
    public Vector2 getPointBy(float k) {
        float x = v1.getX() + (v2.getX() - v1.getX()) * k;
        float y = v1.getY() + (v2.getY() - v1.getY()) * k;
        return new Vector2(x, y);
    }
    
    public Vector2 getIntersectionPointWith(Segment2 s) {
        float x1 = v1.getX(), y1 = v1.getY(), x2 = v2.getX(), y2 = v2.getY();
        float x3 = s.v1.getX(), y3 = s.v1.getY(), x4 = s.v2.getX(), y4 = s.v2.getY();
        float a11 = x2 - x1, a12 = x4 - x3, a21 = y2 - y1, a22 = y4 - y3;
        float b1 = x3 - x1, b2 = y3 - y1;
        float det = a11 * a22 - a12 * a21;
        if (det != 0) {
            float k1 = (b1 * a22 - b2 * a12) / det;
            if (k1 >= 0 && k1 <= 1) {
                float k2 = (a11 * b2 - a21 * b1) / det;
                if (k2 >= 0 && k2 <= 1)
                    return this.getPointBy(k1);
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "(" + v1.toString() + ", " + v2.toString() + ")";
    }
    
}
