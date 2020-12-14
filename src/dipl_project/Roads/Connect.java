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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.event.EventHandler;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import javafx.util.Pair;

/**
 *
 * @author Honza
 */
public class Connect {
    private final int id;
    private Point location, locOrigin;
    private Circle connect;
    private UIControll ui=Dipl_project.getUI();
    private List<MyCurve> startCurves=new ArrayList<>();
    private List<MyCurve> endCurves=new ArrayList<>();
    private Map<Pair<MyCurve, MyCurve>,RoadSegment> connectSegmentsMap=new HashMap<>();
    private double xOld, yOld;
    private Connect thisConnect;
    private Connect connectToConnect;
    private boolean tryConnect=false;
    private boolean selected=false;
    private boolean dragged=false, tramConnect=false;
    private DrawControll dc=Dipl_project.getDC();
    public Connect(Point location, int id)
    {
        this.id=id;
        connect=new Circle(location.getX(), location.getY(), 7, Color.BLUE);
            
        
        thisConnect=this;
        this.location=location;
        initHandler();
    } 

    public int getId() {
        return id;
    }
    public void addConnectSegment(RoadSegment rs, MyCurve mc1, MyCurve mc2)
    {
        connectSegmentsMap.put(new Pair(mc1, mc2), rs);
        
    }
    public void removeConnectSegment(MyCurve mc1, MyCurve mc2)
    {
        connectSegmentsMap.remove(new Pair(mc1, mc2));
    }

    public Map<Pair<MyCurve, MyCurve>, RoadSegment> getConnectSegmentsMap() {
        return connectSegmentsMap;
    }
    
    public RoadSegment getConnectSegment(MyCurve mc1, MyCurve mc2)
    {
        return connectSegmentsMap.get(new Pair(mc1, mc2));
    }
    
    private void initHandler()
    {
        connect.setOnMouseDragged((MouseEvent event) -> {
            move(event.getX(), event.getY());
            dragged=true;
            Dipl_project.getUI().getUiLeftMenu().enableCurveEdit(false, null);
        });
        connect.setOnMouseReleased((MouseEvent event) -> {
            if(tryConnect)
            {
                connectConnects();
                
            }
        });
        connect.setOnMouseEntered((MouseEvent event) -> {
            if(!selected)
                connect.setRadius(8);
        });
        connect.setOnMouseExited((MouseEvent event) -> {
            if(!selected)
                connect.setRadius(6);
        });
        connect.setOnMousePressed((MouseEvent event) -> {
            ui.moveConnectUp(getThis());
        });
        connect.setOnMouseClicked((MouseEvent event) -> {
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
                int splitSize=startCurves.size()+endCurves.size();
                boolean enableSplit=splitSize>1;
                ui.showPopUp(location,enableSplit);
            }
        });
    }
    private Connect getThis()
    {
        return this;
    }
    public boolean canSplit()
    {
        if(startCurves.isEmpty() || endCurves.isEmpty())
            return false;
        else return true;
    }

    public boolean isTramConnect() {
        return tramConnect;
    }

    public void setTramConnect(boolean tramConnect) {
        this.tramConnect = tramConnect;
    }
    
    public void splitConnect()
    {
        for(Map.Entry<Pair<MyCurve, MyCurve>, RoadSegment> rs : connectSegmentsMap.entrySet()) {
            RoadSegment segment = rs.getValue();
            for (RoadSegment rsLast : segment.getRsLast()) {
                for (RoadSegment rsLast2 : rsLast.getRsLast()) {
                    rsLast2.getRsNext().clear();
                }
            }
            segment.removeSegment();
        }
        
        for (MyCurve startCurve : startCurves) {   
            
            Point p1Loc=startCurve.getP1();
            Point newLoc=MyMath.rotate(p1Loc, 
                    MyMath.length(location, p1Loc)-10, MyMath.angle(location, p1Loc));
            int lastConnID=Dipl_project.getDC().getIdLastConnect()+1;
            Dipl_project.getDC().setIdLastConnect(lastConnID);
            Connect newCon=new Connect(newLoc,lastConnID);
            startCurve.setStartConnect(newCon); 
            newCon.addStartCurves(startCurve);
            newCon.move(newLoc.getX(), newLoc.getY());
            ui.addConnect(newCon);
            dc.addConnect(newCon);
        }
        
        
        for (MyCurve endCurve : endCurves) {
            Point p2Loc=endCurve.getP2();
            Point newLoc=MyMath.rotate(p2Loc, 
                    MyMath.length(location, p2Loc)-10, MyMath.angle(location, p2Loc));
            int lastConnID=Dipl_project.getDC().getIdLastConnect()+1;
            Dipl_project.getDC().setIdLastConnect(lastConnID);
            Connect newCon=new Connect(newLoc,lastConnID);
            
            endCurve.setEndConnect(newCon);
            newCon.addEndCurves(endCurve);
            newCon.move(newLoc.getX(), newLoc.getY());
            ui.addConnect(newCon);
            dc.addConnect(newCon);
        }
        
        endCurves.clear();
        ui.removeConnect(thisConnect);
        dc.removeConnect(thisConnect);
        dc.newRoad();
        for (Connect connect : ui.getConnects()) {
            connect.setTryConnect(false);
        }
        
    }
    public void removeConnect()
    {
        for (MyCurve endCurve : endCurves) {
            Connect startConnect=endCurve.getStartConnect();
            for (MyCurve endCurve1 : startConnect.getEndCurves()) {
                RoadSegment rs=startConnect.getConnectSegment(endCurve, endCurve1);
                if(rs!=null)
                    rs.removeSegment();
                startConnect.removeConnectSegment(endCurve,endCurve1);
            }
            startConnect.getStartCurves().remove(endCurve); 
            
            if(startConnect.getStartCurves().isEmpty() 
                    && startConnect.getEndCurves().isEmpty())
                startConnect.removeConnect();
              ui.removeCurve(endCurve);
            for (RoadSegment segment : endCurve.getCurveSegments()) {
                segment.removeSegment();
            }
            for (MyCurve startCurve : startCurves) {
                RoadSegment rs= getConnectSegment(endCurve, startCurve);
                if(rs!=null)
                    rs.removeSegment();
            }
        }
        for (MyCurve startCurve : startCurves) {
            Connect endConnect=startCurve.getEndConnect();
            for (MyCurve startCurve1 : endConnect.getStartCurves()) {
                RoadSegment rs= endConnect.getConnectSegment(startCurve1, startCurve);
                if(rs!=null){
                    rs.removeSegment();
                }
                endConnect.removeConnectSegment(startCurve1,startCurve);
            }
            endConnect.getEndCurves().remove(startCurve);
            
            if(endConnect.getEndCurves().isEmpty() 
                    && endConnect.getStartCurves().isEmpty())
                endConnect.removeConnect();
            ui.removeCurve(startCurve);
            for (RoadSegment segment : startCurve.getCurveSegments()) {
                segment.removeSegment();
            }
        }
        for(Map.Entry<Pair<MyCurve, MyCurve>, RoadSegment> rs : connectSegmentsMap.entrySet()) {
                rs.getValue().removeSegment();
        }
        connectSegmentsMap.clear();
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
        Dipl_project.getDC().deselectCurve();
        Dipl_project.getDC().deselectSegment();
        Dipl_project.getDC().deselectTL();
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
    public void setTryConnect(boolean tryConnect)
    {
        this.tryConnect=tryConnect;
    }
    public double getX()
    {
        return location.getX();
    }
    public double getY()
    {
        return location.getY();
    }

    public Point getLocOrigin() {
        return locOrigin;
    }

    public void setLocOrigin(Point locOrigin) {
        this.locOrigin = locOrigin;
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
            if(!con.equals(thisConnect) && (con.isTramConnect()==tramConnect))
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
        }
        for (MyCurve endCurve : endCurves) {
            connectToConnect.addEndCurves(endCurve);
            endCurve.setEndConnect(connectToConnect);
        }
        if(!connectToConnect.getStartCurves().isEmpty())
        {
            MyCurve defCurve= connectToConnect.getStartCurves().get(0);
            
             for (MyCurve endCurve : connectToConnect.getEndCurves()) {
                 endCurve.adaptControlls(connectToConnect, 
                    defCurve.getStartControll().getLocation(), true);
             }
             for (MyCurve startCurve : connectToConnect.getStartCurves()) {
                 startCurve.adaptControlls(connectToConnect, 
                    defCurve.getStartControll().getLocation(), true);
             }
        }else if(!connectToConnect.getEndCurves().isEmpty())
        {
            MyCurve defCurve= connectToConnect.getEndCurves().get(0);
            
             for (MyCurve endCurve : connectToConnect.getEndCurves()) {
                 endCurve.adaptControlls(connectToConnect, 
                    defCurve.getEndControll().getLocation(), false);
             }
             for (MyCurve startCurve : connectToConnect.getStartCurves()) {
                 startCurve.adaptControlls(connectToConnect, 
                    defCurve.getEndControll().getLocation(), false);
             }
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

    public void addStartCurves(MyCurve startCurve) {
        if(!startCurves.contains(startCurve))
            this.startCurves.add(startCurve);
    }

    public void addEndCurves(MyCurve endCurve) {
        if(!endCurves.contains(endCurve))
            this.endCurves.add(endCurve);
    }
    
}
