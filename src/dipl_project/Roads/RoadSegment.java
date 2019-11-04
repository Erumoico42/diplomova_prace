/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dipl_project.Roads;

import dipl_project.Dipl_project;
import dipl_project.Vehicles.Vehicle;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Line;

/**
 *
 * @author Honza
 */
public class RoadSegment {
    private Point p0, p1, p2, p3;
    private Circle roadSegment;
    private CubicCurve shape;
    private List<RoadSegment> intersectedRoadSegments=new ArrayList<>();
    private List<RoadSegment> checkPoints=new ArrayList<>();
    private List<RoadSegment> rsNext=new ArrayList<>();
    private List<RoadSegment> rsLast=new ArrayList<>();
    private Vehicle vehicle;
    private MyCurve mainCurve;
    private int id;
    public RoadSegment(Point p0, Point p3) {
        this.p0=p0;
        this.p3=p3;
        roadSegment=new Circle(p3.getX(), p3.getY(), 3,Color.GREEN);
        shape=new CubicCurve(p0.getX(), p0.getY(),p0.getX(), p0.getY(),p3.getX(), p3.getY(), p3.getX(), p3.getY());
        setDefRoadSegment();
        shape.setStrokeWidth(6);
        shape.setFill(null);
        Dipl_project.getUI().addRoadSegment(this);
    }

    public CubicCurve getShape() {
        return shape;
    }
    public void setMainRoadSegment()
    {
        shape.setStroke(Color.LIGHTGREEN);
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

    public List<RoadSegment> getCheckPoints() {
        return checkPoints;
    }
    public void setMainCurve(MyCurve mainCurve) {
        this.mainCurve = mainCurve;
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
                checkPoints.add(rs);
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
            checkPoints.remove(rs);
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
    
}
