package asteroids.gui;

import asteroids.TwoD.TwoDVector;
import asteroids.game_objects.AsteroidsGameObject;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class GamePanel {

    public static final int FRAME_WIDTH = 800;
    public static final int FRAME_HEIGHT = 600;    
    
    private static final int LINE_THICKNESS = 4;
    
    private static final Font RETRO_FONT = loadFont("./resources/font/8bit16.TTF");
    private static final Font GAME_NAME_FONT = RETRO_FONT.deriveFont(80F);
    private static final Font PLAY_GAME_FONT = RETRO_FONT.deriveFont(25F);
    
    private static final TwoDVector GAME_NAME_START_POINT = 
            new TwoDVector(FRAME_WIDTH / 6, FRAME_HEIGHT / 5);
    private static final TwoDVector PLAY_GAME_START_POINT = 
            new TwoDVector(FRAME_WIDTH / 2.55F, FRAME_HEIGHT / 1.5F);
    
    private final JFrame frame;
    private final JPanel panel;
    private Graphics2D g;
    
    private boolean showMenu;
    
    public GamePanel() {
        frame = new JFrame("Asteroids");
        panel = new JPanel();
        init();
    }
    
    private void init() {
        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel);
        panel.setBackground(Color.BLACK);
        panel.setFont(RETRO_FONT);
        panel.setDoubleBuffered(true);
        frame.setVisible(true);
        initGraphics();
        frame.setVisible(false);
    }
    
    private void initGraphics() {
        g = (Graphics2D) panel.getGraphics();
        g.setStroke(new BasicStroke(LINE_THICKNESS));
        g.setBackground(Color.BLACK);
        g.setColor(Color.WHITE);
    }
    
    public void addListener(KeyListener kListener) {
        frame.addKeyListener(kListener);
    }
    
    public void showMenu() {
        showMenu = true;
    }
    
    public void hideMenu() {
        showMenu = false;
    }
    
    public void setVisible(boolean visible) {
        frame.setVisible(visible);
    }
    
    public void repaint(Collection<? extends AsteroidsGameObject> gameObjects) {
        panel.paintComponents(g);
        if (showMenu) showMenu0();
        for (AsteroidsGameObject o : gameObjects) o.draw(g);
    }
    
    private void showMenu0() {
        g.setFont(GAME_NAME_FONT);
        g.drawString(
                "ASTEROIDS", 
                GAME_NAME_START_POINT.getXAsInteger(), 
                GAME_NAME_START_POINT.getYAsInteger()
        );
        g.setFont(PLAY_GAME_FONT);
        g.drawString(
                "PLAY GAME", 
                PLAY_GAME_START_POINT.getXAsInteger(), 
                PLAY_GAME_START_POINT.getYAsInteger()
        );
    }
    
    private static Font loadFont(String fontFilePath) {
        Font f = null;
        try {
            f = Font.createFont(Font.TRUETYPE_FONT, new File(fontFilePath));
        } catch (FontFormatException | IOException ex) {}
        return f;
    }
    
    public static void main(String[] args) throws InterruptedException {
        GamePanel h = new GamePanel();
        h.showMenu();
        h.setVisible(true);
        for (int i = 0; i < 19999; i++) {
            h.repaint(Collections.EMPTY_LIST);
            Thread.sleep(40);
        }
    }
    
}
