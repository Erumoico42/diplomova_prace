/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dipl_project.Roads;

import dipl_project.Dipl_project;
import dipl_project.UI.DrawControll;
import dipl_project.UI.UIControll;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

/**
 *
 * @author Honza
 */
public class MyCurve {
    private Connect startConnect, endConnect;
    private Controll startControll,endControll;
    private List<RoadSegment> curveSegments=new ArrayList<>();
    private RoadSegment firstRS, lastRS;
    private CubicCurve curve;
    private Point p0, p1, p2, p3;
    private int id;
    private boolean mainRoad=false;
    private DrawControll dc=Dipl_project.getDC();
    public MyCurve(Connect startConnect, Connect endConnect)
    {
       
        startConnect.addStartCurves(this);
        endConnect.addEndCurves(this);;
        p0=startConnect.getLocation();
        p1=new Point();
        p2=new Point();
        p3=endConnect.getLocation();
        
        startControll=new Controll(startConnect, p1, this);
        endControll=new Controll(endConnect, p2, this);
        this.startConnect=startConnect;
        this.endConnect=endConnect;
        autoMoveControlls();
        curve=new CubicCurve(startConnect.getX(), startConnect.getY(), p1.getX(), p1.getY(), p2.getX(), p2.getY(), endConnect.getX(), endConnect.getY());
        curve.setStroke(Color.BLACK);
        curve.setFill(null);
        controllHandlers();
        dc.newRoad();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isMainRoad() {
        return mainRoad;
    }

    public void setMainRoad(boolean mainRoad) {
        this.mainRoad = mainRoad;
    }
    
    private void controllHandlers()
    {
        startControll.getControll().setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                moveStartControll(event.getX(),event.getY());
                adaptControlls(startConnect, p1, true);
            }
        });
        endControll.getControll().setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                moveEndControll(event.getX(),event.getY());
                adaptControlls(endConnect, p2, false);
            }
        });
    }
    
    public void adaptControlls(Connect connect, Point p, boolean startControll)
    {
        double angle=MyMath.angle(connect.getLocation(),p);
        if(!startControll)
            angle+=Math.PI;
        for (MyCurve startCurve : connect.getStartCurves()) {
            if(!startCurve.equals(this))
            {
                double length=MyMath.length(connect.getLocation(), startCurve.getStartControll().getLocation());
                Point pp=MyMath.rotate(connect.getLocation(), length, angle+Math.PI);
                startCurve.moveStartControll(pp.getX(), pp.getY());
            }
        }
        for (MyCurve endCurve : connect.getEndCurves()) {
            if(!endCurve.equals(this))
            {
                double length=MyMath.length(connect.getLocation(), endCurve.getEndControll().getLocation());
                Point pp=MyMath.rotate(connect.getLocation(), length, angle);
                endCurve.moveEndControll(pp.getX(), pp.getY());
            }
        }         
    }
    public Connect getStartConnect() {
        return startConnect;
    }

    public Connect getEndConnect() {
        return endConnect;
    }

    public CubicCurve getCurve() {
        return curve;
    }
    public void moveStartControll(double x, double y)
    {
        startControll.move(x,y);
        p1.setLocation(x,y);
        curve.setControlX1(x);
        curve.setControlY1(y);
        dc.newRoad();
    }
    public void moveEndControll(double x, double y)
    {
        endControll.move(x,y);
        p2.setLocation(x,y);
        curve.setControlX2(x);
        curve.setControlY2(y);
        dc.newRoad();
        
    }
    private void autoMoveControlls()
    {
        double x0=p0.getX();
        double y0=p0.getY();
        double x3=p3.getX();
        double y3=p3.getY();
        double x1=(x0+(x3-x0)*1/3);
        double y1=(y0+(y3-y0)*1/3);
        double x2=(x0+(x3-x0)*2/3);
        double y2=(y0+(y3-y0)*2/3);
        startControll.move(x1, y1);
        adaptControllToCurve(startConnect, startControll, true);
        endControll.move(x2, y2);
        
    }
    public void moveStartConnect(double x, double y)
    {
        p0.setLocation(x, y);
        curve.setStartX(x);
        curve.setStartY(y);
        dc.newRoad();
    }
    public void moveEndConnect(double x, double y)
    {
        p3.setLocation(x,y);
        curve.setEndX(x);
        curve.setEndY(y);
        dc.newRoad();
    }
    public Controll getStartControll() {
        return startControll;
    }

    public Controll getEndControll() {
        return endControll;
    }
    public void adaptControllToCurve(Connect connect, Controll controll, boolean sc)
    {
        Point p=controll.getLocation();
        List<MyCurve> endCurves=connect.getEndCurves();
        List<MyCurve> startCurves=connect.getStartCurves();
        double angle=-1;
        boolean start=false;
        if(!endCurves.isEmpty())
        {
            angle=MyMath.angle(connect.getLocation(),endCurves.get(0).getEndControll().getLocation());
            
        }
        else if(!startCurves.isEmpty())
        {
            start=true;
            angle=MyMath.angle(connect.getLocation(),startCurves.get(0).getStartControll().getLocation());
            
        }
        if(angle>0){
            if(!sc)
                angle+=Math.PI;
            if(start)
                angle+=Math.PI;
            double length=MyMath.length(connect.getLocation(), p);
            Point pNew=MyMath.rotate(connect.getLocation(), length,angle);
            controll.move(pNew.getX(), pNew.getY());
        }
    }

    public List<RoadSegment> getCurveSegments() {
        return curveSegments;
    }

    public void addCurveSegments(RoadSegment curveSegments) {
        this.curveSegments.add(curveSegments);
    }
    public RoadSegment getLastCurveSegment()
    {
        if(curveSegments.isEmpty())
            return null;
        return curveSegments.get(curveSegments.size()-1);
    }

    public RoadSegment getFirstCurveSegment()
    {
        if(curveSegments.isEmpty())
            return null;
        return curveSegments.get(0);
    }

    public RoadSegment getFirstRS() {
        return firstRS;
    }

    public void setFirstRS(RoadSegment firstRS) {
        this.firstRS = firstRS;
    }

    public RoadSegment getLastRS() {
        return lastRS;
    }

    public void setLastRS(RoadSegment lastRS) {
        this.lastRS = lastRS;
    }
    
    
    
}
