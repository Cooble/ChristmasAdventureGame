package cs.cooble.location;


import cs.cooble.core.Game;
import cs.cooble.graphics.Bitmap;
import cs.cooble.graphics.Renderer;
import cs.cooble.graphics.TextPainter;
import cs.cooble.world.Location;

/**
 * Created by Matej on 19.7.2016.
 */
public class LocationExit extends Location {
    TextPainter textPainter;

    public LocationExit() {
        super("exit");

    }

    private String[] fromLastToFirst(String[] in) {
        String[] out = new String[in.length];
        for (int i = 0; i < out.length; i++) {
            out[i] = in[out.length - 1 - i];
        }
        return out;
    }

    @Override
    public void onStartRendering(Renderer renderer) {
        super.onStartRendering(renderer);
        textPainter.writeOnBitmap(fromLastToFirst(Game.error));
        textPainter.getBitmaps()[0].getCurrentBitmap().setOffset(8*2, 0*2);

        renderer.registerGUIProvider(textPainter);
    }

    @Override
    public void onStopRendering(Renderer renderer) {
        super.onStopRendering(renderer);
        renderer.removeGuiProvider(textPainter);
    }

    @Override
    public void loadTextures() {
        setBackground(Bitmap.get("location/exit"));
        textPainter = new TextPainter(160*2, 90*2, Game.font);
    }
}
