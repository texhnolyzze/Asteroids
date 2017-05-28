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
    
    public float getLength() {
        return (float) Math.sqrt(_firstPoint.squareDistanceBetween(_secondPoint));
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
    
    public TwoDSegment getAbsolute(TwoDVector respectToWhich) {
        return new TwoDSegment(
                _firstPoint.getAbsolute(respectToWhich), 
                _secondPoint.getAbsolute(respectToWhich)
        );
    }
    
    public TwoDSegment getRelativeTo(TwoDVector other) {
        return new TwoDSegment(
                _firstPoint.getRelativeTo(other),
                _secondPoint.getRelativeTo(other)
        );
    }
    
    public TwoDVector getDirectionFrom(TwoDVector from) {
        if (from.equals(_firstPoint)) 
            return _secondPoint.getRelativeTo(_firstPoint).getNormalize();
        else return _firstPoint.getRelativeTo(_secondPoint).getNormalize();
    }
    
    public TwoDSegment[] breakIntoEqualSegments(int parts) {
        TwoDSegment[] segments = new TwoDSegment[parts];
        float partitionStep = getLength() / parts;
        TwoDVector partitionDirection = _secondPoint.getRelativeTo(_firstPoint).getNormalize();
        for (int i = 1; i <= parts; i++) {
            TwoDVector temp = partitionDirection.copy();
            temp.scale(partitionStep);
            segments[i - 1] = new TwoDSegment(
                    new TwoDVector(
                            _firstPoint.getXComponent() + (i - 1) * temp.getXComponent(),
                            _firstPoint.getYComponent() + (i - 1) * temp.getYComponent()
                    ),
                    new TwoDVector(
                            _firstPoint.getXComponent() + i * temp.getXComponent(),
                            _firstPoint.getYComponent() + i * temp.getYComponent()
                    )
            );
        }
        return segments;
    }
    
    public TwoDVector[] getAllPoints() {
        TwoDVector[] points = new TwoDVector[(int) getLength()];
        TwoDVector partitionDirection = _secondPoint.getRelativeTo(_firstPoint).getNormalize();
        for (int i = 0; i < points.length; i++) {
            TwoDVector temp = partitionDirection.copy();
            temp.scale(i);
            points[i] = new TwoDVector(
                    _firstPoint.getXComponent() + temp.getXComponent(),
                    _firstPoint.getYComponent() + temp.getYComponent()
            );
        }
        return points;
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
    
    public TwoDVector intersectWith(TwoDSegment other) {
        if (Utils.floatEquals(k, other.k)) return null;
        TwoDVector intersection = getIntersectionPointOnTheRaysWith(other);
        if (isThePointBelongingToTheLineLyingOnTheSegment(intersection) && 
                other.isThePointBelongingToTheLineLyingOnTheSegment(intersection)) 
            return intersection;
        return null;
    }

    @Override
    public String toString() {
        return "{" + _firstPoint + "; " + _secondPoint + '}';
    }
    
}
