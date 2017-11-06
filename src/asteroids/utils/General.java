/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package asteroids.utils;

import asteroids.App;
import static asteroids.App.HEIGHT;
import static asteroids.App.WIDTH;
import java.util.Random;
import javafx.scene.paint.Color;

/**
 *
 * @author Texhnolyze
 */
public class General {

    public static final float EPS = 0.01F;
    
    public static final float PI = (float) Math.PI;
    
// ROTATE DIRECTIONS
    public static final int NONE                = 0;
    public static final int CLOCKWISE           = 1;
    public static final int COUNTERCLOCK_WISE   = 2;
    
    public static Vector2 GLOBAL_BOUND = new Vector2(App.WIDTH, App.HEIGHT);
    
    public static final Segment2 LEFT_BOUND = new Segment2(
            new Vector2(0, 0), 
            new Vector2(0, App.HEIGHT)
    ), RIGHT_BOUND = new Segment2(
            new Vector2(WIDTH, 0), 
            new Vector2(WIDTH, App.HEIGHT)
    ), TOP_BOUND = new Segment2(
            new Vector2(0, 0), 
            new Vector2(App.WIDTH, 0)
    ), BOTTOM_BOUND = new Segment2(
            new Vector2(0, HEIGHT), 
            new Vector2(App.WIDTH, HEIGHT)
    );
    
    public static final Segment2[] BOUNDS = {
        LEFT_BOUND, TOP_BOUND, RIGHT_BOUND, BOTTOM_BOUND
    };
    
    private static final Random RANDOM = new Random();
    
    public static float getRandomFloatInRange(float a, float b) {
        return a + (b - a) * RANDOM.nextFloat();
    }
    
    public static int getRandomIntegerInRange(int a, int b) {
        return a + RANDOM.nextInt(b - a + 1);
    }
    
    public static int randomizeSign(int a) {
        return RANDOM.nextBoolean() ? -a : a;
    }
    
    public static float randomizeSign(float a) {
        return RANDOM.nextBoolean() ? -a : a;
    }
    
    public static Color getRandomColor() {
        int r = RANDOM.nextInt(255 + 1);
        int g = RANDOM.nextInt(255 + 1);
        int b = RANDOM.nextInt(255 + 1);
        return Color.rgb(r, g, b);
    }
    
}
