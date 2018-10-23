package mc.cooble.canvas;

import javafx.scene.canvas.GraphicsContext;
import mc.cooble.duck.Loc;
import mc.cooble.fx.Controller;
import mc.cooble.fx.Main;

import java.util.ArrayList;

/**
 * Created by Matej on 18.5.2017.
 */
public class Renderer {
    ArrayList<BitmapProvider> providers = new ArrayList<>();
    ArrayList<BitmapProvider> primaryProviders = new ArrayList<>();
    private Loc loc;

    public Renderer(Loc loc){
        this.loc = loc;
    }

    public void render(GraphicsContext context) {
        context.setFill(new javafx.scene.paint.Color(1,1,1,1));
        context.fillRect(0,0, Controller.WIDTH+ Main.OFFSET_CANVAS*2,Controller.HEIGHT+Main.OFFSET_CANVAS*2);
        context.setFill(new javafx.scene.paint.Color(0.5,0.5,0.5,1));

        context.fillRect(Main.OFFSET_CANVAS,Main.OFFSET_CANVAS, Controller.WIDTH,Controller.HEIGHT);

        if (loc.getBack() != null){
            context.drawImage(loc.getBack().getImage(),Main.OFFSET_CANVAS,Main.OFFSET_CANVAS);
        }

        for (BitmapProvider provider : providers) {
            Bitmap[] bitmap = provider.getBufferedImages();
            for (Bitmap bitmap1 : bitmap) {
                if (bitmap1 != null)
                    renderBitmap(bitmap1, context);

            }
        }
        if(loc.getFore()!=null){
            context.drawImage(loc.getFore().getImage(),Main.OFFSET_CANVAS,Main.OFFSET_CANVAS);
        }
        if(loc.getShadow()!=null){
            context.drawImage(loc.getShadow().getImage(),Main.OFFSET_CANVAS,Main.OFFSET_CANVAS);
        }
        for (BitmapProvider primaryProvider : primaryProviders) {
            Bitmap[] bitmap = primaryProvider.getBufferedImages();
            for (Bitmap bitmap1 : bitmap) {
                if (bitmap1 != null)
                    renderBitmap(bitmap1, context);

            }
        }
    }

    private void renderBitmap(Bitmap bitmap1, GraphicsContext context) {
        if (bitmap1.shouldRender())
            context.drawImage(bitmap1.getImage(), bitmap1.getOffsetX()+ Main.OFFSET_CANVAS, bitmap1.getOffsetY()+ Main.OFFSET_CANVAS, bitmap1.getWidth(), bitmap1.getHeight());

    }

    public void addBitmapProvider(BitmapProvider provider) {
        providers.add(provider);
    }
    public void removeBitmapProvider(BitmapProvider provider) {
        for (int i = 0; i < providers.size(); i++) {
            BitmapProvider providerr = providers.get(i);
            if (providerr.equals(provider)) {
                providers.remove(i);
                return;
            }
        }
    }

    public void addPrimaryProvider(BitmapProvider provider){
        primaryProviders.add(provider);
    }
    public void removePrimaryBitmapProvider(BitmapProvider provider){
        for (int i = 0; i < primaryProviders.size(); i++) {
            if(provider.equals(primaryProviders.get(i))){
                primaryProviders.remove(i);
                return;
            }
        }
    }
    public void clearAll(){
        providers.clear();
    }

    public void setLoc(Loc loc) {
        this.loc = loc;
    }
}
