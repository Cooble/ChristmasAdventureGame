package cs.cooble.module;

import cs.cooble.logger.Log;
import cs.cooble.world.CModules;
import cs.cooble.world.LocModule;

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
