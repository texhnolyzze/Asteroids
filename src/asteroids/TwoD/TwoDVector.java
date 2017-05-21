package asteroids.TwoD;

import asteroids.Utils;

public class TwoDVector {
    
    private float xComponent;
    private float yComponent;
    
    public TwoDVector(float x, float y) {
        xComponent = x;
        yComponent = y;
    }
    
    public float getXComponent() {
        return xComponent;
    }
    
    public float getYComponent() {
        return yComponent;
    }
    
    public int getXAsInteger() {
        return (int) xComponent;
    }
    
    public int getYAsInteger() {
        return (int) yComponent;
    }
    
    public void scale(float scale) {
        xComponent *= scale;
        yComponent *= scale;
    }

    public void xScale(float xScale) {
        xComponent *= xScale;
    }
    
    public void yScale(float yScale) {
        yComponent *= yScale;
    }
    
    public TwoDVector getReversed() {
        return new TwoDVector(-xComponent, -yComponent);
    }
    
    public boolean isNullVector() {
        return Utils.floatEquals(xComponent, 0F) && Utils.floatEquals(yComponent, 0F);
    }
    
    public TwoDVector getRelativeTo(TwoDVector other) {
        return new TwoDVector(xComponent - other.xComponent, yComponent - other.yComponent);
    }
    
    public TwoDVector getAbsolute(TwoDVector respectToWhich) {
        return new TwoDVector(
                respectToWhich.xComponent + xComponent, 
                respectToWhich.yComponent + yComponent
        );
    }
    
    public TwoDVector getNormalize() {
        float length = getLength();
        float xNormalize = xComponent / length;
        float yNormalize = yComponent / length;
        return new TwoDVector(xNormalize, yNormalize);
    }
    
    public float getLength() {
        return (float) Math.sqrt(xComponent * xComponent + yComponent * yComponent);
    }
    
    public void transfer(float dx, float dy) {
        xComponent += dx;
        yComponent += dy;
    }
    
    public void transferWithPossibleClosure(float dx, float dy, int xBound, int yBound) {
        float xTransfer = xComponent + dx;
        float yTransfer = yComponent + dy;
        if (xTransfer < 0) xComponent += (xBound - Math.abs(dx));
        else if (xTransfer > xBound) xComponent -= (xBound - Math.abs(dx));
        else xComponent = xTransfer;
        if (yTransfer < 0) yComponent += (yBound - Math.abs(dy));
        else if (yTransfer > yBound) yComponent -= (yBound - Math.abs(dy));
        else yComponent = yTransfer;
    }
    
    public void rotate(float radiansAngle) {
        xComponent = (float) ((xComponent * Math.cos(radiansAngle)) - (yComponent * Math.sin(radiansAngle)));
        yComponent = (float) ((xComponent * Math.sin(radiansAngle)) + (yComponent * Math.cos(radiansAngle)));
    }

    public TwoDVector copy() {
        return new TwoDVector(xComponent, yComponent);
    }

    @Override
    public String toString() {
        return "TwoDVector{" + "xComponent=" + xComponent + ", yComponent=" + yComponent + '}';
    }
    
    public static TwoDVector getRandomUnitVector() {
        float x = Utils.getRandomFloatInRange(0, 1);
        float y = 1F - x;
        return new TwoDVector(Utils.randomizeFloatNumberSign(x), Utils.randomizeFloatNumberSign(y));
    }
    
}
