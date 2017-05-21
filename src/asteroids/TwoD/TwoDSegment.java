package asteroids.TwoD;

import asteroids.Utils;

public class TwoDSegment {
    
    private final TwoDVector _firstPoint;
    private final TwoDVector _secondPoint;
    
    private final float k;
    private final float b;
    
    public TwoDSegment(TwoDVector firstPoint, TwoDVector secondPoint) {
        _firstPoint = firstPoint;
        _secondPoint = secondPoint;
        k = (_firstPoint.getYComponent() - _secondPoint.getYComponent()) / 
                (_firstPoint.getXComponent() - _secondPoint.getXComponent());
        b = _firstPoint.getYComponent() - (k * _firstPoint.getXComponent());
    }

    public TwoDVector getFirstPoint() {
        return _firstPoint;
    }

    public TwoDVector getSecondPoint() {
        return _secondPoint;
    }
    
    public float getSlope() {
        return k;
    }
    
    public TwoDVector getMidpoint() {
        return new TwoDVector(
                (_firstPoint.getXComponent() + _secondPoint.getXComponent()) / 2,
                (_firstPoint.getYComponent() + _secondPoint.getYComponent()) / 2
        );
    }
    
    public TwoDSegment copy() {
        return new TwoDSegment(
                _firstPoint.copy(), 
                _secondPoint.copy()
        );
    }
    
    public TwoDVector getIntersectionPointOnTheRaysWith(TwoDSegment other) {
        float x = (other.b - b) / (k - other.k);
        float y = k * x + b;
        return new TwoDVector(x, y);
    }
    
    public boolean isThePointLyingOnTheSegment(TwoDVector pointRadiusVector) {
        return isThePointLyingOnTheLine(pointRadiusVector) && 
                isThePointBelongingToTheLineLyingOnTheSegment(pointRadiusVector);
    }
    
    public boolean isThePointLyingOnTheLine(TwoDVector pointRadiusVector) {
        float x = pointRadiusVector.getXComponent();
        float y = pointRadiusVector.getYComponent();
        float x1 = _firstPoint.getXComponent();
        float y1 = _firstPoint.getYComponent();
        float x2 = _secondPoint.getXComponent();
        float y2 = _secondPoint.getYComponent();
        return Utils.floatEquals((x - x1) / (x2 - x1), (y - y1) / (y2 - y1));
    }
    
    public boolean isThePointBelongingToTheLineLyingOnTheSegment(TwoDVector pointOnTheLine) {
        float x = pointOnTheLine.getXComponent();
        float x1 = _firstPoint.getXComponent();
        float x2 = _secondPoint.getXComponent();
        return (x >= x1 && x <= x2) || (x <= x1 && x >= x2);
    }
    
    public boolean doesIntersectWith(TwoDSegment other) {
        if (Utils.floatEquals(k, other.k)) return false;
        TwoDVector intersection = getIntersectionPointOnTheRaysWith(other);
        return isThePointBelongingToTheLineLyingOnTheSegment(intersection) 
                && other.isThePointBelongingToTheLineLyingOnTheSegment(intersection);
    }
    
}
