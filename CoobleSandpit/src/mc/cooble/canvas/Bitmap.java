package mc.cooble.canvas;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import mc.cooble.duck.IXML;
import org.w3c.dom.Node;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Created by Matej on 18.5.2017.
 */
public final class Bitmap implements IXML, BitmapProvider {
    private Image image;
    private int offsetX,offsetY;
    private final int defaultWidth;
    private final int defaultHeight;
    private boolean shouldRender;
    private String path;
    private double scale=1;
    public Bitmap(String path){
        this.path = path;
        System.out.println("Load image called "+path);
        image = SwingFXUtils.toFXImage(getBuffered(path), null);
        defaultWidth = (int) image.getWidth();
        defaultHeight = (int) image.getHeight();
        shouldRender=true;
    }
    public static Bitmap load(String path){
        if(new File(path).exists()){
            return new Bitmap(path);
        }
        System.err.println("Error this image is not real! "+path);
        return null;
    }
    public static BufferedImage getBuffered(String path){
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(new File(path));
        } catch (Exception e) {
            System.err.println("Cannot load bitmap: "+path);
            e.printStackTrace();
            System.out.println("Cannot load bitmap: "+path);
        }
        return bufferedImage;
    }
    public Bitmap(){
        shouldRender=true;
        defaultWidth=0;
        defaultHeight=0;
    }
    public Bitmap(int defaultWidth,int defaultHeight) {
        this.defaultWidth = defaultWidth;
        this.defaultHeight = defaultHeight;
        shouldRender = true;
    }
    public static void scale(Bitmap bitmap,int width,int height){
        bitmap.image=SwingFXUtils.toFXImage(resizeBuffered(getBuffered(bitmap.getPath()),width,height), null);
        bitmap.scale=(double)width/(double)bitmap.defaultWidth;
    }
    public static BufferedImage resizeBuffered(BufferedImage img, int newW, int newH) {
        int w = img.getWidth();
        int h = img.getHeight();
        BufferedImage dimg = new BufferedImage(newW, newH, img.getType());
        Graphics2D g = dimg.createGraphics();
        g.drawImage(img, 0, 0, newW, newH, 0, 0, w, h, null);
        g.dispose();
        return dimg;
    }
    public static void scale(Bitmap bitmap,double scale){
        scale(bitmap,(int)(bitmap.defaultWidth*scale),(int)(bitmap.defaultHeight*scale));
    }
    public Bitmap scale(int width,int height){
        scale(this,width,height);
        return this;
    }

    private static Image flip(Image in){
        BufferedImage input = SwingFXUtils.fromFXImage(in, null);
        BufferedImage out = new BufferedImage(input.getWidth(),input.getHeight(),BufferedImage.TYPE_INT_ARGB);
        Graphics graphics = out.createGraphics();
        graphics.drawImage(input,0,0,input.getWidth(),input.getHeight(),input.getWidth(),0,0,input.getHeight(),null);
        graphics.dispose();

        return SwingFXUtils.toFXImage(out,null);
    }

    public Bitmap flip(){
        this.image=flip(this.getImage());
        return this;
    }

    public Bitmap scale(double scale) {
        scale(this,scale);
        return this;
    }

    public int getOffsetX() {
        return offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public Image getImage() {
        return image;
    }

    public boolean shouldRender() {
        return shouldRender;
    }

    public void setShouldRender(boolean shouldRender) {
        this.shouldRender = shouldRender;
    }

    public void setOffset(int x,int y){
        offsetX=x;
        offsetY=y;
    }

    public String getPath() {
        return path;
    }

    public int getWidth(){
        if(image==null)
            return 0;
        return (int)(scale*defaultWidth);
    }
    public int getHeight(){
        if(image==null)
            return 0;
        return (int)(scale*defaultHeight);
    }

    public int getDefaultBitmapHeight() {
        return defaultHeight;
    }

    public int getDefaultBitmapWidth() {
        return defaultWidth;
    }

    @Override
    public Node toXML() {
        return null;
    }

    public static Bitmap create(int width, int height, Color selectColor) {
        if(width<=0||height<=0){
            Bitmap out = new Bitmap();
            out.setShouldRender(false);
            return out;
        }

        BufferedImage bufferedImage = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
        Graphics graphics = bufferedImage.getGraphics();
        graphics.setColor(selectColor);
        graphics.fillRect(0,0,width,height);
        Image image;
        image = SwingFXUtils.toFXImage(bufferedImage, null);
        Bitmap out = new Bitmap(width,height);
        out.image=image;
        return out;

    }
    /**
     * Rotates an image. Actually rotates a new copy of the image.
     *
     * @param img The image to be rotated
     * @param angle The angle in degrees
     * @return The rotated image
     */
    private static Image rotate(Image img, double angle) {
        double sin = Math.abs(Math.sin(angle)),
                cos = Math.abs(Math.cos(angle));

        int w = (int) img.getWidth(), h = (int) img.getHeight();

        int neww = (int) Math.floor(w*cos + h*sin),
                newh = (int) Math.floor(h*cos + w*sin);

        BufferedImage bimg = new BufferedImage(neww,newh,BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bimg.createGraphics();

        g.translate((neww-w)/2, (newh-h)/2);
        g.rotate(angle, w/2, h/2);
        g.drawRenderedImage(SwingFXUtils.fromFXImage(img,null), null);
        g.dispose();

        return SwingFXUtils.toFXImage(bimg,null);
    }

    public static Bitmap rotate(Bitmap bitmap,double angle){
        bitmap.image=rotate(bitmap.getImage(),angle);
        return bitmap;
    }

    public Bitmap rotate(double angle){
        rotate(this,angle);
        return this;
    }

    public Bitmap clone(){
        Bitmap out = new Bitmap(this.defaultWidth,this.defaultHeight);
        out.path=this.path;
        out.image= SwingFXUtils.toFXImage(SwingFXUtils.fromFXImage(this.image,null),null);
        out.scale=this.scale;
        out.offsetY=this.offsetY;
        out.offsetX=this.offsetX;
        out.shouldRender=this.shouldRender;
        return out;
    }


    public double getScale() {
        return scale;
    }

    @Override
    public Bitmap[] getBufferedImages() {
        return new Bitmap[]{this};
    }

    @Override
    public int getLevel() {
        return 0;
    }
}
