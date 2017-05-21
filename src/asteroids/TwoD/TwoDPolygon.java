package asteroids.TwoD;

import asteroids.Utils;
import java.util.Arrays;
import java.util.Iterator;

public class TwoDPolygon implements Iterable<TwoDSegment> {

    private final TwoDVector _center;
    private final TwoDSegment[] edges;
    private final TwoDVector[] _vertices;
    private final float maxDistanceFromCenter;
    
    public TwoDPolygon(TwoDVector[] vertices, TwoDVector center) {
        _vertices = vertices;
        float max = 0;
        int edgesCount = _vertices.length - 1;
        edges = new TwoDSegment[edgesCount];
        for (int i = 0; i < edgesCount; i++) {
            float radius = _vertices[i].getLength();
            max = Math.max(max, radius);
            edges[i] = new TwoDSegment(_vertices[i], _vertices[i + 1]);
        }
        max = Math.max(max, _vertices[edgesCount].getLength());
        edges[edgesCount - 1] = new TwoDSegment(_vertices[0], _vertices[edgesCount]);
        _center = center;
        maxDistanceFromCenter = max;
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
    
    public TwoDVector getMidpointOf(int edgeNumber) {
        return edges[edgeNumber].getMidpoint();
    }
    
    public void rotate(float radianAngle) {
        for (TwoDSegment edge : edges) edge.getFirstPoint().rotate(radianAngle);
    }
    
    public void transfer(float dx, float dy) {
        _center.transfer(dx, dy);
    }
    
    public boolean isThePointInside(TwoDVector pointRadiusVector) {
        TwoDVector relative = pointRadiusVector.getRelativeTo(_center);
        if (relative.getLength() > maxDistanceFromCenter) return false;
        TwoDVector rawStartPoint = new TwoDVector(relative.getXComponent(), relative.getYComponent());
        TwoDVector rawEndPoint = new TwoDVector(2 * maxDistanceFromCenter,
                2 * maxDistanceFromCenter);
        TwoDSegment raw = new TwoDSegment(rawStartPoint, rawEndPoint);
        int counter = 0;
        for (TwoDSegment edge : edges) if (raw.doesIntersectWith(edge)) counter++;
        return counter % 2 != 0;
    }

    public static TwoDPolygon randomPolygone(int verticesCount, TwoDVector center, 
            float minDistanceFromCenter, float maxDistanceFromCenter) {
        TwoDVector[] vertices = new TwoDVector[verticesCount];
        for (int i = 0; i < verticesCount; i++) {
            float x = Utils.randomizeFloatNumberSign(
                    Utils.getRandomFloatInRange(minDistanceFromCenter, maxDistanceFromCenter));
            float y = Utils.randomizeFloatNumberSign(
                    Utils.getRandomFloatInRange(minDistanceFromCenter, maxDistanceFromCenter));
            TwoDVector next = new TwoDVector(x, y);
            vertices[i] = next;
        }
        TwoDPolygon polygon = new TwoDPolygon(vertices, center); 
        return polygon;
    }

    @Override
    public Iterator<TwoDSegment> iterator() {
        return Arrays.asList(edges).iterator();
    }
    
}
