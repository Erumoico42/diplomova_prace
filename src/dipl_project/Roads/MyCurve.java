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
import javafx.scene.shape.StrokeType;

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
    private Point p0orig, p1orig, p2orig, p3orig;
    private int id;
    private double curveLenght;
    private Arrow startArrow, endArrow;
    private List<Arrow> arrows=new ArrayList<>();
    private boolean mainRoad=false, tramCurve=false;
    private DrawControll dc=Dipl_project.getDC();
    private int defStroke=3, selectStroke=5;
    private Color defColor=Color.BLACK, selectColor=Color.DARKRED;

    private double maxAngleP1, maxAngleP2, dAngleP1, dAngleP2, originalAngleP1, originalAngleP2;
    public MyCurve(Connect startConnect, Connect endConnect, int id)
    {
       this.id=id;
        startConnect.addStartCurves(this);
        endConnect.addEndCurves(this);;
        p0=new Point(startConnect.getLocation());
        p1=new Point();
        p2=new Point();
        p3=new Point(endConnect.getLocation());
        
        
        
        startControll=new Controll(startConnect, p1, this);
        endControll=new Controll(endConnect, p2, this);
        this.startConnect=startConnect;
        this.endConnect=endConnect;
        autoMoveControlls();
        curve=new CubicCurve(startConnect.getX(), startConnect.getY(), p1.getX(), p1.getY(), p2.getX(), p2.getY(), endConnect.getX(), endConnect.getY());
        curve.setStroke(Color.BLACK);
        curve.setFill(null);
        curve.setStrokeWidth(3);
        curve.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                    selectCurve();
            }
        });
        curve.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                MyCurve selectedCurve=dc.getSelectedCurve();
                if((selectedCurve!=null && !selectedCurve.equals(getThisCurve())) || selectedCurve==null)
                    deselectCurve();
            }
        });
        curve.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                MyCurve selectedCurve=dc.getSelectedCurve();
                if(selectedCurve!=null)
                {
                    if(selectedCurve.equals(getThisCurve()))
                    {
                        deselectCurve();
                        dc.setSelectedCurve(null);
                    }
                    else
                    {
                        selectedCurve.deselectCurve();
                        dc.setSelectedCurve(getThisCurve());
                    }
                    
                }
                else{
                    selectCurve();
                    dc.setSelectedCurve(getThisCurve());
                }
            }
        });
        
        controllHandlers();
        dc.newRoad();
        startArrow=new Arrow(MyMath.angle(p1, p0), p0.getX(), p0.getY());
        endArrow=new Arrow(MyMath.angle(p3, p2), p3.getX(), p3.getY());
    }
    public void setTramCurve()
    {
        defStroke=6;
        selectStroke=10;
        defColor=Color.DARKSLATEBLUE;
        deselectCurve();
        tramCurve=true;
    }
    public void selectCurve()
    {
        curve.setStrokeWidth(selectStroke);
        curve.setStroke(selectColor);
        startEditCurve();
    }
    public void deselectCurve()
    {
         curve.setStrokeWidth(defStroke);
        curve.setStroke(defColor);
    }
    public MyCurve getThisCurve()
    {
        return this;
    }
    public Arrow getEndArrow() {
        return endArrow;
    }
    public Arrow getStartArrow() {
        return startArrow;
    }

    public List<Arrow> getArows() {
        return arrows;
    }

    public void addArrow(Arrow arrow) {
        arrows.add(arrow);
    }
    public void removeArrow(Arrow arrow) {
        arrows.remove(arrow);
        Dipl_project.getUI().removeComponents(arrow.getArrow());
    }
    public void removeArrowAt(int i) {
        if(arrows.size()-1>i)
            System.out.println("arrows fail");
        Dipl_project.getUI().removeComponents(arrows.get(i).getArrow());
        arrows.remove(i);
        
    }
    
    public void setStartConnect(Connect startConnect) {
        this.startConnect = startConnect;
    }

    public void setEndConnect(Connect endConnect) {
        this.endConnect = endConnect;
    }

    public double getCurveLenght() {
        return curveLenght;
    }

    public void setCurveLenght(double curveLenght) {
        this.curveLenght = curveLenght;
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
    private void startEditCurve()
    {
        originalAngleP1=MyMath.angle(p0, p1);
        originalAngleP2=MyMath.angle(p3, p2);
        maxAngleP1=(MyMath.angle(p0, p3)-originalAngleP1);
        maxAngleP2=(MyMath.angle(p3, p0)-originalAngleP2);
        
        if(maxAngleP1<-Math.PI)
            maxAngleP1=2*Math.PI+maxAngleP1;
        else if(maxAngleP1>Math.PI)
            maxAngleP1=maxAngleP1-2*Math.PI;
        
        if(maxAngleP2<-Math.PI)
            maxAngleP2=2*Math.PI+maxAngleP2;
        else if(maxAngleP2>Math.PI)
            maxAngleP2=maxAngleP2-2*Math.PI;

        dAngleP1=maxAngleP1/100;
        dAngleP2=maxAngleP2/100;
    }
    public void editCurve(double percents)
    {
        Point newP1=MyMath.rotate(p0, MyMath.length(p0, p1), originalAngleP1+(dAngleP1*percents)+Math.PI);
        moveStartControll(newP1.getX(), newP1.getY());
        adaptControlls(startConnect, p1, true);
        
        Point newP2=MyMath.rotate(p3, MyMath.length(p3, p2), originalAngleP2+(dAngleP2*percents)+Math.PI);
        moveEndControll(newP2.getX(), newP2.getY());
        adaptControlls(endConnect, p2, false);
    }
    private void controllHandlers()
    {
        startControll.getControll().setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                moveStartControll(event.getX(),event.getY());
                adaptControlls(startConnect, p1, true);
                Dipl_project.getUI().enableCurveEdit(false);
            }
        });
        endControll.getControll().setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                moveEndControll(event.getX(),event.getY());
                adaptControlls(endConnect, p2, false);
                Dipl_project.getUI().enableCurveEdit(false);
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
        startArrow.moveArrow(p0.getX(), p0.getY(), MyMath.angle(p1, p0));
    }
    public void moveEndControll(double x, double y)
    {
        endControll.move(x,y);
        p2.setLocation(x,y);
        curve.setControlX2(x);
        curve.setControlY2(y);
        dc.newRoad();
        endArrow.moveArrow(p3.getX(), p3.getY(), MyMath.angle(p3, p2));
        
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
        p1.setLocation(x1, y1);
        adaptControllToCurve(startConnect, startControll, true);
        endControll.move(x2, y2);
        
    }
    public void moveCurve(double x0, double y0, double x1, double y1, double x2, double y2, double x3, double y3)
    {
        p0.setLocation(x0, y0);
        curve.setStartX(x0);
        curve.setStartY(y0);
        
        p1.setLocation(x1, y1);
        curve.setControlX1(x1);
        curve.setControlY1(y1);
        
        p2.setLocation(x2, y2);
        curve.setControlX2(x2);
        curve.setControlY2(y2);
        
        p3.setLocation(x3, y3);
        curve.setEndX(x3);
        curve.setEndY(y3);
        startArrow.moveArrow(x0, y0, MyMath.angle(p1, p0));
        endArrow.moveArrow(x3, y3, MyMath.angle(p3, p2));
        Dipl_project.getRC().setArrows();
    }
    public void moveStartConnect(double x, double y)
    {
        p0.setLocation(x, y);
        curve.setStartX(x);
        curve.setStartY(y);
        dc.newRoad();
        startArrow.moveArrow(x, y, MyMath.angle(p1, p0));
    }
    public void moveEndConnect(double x, double y)
    {
        p3.setLocation(x,y);
        curve.setEndX(x);
        curve.setEndY(y);
        dc.newRoad();
        endArrow.moveArrow(x, y, MyMath.angle(p3, p2));
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

    public void addCurveSegments(RoadSegment curveSegment) {
        curveSegments.add(curveSegment);
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

    public Point getP0() {
        return p0;
    }

    public Point getP1() {
        return p1;
    }

    public Point getP2() {
        return p2;
    }

    public Point getP3() {
        return p3;
    }

    public boolean isTramCurve() {
        return tramCurve;
    }
    
    
    
}
