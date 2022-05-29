package mc.cooble.event;

import mc.cooble.core.Game;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matej on 20.7.2017.
 */
public class FilmEvent implements Event {

    private int currentSceneIndex;
    private List<Scene> items = new ArrayList<>();


    public void addItem(Scene item) {
        items.add(item);
    }


    @Override
    public void dispatchEvent() {
        if (currentSceneIndex >= items.size())
            return;
        int nextTime = items.get(currentSceneIndex).runScene(this);
        currentSceneIndex++;
        Game.core.EVENT_BUS.addDelayedEvent(nextTime, this);


    }

    /**
     * Has all camera script -> where should camera be in every point in time
     */
    public class Scene {
        ArrayList<double[]> pentets = new ArrayList<>();//x y scale timeOfGoing timeOfWaiting

        public void addCameraPoint(int posX, int posY, double scale, int timeOfGoing, int timeOfWaiting) {
            pentets.add(new double[]{posX, posY, scale, timeOfGoing, timeOfWaiting});
        }

        private int registerCameraMovement() {
            int time = 0;
            for (double[] pentet : pentets) {
                Game.core.EVENT_BUS.addDelayedEvent(time, new Event() {
                    @Override
                    public void dispatchEvent() {
                        Game.renderer.getCameraMan().goTo((int) pentet[0], (int) pentet[1], pentet[2]);
                        Game.renderer.getCameraMan().setTime((int) pentet[3]);
                    }
                });
                time += (int) (pentet[3] + pentet[4]);
            }
            return time;
        }

        /**
         * @param event
         * @return time for which scene will run for
         */
        int runScene(FilmEvent event) {
            return registerCameraMovement();
        }
    }

}
