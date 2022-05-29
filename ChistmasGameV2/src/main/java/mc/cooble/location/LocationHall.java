package mc.cooble.location;

import mc.cooble.core.Game;
import mc.cooble.entity.Cleaner;
import mc.cooble.event.DialogEvent;
import mc.cooble.event.LocationLoadEvent;
import mc.cooble.event.SpeakEvent;
import mc.cooble.graphics.Renderer;
import mc.cooble.inventory.item.ItemStack;
import mc.cooble.inventory.stuff.Stuff;
import mc.cooble.inventory.stuff.StuffToCome;
import mc.cooble.item.Items;
import mc.cooble.world.IAction;
import mc.cooble.world.Location;
import mc.cooble.world.NBT;
import org.newdawn.slick.Color;

/**
 * Created by Matej on 9.10.2016.
 */
public class LocationHall extends Location {
    private boolean hasGivenSoldier;
    private Cleaner cleaner;
    private Stuff soldier;

    public LocationHall() {
        super("hall");
    }

    @Override
    public void loadTextures() {
        soldier = getStuffByID("soldierCleaner");
        ((StuffToCome) getStuffByID("kumbal_door")).setOnPickedUp(new Runnable() {
            @Override
            public void run() {
               Game.core.EVENT_BUS.addEvent(new LocationLoadEvent("kumbal"));
            }
        });

        cleaner = new Cleaner("cleaner");
        cleaner.loadTextures();
        cleaner.setPos(180, 130);
        cleaner.setAction(new IAction() {
            @Override
            public boolean onClicked(int x, int y, boolean right_button, boolean released, ItemStack item) {
                if (released)
                    return true;

                if (item != null && item.ITEM.ID == Items.itemSoldierBrush.ID) {
                    Game.getWorld().inventory().setOff();
                    hasGivenSoldier = true;
                    cleaner.setCleanActive(false);
                    Game.getWorld().inventory().setItemInHand(null);
                    DialogEvent event = new DialogEvent("cleanerLuxYes");
                    event.registerTalkable(cleaner, "cleaner", Color.blue);
                    Game.core.EVENT_BUS.addEvent(event);
                    Game.core.EVENT_BUS.addDelayedEvent(Game.core.TARGET_TPS * 9, () -> {
                        soldier.setVisible(true);
                        cleaner.setRight(false);
                    });
                }else if(item!=null&&item.ITEM.ID==Items.itemToothbrush.ID){
                    cleaner.setCleanActive(false);
                    SpeakEvent speakEvent = new SpeakEvent("entity.cleaner.brush");
                    speakEvent.setColor(Color.blue);
                    speakEvent.setTalkable(cleaner);
                    speakEvent.setOnEnd(() -> cleaner.setCleanActive(true));
                    Game.core.EVENT_BUS.addEvent(speakEvent);

                } else if (item == null) {
                    cleaner.setCleanActive(false);
                    SpeakEvent speakEvent = new SpeakEvent("entity.cleaner.busy");
                    speakEvent.setColor(Color.blue);
                    speakEvent.setTalkable(cleaner);
                    speakEvent.setOnEnd(() -> cleaner.setCleanActive(true));
                    Game.core.EVENT_BUS.addEvent(speakEvent);
                }
                return true;
            }
        });
        actionRectangleManager.register(cleaner.getActionRectangle());
        registerTickable(cleaner);
    }

    @Override
    public void onStartRendering(Renderer renderer) {
        super.onStartRendering(renderer);
        soldier.setVisible(hasGivenSoldier);
        renderer.registerBitmapProvider(cleaner.getBitmapStack());
        cleaner.setCleanActive(!hasGivenSoldier);

        if (Game.getWorld().inventory().hasItem(Items.itemLux) && !hasGivenSoldier) {
            cleaner.setCleanActive(false);
            Game.getWorld().inventory().lock(true);
            DialogEvent dialogEvent = new DialogEvent("cleanerLuxNo");
            dialogEvent.registerTalkable(Game.getWorld().getUniCreature(), "joe", Color.green);
            dialogEvent.registerTalkable(cleaner, "cleaner", Color.blue);
            dialogEvent.setOnEnd(new Runnable() {
                @Override
                public void run() {
                    Game.getWorld().inventory().lock(false);
                    Game.getWorld().inventory().stealItem(Items.itemLux);
                    cleaner.setCleanActive(true);
                    Game.getWorld().getModule().getNBT().putBoolean("putLuxBack", true);
                }
            });
            Game.core.EVENT_BUS.addEvent(dialogEvent);
        }
        Game.renderer.removeBitmapProvider(Game.getWorld().getUniCreature().getBitmapStack());
        Game.renderer.registerBitmapProvider(Game.getWorld().getUniCreature().getBitmapStack());
    }

    @Override
    public void writeToNBT(NBT nbt) {
        super.writeToNBT(nbt);
        nbt.putBoolean("hasGivenSoldier", hasGivenSoldier);
    }

    @Override
    public void readFromNBT(NBT nbt) {
        super.readFromNBT(nbt);
        hasGivenSoldier = nbt.getBoolean("hasGivenSoldier");
    }
}
