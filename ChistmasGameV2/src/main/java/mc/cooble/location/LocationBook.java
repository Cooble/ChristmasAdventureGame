package mc.cooble.location;

import mc.cooble.core.Game;
import mc.cooble.entity.Arrow;
import mc.cooble.event.SpeakEvent;
import mc.cooble.graphics.Bitmap;
import mc.cooble.graphics.Renderer;
import mc.cooble.inventory.item.ItemStack;
import mc.cooble.inventory.stuff.Stuff;
import mc.cooble.inventory.stuff.StuffToCome;
import mc.cooble.item.Items;
import mc.cooble.music.MPlayer2;
import mc.cooble.translate.Translator;
import mc.cooble.world.Location;
import mc.cooble.world.NBT;

/**
 * Created by Matej on 12.3.2017.
 */
public class LocationBook extends Location {
    private Bitmap bitmap0;
    private Bitmap bitmap1;
    //private Bi
    private int site;

    private StuffToCome datasheet;
    private boolean firstRead;

    private Arrow arrowleft, arrowRight;


    public LocationBook() {
        super("book");
    }

    @Override
    public void loadTextures() {
        setBackground(Bitmap.create(Game.renderer.PIXEL_WIDTH, Game.renderer.PIXEL_HEIGHT));
        setJoeProhibited(true);

        arrowleft = getArrowByID("toLeft");
        arrowleft.setOnClicked(this::leftSide);
        arrowRight = getArrowByID("toRight");
        arrowRight.setOnClicked(this::rightSide);

        bitmap0 = Bitmap.resize(Bitmap.get("item/book_" + Translator.getLanguage() + "_0"), Game.renderer.PIXEL_WIDTH, Game.renderer.PIXEL_HEIGHT);
        bitmap1 = Bitmap.resize(Bitmap.get("item/book_" + Translator.getLanguage() + "_1"), Game.renderer.PIXEL_WIDTH, Game.renderer.PIXEL_HEIGHT);

        Stuff stuff = new Stuff(generateStuffName("openbook"));
        Stuff stuff1 = new Stuff(generateStuffName("openbook1"));

        stuff.setRectangle(0, 0, 0, 0);
        stuff1.setRectangle(0, 0, 0, 0);

        stuff.setTextName("");
        stuff1.setTextName("");

        stuff.setLocationTexture(bitmap0);
        stuff1.setLocationTexture(bitmap1);

        addStuff(stuff);
        addStuff(stuff1);

        datasheet = (StuffToCome) getStuffByID("book_blueprint");
        datasheet.setItem(new ItemStack(Items.itemBluePrint));
    }

    @Override
    public void onStartRendering(Renderer renderer) {
        super.onStartRendering(renderer);
        Game.renderer.removeBitmapProvider(datasheet.getBitmapProvider());//put this bitmapStack at top
        Game.renderer.registerBitmapProvider(datasheet.getBitmapProvider());
    }

    private void rightSide() {
        arrowRight.setEnabled(false);
        arrowleft.setEnabled(true);
        if (site != 0) {
            MPlayer2.playSound("paper");
            site--;
        }
        datasheet.setVisible(!datasheet.isDeath());
        bitmap1.setShouldRender(true);
        bitmap0.setShouldRender(false);
        if (firstRead) {
            firstRead = false;
            Game.core.EVENT_BUS.addEvent(new SpeakEvent("entity.joe.quadraidea"));
        } else Game.core.EVENT_BUS.addEvent(new SpeakEvent("entity.joe.ingredients"));

    }

    private void leftSide() {
        arrowRight.setEnabled(true);
        arrowleft.setEnabled(false);
        if (site != 1) {
            MPlayer2.playSound("paper");
            site++;
        }
        datasheet.setVisible(false);
        bitmap1.setShouldRender(false);
        bitmap0.setShouldRender(true);

    }

    @Override
    public void onStart() {
        super.onStart();

        Game.getWorld().inventory().lock(true);
        if (site == 0)
            rightSide();
        else leftSide();
    }

    @Override
    public void onStop() {
        super.onStop();
        Game.getWorld().inventory().lock(false);
        Game.core.EVENT_BUS.addEvent(new SpeakEvent(null));
    }

    @Override
    public void writeToNBT(NBT nbt) {
        super.writeToNBT(nbt);
        nbt.putInteger("site", site);
        nbt.putBoolean("firstRead", firstRead);
    }

    @Override
    public void readFromNBT(NBT nbt) {
        super.readFromNBT(nbt);
        site = nbt.getInteger("site", 1);
        firstRead = nbt.getBoolean("firstRead", true);
        datasheet.setVisible(site == 1&&!datasheet.isDeath());
    }
}
