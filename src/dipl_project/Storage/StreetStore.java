/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dipl_project.Storage;

import TrafficLights.TrafficLight;
import dipl_project.Roads.CheckPoint;
import dipl_project.Roads.Connect;
import dipl_project.Roads.MyCurve;
import dipl_project.Roads.RoadCreator;
import dipl_project.Roads.RoadSegment;
import dipl_project.Roads.WatchPoint;
import dipl_project.UI.DrawControll;
import dipl_project.UI.UIControll;
import java.awt.Point;
import java.rmi.server.UID;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        dc.setLoadingMap(false);
        loadStartSegments();
        dipl_project.Dipl_project.getRC().setCurves(ui.getCurves());
        dipl_project.Dipl_project.getRC().setArrows();
    }
    public void saveConnects()
    {
        List<Connect> connects=dipl_project.Dipl_project.getUI().getConnects();
        for (Connect connect : connects) {
            Element conn=doc.createElement("connect"); 
            
            Attr idConnect=doc.createAttribute("idConnect");
            idConnect.setValue(String.valueOf(connect.getId()));
            conn.setAttributeNode(idConnect);
            Attr position=doc.createAttribute("position");
            position.setValue(String.valueOf((int)connect.getLocation().getX()+","+(int)connect.getLocation().getY()));
            conn.setAttributeNode(position);
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
    public void saveStartSegments()
    {
        List<RoadSegment> startCarSegments=dipl_project.Dipl_project.getUI().getStartCarSegments();
        for (RoadSegment rs : startCarSegments) {
            Element startCarSegment=doc.createElement("startCarSegment");  
            Attr idStartCarSegment=doc.createAttribute("idStartCarSegment");
            idStartCarSegment.setValue(String.valueOf(rs.getId()));
            startCarSegment.setAttributeNode(idStartCarSegment);
            root.appendChild(startCarSegment);
        }
        List<RoadSegment> startTramSegments=dipl_project.Dipl_project.getUI().getStartTramSegments();
        for (RoadSegment rs : startTramSegments) {
            Element startTramSegment=doc.createElement("startTramSegment");  
            Attr idStartTramSegment=doc.createAttribute("idStartTramSegment");
            idStartTramSegment.setValue(String.valueOf(rs.getId()));
            startTramSegment.setAttributeNode(idStartTramSegment);
            root.appendChild(startTramSegment);
        }
    }
    private void loadStartSegments()
    {
        List<RoadSegment> startCarSegments=new ArrayList<>();
        List<RoadSegment> startTramSegments=new ArrayList<>();
        NodeList startCarSegment=doc.getElementsByTagName("startCarSegment");
        for (int i = 0; i < startCarSegment.getLength(); i++) {           
            int idStartCarSegment=Integer.parseInt(startCarSegment.item(i).getAttributes().getNamedItem("idStartCarSegment").getNodeValue());
            startCarSegments.add(segments.get(idStartCarSegment));
        }
        NodeList startTramSegment=doc.getElementsByTagName("startTramSegment");
        for (int i = 0; i < startTramSegment.getLength(); i++) {           
            int idStartTramSegment=Integer.parseInt(startTramSegment.item(i).getAttributes().getNamedItem("idStartTramSegment").getNodeValue());
            startTramSegments.add(segments.get(idStartTramSegment));
        }
        ui.setStartSegments(startCarSegments,startTramSegments);
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
            
            RoadSegment rsNew=new RoadSegment(p0, p3);
            rsNew.setId(idRoadSegment);
            rsNew.setP1(p1);
            rsNew.setP2(p2);
            /*NodeList tl=((Element)rs).getElementsByTagName("tl");
            for (int j = 0; j < tl.getLength(); j++) {
                int idTL=Integer.parseInt(rsSWs.item(j).getAttributes().getNamedItem("idTL").getNodeValue());
                newRS.addTrafficLight(trafficLights.get(idTL));
            }*/
            segments.put(idRoadSegment, rsNew);
            if(idRoadSegment>idMax)
                idMax=idRoadSegment+1;
        }
        dipl_project.Dipl_project.getRC().setId(idMax);
        
        
        
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

            Attr idFirstSegment=doc.createAttribute("idFirstSegment");
            idFirstSegment.setValue(String.valueOf(mc.getFirstCurveSegment().getId()));
            curve.setAttributeNode(idFirstSegment);

            Attr idLastSegment=doc.createAttribute("idLastSegment");
            idLastSegment.setValue(String.valueOf(mc.getLastCurveSegment().getId()));
            curve.setAttributeNode(idLastSegment);
            
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
            int idFirstSegment=Integer.parseInt(curve.getAttributes().getNamedItem("idFirstSegment").getNodeValue());
            int idLastSegment=Integer.parseInt(curve.getAttributes().getNamedItem("idLastSegment").getNodeValue());
            int curveLenght=Integer.parseInt(curve.getAttributes().getNamedItem("curveLenght").getNodeValue());
            boolean tramCurve=Boolean.parseBoolean(curve.getAttributes().getNamedItem("tramCurve").getNodeValue());
            MyCurve mc=new MyCurve(conn0,conn3, idCurve);
            conn0.addStartCurves(mc);
            conn3.addEndCurves(mc);
            if(tramCurve)
               mc.setTramCurve();
            mc.moveStartControll(p1.getX(), p1.getY());
            mc.moveEndControll(p2.getX(), p2.getY());
            mc.setFirstRS(segments.get(idFirstSegment));
            mc.setLastRS(segments.get(idLastSegment));
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
        dipl_project.Dipl_project.getDC().newRoad();
     }
}
