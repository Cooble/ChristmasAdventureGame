package cs.cooble.location;


import cs.cooble.core.Game;
import cs.cooble.event.Event;
import cs.cooble.event.SpeakEvent;
import cs.cooble.graphics.Bitmap;
import cs.cooble.graphics.Renderer;
import cs.cooble.inventory.item.ItemStack;
import cs.cooble.inventory.stuff.Stuff;
import cs.cooble.inventory.stuff.StuffToCome;
import cs.cooble.item.Items;
import cs.cooble.music.MPlayer2;
import cs.cooble.resources.StringStream;
import cs.cooble.translate.Translator;
import cs.cooble.world.IAction;
import cs.cooble.world.Location;
import cs.cooble.world.NBT;

/**
 * Created by Matej on 12.12.2015.
 */
public final class LocationGarageIn extends Location {

    private Stuff blueprintPonk;
    private Bitmap ponkMap;
    private boolean isBluePlaced;
    private boolean isBatteryPlaced;
    private boolean isElectronicsPlaced;
    private boolean isQuadracopterVisible;
    private boolean isLuxVisible;

    private int fans;
    private static final String ID="garage_in";

    public LocationGarageIn() {
        super(ID);
        setJoeBitmapSize(1.5);
    }


    @Override
    public void onStartRendering(Renderer renderer) {
        super.onStartRendering(renderer);
        MPlayer2.stopSound("arctic_wind");
        //MPlayer2.playSongWithFade("christmas_song", 0.2, 100, 50);
        for (int i = 1; i < fans + 1; i++) {
            getStuffByID("fan"+i).setVisible(true);
        }
        getStuffByID("battery").setVisible(isBatteryPlaced);
        getStuffByID("electronics").setVisible(isElectronicsPlaced);
        getStuffByID("quadracopter").setVisible(isQuadracopterVisible);
        getStuffByID("lux").setVisible(isLuxVisible);
    }

    @Override
    public void onStopRendering(Renderer renderer) {
        super.onStopRendering(renderer);
    }

    @Override
    public void loadTextures() {
        //bp
        StringStream stringsBeforePlace = StringStream.getStream(getLocationPrefix() + ".stuff.blueprint_no_place.comment.");
        StringStream stringsAfterPlaced = StringStream.getStream(getLocationPrefix()+".stuff.blueprint_placed.comment.");

        blueprintPonk = getStuffByID("blueprint");
        ponkMap= (Bitmap) blueprintPonk.getBitmapProvider();
        blueprintPonk.setTextName(Translator.translate(blueprintPonk.getFullName()+".name."+(isBluePlaced?"1":"0")));
        blueprintPonk.setAction(new IAction() {
            @Override
            public boolean onClicked(int x, int y, boolean right_button, boolean released, ItemStack item) {
                if (!released)
                    return true;
                SpeakEvent event=null;
                if(!isBluePlaced&&item!=null){
                    if(item.ITEM.ID==Items.itemFan.ID||item.ITEM.ID==Items.itemBigBattery.ID||item.ITEM.ID==Items.itemLux.ID||item.ITEM.ID==Items.itemElectronics.ID){
                        Game.core.EVENT_BUS.addEvent(new SpeakEvent("entity.joe.noBlueprint"));
                        return true;
                    }
                }
                if (item != null && item.ITEM.ID == Items.itemBluePrint.ID) {//clicked with blueprint to put it on
                    isBluePlaced = true;
                    blueprintPonk.setTextName(Translator.translate(blueprintPonk.getFullName()+".name."+(isBluePlaced?"1":"0")));
                    ponkMap.setShouldRender(true);
                    Game.getWorld().inventory().setItemInHand(null);
                    event = new SpeakEvent(getLocationPrefix()+".comment.blueprintplaced.0");

                }else if (isBluePlaced) {
                    if(item!=null){
                        boolean put=true;
                        if(item.ITEM.ID==Items.itemFan.ID){
                            Game.getWorld().inventory().setItemInHand(null);
                            fans++;
                            getStuffByID("fan"+fans).setVisible(true);
                        }
                        else if(item.ITEM.ID==Items.itemBigBattery.ID){
                            Game.getWorld().inventory().setItemInHand(null);
                            getStuffByID("battery").setVisible(true);
                            isBatteryPlaced=true;
                        }
                        else if(item.ITEM.ID==Items.itemLux.ID){
                            Game.getWorld().inventory().setItemInHand(null);
                            getStuffByID("lux").setVisible(true);
                            isLuxVisible=true;
                        }
                        else if(item.ITEM.ID==Items.itemElectronics.ID){
                            Game.getWorld().inventory().setItemInHand(null);
                            getStuffByID("electronics").setVisible(true);
                            isElectronicsPlaced=true;
                        }
                        else put=false;
                        if(put){
                            MPlayer2.playSound("cvak_1");
                        }
                    }
                    else if(isLuxVisible&&isBatteryPlaced&&isElectronicsPlaced&&fans==4){//lets craft it
                            Game.input.muteMouseInput(true);
                            SpeakEvent event1 = new SpeakEvent("entity.joe.craftquadra");
                            Game.core.EVENT_BUS.addEvent(event1);
                            event1.setOnEnd(new Runnable() {
                                @Override
                                public void run() {
                                    Game.input.muteInput(true);
                                    Game.renderer.enableBlackScreen(true);
                                    MPlayer2.playSound("craft0");
                                    MPlayer2.playSound("craft1",120);
                                    MPlayer2.playSound("craft2",300);
                                    isElectronicsPlaced=false;
                                    isBatteryPlaced=false;
                                    isLuxVisible=false;
                                    fans=0;
                                    Game.core.EVENT_BUS.addDelayedEvent(Game.core.TARGET_TPS * 7, new Event() {
                                        @Override
                                        public void dispatchEvent() {
                                            Game.input.muteInput(false);
                                            Game.renderer.enableBlackScreen(false);
                                            getStuffByID("battery").setVisible(false);
                                            getStuffByID("electronics").setVisible(false);
                                            getStuffByID("lux").setVisible(false);
                                            for (int i = 1; i < 5; i++) {
                                                getStuffByID("fan"+i).setVisible(false);
                                            }
                                            isQuadracopterVisible=true;
                                            getStuffByID("quadracopter").setVisible(true);
                                            Game.core.EVENT_BUS.addEvent(new SpeakEvent("entity.joe.quadra_crafted"));
                                            Game.input.muteMouseInput(false);
                                        }
                                    });

                                }
                            });
                    }
                    else event = new SpeakEvent(stringsAfterPlaced.getNextString());
                } else {
                    event = new SpeakEvent(stringsBeforePlace.getNextString());
                }
                if(event!=null)
                Game.core.EVENT_BUS.addEvent(event);
                return true;
            }
        });
        ponkMap.setShouldRender(false);

        ((StuffToCome)getStuffByID("fan0")).setItem(new ItemStack(Items.itemFan));
        ((StuffToCome)getStuffByID("quadracopter")).setItem(new ItemStack(Items.itemQuadracopter));

        for (int i = 1; i < 5; i++) {
            getStuffByID("fan"+i).setVisible(false);
        }
        getStuffByID("lux").setVisible(false);
    }

    @Override
    public void readFromNBT(NBT nbt) {
        super.readFromNBT(nbt);
        fans = nbt.getInteger("fans");
        isBatteryPlaced=nbt.getBoolean("battery");
        isQuadracopterVisible=nbt.getBoolean("isQuadracopterVisible");
        isElectronicsPlaced=nbt.getBoolean("isElectronicsPlaced");
        isLuxVisible=nbt.getBoolean("isLuxVisible");
        isBluePlaced =Game.getWorld().getModule().getNBT().getBoolean("isBlueprintPlaced",false);
        ponkMap.setShouldRender(isBluePlaced);
        blueprintPonk.setTextName(Translator.translate(blueprintPonk.getFullName()+".name."+(isBluePlaced?"1":"0")));
    }

    @Override
    public void writeToNBT(NBT nbt) {
        super.writeToNBT(nbt);
        nbt.putInteger("fans",fans);
        nbt.putBoolean("battery",isBatteryPlaced);
        nbt.putBoolean("isLuxVisible",isLuxVisible);
        nbt.putBoolean("isElectronicsPlaced",isElectronicsPlaced);
        nbt.putBoolean("isQuadracopterVisible",isQuadracopterVisible);
        Game.getWorld().getModule().getNBT().putBoolean("isBlueprintPlaced", isBluePlaced);
    }
}
