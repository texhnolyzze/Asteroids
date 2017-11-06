package asteroids;

import static asteroids.App.HEIGHT;
import static asteroids.App.WIDTH;
import static asteroids.App.getRetroFontOf;
import asteroids.game_objects.Asteroid;
import asteroids.game_objects.Explosion;
import asteroids.game_objects.Shot;
import asteroids.game_objects.SpaceShip;
import asteroids.game_objects.UFO;
import asteroids.utils.General;
import asteroids.utils.TickOscillator;
import asteroids.utils.TimeStamp;
import asteroids.utils.Vector2;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 *
 * @author Texhnolyze
 */
public class GameController extends AnimationTimer implements EventHandler<KeyEvent> {

    public static final int MENU            = 0;
    public static final int PLAYING         = 1;
    public static final int GAME_OVER       = 2;
    
    private static final Random RANDOM = new Random();
    
    private static void loadAsteroids(List<Asteroid> storage, int num, Vector2 awayFrom, float howFar) {
        for (int i = 0; i < num; i++) storage.add(
            Asteroid.
                getRandomBigAsteroidAwayFrom(
                    awayFrom, 
                    howFar
                )
        );
    }
    
    private static final int IN_THE_CENTER            = 1;
    private static final int IN_THE_LEFT_SIDE         = 2;
    private static final int IN_THE_RIGHT_SIDE        = 3;
    
    private static int centerText(String str, Font f, int type) {
        Text text = new Text(str);
        text.setFont(f);
        Bounds b = text.getBoundsInLocal();
        int x_offset = (int) (b.getMaxX() / 2);
        int base = (int) (App.WIDTH / (type == IN_THE_CENTER ? 2 
                : type == IN_THE_LEFT_SIDE ? 4 : (4D / 3D)));
        return base - x_offset;
    }
    
    private static final int[][] STARS = initStars();
    private static final List<Asteroid> MENU_ASTEROIDS = new LinkedList<>();
    
    static {
        loadAsteroids(MENU_ASTEROIDS, 5, null, -1);
    }
    
    private static int[][] initStars() {
        int[][] stars = new int[50][2];
        for (int i = 0; i < stars.length; i++) {
            stars[i][0] = RANDOM.nextInt(WIDTH);
            stars[i][1] = RANDOM.nextInt(HEIGHT);
        }
        return stars;
    }
    
    private static void drawStars(GraphicsContext gc) {
        gc.setFill(Color.WHITE);
        for (int i = 0; i < STARS.length; i++) {
            if (RANDOM.nextDouble() > 0.02D) gc.fillRect(STARS[i][0], STARS[i][1], 1, 1);            
        }
    }
    
    private final GraphicsContext gc;
    
    private Player player;
    private SpaceShip ss;
    
    private UFO ufo;
    
    private final List<Shot> shots = new LinkedList<>();
    private final List<Asteroid> asteroids = new LinkedList<>();
    private final List<Explosion> explosions = new LinkedList<>();
    
    
    private int state;
    
    public GameController(GraphicsContext gc) {
        this.gc = gc;
        gc.setLineWidth(1.5D);
    }
    
    
    private void clearView() {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, WIDTH, HEIGHT);
    }
    
//-------------ARRAY OF COLORS FROM WHITE(255, 255, 255) TO BLACK(0, 0, 0)--------------
    
    private static final Color[] COLORS = initColors();
    
    private static Color[] initColors() {
        int idx = 0;
        float scale = 0.945F;
        Color current = Color.WHITE;
        Color[] colors = new Color[50];
        while (idx != colors.length) {
            colors[idx++] = current;
            current = Color.color(
                current.getRed() * scale, 
                current.getGreen() * scale, 
                current.getBlue() * scale
            );
        }
        return colors;
    } 
   
//-------------------------------------------------------------------------------------------------------------
    
//----------GAME NAME DRAWING ATTRIBUTES------------
    private static final String GAME_NAME_STR   = "ASTEROIDS";
    private static final Font FONT_1            = getRetroFontOf(50);
    private static final int GAME_NAME_X        = centerText(
        GAME_NAME_STR, 
        FONT_1, 
        IN_THE_CENTER
    ), GAME_NAME_Y = HEIGHT / 4;
//-----------------------------------------------------------------
    
    private void drawGameName() {
        gc.setFill(Color.WHITE);
        gc.setFont(FONT_1);
        gc.fillText(GAME_NAME_STR, GAME_NAME_X, GAME_NAME_Y);
    }
        
//----------"PRESS TO PLAY" LABEL DRAWING ATTRIBUTES-----------
    private static final String PTP_STR = "PRESS TO PLAY";
    private static final TickOscillator TO_1 = new TickOscillator(COLORS.length - 1);
    private static final Font FONT_2 = getRetroFontOf(20);
    private static final int PTP_X = centerText(PTP_STR, FONT_2, IN_THE_CENTER);
    private static final int PTP_Y = (int) (HEIGHT / 1.9D);
//-----------------------------------------------------------------------------
    
    private void drawPressToPlay() {
        Color c = COLORS[TO_1.getTicksPassed()];
        TO_1.tick();
        gc.setFill(c);
        gc.setFont(FONT_2);
        gc.fillText(PTP_STR, PTP_X, PTP_Y);
    }
    
//-----------"WITH FRICTION" / "WITHOUT FRICTION" LABELS DRAWING ATTRIBUTES------------
    private static final String WITH = "WITH FRICTION", WITHOUT = "WITHOUT FRICTION";
    private static final Font FONT_3 = FONT_2;
    private static final int WITH_X = centerText(WITH, FONT_3, IN_THE_CENTER);
    private static final int WITH_Y = HEIGHT / 2;
    private static final int WITHOUT_X = centerText(WITHOUT, FONT_3, IN_THE_CENTER);
    private static final int WITHOUT_Y = (int) (HEIGHT / 1.5D);
    private static final TickOscillator TO_2 = new TickOscillator(COLORS.length - 1);
//--------------------------------------------------------------------------------------------------------------

    private void drawWithWithoutFriction() {
        gc.setFont(FONT_3);
        gc.setFill(COLORS[TO_2.getTicksPassed()]);
        if (iTemp == 0) {
            gc.fillText(WITH, WITH_X, WITH_Y);
            gc.setFill(Color.GRAY);
            gc.fillText(WITHOUT, WITHOUT_X, WITHOUT_Y);
        } else {
            gc.fillText(WITHOUT, WITHOUT_X, WITHOUT_Y);
            gc.setFill(Color.GRAY);
            gc.fillText(WITH, WITH_X, WITH_Y);
        }
        TO_2.tick();
    }
    
//-----------------HUD DRAWING ATTRIBUTES-------------------
    private static final Font FONT_4 = App.getRetroFontOf(18);
    private static final int SCORE_X = centerText("SCORE: 0", FONT_4, IN_THE_LEFT_SIDE);
    private static final int LIVES_X = centerText("LIVES: 3", FONT_4, IN_THE_RIGHT_SIDE);
    private static final int SCORE_LIVES_Y = App.HEIGHT / 6;
//---------------------------------------------------------------------
    
    private void drawHud() {
        gc.setFill(Color.WHITE);
        gc.setFont(FONT_4);
        gc.fillText("SCORE: " + player.getScore(), SCORE_X, SCORE_LIVES_Y);
        gc.fillText("LIVES: " + player.getLifesCount(), LIVES_X, SCORE_LIVES_Y);
    }
    
//-----------------"GAME OVER" DRAWING ATTRIBUTES------------------
    private static final String GAME_OVER_STR = "GAME OVER";
    private static final Font FONT_5 = App.getRetroFontOf(50);
    private static final int GAME_OVER_X = centerText(GAME_OVER_STR, FONT_5, IN_THE_CENTER);
    private static final int GAME_OVER_Y = App.HEIGHT / 3;
    private static String scoreResultStr;
    private static int scoreResultX;
    private static final int SCORE_RESULT_Y = (int) (App.HEIGHT / 1.8D);
//--------------------------------------------------------------------------------
    
    private void drawGameOver() {
        gc.setFill(Color.RED);
        gc.setFont(FONT_5);
        gc.fillText(GAME_OVER_STR, GAME_OVER_X, GAME_OVER_Y);
        gc.setFill(Color.WHITE);
        gc.setFont(FONT_4);
        gc.fillText(scoreResultStr, scoreResultX, SCORE_RESULT_Y);
    }
    
    private int iTemp;
    private boolean bTemp;
    private boolean stagePassed;
    
    private final TimeStamp t1 = new TimeStamp();
    private final TimeStamp t2 = new TimeStamp();
    
    private int stage;
    private int currentAsteroidsNum;
    
    @Override
    //MAIN LOOP
    public void handle(long now) {
        clearView();
        drawStars(gc);
        if (state == MENU) {
            drawGameName();
            if (bTemp) drawWithWithoutFriction();
            else drawPressToPlay();
            MENU_ASTEROIDS.forEach((a) -> { 
                a.draw(gc);
                a.update();
            });
        } else if (state == PLAYING) {
            drawHud();
            ss.draw(gc);
            if (ufo != null) ufo.draw(gc);
            for (Shot s : shots) s.draw(gc);
            for (Asteroid a : asteroids) a.draw(gc);
            for (Explosion e : explosions) e.draw(gc);
            ss.update();
            if (ufo != null) {
                ufo.update();
                if (!ufo.isValid()) {
                    SoundStore.UFO_SIRENS[ufo.getType()].fadeOut();
                    ufo = null;
                } else {
                    Shot s = ufo.blast(ss);
                    if (s != null) shots.add(s);
                }
            }
            for (Asteroid a : asteroids) a.update();
            Iterator<Shot> shotsIt = shots.iterator();
            while (shotsIt.hasNext()) {
                Shot s = shotsIt.next();
                s.update();
                if (!s.isValid()) shotsIt.remove();
            }
            Iterator<Explosion> explosionsIt = explosions.iterator();
            while (explosionsIt.hasNext()) {
                Explosion e = explosionsIt.next();
                e.update();
                if (!e.isValid()) explosionsIt.remove();
            }
            handleCollisions();
            if (asteroids.isEmpty()) {
                if (!stagePassed) {
                    t2.reset();
                    stagePassed = true;
                } else {
                    if (t2.passed(4000)) {
                        stage++;
                        stagePassed = false;
                        if (stage % 5 == 0) player.extraLife();
                        currentAsteroidsNum = Math.min(currentAsteroidsNum + 1, MAX_ASTEROIDS_COUNT);
                        loadAsteroids(asteroids, currentAsteroidsNum, ss.getCenter(), SAFETY_RADIUS);
                    }
                }
            }
            if (bTemp && t1.passed(4000)) {
                bTemp = false;
                player.decreaseLifesCount();
                if (player.getLifesCount() != 0) {
                    ss.reset();
                } else {
                    scoreResultStr = "SCORE: " + player.getScore();
                    scoreResultX = centerText(scoreResultStr, FONT_4, IN_THE_CENTER);
                    state = GAME_OVER;
                }
            }
            if (ufo == null) {
                boolean b1 = General.getRandomIntegerInRange(0, 1700) == 37;
                if (b1) {
                    boolean b2 = Math.random() < 0.75D;
                    if (b2) ufo = UFO.getUFO(UFO.BIG_UFO);
                    else ufo = UFO.getUFO(UFO.SMALL_UFO);
                }
            }
        } else {
            drawGameOver();
        }
    }
    
    private static final int[] ASTEROID_DESTROY_SCORE_INCREASE = {
        40, 20, 10
    }, UFO_DESTROY_SCORE_INCREASE = {
        100, 50
    };
    
    
    private void handleCollisions() {
        handle1();
        handle2();
        handle3();
    }
    
    private void handle1() {
        Iterator<Shot> shotsIt = shots.iterator();
        List<Asteroid> splitsList = null;
        while (shotsIt.hasNext()) {
            Shot s = shotsIt.next();
            Iterator<Asteroid> asteroidsIt = asteroids.iterator();
            while (asteroidsIt.hasNext()) {
                Asteroid a = asteroidsIt.next();
                if (a.isDestroyedBy(s)) {
                    if (s.getType() == Shot.SPACE_SHIP_SHOT) 
                        player.increaseScore(ASTEROID_DESTROY_SCORE_INCREASE[a.getType()]);
                    Asteroid[] splitsArr = a.split();
                    if (splitsArr != null) {
                        if (splitsList == null) splitsList = new LinkedList<>();
                        splitsList.addAll(Arrays.asList(splitsArr));
                    }
                    shotsIt.remove();
                    asteroidsIt.remove();
                    explosions.add(Explosion.getBoomIn(a.getWhereWasDestroyed(), a.getType()));
                    break;
                }
            }
        }
        if (splitsList != null) {
            asteroids.addAll(splitsList);
            splitsList = null;
        }
    }
    
    private void handle2() {
        Iterator<Shot> shotsIt;
        if (!ss.isDead()) {
            Asteroid[] splitsArr = null;
            Iterator<Asteroid> asteroidsIt = asteroids.iterator();
            while (asteroidsIt.hasNext()) {
                Asteroid a = asteroidsIt.next();
                if (a.isCollidedWith(ss)) {
                    ss.destroy();
                    asteroidsIt.remove();
                    splitsArr = a.split();
                    explosions.add(Explosion.getBoomIn(a.getWhereWasDestroyed(), Explosion.SMALL));
                    break;
                }
            }
            if (splitsArr != null) asteroids.addAll(Arrays.asList(splitsArr));
            if (!ss.isDead()) {
                shotsIt = shots.iterator();
                while (shotsIt.hasNext()) {
                    Shot s = shotsIt.next();
                    if (ss.isDestroyedBy(s)) {
                        ss.destroy();
                        shotsIt.remove();
                        explosions.add(Explosion.getBoomIn(ss.getWhereWasDestroyed(), Explosion.SMALL));
                        break;
                    }
                }
                if (!ss.isDead()) {
                    if (ufo != null && ss.isCollidedWith(ufo)) {
                        SoundStore.UFO_SIRENS[ufo.getType()].fadeOut();
                        ufo = null;
                        ss.destroy();
                        explosions.add(Explosion.getBoomIn(ss.getWhereWasDestroyed(), Explosion.SMALL));
                    }
                }
            }
            bTemp = ss.isDead();
            if (bTemp) t1.reset();
        }
    }
    
    private void handle3() {
        if (ufo != null) {
            Iterator<Asteroid> asteroidsIt = asteroids.iterator();
            Asteroid[] splits = null;
            while (asteroidsIt.hasNext()) {
                Asteroid a = asteroidsIt.next();
                if (a.isCollidedWith(ufo)) {
                    explosions.add(Explosion.getBoomIn(a.getWhereWasDestroyed(), Explosion.MEDIUM));
                    SoundStore.UFO_SIRENS[ufo.getType()].fadeOut();
                    ufo = null;
                    splits = a.split();
                    asteroidsIt.remove();
                    break;
                }
            }
            if (splits != null) asteroids.addAll(Arrays.asList(splits));
            if (ufo != null) {
                Iterator<Shot> shotsIt = shots.iterator();
                while (shotsIt.hasNext()) {
                    Shot s = shotsIt.next();
                    if (ufo.isDestroyedBy(s)) {
                        player.increaseScore(UFO_DESTROY_SCORE_INCREASE[ufo.getType()]);
                        explosions.add(Explosion.getBoomIn(ufo.getWhereWasDestroyed(), Explosion.MEDIUM));
                        SoundStore.UFO_SIRENS[ufo.getType()].fadeOut();
                        ufo = null;
                        shotsIt.remove();
                        break;
                    }
                }
            }
        }
    }
    
    private static final int INIT_ASTEROIDS_COUNT = 5;
    private static final int MAX_ASTEROIDS_COUNT = 20;

    private static final float SAFETY_RADIUS = 100F;
    
    private void newGame() {
        SpaceShip.FRICTION_MODE = iTemp == 0;
        ufo = null;
        asteroids.clear();
        shots.clear();
        explosions.clear();
        stage = 1;
        iTemp = 0;
        bTemp = false;
        state = PLAYING;
        currentAsteroidsNum = INIT_ASTEROIDS_COUNT;
        ss = SpaceShip.getSpaceShip(new Vector2(WIDTH / 2, HEIGHT / 2));
        loadAsteroids(asteroids, INIT_ASTEROIDS_COUNT, ss.getCenter(), SAFETY_RADIUS);
        player = new Player(3);
    }
    
    @Override
    public void handle(KeyEvent event) {
        if (state == PLAYING && ss.isDead()) {
            event.consume();
            return;
        }
        KeyCode code = event.getCode();
        if (event.getEventType() == KeyEvent.KEY_PRESSED) {
            if (state == MENU) {
                if (!bTemp) bTemp = true;
                else {
                    if (code == KeyCode.UP && iTemp != 0)
                        iTemp--;
                    else if (code == KeyCode.DOWN && iTemp != 1)
                        iTemp++;
                    else if (code == KeyCode.ENTER) newGame();
                }
            }
            else if (state == PLAYING) {
                if (null != code) 
                    switch (code) {
                    case UP:
                        ss.turnOnTheEngine();
                        break;
                    case LEFT:
                        ss.setRotateDirection(General.COUNTERCLOCK_WISE);
                        break;
                    case RIGHT:
                        ss.setRotateDirection(General.CLOCKWISE);
                        break;
                    default:
                        break;
                }
            } else 
                state = MENU;
        } else {
            if (state == PLAYING) {
                if (null != code) 
                    switch (code) {
                    case UP:
                        ss.turnOffTheEngine();
                        break;
                    case LEFT:
                    case RIGHT:
                        ss.setRotateDirection(General.NONE);
                        break;
                    case SPACE:
                        Shot s = ss.blast();
                        if (s != null) shots.add(s);
                        break;
                    case SHIFT:
                        ss.hyperSpaceJump();
                        break;
                    default:
                        break;
                }
            }
        }
    }
    
}
