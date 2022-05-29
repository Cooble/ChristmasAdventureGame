package mc.cooble.event;


import mc.cooble.core.Game;
import mc.cooble.logger.Log;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;

/**
 * Created by Matej on 19.7.2016.
 */
public final class GameExitEvent implements Event {

    String[] error = null;
    private  boolean restart=false;
    private boolean clear;

    public GameExitEvent(){}

    public void setError(String[] error) {
        this.error = error;
    }

    public void setRestart(boolean restart) {
        this.restart = restart;
    }

    public void setClear(boolean save) {
        this.clear = save;
    }

    @Override
    public void dispatchEvent() {
        if (error == null) {
            if(clear){
                Log.println("Exit with clear game data");
                Game.saver.clearGameFolder();
            }else {
                if (!Game.noSave) {
                    if (Game.paused) {
                        Game.lastMID = Game.pauseMID;
                        Game.lastLOCID = Game.pauseLOCID;
                    }
                    Log.println("Saving Game");
                    Game.saveGame();
                } else {
                    Log.println("Exiting without saving", Log.LogType.WARN);
                }
            }
        } else {
            Game.error = error;
            Game.getWorld().getLocationManager().setLocation("exit");
        }
       // Game.saver.removeTemp();
        Game.core.stop();
        if(restart){
            Log.println("Restarting");
            try {
                restartApplication();
               // restartApplication2(new String[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.exit(0);
    }
    private void restartApplication() throws Exception {
        final String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
        final File currentJar = new File(GameExitEvent.class.getProtectionDomain().getCodeSource().getLocation().toURI());

        /* is it a jar file? */
        if (!currentJar.getName().endsWith(".jar"))
            return;

        /* Build command: java -jar application.jar */
        final ArrayList<String> command = new ArrayList<String>();
        command.add(javaBin);
        command.add("-jar");
        command.add(currentJar.getPath());
        command.add(" enableIT=false ");

        final ProcessBuilder builder = new ProcessBuilder(command);
        builder.start();
    }
    private void restartApplication2(String[] args) throws Exception {
        StringBuilder cmd = new StringBuilder();
        cmd.append(System.getProperty("java.home") + File.separator + "bin" + File.separator + "java ");

        for (String jvmArg : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
            cmd.append(jvmArg + " ");
        }
        cmd.append("-cp ").append(ManagementFactory.getRuntimeMXBean().getClassPath()).append(" ");
        cmd.append(GameExitEvent.class.getName()).append(" ");
        for (String arg : args) {
            cmd.append(arg).append(" ");
        }
        Runtime.getRuntime().exec(cmd.toString());
    }

}
