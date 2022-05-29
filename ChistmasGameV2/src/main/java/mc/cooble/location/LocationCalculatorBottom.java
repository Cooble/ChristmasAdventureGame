package mc.cooble.location;


import mc.cooble.core.Game;
import mc.cooble.graphics.Bitmap;
import mc.cooble.graphics.Renderer;
import mc.cooble.inventory.item.ItemStack;
import mc.cooble.inventory.stuff.Stuff;
import mc.cooble.item.Items;
import mc.cooble.music.MPlayer2;
import mc.cooble.translate.Translator;
import mc.cooble.world.IAction;
import mc.cooble.world.Location;
import mc.cooble.world.NBT;

/**
 * Created by Matej on 7.8.2016.
 */
public class LocationCalculatorBottom extends Location {
    private boolean batteryOne;
    private boolean batteryTwo;
    private boolean isCovered;
    private Stuff bat1;
    private Stuff bat2;
    private Stuff cover;
    private Bitmap bit1;
    private Bitmap bit2;

    private int ticksToOpen;

    public LocationCalculatorBottom() {
        super("calculator_bottom");

    }

    private void sendPoweredIf() {
        if (batteryOne && batteryTwo) {
            Game.getWorld().getModule().getNBT().putBoolean("isCalculatorPowered", true);
        }
    }

    @Override
    public void tick() {
        super.tick();
        bat1.setVisible(!isCovered);
        bat2.setVisible(!isCovered);
        bit1.setShouldRender(batteryOne);
        bit2.setShouldRender(batteryTwo);
        cover.setVisible(isCovered);


        if (ticksToOpen > 0) {
            ticksToOpen--;
            if (ticksToOpen == 0) {
                isCovered = false;
                Game.input.muteInput(false);
                MPlayer2.playSound("metal");
                Game.getWorld().inventory().putItemInHandBack();
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (ticksToOpen > 0) {//if player exists location when screwing out cover
            ticksToOpen = 0;
            isCovered = false;
        }
    }

    @Override
    public void writeToNBT(NBT nbt) {
        super.writeToNBT(nbt);
        nbt.putBoolean("batteryOne", batteryOne);
        nbt.putBoolean("batteryTwo", batteryTwo);
        nbt.putBoolean("covered", isCovered);
    }

    @Override
    public void readFromNBT(NBT nbt) {
        super.readFromNBT(nbt);
        batteryOne = nbt.getBoolean("batteryOne");
        batteryTwo = nbt.getBoolean("batteryTwo");
        isCovered = nbt.getBoolean("covered", true);
    }

    @Override
    public void onStartRendering(Renderer renderer) {
        super.onStartRendering(renderer);
        refeshBatNames();
    }

    private void refeshBatNames() {
        bat1.setTextName(Translator.translate(bat1.getFullName() + (batteryOne ? "1" : "0")));
        bat2.setTextName(Translator.translate(bat2.getFullName() + (batteryTwo ? "1" : "0")));
    }

    @Override
    public void loadTextures() {
        setBackground(Bitmap.get("location/" + getLOCID()));

        bat1 = new Stuff(generateStuffName("bat1"));
        bit1 = Bitmap.get("item/battery");
        bit1.setOffset(38 * 2, 27 * 2);
        bat1.setLocationTexture(bit1);
        bat1.setAction(new IAction() {
            @Override
            public boolean onClicked(int x, int y, boolean right_button, boolean released, ItemStack item) {
                if (!released || isCovered)
                    return true;
                if (item != null && item.ITEM.equals(Items.itemBattery)) {
                    if (!batteryOne) {
                        batteryOne = true;
                        MPlayer2.playSound("cvak_1");
                        sendPoweredIf();
                        Game.getWorld().inventory().setItemInHand(null);
                        refeshBatNames();

                    }
                }
                return true;
            }
        });
        bat1.setRectangle(38 * 2, 27 * 2, 88 * 2, 17 * 2);
        addStuff(bat1);
        bat1.setVisible(false);


        bat2 = new Stuff(generateStuffName("bat2"));
        bit2 = Bitmap.get("item/battery").flip(false);
        bit2.setOffset(39 * 2, 49 * 2);
        bat2.setLocationTexture(bit2);
        bat2.setAction(new IAction() {
            @Override
            public boolean onClicked(int x, int y, boolean right_button, boolean released, ItemStack item) {
                if (!released || isCovered)
                    return true;
                if (item != null && item.ITEM.equals(Items.itemBattery)) {
                    if (!batteryTwo) {
                        batteryTwo = true;
                        MPlayer2.playSound("cvak_1");
                        sendPoweredIf();
                        Game.getWorld().inventory().setItemInHand(null);
                        refeshBatNames();
                    }
                }
                return true;
            }
        });
        bat2.setRectangle(39 * 2, 49 * 2, 88 * 2, 17 * 2);
        bat2.setVisible(false);
        addStuff(bat2);

        cover = getStuffByID("calculator_cover");
        cover.setAction(new IAction() {
            @Override
            public boolean onClicked(int x, int y, boolean right_button, boolean released, ItemStack item) {
                if (!released)
                    return true;
                if (isCovered && item != null && item.ITEM.equals(Items.itemScrewdriver) && ticksToOpen == 0) {
                    MPlayer2.playSound("screw");
                    Game.input.muteMouseInput(true);
                    ticksToOpen = 144;
                }
                return true;
            }
        });
    }
}
