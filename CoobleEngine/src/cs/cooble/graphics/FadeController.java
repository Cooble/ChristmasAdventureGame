package cs.cooble.graphics;


import cs.cooble.window.Tickable;

/**
 * Created by Matej on 19.7.2016.
 *///todo fadecontroller
public class FadeController implements Tickable, BitmapProvider {
    private Bitmap source;
    private Bitmap fade;
    private Bitmap output;
    private boolean render;
    private int maxDelay;
    private int delay;
    private boolean toDark;//or to bright
    private Runnable done;

    public FadeController(Bitmap source, int maxDelay, boolean toDark, Runnable done) {
        this.source = source;
        this.maxDelay = maxDelay;
        this.toDark = toDark;
        this.done = done;
    }


    @Override
    public void tick() {

    }

    @Override
    public Bitmap getCurrentBitmap() {
        return output;
    }

    @Override
    public int[] getOffset() {
        return new int[]{0, 0};
    }

    @Override
    public boolean shouldRender() {
        return render;
    }
}
