package asteroids;

public class Player {

    private static final int LIFES_COUNT = 3;
    
    private int score;
    private int lifesCount;
    
    public Player() {
        lifesCount = LIFES_COUNT;
    }
    
    public void increaseScore(int by) {
        score += by;
    }
    
    public int getScore() {
        return score;
    }
    
    public int getLifesCount() {
        return lifesCount;
    }
    
    public void decreaseLifesCount() {
        lifesCount--;
    }
    
}
