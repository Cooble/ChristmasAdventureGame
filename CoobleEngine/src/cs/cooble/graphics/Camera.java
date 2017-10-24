package cs.cooble.graphics;

import cs.cooble.entity.IMovable;

/**
 * Created by Matej on 21.4.2017.
 */
public class Camera implements IMovable {
    private final int SCREEN_WIDTH,SCREEN_HEIGHT;
    private int x,y;
    private int width,height;
    private float scale;
    private boolean isZoomEnabled;

    public Camera(int screen_width, int screen_height) {
        SCREEN_WIDTH = screen_width;
        SCREEN_HEIGHT = screen_height;
        reset();
    }

    public void setWidth(int width){
        this.width=width;
        scale=(float)SCREEN_WIDTH/(float)width;
        height= (int) (SCREEN_HEIGHT/scale);
    }
    public void setHeight(int height){
        this.height=height;
        scale=SCREEN_HEIGHT/height;
        width= (int) (SCREEN_WIDTH/scale);
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public void setX(int x) {
        this.x = x;
    }

    @Override
    public void setY(int y) {
        this.y = y;
    }

    @Override
    public void setIsMoving(boolean isMoving) {}

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public void reset(){
        setWidth(SCREEN_WIDTH);
        x=0;
        y=0;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setZoomEnabled(boolean isZoomEnabled) {
        this.isZoomEnabled = isZoomEnabled;
    }

    public boolean isZoomEnabled() {
        return isZoomEnabled;
    }
}
