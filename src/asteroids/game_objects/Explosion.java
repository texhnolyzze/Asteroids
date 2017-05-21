package asteroids.game_objects;

import asteroids.TwoD.TwoDVector;
import asteroids.Utils;
import java.awt.Graphics;

public class Explosion extends MoveableObject {

    private static final float EXPLOSION_CENTER_VELOCITY = 4F;
    
    private static final int MIN_PARTICLES_COUNT = 15;
    private static final int MAX_PARTICLES_COUNT = 25;
    
    private static final float MIN_PARTICLE_VELOCITY = 7F;
    private static final float MAX_PARTICLE_VELOCITY = 14F;
    
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
            particles[i] = new Particle(new TwoDVector(0, 0), particleVelocity);
        }
    }

    @Override
    public void move(int rightBound, int bottomBound) {
        super.move(rightBound, bottomBound); 
        for (Particle p : particles) p.move(rightBound, bottomBound);
    }
    
    public static Explosion explose(TwoDVector explosionCenter) {
        TwoDVector velocity = TwoDVector.getRandomUnitVector();
        velocity.scale(EXPLOSION_CENTER_VELOCITY);
        return new Explosion(explosionCenter, velocity);
    } 
    
    public static Explosion explose(TwoDVector explosionCenter, TwoDVector unitDirection) {
        unitDirection.scale(EXPLOSION_CENTER_VELOCITY);
        return new Explosion(explosionCenter, unitDirection);
    } 

    @Override
    public void draw(Graphics g) {
        g.translate(getCenter().getXAsInteger(), getCenter().getYAsInteger());
        for (Particle p : particles) p.draw(g);
        g.translate(0, 0);
    }
    
    public boolean stillExists() {
        return lifeTime <= EXPLOSION_LIFE_TIME;
    }
    
    static class Particle extends MoveableObject {
        
        public Particle(TwoDVector center, TwoDVector velocity) {
            super(center, velocity);
        }

        @Override
        public void draw(Graphics g) {
            Utils.plotPixel(g, getCenter().getXAsInteger(), getCenter().getYAsInteger());
        }
        
    }
    
}
