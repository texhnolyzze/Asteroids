package asteroids.utils;

import java.io.File;
import kuusisto.tinysound.Music;
import kuusisto.tinysound.TinySound;

public class Sound {
    
    static {
        TinySound.init();
    }
    
    private final Music m;
    
    public Sound(String filename) {
        this(filename, 1.0D);
    }
    
    public Sound(String filename, double vol) {
        m = TinySound.loadMusic(new File(filename).getAbsoluteFile());
        m.setVolume(vol);
    }
    
    public void play(boolean loop) {
//        m.rewind();
//        m.play(loop);
    }
    
    public void stop() {
        m.stop();
    }
    
    public void fadeOut() {
        if (m.loop() && m.playing()) m.setLoop(false);
    }
    
    public boolean isPlaying() {
        return m.playing();
    }
    
    
}
