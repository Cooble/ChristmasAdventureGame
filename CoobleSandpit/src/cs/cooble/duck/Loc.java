package cs.cooble.duck;

import cs.cooble.canvas.Bitmap;
import cs.cooble.fx.Controller;
import cs.cooble.xml.XML;
import org.w3c.dom.*;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Matej on 19.5.2017.
 */
public class Loc implements ListViewItem {
    public static final String SRC_FOLDER = "C:\\Users\\Matej\\Dropbox\\Programming\\Java\\ChistmasGame\\ChistmasGameV2\\src\\main\\resources\\mainGameResource/";

    String locid;
    Bitmap back;
    Bitmap fore;
    Bitmap shadow;
    Bitmap boolmap;
    private boolean subLoc;

    private String sound,music;
    private double soundVolume,musicVolume;

    public static final int FORBID_Back = 0;
    public static final int FORBID_Fore = 0;
    public static final int FORBID_Shadow = 0;
    public static final int FORBID_Boolmap = 0;
    private boolean[] forbidenBitmaps = new boolean[4];

    public void setForbidTexture(int index, boolean val) {
        forbidenBitmaps[index] = val;
    }

    public boolean isForbidden(int index) {
        return forbidenBitmaps[index];
    }

    public Loc(String locid) {
        this.locid = locid;
    }

    public void setBack(Bitmap back) {
        this.back = back;
    }

    public void setBoolmap(Bitmap boolmap) {
        this.boolmap = boolmap;
    }

    public void setFore(Bitmap fore) {
        this.fore = fore;
    }

    public void setShadow(Bitmap shadow) {
        this.shadow = shadow;
    }

    public void setLocid(String locid) {
        this.locid = locid;
    }

    public Bitmap getBack() {
        return back;
    }

    public Bitmap getBoolmap() {
        return boolmap;
    }

    public Bitmap getFore() {
        return fore;
    }

    public Bitmap getShadow() {
        return shadow;
    }

    public String getLocid() {
        return locid;
    }

    public void setMusic(String music,double volume) {
        this.music = music;
        this.musicVolume=volume;
    }

    public void setSound(String sound,double volume) {
        this.sound = sound;
        this.soundVolume=volume;
    }

    public double getMusicVolume() {
        return musicVolume;
    }

    public String getMusic() {
        return music;
    }

    public double getSoundVolume() {
        return soundVolume;
    }

    public String getSound() {
        return sound;
    }

    public void toXML(ArrayList<ListViewItem> items) {
        XML.setupForSave();
        Document doc = XML.doc;
        Element root = doc.createElement("location");
        doc.appendChild(root);
        root.setAttribute("LOCID", getID());
        if (getBack() != null) {
            root.setAttribute("BACKGROUND", toName(getBack().getPath()));
        } else if (isForbidden(FORBID_Back)) {
            root.setAttribute("BACKGROUND", "-");
        }
        if (getFore() != null) {
            root.setAttribute("FOREGROUND", toName(getFore().getPath()));
        } else if (isForbidden(FORBID_Fore)) {
            root.setAttribute("FOREGROUND", "-");
        }
        if (getBoolmap() != null) {
            root.setAttribute("BOOLMAP", toName(getBoolmap().getPath()));
        } else if (isForbidden(FORBID_Boolmap)) {
            root.setAttribute("BOOLMAP", "-");
        }
        if (getShadow() != null) {
            root.setAttribute("SHADOW", toName(getShadow().getPath()));
        } else if (isForbidden(FORBID_Shadow)) {
            root.setAttribute("SHADOW", "-");
        }
        if (this.isSubLoc()) {
            Element subLoc = doc.createElement("isSubLocation");
            root.appendChild(subLoc);
        }
        if (this.sound!=null) {
            Element subLoc = doc.createElement("backgroundSound");
            subLoc.setTextContent(this.sound);
            subLoc.setAttribute("volume",soundVolume+"");
            root.appendChild(subLoc);
        }
        if (this.music!=null) {
            Element subLoc = doc.createElement("backgroundSound");
            subLoc.setTextContent(this.music);
            subLoc.setAttribute("volume",musicVolume+"");
            root.appendChild(subLoc);
        }
        Element stuffs = doc.createElement("stuffs");
        root.appendChild(stuffs);
        Element arrows = doc.createElement("arrows");
        root.appendChild(arrows);
        for (ListViewItem item : items) {
            if (item.getType().equals(Type.STUFF)) {
                Stuff stuff = (Stuff) item;
                boolean animation = stuff.isAnimation();
                Element stuffElement = doc.createElement(stuff.getID());
                stuffs.appendChild(stuffElement);
                stuffElement.setAttribute("x", stuff.getX() + "");
                stuffElement.setAttribute("y", stuff.getY() + "");
                stuffElement.setAttribute("width", stuff.getWidth() + "");
                stuffElement.setAttribute("height", stuff.getHeight() + "");
                if (stuff.getBitmap() != null) {
                    Element bitmapElement = doc.createElement("bitmap");
                    stuffElement.appendChild(bitmapElement);
                    if (stuff.isBitmapStack()) {
                        String animationName = new File(new File(stuff.getBitmap().getPath()).getParent()).getName();
                        bitmapElement.appendChild(doc.createTextNode(animationName));

                    } else {
                        bitmapElement.appendChild(doc.createTextNode(toName(stuff.getBitmap().getPath())));

                    }
                    bitmapElement.setAttribute("scale", stuff.getScale() + "");
                    bitmapElement.setAttribute("x", stuff.getBitmapOffsetX() + "");
                    bitmapElement.setAttribute("y", stuff.getBitmapOffsetY() + "");
                    if (animation) {
                        bitmapElement.setAttribute("delay", stuff.getMaxDelay() + "");
                        bitmapElement.setAttribute("type", stuff.getAnimationType() == 0 ? "SAW" : "TOOTH");
                    }
                }
                if (stuff.isPickupable()) {
                    Element pickupElement = doc.createElement("pickup");
                    stuffElement.appendChild(pickupElement);
                }
                if (stuff.isToCome()) {
                    Element pickupElement = doc.createElement("toCome");
                    stuffElement.appendChild(pickupElement);
                    pickupElement.setAttribute("x", stuff.getxToCome() + "");
                    pickupElement.setAttribute("y", stuff.getyToCome() + "");
                }
            } else if (item.getType().equals(Type.ARROW)) {
                Arrow arrow = (Arrow) item;
                Element arrowElement = doc.createElement(arrow.getID());
                arrows.appendChild(arrowElement);
                arrowElement.setAttribute("x", arrow.getX() + "");
                arrowElement.setAttribute("y", arrow.getY() + "");
                arrowElement.setAttribute("width", arrow.getWidth() + "");
                arrowElement.setAttribute("height", arrow.getHeight() + "");
                arrowElement.setAttribute("imageX", arrow.getBitmapOffsetX() + "");
                arrowElement.setAttribute("imageY", arrow.getBitmapOffsetY() + "");
                arrowElement.setAttribute("location", arrow.getLocation() + "");

                Element posElement = doc.createElement("pos");
                arrowElement.appendChild(posElement);
                posElement.appendChild(doc.createTextNode(arrow.getPos()));
                if(arrow.noBitmap) {
                    Element noElement = doc.createElement("noBitmap");
                    arrowElement.appendChild(noElement);
                }

                Element sizeElement = doc.createElement("size");
                arrowElement.appendChild(sizeElement);
                sizeElement.appendChild(doc.createTextNode(arrow.isBig() ? "BIG" : "SMALL"));

                if (arrow.isToCome()) {
                    Element toFinalElement = doc.createElement("toCome");
                    arrowElement.appendChild(toFinalElement);
                    toFinalElement.setAttribute("x", arrow.getxToCome() + "");
                    toFinalElement.setAttribute("y", arrow.getyToCome() + "");
                }

                Element toFinalElement = doc.createElement("toFinal");
                arrowElement.appendChild(toFinalElement);
                toFinalElement.setAttribute("x", arrow.getFinalX() + "");
                toFinalElement.setAttribute("y", arrow.getFinalY() + "");
                toFinalElement.setAttribute("right", arrow.getFinalFace() + "");
            }
        }
    }

    private String toName(String path) {
        String name = new File(path).getName();
        return name.substring(0, name.lastIndexOf('.'));
    }

    @Override
    public Type getType() {
        return Type.LOCATION;
    }

    @Override
    public String getID() {
        return locid;
    }

    @Override
    public Bitmap[] getBufferedImages() {
        return new Bitmap[0];
    }

    @Override
    public int getLevel() {
        return 0;
    }

    public static Loc parseLocation(DuckManager manager, Document document) {
        Loc out = new Loc("");
        Element root = document.getDocumentElement();
        if (!root.getNodeName().toLowerCase().equals("location")) {
            return null;
        }
        NamedNodeMap map = root.getAttributes();//loading loc atributes
        out.setLocid(map.getNamedItem("LOCID").getNodeValue());

        String texturesFolder =SRC_FOLDER+"textures/";
        try {
            out.setBack(Bitmap.load(texturesFolder+"location/" + map.getNamedItem("BACKGROUND").getNodeValue() + ".png"));
        } catch (Exception ingored) {
        }
        try {
            out.setFore(Bitmap.load(texturesFolder+"location/" + map.getNamedItem("FOREGROUND").getNodeValue() + ".png"));
        } catch (Exception e) {
        }
        try {
            out.setShadow(Bitmap.load(texturesFolder+"shadow/" + map.getNamedItem("SHADOW").getNodeValue() + ".png"));
        } catch (Exception e) {
        }
        try {
            out.setBoolmap(Bitmap.load(texturesFolder+"bool/" + map.getNamedItem("BOOLMAP").getNodeValue() + ".png"));
        } catch (Exception e) {
        }
        try

        {

            out.getBack().scale(1280, 720);
            out.getFore().scale(1280, 720);
            out.getShadow().scale(1280, 720);
            out.getBoolmap().scale(1280, 720);
        } catch (
                Exception ingored
                )

        {
        }//something is null
        NodeList list = root.getElementsByTagName("isSubLocation");
        if (list != null) {
            Node node = list.item(0);
            if (node != null) {
                out.setSubLoc(true);
            }
        }

        list = root.getElementsByTagName("stuffs");
        if (list != null)

        {
            Node node = list.item(0);
            if (node != null) {
                ArrayList<Stuff> stuffs = parseStuff(node.getChildNodes());
                for (Stuff stuff : stuffs) {
                    manager.addDuck(stuff);
                }
            }
        }

        list = root.getElementsByTagName("arrows");
        if (list != null)

        {
            Node node = list.item(0);
            if (node != null) {
                ArrayList<Arrow> arrows = parseArrow(node.getChildNodes());
                for (Arrow arrow : arrows) {
                    manager.addDuck(arrow);

                }
            }
        }

        return out;
    }

    public static ArrayList<Stuff> parseStuff(NodeList stuffs) {
        ArrayList<Stuff> out = new ArrayList<>(stuffs.getLength());
        for (int i = 0; i < stuffs.getLength(); i++) {
            Node n = stuffs.item(i);
            if (n.getNodeName().equals("#text"))//skip this strange node
                continue;
            String id = n.getNodeName();
            double scale = 1;
            Bitmap bitmap = null;
            boolean toCome = false;
            Bitmap[] bitmaps = null;
            NodeList children = n.getChildNodes();
            NamedNodeMap atributes = n.getAttributes();
            int x = 0, y = 0, width = 0, height = 0, xToCome = 0, yToCome = 0;
            String itemName = null;
            boolean pickup = false;
            //animation
                boolean saw=true;
                int delay=0;
            //========
            for (int j = 0; j < atributes.getLength(); j++) {
                Node atribute = atributes.item(j);
                String aName = atribute.getNodeName();
                switch (aName) {
                    case "x":
                        x = Integer.parseInt(atribute.getNodeValue());
                        break;
                    case "y":
                        y = Integer.parseInt(atribute.getNodeValue());
                        break;
                    case "width":
                        width = Integer.parseInt(atribute.getNodeValue());
                        break;
                    case "height":
                        height = Integer.parseInt(atribute.getNodeValue());
                        break;
                }
            }
            for (int j = 0; j < children.getLength(); j++) {
                Node child = children.item(j);
                if (child.getNodeName().equals("bitmap")) {
                    DoubleBasket scal = new DoubleBasket();
                    bitmap = parseBitmap("item", child, scal);
                    if (bitmap == null) {//try if it is animation instead
                        IntBasket dela = new IntBasket();
                        BoolBasket sa = new BoolBasket();
                        bitmaps = parseBitmaps("item",child,scal,dela,sa);
                        saw=sa.d;
                        delay=dela.d;

                    }
                    scale = scal.d;

                }
                else if(child.getNodeName().equals("pickup")){
                    pickup=true;
                }
                else if (child.getNodeName().equals("toCome")) {
                    toCome=true;
                    NamedNodeMap atributess = child.getAttributes();
                    for (int k = 0; k < atributess.getLength(); k++) {
                        Node atribute = atributess.item(k);
                        String aName = atribute.getNodeName();
                        switch (aName) {
                            case "x":
                                xToCome = Integer.parseInt(atribute.getNodeValue());
                                break;
                            case "y":
                                yToCome = Integer.parseInt(atribute.getNodeValue());
                                break;
                        }
                    }
                    itemName = child.getNodeValue();
                }
            }
            Stuff stuff = new Stuff(id);
            stuff.setIsPickupable(pickup);
            if (bitmap != null) {
                stuff.setBitmap(new Bitmap[]{bitmap});
                stuff.setScale(scale);

            }
            else if(bitmaps!=null){
                stuff.setBitmap(bitmaps);
                stuff.setMaxDelay(delay);
                stuff.setAnimationType(saw?0:1);
                stuff.setScale(scale);
            }
            stuff.setActionOffset(x, y);
            stuff.setActionDimensions(width, height);
            stuff.setIsToCome(false);
            if (toCome) {
                stuff.setIsToCome(true);
                stuff.setToCome(xToCome, yToCome);
            }
            out.add(stuff);
        }
        return out;
    }

    public static Bitmap parseBitmap(String currentFolder, Node node, DoubleBasket scale) {
        Bitmap bitmap = Bitmap.load("C:/Users/Matej/Dropbox/Programming/Java/ChistmasGame/ChistmasGameV2/src/main/resources/mainGameResource/res/textures/" + currentFolder + "/" + node.getTextContent() + ".png");
        if (bitmap == null) {
            return null;
        }
        //bitmap.scale(Controller.RATIO);
        NamedNodeMap map = node.getAttributes();
        for (int i = 0; i < map.getLength(); i++) {
            Node atribute = map.item(i);
            String aName = atribute.getNodeName();
            switch (aName) {
                case "scale":
                    double d;
                    try {
                        d = Double.parseDouble(atribute.getNodeValue());
                    } catch (Exception p) {
                        d = Integer.parseInt(atribute.getNodeValue());
                    }
                    scale.setD(d);
                    break;
                case "x":
                    int dd = Integer.parseInt(atribute.getNodeValue());
                    bitmap.setOffset(dd * Controller.RATIO, bitmap.getOffsetY());
                    break;
                case "y":
                    int ddd = Integer.parseInt(atribute.getNodeValue());
                    bitmap.setOffset(bitmap.getOffsetX(), ddd * Controller.RATIO);
                    break;
            }
        }
        return bitmap;
    }

    public static Bitmap[] parseBitmaps(String currentFolder, Node node, DoubleBasket scale, IntBasket delay, BoolBasket saw) {
        Bitmap[] out;
        File folder = new File("C:/Users/Matej/Dropbox/Programming/Java/ChistmasGame/ChistmasGameV2/src/main/resources/mainGameResource//res/textures/" +currentFolder + "/" + node.getTextContent() + "/");
        File[] files = folder.listFiles();
        out = new Bitmap[files.length];
        for (int i = 0; i < files.length; i++) {
            out[i] = Bitmap.load(folder.getAbsolutePath() + "/" + i + ".png");
        }
        NamedNodeMap map = node.getAttributes();
        for (int i = 0; i < map.getLength(); i++) {
            Node atribute = map.item(i);
            String aName = atribute.getNodeName();
            switch (aName) {
                case "scale":
                    double d;
                    try {
                        d = Double.parseDouble(atribute.getNodeValue());
                    } catch (Exception p) {
                        d = Integer.parseInt(atribute.getNodeValue());
                    }
                    scale.setD(d);
                    break;
                case "x":
                    int dd = Integer.parseInt(atribute.getNodeValue());
                    for (Bitmap b : out) {
                        b.setOffset(dd * Controller.RATIO, b.getOffsetY());
                    }
                    break;

                case "y":
                    int ddd = Integer.parseInt(atribute.getNodeValue());
                    for (Bitmap b : out) {
                        b.setOffset(b.getOffsetX(), ddd * Controller.RATIO);
                    }
                    break;
                case "delay":
                    int dddd = Integer.parseInt(atribute.getNodeValue());
                    delay.setD(dddd);
                    break;
                case "type":
                    boolean ddddd = atribute.getNodeValue().equals("SAW");
                    saw.setD(ddddd);
                    break;
            }
        }
        return out;
    }

    public static ArrayList<Arrow> parseArrow(NodeList stuffs) {
        ArrayList<Arrow> out = new ArrayList<>(stuffs.getLength());
        for (int i = 0; i < stuffs.getLength(); i++) {
            Node n = stuffs.item(i);
            if (n.getNodeName().equals("#text"))//skip this strange node
                continue;
            String id = n.getNodeName();
            String location = null;
            NodeList children = n.getChildNodes();
            NamedNodeMap atributes = n.getAttributes();
            boolean toCome=false;
            boolean imagePos=false;
            boolean noBitmap=false;
            int x = -1, y = 0, width = 0, height = 0, xToCome = 0, yToCome = 0, xFinal = 0, yFinal = 0, imageX = 0, imageY = 0;
            boolean faceRight = false;
            String pos = null;
            boolean big = true;
            for (int j = 0; j < atributes.getLength(); j++) {
                Node atribute = atributes.item(j);
                String aName = atribute.getNodeName();
                switch (aName) {
                    case "x":
                        x = Integer.parseInt(atribute.getNodeValue());
                        break;
                    case "y":
                        y = Integer.parseInt(atribute.getNodeValue());
                        break;
                    case "width":
                        width = Integer.parseInt(atribute.getNodeValue());
                        break;
                    case "height":
                        height = Integer.parseInt(atribute.getNodeValue());
                        break;
                    case "location":
                        location = atribute.getNodeValue();
                        break;
                    case "imageX":
                        imageX = Integer.parseInt(atribute.getNodeValue());
                        break;
                    case "imageY":
                        imageY = Integer.parseInt(atribute.getNodeValue());
                        break;
                }
            }
            for (int j = 0; j < children.getLength(); j++) {
                Node child = children.item(j);
                if (child.getNodeName().equals("pos")) {
                    pos = child.getTextContent();
                }
                if (child.getNodeName().equals("size")) {
                    big = child.getTextContent().equals("BIG");
                }
                if (child.getNodeName().equals("toCome")) {
                    NamedNodeMap map = child.getAttributes();
                    toCome=true;
                    for (int k = 0; k < map.getLength(); k++) {
                        Node nn = map.item(k);
                        String nomine = nn.getNodeName();
                        switch (nomine) {
                            case "x":
                                xToCome = Integer.parseInt(nn.getNodeValue());
                                break;
                            case "y":
                                yToCome = Integer.parseInt(nn.getNodeValue());
                                break;
                        }
                    }
                }
                if (child.getNodeName().equals("noBitmap")) {
                 noBitmap=true;
                }
                if (child.getNodeName().equals("toFinal")) {
                    NamedNodeMap map = child.getAttributes();
                    for (int k = 0; k < map.getLength(); k++) {
                        Node nn = map.item(k);
                        String nomine = nn.getNodeName();
                        switch (nomine) {
                            case "x":
                                xFinal = Integer.parseInt(nn.getNodeValue());
                                break;
                            case "y":
                                yFinal = Integer.parseInt(nn.getNodeValue());
                                break;
                            case "right":
                                faceRight = Boolean.parseBoolean(nn.getNodeValue());
                                break;
                        }
                    }
                }
            }
            Arrow arrow = new Arrow();
            arrow.setID(id);
            arrow.setIsToCome(toCome);
            arrow.setToCome(xToCome, yToCome);
            arrow.setPos(pos);
            arrow.setBitmapOffset(imageX, imageY);
            arrow.setActionOffset(x, y);
            arrow.setActionDimensions(width, height);
            arrow.setBig(big);
            arrow.setLocation(location);
            arrow.setFinal(xFinal, yFinal);
            arrow.setFinalRight(faceRight);
            arrow.setNoBitmap(noBitmap);
            out.add(arrow);
        }
        return out;
    }

    public boolean isSubLoc() {
        return subLoc;
    }

    public void setSubLoc(boolean subLoc) {
        this.subLoc = subLoc;
    }

    static class DoubleBasket {
        double d;

        public void setD(double d) {
            this.d = d;
        }

        public double getD() {
            return d;
        }
    }

    static class IntBasket {
        int d;

        public void setD(int d) {
            this.d = d;
        }

        public int getD() {
            return d;
        }
    }

    static class BoolBasket {
        boolean d;

        public void setD(boolean d) {
            this.d = d;
        }

        public boolean getD() {
            return d;
        }
    }
}
