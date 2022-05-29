package cooble.ch.event;

import java.util.ArrayList;

/**
 * Created by Matej on 13.7.2016.
 */
public abstract class UserInput implements IUserInput {
    protected MyMouseListener mouseListener;
    protected MyKeyListener keyListener;

    public static final int HIGH_PRIORITY = 0;
    public static final int NORMAL_PRIORITY = 1;
    public static final int LOW_PRIORITY = 2;

    protected ArrayList<MouseEventConsumer> mouseConsumers = new ArrayList<>();
    protected ArrayList<MouseEventConsumer> lowPriorityConsumers = new ArrayList<>();
    protected boolean muteMouse;
    protected boolean muteKey;


    public UserInput(MyMouseListener mouseListener, MyKeyListener keyListener) {
        this.mouseListener = mouseListener;
        this.keyListener = keyListener;
    }

    public void registerMouseEventConsumer(MouseEventConsumer consumer, int priority) {
        switch (priority) {
            case HIGH_PRIORITY:
                ArrayList<MouseEventConsumer> newList = new ArrayList<>();
                newList.add(consumer);
                newList.addAll(mouseConsumers);
                mouseConsumers = newList;
                break;
            case NORMAL_PRIORITY:
                if (!mouseConsumers.contains(consumer))
                    mouseConsumers.add(consumer);
                break;
            case LOW_PRIORITY:
                if (!lowPriorityConsumers.contains(consumer))
                    lowPriorityConsumers.add(consumer);
                break;
        }
    }

    public void registerMouseEventConsumer(MouseEventConsumer consumer) {
        registerMouseEventConsumer(consumer, NORMAL_PRIORITY);
    }

    public boolean unregisterMouseEventConsumer(MouseEventConsumer consumer) {
        boolean success = false;
        for (int i = 0; i < mouseConsumers.size(); i++) {
            if (mouseConsumers.get(i).equals(consumer)) {
                mouseConsumers.set(i, null);
                success = true;
                break;
            }
        }
        if (!success) {
            for (int i = 0; i < lowPriorityConsumers.size(); i++) {
                if (lowPriorityConsumers.get(i).equals(consumer)) {
                    lowPriorityConsumers.set(i, null);
                    success = true;
                    break;
                }
            }
            if (success) {
                lowPriorityConsumers = removeNulls(lowPriorityConsumers);
                return true;
            }
            return false;


        }
        mouseConsumers = removeNulls(mouseConsumers);
        return true;
    }

    private ArrayList<MouseEventConsumer> removeNulls(ArrayList<MouseEventConsumer> list) {
        ArrayList<MouseEventConsumer> arrayList = new ArrayList<MouseEventConsumer>();
        for (MouseEventConsumer o : list) {
            if (o != null)
                arrayList.add(o);
        }
        return arrayList;
    }

    @Override
    public void tick() {
        keyListener.tick();
        mouseListener.tick();

        keyTick();
        mouseTick();
    }

    protected void keyTick() {

    }

    protected void mouseTick() {
        if(muteMouse)
            return;
        int x = (int) (mouseListener.getLocation()[0]/*/cam.getScale()+cam.x*/);
        int y = (int) (mouseListener.getLocation()[1]/*/cam.getScale()+cam.y*/);

        if (mouseListener.isFreshlyPressed(true) || mouseListener.isFreshlyPressed(false)) {//left or right
            int state = mouseListener.isFreshlyPressed(true) ? MouseEventConsumer.CLICKED_LEFT : MouseEventConsumer.CLICKED_RIGHT;
            boolean consumed = false;
            for (MouseEventConsumer consumer : mouseConsumers) {
                if (consumer.consume(x, y, state, false)) {
                    consumed = true;
                    break;
                }
            }
            if (!consumed)
                for (MouseEventConsumer consumer : lowPriorityConsumers) {
                    if (consumer.consume(x, y, state, false)) {
                        break;
                    }
                }
        }
        if (mouseListener.wasFreshlyReleased(true) || mouseListener.wasFreshlyReleased(false)) {//left
            int state = mouseListener.wasFreshlyReleased(true) ? MouseEventConsumer.CLICKED_LEFT : MouseEventConsumer.CLICKED_RIGHT;
            boolean consumed = false;
            for (MouseEventConsumer consumer : mouseConsumers) {
                if (consumer.consume(x, y, state, true)) {
                    consumed = true;
                    break;
                }
            }
            if (!consumed)
                for (MouseEventConsumer consumer : lowPriorityConsumers) {
                    if (consumer.consume(x, y, state, true)) {
                        break;
                    }
                }
        }
        if (mouseListener.isMoved() || mouseListener.isDragged()) {
            boolean consumed = false;
            for (MouseEventConsumer consumer : mouseConsumers) {
                if (consumer.consume(x, y,mouseListener.isDragged()?MouseEventConsumer.MOUSE_DRAGGED:MouseEventConsumer.MOUSE_MOVED, false)) {
                    consumed=true;
                    break;
                }
            }
            if(!consumed) {
                for (MouseEventConsumer consumer : lowPriorityConsumers) {
                    consumer.consume(x, y, mouseListener.isDragged() ? MouseEventConsumer.MOUSE_DRAGGED : MouseEventConsumer.MOUSE_MOVED, false);
                }
            }
        }
        int wheel = mouseListener.getWheelMoved();
        if(wheel!=0){
            for (MouseEventConsumer consumer : mouseConsumers) {
                consumer.consume(wheel, 0,MouseEventConsumer.WHEEL_SCROLL, false);
            }
            for (MouseEventConsumer consumer : lowPriorityConsumers) {
                consumer.consume(wheel, 0,MouseEventConsumer.WHEEL_SCROLL, false);
            }
        }
    }

    public MyKeyListener getKeyListener() {
        return keyListener;
    }

    public MyMouseListener getMouseListener() {
        return mouseListener;
    }

    public void muteMouseInput(boolean muteMouse) {
        this.muteMouse = muteMouse;
    }

    public void muteKeyInput(boolean b) {
        this.muteKey=b;
    }

    public void muteInput(boolean mute) {
        muteMouseInput(mute);
        muteKeyInput(mute);
    }
}
