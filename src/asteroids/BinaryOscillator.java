package asteroids;

public class BinaryOscillator {

    private final int _flickerFrequency;
    
    private int tick;
    private final boolean _countdown;
    private boolean tickIncrement;
    
    public BinaryOscillator(int flickerFrequency, boolean countdown) {
        _flickerFrequency = flickerFrequency;
        _countdown = countdown;
        init();
    }
    
    private void init() {
        tick = _countdown ? _flickerFrequency : 0;
        tickIncrement = !_countdown;
    }
    
    public void reset() {
        init();
    }
    
    public boolean needToPerformAction() {
        if (tickIncrement) {
            tick++;
            if (tick == _flickerFrequency) tickIncrement = false;
            return true;
        } else {
            tick--;
            if (tick == 0) tickIncrement = true;
            return false;
        }
    }
    
}
