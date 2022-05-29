package cooble.ch.location;


import cooble.ch.core.Game;
import cooble.ch.event.LocationLoadEvent;
import cooble.ch.translate.Translator;

/**
 * Created by Matej on 17.12.2015.
 */
public final class LocationMail extends LocationPaper {


    public LocationMail() {
        super("mail");

    }
    @Override
    public void loadTextures() {
        setBackground("location/mail_"+ Translator.getLanguage());
    }

    @Override
    public void onStop() {
        super.onStop();
        Game.core.EVENT_BUS.addEvent(new LocationLoadEvent("ice_plain"));
    }
}
