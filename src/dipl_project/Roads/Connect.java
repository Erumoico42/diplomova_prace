/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dipl_project.Roads;

import dipl_project.Dipl_project;
import dipl_project.UI.DrawControll;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import javafx.event.EventHandler;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 *
 * @author Honza
 */
public class Connect {
    private Point location;
    private Circle connect;
    private List<MyCurve> startCurves=new ArrayList<>();
    private List<MyCurve> endCurves=new ArrayList<>();
    private double xOld, yOld;
    private Connect thisConnect;
    private boolean selected=false;
    private DrawControll dc=Dipl_project.getDC();
    public Connect(Point location)
    {
        connect=new Circle(location.getX(), location.getY(), 5, Color.BLUE);
        thisConnect=this;
        this.location=location;
        initHandler();
    } 
    private void initHandler()
    {
        connect.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                move(event.getX(), event.getY());
            }
        });
        connect.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getButton()==MouseButton.SECONDARY)
                {
                    dc.newCurve(thisConnect);
                    connectNewCurve(dc.getActualCurve());
                }
                else if(event.getButton()==MouseButton.PRIMARY)
                {
                    if(selected)
                        deselect();
                    else
                        select();
                }
            }
        });
    }
    public void select()
    {
        Connect actual=dc.getActualConnect();
        if(actual!=null)
            actual.deselect();
        dc.setActualConnect(this);
        selected=true;
        connect.setStroke(Color.AQUA);
        connect.setRadius(7);
    }
    public void deselect()
    {
        selected=false;
        dc.setActualConnect(null);
        setDefSkin();
    }
    public void setDefSkin()
    {
        
        connect.setStroke(null);
        connect.setRadius(5);
    }
    private void connectNewCurve(MyCurve curve)
    {
        Point p=curve.getEndControll().getLocation();
        double angle=-1;
        boolean start=false;
        if(!startCurves.isEmpty())
        {
            angle=MyMath.angle(location,startCurves.get(0).getStartControll().getLocation());
        } else if(!endCurves.isEmpty())
        {
            start=true;
            angle=MyMath.angle(location,endCurves.get(0).getEndControll().getLocation());
            
        }
        if(angle>0){
            if(start)
                angle+=Math.PI;
            double length=MyMath.length(location, p);
            Point pNew=MyMath.rotate(location, length,angle);
            curve.moveEndControll(pNew.getX(), pNew.getY());
        }
    }
    public double getX()
    {
        return location.getX();
    }
    public double getY()
    {
        return location.getY();
    }
    public void moveConnect(double x, double y)
    {
        location.setLocation(x, y);
        connect.setCenterX(x);
        connect.setCenterY(y);
    }
    public void move(double x, double y)
    {
        xOld=location.getX();
        yOld=location.getY();
        moveConnect(x,y);
        moveCurves(x,y);
    }
    private void moveCurves(double x, double y)
    {
        if(!startCurves.isEmpty() && !endCurves.isEmpty())
        {
            double xNew=(xOld-x);
            double yNew=(yOld-y);
            for (MyCurve startCurve : startCurves) {
                Point origP1=startCurve.getStartControll().getLocation();
                startCurve.moveStartControll(origP1.getX()-xNew, origP1.getY()-yNew);
                
            }
            for (MyCurve endCurve : endCurves) {
                Point origP2=endCurve.getEndControll().getLocation();
                endCurve.moveEndControll(origP2.getX()-xNew, origP2.getY()-yNew);
            }
        }
        for (MyCurve startCurve : startCurves) {
            startCurve.moveStartConnect(x,y);
            startCurve.getStartControll().moveLineStart(x, y);
        }
        for (MyCurve endCurve : endCurves) {  
            endCurve.moveEndConnect(x,y);
            endCurve.getEndControll().moveLineStart(x, y);
        }
    }
    public Point getLocation() {
        return location;
    }

    public Circle getConnect() {
        return connect;
    }

    public List<MyCurve> getStartCurves() {
        return startCurves;
    }

    public List<MyCurve> getEndCurves() {
        return endCurves;
    }

    public void addStartCurves(MyCurve startCurves) {
        this.startCurves.add(startCurves);
    }

    public void addEndCurves(MyCurve endCurves) {
        this.endCurves.add(endCurves);
    }
    
}
