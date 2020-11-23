/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dipl_project.Storage;

import dipl_project.TrafficLights.TrafficLight;
import dipl_project.Dipl_project;
import dipl_project.Roads.CheckPoint;
import dipl_project.Roads.Connect;
import dipl_project.Roads.MyCurve;
import dipl_project.Roads.RoadCreator;
import dipl_project.Roads.RoadSegment;
import dipl_project.Roads.VehicleGenerating.StartCar;
import dipl_project.Roads.VehicleGenerating.StartSegment;
import dipl_project.Roads.VehicleGenerating.StartTram;
import dipl_project.Roads.WatchPoint;
import dipl_project.UI.DrawControll;
import dipl_project.UI.UIControll;
import java.awt.Point;
import java.rmi.server.UID;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.util.Pair;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Honza
 */
public class StreetStore {
    private Document doc;
    private Element root;
    private UIControll ui=dipl_project.Dipl_project.getUI();
    private DrawControll dc=dipl_project.Dipl_project.getDC();
    private Map<Integer, MyCurve> curves;
    private Map<Integer, Connect> connects;
    private Map<Integer, RoadSegment> segments;
    private Map<Integer, TrafficLight> trafficLights;
    private Map<Integer, CheckPoint> checkPoints;
    private List<RoadSegment> startSegments;
    public StreetStore(Document doc, Element root) {
        this.doc = doc;
        this.root = root;
    }
    public void saveStreet()
    {
        saveConnects();
        saveCurves();
        saveSegments();
        saveStartSegments();
    }
    public void loadStreet(Map<Integer, TrafficLight> tls)
    {
        dc.setLoadingMap(true);
        loadConnects();
        loadSegments();
        loadCurves();
        
        setSegments(tls);
        setRSConnects();
        dc.setLoadingMap(false);
        loadStartSegments();
        Dipl_project.getDC().newRoad();
        //dipl_project.Dipl_project.getRC().setCurves(ui.getCurves());
        Dipl_project.getRC().setArrows();
        
    }
    public void saveConnects()
    {
        List<Connect> connects=Dipl_project.getUI().getConnects();
        for (Connect connect : connects) {
            Element conn=doc.createElement("connect"); 
            
            Attr idConnect=doc.createAttribute("idConnect");
            idConnect.setValue(String.valueOf(connect.getId()));
            conn.setAttributeNode(idConnect);
            Attr position=doc.createAttribute("position");
            position.setValue(String.valueOf((int)connect.getLocation().getX()+","+(int)connect.getLocation().getY()));
            conn.setAttributeNode(position);
            
            for(Map.Entry<Pair<MyCurve, MyCurve>, RoadSegment> rs : connect.getConnectSegmentsMap().entrySet()) {
                RoadSegment segment = rs.getValue();
                Pair<MyCurve, MyCurve> curves=rs.getKey();
                
                Element rsConnect=doc.createElement("rsConnect");
                Attr idRsConnect=doc.createAttribute("idRsConnect");
                idRsConnect.setValue(String.valueOf(segment.getId()));
                rsConnect.setAttributeNode(idRsConnect);
                
                Attr idCurve1=doc.createAttribute("idCurve1");
                idCurve1.setValue(String.valueOf(curves.getKey().getId()));
                rsConnect.setAttributeNode(idCurve1);
                
                Attr idCurve2=doc.createAttribute("idCurve2");
                idCurve2.setValue(String.valueOf(curves.getValue().getId()));
                rsConnect.setAttributeNode(idCurve2);
                
                conn.appendChild(rsConnect);
            }
            root.appendChild(conn);
        }
    }
    public void loadConnects()
    {
        int maxId=0;
        connects=new HashMap<>();
        NodeList conn=doc.getElementsByTagName("connect");
        for (int i = 0; i < conn.getLength(); i++) {
            
            Node con=conn.item(i);           
            int idConn=Integer.parseInt(con.getAttributes().getNamedItem("idConnect").getNodeValue());
            String p=con.getAttributes().getNamedItem("position").getNodeValue();
            String[] s=p.split(",");
            Point pp=new Point(Integer.parseInt(s[0]),Integer.parseInt(s[1]));
            if(idConn>=maxId)
                maxId=idConn+1;
            Connect connect=new Connect(pp, idConn);
            connects.put(idConn, connect);
            ui.addConnect(connect);
            dc.addConnect(connect);
            
            
        }
        dc.setIdLastConnect(maxId);
    }
    private void setRSConnects()
    {
        NodeList conn=doc.getElementsByTagName("connect");
        for (int i = 0; i < conn.getLength(); i++) {
            Node con=conn.item(i);           
            int idConn=Integer.parseInt(con.getAttributes().getNamedItem("idConnect").getNodeValue());
            Connect connect=connects.get(idConn);
            NodeList rsConnects=((Element)con).getElementsByTagName("rsConnect");
            for (int j = 0; j < rsConnects.getLength(); j++) {
                int idRsConnect=Integer.parseInt(rsConnects.item(j).getAttributes().getNamedItem("idRsConnect").getNodeValue());
                int idCurve1=Integer.parseInt(rsConnects.item(j).getAttributes().getNamedItem("idCurve1").getNodeValue());
                int idCurve2=Integer.parseInt(rsConnects.item(j).getAttributes().getNamedItem("idCurve2").getNodeValue());
                RoadSegment segment=segments.get(idRsConnect);
                segment.setVisible(false);
                MyCurve mc1=curves.get(idCurve1);
                MyCurve mc2=curves.get(idCurve2);
                connect.addConnectSegment(segment, mc1, mc2);
            }
        }
    }
    public void saveStartSegments()
    {
        List<StartSegment> startCarSegments=Dipl_project.getUI().getStarCarSegments();
        for (StartSegment ss : startCarSegments) {
            Element startCarSegment=doc.createElement("startCarSegment");  
            setDataStartSegment(startCarSegment,ss);
            root.appendChild(startCarSegment);
        }
        List<StartSegment> startTramSegments=Dipl_project.getUI().getStarTramSegments();
        for (StartSegment ss : startTramSegments) {
            Element startTramSegment=doc.createElement("startTramSegment");  
            setDataStartSegment(startTramSegment,ss);
            root.appendChild(startTramSegment);
        }
    }
    private void setDataStartSegment(Element segment, StartSegment ss)
    {
        Attr idSegment=doc.createAttribute("idStartSegment");
            idSegment.setValue(String.valueOf(ss.getStartRS().getId()));
            segment.setAttributeNode(idSegment);
            Attr idCurve=doc.createAttribute("idCurve");
            idCurve.setValue(String.valueOf(ss.getMc().getId()));
            segment.setAttributeNode(idCurve);
            Attr frequencyMinute=doc.createAttribute("frequencyMinute");
            frequencyMinute.setValue(String.valueOf(ss.getFrequencyMinute()));
            segment.setAttributeNode(frequencyMinute);  
            Attr idConnect=doc.createAttribute("idConnect");
            idConnect.setValue(String.valueOf(ss.getStartConnect().getId()));
            segment.setAttributeNode(idConnect);
            Attr actualFreqChanged=doc.createAttribute("actualFreqChanged");
            actualFreqChanged.setValue(String.valueOf(ss.isFrequencyChangedActual()));
            segment.setAttributeNode(actualFreqChanged);
            
    }
    private void loadStartSegments()
    {
        List<StartSegment> startCarSegments=new ArrayList<>();
        NodeList startCarSegment=doc.getElementsByTagName("startCarSegment");
        for (int i = 0; i < startCarSegment.getLength(); i++) {           
            StartSegment start=loadDataStartSegment(startCarSegment.item(i));
            startCarSegments.add(start);
        }
        
        List<StartSegment> startTramSegments=new ArrayList<>();
        NodeList startTramSegment=doc.getElementsByTagName("startTramSegment");
        for (int i = 0; i < startTramSegment.getLength(); i++) {
            StartSegment start=loadDataStartSegment(startTramSegment.item(i));
            startTramSegments.add(start);
        }
        ui.setStartSegments(startCarSegments,startTramSegments);
    }
    private StartSegment loadDataStartSegment(Node startTramSegment)
    {
            int idStartSegment=Integer.parseInt(startTramSegment.getAttributes().getNamedItem("idStartSegment").getNodeValue());
            int idCurve=Integer.parseInt(startTramSegment.getAttributes().getNamedItem("idCurve").getNodeValue());
            int frequencyMinute=Integer.parseInt(startTramSegment.getAttributes().getNamedItem("frequencyMinute").getNodeValue());
            int idConnect=Integer.parseInt(startTramSegment.getAttributes().getNamedItem("idConnect").getNodeValue());
            boolean actualFreqChanged=Boolean.parseBoolean(startTramSegment.getAttributes().getNamedItem("actualFreqChanged").getNodeValue());
            StartSegment start=new StartCar(segments.get(idStartSegment), frequencyMinute, connects.get(idConnect), curves.get(idCurve));
            start.setFrequencyChangedActual(actualFreqChanged);
            return start;
    }
    public void saveSegments()
    {
        List<RoadSegment> roadSegments=dipl_project.Dipl_project.getUI().getSegments();
        for (RoadSegment rs : roadSegments) {
            Element roadSegment=doc.createElement("roadSegment");   

            Attr idRoadSegment=doc.createAttribute("idRoadSegment");
            idRoadSegment.setValue(String.valueOf(rs.getId()));
            roadSegment.setAttributeNode(idRoadSegment);

            Attr rsP0=doc.createAttribute("rsP0");
            rsP0.setValue(String.valueOf((int)rs.getP0().getX()+","+(int)rs.getP0().getY()));
            roadSegment.setAttributeNode(rsP0);
            
            Attr rsP1=doc.createAttribute("rsP1");
            rsP1.setValue(String.valueOf((int)rs.getP1().getX()+","+(int)rs.getP1().getY()));
            roadSegment.setAttributeNode(rsP1);
            
            Attr rsP2=doc.createAttribute("rsP2");
            rsP2.setValue(String.valueOf((int)rs.getP2().getX()+","+(int)rs.getP2().getY()));
            roadSegment.setAttributeNode(rsP2);
            
            Attr rsP3=doc.createAttribute("rsP3");
            rsP3.setValue(String.valueOf((int)rs.getP3().getX()+","+(int)rs.getP3().getY()));
            roadSegment.setAttributeNode(rsP3);
            
            Attr blinkerLeft=doc.createAttribute("blinkerLeft");
            blinkerLeft.setValue(String.valueOf(rs.isBlinkerLeft()));
            roadSegment.setAttributeNode(blinkerLeft);
            
            Attr blinkerRight=doc.createAttribute("blinkerRight");
            blinkerRight.setValue(String.valueOf(rs.isBlinkerRight()));
            roadSegment.setAttributeNode(blinkerRight);
            
            Attr blinkerStop=doc.createAttribute("blinkerStop");
            blinkerStop.setValue(String.valueOf(rs.isStopBlinker()));
            roadSegment.setAttributeNode(blinkerStop);
            
            Attr segmentLenght=doc.createAttribute("segmentLenght");
            segmentLenght.setValue(String.valueOf((int)rs.getSegmentLenght()));
            roadSegment.setAttributeNode(segmentLenght);
            
            for (TrafficLight tl : rs.getTrafficLights()) {
                Element trl=doc.createElement("tl");
                Attr idTL=doc.createAttribute("idTL");
                idTL.setValue(String.valueOf(tl.getId()));
                trl.setAttributeNode(idTL);
                roadSegment.appendChild(trl);
            }
            for (RoadSegment rsSW : rs.getRsSameWay()) {
                Element rsS=doc.createElement("rsSW");
                Attr idRSSW=doc.createAttribute("idRSSW");
                idRSSW.setValue(String.valueOf(rsSW.getId()));
                rsS.setAttributeNode(idRSSW);
                roadSegment.appendChild(rsS);
            }
            for (RoadSegment rsN : rs.getRsNext()) {
                Element rsNext=doc.createElement("rsNext");
                Attr idRsNext=doc.createAttribute("idRsNext");
                idRsNext.setValue(String.valueOf(rsN.getId()));
                rsNext.setAttributeNode(idRsNext);
                roadSegment.appendChild(rsNext);
            }
            for (RoadSegment rsL : rs.getRsLast()) {
                Element rsLast=doc.createElement("rsLast");
                Attr idRsLast=doc.createAttribute("idRsLast");
                idRsLast.setValue(String.valueOf(rsL.getId()));
                
                rsLast.setAttributeNode(idRsLast);
                roadSegment.appendChild(rsLast);
            }
            for (WatchPoint wp : rs.getWatchPoints()) {
                Element watchPoint=doc.createElement("watchPoint");
                Attr idWPRS=doc.createAttribute("idWPRS");
                idWPRS.setValue(String.valueOf(wp.getRs().getId()));
                watchPoint.setAttributeNode(idWPRS);
                Attr wpDistance=doc.createAttribute("wpDistance");
                wpDistance.setValue(String.valueOf(wp.getDistance()));
                watchPoint.setAttributeNode(wpDistance);
                
                roadSegment.appendChild(watchPoint);
            }
            for (CheckPoint seCP : rs.getSecondaryCheckPoints()) {
                Element secCP=doc.createElement("secCP");
                Attr secCPID=doc.createAttribute("secCPID");
                secCPID.setValue(String.valueOf(seCP.getId()));
                secCP.setAttributeNode(secCPID);
                
                Attr secCPprimRSID=doc.createAttribute("secCPprimRSID");
                secCPprimRSID.setValue(String.valueOf(seCP.getPrim().getId()));
                secCP.setAttributeNode(secCPprimRSID);
                
                Attr secCPrsID=doc.createAttribute("secCPrsID");
                secCPrsID.setValue(String.valueOf(seCP.getRs().getId()));
                secCP.setAttributeNode(secCPrsID);
                
                Attr secCPdist=doc.createAttribute("secCPdist");
                secCPdist.setValue(String.valueOf(seCP.getDistance()));
                secCP.setAttributeNode(secCPdist);
                
                roadSegment.appendChild(secCP);
            }
            
            
            for (CheckPoint checkPoint : rs.getCheckPoints()) {
                Element cp=doc.createElement("checkPoint");
                Attr idCP=doc.createAttribute("idCP");
                idCP.setValue(String.valueOf(checkPoint.getId()));
                cp.setAttributeNode(idCP);
                
                Attr cpRSID=doc.createAttribute("cpRSID");
                cpRSID.setValue(String.valueOf(checkPoint.getRs().getId()));
                cp.setAttributeNode(cpRSID);
                Attr distance=doc.createAttribute("distance");
                distance.setValue(String.valueOf(checkPoint.getDistance()));
                cp.setAttributeNode(distance);
                
                for (CheckPoint seCP:  checkPoint.getSecondaryCP()) {
                    Element cpSecCP=doc.createElement("cpSecCP");
                    Attr secCPID=doc.createAttribute("secCPID");
                    secCPID.setValue(String.valueOf(seCP.getId()));
                    cpSecCP.setAttributeNode(secCPID);
                    
                    Attr secCPprimID=doc.createAttribute("secCPprimID");
                    secCPprimID.setValue(String.valueOf(seCP.getPrim().getId()));
                    cpSecCP.setAttributeNode(secCPprimID);
                    
                    Attr secCPrsID=doc.createAttribute("secCPrsID");
                    secCPrsID.setValue(String.valueOf(seCP.getRs().getId()));
                    cpSecCP.setAttributeNode(secCPrsID);
                    
                    Attr secCPdist=doc.createAttribute("secCPdist");
                    secCPdist.setValue(String.valueOf(seCP.getDistance()));
                    cpSecCP.setAttributeNode(secCPdist);
                    
                    cp.appendChild(cpSecCP);
                }
                Attr enabled=doc.createAttribute("enabled");
                enabled.setValue(String.valueOf(checkPoint.isEnabled()));
                cp.setAttributeNode(enabled);
                roadSegment.appendChild(cp);
            }
            
            root.appendChild(roadSegment);
        }
    }
    private void setSegments(Map<Integer, TrafficLight> tls)
    {
        startSegments=new ArrayList<>();
        NodeList rs=doc.getElementsByTagName("roadSegment");
        for (int i = 0; i < rs.getLength(); i++) {
            Node roadSegment=rs.item(i); 
            int idRoadSegment=Integer.parseInt(roadSegment.getAttributes().getNamedItem("idRoadSegment").getNodeValue());
            RoadSegment newRS=segments.get(idRoadSegment);

            NodeList rsSW=((Element)roadSegment).getElementsByTagName("rsSW");
            for (int j = 0; j < rsSW.getLength(); j++) {
                int idRSSW=Integer.parseInt(rsSW.item(j).getAttributes().getNamedItem("idRSSW").getNodeValue());
                newRS.addRsSameWay(segments.get(idRSSW));
            }
            NodeList rsNext=((Element)roadSegment).getElementsByTagName("rsNext");
            for (int j = 0; j < rsNext.getLength(); j++) {
                int idRSNext=Integer.parseInt(rsNext.item(j).getAttributes().getNamedItem("idRsNext").getNodeValue());
                newRS.addNextRs(segments.get(idRSNext));
            }
            NodeList rsLast=((Element)roadSegment).getElementsByTagName("rsLast");
            for (int j = 0; j < rsLast.getLength(); j++) {
                int idRsLast=Integer.parseInt(rsLast.item(j).getAttributes().getNamedItem("idRsLast").getNodeValue());
                if(segments.get(idRsLast)==null)
                    newRS.setSideRoadSegment();
                newRS.addLastRs(segments.get(idRsLast));
            }
            

            NodeList wps=((Element)roadSegment).getElementsByTagName("watchPoint");
            for (int j = 0; j < wps.getLength(); j++) {
                
                int idWPRS=Integer.parseInt(wps.item(j).getAttributes().getNamedItem("idWPRS").getNodeValue());
                int wpDistance=Integer.parseInt(wps.item(j).getAttributes().getNamedItem("wpDistance").getNodeValue());
                WatchPoint newWP=new WatchPoint(segments.get(idRoadSegment), segments.get(idWPRS), wpDistance);
                segments.get(idRoadSegment).addWP(newWP);
            }
            NodeList trl=((Element)roadSegment).getElementsByTagName("tl");
            for (int j = 0; j < trl.getLength(); j++) {
                int idTL=Integer.parseInt(trl.item(j).getAttributes().getNamedItem("idTL").getNodeValue());
                newRS.addTrafficLight(tls.get(idTL));
            }
            NodeList secCP=((Element)roadSegment).getElementsByTagName("secCP");
            for (int j = 0; j < secCP.getLength(); j++) {
                Node secCPP=secCP.item(j);
                int secCPID=Integer.parseInt(secCPP.getAttributes().getNamedItem("secCPID").getNodeValue());
               
                
                int secCPrsID=Integer.parseInt(secCPP.getAttributes().getNamedItem("secCPrsID").getNodeValue());
                
                int secCPdist=Integer.parseInt(secCPP.getAttributes().getNamedItem("secCPdist").getNodeValue());
                
                CheckPoint newSecCP = new CheckPoint(newRS, segments.get(secCPrsID),secCPdist);
                newSecCP.setId(secCPID);;
                newRS.addSecondaryCheckPoints(newSecCP);
            }
            
            NodeList cp=((Element)roadSegment).getElementsByTagName("checkPoint");
            for (int j = 0; j < cp.getLength(); j++) {
                Node cpp=cp.item(j);
                int idCP=Integer.parseInt(cpp.getAttributes().getNamedItem("idCP").getNodeValue());
                
                int cpRSID=Integer.parseInt(cpp.getAttributes().getNamedItem("cpRSID").getNodeValue());
                
                int distance=Integer.parseInt(cpp.getAttributes().getNamedItem("distance").getNodeValue());
                boolean enabled=Boolean.parseBoolean(cpp.getAttributes().getNamedItem("enabled").getNodeValue());
                
                CheckPoint newCP=new CheckPoint(newRS,segments.get(cpRSID), distance);
                newCP.setId(idCP);
                
                NodeList cpSecCP=((Element)cpp).getElementsByTagName("cpSecCP");
                for (int k = 0; k < cpSecCP.getLength(); k++) {
                    int secCPID=Integer.parseInt(cpSecCP.item(k).getAttributes().getNamedItem("secCPID").getNodeValue());
                    int secCPprimID=Integer.parseInt(cpSecCP.item(k).getAttributes().getNamedItem("secCPprimID").getNodeValue());
                    int secCPrsID=Integer.parseInt(cpSecCP.item(k).getAttributes().getNamedItem("secCPrsID").getNodeValue());
                    int secCPdist=Integer.parseInt(cpSecCP.item(k).getAttributes().getNamedItem("secCPdist").getNodeValue());
                    CheckPoint newCPP=new CheckPoint(segments.get(secCPprimID), segments.get(secCPrsID),secCPdist);
                    newCPP.setId(secCPID);
                    newCP.addSecondaryCP(newCPP);
                }
                newRS.addCP(newCP);
                newCP.setEnabled(enabled);
            }
            
        }
        
    }
    public void loadSegments()
    {
        int idMax=0;
        segments=new HashMap<>();
        NodeList rs=doc.getElementsByTagName("roadSegment");
        for (int i = 0; i < rs.getLength(); i++) {
            Node roadSegment=rs.item(i); 
            int idRoadSegment=Integer.parseInt(roadSegment.getAttributes().getNamedItem("idRoadSegment").getNodeValue());
            
            String p;
            p=roadSegment.getAttributes().getNamedItem("rsP0").getNodeValue();
            String[] s0=p.split(",");
            Point p0=new Point(Integer.parseInt(s0[0]),Integer.parseInt(s0[1]));
            
            p=roadSegment.getAttributes().getNamedItem("rsP1").getNodeValue();
            String[] s1=p.split(",");
            Point p1=new Point(Integer.parseInt(s1[0]),Integer.parseInt(s1[1]));
            
            p=roadSegment.getAttributes().getNamedItem("rsP2").getNodeValue();
            String[] s2=p.split(",");
            Point p2=new Point(Integer.parseInt(s2[0]),Integer.parseInt(s2[1]));
            
            p=roadSegment.getAttributes().getNamedItem("rsP3").getNodeValue();
            String[] s3=p.split(",");
            Point p3=new Point(Integer.parseInt(s3[0]),Integer.parseInt(s3[1]));
            
            int segmentLenght=Integer.parseInt(roadSegment.getAttributes().getNamedItem("segmentLenght").getNodeValue());
            
            boolean blinkerLeft=Boolean.parseBoolean(roadSegment.getAttributes().getNamedItem("blinkerLeft").getNodeValue());
            boolean blinkerRight=Boolean.parseBoolean(roadSegment.getAttributes().getNamedItem("blinkerRight").getNodeValue());
            boolean blinkerStop=Boolean.parseBoolean(roadSegment.getAttributes().getNamedItem("blinkerStop").getNodeValue());
            RoadSegment rsNew=new RoadSegment(p0, p3);
            rsNew.setId(idRoadSegment);
            rsNew.setP1(p1);
            rsNew.setP2(p2);
            rsNew.setSegmentLenght(segmentLenght);
            rsNew.setBlinkerLeft(blinkerLeft);
            rsNew.setBlinkerRight(blinkerRight);
            rsNew.setStopBlinker(blinkerStop);
            segments.put(idRoadSegment, rsNew);
            if(idRoadSegment>=idMax)
                idMax=idRoadSegment+1;
        }
        Dipl_project.getRC().setId(idMax+1);
        
        
        
    }
    public void saveCurves()
    {
        List<MyCurve> curves=dipl_project.Dipl_project.getUI().getCurves();
        for (MyCurve mc : curves) {
            Element curve=doc.createElement("curve");   

            Attr idCurve=doc.createAttribute("idCurve");
            idCurve.setValue(String.valueOf(mc.getId()));
            curve.setAttributeNode(idCurve);

            Attr idConn0=doc.createAttribute("idConnStart");
            idConn0.setValue(String.valueOf(mc.getStartConnect().getId()));
            curve.setAttributeNode(idConn0);

            Attr idConn3=doc.createAttribute("idConnEnd");
            idConn3.setValue(String.valueOf(mc.getEndConnect().getId()));
            curve.setAttributeNode(idConn3);

            Attr p0=doc.createAttribute("p0");
            p0.setValue(String.valueOf((int)mc.getCurve().getStartX()+","+(int)mc.getCurve().getStartY()));
            curve.setAttributeNode(p0);

            Attr p1=doc.createAttribute("p1");
            p1.setValue(String.valueOf((int)mc.getCurve().getControlX1()+","+(int)mc.getCurve().getControlY1()));
            curve.setAttributeNode(p1);

            Attr p2=doc.createAttribute("p2");
            p2.setValue(String.valueOf((int)mc.getCurve().getControlX2()+","+(int)mc.getCurve().getControlY2()));
            curve.setAttributeNode(p2);

            Attr p3=doc.createAttribute("p3");
            p3.setValue(String.valueOf((int)mc.getCurve().getEndX()+","+(int)mc.getCurve().getEndY()));
            curve.setAttributeNode(p3);
            root.appendChild(curve);
            
            Attr tramCurve=doc.createAttribute("tramCurve");
            tramCurve.setValue(String.valueOf(mc.isTramCurve()));
            curve.setAttributeNode(tramCurve);
            root.appendChild(curve);
            
            Attr curveLenght=doc.createAttribute("curveLenght");
            curveLenght.setValue(String.valueOf((int)mc.getCurveLenght()));
            curve.setAttributeNode(curveLenght);
            
            for (RoadSegment segment : mc.getCurveSegments()) {
                Element subSegment=doc.createElement("subSegment");
                Attr idSubSegment=doc.createAttribute("idSubSegment");
                idSubSegment.setValue(String.valueOf(segment.getId()));
                subSegment.setAttributeNode(idSubSegment);
                curve.appendChild(subSegment);
            }
            root.appendChild(curve);
        }
    }
     private void loadCurves()
     {
         int maxId=0;
        curves=new HashMap<>();
        NodeList curv=doc.getElementsByTagName("curve");
        for (int i = 0; i < curv.getLength(); i++) {
            Node curve=curv.item(i); 

            int conn0Id=Integer.parseInt(curve.getAttributes().getNamedItem("idConnStart").getNodeValue());
            int conn3Id=Integer.parseInt(curve.getAttributes().getNamedItem("idConnEnd").getNodeValue());

            String p;

            p=curve.getAttributes().getNamedItem("p1").getNodeValue();
            String[] s1=p.split(",");
            Point p1=new Point(Integer.parseInt(s1[0]),Integer.parseInt(s1[1]));

            p=curve.getAttributes().getNamedItem("p2").getNodeValue();
            String[] s2=p.split(",");
            Point p2=new Point(Integer.parseInt(s2[0]),Integer.parseInt(s2[1]));

            Connect conn0=connects.get(conn0Id);

            Connect conn3=connects.get(conn3Id);

            int idCurve=Integer.parseInt(curve.getAttributes().getNamedItem("idCurve").getNodeValue());
            if(idCurve>=maxId)
                maxId=idCurve+1;
            int curveLenght=Integer.parseInt(curve.getAttributes().getNamedItem("curveLenght").getNodeValue());
            boolean tramCurve=Boolean.parseBoolean(curve.getAttributes().getNamedItem("tramCurve").getNodeValue());
            MyCurve mc=new MyCurve(conn0,conn3, idCurve);
            conn0.addStartCurves(mc);
            conn3.addEndCurves(mc);
            if(tramCurve)
               mc.setTramCurve();
            mc.moveStartControll(p1.getX(), p1.getY());
            mc.moveEndControll(p2.getX(), p2.getY());
            mc.setCurveLenght(curveLenght);
            NodeList subSegment=((Element)curve).getElementsByTagName("subSegment");
            for (int j = 0; j < subSegment.getLength(); j++) {
                int idSubSegment=Integer.parseInt(subSegment.item(j).getAttributes().getNamedItem("idSubSegment").getNodeValue());
                mc.addCurveSegments(segments.get(idSubSegment));
            }
            ui.addCurve(mc);
            dc.addCurve(mc);
            
            curves.put(idCurve, mc);
        }
        dc.setIdLastCurve(maxId);
        Dipl_project.getDC().newRoad();
     }
}
