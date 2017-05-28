package asteroids.game_objects;

import asteroids.TwoD.TwoDVector;
import asteroids.Utils;
import asteroids.sound.SoundEffect;
import javafx.scene.canvas.GraphicsContext;

public class Explosion extends GameObjectImpl implements LimitedLifeTime {

    private static final float EXPLOSION_CENTER_VELOCITY = 4F;
    
    private static final int MIN_PARTICLES_COUNT = 10;
    private static final int MAX_PARTICLES_COUNT = 20;
    
    private static final float MIN_PARTICLE_VELOCITY = 2F;
    private static final float MAX_PARTICLE_VELOCITY = 5F;
    
    private static final int EXPLOSION_LIFE_TIME = 50;
    
    private int lifeTime;
    private final Particle[] particles;
    
    private Explosion(TwoDVector center, TwoDVector velocity) {
        super(center, velocity);
        particles = new Particle[
                Utils.getRandomIntegerInRange(MIN_PARTICLES_COUNT, MAX_PARTICLES_COUNT)
        ];
        for (int i = 0; i < particles.length; i++) {
            TwoDVector particleVelocity = TwoDVector.getRandomUnitVector();
            particleVelocity.scale(
                    Utils.getRandomFloatInRange(MIN_PARTICLE_VELOCITY, MAX_PARTICLE_VELOCITY)
            );
            particles[i] = new Particle(center.copy(), particleVelocity);
        }
    }

    @Override
    public void move(int rightBound, int bottomBound) {
        for (Particle p : particles) p.move(rightBound, bottomBound);
        lifeTime++;
    }
    
    public static Explosion explose(TwoDVector explosionCenter) {
        SoundEffect.EXPLOSION.playSound();
        return new Explosion(explosionCenter, null);
    }

    @Override
    public void draw(GraphicsContext g) {
        for (Particle p : particles) p.draw(g);
    }
    
    @Override
    public boolean stillExists() {
        return lifeTime <= EXPLOSION_LIFE_TIME;
    }
    
    static class Particle extends GameObjectImpl {
        
        public Particle(TwoDVector center, TwoDVector velocity) {
            super(center, velocity);
        }

        @Override
        public void draw(GraphicsContext g) {
            g.fillRect(getCenter().getXComponent(), getCenter().getYComponent(), 4, 4);
        }
        
    }
    
}
