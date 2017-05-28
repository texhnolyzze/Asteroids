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
    
    public void setX(float x) {
        xComponent = x;
    }
    
    public void setY(float y) {
        yComponent = y;
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
    
    public float squareDistanceBetween(TwoDVector other) {
        return (float) 
                (Math.pow(other.xComponent - xComponent, 2) + Math.pow(other.yComponent - yComponent, 2));
    }
    
    public TwoDVector getReversed() {
        return new TwoDVector(-xComponent, -yComponent);
    }
    
    public boolean isNullVector() {
        return Utils.floatEquals(getSquareLength(), 0F);
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
    
    public float getSquareLength() {
        return xComponent * xComponent + yComponent * yComponent;
    }
    
    public void transfer(float dx, float dy) {
        xComponent += dx;
        yComponent += dy;
    }
    
    public boolean transferWithPossibleClosure(float dx, float dy, int xBound, int yBound) {
        boolean xClosure = true, yClosure = true;
        float xTransfer = xComponent + dx;
        float yTransfer = yComponent + dy;
        if (xTransfer < 0) xComponent += (xBound - Math.abs(dx));
        else if (xTransfer > xBound) xComponent -= (xBound - Math.abs(dx));
        else {
            xComponent = xTransfer;
            xClosure = false;
        }
        if (yTransfer < 0) yComponent += (yBound - Math.abs(dy));
        else if (yTransfer > yBound) yComponent -= (yBound - Math.abs(dy));
        else {
            yComponent = yTransfer;
            yClosure = false;
        }
        return xClosure || yClosure;
    }
    
    public void rotate(float radiansAngle) {
        float newX = (float) (xComponent * Math.cos(radiansAngle) - yComponent * Math.sin(radiansAngle));
        float newY = (float) (xComponent * Math.sin(radiansAngle) + yComponent * Math.cos(radiansAngle));
        xComponent = newX;
        yComponent = newY;
    }

    public TwoDVector copy() {
        return new TwoDVector(xComponent, yComponent);
    }

    @Override
    public String toString() {
        return "(" + xComponent + "; " + yComponent + ')';
    }

    @Override
    public int hashCode() {
        int hash = 5;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final TwoDVector other = (TwoDVector) obj;
        if (!Utils.floatEquals(xComponent, other.xComponent)) return false;
        return Utils.floatEquals(yComponent, other.yComponent);
    }
    
    public static TwoDVector getRandomUnitVector() {
        float x = (float) Math.random();
        float y = 1F - x;
        return new TwoDVector(
                Utils.randomizeFloatNumberSign(x), 
                Utils.randomizeFloatNumberSign(y)
        );
    }
    
    public static TwoDVector getRandomInBounds(float rightBound, float bottomBound) {
        return new TwoDVector(
                Utils.getRandomFloatInRange(0, rightBound), 
                Utils.getRandomFloatInRange(0, bottomBound)
        );
    }
    
    public static TwoDVector getRandomInBoundsAwayFromCenter(float minHowMuchFrom, 
            float rightBound, float bottomBound) {
        TwoDVector unit = getRandomUnitVector();
        unit.scale(minHowMuchFrom);
        float x = Utils.randomizeFloatNumberSign(1);
        float y = Utils.randomizeFloatNumberSign(1);
        return new TwoDVector(
                Utils.getRandomFloatInRange(rightBound / 2 + (x * unit.xComponent), x == -1 ? 0 : rightBound),
                Utils.getRandomFloatInRange(bottomBound / 2 + (y * unit.yComponent), y == - 1 ? 0 : bottomBound)
        );
    }
    
}
