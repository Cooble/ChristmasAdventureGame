package cooble.ch.event;

/**
 * Created by Matej on 12.12.2015.
 */
public interface MouseEventConsumer {


    /**
     * called whenever is clicked somewhere in location
     * if state==WHEEL_SCROLL -> x means whether up>0 down<0
     *
     * @param x
     * @param y
     * @param state    determines if
     *                 0# mouseMoved
     *                 1# mouseClickedLeft
     *                 2# mouseClickedRight
     * @param released true = released false pressed
     * @return true if event was consumed and shouldn't be managed by others
     */
    boolean consume(int x, int y, int state, boolean released);

    int MOUSE_MOVED = 0;
    int CLICKED_LEFT = 1;
    int CLICKED_RIGHT = 2;
    int MOUSE_DRAGGED = 3;
    int WHEEL_SCROLL = 4;

}
