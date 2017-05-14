package asteroids;

import asteroids.TwoD.TwoDLine;
import asteroids.TwoD.TwoDPoint;
import java.util.Random;

public final class Utils {
    
    private static final Random RANDOM = new Random();
    
    private Utils() {}
    
    public static float getRandomFloatInRange(float min, float max) {
        return (float) (min + (Math.random() * (max - min)));
    }
    
    public static float randomizeFloatNumberSign(float number) {
        return RANDOM.nextBoolean() ? -number : number;
    }
    
    public static boolean doTheLinesIntersects(TwoDLine firstLine, TwoDLine secondLine) {
        if (firstLine.getSlope() == secondLine.getSlope()) return false;
        TwoDPoint intersection = firstLine.getIntersectionPointOnTheRay(secondLine);
        float x = intersection.getX();
        float y = intersection.getY();
        return ((firstLine.getFirstPoint().getX() <= x && firstLine.getSecondPoint().getX() >= x && 
                secondLine.getFirstPoint().getX() <= x && secondLine.getSecondPoint().getX() >= x) 
                || 
                (firstLine.getFirstPoint().getY() <= y && firstLine.getSecondPoint().getY() >= y && 
                secondLine.getFirstPoint().getY() <= y && secondLine.getSecondPoint().getY() >= y));
    }
    
    public static void main(String[] args) {
        for (int i = 0; i < 1000; i++)
        System.out.println(getRandomFloatInRange(0.1F, 1.5F));
    }
    
}
