package cooble.ch.event;

import cooble.ch.core.Game;
import cooble.ch.graphics.Renderer;
import cooble.ch.graphics.dialog.DialogManager;
import cooble.ch.logger.Log;
import cooble.ch.world.CustomSettings;
import cooble.ch.world.NBT;
import cooble.ch.world.World;

import java.util.ArrayList;

/**
 * Created by Matej on 12.12.2015.
 * Consists of preInit, init, postInit event
 */
public class GameLoadEvent implements Event,PreInitEvent,InitEvent,PostInitEvent {

    /**
     * Register GameLoadEvent as Pre,Init,Post Event Consumer
     *
     *
     * Note!
     *      !!!Call this constructor before you start adding initEvents into Game!!!
     */
    public GameLoadEvent() {

        Game.registerPreInitEventConsumer(this);
        Game.registerInitEventConsumer(this);
        Game.registerPostInitEventConsumer(this);

    }

    /**
     * 1. call PreInitEvents
     * 2. call InitEvents
     * 3. call PostInitEvents
     */
    @Override
    public void dispatchEvent() {
        ArrayList<PreInitEvent> preInitEvents = Game.getPreInitEvents();
        for(PreInitEvent event:preInitEvents){
            event.preInit();
        }
        ArrayList<InitEvent> initEvents = Game.getInitEvents();
        for(InitEvent event:initEvents){
            event.init();
        }
        ArrayList<PostInitEvent> postInitEvents = Game.getPostInitEvents();
        for(PostInitEvent event:postInitEvents){
            event.postInit();
        }
        Game.core.EVENT_BUS.addEvent(()->Game.isReadyToPlay=true);
    }

    /**
     * PreInitEvent set Saver,
     *                  Translator,
     *                  Renderer
     *                  load Settings (if exists)
     */
    @Override
    public void preInit() {
        Log.println("PRE-INIT in GameLoad");
        Log.println("Making default folders");
        if(Game.saver.makeDefaultFoldersFiles()){
            NBT nbt = new NBT();
            new CustomSettings().writeToNBT(nbt);
            Game.saver.saveSettingsNBT(nbt);
            Game.saveSettings();
        }
        Log.println("Loading world");
        Game.getSettings().readFromNBT(Game.saver.loadSettingsNBT());
        Log.println("Loading language");
        Game.setLanguage(Game.getSettings().getString(Game.getSettings().LANG));


    }

    /**
     * InitEvent    gives the World NBT to process later in postInit
     *              gives Renderer main Paintable objects such as:  Inventory
     *                                                              TextManager/TextPainter
     *
     *
     */
    @Override
    public void init() {
        Log.println("INIT in GameLoad");
        Game.renderer = new Renderer(Game.getWIDTH(), Game.getHEIGHT());
        Game.dialog = new DialogManager();

        Game.setWorld(new World(Game.saver.loadWorld()));
        Game.renderer.registerGUIProvider(Game.getWorld().inventory());
        Game.renderer.registerGUIProvider(Game.dialog.getAnswerPainter());
        Game.renderer.registerGUIProvider(Game.dialog.getDialogPainter());

        Game.input.registerMouseEventConsumer(Game.getWorld().inventory(),UserInput.HIGH_PRIORITY);
        Game.input.registerMouseEventConsumer(Game.dialog, UserInput.HIGH_PRIORITY);
    }

    /**
     * PostInitEvent
     *              calls World.loadNBT() to load its NBT
     *              looks what Modules/Location loadNow
     */
    @Override
    public void postInit() {
        Log.println("POST-INIT in GameLoad");
        Game.core.EVENT_BUS.addEvent(new LocationLoadEvent("blank_loc","blank_module"));

    }


}
