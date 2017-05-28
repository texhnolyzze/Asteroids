package asteroids;

import java.util.Random;

public final class Utils {
    
    public static final float EPSILON = 0.001F;
    
    private static final Random RANDOM = new Random();
    
    private Utils() {}
    
    public static float getRandomFloatInRange(float min, float max) {
        return (float) (min + (Math.random() * (max - min)));
    }
    
    public static int getRandomIntegerInRange(int min, int max) {
        return min + RANDOM.nextInt(max - min + 1);
    }
    
    public static float randomizeFloatNumberSign(float number) {
        return RANDOM.nextBoolean() ? -number : number;
    }
    
    public static boolean conditionWithProbability(int i) {
        return RANDOM.nextInt() % i == 0;
    }
    
    public static boolean floatEquals(float f1, float f2) {
        return Math.abs(f2 - f1) < EPSILON;
    }
    
}
