package cs.cooble.stuff;


import cs.cooble.core.Game;
import cs.cooble.entity.Mover;
import cs.cooble.entity.Position;
import cs.cooble.event.LocationLoadEvent;
import cs.cooble.graphics.Bitmap;
import cs.cooble.graphics.BitmapStack;
import cs.cooble.inventory.item.ItemStack;
import cs.cooble.inventory.stuff.Stuff;
import cs.cooble.item.Items;
import cs.cooble.music.MPlayer2;
import cs.cooble.resources.ResourceStackBuilder;
import cs.cooble.translate.Translator;
import cs.cooble.world.IAction;
import cs.cooble.world.NBT;
import cs.cooble.world.TickableRegister;

/**
 * Created by Matej on 6.8.2016.
 */
public class StuffLever extends Stuff {

    private BitmapStack bitmaps;
    private boolean open;
    private int opening;
    private Bitmap fuseFround;
    private boolean on;

    public StuffLever(String ID,TickableRegister tickableRegister,Bitmap fuseFround) {
        super(ID);
        this.fuseFround = fuseFround;
        fuseFround.setShouldRender(false);
        bitmaps = BitmapStack.getBitmapStack(ResourceStackBuilder.buildResourcesStack("item/fuse", "", "0", "1", "2", "3"));
        setLocationTexture(bitmaps);
        setTextName(Translator.translate(this.getFullName() + (open ? "1" : "0")));
        setAction(new IAction() {
            @Override
            public boolean onClicked(int x, int y, boolean right_button, boolean released, ItemStack item) {
                if (!released)
                    return true;
                if(on){//switch off
                    MPlayer2.playSound("cvak_0");
                    bitmaps.setCurrentIndex(1);
                    Game.getWorld().getModule().getNBT().putBoolean("panic", true);
                    Game.getWorld().getModule().getNBT().putBoolean("isElectricityOn", false);
                    MPlayer2.playSound("panic",0.2);
                    LocationLoadEvent reload = new LocationLoadEvent(Game.getWorld().getLocationManager().getCurrentLocationID(), null);
                    Game.core.EVENT_BUS.addEvent(reload);
                    on=false;
                }
                else if (open) {//switch on
                    on=true;
                    MPlayer2.playSound("cvak_1");
                    bitmaps.setCurrentIndex(2);
                    Game.getWorld().getModule().getNBT().putBoolean("isElectricityOn", true);
                    MPlayer2.stopSound("panic");
                    Game.getWorld().getModule().getNBT().putBoolean("panic",false);
                    LocationLoadEvent reload = new LocationLoadEvent(Game.getWorld().getLocationManager().getCurrentLocationID(), null);
                    Game.core.EVENT_BUS.addEvent(reload);
                }
                else {//unscrew
                    if (item != null && item.ITEM.equals(Items.itemScrewdriver)) {
                        Game.input.muteInput(true);
                        tickableRegister.registerTickable(new Mover(new Position(7 * 2, 80 * 2), () -> {
                            MPlayer2.playSound("screw");
                            opening = 133;
                        }));

                    }
                }
                setTextName(Translator.translate(getFullName() + (open ? "1" : "0")));
                return true;
            }
        });
    }

    @Override
    public void tick() {
        super.tick();
        fuseFround.setShouldRender(open);
        if (opening > 0) {
            opening--;
            if (opening == 0) {
                Game.input.muteInput(false);
                MPlayer2.playSound("metal");
                open = true;
                bitmaps.setCurrentIndex(1);
                Game.getWorld().inventory().putItemInHandBack();
                Game.getWorld().inventory().setOffImmediately();
                setTextName(Translator.translate(this.getFullName() + (open ? "1" : "0")+".name"));
            }
        }
        if(on){
            bitmaps.setCurrentIndex(2);
        }
    }

    @Override
    public void writeToNBT(NBT nbt) {
        super.writeToNBT(nbt);
        nbt.putBoolean("open", open);
        nbt.putBoolean("on",on);
        nbt.putInteger("opening",opening);

    }

    @Override
    public void readFromNBT(NBT nbt) {
        super.readFromNBT(nbt);
        open = nbt.getBoolean("open");
        on=nbt.getBoolean("on");
        opening=nbt.getInteger("opening");
        if (open)
            bitmaps.setCurrentIndex(1);

        setTextName(Translator.translate(this.getFullName() + (open ? "1" : "0")+".name"));
    }
}


