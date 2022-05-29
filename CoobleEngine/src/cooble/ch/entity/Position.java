package cooble.ch.entity;

/**
 * Created by Matej on 13.12.2015.
 */
public class Position {
    public final int X;
    public final int Y;

    public Position(int x, int y) {
        this.X = x;
        this.Y = y;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Position && ((Position) obj).X == X && ((Position) obj).Y == Y;
    }

    public boolean equalsInRadius(Position obj, int radius) {
        return this.X + radius > obj.X && this.X - radius < obj.X && this.Y + radius > obj.Y && this.Y - radius < obj.Y;

    }

    /**
     * big computational work!!!!!!
     * @param position
     * @return
     */
    public int getDifference(Position position){
        int vectorX,vectorY;
        vectorX=Math.abs(X-position.X);
        vectorY=Math.abs(Y-position.Y);
        return (int) Math.sqrt(vectorX*vectorX+vectorY*vectorY);
    }

    @Override
    public String toString() {
        return "[x=" + X + " y=" + Y+"]";
    }
}
