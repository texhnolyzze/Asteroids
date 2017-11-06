
package asteroids.utils;

import static asteroids.utils.General.GLOBAL_BOUND;
import static asteroids.utils.General.getRandomFloatInRange;
import static asteroids.utils.General.randomizeSign;

/**
 *
 * @author Texhnolyze
 */
public class Vector2 {

    private float x, y;
    
    public Vector2() {
        
    }
    
    public Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }
    
    public Vector2(Vector2 v) {
        x = v.x;
        y = v.y;
    }
    
    public Vector2 copy() {
        return new Vector2(x, y);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public Vector2 setX(float x) {
        return new Vector2(x, this.y);
    }

    public Vector2 setY(float y) {
        return new Vector2(this.x, y);
    }
    
    public Vector2 setLocalX(float x) {
        this.x = x;
        return this;
    }
    
    public Vector2 setLocalY(float y) {
        this.y = y;
        return this;
    }
    
    public Vector2 setLocal(Vector2 v) {
        return setLocal(v.x, v.y);
    }
    
    public Vector2 setLocal(float x, float y) {
        this.x = x;
        this.y = y;
        return this;
    }
    
    public Vector2 normalizeLocal() {
        float len = len();
        x = x / len;
        y = y / len;
        return this;
    }
    
    public Vector2 normalize() {
        float len = len();
        return new Vector2(x / len, y / len);
    }
    
    public Vector2 inverse() {
        return new Vector2(-x, -y);
    }
    
    public Vector2 inverseLocal() {
        x = -x;
        y = -y;
        return this;
    }
    
    public float len() {
        return (float) Math.sqrt(len2());
    }
    
    public float len2() {
        return x * x + y * y;
    }
    
    public float distance2Between(Vector2 v) {
        return distance2Between(v.x, v.y);
    }
    
    public float distance2Between(float x, float y) {
        float xx = this.x - x;
        float yy = this.y - y;
        return xx * xx + yy * yy;
    }
    
    public Vector2 add(Vector2 v) {
        return add(v.x, v.y);
    }
    
    public Vector2 add(float x, float y) {
        return new Vector2(this.x + x, this.y + y);
    }
    
    public Vector2 addLocal(Vector2 v) {
        return addLocal(v.x, v.y);
    }
    
    public Vector2 addLocal(float x, float y) {
        this.x += x;
        this.y += y;
        return this;
    }
    
    public Vector2 sub(Vector2 v) {
        return sub(v.x, v.y);
    }
    
    public Vector2 sub(float x, float y) {
        return add(-x, -y);
    }
    
    public Vector2 subLocal(Vector2 v) {
        return subLocal(v.x, v.y);
    } 
    
    public Vector2 subLocal(float x, float y) {
        return addLocal(-x, -y);
    }
    
    public Vector2 scale(Vector2 v) {
        return scale(v.x, v.y);
    }
    
    public Vector2 scale(float sx, float sy) {
        return new Vector2(x * sx, y * sy);
    }
    
    public Vector2 scaleLocal(Vector2 v) {
        return scaleLocal(v.x, v.y);
    }
    
    public Vector2 scaleLocal(float sx, float sy) {
        this.x *= sx;
        this.y *= sy;
        return this;
    }
    
    public Vector2 scaleTo(float len) {
        float l = len();
        return new Vector2((x / l) * len, (y / l) * len);
    }
    
    public Vector2 scaleLocalTo(float len) {
        float l = len();
        if (l != 0) {
            x = (x / l) * len;
            y = (y / l) * len;
        }
        return this;
    }
    
    public Vector2 rotate(float alpha) {
        float ca = (float) Math.cos(alpha), sa = (float) Math.sin(alpha);
        return new Vector2(x * ca - y * sa, x * sa + y * ca);
    }
    
    public Vector2 rotateLocal(float alpha) {
        float ca = (float) Math.cos(alpha), sa = (float) Math.sin(alpha);
        float rx = x * ca - y * sa, ry = x * sa + y * ca;
        return setLocal(rx, ry);
    }
    
    public float dot(Vector2 v) {
        return dot(v.x, v.y);
    }
    
    public float dot(float x, float y) {
        return x * this.x + y * this.y;
    }
    
    public float cross(Vector2 v) {
        return cross(v.x, v.y);
    }
    
    public float cross(float x, float y) {
        return this.x * y - this.y * x;
    }
    
    public Vector2 closure() {
        return new Vector2(
            Math.abs((x + GLOBAL_BOUND.getX()) % GLOBAL_BOUND.getX()),
            Math.abs((y + GLOBAL_BOUND.getY()) % GLOBAL_BOUND.getY())
        );
    }
    
    public Vector2 closureLocal() {
        x = Math.abs((x + GLOBAL_BOUND.getX()) % GLOBAL_BOUND.getX());
        y = Math.abs((y + GLOBAL_BOUND.getY()) % GLOBAL_BOUND.getY());
        return this;
    }
    
    public Vector2 closureX() {
        return new Vector2(
            Math.abs((x + GLOBAL_BOUND.getX()) % GLOBAL_BOUND.getX()),
            y
        );
    }
    
    public Vector2 closureY() {
        return new Vector2(
            x,
            y = Math.abs((y + GLOBAL_BOUND.getY()) % GLOBAL_BOUND.getY())
        );
    }
    
    public Vector2 closureLocalX() {
        x = Math.abs((x + GLOBAL_BOUND.getX()) % GLOBAL_BOUND.getX());
        return this;
    }
    
    public Vector2 closureLocalY() {
        y = Math.abs((y + GLOBAL_BOUND.getY()) % GLOBAL_BOUND.getY());
        return this;
    }
    
    public boolean isValid() {
        return x >= 0 && x <= GLOBAL_BOUND.getX() && y >= 0 && y <= GLOBAL_BOUND.getY();
    }
    
    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
    
    public static Vector2 getRandomUnitVector() {
        float x = getRandomFloatInRange(0, 1);
        float y = (float) Math.sqrt(1 - x * x);
        return new Vector2(randomizeSign(x), randomizeSign(y));
    }
    
}
