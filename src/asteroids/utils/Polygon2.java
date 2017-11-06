
package asteroids.utils;

import javafx.scene.canvas.GraphicsContext;


/**
 *
 * @author Texhnolyze
 */
public class Polygon2 {
    
    private Vector2 center;
    private float dFromCenter2;

    private Segment2[] edges;
    
    public Polygon2(Vector2 center, Segment2[] edges) {
        this.center = center;
        this.edges = edges;
        for (int i = 0; i < edges.length; i++) {
            float d2 = edges[i].getV1().len2();
            dFromCenter2 = Math.max(dFromCenter2, d2);
        }
    }
    
    public Polygon2(Vector2 center, Vector2[] vertices) {
        this(center, convertVerticesToEdges(vertices));
    }
    
    public Segment2 getEdgeBy(int id) {
        return edges[id];
    }
    
    public Vector2 getVerticeBy(int id) {
        return edges[id].getV1();
    }
    
    public Vector2 getCenter() {
        return center;
    }
    
    public float getMaxDistance2FromCenter() {
        return dFromCenter2;
    }
    
    public void translateCenter(Vector2 v) {
        center.addLocal(v);
        center.closureLocal();
    }
    
    public void rotate(float alpha) {
        for (Segment2 e : edges) 
            e.getV1().rotateLocal(alpha);
    }
    
    public void draw(GraphicsContext gc) {
        gc.translate(center.getX(), center.getY());
        for (Segment2 e : edges) e.draw(gc);
        gc.translate(-center.getX(), -center.getY());
    }
    
    public static Segment2[] convertVerticesToEdges(Vector2[] vertices) {
        Segment2[] edges = new Segment2[vertices.length];
        for (int i = 0; i < vertices.length; i++) {
            edges[i] = new Segment2(vertices[i], vertices[(i + 1) % vertices.length]);
        }
        return edges;
    }
    
    private static final Segment2 TEMP = new Segment2(new Vector2(), new Vector2());
    
    public Vector2 getIntersectionPointWith(Segment2 s) {
        Vector2 v = null;
        float cx = s.getV1().getX() + (s.getV2().getX() - s.getV1().getX()) * 0.5F;
        float cy = s.getV1().getY() + (s.getV2().getY() - s.getV1().getY()) * 0.5F;
        float d2 = center.distance2Between(cx, cy);
        if (d2 > dFromCenter2 + s.len2() / 4) return null;
        else {
            s.getV1().subLocal(center);
            s.getV2().subLocal(center);
            for (Segment2 edge : edges) {
                v = s.getIntersectionPointWith(edge);
                if (v == null) v = edge.getIntersectionPointWith(s);
                if (v != null) break;
            }
            s.getV1().addLocal(center);
            s.getV2().addLocal(center);
        }
        if (v != null) v.addLocal(center);
        return v;
    }
    
    public Vector2 getIntersectionPointWith(Polygon2 p) {
        Vector2 v = null;
        float d2 = center.distance2Between(p.center);
        if (d2 > dFromCenter2 + p.dFromCenter2) return null;
        else {
            Vector2 r = p.center.subLocal(center);
            for (Segment2 pEdge : p.edges) {
                pEdge.getV1().addLocal(r);
                pEdge.getV2().addLocal(r);
                for (Segment2 thisEdge : this.edges) {
                    v = thisEdge.getIntersectionPointWith(pEdge);
                    if (v != null) 
                        break;
                }
                pEdge.getV1().subLocal(r);
                pEdge.getV2().subLocal(r);
                if (v != null) 
                    break;
            }
            p.center.addLocal(center);
        }
        if (v != null) v.addLocal(center);
        return v;
    }
    
    public boolean isPointInside(Vector2 p) {
        if (p.distance2Between(center) > dFromCenter2) 
            return false;
        float v1x = p.getX() - center.getX(), v1y = p.getY() - center.getY();
        TEMP.getV1().setLocal(v1x, v1y);
        TEMP.getV2().setLocal(2 * dFromCenter2, 2 * dFromCenter2);
        boolean odd = false;
        for (Segment2 edge : edges) 
            if (edge.getIntersectionPointWith(TEMP) != null) 
                odd = !odd;
        return odd;
    } 
    
}
