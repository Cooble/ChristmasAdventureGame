package mc.cooble.location;


import mc.cooble.core.Game;
import mc.cooble.entity.Arrow;
import mc.cooble.entity.Mover;
import mc.cooble.entity.Weather;
import mc.cooble.event.SpeakEvent;
import mc.cooble.graphics.Bitmap;
import mc.cooble.graphics.BitmapStack;
import mc.cooble.graphics.Renderer;
import mc.cooble.inventory.item.ItemStack;
import mc.cooble.inventory.stuff.Stuff;
import mc.cooble.inventory.stuff.StuffToCome;
import mc.cooble.item.Items;
import mc.cooble.music.MPlayer2;
import mc.cooble.translate.Translator;
import mc.cooble.world.IAction;
import mc.cooble.world.Location;
import mc.cooble.world.NBT;

import java.util.function.Supplier;

/**
 * Created by Matej on 13.12.2015.
 */
public final class LocationPostBox extends Location {

    private int smokeSlower;
    private Bitmap steam;

    private int clicksOnPost;
    private int speaksOnPost;
    private int postBoxState;

    private Stuff postBoxStick;
    private Weather weather;
    private Stuff mail;

    public LocationPostBox() {
        super("post_box");
    }

    @Override
    public void onStartRendering(Renderer renderer) {
        super.onStartRendering(renderer);
        Game.renderer.registerBitmapProvider(steam);
        MPlayer2.playSound("arctic_wind");
    }

    @Override
    public void onStop() {
        super.onStop();
        MPlayer2.stopSound("arctic_wind");
    }

    @Override
    public void tick() {
        super.tick();
        if (steam.shouldRender()) {
            smokeSlower++;
            if (smokeSlower > 2) {
                smokeSlower = 0;
                int[] offset = steam.getOffset();
                steam.setOffset(offset[0], offset[1] - 1);
                if (offset[1] < -30 * 2) {
                    Game.renderer.removeBitmapProvider(steam);
                    steam.setShouldRender(false);
                }
            }
        }
    }

    @Override
    public void writeToNBT(NBT nbt) {
        super.writeToNBT(nbt);
        nbt.putInteger("postBoxState", postBoxState);
        nbt.putInteger("clicksOnPost", clicksOnPost);
        nbt.putInteger("speaksOnPost", speaksOnPost);
    }

    @Override
    public void readFromNBT(NBT nbt) {
        super.readFromNBT(nbt);
        postBoxState = nbt.getInteger("postBoxState");
        clicksOnPost = nbt.getInteger("clicksOnPost");
        speaksOnPost = nbt.getInteger("speaksOnPost");

        ((BitmapStack)postBoxStick.getBitmapProvider()).setCurrentIndex(postBoxState);
    }
    private int speakIndex=-1;
    @Override
    public void loadTextures() {
        //steam
        steam = Bitmap.get("item/steam_cloud");
        steam.setShouldRender(false);

        //mail
        StuffToCome mail = (StuffToCome) getStuffByID("mail");
        mail.setItem(new ItemStack(Items.itemMail));
        mail.setTextName(Translator.translate(getLocationPrefix() + ".mail.collect"));
        mail.setLore(null);
        mail.setOnPickedUp(() -> {
            Game.getWorld().inventory().setOn();
            MPlayer2.playSound("paper");
        });
        mail.setVisible(false);

        postBoxStick = getStuffByID("postbox");
        postBoxStick.setTextNameAsName();
        postBoxStick.setLore(new Supplier<String>() {
            @Override
            public String get() {
                speakIndex++;
                if(speakIndex>=2){
                    Game.getWorld().getModule().getNBT().putBoolean("find_post", true);
                }
                if(speakIndex>=3){
                    speakIndex=0;
                }
                if(postBoxState==2)
                    return getLocationPrefix()+".stuff.postbox.comment.3";

                return getLocationPrefix()+".stuff.postbox.comment."+speakIndex;
            }
        });
        postBoxStick.setAction(new IAction() {
            @Override
            public boolean onClicked(int x, int y, boolean right_button, boolean released, ItemStack item) {
                if(!released)
                    return false;
                if(item!=null&&item.ITEM.ID==Items.itemPot.ID&&postBoxState==0){
                    registerTickable(new Mover(mail.getPositionToCome(),()->{
                        Game.getWorld().inventory().setItemInHand(null);
                        steam.setShouldRender(true);
                        postBoxState=1;
                        ((BitmapStack)postBoxStick.getBitmapProvider()).setCurrentIndex(postBoxState);
                        MPlayer2.playSound("steam");
                        SpeakEvent event = new SpeakEvent(getLocationPrefix()+".stuff.postbox.success");
                        Game.core.EVENT_BUS.addEvent(event);
                    }));


                    return true;
                }
                else if(postBoxState==1){
                    registerTickable(new Mover(mail.getPositionToCome(),()->{
                        mail.setVisible(true);
                        postBoxState=2;
                        ((BitmapStack)postBoxStick.getBitmapProvider()).setCurrentIndex(1);
                        ((BitmapStack)postBoxStick.getBitmapProvider()).getCurrentBitmap().setShouldRender(true);
                        postBoxStick.setRectangle(0,0,0,0);
                    }));
                }
                return false;
            }
        });
        steam.setOffset(postBoxStick.getBitmapProvider().getOffset());
        //arrow
        Arrow homeArrow = getArrowByID("toHome");
        homeArrow.setOnExit(() -> MPlayer2.playSound("door", 1));
        //end of postbox arrow

        weather = new Weather(new Supplier<Bitmap>() {
           BitmapStack b =  BitmapStack.getBitmapStackFromFolder("item/snow_spark").resize(0.5);
            @Override
            public Bitmap get() {
                return b.getBitmap(Game.random.nextInt(b.getMaxLength())).copy();
            }
        });
        weather.setSpeed(0.01,0.2);
        weather.setCadence(1);
        registerTickable(weather);
        weather.setEnabled(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        postBoxStick.setTextName(Translator.translate(postBoxStick.getFullName()+".name"));
        for (int i = 0; i < Game.core.TARGET_TPS * 5; i++) {
            weather.tick();
        }
    }
}

