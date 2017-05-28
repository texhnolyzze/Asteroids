package asteroids;

import asteroids.sound.SoundEffect;
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

public class Asteroids extends Application {

    public static void main(String[] args) throws ClassNotFoundException {
        SoundEffect.class.getName();
        launch(args);
    }
    
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;
    
    private static final float LINE_WIDTH = 1F;
    
    private static final Font RETRO_FONT = loadFont("./resources/font/8bit16.TTF");
    
    public static final Font GAME_FONT = new Font(RETRO_FONT.getName(), 25);
    public static final Font GAME_NAME_FONT = new Font(RETRO_FONT.getName(), 70);
    public static final Font PLAY_GAME_FONT = new Font(RETRO_FONT.getName(), 20);
    
    private GameController controller;
    
    @Override
    public void start(Stage mainStage) throws Exception {
        Group root = new Group();
        mainStage.setWidth(WIDTH);
        mainStage.setHeight(HEIGHT);
        mainStage.setResizable(false);
        mainStage.setTitle("Asteroids");
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setLineWidth(LINE_WIDTH);
        gc.setStroke(Color.WHITE);
        gc.setFill(Color.WHITE);
        root.getChildren().add(canvas);
        controller = new GameController(gc);
        root.setOnKeyPressed(controller.getKeyPressedEventHandler());
        root.setOnKeyReleased(controller.getKeyReleasedEventHandler());
        mainStage.setScene(new Scene(root, Color.BLACK));
        mainStage.show();
        root.requestFocus();
        controller.start();
    }
    
    private static Font loadFont(String fontFilePath) {
        Font f = null;
        try {
            f = Font.loadFont(new FileInputStream(new File(fontFilePath)), WIDTH);
        } catch (FileNotFoundException ex) {}
        return f;
    }
    
    
    
}
