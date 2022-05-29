package cooble.ch.location;


import cooble.ch.core.Game;
import cooble.ch.event.MouseEventConsumer;
import cooble.ch.event.SpeakEvent;

/**
 * Created by Matej on 17.12.2015.
 */
public final class LocationBlueprint extends LocationPaper {


    public LocationBlueprint() {
        super("blueprint");
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
    }

    @Override
    public boolean consume(int x, int y, int mouseEvent, boolean released) {
        if (mouseEvent == MouseEventConsumer.CLICKED_LEFT) {
            if (!released)
                Game.core.EVENT_BUS.addEvent(new SpeakEvent("entity.joe.blueprint"));
        }
        return super.consume(x, y, mouseEvent, released);
    }
}
