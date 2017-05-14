package asteroids.TwoD;

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
    
    public void rotate(float radiansAngle) {
        xComponent = (float) ((xComponent * Math.cos(radiansAngle)) - (yComponent * Math.sin(radiansAngle)));
        yComponent = (float) ((xComponent * Math.sin(radiansAngle)) + (yComponent * Math.cos(radiansAngle)));
    }
    
    
    
}
