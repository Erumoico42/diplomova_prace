/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dipl_project.Storage;

import TrafficLights.TrafficLight;
import dipl_project.Dipl_project;
import dipl_project.Roads.Connect;
import dipl_project.UI.BackgroundControll;
import dipl_project.UI.DrawControll;
import dipl_project.UI.UIControll;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.CubicCurve;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Honza
 */
public class TrafficLightsStore {
    private UIControll ui=Dipl_project.getUI();
    private DrawControll dc= Dipl_project.getDC();
    private final Document doc;
    private final Element root;
    private Map<Integer, TrafficLight> trafficLights;
     public TrafficLightsStore(Document doc, Element root) {
        this.doc=doc;
        this.root=root;
    }
    public Map<Integer, TrafficLight> loadTrafficLights()
    {
        loadTLS();
        setTLs();
        return trafficLights;
    }
    public void saveTrafficLights()
    {
        List<TrafficLight>trafficLights = Dipl_project.getDC().getTrafficLights();
        for (TrafficLight trafficLight : trafficLights) {
            Element tl=doc.createElement("trafficLight"); 
            Attr idTL=doc.createAttribute("idTL");
            idTL.setValue(String.valueOf(trafficLight.getId()));
            tl.setAttributeNode(idTL);
            
            
            
            root.appendChild(tl);
        }
        
        
    }
    public void loadTLS()
    {
        int maxId=0;
        trafficLights=new HashMap<>();
        NodeList tls=doc.getElementsByTagName("trafficLight");
        for (int i = 0; i < tls.getLength(); i++) {
            Node trafficLight=tls.item(i);           
            int idTL=Integer.parseInt(trafficLight.getAttributes().getNamedItem("idTL").getNodeValue());
            String p=trafficLight.getAttributes().getNamedItem("tlPosition").getNodeValue();
            String[] s=p.split(",");
            Point pp=new Point(Integer.parseInt(s[0]),Integer.parseInt(s[1]));
            if(idTL>=maxId)
                maxId=idTL+1;
            
            TrafficLight newTL=new TrafficLight(pp.getX(), pp.getY(), idTL);
            int timeToSwitchGreen=Integer.parseInt(trafficLight.getAttributes().getNamedItem("timeToSwitchGreen").getNodeValue());
            int timeToSwitchOrange=Integer.parseInt(trafficLight.getAttributes().getNamedItem("timeToSwitchOrange").getNodeValue());
            int timeToSwitchRed=Integer.parseInt(trafficLight.getAttributes().getNamedItem("timeToSwitchRed").getNodeValue());
            int tlTime=Integer.parseInt(trafficLight.getAttributes().getNamedItem("tlTime").getNodeValue());
            int tlStatus=Integer.parseInt(trafficLight.getAttributes().getNamedItem("tlStatus").getNodeValue());
            boolean tlSwitchGreen=Boolean.parseBoolean(trafficLight.getAttributes().getNamedItem("tlSwitchGreen").getNodeValue());
            boolean tlSwitchOrange=Boolean.parseBoolean(trafficLight.getAttributes().getNamedItem("tlSwitchOrange").getNodeValue());
            boolean tlSwitchRed=Boolean.parseBoolean(trafficLight.getAttributes().getNamedItem("tlSwitchRed").getNodeValue());
            
            ui.addComponents(newTL.getTlImage());
            trafficLights.put(idTL, newTL);
            Dipl_project.getDC().addTrafficLight(newTL);
        }
        dc.setIdLastTL(maxId);
    }
    private void setTLs()
    {
        NodeList tls=doc.getElementsByTagName("trafficLight");
        for (int i = 0; i < tls.getLength(); i++) {

        }
    
    }
}
