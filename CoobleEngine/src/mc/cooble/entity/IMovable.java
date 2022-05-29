package mc.cooble.entity;

/**
 * Created by Matej on 13.12.2015.
 */
public interface IMovable {
    int getX();

    int getY();

    void setX(int x);

    void setY(int y);

    default Position getLocation() {
        return new Position(getX(), getY());
    }

    void setIsMoving(boolean isMoving);
}
