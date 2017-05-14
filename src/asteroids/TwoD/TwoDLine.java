package asteroids.TwoD;

public class TwoDLine {
    
    private final TwoDPoint _firstPoint;
    private final TwoDPoint _secondPoint;
    
    public TwoDLine(TwoDPoint firstPoint, TwoDPoint secondPoint) {
        _firstPoint = firstPoint;
        _secondPoint = secondPoint;
    }

    public TwoDPoint getFirstPoint() {
        return _firstPoint;
    }

    public TwoDPoint getSecondPoint() {
        return _secondPoint;
    }
    
    public float getSlope() {
        return (_secondPoint.getX() - _firstPoint.getX()) / (_secondPoint.getY() - _firstPoint.getY());
    }
    
    public TwoDPoint getIntersectionPointOnTheRay(TwoDLine other) {
        float x1 = _firstPoint.getX();
        float y1 = _firstPoint.getY();
        float x2 = _secondPoint.getX();
        float y2 = _secondPoint.getY();
        float x3 = other._firstPoint.getX();
        float y3 = other._firstPoint.getY();
        float x4 = other._secondPoint.getX();
        float y4 = other._secondPoint.getY();
        float x = ((x1 * y2 - x2 * y1) * (x4 - x3) - (x3 * y4 - x4 * y3) * (x2 - x1)) 
                / ((y1 - y2) * (x4 - x3) - (y3 - y4) * (x2 - x1));
        float y = ((y3 - y4) * x - (x3 * y4 - x4 * y3)) / (x4 - x3);
        return new TwoDPoint(x, y);
    }
    
}
