/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dipl_project.Storage;

import TrafficLights.TrafficLight;
import TrafficLights.TrafficLightsConnection;
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
            Attr timeToSwitchGreen=doc.createAttribute("timeToSwitchGreen");
            timeToSwitchGreen.setValue(String.valueOf(trafficLight.getTimeToSwitchGreen()));
            tl.setAttributeNode(timeToSwitchGreen);
            Attr timeToSwitchOrange=doc.createAttribute("timeToSwitchOrange");
            timeToSwitchOrange.setValue(String.valueOf(trafficLight.getTimeToSwitchOrange()));
            tl.setAttributeNode(timeToSwitchOrange);
            Attr timeToSwitchRed=doc.createAttribute("timeToSwitchRed");
            timeToSwitchRed.setValue(String.valueOf(trafficLight.getTimeToSwitchRed()));
            tl.setAttributeNode(timeToSwitchRed);
            Attr tlTime=doc.createAttribute("tlTime");
            tlTime.setValue(String.valueOf(trafficLight.getTime()));
            tl.setAttributeNode(tlTime);
            
            Attr tlPosition=doc.createAttribute("tlPosition");
            tlPosition.setValue(String.valueOf((int)(trafficLight.getPosition().getX()+trafficLight.getTlImage().getWidth()/2)+","
                    +(int)(trafficLight.getPosition().getY()+trafficLight.getTlImage().getHeight()/2)));
            tl.setAttributeNode(tlPosition);
            
            Attr tlStatus=doc.createAttribute("tlStatus");
            tlStatus.setValue(String.valueOf(trafficLight.getStatus()));
            tl.setAttributeNode(tlStatus);
            
            Attr tlSwitchGreen=doc.createAttribute("tlSwitchGreen");
            tlSwitchGreen.setValue(String.valueOf(trafficLight.isEnableSwitchGreen()));
            tl.setAttributeNode(tlSwitchGreen);
            
            Attr tlSwitchOrange=doc.createAttribute("tlSwitchOrange");
            tlSwitchOrange.setValue(String.valueOf(trafficLight.isEnableSwitchOrange()));
            tl.setAttributeNode(tlSwitchOrange);
            
            Attr tlSwitchRed=doc.createAttribute("tlSwitchRed");
            tlSwitchRed.setValue(String.valueOf(trafficLight.isEnableSwitchRed()));
            tl.setAttributeNode(tlSwitchRed);
            
            for (TrafficLightsConnection connGreen : trafficLight.getConnectionsGreen()) {
                Element tlConnGreen=doc.createElement("tlConnGreen");
                Attr connStatus=doc.createAttribute("connStatus");
                connStatus.setValue(String.valueOf(connGreen.getStatus()));
                tlConnGreen.setAttributeNode(connStatus);
                Attr connStartStatus=doc.createAttribute("connStartStatus");
                connStartStatus.setValue(String.valueOf(connGreen.getStartStatus()));
                tlConnGreen.setAttributeNode(connStartStatus);
                Attr idTlRev=doc.createAttribute("idTlRev");
                idTlRev.setValue(String.valueOf(connGreen.getTl().getId()));
                tlConnGreen.setAttributeNode(idTlRev);
                Attr connSwitchDelay=doc.createAttribute("connSwitchDelay");
                connSwitchDelay.setValue(String.valueOf(connGreen.getSwitchDelay()));
                tlConnGreen.setAttributeNode(connSwitchDelay);
                
                Attr conStartPos=doc.createAttribute("conStartPos");
                conStartPos.setValue(String.valueOf((int)connGreen.getConnectCurve().getStartX()+","+(int)connGreen.getConnectCurve().getStartY()));
                tlConnGreen.setAttributeNode(conStartPos);
                
                Attr conEndPos=doc.createAttribute("conEndPos");
                conEndPos.setValue(String.valueOf((int)connGreen.getConnectCurve().getEndX()+","+(int)connGreen.getConnectCurve().getEndY()));
                tlConnGreen.setAttributeNode(conEndPos);
                tl.appendChild(tlConnGreen);
            }   
            
            for (TrafficLightsConnection connOrange : trafficLight.getConnectionsOrange()) {
                Element tlConnOrange=doc.createElement("tlConnOrange");
                Attr connStatus=doc.createAttribute("connStatus");
                connStatus.setValue(String.valueOf(connOrange.getStatus()));
                tlConnOrange.setAttributeNode(connStatus);
                Attr connStartStatus=doc.createAttribute("connStartStatus");
                connStartStatus.setValue(String.valueOf(connOrange.getStartStatus()));
                tlConnOrange.setAttributeNode(connStartStatus);
                Attr idTlRev=doc.createAttribute("idTlRev");
                idTlRev.setValue(String.valueOf(connOrange.getTl().getId()));
                tlConnOrange.setAttributeNode(idTlRev);
                Attr connSwitchDelay=doc.createAttribute("connSwitchDelay");
                connSwitchDelay.setValue(String.valueOf(connOrange.getSwitchDelay()));
                tlConnOrange.setAttributeNode(connSwitchDelay);
                
                Attr conStartPos=doc.createAttribute("conStartPos");
                conStartPos.setValue(String.valueOf((int)connOrange.getConnectCurve().getStartX()+","+(int)connOrange.getConnectCurve().getStartY()));
                tlConnOrange.setAttributeNode(conStartPos);
                
                Attr conEndPos=doc.createAttribute("conEndPos");
                conEndPos.setValue(String.valueOf((int)connOrange.getConnectCurve().getEndX()+","+(int)connOrange.getConnectCurve().getEndY()));
                tlConnOrange.setAttributeNode(conEndPos);
                tl.appendChild(tlConnOrange);
            }
            
            for (TrafficLightsConnection connRed : trafficLight.getConnectionsRed()) {
                Element tlConnRed=doc.createElement("tlConnRed");
                Attr connStatus=doc.createAttribute("connStatus");
                connStatus.setValue(String.valueOf(connRed.getStatus()));
                tlConnRed.setAttributeNode(connStatus);
                Attr connStartStatus=doc.createAttribute("connStartStatus");
                connStartStatus.setValue(String.valueOf(connRed.getStartStatus()));
                tlConnRed.setAttributeNode(connStartStatus);
                Attr idTlRev=doc.createAttribute("idTlRev");
                idTlRev.setValue(String.valueOf(connRed.getTl().getId()));
                tlConnRed.setAttributeNode(idTlRev);
                Attr connSwitchDelay=doc.createAttribute("connSwitchDelay");
                connSwitchDelay.setValue(String.valueOf(connRed.getSwitchDelay()));
                tlConnRed.setAttributeNode(connSwitchDelay);
                
                Attr conStartPos=doc.createAttribute("conStartPos");
                conStartPos.setValue(String.valueOf((int)connRed.getConnectCurve().getStartX()+","+(int)connRed.getConnectCurve().getStartY()));
                tlConnRed.setAttributeNode(conStartPos);
                
                Attr conEndPos=doc.createAttribute("conEndPos");
                conEndPos.setValue(String.valueOf((int)connRed.getConnectCurve().getEndX()+","+(int)connRed.getConnectCurve().getEndY()));
                tlConnRed.setAttributeNode(conEndPos);
                tl.appendChild(tlConnRed);
            }
            
            
            //rev connections
            for (TrafficLightsConnection revConnGreen : trafficLight.getRevConnectionsGreen()) {
                Element tlRevConnGreen=doc.createElement("tlRevConnGreen");
                Attr connStatus=doc.createAttribute("connStatus");
                connStatus.setValue(String.valueOf(revConnGreen.getStatus()));
                tlRevConnGreen.setAttributeNode(connStatus);
                Attr idTlRev=doc.createAttribute("idTlRev");
                idTlRev.setValue(String.valueOf(revConnGreen.getTlRev().getId()));
                tlRevConnGreen.setAttributeNode(idTlRev);
                Attr connSwitchDelay=doc.createAttribute("connSwitchDelay");
                connSwitchDelay.setValue(String.valueOf(revConnGreen.getSwitchDelay()));
                tlRevConnGreen.setAttributeNode(connSwitchDelay);
                tl.appendChild(tlRevConnGreen);
            }
            
            for (TrafficLightsConnection revConnOrange : trafficLight.getRevConnectionsOrange()) {
                Element tlRevConnOrange=doc.createElement("tlRevConnOrange");
                Attr connStatus=doc.createAttribute("connStatus");
                connStatus.setValue(String.valueOf(revConnOrange.getStatus()));
                tlRevConnOrange.setAttributeNode(connStatus);
                Attr idTlRev=doc.createAttribute("idTlRev");
                idTlRev.setValue(String.valueOf(revConnOrange.getTlRev().getId()));
                tlRevConnOrange.setAttributeNode(idTlRev);
                Attr connSwitchDelay=doc.createAttribute("connSwitchDelay");
                connSwitchDelay.setValue(String.valueOf(revConnOrange.getSwitchDelay()));
                tlRevConnOrange.setAttributeNode(connSwitchDelay);
                tl.appendChild(tlRevConnOrange);
            }
            
            for (TrafficLightsConnection revConnRed : trafficLight.getRevConnectionsRed()) {
                Element tlRevConnRed=doc.createElement("tlRevConnRed");
                Attr connStatus=doc.createAttribute("connStatus");
                connStatus.setValue(String.valueOf(revConnRed.getStatus()));
                tlRevConnRed.setAttributeNode(connStatus);
                Attr idTlRev=doc.createAttribute("idTlRev");
                idTlRev.setValue(String.valueOf(revConnRed.getTlRev().getId()));
                tlRevConnRed.setAttributeNode(idTlRev);
                Attr connSwitchDelay=doc.createAttribute("connSwitchDelay");
                connSwitchDelay.setValue(String.valueOf(revConnRed.getSwitchDelay()));
                tlRevConnRed.setAttributeNode(connSwitchDelay);
                tl.appendChild(tlRevConnRed);
            }
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
            newTL.setTimeToSwitchGreen(timeToSwitchGreen);
            newTL.setTimeToSwitchOrange(timeToSwitchOrange);
            newTL.setTimeToSwitchRed(timeToSwitchRed);
            newTL.setTime(tlTime);
            newTL.setStatus(tlStatus, false);
            newTL.setEnableSwitchGreen(tlSwitchGreen);
            newTL.setEnableSwitchOrange(tlSwitchOrange);
            newTL.setEnableSwitchRed(tlSwitchRed);
            
            ui.addComponents(newTL.getTlImage(), newTL.getCircleRed(), 
                                        newTL.getCircleOrange(), newTL.getCircleGreen());
            trafficLights.put(idTL, newTL);
            Dipl_project.getDC().addTrafficLight(newTL);
        }
        dc.setIdLastTL(maxId);
    }
    private void setTLs()
    {
        NodeList tls=doc.getElementsByTagName("trafficLight");
        for (int i = 0; i < tls.getLength(); i++) {
            Node trafficLight=tls.item(i); 
            int idTL=Integer.parseInt(trafficLight.getAttributes().getNamedItem("idTL").getNodeValue());
            TrafficLight tlAct=trafficLights.get(idTL);
            NodeList tlConnGreens=((Element)trafficLight).getElementsByTagName("tlConnGreen");
            for (int j = 0; j < tlConnGreens.getLength(); j++) {
                Node tlConnGreen = tlConnGreens.item(j);
                tlAct.addConnectionsGreen(setTLC(tlAct, tlConnGreen));
            }
            NodeList tlConnOranges=((Element)trafficLight).getElementsByTagName("tlConnOrange");
            for (int j = 0; j < tlConnOranges.getLength(); j++) {
                Node tlConnOrange = tlConnOranges.item(j);
                tlAct.addConnectionsOrange(setTLC(tlAct, tlConnOrange));
            }
            NodeList tlConnReds=((Element)trafficLight).getElementsByTagName("tlConnRed");
            for (int j = 0; j < tlConnReds.getLength(); j++) {
                Node tlConnRed = tlConnReds.item(j);
                tlAct.addConnectionsRed(setTLC(tlAct, tlConnRed));
            }
        }
    }
    private TrafficLightsConnection setTLC(TrafficLight tlAct, Node tlConn)
    {
        int connStatus=Integer.parseInt(tlConn.getAttributes().getNamedItem("connStatus").getNodeValue());
        int connStartStatus=Integer.parseInt(tlConn.getAttributes().getNamedItem("connStartStatus").getNodeValue());
        int idTlRev=Integer.parseInt(tlConn.getAttributes().getNamedItem("idTlRev").getNodeValue());
        int connSwitchDelay=Integer.parseInt(tlConn.getAttributes().getNamedItem("connSwitchDelay").getNodeValue());
        CubicCurve conCurve=new CubicCurve();
        conCurve.setFill(null);
        conCurve.setStrokeWidth(2);
        conCurve.setStroke(Color.BLACK);
        TrafficLight tlRev=this.trafficLights.get(idTlRev);
        String p=tlConn.getAttributes().getNamedItem("conStartPos").getNodeValue();
        String[] s=p.split(",");
        Point conStartPos=new Point(Integer.parseInt(s[0]),Integer.parseInt(s[1]));
        p=tlConn.getAttributes().getNamedItem("conEndPos").getNodeValue();
        s=p.split(",");
        Point conEndPos=new Point(Integer.parseInt(s[0]),Integer.parseInt(s[1]));
        conCurve.setStartX(conStartPos.getX());
        conCurve.setStartY(conStartPos.getY());
        conCurve.setEndX(conEndPos.getX());
        conCurve.setEndY(conEndPos.getY());
        tlAct.moveCurveToConnect(conCurve);
        Dipl_project.getUI().addComponentsDown(conCurve);
        TrafficLightsConnection tlcNew=new TrafficLightsConnection(tlRev, tlAct, connStatus, connStartStatus, conCurve);
        tlcNew.setSwitchDelay(connSwitchDelay);
        
        switch(connStartStatus)
        {
            case 2:
            {
                tlRev.addRevConnectionsGreen(tlcNew);
                break;
            }
            case 1:
            {
                tlRev.addRevConnectionsOrange(tlcNew);
                break;
            }
            case 0:
            {
                tlRev.addRevConnectionsRed(tlcNew);
                break;
            }
        }
        return tlcNew;
    }
}
