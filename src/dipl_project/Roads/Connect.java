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
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

/**
 *
 * @author Honza
 */
public class Connect {
    private Point location;
    private Circle connect;
    private UIControll ui=Dipl_project.getUI();
    private List<MyCurve> startCurves=new ArrayList<>();
    private List<MyCurve> endCurves=new ArrayList<>();
    private double xOld, yOld;
    private Connect thisConnect;
    private Connect connectToConnect;
    private boolean tryConnect=false;
    private boolean selected=false;
    private boolean dragged=false;
    private DrawControll dc=Dipl_project.getDC();
    public Connect(Point location)
    {
        connect=new Circle(location.getX(), location.getY(), 7, Color.BLUE);
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
                dragged=true;
            }
        });
        connect.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(tryConnect)
                {
                    connectConnects();
                    
                }
                   
            }
        });
        connect.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(!selected)
                    connect.setRadius(8);
                
            }
        });
        connect.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(!selected)
                    connect.setRadius(6);
            }
        });
        connect.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                ui.moveComponentUp(connect);
            }
        });
        connect.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                
                if(event.getButton()==MouseButton.PRIMARY)
                {
                    if(selected && !dragged)
                        deselect();
                    else
                        select();
                    dragged=false; 
                }
                else if(event.getButton()==MouseButton.SECONDARY)
                {
                    select();
                    ui.showPopUp(location, thisConnect);
                }
            }
        });
    }
    public boolean canSplit()
    {
        if(startCurves.isEmpty() || endCurves.isEmpty())
            return false;
        else return true;
    }
    public void splitConnect()
    {
        for (MyCurve startCurve : startCurves) {
            Connect endCon=startCurve.getEndConnect();
            Point endConLoc=endCon.getLocation();
            Point newLoc=MyMath.rotate(endConLoc, 
                    MyMath.length(location, endConLoc)-10, MyMath.angle(location, endConLoc));
            Connect newCon=new Connect(newLoc);
            startCurve.setStartConnect(newCon); 
            newCon.addStartCurves(startCurve);
            
            
            newCon.move(newLoc.getX(), newLoc.getY());
            ui.addConnect(newCon);
        }
        
        for (MyCurve endCurve : endCurves) {
            Connect startCon=endCurve.getStartConnect();
            Point startConLoc=startCon.getLocation();
            Point newLoc=MyMath.rotate(startConLoc, 
                    MyMath.length(location, startConLoc)-10, MyMath.angle(location, startConLoc));
            Connect newCon=new Connect(newLoc);
            endCurve.setEndConnect(newCon);
            newCon.addEndCurves(endCurve);
            
            endCurve.getLastRS().removeNext();
            
            
            newCon.move(newLoc.getX(), newLoc.getY());
            ui.addConnect(newCon);
        }
        ui.removeConnect(thisConnect);
        dc.newRoad();
    }
    public void removeConnect()
    {
        for (MyCurve endCurve : endCurves) {
            endCurve.getStartConnect().getStartCurves().remove(endCurve); 
            if(endCurve.getStartConnect().getStartCurves().isEmpty() 
                    && endCurve.getStartConnect().getEndCurves().isEmpty())
                endCurve.getStartConnect().removeConnect();
              ui.removeCurve(endCurve);
            for (RoadSegment segment : endCurve.getCurveSegments()) {
                segment.removeSegment();
            }
        }
        for (MyCurve startCurve : startCurves) {
            startCurve.getEndConnect().getEndCurves().remove(startCurve);
            if(startCurve.getEndConnect().getEndCurves().isEmpty() 
                    && startCurve.getEndConnect().getStartCurves().isEmpty())
                startCurve.getEndConnect().removeConnect();
            ui.removeCurve(startCurve);
            for (RoadSegment segment : startCurve.getCurveSegments()) {
                segment.removeSegment();
            }
        }
        endCurves.clear();
        startCurves.clear();

        dc.setActualConnect(null);
        ui.removeConnect(thisConnect);
        dc.newRoad();
    }
    public void select()
    {
        Connect actual=dc.getActualConnect();
        if(actual!=null)
            actual.deselect();
        dc.setActualConnect(this);
        selected=true;
        connect.setStroke(Color.AQUA);
        connect.setRadius(8);
    }
    public void deselect()
    {
        selected=false;
        
        dc.setActualConnect(null);
        setDefSkin();
    }
    public void setDefSkin()
    {
        connect.setFill(Color.BLUE);
        connect.setStroke(null);
        connect.setRadius(6);
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
        checkConnect();
    }
    private void checkConnect()
    {
        for (Connect con : ui.getConnects()) {
            if(!con.equals(thisConnect))
            {
                if(Shape.intersect(con.getConnect(), 
                        connect).getBoundsInLocal().getWidth()>0)
                {
                    selectToConnect(con);
                    break;
                }
                else
                {
                    con.setDefSkin();
                    tryConnect=false;
                }
                    
            }
            else
            {
                tryConnect=false;
            }
                
            
        }
    }
    private void selectToConnect(Connect con)
    {
        
        con.getConnect().setFill(Color.ORANGE);
        con.getConnect().setRadius(8);
        connectToConnect=con;
        tryConnect=true;
    }
    private void connectConnects()
    {
        for (MyCurve startCurve : startCurves) {
            connectToConnect.addStartCurves(startCurve);
            startCurve.setStartConnect(connectToConnect);
            startCurve.adaptControlls(connectToConnect, 
                    startCurve.getStartControll().getLocation(), true);
        }
        for (MyCurve endCurve : endCurves) {
            connectToConnect.addEndCurves(endCurve);
            endCurve.setEndConnect(connectToConnect);
            endCurve.adaptControlls(connectToConnect, 
                    endCurve.getEndControll().getLocation(), false);
        }
        move(connectToConnect.getX(), connectToConnect.getY());
        endCurves.clear();
        startCurves.clear();
        ui.removeConnect(thisConnect);
        connectToConnect.deselect();
        deselect();
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
