package mc.cooble.timer;

import javafx.application.Platform;

import java.util.ArrayList;

/**
 * Created by Matej on 18.5.2017.
 */
public class Timer implements Runnable {
    ArrayList<Runnable> runnables;
    private int delay;
    private boolean isWorking;

    public Timer() {
        runnables = new ArrayList<>();
        setTPS(10);
    }

    /**
     * takes Thread and never give back
     */
    public void start() {
        isWorking = true;
        Thread thread = new Thread(this);
        thread.start();
    }

    public void setTPS(int tps) {
        delay = 1000 / tps;
    }

    public void stop() {
        isWorking = false;
    }

    public boolean isWorking() {
        return isWorking;
    }

    @Override
    public void run() {
        while (isWorking) {
            Platform.runLater(()-> {
                for (Runnable runnable : runnables) {
                    runnable.run();
                }
            });
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public void addRunnable(Runnable r) {
        runnables.add(r);
    }
}
