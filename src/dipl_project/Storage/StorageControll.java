/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dipl_project.Storage;

import TrafficLights.TrafficLight;
import dipl_project.Dipl_project;
import dipl_project.Roads.Connect;
import dipl_project.Roads.MyCurve;
import dipl_project.UI.DrawControll;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 *
 * @author Honza
 */
public  class StorageControll {
    public StorageControll()
    {
        
    }
    public void saver(File file)
    {
        DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
        DocumentBuilder db;
        try {
            db = dbf.newDocumentBuilder();
            Document doc=db.newDocument();
            Element root=doc.createElement("root");
            doc.appendChild(root);
            
            new BackgroundStore(doc, root).saveBackground();
            new StreetStore(doc, root).saveStreet();
            new TrafficLightsStore(doc, root).saveTrafficLights();
            
            TransformerFactory tfc=TransformerFactory.newInstance();
            Transformer tf=tfc.newTransformer();
            tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            tf.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source=new DOMSource(doc);
            StreamResult result=new StreamResult(file);
            tf.transform(source, result);
        } catch (ParserConfigurationException | TransformerException ex) {
            Logger.getLogger(StorageControll.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void saveFile()
    {
        
        FileChooser fch=new FileChooser();
        fch.setInitialDirectory(new File(System.getProperty("user.home")+"/Downloads"));
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("XML soubory (*.xml)", "*.xml");
        fch.getExtensionFilters().add(filter);
        File file = fch.showSaveDialog(null);
        if (file != null) { 
            saver(file);
            //saver(dc.getCurves(), dc.getConnects(), lc.getLights(), pc.getPolices(), file, dc.getBgSource(), dc.getBG(), dc.getStartTram(), dc.getStartCar(), cc.getGenerDeleyCar(), cc.getGenerDeleyTram(), cc.isCarGeneratorRun(), cc.isTramGeneratorRun(), pc.getRunPolice(), lc.getRunLights());
        }
    }
    
    public void loader(File input)
    {
        try {
            DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
            DocumentBuilder db=dbf.newDocumentBuilder();
            Document doc;
            doc = db.parse(input);
            doc.getDocumentElement().normalize();
            
            Dipl_project.getDC().cleanAll();
            Map<Integer, TrafficLight> tls = new TrafficLightsStore(doc, null).loadTrafficLights();
            new StreetStore(doc, null).loadStreet(tls);
            new BackgroundStore(doc, null).loadBackground();
            
            
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            throw new Error(ex);
        } 
    }
    
    public void loadFile()
    {
        
        FileChooser fch=new FileChooser();
        fch.setInitialDirectory(new File(System.getProperty("user.home")+"/Downloads"));
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("XML soubory (*.xml)", "*.xml");
        fch.getExtensionFilters().add(filter);
        File file = fch.showOpenDialog(null);
        if (file != null) { 
            loader(file);
        } 
    }
}
