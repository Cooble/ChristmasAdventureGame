package cooble.ch.world;

import com.sun.istack.internal.Nullable;
import cooble.ch.entity.Arrow;
import cooble.ch.entity.Position;
import cooble.ch.event.LocationLoadEvent;
import cooble.ch.graphics.*;
import cooble.ch.inventory.stuff.Stuff;
import cooble.ch.inventory.stuff.StuffToCome;
import org.w3c.dom.*;

import java.util.ArrayList;

/**
 * This class does everything with parsing the xml documents
 * It create whole new Location with document or it sets all the attributes to predefined location
 */
public final class LocationFactory {
    private static final String[] VALS = new String[10];
    private static final String[] KEYS = new String[VALS.length];
    private static final int
            LOCID = 0,
            BACKGROUND_NAME = 1,
            FOREGROUND_NAME = 2,
            BOOLMAP_NAME = 3,
            SHADOW_NAME = 4;

    static {
        KEYS[LOCID] = "LOCID";
        KEYS[BACKGROUND_NAME] = "BACKGROUND";
        KEYS[FOREGROUND_NAME] = "FOREGROUND";
        KEYS[BOOLMAP_NAME] = "BOOLMAP";
        KEYS[SHADOW_NAME] = "SHADOW";
    }

    /**
     * @param document xml document created usually by Sandpit.jar
     * @param location it sets all the attributes to predefined location or creates new if location==null
     * @return new Location or modified location in argument
     */
    public static Location parseLocation(Document document, @Nullable Location location) {
        Element root = document.getDocumentElement();

        if (!root.getNodeName().toLowerCase().equals("location")) {
            return null;
        }
        boolean subLocation = false;
        NodeList list = root.getElementsByTagName("isSubLocation");
        if (list != null) {
            Node node = list.item(0);
            if (node != null) {
                subLocation = true;
            }
        }
        String backgroundSound=null;
        double backgroundSoundVolume=0;
        list = root.getElementsByTagName("backgroundSound");
        if (list != null) {
            Node node = list.item(0);
            if (node != null) {
                backgroundSound = node.getTextContent();
                NamedNodeMap map = node.getAttributes();
                backgroundSoundVolume = Double.parseDouble(map.getNamedItem("volume").getNodeValue());

            }
        }
        String backgroundMusic=null;
        double backgroundMusicVolume=0;
        list = root.getElementsByTagName("backgroundMusic");
        if (list != null) {
            Node node = list.item(0);
            if (node != null) {
                backgroundMusic = node.getTextContent();
                NamedNodeMap map = node.getAttributes();
                backgroundMusicVolume = Double.parseDouble(map.getNamedItem("volume").getNodeValue());

            }
        }
        NamedNodeMap map = root.getAttributes();//loading loc atributes
        for (int i = 0; i < 5; i++) {
            Node currentNode = map.getNamedItem(KEYS[i]);
            if (currentNode != null) {
                VALS[i] = currentNode.getNodeValue();
            } else VALS[i] = null;
        }

        if (location == null) {
            location = new LocationBlank(VALS[LOCID]);
        } else if (!location.getLOCID().equals(VALS[LOCID]))//return null if input location nullable  does not have same locid as document one
            return null;

        if (VALS[BACKGROUND_NAME] != null) {
            if (!"-".equals(VALS[BACKGROUND_NAME]))
                location.setBackground("location/" + VALS[BACKGROUND_NAME]);
        } else
            try {
                location.setDefaultBackground();
            } catch (Exception ignored) {
            }
        Bitmap.silentConsole=true;
        if (VALS[FOREGROUND_NAME] != null) {
            if (!"-".equals(VALS[FOREGROUND_NAME]))
                location.setForeground("location/" + VALS[FOREGROUND_NAME]);
        } else try {
            location.setDefaultForeground();
        } catch (Exception ignored) {
        }
        if (VALS[SHADOW_NAME] != null) {
            if (!"-".equals(VALS[FOREGROUND_NAME]))
                location.setShadow(Bitmap.get("shadow/" + VALS[SHADOW_NAME]));
        } else try {
            location.setDefaultShadow();
        } catch (Exception ignored) {
        }
        if (VALS[BOOLMAP_NAME] != null) {
            if (!VALS[BOOLMAP_NAME].equals("-"))
                location.setBoolMap(BoolMap.getBoolMap(VALS[BOOLMAP_NAME]));
        } else
            try {
                if (!subLocation)
                    location.setDefaultBoolMap();
            } catch (Exception ignored) {
            }
        Bitmap.silentConsole=false;

        list = root.getElementsByTagName("stuffs");
        if (list != null) {
            Node node = list.item(0);
            if (node != null) {
                ArrayList<Stuff> stuffs = parseStuff(node.getChildNodes(),location);
                for (Stuff stuff : stuffs) {
                    location.addStuff(stuff);
                }
            }
        }
        list = root.getElementsByTagName("arrows");
        if (list != null) {
            Node node = list.item(0);
            if (node != null) {
                ArrayList<Boolean> areMovers = new ArrayList<>(node.getChildNodes().getLength());
                ArrayList<Arrow> arrows = parseArrow(node.getChildNodes(), areMovers);
                for (int i = 0; i < arrows.size(); i++) {
                    Arrow arrow = arrows.get(i);
                    if (areMovers.get(i))
                        location.addArrowWithMover(arrow);
                    else location.addArrow(arrow);
                }
            }
        }

        location.setIsSubLocation(subLocation);

        location.backgroundSound=backgroundSound;
        location.backgroundSoundVolume=backgroundSoundVolume;
        location.backgroundMusic=backgroundMusic;
        location.backgroundMusicVolume=backgroundMusicVolume;
        return location;


    }

    public static ArrayList<Stuff> parseStuff(NodeList stuffs,Location location) {
        ArrayList<Stuff> out = new ArrayList<>(stuffs.getLength());
        for (int i = 0; i < stuffs.getLength(); i++) {
            Node n = stuffs.item(i);
            if (n.getNodeName().equals("#text"))//skip this strange node
                continue;
            String id = n.getNodeName();
            BitmapProvider bitmap = null;
            NodeList children = n.getChildNodes();
            NamedNodeMap atributes = n.getAttributes();
            int x = 0, y = 0, width = 0, height = 0, xToCome = 0, yToCome = 0;
            boolean toComeEnable = false;
            String itemName = null;
            boolean pickup = false;
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
                    bitmap = parseBitmap("item", child);
                } else if (child.getNodeName().equals("pickup"))
                    pickup = true;
                else if (child.getNodeName().equals("toCome")) {
                    toComeEnable = true;
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
            id=location.generateStuffName(id);
            Stuff stuff = null;
            if (toComeEnable) {
                stuff = new StuffToCome(id);
                ((StuffToCome) stuff).setPositionToCome(new Position(xToCome, yToCome));
            }
            if (pickup) {
                if (stuff == null)
                    stuff = new StuffToCome(id);
                //((StuffToCome) stuff).setItem(new ItemStack());
            }
            if (stuff == null)
                stuff = new Stuff(id);

            stuff.setLocationTexture(bitmap);
            stuff.setRectangle(x, y, width, height);
            out.add(stuff);
        }
        return out;
    }

    public static ArrayList<Arrow> parseArrow(NodeList stuffs, ArrayList<Boolean> moverArrows) {
        ArrayList<Arrow> out = new ArrayList<>(stuffs.getLength());
        for (int i = 0; i < stuffs.getLength(); i++) {
            Node n = stuffs.item(i);
            if (n.getNodeName().equals("#text"))//skip this strange node
                continue;
            String id = n.getNodeName();
            String location = null;
            boolean noBitmap = false;
            NodeList children = n.getChildNodes();
            NamedNodeMap atributes = n.getAttributes();
            int x = -10000, y = 0, width = 0, height = 0, xToCome = -10000, yToCome = 0, xFinal = -10000, yFinal = 0, imageX = -10000, imageY = -10000;
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
                    noBitmap = true;
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
            Arrow arrow;
            byte bPos = Arrow.toByte(pos);
            if (xToCome == -10000) {//havent specified joe loc to go -> no mover
                if (x == -10000) {//havent specified dimensions of action rectangle
                    arrow = Arrow.createArrowWithMover(id, bPos, big);
                } else {//dimensions action were specified
                    arrow = new Arrow(id);
                    arrow.setBigPos(big, bPos);
                    arrow.setActionRectangle(x, y, width, height);
                    if (imageX != -10000 && !noBitmap) {
                        arrow.setBitmapLocation(imageX, imageY);
                    } else arrow.setBitmapLocation(x, y);
                }
                arrow.setNoBitmap(noBitmap);

                if (location != null && !location.equals("-")) {
                    LocationLoadEvent event = new LocationLoadEvent(location);
                    if (xFinal != -10000) {
                        event.setJoesLocation(xFinal, yFinal);
                        event.setJoeRightFacing(faceRight);
                    }
                    arrow.setOnClickedEvent(event);
                }
                moverArrows.add(false);
            } else {//mover present
                if (x == -1) {//dimensions were not specified
                    arrow = Arrow.createArrowWithMover(id, bPos, big);
                } else {//dimensions of arrow action were specified
                    arrow = new Arrow(id);
                    arrow.setActionRectangle(x, y, width, height);
                    arrow.setBigPos(big, bPos);
                    if (imageX != -10000) {
                        arrow.setBitmapLocation(imageX, imageY);
                    } else arrow.setBitmapLocation(x, y);
                }
                if (location != null && !location.equals("-")) {
                    arrow.setLocation(location);
                    arrow.setJoeLocationToCome(xToCome, yToCome);
                    arrow.setJoeFinalLocation(xFinal, yFinal);
                    arrow.setJoeFinalRight(faceRight);
                }
                arrow.setNoBitmap(noBitmap);
                moverArrows.add(true);
            }
            out.add(arrow);
        }
        return out;
    }

    /**
     * @param currentFolder
     * @param node
     * @return bitmapStack or animation or bitmapstack
     */
    public static BitmapProvider parseBitmap(String currentFolder, Node node) {
        Bitmap bitmap = Bitmap.getIfExists(currentFolder + "/" + node.getTextContent());
        BitmapStack stack = null;
        int maxdelay = -1;
        int type = 0;
        if (bitmap == null) {//try animation
            try {
                stack = BitmapStack.getBitmapStackFromFolder(currentFolder + "/" + node.getTextContent());

            } catch (Exception ignored) {
                ignored.printStackTrace();
                new Exception("NonExistingBitmap or stack or animation file: " + currentFolder + "/" + node.getTextContent()).printStackTrace();
            }
        }
        NamedNodeMap map = node.getAttributes();
        int xOffset = 0, yOffset = 0;

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

                    if (stack != null) {
                        BitmapProvider[] bitmaps = stack.getAllBitmaps();
                        Bitmap[] out = new Bitmap[bitmaps.length];
                        for (int i1 = 0; i1 < bitmaps.length; i1++) {
                            BitmapProvider provider = bitmaps[i1];
                            Bitmap b = (Bitmap) provider;
                            out[i1] = b.resize(d);
                        }
                        stack = new BitmapStack(out);
                    } else
                        bitmap = bitmap.resize(d);

                    break;
                case "x":
                    xOffset = Integer.parseInt(atribute.getNodeValue());
                    break;
                case "y":
                    yOffset = Integer.parseInt(atribute.getNodeValue());
                    break;
                case "delay":
                    maxdelay = Integer.parseInt(atribute.getNodeValue());
                    break;
                case "saw":
                    type = atribute.getNodeValue().equals("SAW") ? 0 : 1;
                    break;
            }
        }
        if (stack != null) {
            stack.setOffset(xOffset, yOffset);
            if (maxdelay != -1)
                return new Animation(stack, maxdelay, type);
            return stack;
        }
        bitmap.setOffset(xOffset, yOffset);
        return bitmap;
    }

}
