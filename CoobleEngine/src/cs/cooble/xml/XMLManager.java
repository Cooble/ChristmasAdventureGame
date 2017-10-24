package cs.cooble.xml;

import cs.cooble.core.Game;
import cs.cooble.logger.Log;
import cs.cooble.saving.Saver;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.ArrayList;
import java.util.List;

/**
 * This class handles loading of the xml file
 * It uses saver to get right folder (/xml)
 */
public class XMLManager {
    private String xmlPath;

    public XMLManager(Saver saver) {
        xmlPath = saver.XML_PATH;
    }

    /**
     * @param MID module name to search location in
     * @return all location names in xml/moduleName/
     */
    public List<String> getLocationNames(String MID) {
        List<String> fullNames = Game.saver.findResourceC(Game.saver.XML_PATH + MID + "/", null);
        if (fullNames == null)
            return new ArrayList<>();
        for (int i = 0; i < fullNames.size(); i++) {
            String name = fullNames.get(i);
            if (name.contains(".")) {
                name = name.substring(0, name.lastIndexOf('.'));//removinh .xml
                name = name.substring(name.lastIndexOf('/') + 1);
                fullNames.set(i, name);
            }
        }
        return fullNames;

      /*  File inputFile = Game.saver.getResource(xmlPath + MID + "/");
        File[] files;
        if(inputFile==null)
            return new ArrayList<>(0);
        else {
            files = inputFile.listFiles();
            if(files==null)
                return new ArrayList<>();
        }

        ArrayList<String> locationNames = new ArrayList<>(files.length);
        for (File file : files) {
            //System.out.println("file "+file.getName());
            locationNames.add(file.getName().substring(0,file.getName().lastIndexOf('.')));//removing .xml
        }
        return locationNames;*/
    }

    public Document loadDocument(String upFolder, String fileName) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(Game.saver.getResourceAsStream(Game.saver.makeResString(Game.saver.XML_PATH + upFolder + "/" + fileName + ".xml")));
            doc.getDocumentElement().normalize();
            return doc;
        } catch (Exception ignored) {
            Log.println("Cannot load xml", Log.LogType.ERROR);
            return null;
        }
    }

}
