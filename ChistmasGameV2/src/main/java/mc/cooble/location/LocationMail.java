package mc.cooble.location;


import mc.cooble.core.Game;
import mc.cooble.event.LocationLoadEvent;
import mc.cooble.translate.Translator;

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
