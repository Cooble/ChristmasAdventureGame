package mc.cooble.world;


/**
 * Created by Matej on 4.8.2016.
 */
public final class CModules {

    protected CModules(){}

    public final LocModule blankModule=new LocModule("blank_module") {
        @Override
        public Location[] load() {
            return new Location[]{new Location("blank_loc") {
                @Override
                public void loadTextures() {

                }
            }};
        }
    };

    private LocModule[] modules;
    private int currentIndex;

    public void register(LocModule locModule){
        modules[currentIndex]=locModule;
        currentIndex++;
    }
    public LocModule getModule(String MID){
        for(LocModule module:modules){
            if(module!=null) {
                if (module.MID.equals(MID))
                    return module;
            }
            else return null;
            }
        return null;
    }

    public void setModulesSize(int modulesSize) {
        modules=new LocModule[modulesSize+1];
        register(blankModule);
    }
}
