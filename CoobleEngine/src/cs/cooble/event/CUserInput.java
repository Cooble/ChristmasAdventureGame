package cs.cooble.event;

import cs.cooble.core.Game;
import cs.cooble.logger.Log;
import org.newdawn.slick.Input;

/**
 * Created by Matej on 1.10.2016.
 */
public class CUserInput extends UserInput {
    public CUserInput(MyKeyListener keyListener, MyMouseListener mouseListener) {
        super(mouseListener, keyListener);
    }

    protected void keyTick() {
        super.keyTick();
        if(this.muteKey)
            return;
        if (keyListener.isfreshedPressed(Input.KEY_DELETE)) {
            if (Game.isDebugging) {
                Log.println("ClearingAndExiting");
                Game.saver.clearGameFolder();
                GameExitEvent event = new GameExitEvent();
                event.setClear(true);
                Game.core.EVENT_BUS.addEvent(event);
            }
        }
        if (keyListener.getTicksOn(Input.KEY_I) == 1) {
            Game.getWorld().inventory().setOnOrOff();
        }
        if (keyListener.isfreshedPressed(Input.KEY_ESCAPE)) {
            LocationLoadEvent event = new LocationLoadEvent(null);
            event.setRemoveSubLoc(true);
            Game.core.EVENT_BUS.addEvent(event);
        }
        if (keyListener.getTicksOn(Input.KEY_ESCAPE) == 45) {
           // Game.saveGame();
            Game.pauseLOCID=Game.getWorld().getLocationManager().getCurrentLocationID();
            Game.pauseMID=Game.getWorld().getModule().MID;
            Game.lastLOCID=Game.getWorld().getLocationManager().getCurrentLocationID();
            Game.lastMID=Game.getWorld().getModule().MID;
            Game.paused=true;
            Game.core.EVENT_BUS.addEvent(new LocationLoadEvent("intro"));
        }
    }
}
