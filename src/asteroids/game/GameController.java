package asteroids.game;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class GameController {

    private static GameController controller;
    
    public static GameController getController() {
        if (controller == null) controller = new GameController();
        return controller;
    }
    
    class InputListener implements KeyListener {

        @Override
        public void keyTyped(KeyEvent e) {/*DON'T NEED*/}

        @Override
        public void keyReleased(KeyEvent e) {/*DON'T NEED*/}
        
        @Override
        public void keyPressed(KeyEvent e) {
            
        }
        
    }
    
}
