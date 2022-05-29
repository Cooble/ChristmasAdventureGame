package mc.cooble.entity;

import com.sun.istack.internal.Nullable;
import mc.cooble.core.Game;
import mc.cooble.window.Tickable;


/**
 * Used to move IMoveable to another location in Location
 * Needs to call tick()
 */
public final class Mover implements Tickable {

    private IMovable MOVEABLE;
    private final Position2 movPosition;
    private final Vector2 vector;
    private final Runnable run;
    private int time;
    private final int MAX_TIME;
    private boolean done;

    /**
     * @param runnable      called when object is on movPosition
     * @param moveable      object, which is moved
     * @param finalPosition location to reach
     * @param speed         speed of moving is forbiden to be 0
     */
    public Mover(IMovable moveable, Position finalPosition, double speed, @Nullable Runnable runnable) {
        this.MOVEABLE = moveable;
        this.movPosition = new Position2(moveable.getX(), moveable.getY());//odsazeni protoze joe ma trochu jinou pozici
        run = runnable;
        vector=Vector2.getVectorFromPositions(movPosition,new Position2(finalPosition.X,finalPosition.Y));

        speed=speed==0?1:speed;//speed cannot be 0

        MAX_TIME = (int) Math.ceil(vector.getMagnitude() / speed);

        if(vector.getMagnitude()==0.0){
            done=true;
            MOVEABLE=null;
        }


    }

    public Mover(Position finalPosition, @Nullable Runnable runnable) {
        this(Game.getWorld().getUniCreature(), finalPosition, Game.getWorld().getUniCreature().getSpeed(), runnable);
    }

    private void sync(){
        MOVEABLE.setX((int) movPosition.getPosX());
        MOVEABLE.setY((int) movPosition.getPosY());
    }

    public void tick() {
        if (done) {
            done = false;
            if (run != null) {
                run.run();
                MOVEABLE=null;
            }
            return;
        }
        if (MOVEABLE != null)
            if (move()) {
                MOVEABLE.setIsMoving(false);
                MOVEABLE = null;
                if (run != null)
                    run.run();
            }

    }

    /**
     * @return true if should be removed
     */
    private boolean move() {
        time++;
        MOVEABLE.setIsMoving(true);
        double time=(double)this.time/MAX_TIME;
        movPosition.goWithVector(vector,time);
        sync();
        return (int)Math.floor(time)==1;
    }
}
