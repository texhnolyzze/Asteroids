package asteroids.sound;

import java.io.File;
import kuusisto.tinysound.Music;
import kuusisto.tinysound.TinySound;

public class SoundEffect {
    
    static {
        TinySound.init();
        TinySound.setGlobalVolume(2.0);
    }
    
    private static final String SOUNDS_DIRECTORY_PATH = "./resources/sounds/";
    
    public static final SoundEffect SMALL_UFO_SIREN = new SoundEffect(SOUNDS_DIRECTORY_PATH + "small_ufo_siren.wav");
    public static final SoundEffect BIG_UFO_SIREN = new SoundEffect(SOUNDS_DIRECTORY_PATH + "big_ufo_siren.wav");
    public static final SoundEffect SHOT = new SoundEffect(SOUNDS_DIRECTORY_PATH + "laser_shot.wav");
    public static final SoundEffect MAIN_THEME = new SoundEffect(SOUNDS_DIRECTORY_PATH + "main_theme.wav");
    public static final SoundEffect JET_ENGINE = new SoundEffect(SOUNDS_DIRECTORY_PATH + "jet_engine.wav");
    public static final SoundEffect EXPLOSION = new SoundEffect(SOUNDS_DIRECTORY_PATH + "explosion.wav");
    
    public static final SoundEffect[] ALL_SOUNDS = {
        SMALL_UFO_SIREN, BIG_UFO_SIREN, SHOT, MAIN_THEME, JET_ENGINE, EXPLOSION
    };
    
    private final Music m;
    
    private SoundEffect(String filename) {
        m = TinySound.loadMusic(new File(filename));
    }
    
    public void playSound() {
        m.rewind();
        m.play(false);
    }
    
    public void playLooply() {
        m.rewind();
        m.play(true);
    }
    
    public void stop() {
        if (m.playing()) m.stop();
    }
    
}
