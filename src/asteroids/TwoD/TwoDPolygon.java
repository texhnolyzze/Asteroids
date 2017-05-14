package asteroids.TwoD;

import asteroids.Utils;

public class TwoDPolygon {

    private float _centerX, _centerY;
    private final TwoDVector[] vertices;
    private final float maxDistanceFromCenter;
    
    public TwoDPolygon(int verticesCount, float[] xComponentsRelativeToCenterX, 
            float[] yComponentsRelativeToCenterY, float centerX, float centerY) {
        vertices = new TwoDVector[verticesCount];
        float max = 0;
        for (int i = 0; i < vertices.length; i++) {
            float nextX = xComponentsRelativeToCenterX[i];
            float nextY = yComponentsRelativeToCenterY[i];
            vertices[i] = new TwoDVector(nextX, nextY);
            float perhapsMore = (float) Math.sqrt(nextX * nextX + nextY * nextY);
            if (perhapsMore > max) max = perhapsMore;
        }
        _centerX = centerX;
        _centerY = centerY;
        maxDistanceFromCenter = max;
    }
    
    public int verticesCount() {
        return vertices.length;
    }
    
    public TwoDVector[] getVertices() {
        return vertices;
    }
    
    public float getCenterX() {
        return _centerX;
    }
    
    public float getCenterY() {
        return _centerY;
    }
    
    public void rotate(float radianAngle) {
        for (TwoDVector vertex : vertices) vertex.rotate(radianAngle);
    }
    
    public void transfer(float dx, float dy) {
        _centerX += dx;
        _centerY += dy;
    }
    
    public boolean isThePointInside(float pointX, float pointY) {
        TwoDPoint rawStartPoint = new TwoDPoint(pointX, pointY);
        TwoDPoint rawEndPoint = new TwoDPoint(pointX + 2 * maxDistanceFromCenter,
                pointY + 2 * maxDistanceFromCenter);
        TwoDLine raw = new TwoDLine(rawStartPoint, rawEndPoint);
        int counter = 0;
        for (int i = 0; i < vertices.length - 1; i++) {
            TwoDPoint firstVertex = getVertexAsTwoDPoint(i);
            TwoDPoint secondVertex = getVertexAsTwoDPoint(i + 1);
            TwoDLine edge = new TwoDLine(firstVertex, secondVertex);
            if (Utils.doTheLinesIntersects(raw, edge)) counter++;
        }
        TwoDPoint startVertex = getVertexAsTwoDPoint(0);
        TwoDPoint endVertex = getVertexAsTwoDPoint(vertices.length - 1);
        TwoDLine closingEdge = new TwoDLine(startVertex, endVertex);
        if (Utils.doTheLinesIntersects(raw, closingEdge)) counter++;
        return counter % 2 != 0;
    }
    
    public TwoDPoint getVertexAsTwoDPoint(int vertex) {
        return new TwoDPoint(_centerX + vertices[vertex].getXComponent(), 
                _centerY + vertices[vertex].getYComponent());
    }

    public static TwoDPolygon randomPolygone(int verticesCount, float centerX, float centerY, 
            float minDistanceFromCenter, float maxDistanceFromCenter) {
        float[] xComponents = new float[verticesCount];
        float[] yComponents = new float[verticesCount];
        for (int i = 0; i < verticesCount; i++) {
            xComponents[i] = Utils.randomizeFloatNumberSign(
                    Utils.getRandomFloatInRange(minDistanceFromCenter, maxDistanceFromCenter));
            yComponents[i] = Utils.randomizeFloatNumberSign(
                    Utils.getRandomFloatInRange(minDistanceFromCenter, maxDistanceFromCenter));
        }
        TwoDPolygon polygon = new TwoDPolygon(verticesCount, xComponents, yComponents, centerX, centerY); 
        return polygon;
    }
    
}
