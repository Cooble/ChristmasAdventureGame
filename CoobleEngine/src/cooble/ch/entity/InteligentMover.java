package cooble.ch.entity;

import com.sun.istack.internal.Nullable;
import cooble.ch.core.Game;
import cooble.ch.graphics.BoolMap;
import cooble.ch.window.Tickable;

import java.awt.*;
import java.util.List;


/**
 * Used to move IMoveable to another position in Location
 * Needs to call tick()
 * not sure if this is not just poor experiment!!!!! No It's definitely not! It's great! :D
 * this class moves in time with moveable on predefined way which was solved using great and famous PathFinder
 */
public final class InteligentMover implements Tickable {

    private boolean done;
    private Mover currentMover;
    private int index;
    private List<Position> positions;
    private IMovable moveable;
    private IMovable movableOriginal;
    private final Runnable onEnd;
    private double speed;

    private Position superEnd;
    private Position superStart;

    /**
     * @param runnable      called when object is on movPosition
     * @param moveable      object, which is moved
     * @param finalPosition position to reach
     * @param speed         must not be 0!
     */
    public InteligentMover(IMovable moveable, Position finalPosition, BoolMap map, double speed, @Nullable Runnable runnable) {
        this.moveable = new IMovable() {
            @Override
            public int getX() {
                return moveable.getX();
            }

            @Override
            public int getY() {
                return moveable.getY();
            }

            @Override
            public void setX(int x) {
                moveable.setX(x);
            }

            @Override
            public void setY(int y) {
                moveable.setY(y);
            }

            @Override
            public void setIsMoving(boolean isMoving) {
            }
        };
        this.movableOriginal = moveable;
        this.onEnd = runnable;
        this.speed = speed;
        if(!new Rectangle(0,0,map.getWidth(),map.getHeight()).contains(finalPosition.X,finalPosition.Y)){//clicked off the map
            int x = finalPosition.X;
            int y = finalPosition.Y;
            if(x>map.getWidth()-1){
                x=map.getWidth()-1;
            }
            else if(x<0){
                x=0;
            }
            if(y>map.getHeight()-1){
                y=map.getHeight()-1;
            }
            else if(y<0){
                y=0;
            }
            superEnd=finalPosition;
            finalPosition=new Position(x,y);
        }
        Position startPos = moveable.getLocation();
        if(!new Rectangle(0,0,map.getWidth(),map.getHeight()).contains(startPos.X,startPos.Y)){//clicked off the map
            int x = startPos.X;
            int y = startPos.Y;
            if(x>map.getWidth()-1){
                x=map.getWidth()-1;
            }
            else if(x<0){
                x=0;
            }
            if(y>map.getHeight()-1){
                y=map.getHeight()-1;
            }
            else if(y<0){
                y=0;
            }
            superStart=new Position(x,y);
        }

        if(superStart==null) {
            boolean needPathFinder = PathFinder.isWallBetween(startPos, finalPosition, map);
            if (needPathFinder) {
                PathFinder finder = new PathFinder(startPos, finalPosition, map);
                positions = finder.solveMaze();
                if (positions != null) {
                    index = 0;
                    movableOriginal.setIsMoving(true);
                    createMover();
                    return;
                }
            }
            currentMover = new Mover(moveable, finalPosition, speed, runnable);
        }else{
            boolean needPathFinder = PathFinder.isWallBetween(superStart, finalPosition, map);
            if (needPathFinder) {
                final Position finalLoc = finalPosition;
                currentMover = new Mover(moveable, superStart, speed, new Runnable() {
                    @Override
                    public void run() {
                        PathFinder finder = new PathFinder(superStart, finalLoc, map);
                        positions = finder.solveMaze();
                        if (positions != null) {
                            index = 0;
                            movableOriginal.setIsMoving(true);
                            createMover();
                        }
                    }
                });
                return;
            }
            currentMover = new Mover(moveable, finalPosition, speed, runnable);
        }

    }

    public InteligentMover(Position finalPosition, BoolMap map, @Nullable Runnable runnable) {
        this(Game.getWorld().getUniCreature(), finalPosition, map, Game.getWorld().getUniCreature().getSpeed(), runnable);
    }


    public void tick() {
        if (done)
            return;
        if (currentMover != null)
            currentMover.tick();
    }

    private void createMover() {
        Position position = positions.get(index);
        Runnable[] rr = new Runnable[1];
        Runnable r = new Runnable() {
            @Override
            public void run() {
                index++;
                if (index < positions.size()) {
                    InteligentMover.this.createMover();
                }else if(superEnd!=null){
                    currentMover=new Mover(moveable,superEnd,speed,rr[0]);
                    superEnd=null;
                } else {
                    if (!done) {
                        done = true;
                        if (onEnd != null)
                            onEnd.run();
                        currentMover = null;
                        movableOriginal.setIsMoving(false);
                    }
                }
            }
        };
        rr[0]=r;
        currentMover = new Mover(moveable, position, speed, r);
    }
}
