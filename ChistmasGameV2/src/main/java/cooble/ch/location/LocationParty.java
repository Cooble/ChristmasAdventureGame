package cooble.ch.location;


import cooble.ch.core.Game;
import cooble.ch.entity.Helper;
import cooble.ch.entity.Joe;
import cooble.ch.event.DialogEvent;
import cooble.ch.event.Event;
import cooble.ch.event.LocationLoadEvent;
import cooble.ch.event.SpeakEvent;
import cooble.ch.graphics.Animation;
import cooble.ch.graphics.Bitmap;
import cooble.ch.graphics.Renderer;
import cooble.ch.inventory.item.ItemStack;
import cooble.ch.inventory.stuff.Stuff;
import cooble.ch.inventory.stuff.StuffToCome;
import cooble.ch.item.Items;
import cooble.ch.music.MPlayer2;
import cooble.ch.world.IAction;
import cooble.ch.world.Location;
import cooble.ch.world.NBT;
import org.newdawn.slick.Color;


/**
 * Created by Matej on 21.12.2015.
 */
public final class LocationParty extends Location {

    private boolean fresh;
    private Helper helper;
    private Helper santa;
    private Stuff lightStuff;
    private Bitmap darkBitmap;
    private StuffToCome door;

    private boolean isOfficeUnlocked;

    private boolean dark;

    public LocationParty() {
        super("party");
    }

    @Override
    public void onStart() {
        MPlayer2.stopSound("arctic_wind");
        if (fresh) {
            onStartOnce();
        } else {
            dark = !Game.getWorld().getModule().getNBT().getBoolean("isElectricityOn", false);
            Game.renderer.removeBitmapProvider(santa.getBitmapStack());
            getArrowByID("toHall").setEnabled(!dark);
            door.setVisible(!dark);
        }
        super.onStart();
        fresh = false;

    }

    private void onStartOnce() {
        Game.core.EVENT_BUS.addEvent(()->Game.input.muteInput(true));
        //make sure that joe wont have mail
        Game.getWorld().inventory().stealItem(Items.itemMail);

        Game.getWorld().getModule().getNBT().putBoolean("panic", true);
        MPlayer2.playSong("party", 1, 1);
        Game.getWorld().inventory().lock(true);
        getArrowByID("toHall").setEnabled(false);
        door.setVisible(false);

        //helper
        helper.setAnimationSpeed(5);
        helper.setIsDancing(true);
        helper.setPos(160, 110);
        //joe
        Game.getWorld().getUniCreature().setPos(190, 150);
        ((Joe) Game.getWorld().getUniCreature()).setIsDancing(true);

        //santa
        santa.setIsDancing(true);
        santa.setPos(70, 140);
        santa.setAnimationSpeed(20);

        DialogEvent event = new DialogEvent("startPartyDialog");
        event.registerTalkable(santa, "santa", Color.red);
        event.registerTalkable(Game.getWorld().getUniCreature(), "joe", Color.green);
        Game.core.EVENT_BUS.addDelayedEvent(Game.core.TARGET_TPS * (Game.isDebugging ? 1 : 8), event);

        Game.core.EVENT_BUS.addDelayedEvent((Game.isDebugging ? 1 : 25) * Game.core.TARGET_TPS, new Event() {
            @Override
            public void dispatchEvent() {
                dark = true;
                MPlayer2.stopSong();
                MPlayer2.playSound("light_switch");
                MPlayer2.playSound("dj_stop", 1, 10);
                Game.core.EVENT_BUS.addDelayedEvent(60, () -> MPlayer2.playSound("panic", 0.2));
                Game.dialog.say("OOOOOOOOOOOOO eeee OOOOOO OOOOO AAAAA EEEEEEEEAAA AARGGHHHHH H!!!!!!! !!!!! !!!!!!", 6, Color.red);

                helper.setIsDancing(false);
                ((Joe) Game.getWorld().getUniCreature()).setIsDancing(false);

                //santa
                santa.setIsDancing(false);
                Game.renderer.removeBitmapProvider(santa.getBitmapStack());
                removeTickable(santa);

                lightStuff.setVisible(false);
                Game.renderer.setShadow(darkBitmap);
                DialogEvent thought = new DialogEvent("joeDarkThough");
                thought.registerTalkable(Game.getWorld().getUniCreature(), "joe", Color.green);
                Game.core.EVENT_BUS.addDelayedEvent(Game.core.TARGET_TPS * ((Game.isDebugging ? 2 : 8) + 1), () -> MPlayer2.playSound("panic", 0.2));
                Game.core.EVENT_BUS.addDelayedEvent(Game.core.TARGET_TPS * (Game.isDebugging ? 2 : 8), new Event() {
                    @Override
                    public void dispatchEvent() {
                        Game.input.muteInput(false);
                        Game.core.EVENT_BUS.addEvent(thought);
                        getArrowByID("toCottageFront").setEnabled(true);
                        Game.core.EVENT_BUS.addDelayedEvent(1, () -> MPlayer2.playSoundIfNotExist("panic", 0.2));
                    }
                });
            }
        });

        getArrowByID("toCottageFront").setOnExit(() -> MPlayer2.playSound("door"));
    }

    @Override
    public void onStop() {
        super.onStop();
        Game.getWorld().inventory().lock(false);
    }

    @Override
    public void onStartRendering(Renderer renderer) {
        renderer.registerBitmapProvider(helper.getBitmapStack());

        if (fresh) {
            renderer.registerBitmapProvider(santa.getBitmapStack());
        }

        lightStuff.setVisible(fresh);
        if (!fresh) {
            if (!dark) {
                if (!actionRectangleManager.contains(helper.getActionRectangle()))
                    actionRectangleManager.register(helper.getActionRectangle());
                setShadow(null);
                Game.renderer.setShadow(null);
            } else {
                setShadow(darkBitmap);
                Game.renderer.setShadow(darkBitmap);
            }
            getArrowByID("toCottageFront").setEnabled(true);
        }
        else getArrowByID("toCottageFront").setEnabled(false);
        super.onStartRendering(renderer);

    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public void writeToNBT(NBT nbt) {
        super.writeToNBT(nbt);
        nbt.putBoolean("fresh", fresh);
        nbt.putBoolean("dark", dark);
        nbt.putBoolean("officeUnlocked", isOfficeUnlocked);
    }

    @Override
    public void readFromNBT(NBT nbt) {
        super.readFromNBT(nbt);
        fresh = nbt.getBoolean("fresh", true);
        dark = nbt.getBoolean("dark");
        isOfficeUnlocked = nbt.getBoolean("officeUnlocked");
    }

    @Override
    public void loadTextures() {
        darkBitmap = Bitmap.get("/shadow/santa_home1");
        setShadow(Bitmap.get("/shadow/santa_home0"));

        //door
        door = (StuffToCome) getStuffByID("office_door");
        door.setOnPickedUp(new Runnable() {
            @Override
            public void run() {
                ItemStack inHand = Game.getWorld().inventory().getItemInHand();
                if (isOfficeUnlocked) {
                    LocationLoadEvent event = new LocationLoadEvent("office");
                    event.setJoesLocation(230, 175);
                    event.setJoeRightFacing(false);
                    Game.core.EVENT_BUS.addEvent(event);
                } else {
                    if (inHand != null && inHand.ITEM.ID == Items.itemKey.ID) {
                        Game.input.muteInput(true);
                        SpeakEvent event = new SpeakEvent("location.party.unlocking");
                        event.setOnEnd(() -> {
                            Game.input.muteInput(false);
                            isOfficeUnlocked = true;
                            MPlayer2.playSound("unlocked");
                            Game.getWorld().inventory().setItemInHand(null);
                            Game.core.EVENT_BUS.addDelayedEvent(30, new SpeakEvent("location.party.unlocked"));
                        });
                        Game.core.EVENT_BUS.addEvent(event);

                    } else {
                        MPlayer2.playSound("locked");
                        Game.core.EVENT_BUS.addDelayedEvent(30, new SpeakEvent("isLocked"));
                    }
                }
            }
        });

        //lights
        lightStuff = getStuffByID("lights");
        lightStuff.setRectangle(0, 0, 0, 0);
        registerTickable((Animation) lightStuff.getBitmapProvider());


        //santa
        santa = new Helper("santa");
        santa.loadTextures();
        santa.setPos(70, 140);
        registerTickable(santa);

        //dancingdwarf1
        helper = new Helper("helper0");
        helper.loadTextures();
        helper.setAction(new IAction() {
            @Override
            public boolean onClicked(int x, int y, boolean right_button, boolean released, ItemStack item) {
                if (released)
                    return true;
                DialogEvent event = new DialogEvent("partyDialog");
                event.registerTalkable(Game.getWorld().getUniCreature(), "joe", Color.green);
                event.registerTalkable(helper, helper.getName(), Color.darkGray);
                Game.core.EVENT_BUS.addEvent(event);
                return false;
            }
        });
        helper.setPos(100, 120);
        registerTickable(helper);
        //dancingdwarf2
        //dancingdwarf3
        //dancingdwarf4
    }
}
