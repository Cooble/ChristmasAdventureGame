package cooble.ch.module;

import cooble.ch.logger.Log;
import cooble.ch.world.CModules;
import cooble.ch.world.LocModule;

/**
 * Created by Matej on 1.10.2016.
 */
public class Modules {
    public static final LocModule moduleIntro = new ModuleIntro();
    public static final LocModule moduleParty = new ModuleParty();

    public static void load(CModules modules){
        Log.println("MODULES loaded");

        modules.setModulesSize(2);
        modules.register(moduleIntro);
        modules.register(moduleParty);
    }
}
