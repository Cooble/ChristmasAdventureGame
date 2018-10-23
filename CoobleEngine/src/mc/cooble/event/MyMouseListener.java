package mc.cooble.event;

import mc.cooble.window.Tickable;
import org.newdawn.slick.Input;
import org.newdawn.slick.MouseListener;

/**
 * Created by Matej on 2.1.2017.
 */
public class MyMouseListener implements MouseListener,Tickable {

    private Input input;

    private int[] location;
    private boolean isDragged;
    private boolean isMoved;
    private CKeyEvent.Key[] keys;
    private byte delayMoved;
    private int wheelMoved;
    private boolean freshWheel;
    private int wheelTick;

    @Override
    public void tick() {
        if(wheelTick>0){
            wheelTick--;
            if(wheelTick==0){
                freshWheel=false;
            }
        }
      /*  if(input!=null) {
            location[0] = input.getMouseX();
            location[1] = input.getMouseY();
        }*/
        for (CKeyEvent.Key k : keys) {
            if (k.tickspressed != -1)
                k.tickspressed++;
        }
        if (delayMoved > 0) {
            delayMoved--;
        }
        if (delayMoved == 0) {
            isDragged = false;
            isMoved = false;
        }
    }

    public MyMouseListener() {
        location = new int[2];
        keys = new CKeyEvent.Key[2];
        for (int i = 0; i < 2; i++) {
            keys[i] = new CKeyEvent.Key();
        }
    }

    @Override
    public void mouseWheelMoved(int i) {
        wheelMoved=i;
        freshWheel=true;
        wheelTick=2;
    }

    @Override
    public void mouseClicked(int i, int i1, int i2, int i3) {

    }

    @Override
    public void mousePressed(int i, int x, int y) {
        if(i>1)
            return;
        keys[i].setPressed(true);
        location[0]=x;
        location[1]=y;

    }

    @Override
    public void mouseReleased(int i, int i1, int i2) {
        if(i>1)
            return;
        keys[i].setPressed(false);
        location[0]=i1;
        location[1]=i2;
    }

    @Override
    public void mouseMoved(int i, int i1, int x, int y) {
        location[0] = x;
        location[1] = y;
        isMoved = true;
        delayMoved = 2;

    }

    @Override
    public void mouseDragged(int i, int i1, int x, int y) {
        location[0] = x;
        location[1] = y;
        isDragged = true;
        delayMoved = 2;
    }

    @Override
    public void setInput(Input input) {
        this.input = input;
    }

    @Override
    public boolean isAcceptingInput() {
        return true;
    }

    @Override
    public void inputEnded() {

    }

    @Override
    public void inputStarted() {

    }

    public boolean isPressed(boolean left) {
        return keys[left ? 0 : 1].isPressed();
    }

    public boolean isFreshlyPressed(boolean left) {
        return keys[left ? 0 : 1].isFreshedPressed();
    }

    public boolean wasFreshlyReleased(boolean left) {
        return keys[left ? 0 : 1].wasFreshlyReleased();
    }

    /**
     *
     * @return zero if no move wheel
     */
    public int getWheelMoved(){
        return freshWheel?wheelMoved:0;
    }



    /**
     *
     * @return real loc
     */
    public int[] getLocation() {
        return location;
    }

    public boolean isMoved() {
        return isMoved;
    }

    public boolean isDragged() {
        return isDragged;
    }
}
