package asteroids.TwoD;

import java.util.Arrays;
import java.util.Iterator;

public class TwoDPolygon implements Iterable<TwoDSegment> {

    private final TwoDVector _center;
    private final TwoDSegment[] edges;
    private final TwoDVector[] _vertices;
    private float maxSquareDistanceFromCenter;
    
    public TwoDPolygon(TwoDVector[] vertices, TwoDVector center) {
        _vertices = vertices;
        _center = center;
        edges = new TwoDSegment[_vertices.length];
        init();
    }
    
    private void init() {
        float max = 0;
        for (int i = 0; i < edges.length - 1; i++) {
            float squareRadius = _vertices[i].getSquareLength();
            max = Math.max(max, squareRadius);
            edges[i] = new TwoDSegment(_vertices[i], _vertices[i + 1]);
        }
        max = Math.max(max, _vertices[edges.length - 1].getSquareLength());
        edges[edges.length - 1] = new TwoDSegment(_vertices[0], _vertices[edges.length - 1]);
        maxSquareDistanceFromCenter = max;
    }
    
    public int verticesCount() {
        return edges.length + 1;
    }
    
    public int edgesCount() {
        return edges.length;
    }
    
    public float getCenterX() {
        return _center.getXComponent();
    }
    
    public float getCenterY() {
        return _center.getYComponent();
    }
    
    public TwoDVector[] getVertices() {
        return _vertices;
    }
    
    public float getMaxSquareDistanceFromCenter() {
        return maxSquareDistanceFromCenter;
    }
    
    public TwoDVector getMidpointOf(int edgeNumber) {
        return edges[edgeNumber].getMidpoint();
    }
    
    public void rotate(float radianAngle) {
        for (TwoDVector v : _vertices) v.rotate(radianAngle);
    }
    
    public void transfer(float dx, float dy) {
        _center.transfer(dx, dy);
    }
    
    public boolean isThePointInside(TwoDVector pointRadiusVector) {
        TwoDVector relative = pointRadiusVector.getRelativeTo(_center);
        if (relative.getSquareLength() > maxSquareDistanceFromCenter) return false;
        TwoDVector rawStartPoint = new TwoDVector(relative.getXComponent(), relative.getYComponent());
        TwoDVector rawEndPoint = TwoDVector.getRandomUnitVector();
        rawEndPoint.scale(maxSquareDistanceFromCenter);
        TwoDSegment raw = new TwoDSegment(rawStartPoint, rawEndPoint);
        int counter = 0;
        for (TwoDSegment edge : this) if (raw.intersectWith(edge) != null) counter++;
        return counter % 2 != 0;
    }
    
    public TwoDVector intersectWith(TwoDSegment segment) {
        TwoDSegment relative = segment.getRelativeTo(_center);
        for (TwoDSegment s : this) {
            TwoDVector intersection = s.intersectWith(relative);
            if (intersection != null) {
                return intersection.getAbsolute(_center);
            }
        }
        return null;
    }

    @Override
    public Iterator<TwoDSegment> iterator() {
        return Arrays.asList(edges).iterator();
    }
    
}
