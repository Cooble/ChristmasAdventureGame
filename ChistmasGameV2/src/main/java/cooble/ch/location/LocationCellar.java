package cooble.ch.location;

import com.sun.istack.internal.Nullable;
import cooble.ch.core.Game;
import cooble.ch.event.Event;
import cooble.ch.event.SpeakEvent;
import cooble.ch.graphics.Bitmap;
import cooble.ch.graphics.BitmapProvider;
import cooble.ch.graphics.BitmapStack;
import cooble.ch.inventory.item.ItemStack;
import cooble.ch.inventory.stuff.Stuff;
import cooble.ch.inventory.stuff.StuffToCome;
import cooble.ch.item.Items;
import cooble.ch.music.MPlayer2;
import cooble.ch.world.IAction;
import cooble.ch.world.Location;
import cooble.ch.world.NBT;
import org.newdawn.slick.Color;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Matej on 9.10.2016.
 */
public class LocationCellar extends Location {
    private int WIDTH = 144;
    private int HEIGHT = 72;
    private final int SIZE = 18;
    private StuffPipe[][] pipes;
    private int currentPipeID;
    private Bitmap[] bitmaps;
    private Stuff waterHeater;
    private Bitmap valveSrc;

    private int OFFSET_X = 26, OFFSET_Y = 16;

    private Random random = new Random();

    public LocationCellar() {
        super("cellar");
        pipes = new StuffPipe[/*WIDTH / SIZE*/4][/*HEIGHT / SIZE*/8];
    }

    @Override
    public void loadTextures() {
        loadBitmaps();
        loadArray();
        Valve stuff = new Valve("valve");
        addXMLSubstrate(stuff,"valve");
        valveSrc=Bitmap.get("item/valve");
        stuff.setCurrentBitmap(valveSrc);
        addStuff(stuff);

        ((StuffToCome)getStuffByID("dark_fan")).setItem(new ItemStack(Items.itemFan));

        waterHeater=getStuffByID("water_heater");
        waterHeater.setAction(new IAction() {
            @Override
            public boolean onClicked(int x, int y, boolean right_button, boolean released, ItemStack item) {
                if(!IAction.onlyPressed(right_button,released,item))
                    return true;
                BitmapStack stack = (BitmapStack) waterHeater.getBitmapProvider();
                stack.setCurrentIndex(1);
                Game.getWorld().getModule().getNBT().putBoolean("waterHeatOn",true);
                MPlayer2.playSound("cvak_0");
                return true;
            }
        });
        ((BitmapStack)waterHeater.getBitmapProvider()).setCurrentIndex(Game.getWorld().getModule().getNBT().getBoolean("waterHeatOn")?1:0);
    }

    @Override
    public void onStop() {
        super.onStop();
        MPlayer2.stopSound("shower");
    }
    @Override
    public void onStart() {
        super.onStart();
    }

    private void loadBitmaps() {
        bitmaps = new Bitmap[PipeType.values().length];
        for (int i = 0; i < PipeType.values().length; i++) {
            PipeType pipeType = PipeType.values()[i];
            Bitmap b = null;
            switch (pipeType) {
                case UP_DOWN:
                    b = Bitmap.get("item/pipe_line");
                    break;
                case LEFT_RIGHT:
                    b = Bitmap.get("item/pipe_line").rotateCW(90);
                    break;
                case LEFT_UP:
                    b = Bitmap.get("item/pipe_curved");
                    break;
                case UP_RIGHT:
                    b = Bitmap.get("item/pipe_curved").rotateCW(90);
                    break;
                case RIGHT_DOWN:
                    b = Bitmap.get("item/pipe_curved").rotateCW(180);
                    break;
                case DOWN_LEFT:
                    b = Bitmap.get("item/pipe_curved").rotateCW(270);
                    break;

            }
            bitmaps[i] = b;
        }
    }

    private Bitmap getBitmap(StuffPipe arroww) {
        Bitmap out = bitmaps[arroww.getPipe().ordinal()].copy();
        out.setOffset(arroww.getActionListener().getRectangle().x, arroww.getActionListener().getRectangle().y);
        return out;
    }

    @Nullable
    private StuffPipe getPipeInDirection(StuffPipe src, Arrow a) {
        String name = src.getOnlyName();
        int index = Integer.parseInt(name.substring(name.indexOf('_')+1));

        int yPos = index / pipes[0].length;
        int xPos = index % pipes[0].length;
        switch (a) {
            case UP:
                if (yPos == 0)
                    return null;
                yPos--;
                break;
            case DOWN:
                if (yPos == pipes.length-1)
                    return null;
                yPos++;
                break;
            case LEFT:
                if (xPos == 0)
                    return null;
                xPos--;
                break;
            case RIGHT:
                if (xPos == pipes[0].length-1)
                    return null;
                xPos++;
                break;
        }
        return pipes[yPos][xPos];
    }

    private boolean findDefaultPath(){
        return findPath(pipes[0][0],Arrow.UP,pipes[pipes.length-1][pipes[pipes.length-1].length-1],Arrow.DOWN);
    }

    private boolean findPath(StuffPipe start, Arrow srcArrow,StuffPipe finish,Arrow finishArrow) {
        if(!start.getPipe().contains(srcArrow)||!finish.getPipe().contains(finishArrow))
            return false;
        StuffPipe currentPipe = start;
        StuffPipe lastPipe=null;
        while (!currentPipe.equals(finish)){
            ArrayList<StuffPipe> connected = getConnectedPipes(currentPipe,lastPipe);
            if(connected.size()>0){
                lastPipe=currentPipe;
                currentPipe=connected.get(0);
            }
            else if(connected.size()==0)
                break;
        }
        return currentPipe.equals(finish);
    }

    private ArrayList<StuffPipe> getConnectedPipes(StuffPipe src, @Nullable StuffPipe excluded) {
        ArrayList<StuffPipe> possibleSrcConections = getPipesWhichSrcIsConnected(src);
        ArrayList<StuffPipe> out = new ArrayList<>();

        for(StuffPipe pipe:possibleSrcConections){
            if(getPipesWhichSrcIsConnected(pipe).contains(src)){
                if(excluded!=null){
                    if(!pipe.equals(excluded))
                        out.add(pipe);
                }else out.add(pipe);
            }else {int i = 1;}
        }
        return out;
    }

    private ArrayList<StuffPipe> getPipesWhichSrcIsConnected(StuffPipe src) {
        ArrayList<StuffPipe> out = new ArrayList<>();
        Arrow[] arrows = Arrow.values();
        for (Arrow arrow : arrows) {
            if (!src.getPipe().contains(arrow))
                continue;
            StuffPipe s = getPipeInDirection(src, arrow);
            if (s != null)
                out.add(s);
        }
        return out;
    }



    private StuffPipe line() {
        StuffPipe stuff = new StuffPipe("pipe_" + currentPipeID,random.nextBoolean() ? PipeType.UP_DOWN : PipeType.LEFT_RIGHT);
        currentPipeID++;
        return stuff;
    }

    private StuffPipe curved() {
        int r = random.nextInt(4);
        PipeType pipeType=null;
        switch (r) {
            case 0:
                pipeType=PipeType.DOWN_LEFT;
                break;
            case 1:
                pipeType=PipeType.LEFT_UP;
                break;
            case 2:
                pipeType=PipeType.UP_RIGHT;
                break;
            case 3:
                pipeType=PipeType.RIGHT_DOWN;
                break;
        }
        StuffPipe stuff = new StuffPipe("pipe_" + currentPipeID,pipeType);
        currentPipeID++;
        return stuff;

    }

    private void loadArray() {
        fillLine(pipes, "clcccclc", 0);
        fillLine(pipes, "ccccclcc", 1);
        fillLine(pipes, "cccccccc", 2);
        fillLine(pipes, "clclclcl", 3);
    }

    private void fillLine(StuffPipe[][] array, String type, int lineIndex) {
        for (int i = 0; i < array[lineIndex].length; i++) {
            array[lineIndex][i] = type.charAt(i) == 'c' ? curved() : line();
            array[lineIndex][i].setRectangle(OFFSET_X + i * SIZE, OFFSET_Y + lineIndex * SIZE, SIZE, SIZE);
            addStuff(array[lineIndex][i]);
        }
    }

    private class StuffPipe extends Stuff implements BitmapProvider {

        private PipeType pipe;
        private Bitmap currentBitmap;

        public StuffPipe(String name,PipeType pipee) {
            super(name);
            this.pipe = pipee;
            StuffPipe thisis = this;
            currentBitmap = getBitmap(thisis);

            IAction action = new IAction() {
                @Override
                public boolean onClicked(int x, int y, boolean right_button, boolean released, ItemStack item) {
                    if (released)
                        return true;
                    pipe = getNextArrow(pipe);
                    currentBitmap = getBitmap(thisis);
                    MPlayer2.playSound("metal",0.2);
                    return false;
                }
            };
            setAction(action);
            setLocationTexture(this);
        }


        @Override
        public void readFromNBT(NBT nbt) {
            super.readFromNBT(nbt);
            if (nbt.getString("pipee")!=null&&PipeType.valueOf(nbt.getString("pipee")) != null) {
                pipe = PipeType.valueOf(nbt.getString("pipee"));
                currentBitmap=getBitmap(this);
            }

        }

        @Override
        public void writeToNBT(NBT nbt) {
            super.writeToNBT(nbt);
            nbt.putString("pipee", pipe.name());
        }

        public PipeType getPipe() {
            return pipe;
        }

        @Override
        public Bitmap getCurrentBitmap() {
            return currentBitmap;
        }

        @Override
        public int[] getOffset() {
            return new int[]{rectangle.getRectangle().x, rectangle.getRectangle().y};
        }
    }
    private class Valve extends Stuff implements BitmapProvider {

        private Bitmap currentBitmap;
        private int turnEventID=-1;
        private float angle;

        public Valve(String name) {
            super(name);
            IAction action = new IAction() {
                @Override
                public boolean onClicked(int x, int y, boolean right_button, boolean released, ItemStack item) {
                    if(!IAction.onlyPressed(right_button,released,item))
                        return true;
                    if (turnEventID!=-1)
                        return true;
                    if(!findDefaultPath()){
                        Game.core.EVENT_BUS.addEvent(new SpeakEvent("entity.joe.drown"));
                        return true;
                    }
                    MPlayer2.playSound("water_valve");
                    turnEventID=Game.core.EVENT_BUS.addRepeatingEvent(5, new Event() {
                        @Override
                        public void dispatchEvent() {
                            angle+=10;
                            currentBitmap=valveSrc.rotateCW(angle);
                            if(angle==180){
                                boolean success=Game.getWorld().getModule().getNBT().getBoolean("waterHeatOn");
                                if(!Game.getWorld().getModule().getNBT().getBoolean("waterOn")){
                                    Game.getWorld().getModule().getNBT().putBoolean("waterOn",success);
                                    if(success){//fresh
                                        MPlayer2.playSound("shower",0.2);
                                        SpeakEvent event = new SpeakEvent("washy","entity.washy.comment.hasWater");
                                        event.setColor(Color.orange);
                                        Game.core.EVENT_BUS.addDelayedEvent((int) (Game.core.TARGET_TPS*0.5),event);
                                    }else{
                                        SpeakEvent event  =new SpeakEvent("entity.washy.cold");
                                        event.setColor(Color.orange);
                                        Game.core.EVENT_BUS.addEvent(event);
                                    }
                                }
                                Game.core.EVENT_BUS.unregisterEvent(turnEventID);
                                turnEventID=-1;
                                angle=0;
                            }
                        }
                    });
                    return false;
                }
            };
            setAction(action);
            setLocationTexture(this);
        }

        @Override
        public Bitmap getCurrentBitmap() {
            return currentBitmap;
        }

        @Override
        public int[] getOffset() {
            return new int[]{getActionListener().getRectangle().x,getActionListener().getRectangle().y};
        }

        public void setCurrentBitmap(Bitmap currentBitmap) {
            this.currentBitmap = currentBitmap;
        }
    }



    private enum PipeType {
        UP_DOWN(Arrow.UP, Arrow.DOWN), LEFT_RIGHT(Arrow.LEFT, Arrow.RIGHT), UP_RIGHT(Arrow.UP, Arrow.RIGHT), RIGHT_DOWN(Arrow.RIGHT, Arrow.DOWN), DOWN_LEFT(Arrow.DOWN, Arrow.LEFT), LEFT_UP(Arrow.LEFT, Arrow.UP);

        Arrow[] arrows;

        PipeType(Arrow... arrows) {
            this.arrows = arrows;
        }

        public boolean contains(Arrow arrow) {
            for (Arrow arrow1 : arrows) {
                if (arrow1.equals(arrow)) {
                    return true;
                }
            }
            return false;
        }


    }

    private enum Arrow {
        UP, DOWN, LEFT, RIGHT

    }

    private PipeType getNextArrow(PipeType current) {
        switch (current) {
            case UP_DOWN:
                return PipeType.LEFT_RIGHT;
            case LEFT_RIGHT:
                return PipeType.UP_DOWN;

            case UP_RIGHT:
                return PipeType.RIGHT_DOWN;
            case RIGHT_DOWN:
                return PipeType.DOWN_LEFT;
            case DOWN_LEFT:
                return PipeType.LEFT_UP;
            case LEFT_UP:
                return PipeType.UP_RIGHT;
        }
        return null;
    }

}
