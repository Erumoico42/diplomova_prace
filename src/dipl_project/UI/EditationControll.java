/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dipl_project.UI;

import TrafficLights.TrafficLight;
import dipl_project.Dipl_project;
import dipl_project.Roads.Connect;
import dipl_project.Roads.Controll;
import dipl_project.Roads.MyCurve;
import dipl_project.Roads.MyMath;
import dipl_project.Roads.RoadCreator;
import dipl_project.Roads.RoadSegment;
import dipl_project.Vehicles.Vehicle;
import java.awt.Point;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.application.Platform;
import javafx.util.Pair;

/**
 *
 * @author Honza
 */
public  class EditationControll {
    private static double zoomInRatio=1.2, zoomOutRatio=(double)5/6, zoomRatio=1;
    private static final int MAX_ZOOM=7, MIN_ZOOM=-5;
    private static double canvasWidth, canvasHeight;
    private static int zoomCount=0;
    public static void setDefRatio()
    {
        zoomCount=0;
        zoomRatio=1;
        Dipl_project.getAnim().setZoomRatio(zoomRatio);
    }
    public static void setCanvasSize(double width, double height)
    {
        canvasWidth=width/2;
        canvasHeight=height/2;
    }
    public static void zoomAll(double e)
    {
            if(e<0)
            {
                if(zoomCount>MIN_ZOOM)
                {
                    zoomCount--;
                    zoomRatio*=zoomOutRatio;
                    zoomByRatio(zoomOutRatio);
                }
                

            }
            else
            {
                if(zoomCount<MAX_ZOOM)
                {
                    zoomCount++;
                    zoomRatio*=zoomInRatio;
                    zoomByRatio(zoomInRatio);
                }
            }
            
        
    }

    public static double getZoomRatio() {
        return zoomRatio;
    }
   
    public static void zoomByRatio(double zr)
    {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Dipl_project.getAnim().changeZoomRatio(zr);
                Dipl_project.getAnim().setZoomRatio(zoomRatio);
                zoomRC(zr);
                zoomConnects(zr);
                zoomCurves(zr);
                zoomSegments(zr);
                zoomTLs(zr);
                Dipl_project.getRC().enableNewSegment(false);
                Dipl_project.getRC().setZooming(true);
                if(BackgroundControll.isBackground())
                    BackgroundControll.zoomBackgroundByRatio(zr);
                Dipl_project.getDC().newRoad();
                Dipl_project.getRC().setArrows();
                Dipl_project.getRC().enableNewSegment(true);
                Dipl_project.getRC().setZooming(false);
                Dipl_project.getUI().refreshShowRoads();
            }
        });
        
        
    }
    private static void zoomRC(double zoomRatio)
    {
        double segLenght=Dipl_project.getRC().getSegLenght()*zoomRatio;
        Dipl_project.getRC().setSegLenght(segLenght);
        double colisDist=Dipl_project.getRC().getCollisDistance()*zoomRatio;
        Dipl_project.getRC().setCollisDistance(colisDist);
        double arrowLenghtMin=Dipl_project.getRC().getArrowLengthMin()*zoomRatio;
        Dipl_project.getRC().setArrowLengthMin(arrowLenghtMin);
    }
    private static void zoomTLs(double zoomRatio)
    {
        for (TrafficLight tl : Dipl_project.getDC().getTrafficLights()) {
            Point pos=tl.getPosition();
            double tlLayoutX = pos.getX()-(canvasWidth);
            tlLayoutX*=zoomRatio;
            tlLayoutX+=canvasWidth;
            double tlLayoutY = pos.getY()-(canvasHeight);
            tlLayoutY*=zoomRatio;
            tlLayoutY+=canvasHeight;
            pos.setLocation(tlLayoutX,tlLayoutY);
            tl.move(tlLayoutX, tlLayoutY);
            tl.zoomTL(zoomRatio);
        }
    }
    private static void zoomSegments(double zoomRatio)
    {
        List<RoadSegment> segments=Dipl_project.getUI().getSegments();
        for (RoadSegment segment : segments) {
            zoomSegment(segment, zoomRatio);
        }
        
    }
    private static void zoomSegment(RoadSegment segment, double zoomRatio)
    {
        Point p0=segment.getP0();
            double p0layoutX = p0.getX()-(canvasWidth);
            p0layoutX*=zoomRatio;
            p0layoutX+=canvasWidth;
            double p0layoutY = p0.getY()-(canvasHeight);
            p0layoutY*=zoomRatio;
            p0layoutY+=canvasHeight;
            p0.setLocation(p0layoutX,p0layoutY);
            segment.setP0(p0);
            
            Point p1=segment.getP1();
            double p1layoutX = p1.getX()-(canvasWidth);
            p1layoutX*=zoomRatio;
            p1layoutX+=canvasWidth;
            double p1layoutY = p1.getY()-(canvasHeight);
            p1layoutY*=zoomRatio;
            p1layoutY+=canvasHeight;
            p1.setLocation(p1layoutX,p1layoutY);
            segment.setP1(p1);
            
            Point p2=segment.getP2();
            double p2layoutX = p2.getX()-(canvasWidth);
            p2layoutX*=zoomRatio;
            p2layoutX+=canvasWidth;
            double p2layoutY = p2.getY()-(canvasHeight);
            p2layoutY*=zoomRatio;
            p2layoutY+=canvasHeight;
            p2.setLocation(p2layoutX,p2layoutY);
            segment.setP2(p2);
            
            Point p3=segment.getP3();
            double p3layoutX = p3.getX()-(canvasWidth);
            p3layoutX*=zoomRatio;
            p3layoutX+=canvasWidth;
            double p3layoutY = p3.getY()-(canvasHeight);
            p3layoutY*=zoomRatio;
            p3layoutY+=canvasHeight;
            p3.setLocation(p3layoutX,p3layoutY);
            segment.setP3(p3);
            segment.moveSegment(p3);
            double length=segment.getSegmentLenght();
            length*=zoomRatio;
            segment.setSegmentLenght(length);
    }
    private static void zoomCurves(double zoomRatio)
    {
        List<MyCurve> curves = Dipl_project.getUI().getCurves();
        for (MyCurve curve : curves) {
            
            Point p0=curve.getP0();
            double p0layoutX = p0.getX()-(canvasWidth);
            p0layoutX*=zoomRatio;
            p0layoutX+=canvasWidth;
            double p0layoutY = p0.getY()-(canvasHeight);
            p0layoutY*=zoomRatio;
            p0layoutY+=canvasHeight;
            
            
            Point p1=curve.getP1();
            double c11Lenght=MyMath.length(p1,p0);
            Controll c1=curve.getStartControll();
            p0.setLocation(p0layoutX,p0layoutY);
            
            double c1Angle=c1.getOriginAngle();
            Point p1New=MyMath.rotate(p0,c11Lenght*zoomRatio, c1Angle);
            p1.setLocation(p1New);
            
            
            Point p2=curve.getP2();
            Point p3=curve.getP3();
            double c12Lenght=MyMath.length(p2,p3);
            Controll c2=curve.getEndControll();
            double c2Angle=c2.getOriginAngle();
            
            
            
            double p3layoutX = p3.getX()-(canvasWidth);
            p3layoutX*=zoomRatio;
            p3layoutX+=canvasWidth;
            double p3layoutY = p3.getY()-(canvasHeight);
            p3layoutY*=zoomRatio;
            p3layoutY+=canvasHeight;
            p3.setLocation(p3layoutX,p3layoutY);
            
            Point p2New=MyMath.rotate(p3,c12Lenght*zoomRatio, c2Angle);
            p2.setLocation(p2New.getX(),p2New.getY());
            c1.move(p1New.getX(), p1New.getY());
            c1.moveLineStart(p0layoutX,p0layoutY);
            c2.move(p2New.getX(), p2New.getY());
            c2.moveLineStart(p3layoutX, p3layoutY);
            curve.moveCurve(p0layoutX, p0layoutY, p1.getX(), p1.getY(), p2.getX(), p2.getY(), p3layoutX, p3layoutY);
        }
    }
    private static void zoomConnects(double zoomRatio)
    {
        List<Connect> connects = Dipl_project.getUI().getConnects();
        for (Connect connect : connects) {
            Point p0=connect.getLocation();
            double p0layoutX = p0.getX()-(canvasWidth);
            p0layoutX*=zoomRatio;
            p0layoutX+=canvasWidth;
            double p0layoutY = p0.getY()-(canvasHeight);
            p0layoutY*=zoomRatio;
            p0layoutY+=canvasHeight;
            p0.setLocation(p0layoutX,p0layoutY);
            connect.moveConnect(p0layoutX, p0layoutY);
        }
    }
    
    public static void moveAll(double x, double y)
    {
        Dipl_project.getRC().enableNewSegment(false);
        BackgroundControll.moveBackground(x, y);
        moveSegments(x, y);
        moveCurves(x, y);
        moveConnects(x, y);
        moveTLs(x,y);
        moveVehicles();
        //Dipl_project.getDC().newRoad();
        //Dipl_project.getRC().setArrows();
        Dipl_project.getRC().enableNewSegment(true);

        
    }
    public static void editClick(double x, double y)
    {
        BackgroundControll.backgroundClick(x, y);
        clickSegments(x,y);
        clickCurves(x, y);
        clickConnects(x, y);
        cliclTLs(x,y);
    }
    private static void clickSegments(double x, double y)
    {
        List<RoadSegment> segments = Dipl_project.getUI().getSegments();
        for (RoadSegment segment : segments) {
            clickSegment(segment, x, y);
        }
    }
    private static void clickSegment(RoadSegment segment, double x, double y)
    {
        Point p0=segment.getP0();
        double p0layoutX = x-p0.getX();
        double p0layoutY = y-p0.getY();
        segment.setP0orig(new Point((int)p0layoutX, (int)p0layoutY));
        Point p1=segment.getP0();
        double p1layoutX = x-p1.getX();
        double p1layoutY = y-p1.getY();
        segment.setP1orig(new Point((int)p1layoutX, (int)p1layoutY));
        Point p2=segment.getP2();
        double p2layoutX = x-p2.getX();
        double p2layoutY = y-p2.getY();
        segment.setP2orig(new Point((int)p2layoutX, (int)p2layoutY));
        Point p3=segment.getP3();
        double p3layoutX = x-p3.getX();
        double p3layoutY = y-p3.getY();
        segment.setP3orig(new Point((int)p3layoutX, (int)p3layoutY));
    }
    
    public static void moveSegments(double x, double y)
    {
        List<RoadSegment> segments=dipl_project.Dipl_project.getUI().getSegments();
        for (RoadSegment segment : segments) {
            moveSegment(segment,x,y);
            
        }
        
    }
    private static void moveSegment(RoadSegment segment, double x, double y)
    {
        Point p0orig=segment.getP0orig();
        if(p0orig!=null)
        {
            double p0layoutX = x - p0orig.getX();
            double p0layoutY = y - p0orig.getY();
            Point p0=segment.getP0();
            p0.setLocation(p0layoutX, p0layoutY);
            segment.setP0(p0);
        }
        Point p1orig=segment.getP1orig();
        if(p1orig!=null)
        {
        
            double p1layoutX = x - p1orig.getX();
            double p1layoutY = y - p1orig.getY();
            Point p1=segment.getP1();
            p1.setLocation(p1layoutX, p1layoutY);
            segment.setP1(p1);
        }
        Point p2orig=segment.getP2orig();
        if(p2orig!=null)
        {
        
            double p2layoutX = x - p2orig.getX();
            double p2layoutY = y - p2orig.getY();
            Point p2=segment.getP2();
            p2.setLocation(p2layoutX, p2layoutY);
            segment.setP2(p2);
        }
        Point p3orig=segment.getP3orig();
        if(p3orig!=null)
        {
        
            double p3layoutX = x - p3orig.getX();
            double p3layoutY = y - p3orig.getY();
            Point p3=segment.getP3();
            p3.setLocation(p3layoutX, p3layoutY);
            segment.setP3(p3);
            segment.moveSegment(p3);
        }
    }
    private static void clickCurves(double x, double y)
    {
        for (MyCurve curve : Dipl_project.getUI().getCurves()) {
            Point p0=curve.getP0();
            double p0layoutX = x-p0.getX();
            double p0layoutY = y-p0.getY();
            curve.setP0orig(new Point((int)p0layoutX, (int)p0layoutY));
            Point p1=curve.getP1();
            double p1layoutX = x-p1.getX();
            double p1layoutY = y-p1.getY();
            Point p1Orig=new Point((int)p1layoutX, (int)p1layoutY);
            curve.setP1orig(p1Orig);
            Point p2=curve.getP2();
            double p2layoutX = x-p2.getX();
            double p2layoutY = y-p2.getY();
            Point p2Orig=new Point((int)p2layoutX, (int)p2layoutY);
            curve.setP2orig(p2Orig);
            Point p3=curve.getP3();
            double p3layoutX = x-p3.getX();
            double p3layoutY = y-p3.getY();
            curve.setP3orig(new Point((int)p3layoutX, (int)p3layoutY));
            
            Controll c1=curve.getStartControll();
            double c1layoutX = x-c1.getLocation().getX();
            double c1layoutY = y-c1.getLocation().getY();
            c1.setLocOrigin(new Point((int)c1layoutX, (int)c1layoutY));
            
            Controll c2=curve.getEndControll();
            double c2layoutX = x-c2.getLocation().getX();
            double c2layoutY = y-c2.getLocation().getY();
            c1.setLocOrigin(new Point((int)c2layoutX, (int)c2layoutY));
        }
    }
    private static void moveCurves(double x, double y)
    {
        for (MyCurve curve : Dipl_project.getUI().getCurves()) {
            
            Point p0orig=curve.getP0orig();
            double p0layoutX = x - p0orig.getX();
            double p0layoutY = y - p0orig.getY();
            
            Point p1orig=curve.getP1orig();
            double p1layoutX = x - p1orig.getX();
            double p1layoutY = y - p1orig.getY();
            
            Point p2orig=curve.getP2orig();
            double p2layoutX = x - p2orig.getX();
            double p2layoutY = y - p2orig.getY();
            
            Point p3orig=curve.getP3orig();
            double p3layoutX = x - p3orig.getX();
            double p3layoutY = y - p3orig.getY();
            
            curve.moveCurve(p0layoutX, p0layoutY, p1layoutX, p1layoutY, p2layoutX, p2layoutY, p3layoutX, p3layoutY);
            Controll c1=curve.getStartControll();
            c1.move(p1layoutX, p1layoutY);
            c1.moveLineStart(p0layoutX, p0layoutY);
            
            Controll c2=curve.getEndControll();
            c2.move(p2layoutX, p2layoutY);
            c2.moveLineStart(p3layoutX, p3layoutY);
        }
    }

    private static void moveVehicles()
    {
        Dipl_project.getAnim().moveVehicles();
        
    }
    private static void clickConnects(double x, double y)
    {
        for (Connect connect : Dipl_project.getUI().getConnects()) {
            Point c=connect.getLocation();
            double cLayoutX = x-c.getX();
            double cLayoutY = y-c.getY();
            connect.setLocOrigin(new Point((int)cLayoutX, (int)cLayoutY));
            for(Map.Entry<Pair<MyCurve, MyCurve>, RoadSegment> rs : connect.getConnectSegmentsMap().entrySet()) {
                RoadSegment segment = rs.getValue();
                clickSegment(segment, x,y);
            }
        }
    }
    private static void moveConnects(double x, double y)
    {
        for (Connect connect : Dipl_project.getUI().getConnects()) {
            Point cOrig=connect.getLocOrigin();
            double cLayoutX = x-cOrig.getX();
            double cLayoutY = y-cOrig.getY();
            connect.moveConnect(cLayoutX, cLayoutY);
            for(Map.Entry<Pair<MyCurve, MyCurve>, RoadSegment> rs : connect.getConnectSegmentsMap().entrySet()) {
                RoadSegment segment = rs.getValue();
                moveSegment(segment, x,y);
            }
        }
    }
     private static void cliclTLs(double x, double y)
    {
        for (TrafficLight tl : Dipl_project.getDC().getTrafficLights()) {
            Point t=tl.getPosition();
            double tlLayoutX = x-t.getX();
            double tlLayoutY = y-t.getY();
            tl.setLocOrig(new Point((int)tlLayoutX, (int)tlLayoutY));
        }
    }
    private static void moveTLs(double x, double y)
    {
        for (TrafficLight tl : Dipl_project.getDC().getTrafficLights()) {
            Point tlOrig=tl.getLocOrig();
            double tlLayoutX = x-tlOrig.getX();
            double tlLayoutY = y-tlOrig.getY();
            tl.setTLPosition(tlLayoutX, tlLayoutY);
        }
    }
    
    public static void zoomBack()
    {
        double defZoomRatio=Math.pow(zoomInRatio, -zoomCount);
        zoomByRatio(defZoomRatio);
    }
    public static void zoomRev()
    {
        double defZoomRatio=Math.pow(zoomInRatio, zoomCount);
        zoomByRatio(defZoomRatio);
    }
    public static double getZoomInRatio() {
        return zoomInRatio;
    }

    public static int getZoomCount() {
        return zoomCount;
    }
    
}
