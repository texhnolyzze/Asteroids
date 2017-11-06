package asteroids;

import asteroids.utils.Sound;

/**
 *
 * @author Texhnolyze
 */
public class SoundStore {
    
    public static final Sound BLAST         = App.loadSound("blast", 0.5D);
    public static final Sound BIG_BANG      = App.loadSound("big_bang", 1.2D);
    public static final Sound JET_ENGINE    = App.loadSound("jet_engine", 0.55D);
    public static final Sound MAIN_THEME    = App.loadSound("main_theme", 0.7D);
    public static final Sound SMALL_UFO     = App.loadSound("small_ufo_siren");
    public static final Sound BIG_UFO       = App.loadSound("big_ufo_siren");
    public static final Sound EXTRA_LIFE    = App.loadSound("extra_life");
    
    public static final Sound[] UFO_SIRENS = {
        SMALL_UFO, BIG_UFO
    };
    
}
