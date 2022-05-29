package mc.cooble.event;


import com.sun.istack.internal.Nullable;
import mc.cooble.core.Game;

/**
 * Created by Matej on 12.12.2015.
 */
public final class LocationLoadEvent implements Event {
    /**
     * Loads location and unloads old location and module
     */
    private String LOCID;
    private final String MID;
    private int joePosX = Integer.MAX_VALUE, joePosY = Integer.MAX_VALUE;
    private Boolean joeRightFacing;
    private boolean reloadLocation;

    private boolean removeSubLoc;

    public LocationLoadEvent(@Nullable String LOCID, @Nullable String MID) {
        this.LOCID = LOCID;
        this.MID = MID;
    }

    public LocationLoadEvent(String LOCID) {

        if (LOCID != null && LOCID.contains(":")) {
            String[] ss = LOCID.split(":");
            this.LOCID = ss[0];
            this.MID = ss[1];
        } else {
            this.LOCID = LOCID;
            MID = null;
        }
    }

    public void setRemoveSubLoc(boolean removeSubLoc) {
        this.removeSubLoc = removeSubLoc;
    }

    public void setJoeRightFacing(boolean joeRightFacing) {
        this.joeRightFacing = joeRightFacing;
    }

    public void setReloadLocation(boolean reloadLocation) {
        this.reloadLocation = reloadLocation;
    }

    public void setJoesLocation(int posX, int posY) {
        this.joePosX = posX;
        this.joePosY = posY;
    }

    @Override
    public void dispatchEvent() {
        Game.renderer.setEnableNormalDrawing(true);
        if (MID != null) {
            Game.setLoadingScreen(true);
            Game.core.EVENT_BUS.addEvent(this::gogogog);
        } else gogogog();


    }

    private void gogogog() {
        if (removeSubLoc) {
            Game.getWorld().getLocationManager().removeSubLocationIfExists();
        } else {
            if (MID != null) {
                boolean sameMIDs = Game.getWorld().getModule() != null && Game.getWorld().getModule().MID.equals(MID);
                if (!sameMIDs)
                    Game.getWorld().setModule(Game.getWorld().modules().getModule(MID));
            }
            if (LOCID != null) {
                boolean crash = !Game.getWorld().getLocationManager().setLocation(LOCID);
                if (crash) {
                    new Exception("[LocationLoadEvent] cannot find location with ID: " + LOCID + " in module " + Game.getWorld().getModule().MID).printStackTrace();
                }
                if (joePosX != Integer.MAX_VALUE)
                    Game.getWorld().getUniCreature().setPos(joePosX, joePosY);
                if (joeRightFacing != null) {
                    Game.getWorld().getUniCreature().setRight(joeRightFacing);
                }
            }
        }
        Game.setLoadingScreen(false);
        Game.getWorld().inventory().setOffImmediately();
        Game.dialog.setText(null);
    }
}
