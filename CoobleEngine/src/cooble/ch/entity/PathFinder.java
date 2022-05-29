package cooble.ch.entity;

import com.sun.istack.internal.Nullable;
import cooble.ch.graphics.BoolMap;
import cooble.ch.logger.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matej on 5.9.2017.
 * Provides algorithm to get through any maze.
 * Outputs points on which you can get from start to end point
 * Maze is boolmap: true -> possible way
 *                  false -> wall (cannot get through)
 */
public class PathFinder {

    private Position END_POS;
    private Position START_POS;
    private BoolMap map;
    private int[][] waterMap;
    private List<Position> points;

    public PathFinder(Position start_pos, Position end_pos, BoolMap labyrinth) {
        END_POS = end_pos;
        START_POS = start_pos;
        this.map = labyrinth;
        waterMap = new int[labyrinth.getWidth()][labyrinth.getHeight()];
        waterMap[start_pos.X][start_pos.Y] = 1;
        points = new ArrayList<>();

        for (int x = 0; x < waterMap.length; x++) {
            for (int y = 0; y < waterMap[0].length; y++) {
                if (!labyrinth.getBoolean(x, y))
                    waterMap[x][y] = -1;
            }
        }
        arrow=-1;
    }

    /**
     *
     * @return list of points how to get from start to end, or null if it's impossible.
     * (index0 = startPoint, lastIndex = endPoint)
     * After calling this method this instance should be eaten by GC (no proper working after calling this method)
     */
    @Nullable
    public List<Position> solveMaze() {
        while (waterMap[END_POS.X][END_POS.Y] == 0 && wave()) ;
        boolean success = waterMap[END_POS.X][END_POS.Y] != 0;
        if (success) {
            points.add(END_POS);
            latestPos = END_POS;
            currentX = latestPos.X;
            currentY = latestPos.Y;
            while (true) {
                int lastArrow = arrow;
                if (!erasePath()) {
                    if(points.size()!=0){
                        if(!points.get(points.size()-1).equals(START_POS)){
                            points.add(START_POS);
                        }
                    }
                    return points.size() == 0 ? null : optimizePoints(points);
                }else{
                    if(lastArrow!=arrow){
                        addPosition(new Position(oldX,oldY));
                    }
                }
            }
        }
        return null;
    }

    private int arrow;

    private int currentX, currentY;
    private int oldX, oldY;
    private int currentAge = 1;

    private boolean erasePath() {
        oldX = currentX;
        oldY = currentY;
        int currentAge = waterMap[currentX][currentY];
        /* if (pi >= precision) {
            pi = 0;
            addPosition(new Position(currentX, currentY));
        }*/
        if (currentAge == 0)
            return false;

        // erase(currentAge, -1); //not needed
        waterMap[currentX][currentY] = currentAge;


        //diagonal
        if (currentX > 0 && currentY > 0) {//--
            if (currentAge - 2 == waterMap[currentX - 1][currentY - 1]) {
                currentX--;
                currentY--;
                arrow=0;
                return true;
            }
        }
        if (currentX < waterMap.length - 1 && currentY < waterMap[0].length - 1) {//++
            if (currentAge - 2 == waterMap[currentX + 1][currentY + 1]) {
                currentX++;
                currentY++;
                arrow=1;
                return true;
            }
        }
        if (currentX > 0 && currentY < waterMap[0].length - 1) {//-+
            if (currentAge - 2 == waterMap[currentX - 1][currentY + 1]) {
                currentX--;
                currentY++;
                arrow=2;
                return true;
            }
        }
        if (currentX < waterMap.length - 1 && currentY > 0) {//+-
            if (currentAge - 2 == waterMap[currentX + 1][currentY - 1]) {
                currentX++;
                currentY--;
                arrow=3;
                return true;
            }
        }

        //ortho
        if (currentX > 0) {
            if (currentAge - 1 == waterMap[currentX - 1][currentY]) {
                currentX--;
                arrow=4;
                return true;
            }
        }
        if (currentX < waterMap.length - 1) {
            if (currentAge - 1 == waterMap[currentX + 1][currentY]) {
                currentX++;
                arrow=5;
                return true;
            }
        }
        if (currentY > 0) {
            if (currentAge - 1 == waterMap[currentX][currentY - 1]) {
                currentY--;
                arrow=6;
                return true;
            }
        }
        if (currentY < waterMap[0].length - 1) {
            if (currentAge - 1 == waterMap[currentX][currentY + 1]) {
                currentY++;
                arrow=7;
                return true;
            }
        }
        return false;
    }
    Position latestPos;

    private void addPosition(Position position) {
        points.add(position);
        latestPos = position;
    }

    /**
     * @return if can wave continue
     */
    private boolean wave() {
        int oldAge = currentAge;
        currentAge++;
        boolean canContinue = false;
        for (int x = 0; x < waterMap.length; x++) {
            for (int y = 0; y < waterMap[0].length; y++) {
                int age = waterMap[x][y];
                if (age == oldAge) {
                    if (x > 0) {
                        if (waterMap[x - 1][y] == 0) {
                            waterMap[x - 1][y] = currentAge;
                            canContinue = true;
                        }
                    }
                    if (x < waterMap.length - 1) {
                        if (waterMap[x + 1][y] == 0) {
                            waterMap[x + 1][y] = currentAge;
                            canContinue = true;
                        }
                    }
                    if (y > 0) {
                        if (waterMap[x][y - 1] == 0) {
                            waterMap[x][y - 1] = currentAge;
                            canContinue = true;
                        }
                    }
                    if (y < waterMap[0].length - 1) {
                        if (waterMap[x][y + 1] == 0) {
                            waterMap[x][y + 1] = currentAge;
                            canContinue = true;
                        }
                    }
                }
            }
        }
        return canContinue;
    }

    public static boolean isWallBetween(Position p1, Position p2,BoolMap map) {
        Position min = new Position(Math.min(p1.X, p2.X), Math.min(p1.Y, p2.Y));
        Position max = new Position(Math.max(p1.X, p2.X), Math.max(p1.Y, p2.Y));
        int deltaX = max.X - min.X;
        int deltaY = max.Y - min.Y;
        if (deltaX > deltaY) {
            for (int x = 0; x < deltaX; x++) {
                if (!map.getBoolean(x + min.X, min.Y + (int) (((double) x / deltaX) * deltaY))) {
                    return true;
                }
            }
        } else {
            for (int y = 0; y < deltaY; y++) {
                if (!map.getBoolean(min.X + (int) (((double) y / deltaY) * deltaX), min.Y + y)) {
                    return true;
                }
            }
        }
        return false;
    }

    private List<Position> optimizePoints(List<Position> list){

        setNullToBadPoints(list);
        List<Position> semi = new ArrayList<>();
        for (Position aList : list) {
            if (aList != null)
                semi.add(aList);
        }
        setNullToBadPoints(semi);

        List<Position> out = new ArrayList<>();
        for (int i = semi.size() - 1; i >= 0; i--) {
            if(semi.get(i)!=null)
                out.add(semi.get(i));
        }
        return out;
    }
    private void setNullToBadPoints(List<Position> list){
        int index = 2;
        int oldIndex=0;
        while (index<list.size()){
            if(!isWallBetween(list.get(oldIndex),list.get(index),map)){
                list.set(index-1,null);
                oldIndex=index;
                index=index+2;
            }
            else{
                index++;
                oldIndex++;
            }
        }
    }

    public void printMap() {
        for (int[] aWaterMap : waterMap) {
            StringBuilder builder = new StringBuilder(waterMap[0].length);
            for (int y = 0; y < waterMap[0].length; y++) {
                String s = aWaterMap[y] + "";
                if (s.length() == 1) {
                    s = " " + s;
                }
                builder.append(s);
            }
            Log.println("[" + builder.toString() + "]");
        }
    }

}
