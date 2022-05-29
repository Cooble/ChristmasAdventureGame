package cooble.ch.entity;

import cooble.ch.core.Game;
import cooble.ch.graphics.Bitmap;
import cooble.ch.window.Tickable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

/**
 * Created by Matej on 5.9.2017.
 */
public class Weather implements Tickable {
    private boolean enabled;
    private Supplier<Bitmap> particleFactory;
    private int cadence;
    private List<Particle> particles;
    private int ticksToNextBall;
    private double speedX,speedY;
    private double xSpawnLine;

    public Weather(Supplier<Bitmap> particleFactory) {
        this.particleFactory = particleFactory;
        particles = new ArrayList<>();
        xSpawnLine=1;
    }

    public void spawnWidth(double xSpawnLine) {
        this.xSpawnLine = xSpawnLine;
    }

    public void setCadence(int particlesPerSecond) {
        this.cadence = Game.core.TARGET_TPS / particlesPerSecond;
        ticksToNextBall=1;
    }

    public void setSpeed(double speedX,double speedY) {
        this.speedX = speedX;
        this.speedY = speedY;
    }

    public void spawnParticle(double xSpeed, double ySpeed) {
        Random random = Game.random;
        Particle droplet = new Particle(particleFactory.get(), xSpeed, ySpeed);
        droplet.setPos(random.nextInt((int) (Game.renderer.PIXEL_WIDTH*xSpawnLine)), -droplet.src.getHeight());
        droplet.tick();
        particles.add(droplet);
        Game.renderer.registerBitmapProvider(droplet.src);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public void tick() {

        ArrayList<Particle> toRemove = new ArrayList<>();
        for (Particle s : particles) {
            if (s != null) {
                s.tick();
                if (s.getY() > Game.renderer.PIXEL_HEIGHT + s.src.getHeight()) {
                    toRemove.add(s);
                }
            }
        }
        for (Particle s : toRemove) {
            particles.remove(s);
            Game.renderer.removeBitmapProvider(s.src);
        }
        if (!enabled)
            return;
        if (ticksToNextBall > 0) {
            ticksToNextBall--;
            if (ticksToNextBall == 0) {
                spawnParticle(vary(speedX), vary(speedY));
                ticksToNextBall = cadence;
            }
        }
    }

    private class Particle {
        private Bitmap src;
        private final double speedX;
        private final double speedY;
        private double posX, posY;

        Particle(Bitmap src, double speedX, double speedY) {
            this.src = src;
            this.speedX = speedX;
            this.speedY = speedY;
        }


        void setPos(int x, int y) {
            posX = x;
            posY = y;
        }

        void setSrcPosition(int x, int y) {
            src.setOffset(x - src.getWidth() / 2, y - src.getHeight() / 2);
        }

        int getX() {
            return src.getOffset()[0] + src.getWidth();
        }

        int getY() {
            return src.getOffset()[1] + src.getHeight();
        }

        void tick() {
            posX += speedX;
            posY += speedY;
            setSrcPosition((int) posX, (int) posY);
        }
    }
    private double vary(double src){
        double rand = Game.random.nextDouble()*Math.abs(src/10);
        return src+(Game.random.nextBoolean()?rand:-rand);
    }

}
