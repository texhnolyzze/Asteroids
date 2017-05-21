package asteroids.sound;

import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Sound {
    
    private static final String SOUNDS_DIRECTORY_PATH = "./resources/sounds/";
    
    public static final Sound SMALL_UFO_SIREN = loadSound(SOUNDS_DIRECTORY_PATH + "small_ufo_siren.wav");
    public static final Sound BIG_UFO_SIREN = loadSound(SOUNDS_DIRECTORY_PATH + "big_ufo_siren.wav");
    public static final Sound SHOT = loadSound(SOUNDS_DIRECTORY_PATH + "laser_shot.wav");
    public static final Sound MAIN_THEME = loadSound(SOUNDS_DIRECTORY_PATH + "main_theme.wav");
    public static final Sound JET_ENGINE = loadSound(SOUNDS_DIRECTORY_PATH + "jet_engine.wav");
    public static final Sound EXPLOSION = loadSound(SOUNDS_DIRECTORY_PATH + "explosion.wav");
    
    private final Clip clip;
    
    Sound(String file) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        AudioInputStream stream = AudioSystem.getAudioInputStream(new File(file));
        clip = AudioSystem.getClip();
        clip.open(stream);
    }
    
    public void playSound() {
        clip.setFramePosition(0);
        clip.start();
    }
    
    public void playLooply() {
        clip.setFramePosition(0);
        clip.loop(Clip.LOOP_CONTINUOUSLY);
        clip.start();
    }
    
    public void stop() {
        clip.stop();
    }
    
    private static Sound loadSound(String soundFilePath) {
        Sound s = null;
        try {
            s = new Sound(soundFilePath);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException ex) {}
        return s;
    }
    
}
