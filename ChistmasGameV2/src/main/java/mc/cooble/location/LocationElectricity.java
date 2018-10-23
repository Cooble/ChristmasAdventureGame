package mc.cooble.location;


import mc.cooble.event.LocationLoadEvent;
import mc.cooble.graphics.Bitmap;
import mc.cooble.inventory.stuff.Stuff;
import mc.cooble.stuff.StuffSpark;
import mc.cooble.world.Location;

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
