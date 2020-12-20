/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dipl_project.Storage;

import dipl_project.TrafficLights.*;
import dipl_project.Dipl_project;
import dipl_project.Roads.Connect;
import dipl_project.UI.UIControlls.BackgroundControll;
import dipl_project.UI.UIControlls.DrawControll;
import dipl_project.UI.UIControlls.UIControll;
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
            
            Attr tlPosition=doc.createAttribute("tlPosition");
            tlPosition.setValue(String.valueOf(String.valueOf((int)trafficLight.getPosition().getX()+","+(int)trafficLight.getPosition().getY())));
            tl.setAttributeNode(tlPosition);
            
            Attr tlStatus=doc.createAttribute("tlStatus");
            tlStatus.setValue(String.valueOf(trafficLight.getStatus()));
            tl.setAttributeNode(tlStatus);
            
            Attr orangeSwitching=doc.createAttribute("orangeSwitching");
            orangeSwitching.setValue(String.valueOf(trafficLight.isOrangeSwitching()));
            tl.setAttributeNode(orangeSwitching);
            
            root.appendChild(tl);
        }
       saveGroups();
        
    }
    private void saveGroups()
    {
         TrafficLightsControll tlc=Dipl_project.getTlc();
        List<TrafficLightsGroup> tlGroups = tlc.getTlsGroups();
        for (TrafficLightsGroup tlGroup : tlGroups) {
            
            Element tlg=doc.createElement("trafficLightGroup"); 
            Attr idTLG=doc.createAttribute("idTLG");
            idTLG.setValue(String.valueOf(tlGroup.getId()));
            tlg.setAttributeNode(idTLG);
            
            
            Attr timeTLG=doc.createAttribute("timeTLG");
            timeTLG.setValue(String.valueOf(tlGroup.getTime()));
            tlg.setAttributeNode(timeTLG);
            
            
            
            List<TrafficLightSwitch> tlss=tlGroup.getTrafficLightSwitchList();
            for (TrafficLightSwitch tls : tlss) {
                Element tlSwitch=doc.createElement("tlSwitch"); 
                Attr tlTLid=doc.createAttribute("tlTLid");
                tlTLid.setValue(String.valueOf(tls.getTrafficLight().getId()));
                tlSwitch.setAttributeNode(tlTLid);
                
                Attr tlsNewStatus=doc.createAttribute("tlsNewStatus");
                tlsNewStatus.setValue(String.valueOf(tls.getNewStatus()));
                tlSwitch.setAttributeNode(tlsNewStatus);
                
                Attr tlsSwitchTime=doc.createAttribute("tlsSwitchTime");
                tlsSwitchTime.setValue(String.valueOf(tls.getSwitchTime()));
                tlSwitch.setAttributeNode(tlsSwitchTime);
                
                tlg.appendChild(tlSwitch);
            }
            root.appendChild(tlg);
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
            int status=Integer.parseInt(trafficLight.getAttributes().getNamedItem("tlStatus").getNodeValue());
            TrafficLight newTL=new TrafficLight(pp.getX(), pp.getY(), idTL);
            newTL.setStatus(status);
                
            boolean orangeSwitching=Boolean.parseBoolean(trafficLight.getAttributes().getNamedItem("orangeSwitching").getNodeValue());
            newTL.setOrangeSwitching(orangeSwitching);
       
            ui.addTL(newTL);
            trafficLights.put(idTL, newTL);
            Dipl_project.getDC().addTrafficLight(newTL);
        }
        loadTLGroups();
        dc.setIdLastTL(maxId);
    }
    private void loadTLGroups()
    {
        NodeList tlGroups=doc.getElementsByTagName("trafficLightGroup");
        TrafficLightsControll tlc=Dipl_project.getTlc();
        
        tlc.getTlsGroups().clear();
        tlc.getTrafficLightsGroups().getItems().clear();
        tlc.initAddNewGroup();
        for (int i = 0; i < tlGroups.getLength(); i++) {
            Node tlGroup=tlGroups.item(i); 
            int idTLG=Integer.parseInt(tlGroup.getAttributes().getNamedItem("idTLG").getNodeValue());
            int timeTLG=Integer.parseInt(tlGroup.getAttributes().getNamedItem("timeTLG").getNodeValue()); 
            TrafficLightsGroup tlg=new TrafficLightsGroup(idTLG, timeTLG);
            
            NodeList tlss=((Element)tlGroup).getElementsByTagName("tlSwitch");
            for (int j = 0; j < tlss.getLength(); j++) {
                Node tls=tlss.item(j); 
                int tlTLid=Integer.parseInt(tls.getAttributes().getNamedItem("tlTLid").getNodeValue());
                
                int tlsNewStatus=Integer.parseInt(tls.getAttributes().getNamedItem("tlsNewStatus").getNodeValue());
                int tlsSwitchTime=Integer.parseInt(tls.getAttributes().getNamedItem("tlsSwitchTime").getNodeValue());
                TrafficLightSwitch newTLS=new TrafficLightSwitch(tlsSwitchTime, trafficLights.get(tlTLid), tlg);
                newTLS.setNewStatus(tlsNewStatus);
                tlg.addTrafficLightSwitch(newTLS);
            }
            tlc.addTLGroup(i, tlg);
        }
    }

}
