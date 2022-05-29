package cooble.ch.stuff;


import cooble.ch.core.Game;
import cooble.ch.event.SpeakEvent;
import cooble.ch.graphics.Bitmap;
import cooble.ch.graphics.BitmapProvider;
import cooble.ch.inventory.item.ItemStack;
import cooble.ch.inventory.stuff.Stuff;
import cooble.ch.location.LocationGarageOut;
import cooble.ch.resources.StringStream;
import cooble.ch.world.IAction;
import cooble.ch.world.NBT;

/**
 * Created by Matej on 12.8.2016.
 */
public class StuffGarage extends Stuff implements Runnable {
    private int opened;
    private StringStream stringStream;
    private Bitmap bitmap;
    private Bitmap door_bitmap;
    private byte slower;
    private boolean wasOpened;
    private final int maxOpenPixel = 47 * 2;
    private LocationGarageOut location;

    public static final int MAX_OPENED = 100;

    public StuffGarage(LocationGarageOut location) {
        super(location.generateStuffName("garage_door"));
        this.location = location;
        door_bitmap = Bitmap.get("item/garage_door");
        bitmap = Bitmap.create(door_bitmap.getWidth(), door_bitmap.getHeight());
        rerender = true;

        //setLocationTexture(bitmapStack);
        stringStream = new StringStream(StringStream.getTranslated("comment.garage_door."));

        setAction(new IAction() {
            @Override
            public boolean onClicked(int x, int y, boolean right_button, boolean released, ItemStack item) {
                if (!IAction.onlyPressed(right_button, released, item))
                    return true;
                if (opened == 0) {
                    SpeakEvent se = new SpeakEvent(stringStream.getNextString());
                    Game.core.EVENT_BUS.addEvent(se);
                }
                return true;
            }
        });
        renderThread();
    }

    @Override
    public void tick() {
        super.tick();
        setVisible(true);
        if (!wasOpened) {
            slower++;
            if (slower > 5) {
                slower = 0;
                if (opened != 0 && opened != MAX_OPENED) {
                    opened++;
                    rerender=true;
                    if (opened == maxOpenPixel) {
                        opened = MAX_OPENED;
                        wasOpened = true;
                    } else rerender = true;
                }
            }
        }
        location.getArrowToGarage().setEnabled(opened == MAX_OPENED);
    }

    boolean rerender;

    private void renderThread() {
        if(rerender) {
            rerender = false;
            door_bitmap.setOffset(0, -opened);
            bitmap.clear();
            bitmap.draw(door_bitmap);
            bitmap.setOffset(7 * 2, 20 * 2);
            bitmap.setShouldRender(true);
        }
    }

    @Override
    public void writeToNBT(NBT nbt) {
        super.writeToNBT(nbt);
        nbt.putInteger("opened", opened);
        nbt.putBoolean("wasOpened", wasOpened);
    }

    @Override
    public void readFromNBT(NBT nbt) {
        super.readFromNBT(nbt);
        opened = nbt.getInteger("opened");
        wasOpened = nbt.getBoolean("wasOpened");
        rerender = true;

    }

    public void open() {
        if (!wasOpened)
            opened = 1;
        else opened = MAX_OPENED;
        rerender = true;
    }

    public boolean wasOpened() {
        return wasOpened;
    }

    @Override
    public void run() {
        renderThread();
    }

    @Override
    public BitmapProvider getBitmapProvider() {
        return bitmap;
    }

}
