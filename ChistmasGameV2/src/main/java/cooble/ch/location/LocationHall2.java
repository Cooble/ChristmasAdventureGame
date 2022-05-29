package cooble.ch.location;

import cooble.ch.core.Game;
import cooble.ch.entity.Helper;
import cooble.ch.entity.InteligentMover;
import cooble.ch.entity.Mover;
import cooble.ch.entity.Position;
import cooble.ch.event.DialogEvent;
import cooble.ch.event.Event;
import cooble.ch.event.LocationLoadEvent;
import cooble.ch.event.SpeakEvent;
import cooble.ch.graphics.Renderer;
import cooble.ch.inventory.item.ItemStack;
import cooble.ch.inventory.stuff.Stuff;
import cooble.ch.item.Items;
import cooble.ch.music.MPlayer2;
import cooble.ch.world.IAction;
import cooble.ch.world.Location;
import cooble.ch.world.NBT;
import org.newdawn.slick.Color;

/**
 * Created by Matej on 21.4.2017.
 */
public class LocationHall2 extends Location {

    public LocationHall2() {
        super("hall2");
    }

    private boolean bathroomUnlocked;
    private boolean canClick;
    private Helper washy;

    @Override
    public void onStart() {
        super.onStart();
        if (!bathroomUnlocked && Game.getWorld().getModule().getNBT().getBoolean("waterOn"))
            MPlayer2.playSound("shower", 0.2);
        canClick = true;
    }

    @Override
    public void onStartRendering(Renderer renderer) {
        if (bathroomUnlocked) {
            renderer.registerBitmapProvider(washy.getBitmapStack());
            washy.setPos(163, 122);
            washy.setRight(false);
            actionRectangleManager.register(washy.getActionRectangle());
        }
        super.onStartRendering(renderer);

    }

    @Override
    public void onStop() {
        super.onStop();
        MPlayer2.stopSound("shower");
    }

    @Override
    public void loadTextures() {

        washy = new Helper("washy");
        washy.loadTextures();
        washy.setPos(80, 150);
        registerTickable(washy);
        Stuff door = getStuffByID("bathroom_door");
        door.setAction(new IAction() {
            @Override
            public boolean onClicked(int x, int y, boolean right_button, boolean released, ItemStack item) {
                if (released || !canClick)
                    return true;
                if (Game.getWorld().getModule().getNBT().getBoolean("waterOn")) {
                    if (!bathroomUnlocked) {
                        bathroomUnlocked = true;
                        canClick = false;
                        Game.core.EVENT_BUS.addDelayedEvent(Game.core.TARGET_TPS * 2, new Event() {
                            @Override
                            public void dispatchEvent() {
                                MPlayer2.stopSound("shower");
                                SpeakEvent speakEvent = new SpeakEvent("entity.washy.comment.hadWater");
                                speakEvent.setColor(Color.orange);
                                Game.core.EVENT_BUS.addDelayedEvent((int) (Game.core.TARGET_TPS * 0.5), speakEvent);
                                speakEvent.setOnEnd(() -> {
                                    canClick = true;
                                    Mover mover = null;
                                    MPlayer2.playSound("door");
                                    Game.renderer.registerBitmapProvider(washy.getBitmapStack());

                                    //refresh to joe be able to draw onto washy
                                    Game.renderer.removeBitmapProvider(Game.getWorld().getUniCreature().getBitmapStack());
                                    Game.renderer.registerBitmapProvider(Game.getWorld().getUniCreature().getBitmapStack());
                                    mover = new Mover(washy, new Position(163, 122), 0.5, new Runnable() {
                                        @Override
                                        public void run() {
                                            SpeakEvent event = new SpeakEvent("entity.washy.canGo");
                                            event.setTalkable(washy);
                                            event.setColor(Color.orange);
                                            Game.core.EVENT_BUS.addEvent(event);
                                            washy.setRight(false);
                                            actionRectangleManager.register(washy.getActionRectangle());
                                        }
                                    });
                                    registerTickable(mover);

                                });
                            }
                        });
                    } else {
                        Mover mover = new Mover(new Position(80, 150), new Runnable() {
                            @Override
                            public void run() {
                                MPlayer2.playSound("door");
                                LocationLoadEvent event = new LocationLoadEvent("bathroom");
                                event.setJoesLocation(27, 153);
                                event.setJoeRightFacing(true);
                                Game.core.EVENT_BUS.addEvent(event);
                            }
                        });
                        registerTickable(mover);

                    }
                } else
                    registerTickable(new InteligentMover(new Position(95, 139), getBoolMap(), () -> {
                        if (item != null && item.ITEM.ID == Items.itemKey.ID) {
                            Game.core.EVENT_BUS.addEvent(new SpeakEvent("entity.joe.nofit"));
                            return;
                        }
                        MPlayer2.playSound("locked");
                        Game.input.muteMouseInput(true);
                        MPlayer2.playSound("knock", 1, 60);
                        Game.core.EVENT_BUS.addEvent(() -> Game.input.muteInput(false));
                        DialogEvent event = new DialogEvent("washyNotOpen");
                        event.registerTalkable(Game.getWorld().getUniCreature(), "joe", Color.green);
                        event.registerTalkable(isTalking -> {
                        }, "washy", Color.cyan);
                        Game.core.EVENT_BUS.addDelayedEvent(Game.core.TARGET_TPS, event);

                    }));
                return true;
            }
        });
        washy.setAction(new IAction() {
            @Override
            public boolean onClicked(int x, int y, boolean right_button, boolean released, ItemStack item) {
                if (released)
                    return true;
                SpeakEvent event = new SpeakEvent("entity.washy.canGo");
                event.setColor(Color.orange);
                event.setTalkable(washy);
                Game.core.EVENT_BUS.addEvent(event);
                return true;
            }
        });
    }

    @Override
    public void writeToNBT(NBT nbt) {
        super.writeToNBT(nbt);
        nbt.putBoolean("bathroomUnlocked", bathroomUnlocked);
    }

    @Override
    public void readFromNBT(NBT nbt) {
        super.readFromNBT(nbt);
        bathroomUnlocked = nbt.getBoolean("bathroomUnlocked");
    }
}
