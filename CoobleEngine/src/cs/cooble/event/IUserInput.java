package cs.cooble.event;

import cs.cooble.window.Tickable;

/**
 * Created by Matej on 7.2.2017.
 */
public interface IUserInput extends Tickable{

    MyKeyListener getKeyListener();

    MyMouseListener getMouseListener();


}
