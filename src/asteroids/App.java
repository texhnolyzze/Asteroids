
package asteroids;

import asteroids.utils.Sound;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 *
 * @author Texhnolyze
 */
public class App extends Application {

    private static final String SOUNDS_DIRECTORY = loadSoundsDirectory();
    
    private static final Font RETRO_FONT = loadFont();
    
    public static final int WIDTH = 640, HEIGHT = 480;
    
    private static final int WINDOW_WIDTH_OFFSET    = 6;
    private static final int WINDOW_HEIGHT_OFFSET   = 25;
    
    public static void main(String[] args) {
        launch();
    }
    
    private static String loadSoundsDirectory() {
        return new File("resources/sounds").getAbsolutePath();
    }
    
    public static Sound loadSound(String name) {
        return loadSound(name, 1.0D);
    }
    
    public static Sound loadSound(String name, double vol) {
        return new Sound(SOUNDS_DIRECTORY + "/" + name + ".wav", vol);
    }
    
    public static Font getRetroFontOf(double size) {
        return new Font(RETRO_FONT.getName(), size);
    }
    
    private static Font loadFont() {
        Font f = null;
        try {
            f = Font.loadFont(new FileInputStream(new File("resources/font/8bit16.TTF").getAbsolutePath()), 1.0D);
        } catch (FileNotFoundException ex) {
            System.exit(0);
        }
        return f;
    }
    
    @Override
    public void start(Stage stage) throws Exception {
        try {
            Class.forName("asteroids.SoundStore");
        } catch (ClassNotFoundException ex) {
            System.exit(0);
        }
        SoundStore.MAIN_THEME.play(true);
        Group root = new Group();
        stage.setWidth(WIDTH + WINDOW_WIDTH_OFFSET);
        stage.setHeight(HEIGHT + WINDOW_HEIGHT_OFFSET);
        stage.setResizable(false);
        stage.setTitle("Asteroids");
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        root.getChildren().add(canvas);
        GameController controller = new GameController(gc);
        root.setOnKeyPressed(controller);
        root.setOnKeyReleased(controller);
        stage.setScene(new Scene(root, Color.BLACK));
        stage.show();
        root.requestFocus();
        controller.start();
    }
    
}
