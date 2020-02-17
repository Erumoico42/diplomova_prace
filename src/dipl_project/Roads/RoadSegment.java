/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dipl_project.Roads;

import TrafficLights.TrafficLight;
import dipl_project.Dipl_project;
import dipl_project.UI.DrawControll;
import dipl_project.UI.UIControll;
import dipl_project.Vehicles.Vehicle;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.SwipeEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;

/**
 *
 * @author Honza
 */
public class RoadSegment {
    private Point p0, p1, p2, p3;
    private Point p0orig, p1orig, p2orig, p3orig;
    private Circle roadSegment;
    private CubicCurve shape;
    private List<RoadSegment> intersectedRoadSegments=new ArrayList<>();
    private List<CheckPoint> checkPoints=new ArrayList<>();
    private List<WatchPoint> watchPoints=new ArrayList<>();
    private List<CheckPoint> secondaryCheckPoints=new ArrayList<>();
    private List<RoadSegment> rsNext=new ArrayList<>();
    private List<RoadSegment> rsLast=new ArrayList<>();
    private List<RoadSegment> rsSameWay=new ArrayList<>();
    private List<TrafficLight> trafficLights=new ArrayList<>();
    private Vehicle vehicle;
    private MyCurve mainCurve;
    private HBox checkPointInfo;
    private boolean selectedRS=false;
    private UIControll ui=Dipl_project.getUI();
    private DrawControll dc=Dipl_project.getDC();
    private int id;
    private double errorDistance=0, segmentLenght;
    private boolean run=false, blinkerLeft=false, blinkerRight=false, blinkerStop=false;
    public RoadSegment(Point p0, Point p3) {
        this.p0=p0;
        this.p3=p3;
        roadSegment=new Circle(p3.getX(), p3.getY(), 5,Color.GREEN);
        roadSegment.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(!selectedRS)
                    selectRS();
                else
                    deselectRS();
            }
        });
        roadSegment.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                roadSegment.setRadius(7);
            }
        });
        roadSegment.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                roadSegment.setRadius(5);
            }
        });
        shape=new CubicCurve(p0.getX(), p0.getY(),p0.getX(), p0.getY(),p3.getX(), p3.getY(), p3.getX(), p3.getY());
        setDefRoadSegment();
        shape.setStrokeWidth(7);
        shape.setFill(null);
        shape.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                ui.setVisibleCPs(true, getThisSegment());
            }
        });
        Dipl_project.getUI().addRoadSegment(this);
    }

    public double getSegmentLenght() {
        return segmentLenght;
    }

    public void setSegmentLenght(double segmentLenght) {
        this.segmentLenght = segmentLenght;
    }
    public void setVisible(boolean visible)
    {
        roadSegment.setVisible(visible);
    }
    public void runSecondary(int distanceStart, int distanceEnd, int distanceOld, RoadSegment rsToCheck)
    {
        int minMax=Math.max(distanceEnd, distanceOld);
        if(!rsLast.isEmpty() && distanceStart<minMax)
        {
            for (RoadSegment rs : rsToCheck.getRsLast()) {
                    rs.setRun(false);
                runSecondary(distanceStart+1, distanceEnd, distanceOld, rs);
            }
        }
    }
    public void findSecondarySegment(int distanceStart, int distanceEnd, RoadSegment rsToCheck)
    {
        if(!rsLast.isEmpty() && distanceStart<distanceEnd)
        {
            for (RoadSegment rs : rsToCheck.getRsLast()) {
                rs.setRun(false);
                findSecondarySegment(distanceStart+1, distanceEnd, rs);
                
            }
        }
        else
        {
            RoadSegment actRS=dc.getActualRS();
            CheckPoint cpp = new CheckPoint(actRS,getThisSegment(), distanceStart);
            rsToCheck.addSecondaryCheckPoints(cpp);
            
            CheckPoint cp =actRS.getCPByRS(getThisSegment());
            cp.setDistance(distanceStart);
            rsToCheck.setRun(true);
            cp.addSecondaryCP(new CheckPoint(actRS,rsToCheck, -1));
        }
    }
    public void selectRS()
    {
        if(ui.isAddCP())
        {
            RoadSegment actRS=dc.getActualRS();
            if(ui.isSetPriority())
            {
                if(actRS.getCheckPoints().contains(actRS.getCPByRS(getThisSegment())))
                {
                    CheckPoint remCP=actRS.getCPByRS(getThisSegment());
                    actRS.removeCP(remCP);
                    setDefRoadSegment();
                    ui.removeCPFromList(remCP);

                }else
                {
                    CheckPoint newCP=new CheckPoint(actRS,getThisSegment());
                    actRS.addCP(newCP);
                    setMainRoadSegment();
                    ui.addCPToList(newCP);

                }
            }
            else if(ui.isSetWatch())
            {
                if(actRS.getWatchPoints().contains(actRS.getWPByRS(getThisSegment())))
                {
                    WatchPoint remWP=actRS.getWPByRS(getThisSegment());
                    actRS.removeWP(remWP);
                    setDefRoadSegment();
                    ui.removeWPFromList(remWP);

                }else
                {
                    WatchPoint newWP=new WatchPoint(actRS,getThisSegment(),2);
                    actRS.addWP(newWP);
                    setWatchRoadSegment();
                    ui.addWPToList(newWP);

                }
            }
            
            
        }
        else
        {
            RoadSegment actual=dc.getActualRS();
            if(actual!=null)
                actual.deselectRS();
            ui.setVisibleCPs(true, getThisSegment());
            selectedRS=true;
            setSideRoadSegment();
            dc.setActualRS(getThisSegment());
            for (CheckPoint checkPoint : checkPoints) {
                checkPoint.getRs().setMainRoadSegment();
            }
            for (WatchPoint watchPoint : watchPoints) {
                watchPoint.getRs().setWatchRoadSegment();
            }
            for (TrafficLight trafficLight : trafficLights) {
                trafficLight.rsSelect();
            }
            System.out.println(id);
        }
                    
            
        
    }
    public CheckPoint getCPByRS(RoadSegment rs)
    {
        for (CheckPoint checkPoint : checkPoints) {
            if(checkPoint.getRs().equals(rs))
                return checkPoint;
        }
        return null;
    }
    public WatchPoint getWPByRS(RoadSegment rs)
    {
        for (WatchPoint watchPoint : watchPoints) {
            if(watchPoint.getRs().equals(rs))
                return watchPoint;
        }
        return null;
    }
    public void deselectRS()
    {
        if(!ui.isAddCP() || dc.getActualRS().equals(getThisSegment()))
        {
            selectedRS=false;
            setDefRoadSegment();
            ui.setVisibleCPs(false, null);
            dc.setActualRS(null);
            for (CheckPoint checkPoint : checkPoints) {
                checkPoint.getRs().setDefRoadSegment();
            }
            for (TrafficLight trafficLight : trafficLights) {
                trafficLight.deselectTL();
            }
            for (WatchPoint watchPoint : watchPoints) {
                watchPoint.getRs().setDefRoadSegment();
            }
            ui.setAddCP(false);
                    
        }
        
    }
    public RoadSegment getThisSegment()
    {
        return this;
    }
    public HBox getCheckPointInfo()
    {
        return checkPointInfo;
    }
    public CubicCurve getShape() {
        return shape;
    }
    public void setMainRoadSegment()
    {
        shape.setStroke(Color.LIGHTGREEN);
        shape.setVisible(true);
    }
    public void setWatchRoadSegment()
    {
        shape.setStroke(Color.LIGHTSTEELBLUE);
        shape.setVisible(true);
    }
    public void setSideRoadSegment()
    {
        shape.setStroke(Color.RED);
        shape.setVisible(true);
    }
    public void setDefRoadSegment()
    {
        shape.setStroke(Color.TRANSPARENT);
        shape.setVisible(false);
    }
    public MyCurve getMainCurve() {
        return mainCurve;
    }

    public List<CheckPoint> getCheckPoints() {
        return checkPoints;
    }
    public void setMainCurve(MyCurve mainCurve) {
        this.mainCurve = mainCurve;
    }

    public double getErrorDistance() {
        return errorDistance;
    }

    public void setErrorDistance(double errorDistance) {
        this.errorDistance = errorDistance;
    }

    public List<TrafficLight> getTrafficLights() {
        return trafficLights;
    }

    public void addTrafficLight(TrafficLight trafficLight) {
        trafficLights.add(trafficLight);
    }
    public void removeTrafficLight(TrafficLight trafficLight) {
        trafficLights.remove(trafficLight);
    }
    public void addCP(CheckPoint cp)
    {
        checkPoints.add(cp);
    }
    public void removeCP(CheckPoint cp)
    {
        checkPoints.remove(cp);
    }
    public void addWP(WatchPoint wp)
    {
        watchPoints.add(wp);
    }
    public void removeWP(WatchPoint wp)
    {
        watchPoints.remove(wp);
    }


    public List<WatchPoint> getWatchPoints() {
        return watchPoints;
    }
    
    public void addIntersectedRS(RoadSegment rs)
    {
        if(!intersectedRoadSegments.contains(rs))
        {
            if(!rs.getMainCurve().isMainRoad())
            {
                mainCurve.setMainRoad(true);
                setMainRoadSegment();
            }
            else
            {
                mainCurve.setMainRoad(false);
                checkPoints.add(new CheckPoint(dc.getActualRS(),rs));
                setSideRoadSegment();
            }
            if(mainCurve.isMainRoad())
            {
                setMainRoadSegment();
            }
            intersectedRoadSegments.add(rs);
            boolean add=true;
            for (RoadSegment roadSegment : rs.getRsLast()) {
                if(!roadSegment.getIntersectedRoadSegments().isEmpty())
                    add=false;
            }
            if(add)
                rs.addIntersectedRS(this);
        }   
    }
    public void removeIntersectedRS(RoadSegment rs)
    {
        
        if(intersectedRoadSegments.contains(rs))
        {
            intersectedRoadSegments.remove(rs);
            checkPoints.remove(getCPByRS(rs));
            rs.removeIntersectedRS(this);
        } 
        if(intersectedRoadSegments.isEmpty())
            setDefRoadSegment();
    }
    public List<RoadSegment> getIntersectedRoadSegments() {
        return intersectedRoadSegments;
    }
    
    public void moveSegment(Point p)
    {
        roadSegment.setCenterX(p.getX());
        roadSegment.setCenterY(p.getY());
    }
    public void removeNext()
    {
        for (RoadSegment segment : rsNext) {
            segment.getRsLast().remove(this);
        }
        rsNext.clear();  
    }
    public void removeLast()
    {
        for (RoadSegment segment : rsLast) {
            segment.getRsNext().remove(this);
        }
        rsLast.clear();
    }
    public void removeSegment()
    {
        Dipl_project.getUI().removeRoadSegment(this);
        removeNext();
        removeLast();
        clearRsSameWay();
    }
    public void addNextRs(RoadSegment rs)
    {
        rsNext.add(rs);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
        
    }
    
    public void addLastRs(RoadSegment rs)
    {
        rsLast.add(rs);
    }

    public Circle getRoadSegment() {
        return roadSegment;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }
    
    public List<RoadSegment> getRsNext() {
        return rsNext;
    }

    public List<RoadSegment> getRsLast() {
        return rsLast;
    }
    
    public Point getP0() {
        return p0;
    }

    public List<RoadSegment> getRsSameWay() {
        return rsSameWay;
    }

    public void addRsSameWay(RoadSegment rs) {
        rsSameWay.add(rs);
    }
    public void removeRsSameWay(RoadSegment rs)
    {
        rsSameWay.remove(rs);
    }
    public void clearRsSameWay()
    {
        List<RoadSegment> rsHelpRSSW=new ArrayList<>();
        rsHelpRSSW.addAll(rsSameWay);

        for (RoadSegment rsSW : rsHelpRSSW) {
            rsSW.removeRsSameWay(getThisSegment());
        }
        rsSameWay.clear();
    }
    
    public void setP0(Point p0) {
        this.p0 = p0;
        shape.setStartX(p0.getX());
        shape.setStartY(p0.getY());
    }

    public Point getP1() {
        return p1;
    }

    public void setP1(Point p1) {
        this.p1 = p1;
        shape.setControlX1(p1.getX());
        shape.setControlY1(p1.getY());
    }

    public Point getP2() {
        return p2;
    }

    public void setP2(Point p2) {
        this.p2 = p2;
        shape.setControlX2(p2.getX());
        shape.setControlY2(p2.getY());
    }

    public Point getP3() {
        return p3;
    }

    public void setP3(Point p3) {
        this.p3 = p3;
        shape.setEndX(p3.getX());
        shape.setEndY(p3.getY());
    }

    public List<CheckPoint> getSecondaryCheckPoints() {
        return secondaryCheckPoints;
    }

    public void addSecondaryCheckPoints(CheckPoint scp) {
        secondaryCheckPoints.add(scp);
    }
    public void removeSecondaryCheckPointsByRS(RoadSegment scp) {
        for (CheckPoint scpp : secondaryCheckPoints) {
            if(scpp.getRs().equals(scp))
            {
                secondaryCheckPoints.remove(scpp);
                break;
            }
                
        }
        
    }

    public boolean isRun() {
        return run;
    }

    public void setRun(boolean run) {
        this.run = run;
    }

    public Point getP0orig() {
        return p0orig;
    }

    public void setP0orig(Point p0orig) {
        this.p0orig = p0orig;
    }

    public Point getP1orig() {
        return p1orig;
    }

    public void setP1orig(Point p1orig) {
        this.p1orig = p1orig;
    }

    public Point getP2orig() {
        return p2orig;
    }

    public void setP2orig(Point p2orig) {
        this.p2orig = p2orig;
    }

    public Point getP3orig() {
        return p3orig;
    }

    public void setP3orig(Point p3orig) {
        this.p3orig = p3orig;
    }

    public void setBlinkerLeft(boolean run) {
        blinkerLeft=run;
        if(run)
            blinkerRight=false;
    }

    public void setBlinkerRight(boolean run) {
        blinkerRight=run;
        if(run)
            blinkerLeft=false;
    }
    public void setStopBlinker(boolean stop)
    {
        blinkerStop=stop;
    }
    public boolean isStopBlinker() {
            return blinkerStop;
    }

    public boolean isBlinkerLeft() {
        return blinkerLeft;
    }

    public boolean isBlinkerRight() {
        return blinkerRight;
    }
    public void disconnectRS()
    {
        for (RoadSegment roadSegment1 : rsNext) {
            roadSegment1.getRsLast().remove(this);
        }
        for (RoadSegment roadSegment1 : rsLast) {
            roadSegment1.getRsNext().remove(this);
        }
        rsNext.clear();
        rsLast.clear();
    }
}
