package cs.cooble.world;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import cs.cooble.core.Game;
import cs.cooble.entity.*;
import cs.cooble.event.MouseEventConsumer;
import cs.cooble.graphics.*;
import cs.cooble.inventory.stuff.Stuff;
import cs.cooble.inventory.stuff.StuffToCome;
import cs.cooble.music.MPlayer2;
import cs.cooble.window.Tickable;

import java.awt.*;
import java.util.ArrayList;
import java.util.function.Supplier;

/**
 * Created by Matej on 12.12.2015.
 */
public abstract class Location implements Tickable, MouseEventConsumer, NBTSaveable, TickableRegister, TextureLoadable {

    protected String backgroundSound,backgroundMusic;
    protected double backgroundSoundVolume,backgroundMusicVolume;


    protected BitmapProvider background;
    protected BitmapProvider foreground;
    protected BitmapProvider shadow;
    protected ActionRectangleManager actionRectangleManager;
    protected ArrayList<Stuff> stuffs;
    private ArrayList<Tickable> tickables;
    private BoolMap boolMap;
    private ArrayList<Arrow> arrows;
    private BitmapProvider[] fore;
    private boolean isSubLocation;

    private boolean isJoeProhibited;

    private double joeBitmapSize;


    private final String LOCID;

    public Location(String id) {
        stuffs = new ArrayList<>();
        LOCID = id;
        actionRectangleManager = new ActionRectangleManager();
        tickables = new ArrayList<>();
        fore = new BitmapProvider[2];
        isJoeProhibited = false;
        arrows = new ArrayList<>();
        joeBitmapSize=1;

    }
    protected final Stuff getStuffByID(String id){
        id= generateStuffName(id);
        if(stuffs.size()==0)
            System.out.println("we are low");
        for (Stuff stuff : stuffs) {
            System.err.println("listing full names "+stuff.getFullName());
            if (stuff.getFullName().equals(id)) {
                return stuff;
            }
        }
        Stuff stuff = new Stuff(id);
        addStuff(stuff);
        return stuff;
    }
    protected final boolean existStuff(String ID){
        ID= generateStuffName(ID);
        for (Stuff stuff : stuffs) {
            if (stuff.getOnlyName().equals(ID)) {
                return true;
            }
        }
        return false;
    }
    public final String generateStuffName(String stuffID){
        return "location."+getLOCID()+".stuff."+stuffID;
    }
    public final String getLocationPrefix(){
        return "location."+getLOCID();
    }
    protected final Arrow getArrowByID(String id){
        for (Arrow a : arrows) {
            if (a.getName().equals(id))
                return a;
        }
        Arrow arrow = new Arrow(id);
        addArrow(arrow);
        return arrow;
    }

    protected final void setJoeProhibited(boolean b) {
        isJoeProhibited = b;
    }

    protected final void setDefaultShadow() {
        setShadow(Bitmap.get("shadow/" + this.getLOCID()));
    }

    public final boolean isJoeProhibited() {
        return isJoeProhibited;
    }

    public final String getLOCID() {
        return LOCID;
    }

    protected final void setBackground(BitmapProvider bitmapProviderd) {
        background = bitmapProviderd;
    }
    protected final void setDefaultBackground() {
        background = Bitmap.get("location/"+LOCID);
    }

    protected final void setForeground(BitmapProvider bitmapProviderd) {
        foreground = bitmapProviderd;
    }

    protected final void setBoolMap(BoolMap boolMap) {
        this.boolMap = boolMap;
    }

    protected final void addArrow(Arrow arrow) {
        arrows.add(arrow);
        actionRectangleManager.register(tosupplier(arrow.getActionListener()));
    }
    private Supplier<IActionRectangle> tosupplier(IActionRectangle rectangle){
        return new Supplier<IActionRectangle>() {
            @Override
            public IActionRectangle get() {
                return rectangle;
            }
        };
    }

    protected final void addArrowWithMover(Arrow arrow) {
        addArrow(arrow);
        registerTickable(arrow);
    }


    /**
     * sets default boolmap with name of LOCID
     */
    protected void setDefaultBoolMap() {
        setBoolMap(BoolMap.getBoolMap(LOCID));
    }

    @NotNull
    protected final BitmapProvider getBackground() {
        return background;
    }


    @Nullable
    protected final BitmapProvider getForeground() {
        return foreground;
    }

    @Nullable
    protected final BitmapProvider getShadow() {
        return shadow;
    }

    protected final void setShadow(@Nullable BitmapProvider shadow) {
        this.shadow = shadow;
    }

    @Override
    public void tick() {
        if (tickables != null) {
            for (int i = 0; i < tickables.size(); i++) {//don't convert to foreach!
                Tickable tickable = tickables.get(i);
                if (tickable != null)
                    tickable.tick();
            }
        }
        for (int i = stuffs.size() - 1; i >= 0; i--) {
            Stuff stuff = stuffs.get(i);
            if (stuff.isMarkedDeath() && !stuff.isDeath()) {
                actionRectangleManager.remove(stuff);
                Game.renderer.removeBitmapProvider(stuff.getBitmapProvider());
                stuff.setDeath();
            } else {
                stuff.tick();
            }
        }
    }

    private BitmapStack joeOriginalBitmap;
    private double joeOriginalSpeed;
    /**
     * called when location started to be rendered
     */
    public void onStartRendering(Renderer renderer) {

        if(backgroundSound!=null){
            MPlayer2.loopSound(backgroundSound, backgroundSoundVolume);
        }else MPlayer2.stopLoop();

        renderer.setBackground(background);
        fore[0] = foreground;
        renderer.setForeground(new MultiBitmapProvider() {

            @Override
            public BitmapProvider[] getBitmaps() {
                return fore;
            }

            @Override
            public boolean shouldRender() {
                return true;
            }
        });
        for (Stuff stuff : stuffs) {
            if (stuff.getBitmapProvider() != null && !stuff.isDeath())
                renderer.registerBitmapProvider(stuff.getBitmapProvider());
        }
        if (arrows != null)
            for (Arrow arrow : arrows) {
                if (arrow.isEnabled())
                    if (arrow.getBitmapProvider() != null)
                        renderer.registerBitmapProvider(arrow.getBitmapProvider());
            }
        renderer.setShadow(shadow == null ? null : shadow.getCurrentBitmap());

        if(!isSubLocation()&&!isJoeProhibited()){
            if(joeBitmapSize!=1){
                joeOriginalBitmap=Game.getWorld().getUniCreature().getBitmapStack();
               // joeOriginalSpeed=Game.getWorld().getUniCreature().getSpeed();//speed modify not working
               // Game.getWorld().getUniCreature().setSpeed(joeOriginalSpeed*joeBitmapSize);
                Game.getWorld().getUniCreature().setBitmapStack(joeOriginalBitmap.resize(joeBitmapSize));
            }
            Game.renderer.registerBitmapProvider(Game.getWorld().getUniCreature().getBitmapStack());
        }
    }

    /**
     * called when location stopped to be rendered
     */
    public void onStopRendering(Renderer renderer) {
        if (arrows != null)
            for (Arrow arrow : arrows) {
                if (arrow.isEnabled())
                    if (arrow.getBitmapProvider() != null)
                        renderer.removeBitmapProvider(arrow.getBitmapProvider());
            }
        if(joeOriginalBitmap!=null){
             Game.getWorld().getUniCreature().setBitmapStack(joeOriginalBitmap);
           //  Game.getWorld().getUniCreature().setSpeed(joeOriginalSpeed);

        }
        //if (shadow != null)
        //  renderer.removeBitmapProvider(shadow);

    }

    /**
     * called when location is loaded
     * use super.start() when overriding
     */
    public void onStart() {
       onStartRendering(Game.renderer);
    }

    /**
     * called when location is unloaded
     */
    public void onStop() {
        onStopRendering(Game.renderer);
    }

    public boolean consume(int x, int y, int mouseEvent, boolean released) {
        x /= Game.renderer.PIXEL_SIZE;
        y /= Game.renderer.PIXEL_SIZE;

        if (!actionRectangleManager.consume(x, y, mouseEvent, released)) {
            if (released) {
                if (boolMap != null) {
                    if (boolMap.getBoolean(x, y)) {
                        if (tickables.isEmpty())
                            tickables.add(null);
                        tickables.set(0, new InteligentMover(new Position(x, y),boolMap, null));
                        return true;
                    }
                }
            }
        }
        return false;
    }

    protected final void addStuff(Stuff stuff) {
        if(stuff instanceof StuffToCome){
            ((StuffToCome)stuff).packIt(actionRectangleManager,boolMap);
            if (!stuff.isDeath()) {
                stuffs.add(stuff);
            }
        }
        else {
            stuffs.add(stuff);
            if (!stuff.isDeath()) {
                actionRectangleManager.register(stuff);
            }
        }
        if(stuff.getBitmapProvider() instanceof Animation){
            registerTickable(stuff);
        }
    }

    public final boolean removeTickable(Tickable tickable) {
        for (int i = 0; i < tickables.size(); i++) {
            if (tickables.get(i) != null && tickables.get(i).equals(tickable)) {
                tickables.remove(i);
                return true;
            }
        }
        return false;
    }

    public final void registerTickable(Tickable tickable) {
        if (tickable instanceof Mover) {
            if (tickables.isEmpty())
                tickables.add(tickable);
            else
                tickables.set(0, tickable);
        } else {
            if (tickables.size() == 0)
                tickables.add(null);
            tickables.add(tickable);
        }
    }

    @Override
    public void readFromNBT(NBT nbt) {
        for (Stuff stuff : stuffs) {
            stuff.readFromNBT(nbt.getNBT("item_" + stuff.getFullName()));
        }
        //Boolean b =nbt.getBoolean("joe_prohibited");
        //isJoeProhibited =b;
    }

    @Override
    public void writeToNBT(NBT nbt) {
        for (Stuff stuff : stuffs) {
            stuff.writeToNBT(nbt.getNBT("item_" + stuff.getFullName()));
        }
        //nbt.putBoolean("joe_prohibited", isJoeProhibited);
    }

    @Override
    public boolean isDirty() {
        return false;
    }

    //======================================================UTILS============================================================================================================================


    /**
     * Which ratio of bitmap of joe should be used in location i.e. when joe is in small room -> joe should be big
     * @param joeBitmapSize
     */
    protected void setJoeBitmapSize(double joeBitmapSize) {
        this.joeBitmapSize = joeBitmapSize;
    }

    protected void setIsSubLocation(boolean isSubLocation) {
        this.isSubLocation = isSubLocation;
    }

    public boolean isSubLocation() {
        return isSubLocation;
    }
    protected final void setBackground(String bitmapPath) {
        setBackground(Bitmap.get(bitmapPath));
    }

    protected final void setForeground(String bitmapPath) {
        setForeground(Bitmap.get(bitmapPath));
    }


    public void setDefaultForeground() {
        foreground=Bitmap.getIfExists("location/" + LOCID + "_f");
    }

    /**
     * Gives stuff actionRectangleDimensions and bitmapStack offset of the preset xml entity
     * removes xml entity
     * @param newStuff
     * @param xmlName
     */
    public void addXMLSubstrate(Stuff newStuff,String xmlName){
        Stuff xml = getStuffByID(xmlName);
        Rectangle action = xml.getActionListener().getRectangle();
        newStuff.setRectangle(action.x, action.y, action.width, action.height);
        BitmapProvider provider = newStuff.getBitmapProvider();
        if(provider instanceof Bitmap&&xml.getBitmapProvider()!=null){
            ((Bitmap) provider).setOffset(xml.getBitmapProvider().getOffset()[0],xml.getBitmapProvider().getOffset()[1]);
        }
        else if(provider instanceof BitmapStack&&xml.getBitmapProvider()!=null){
            ((BitmapStack) provider).setOffset(xml.getBitmapProvider().getOffset()[0],xml.getBitmapProvider().getOffset()[1]);
        }

        stuffs.remove(stuffs.indexOf(xml));
        actionRectangleManager.remove(xml.getActionListener());

    }

    public BoolMap getBoolMap() {
        return boolMap;
    }
}
