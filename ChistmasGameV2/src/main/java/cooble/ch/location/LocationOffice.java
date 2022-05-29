package cooble.ch.location;

import cooble.ch.core.Game;
import cooble.ch.event.Event;
import cooble.ch.event.LocationLoadEvent;
import cooble.ch.graphics.BitmapStack;
import cooble.ch.inventory.item.ItemStack;
import cooble.ch.inventory.stuff.StuffToCome;
import cooble.ch.item.Items;
import cooble.ch.music.MPlayer2;
import cooble.ch.world.Location;
import cooble.ch.world.NBT;

/**
 * Created by Matej on 9.10.2016.
 */
public class LocationOffice extends Location {
    private boolean openedPc;
    private StuffToCome cover;

    public LocationOffice() {
        super("office");
    }

    @Override
    public void loadTextures() {

        StuffToCome whiteboard = (StuffToCome) getStuffByID("whiteboard");
        whiteboard.setOnPickedUp(() -> Game.core.EVENT_BUS.addEvent(new LocationLoadEvent("whiteboard")));

        StuffToCome casee = (StuffToCome) getStuffByID("library");
        casee.setOnPickedUp(() -> Game.core.EVENT_BUS.addEvent(new LocationLoadEvent("library")));

        StuffToCome electronics = (StuffToCome) getStuffByID("electronics");
        electronics.setVisible(false);
        electronics.setItem(new ItemStack(Items.itemElectronics));

        cover = (StuffToCome) getStuffByID("pc_cover");
        cover.setTextNameAsName();
        cover.setOnPickedUp(new Runnable() {
            @Override
            public void run() {
                if(Game.getWorld().inventory().getItemInHand()!=null&&Game.getWorld().inventory().getItemInHand().ITEM.ID==Items.itemScrewdriver.ID) {
                    MPlayer2.playSound("screw");
                    Game.input.muteInput(true);
                    Game.core.EVENT_BUS.addDelayedEvent(Game.core.TARGET_TPS * 3, new Event() {
                        @Override
                        public void dispatchEvent() {
                            Game.input.muteInput(false);
                            openedPc = true;
                            MPlayer2.playSound("metal");
                            Game.getWorld().inventory().putItemInHandBack();
                                    ((BitmapStack) cover.getBitmapProvider()).setCurrentIndex(1);
                            cover.setRectangle(0, 0, 0, 0);
                            electronics.setVisible(true);
                        }
                    });
                }
            }
        });
        for (int i = 0; i < 2; i++) {
            ((StuffToCome)getStuffByID("fan"+i)).setItem(new ItemStack(Items.itemFan));
        }

        getArrowByID("toParty").setOnExit(() -> MPlayer2.playSound("door"));
    }

    @Override
    public void writeToNBT(NBT nbt) {
        super.writeToNBT(nbt);
        nbt.putBoolean("openPc", openedPc);
    }

    @Override
    public void readFromNBT(NBT nbt) {
        super.readFromNBT(nbt);
        openedPc = nbt.getBoolean("openPc");
        getStuffByID("electronics").setVisible(openedPc);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (openedPc) {
            ((BitmapStack) cover.getBitmapProvider()).setCurrentIndex(1);
            cover.setRectangle(0, 0, 0, 0);
        }
    }

}
