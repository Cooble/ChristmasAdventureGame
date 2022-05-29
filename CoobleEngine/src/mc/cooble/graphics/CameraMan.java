package mc.cooble.graphics;

import mc.cooble.entity.Mover;
import mc.cooble.entity.Position;
import mc.cooble.window.Tickable;

/**
 * Created by Matej on 20.7.2017.
 */
public class CameraMan implements Tickable {
    private Camera camera;
    private double speed;
    private double speedScale;
    private DoubleBasket targetScale;
    private int targetX, targetY;
    private SubMover moverSize;
    private Mover mover;

    public CameraMan(Camera camera) {
        this.camera = camera;
        targetX = camera.getX();
        targetY = camera.getY();
        targetScale = new CameraScaleBasket(camera.getScale(), camera);
        moverSize = new SubMover(targetScale, speed);
    }

    private void createMovers() {
        mover = new Mover(camera, new Position(targetX, targetY), speed, null);
        moverSize = new SubMover(targetScale, speedScale);
    }

    public void setSpeed(double speed) {
        this.speed = speed;
        createMovers();

    }

    public void setScaleSpeed(double sizeSpeed) {
        speedScale = sizeSpeed;
        createMovers();
    }

    /**
     * Please call after goTo()!!!
     *
     * @param ticks
     */
    public void setPosTime(int ticks) {
        setSpeed(new Position(targetX, targetY).getDifference(new Position(camera.getX(), camera.getY())) / ticks);
    }

    /**
     * Please call after goTo()!!!
     *
     * @param ticks
     */
    public void setScaleTime(int ticks) {
        setScaleSpeed(Math.abs((int) (camera.getScale() - targetScale.getD())) / ticks);
    }
    /**
     * Please call after goTo()!!!
     *
     * @param ticks
     */
    public void setTime(int ticks){
      setTimes(ticks,ticks);
    }
    /**
     * Please call after goTo()!!!
     *
     * @param ticks
     */
    public void setTimes(int posTicks,int scaleTicks){
        setScaleTime(scaleTicks);
        setPosTime(posTicks);
    }

    public void goTo(int x, int y) {
        goTo(x, y, targetScale.getD());
    }

    public void goTo(double width) {
        goTo(targetX, targetY, width);
    }

    public void goTo(int x, int y, double size) {
        this.targetX = x;
        this.targetY = y;
        this.targetScale.setD(size);
    }

    @Override
    public void tick() {
        if (mover != null)
            mover.tick();
        moverSize.tick();
    }

    private class DoubleBasket {
        double d;

        public DoubleBasket(double d) {
            this.d = d;
        }

        public double getD() {
            return d;
        }

        public int getInt() {
            return (int) d;
        }

        public void setD(double d) {
            this.d = d;
        }
    }

    private class CameraScaleBasket extends DoubleBasket {

        private Camera camera;

        public CameraScaleBasket(double d, Camera camera) {
            super(d);
            this.camera = camera;
        }

        @Override
        public void setD(double d) {
            super.setD(d);
            camera.setScale((float) getD());
        }
    }

    private class SubMover implements Tickable {
        private DoubleBasket basket;
        private double targetD;
        private double speed;

        @Override
        public void tick() {
            if (!isBusyMoving()) {
                if (basket.getD() != targetD)
                    basket.setD(targetD);
                return;
            }
            if (basket.getD() > targetD) {
                basket.setD(basket.getD() - speed);
            } else basket.setD(basket.getD() + speed);


        }

        public boolean isBusyMoving() {
            double currentD = basket.getD();
            return Math.abs(currentD - targetD) > speed;

        }

        public SubMover(DoubleBasket basket, double speed) {
            this.basket = basket;
            this.speed = speed;
        }

        public void goTo(double targetD) {
            this.targetD = targetD;
        }
    }

    public Camera getCamera() {
        return camera;
    }
}
