package cooble.ch.event;

import cooble.ch.window.Tickable;

/**
 * Created by Matej on 7.2.2017.
 */
public interface IUserInput extends Tickable {

    MyKeyListener getKeyListener();

    MyMouseListener getMouseListener();


}
