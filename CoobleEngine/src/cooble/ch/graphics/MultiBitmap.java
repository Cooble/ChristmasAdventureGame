package cooble.ch.graphics;

import java.util.ArrayList;

/**
 * Created by Matej on 6.8.2016.
 */
public class MultiBitmap implements MultiBitmapProvider {

    private final ArrayList<Bitmap> bitmaps;
    private Bitmap[] array;
    private boolean shouldRender;

    public MultiBitmap(int initialSize) {
        bitmaps=new ArrayList<>();
        shouldRender=true;
        array =new Bitmap[initialSize];
    }
    public void addBitmap(Bitmap b){
        bitmaps.add(b);
        refreshArray();
    }
    private void refreshArray(){
        if(bitmaps.size()> array.length){
            Bitmap[] newArray  = new Bitmap[bitmaps.size()+10];
            for (int i = 0; i < bitmaps.size(); i++) {
                newArray[i]=bitmaps.get(i);
            }
            array =newArray;
        }
    }

    @Override
    public BitmapProvider[] getBitmaps() {
        return array;
    }

    @Override
    public boolean shouldRender() {
        return shouldRender;
    }

    public void setShouldRender(boolean shouldRender) {
        this.shouldRender = shouldRender;
    }
}
