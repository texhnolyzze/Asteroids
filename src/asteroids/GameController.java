package asteroids;

import static asteroids.Asteroids.HEIGHT;
import static asteroids.Asteroids.WIDTH;
import asteroids.TwoD.TwoDVector;
import asteroids.game_objects.Asteroid;
import asteroids.game_objects.Bullet;
import asteroids.game_objects.Explosion;
import asteroids.game_objects.GameObject;
import asteroids.game_objects.LimitedLifeTime;
import asteroids.game_objects.SpaceShip;
import asteroids.game_objects.UFO;
import asteroids.sound.SoundEffect;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class GameController extends AnimationTimer {
    
    private static final int MENU_STATE = 0;
    private static final int GAME_STATE = 1;
    private static final int STATE_BETWEEN_LEVELS = 2;
    private static final int GAME_OVER_STATE = 3;
    
    private static final BinaryOscillator LABEL_FLICKERING_SIMULATOR 
            = new BinaryOscillator(30, false);
    private static final BinaryOscillator DELAY_BETWEEN_LEVELS 
            = new BinaryOscillator(200, true);
    
    private static final List<Asteroid> MENU_ASTEROIDS = createAsteroids(10);
    
    private static final int NEW_GAME_INIT_ASTEROIDS_COUNT = 1;
    private static final int MAX_INIT_ASTEROIDS_COUNT = 15;
    
    private int state;
    private int currentLevelInitAsteroidsCount;
    
    private Player player;
    private SpaceShip ship;
    private UFO ufo;

    private final GraphicsContext gc;
    
    private List<Bullet> bullets;    
    private List<Asteroid> asteroids;
    private List<Explosion> explosions;
    
    private final KeyPressedEventHandler keyPressedEventHandler;
    private final KeyReleasedEventHandler keyReleasedEventHandler;
    
    public GameController(GraphicsContext g) {
        keyPressedEventHandler = new KeyPressedEventHandler();
        keyReleasedEventHandler = new KeyReleasedEventHandler();
        gc = g;
    }

    public KeyPressedEventHandler getKeyPressedEventHandler() {
        return keyPressedEventHandler;
    }

    public KeyReleasedEventHandler getKeyReleasedEventHandler() {
        return keyReleasedEventHandler;
    }
    
    @Override
    public void handle(long now) {
        clearView();
        switch (state) {
            case MENU_STATE:
                menuState();
                break;
            case GAME_STATE:
                gameState();
                if ((asteroids.isEmpty() && ufo == null) || player.getLifesCount() == 0) 
                    state = STATE_BETWEEN_LEVELS;
                break;
            case STATE_BETWEEN_LEVELS:
                stateBetweenLevels();
                break;
            case GAME_OVER_STATE:
                gameOverState();
                break;
            default:
                break;
        }
    }
    
    private void clearView() {
        gc.clearRect(0, 0, WIDTH, HEIGHT);
    }
    
    private void gameObjectTick(GameObject o) {
        o.move(WIDTH, HEIGHT);
        o.draw(gc);
    } 
    
    private void menuState() {
        for (Asteroid a : MENU_ASTEROIDS) gameObjectTick(a);
        gc.setFont(Asteroids.GAME_NAME_FONT);
        gc.fillText("ASTEROIDS", WIDTH / 2 - 230, HEIGHT / 2 - 100);
        if (LABEL_FLICKERING_SIMULATOR.needToPerformAction()) {
            gc.setFont(Asteroids.PLAY_GAME_FONT);
            gc.fillText("PRESS ANY KEY TO PLAY", WIDTH / 2 - 160, HEIGHT / 2 + 100);
        }
    }
    
    private void newGame() {
        player = new Player();
        ship = SpaceShip.createShip(new TwoDVector(WIDTH / 2, HEIGHT / 2));
        bullets = new LinkedList<>();
        explosions = new LinkedList<>();
        asteroids = createAsteroids(NEW_GAME_INIT_ASTEROIDS_COUNT);
        currentLevelInitAsteroidsCount = NEW_GAME_INIT_ASTEROIDS_COUNT;
        state = GAME_STATE;
        SoundEffect.MAIN_THEME.playLooply();
    }
    
    private void stateBetweenLevels() {
        gameState();
        if (DELAY_BETWEEN_LEVELS.needToPerformAction()) {
            DELAY_BETWEEN_LEVELS.reset();
            if (player.getLifesCount() == 0) {
                allSoundsOff();
                state = GAME_OVER_STATE;
            } else nextLevel();
        }
    }
    
    private void nextLevel() {
        asteroids = createAsteroids(
                currentLevelInitAsteroidsCount == MAX_INIT_ASTEROIDS_COUNT ? 
                        MAX_INIT_ASTEROIDS_COUNT : ++currentLevelInitAsteroidsCount);
        state = GAME_STATE;
    }
    
    private void gameState() {
        printPlayerStatus();
        drawAndMoveAll();
        allCollisionsHandle();
        if (ufo == null) {
            if (timeToUFO()) ufo = UFO.createUFO(WIDTH, HEIGHT);
        }
        else {
            if (ufo.timeToShot())
                bullets.add(ufo.shoot(ship));
        }
    }
    
    private void allSoundsOff() {
        for (SoundEffect se : SoundEffect.ALL_SOUNDS)
            se.stop();
    }
    
    private void gameOverState() {
        gc.setFont(Asteroids.GAME_FONT);
        if (LABEL_FLICKERING_SIMULATOR.needToPerformAction())
            gc.fillText("GAME OVER", WIDTH / 2 - 90, HEIGHT / 2 - 20);
        gc.fillText("SCORE: " + player.getScore(), WIDTH / 2 - 80, HEIGHT / 2 - 150);
        gc.fillText("PRESS 'N' TO PLAY AGAIN", WIDTH / 2 - 300, HEIGHT / 2 + 150);
        gc.fillText("PRESS 'M' TO EXIT THE MENU", WIDTH / 2 - 300, HEIGHT / 2 + 200);
    }
    
    private void printPlayerStatus() {
        gc.setFont(Asteroids.GAME_FONT);
        gc.fillText("SCORE: " + player.getScore(), WIDTH / 2 - 300, HEIGHT / 2 - 250);
        gc.fillText("LIVES REMAIN: " + player.getLifesCount(), WIDTH / 2 - 300, HEIGHT / 2 - 200);
    }
    
    private void drawAndMoveAll() {
        gameObjectTick(ship);
        drawAndMoveLimitedLifeTime(bullets.iterator());
        drawAndMoveLimitedLifeTime(explosions.iterator());
        for (Asteroid a : asteroids) gameObjectTick(a);
        if (ufo != null) {
            if (ufo.stillExists()) gameObjectTick(ufo);
            else ufo = null;
        }
    }
    
    private void drawAndMoveLimitedLifeTime(Iterator<? extends LimitedLifeTime> it) {
        while (it.hasNext()) {
            LimitedLifeTime object = it.next();
            if (object.stillExists()) gameObjectTick(object);
            else it.remove();
        }
    }
    
    private void allCollisionsHandle() {
        playerWithAsteroidsCollisionHandle();
        playerWithBulletsCollisionHandle();
        if (ufo != null) {
            playerWithUFOCollisionHandle();
            ufoWithAsteroidsCollisionHandle();
            ufoWithBulletsCollisionHandle();
        }
        asteroidsWithBulletsCollisionsHandle();
    }
    
    private void playerWithAsteroidsCollisionHandle() {
        Asteroid[] splits = null;
        Iterator<Asteroid> asteroidsIt = asteroids.iterator();
        while (asteroidsIt.hasNext()) {
            Asteroid a = asteroidsIt.next();
            Explosion e = ship.collisionWith(a);
            if (e != null) {
                splits = a.split();
                ship.shipDeath();
                asteroidsIt.remove();
                player.decreaseLifesCount();
                explosions.add(e);
                break;
            }
        }
        if (splits != null) asteroids.addAll(Arrays.asList(splits));
    }
    
    private void ufoWithBulletsCollisionHandle() {
        Iterator<Bullet> bulletsIt = bullets.iterator();
        while (bulletsIt.hasNext()) {
            Bullet b = bulletsIt.next();
            Explosion e = ufo.collisionWith(b);
            if (e != null) {
                player.increaseScore(ufo.getScoreIncrement());
                ufo.crush();
                explosions.add(e);
                bulletsIt.remove();
                break;
            }
        }
    }
    
    private void ufoWithAsteroidsCollisionHandle() {
        Asteroid[] splits = null;
        Iterator<Asteroid> asteroidsIt = asteroids.iterator();
        while (asteroidsIt.hasNext()) {
            Asteroid a = asteroidsIt.next();
            Explosion e = ufo.collisionWith(a);
            if (e != null) {
                splits = a.split();
                explosions.add(e);
                ufo.crush();
                asteroidsIt.remove();
                break;
            }
        }
        if (splits != null) asteroids.addAll(Arrays.asList(splits));
    }
    
    private void playerWithUFOCollisionHandle() {
        Explosion e = ship.collisionWith(ufo);
        if (e != null) {
            ufo.crush();
            explosions.add(e);
            ship.shipDeath();
            player.decreaseLifesCount();
        }
    }
    
    private void playerWithBulletsCollisionHandle() {
        Iterator<Bullet> bulletsIt = bullets.iterator();
        while (bulletsIt.hasNext()) {
            Bullet b = bulletsIt.next();
            Explosion e = ship.collisionWith(b);
            if (e != null) {
                player.decreaseLifesCount();
                explosions.add(e);
                ship.shipDeath();
                bulletsIt.remove();
            }
        }
    }
    
    private void asteroidsWithBulletsCollisionsHandle() {
        List<Asteroid[]> splits = new LinkedList<>();
        Iterator<Bullet> bulletsIt = bullets.iterator();
        while (bulletsIt.hasNext()) {
            Bullet b = bulletsIt.next();
            Iterator<Asteroid> asteroidsIt = asteroids.iterator();
            while (asteroidsIt.hasNext()) {                
                Asteroid a = asteroidsIt.next();
                Explosion e = b.collisionWith(a);
                if (e != null) {
                    explosions.add(e);
                    Asteroid[] split = a.split(b);
                    if (split != null) splits.add(split);
                    asteroidsIt.remove();
                    bulletsIt.remove();
                    if (b.isPlayerShot()) player.increaseScore(a.getScoreIncrement());
                    break;
                }
            }
        }
        for (Asteroid[] aSplits : splits) 
            for (Asteroid a : aSplits) 
                asteroids.add(a);
    }
    
    private boolean timeToUFO() {
        return Utils.conditionWithProbability(500) 
                && !ship.isDead() 
                && state == GAME_STATE;
    }
    
    private static List<Asteroid> createAsteroids(int count) {
        List<Asteroid> list = new LinkedList<>();
        for (int i = 0; i < count; i++) 
            list.add(
                    Asteroid.getRandomBigAsteroid(
                            TwoDVector.getRandomInBoundsAwayFromCenter(100, WIDTH, HEIGHT)
                    )
            );
        return list;
    }
    
    class KeyPressedEventHandler implements EventHandler<KeyEvent> {
        
        @Override
        public void handle(KeyEvent event) {
            switch (state) {
                case MENU_STATE:
                    newGame();
                    break;
                case GAME_STATE:
                case STATE_BETWEEN_LEVELS:
                    if (!ship.isDead())
                        switch (event.getCode()) {
                            case UP:
                                ship.turnOnEngine();
                                break;
                            case RIGHT:
                                ship.rotate(SpaceShip.CLOCKWISE_ROTATION);
                                break;
                            case LEFT:
                                ship.rotate(SpaceShip.COUNTER_CLOCKWISE_ROTATION);
                                break;
                            default:
                                break;
                        }   break;
                case GAME_OVER_STATE:
                    if (event.getCode() == KeyCode.M) state = MENU_STATE;
                    else if (event.getCode() == KeyCode.N) newGame();
                    break;
                default:
                    break;
            }
        }
    
    }
    
    class KeyReleasedEventHandler implements EventHandler<KeyEvent> {

        @Override
        public void handle(KeyEvent event) {
            if (state == GAME_STATE || state == STATE_BETWEEN_LEVELS) {
                switch (event.getCode()) {
                    case UP:
                        ship.turnOffEngine();
                        break;
                    case LEFT:
                    case RIGHT:
                        ship.stopRotate();
                        break;
                    case SPACE:
                        Bullet b = ship.shoot();
                        if (b != null) bullets.add(ship.shoot());
                    default:
                        break;
                }
            }
        }
        
    }
    
}
