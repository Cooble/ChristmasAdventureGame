package cooble.ch.fx;

import cooble.ch.canvas.Bitmap;
import cooble.ch.canvas.BitmapProvider;
import cooble.ch.canvas.Renderer;
import cooble.ch.duck.*;
import cooble.ch.timer.Timer;
import cooble.ch.xml.XML;
import javafx.event.ActionEvent;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static cooble.ch.duck.Loc.SRC_FOLDER;

/**
 * Created by Matej on 18.5.2017.
 */
public class Controller implements Runnable {
    public static boolean ARROWSHOW;
    private Main main;
    private Renderer renderer;
    private Timer timer;
    private DuckManager manager;
    private Loc currentLoc;
    /**
     * bittmap on the first level to render the 0th is joe
     */
    private Bitmap bitmap;
    private Joe joe;

    public static final int WIDTH = 1280;
    public static final int HEIGHT = 720;

    public static final int RATIO = 4;
    private static boolean arMode;



    public Controller(Main main) {
        this.main = main;
        timer = new Timer();
        timer.addRunnable(this);
        load();
    }

    public void load() {
        currentLoc = new Loc("new Loc");
        renderer = new Renderer(currentLoc);
        joe = new Joe();
        joe.setPos(160, 90);
        joe.setEnabled(false);
        bitmap = new Bitmap(1, 1);
        bitmap.setShouldRender(false);
        renderer.addBitmapProvider(new BitmapProvider() {
            @Override
            public Bitmap[] getBufferedImages() {
                return new Bitmap[]{bitmap};
            }

            @Override
            public int getLevel() {
                return 0;
            }
        });
        renderer.addPrimaryProvider(joe);
    }

    /**
     * called after main setup
     */
    public void finishPreparations() {
        main.getCanvas().setOnMousePressed(this::canvasPressed);
        main.getCanvas().setOnMouseDragged(this::canvasDragged);
        main.getCanvas().setOnMouseMoved(this::canvasMoved);
        main.getComScene().setOnKeyPressed(this::canvasKeyPressed);
        manager = new DuckManager(main.getListView(), renderer);
        timer.start();
    }


    private int offsetX, offsetY;
    private int offsetX2, offsetY2;

    private void canvasDragged(MouseEvent mouseEvent) {
        int mouseX = (int) (mouseEvent.getX() - Main.OFFSET_CANVAS);
        int mouseY = (int) (mouseEvent.getY() - Main.OFFSET_CANVAS);
        if (joe.isEnabled()) {
            joe.setPos(quantize((int) mouseX), quantize((int) mouseY));
        } else {
            if (manager.getCurrentDuck() != null) {
                ListViewItem listViewItem = manager.getCurrentDuck();
                ActionRectangleOwner stuff = (ActionRectangleOwner) listViewItem;
                if (isArMode()) {
                    if (mouseEvent.isSecondaryButtonDown())
                        stuff.setActionDimensions(quantize((int) mouseX) - offsetX, quantize((int) mouseY) - offsetY);
                    else stuff.setActionOffset(quantize((int) mouseX), quantize((int) mouseY));
                } else {
                    if (!mouseEvent.isShiftDown()) {
                        stuff.setActionOffset(offsetX2 + quantize((int) mouseX), offsetY2 + quantize((int) mouseY));
                    }
                    stuff.setBitmapOffset(offsetX + quantize(mouseX), offsetY + quantize(mouseY));
                }
            }
        }
    }

    private void canvasKeyPressed(KeyEvent keyEvent) {
        if (joe.isEnabled() && !joe.isFrozen()) {
            switch (keyEvent.getCode()) {
                case UP:
                    joe.setPos(joe.getPosX(), joe.getPosY() - 5);
                    break;
                case DOWN:
                    joe.setPos(joe.getPosX(), joe.getPosY() + 5);
                    break;
                case LEFT:
                    joe.setPos(joe.getPosX() - 5, joe.getPosY());
                    break;
                case RIGHT:
                    joe.setPos(joe.getPosX() + 5, joe.getPosY());
                    break;
            }
        }
    }

    private int lastJoeX;

    private void canvasMoved(MouseEvent mouseEvent) {
        int mouseX = (int) (mouseEvent.getX() - Main.OFFSET_CANVAS);
        int mouseY = (int) (mouseEvent.getY() - Main.OFFSET_CANVAS);

        if (joe.isEnabled() && !joe.isFrozen()) {
            joe.setPos(quantize(mouseX), quantize(mouseY));
            if (Math.abs(mouseX - lastJoeX) > 5) {
                joe.setRight(lastJoeX < mouseX);
                lastJoeX = mouseX;
            }
        }
    }

    private int quantize(int input) {
        return (input / Controller.RATIO);
    }


    private void canvasPressed(MouseEvent mouseEvent) {
        int mouseX = (int) (mouseEvent.getX() - Main.OFFSET_CANVAS);
        int mouseY = (int) (mouseEvent.getY() - Main.OFFSET_CANVAS);

        if (joe.isEnabled()) {
            joe.setFreeze(!joe.isFrozen());
        } else {
            if (manager.getCurrentDuck() != null) {
                ListViewItem listViewItem = manager.getCurrentDuck();
                ActionRectangleOwner stuff = (ActionRectangleOwner) listViewItem;
                if (isArMode()) {
                    stuff.setActionBitmapActive(true);
                    if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                        offsetX = quantize((int) mouseX);
                        offsetY = quantize((int) mouseY);
                        stuff.setActionOffset(offsetX, offsetY);
                        stuff.setActionDimensions(0, 0);
                    } else if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                        offsetX = quantize((int) mouseX);
                        offsetY = quantize((int) mouseY);
                        stuff.setActionOffset(offsetX, offsetY);
                    }
                } else {
                    offsetX = (stuff.getBitmapOffsetX() - quantize((int) mouseX));
                    offsetY = (stuff.getBitmapOffsetY() - quantize((int) mouseY));
                    offsetX2 = (stuff.getX() - quantize((int) mouseX));
                    offsetY2 = (stuff.getY() - quantize((int) mouseY));
                }

            }
        }
    }

    public void onProgramQuited() {

    }

    public void saveFileAs(ActionEvent event) {
        File file = openFolder(SRC_FOLDER+"/xml", "Choose folder to save loc.xml");
        if (file == null)
            return;
        main.inform("   File: " + file + "   \n   saved", 1500);
        currentLoc.toXML(manager.getList());
        String path = file + "/" + currentLoc.getID() + ".xml";
        File f = new File(path);
        if (f.exists())
            f.delete();
        XML.save(path);

    }

    public void openLocation(ActionEvent event) {
        File file = openFile(SRC_FOLDER+"/xml", "Choose loc.xml");
        if (file != null && file.exists()) {
            openLocation(file.getAbsolutePath());
        }


    }

    private void openLocation(String path) {
        clearAllData();
        XML.loadDocument(path);
        currentLoc = Loc.parseLocation(manager, XML.doc);
        main.setTitle(currentLoc.getLocid());
        renderer.setLoc(currentLoc);
    }

    private File openFile(String path, String question) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle(question);
        File defaultDirectory = new File(path);
        chooser.setInitialDirectory(defaultDirectory);
        return chooser.showOpenDialog(null);
    }

    public void newFileLocation(ActionEvent event) {
        if (main.askForBool("Want to destroy unsaved data?")) {
            clearAllData();
            setLocationName(null);
        }
    }

    private void clearAllData() {
        main.getListView().getItems().clear();
        renderer.clearAll();
        load();
        manager = new DuckManager(main.getListView(), renderer);
    }


    public void setLocationName(ActionEvent event) {
        String newName = main.askForString("ENTER LOCID", false);
        if (newName != null) {
            currentLoc.setLocid(newName);
            main.setTitle("CurrentLoc: " + newName);
        }
    }

    public void addStuffNoBitmap(ActionEvent event) {
        addstuff(false, true);
    }

    public void addStuff(ActionEvent event) {
        addstuff(false, false);
    }

    private void addstuff(boolean pickupable, boolean noBitmap) {
        Bitmap[] bitmaps = null;
        if (!noBitmap)
            bitmaps = openMultipleFileBitmap("Please selected bitmap", SRC_FOLDER+"/textures\\item");
        if (bitmaps != null) {
            for (Bitmap b : bitmaps) {
                b.scale(Controller.RATIO);
            }
        } else if (!noBitmap)
            return;
        String name = main.askForString("Create name for Stuff", false);
        if (name != null) {
            Stuff stuff = new Stuff(name);
            stuff.setIsPickupable(pickupable);
            if (bitmaps != null) {
                stuff.setBitmap(bitmaps);
                if (bitmaps.length != 1) {
                    Integer delay = main.askForInt("Enter delay of animation", true);
                    if (delay == null || delay < 1)
                        main.inform("No animation", 600);
                    else {

                        stuff.setMaxDelay(delay);
                        Boolean sawOrTooth = main.askForBool("Saw or Tooth Animation?");
                        stuff.setType(sawOrTooth ? 0 : 1);
                    }
                }
            }

            manager.addDuck(stuff);


        }

    }

    public void addPickStuff(ActionEvent event) {
        addstuff(true, false);
    }

    public void addArrow(ActionEvent event) {
        Arrow arrow = main.createArrow();
        if (arrow == null)
            return;
        manager.addDuck(arrow);
    }
    public void addArrowNoBitmap(ActionEvent event) {
        Arrow arrow = main.createArrow();
        if (arrow == null)
            return;
        arrow.setNoBitmap(true);
        manager.addDuck(arrow);
    }

    /**
     * tick update
     */
    @Override
    public void run() {
        renderer.render(main.getCanvas().getGraphicsContext2D());
    }

    public Bitmap openFileBitmap(String question, String directory) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(question);

        fileChooser.setInitialDirectory(new File(directory));
        //Set extension filter
        //FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");
        FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
        fileChooser.getExtensionFilters().addAll(extFilterPNG);

        List<File> files = new ArrayList<>();
        //Show open file dialog
        files.add(fileChooser.showOpenDialog(null));
        if (files.get(0) == null)
            return null;

        return new Bitmap(files.get(0).getAbsolutePath());
    }

    public Bitmap[] openMultipleFileBitmap(String question, String directory) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(question);

        fileChooser.setInitialDirectory(new File(directory));
        //Set extension filter
        //FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");
        FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
        fileChooser.getExtensionFilters().addAll(extFilterPNG);

        List<File> files = new ArrayList<>();
        //Show open file dialog
        files = fileChooser.showOpenMultipleDialog(null);
        if (files == null || files.get(0) == null)
            return null;
        Bitmap[] bitmaps = new Bitmap[files.size()];
        for (int i = 0; i < bitmaps.length; i++) {
            bitmaps[i] = new Bitmap(files.get(i).getAbsolutePath());
        }
        return bitmaps;
    }

    public File openFolder(String initialPath, String name) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle(name);
        File defaultDirectory = new File(initialPath);
        chooser.setInitialDirectory(defaultDirectory);
        return chooser.showDialog(null);
    }

    public void refreshListView() {
        manager.refreshCurrentDuck();
        if(manager.getCurrentDuck()==null){
            main.onArrowSelected(false);
            return;
        }
        main.onArrowSelected(manager.getCurrentDuck() instanceof Arrow);
    }

    public void refreshList() {
        manager.refreshList();
    }

    public void bitmapShadow(ActionEvent event) {
        Bitmap bitmap = openFileBitmap("Set shadow", SRC_FOLDER+"/textures/shadow");
        if (bitmap == null)
            return;
        bitmap.scale(1280, 720);
        currentLoc.setShadow(bitmap);
    }

    public void bitmapFore(ActionEvent event) {
        Bitmap bitmap = openFileBitmap("Set Fore", SRC_FOLDER+"/textures/location");
        if (bitmap == null)
            return;
        bitmap.scale(1280, 720);
        currentLoc.setFore(bitmap);
    }

    public void bitmapBool(ActionEvent event) {
        Bitmap bitmap = openFileBitmap("Set bool", SRC_FOLDER+"/textures/bool");
        if (bitmap == null)
            return;
        bitmap.scale(1280, 720);
        currentLoc.setBoolmap(bitmap);
    }

    public void bitmapBack(ActionEvent event) {
        Bitmap bitmap = openFileBitmap("Set background", SRC_FOLDER+"/textures/location");
        if (bitmap == null)
            return;
        System.out.println("bitmap dimesnions " + bitmap.getImage().getWidth());
        bitmap.scale(1280, 720);
        System.out.println("bitmap dimesnions potom " + bitmap.getImage().getWidth());

        currentLoc.setBack(bitmap);
    }

    public void edit(ActionEvent event) {
        System.out.println("edit");
        ListViewItem item = manager.getCurrentDuck();
        if (item == null)
            return;
        if (item.getType().equals(ListViewItem.Type.STUFF)) {
            main.openStuffParams((Stuff) item);
        } else if (item.getType().equals(ListViewItem.Type.ARROW)) {
            main.openArrowParams((Arrow) item);
        }
    }

    public void openLocParams(ActionEvent event) {
        main.openLocParams(currentLoc, event);
    }

    public void removeSelected(ActionEvent event) {
        if (manager.getCurrentDuck() != null) {
            manager.removeCurrentDuck();

        }
    }

    public void switchARMode(boolean b) {
        arMode = b;
        if (manager.getCurrentDuck() != null) {
            if (manager.getCurrentDuck().getType().equals(ListViewItem.Type.STUFF)) {
                ((Stuff) manager.getCurrentDuck()).setActionBitmapActive(b);
            }
        }
    }

    public static boolean isArMode() {
        return arMode;
    }

    public void switchArrowMode(boolean b) {
        Controller.ARROWSHOW = b;
        if (manager.getCurrentDuck() != null) {
            if (manager.getCurrentDuck().getType().equals(ListViewItem.Type.ARROW)) {
                ((Arrow) manager.getCurrentDuck()).setBig(((Arrow) manager.getCurrentDuck()).isBig());
            }
        }
    }

    public void switchJoeMode(boolean joeEnabled) {
        joe.setEnabled(joeEnabled);
    }

    public Joe getJoe() {
        return joe;
    }

    private boolean firstQuery;

    public void setArrowFinalCome(Arrow arrow, Stage dialog) {
        if (!firstQuery) {
            firstQuery = true;
            File pathFile = findFileWithName(SRC_FOLDER+"/xml/", arrow.getLocation() + ".xml");
            if (!pathFile.exists()) {
                Bitmap bitmap = openFileBitmap("Please chose location bitmap to put finalArrow pos in", SRC_FOLDER+"/textures/location");
                if (bitmap != null) {
                    this.bitmap = bitmap.scale(WIDTH, HEIGHT);
                }

            } else {
                XML.loadDocument(pathFile.getAbsolutePath());
                Loc loc = Loc.parseLocation(new DuckManager(new ListView<String>(), new Renderer(new Loc(""))), XML.doc);
                this.bitmap = loc.getBack();
            }
        } else {
            firstQuery = false;
            this.bitmap.setShouldRender(false);
            arrow.setFinal(getJoe().getPosX(), getJoe().getPosY());
            arrow.setFinalRight(getJoe().isRight());
            dialog.close();
            main.openArrowParams(arrow);
        }

    }

    public File findFileWithName(String pathFolder, String name) {
        File pathFile = new File(pathFolder);
        if (!pathFile.exists())
            return null;
        File possibleOut = new File(pathFile + "/" + name);
        if (possibleOut.exists())
            return possibleOut;
        File[] subFolders = pathFile.listFiles();
        if (subFolders == null) {
            return null;
        }
        for (File subFile : subFolders) {
            File outcome = findFileWithName(pathFolder + "/" + subFile.getName(), name);
            if (outcome != null)
                return outcome;
        }
        return null;
    }

    public void tick() {
        if (manager != null)
            manager.tick();
    }

    public void openEditing() {
        ListViewItem item = manager.getCurrentDuck();
        if (item == null)
            return;
        if (item instanceof Stuff) {
            main.openStuffParams((Stuff) item);
        } else if (item instanceof Arrow) {
            main.openArrowParams((Arrow) item);
        }
    }

    public void goToArrowLocation() {
        if (manager.getCurrentDuck() != null && manager.getCurrentDuck() instanceof Arrow) {
            Arrow arrow = (Arrow) manager.getCurrentDuck();
            if (arrow.getLocation() != null) {
                openLocation(findFileWithName(SRC_FOLDER+"/xml/",arrow.getLocation()+".xml").getAbsolutePath());
            }
        }
    }


}
