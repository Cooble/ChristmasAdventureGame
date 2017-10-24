package cs.cooble.stuff;

import cs.cooble.core.Game;
import cs.cooble.graphics.BitmapProvider;
import cs.cooble.graphics.BitmapStack;
import cs.cooble.inventory.stuff.Stuff;
import cs.cooble.music.MPlayer2;
import cs.cooble.resources.ResourceStackBuilder;

/**
 * Created by Matej on 13.8.2016.
 */
public class StuffSpark extends Stuff {
    private BitmapStack bitmapStack;
    private int delay;
    private int state;
    private boolean makespark;
    private boolean inProgress;

    public StuffSpark(String name) {
        super(name);
        bitmapStack = BitmapStack.getBitmapStack(ResourceStackBuilder.buildResourcesStack("item/spark/", "", "0", "1", "2", "3", "4", "5", "6"));
        bitmapStack.setOffset(71*2, 54*2);
    }

    @Override
    public void tick() {
        super.tick();
        if (!makespark)
            bitmapStack.setCurrentIndex(0);
        delay++;
        if (delay > 3) {
            if (!makespark) {
                makespark = Game.random.nextInt(20) == 0;
                if(makespark)
                    MPlayer2.playSound("arc", 0.4);

                bitmapStack.setCurrentIndex(0);
            }
            delay = 0;
            if (makespark) {
                makespark = !continueInIndexing();
                if (!makespark) {
                    delay = -50;
                }
            }
        }
    }

    private boolean continueInIndexing() {
        state++;
        bitmapStack.setCurrentIndex(state);
        if (state >= 6) {
            state = 0;
            return true;
        }
        return false;
    }

    @Override
    public BitmapProvider getBitmapProvider() {
        return bitmapStack;
    }
}
