package cs.cooble.location;


import cs.cooble.event.LocationLoadEvent;
import cs.cooble.graphics.Bitmap;
import cs.cooble.inventory.stuff.Stuff;
import cs.cooble.stuff.StuffSpark;
import cs.cooble.world.Location;

/**
 * Created by Matej on 13.8.2016.
 */
public class LocationElectricity extends Location {
    public LocationElectricity() {
        super("electricity");

    }

    @Override
    public void loadTextures() {
        setBackground(Bitmap.get("location/electricity"));
        ;

        LocationLoadEvent locationLoadEvent = new LocationLoadEvent("garage", null);
        locationLoadEvent.setJoesLocation(135*2,35*2);
        locationLoadEvent.setJoeRightFacing(false);

        Stuff spark = new StuffSpark(generateStuffName("spark"));
        spark.setRectangle(0, 0, 0, 0);
        addStuff(spark);
    }
}
