/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TrafficLights;

import com.sun.javafx.scene.control.skin.VirtualFlow;
import dipl_project.Dipl_project;
import dipl_project.Roads.RoadSegment;
import dipl_project.UI.DrawControll;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Shape;

/**
 *
 * @author Honza
 */
public class TrafficLight {
    private int status=0;
    private int time=10, maxTime=10;
    private int id;
    private boolean enableSwitchRed=true, enableSwitchGreen=true,enableSwitchOrange=true;
    private int timeToSwitchRed=10, timeToSwitchGreen=10, timeToSwitchOrange=3;
    private Timer timer;
    private TimerTask timerTask;
    private double layoutX, layoutY, startX, startY, distX, distY;
    private boolean run=false;
    private ImageView tlImage=new ImageView();
    private double moveColorX, moveColorY;
    private Point location, locOrig;
    private List<TrafficLightsConnection> connectionsRed=new ArrayList<>();
    private List<TrafficLightsConnection> connectionsOrange=new ArrayList<>();
    private List<TrafficLightsConnection> connectionsGreen=new ArrayList<>();
    private List<TrafficLightsConnection> revConnectionsRed=new ArrayList<>();
    private List<TrafficLightsConnection> revConnectionsOrange=new ArrayList<>();
    private List<TrafficLightsConnection> revConnectionsGreen=new ArrayList<>();
    private Image imgGreen=new Image(Dipl_project.class.getResource("Resources/trafficLights/green.png").toString());
    private Image imgOrangeToRed=new Image(Dipl_project.class.getResource("Resources/trafficLights/orangeToRed.png").toString());
    private Image imgRed=new Image(Dipl_project.class.getResource("Resources/trafficLights/red.png").toString());
    private Image imgOrangeToGreen=new Image(Dipl_project.class.getResource("Resources/trafficLights/orangeToGreen.png").toString());
    private final String STYLE_SELECT_PRIM="-fx-border-color: blue;"
            + "-fx-border-width: 2;"
            + "-fx-border-style: solid;"; 
    private final String STYLE_DEF="-fx-border-color: red;"
            + "-fx-border-width: 0;"
            + "-fx-border-style: solid;"; 
    private final String STYLE_SELECT_SEC="-fx-border-color: red;"
            + "-fx-border-width: 2;"
            + "-fx-border-style: solid;"; 
    private HBox tlBox;
    private Circle circleRed, circleGreen, circleOrange;
    private CubicCurve curveToConnect;
    private TrafficLight tlToConnect;
    private int statusToConnect=-1;
    private Point pToConnect;
    private double redLayout=9.33, orangeLayout=24, greenLayout=39.67;
    private double tlWidth=20, tlHeight=50;
            

    public TrafficLight(double x, double y, int id) {
        circleRed=new Circle(7,Color.RED);
        this.id=id;
        circleGreen=new Circle(7, Color.LIGHTGREEN);
        circleOrange=new Circle(7,Color.ORANGE);
        circleRed.setVisible(false);
        circleOrange.setVisible(false);
        circleGreen.setVisible(false);
        tlBox=new HBox();
        tlBox.getChildren().addAll(tlImage);
        tlImage.setFitWidth(tlWidth);
        tlImage.setFitHeight(tlHeight);
        location=new Point((int)x-10, (int)y-25);
        moveTL(location.getX(), location.getY());
        setStatus(0, false);
        initHandlers();
        DrawControll dc=Dipl_project.getDC();
        TrafficLight tlAct=dc.getActualTL();
        if(tlAct!=null)
            tlAct.deselectTL();
        dc.setActualTL(getThis());
        selectTL();
    }
    
    public TrafficLight getThis()
    {
        return this;
    }
    
    public void enableConnectLights(boolean enable)
    {
        if(enable)
        {
            moveCirclesToTL();
            
            deselectTL();
        }
        tlBox.setDisable(enable);
        circleRed.setVisible(enable);
        circleOrange.setVisible(enable);
        circleGreen.setVisible(enable);
    }
    public void moveCirclesToTL()
    {
        double tlX=tlBox.getLayoutX();
        double tlY=tlBox.getLayoutY();
        circleRed.setCenterX(tlX+tlWidth/2);
        circleRed.setCenterY(tlY+redLayout);
        circleOrange.setCenterX(tlX+tlWidth/2);
        circleOrange.setCenterY(tlY+orangeLayout);
        circleGreen.setCenterX(tlX+tlWidth/2);
        circleGreen.setCenterY(tlY+greenLayout);
    }
    public void moveCurveToConnect(CubicCurve curve)
    {
        double x1=curve.getStartX();
        double y1=curve.getStartY();
        double x2=curve.getEndX();
        double y2=curve.getEndY();
        
        double dX=x1-x2;
        double dY=y1-y2;
        if(Math.abs(dX)>Math.abs(dY))
        {
            double contX=x1-dX/2;
            curve.setControlX1(contX);
            curve.setControlX2(contX);
            curve.setControlY1(y1);
            curve.setControlY2(y2);
        }
        else
        {
            double contY=y1-dY/2;
            curve.setControlX1(x1);
            curve.setControlX2(x2);
            curve.setControlY1(contY);
            curve.setControlY2(contY);
        }
    }
    public void newConnection(double x, double y)
    {
        curveToConnect=new CubicCurve(x, y, x, y, x, y, x, y);
        curveToConnect.setFill(null);
        curveToConnect.setStrokeWidth(2);
        curveToConnect.setStroke(Color.BLACK);
        Dipl_project.getUI().addComponentsDown(curveToConnect);
    }
    private void createConnection(int status)
    {
        TrafficLightsConnection tlCon=new TrafficLightsConnection(tlToConnect,getThis(), statusToConnect, status, curveToConnect);
        switch(status)
        {
            case 0:
            {
                connectionsGreen.add(tlCon);
                break;
            }
            case 1:
            {
                connectionsOrange.add(tlCon);
                break;
            }
            case 2:
            {
                connectionsRed.add(tlCon);
                break;
            }
        }
        switch(statusToConnect)
        {
            case 2:
            {
                tlToConnect.addRevConnectionsGreen(tlCon);
                break;
            }
            case 1:
            {
                tlToConnect.addRevConnectionsOrange(tlCon);
                break;
            }
            case 0:
            {
                tlToConnect.addRevConnectionsRed(tlCon);
                break;
            }
        }
        
    }
    private void handlerColorPressed(MouseEvent event, Circle circle)
    {
        moveColorX=circle.getLayoutX();
        moveColorY=circle.getLayoutY();
        newConnection(circle.getCenterX(),circle.getCenterY());
    }
    private void handlerColorDragged(MouseEvent event, Circle circle)
    {
        circle.setCenterX(event.getX());
        circle.setCenterY(event.getY());
        curveToConnect.setEndX(event.getX()+moveColorX);
        curveToConnect.setEndY(event.getY()+moveColorY);
        moveCurveToConnect(curveToConnect);
        findConnection(circle);
    }
    private void handlerColorReleased(int status)
    {
        if(statusToConnect!=-1)
        {
            curveToConnect.setEndX(pToConnect.getX());
            curveToConnect.setEndY(pToConnect.getY());
            createConnection(status);
        }
        else
        {
            Dipl_project.getUI().removeComponents(curveToConnect);
        }
        enableConnectLights(true);
    }
    private void initHandlers()
    {   
        circleRed.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                handlerColorPressed(event, circleRed);
            }
        });
        circleRed.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                handlerColorDragged(event, circleRed);
            }
        });
        circleRed.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                handlerColorReleased(2);
            }
        });
        
        circleOrange.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                handlerColorPressed(event, circleOrange);
            }
        });
        circleOrange.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                handlerColorDragged(event, circleOrange);
            }
        });
        circleOrange.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                handlerColorReleased(1);
            }
        });
        
        circleGreen.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                handlerColorPressed(event, circleGreen);
            }
        });
        circleGreen.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                handlerColorDragged(event, circleGreen);
            }
        });
        circleGreen.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                handlerColorReleased(0);
            }
        });
        
        tlBox.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                DrawControll dc=Dipl_project.getDC();
                TrafficLight tlAct=dc.getActualTL();
                
                if(event.getButton()==MouseButton.PRIMARY){
                    
                    if(tlAct!=null)
                    {
                        if(tlAct!=getThis())
                        {
                            tlAct.deselectTL();
                            dc.setActualTL(getThis());
                            selectTL();
                        }
                        else
                        {
                            dc.setActualTL(null);
                            deselectTL();
                        }
                    }
                    else
                    {
                        dc.setActualTL(getThis());
                            selectTL();
                    }
                }else if(event.getButton()==MouseButton.SECONDARY){
                    RoadSegment rsAct = dc.getActualRS();
                    if(rsAct!=null)
                    {
                        if(rsAct.getTrafficLights().contains(getThis()))
                        {
                            rsAct.removeTrafficLight(getThis());
                            deselectTL();
                        }
                        else{
                            rsAct.addTrafficLight(getThis());
                            rsSelect();
                        }   
                    }
                    else
                    {
                        if(tlAct!=null)
                            tlAct.deselectTL();
                        dc.setActualTL(getThis());
                        selectTL();
                        Dipl_project.getUI().showPopUpTL(new Point((int)tlBox.getLayoutX(), (int)tlBox.getLayoutY()));
                    }
                }
                
                
            }
        });
        tlBox.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                startX = event.getX();
                startY = event.getY();
                distX = startX - layoutX;
                distY = startY - layoutY;
            }
        });
        tlBox.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                moveTL(event.getX(), event.getY());
            }
        });
    }
    private void findConnection(Circle cir)
    {
        for (TrafficLight tl : Dipl_project.getDC().getTrafficLights()) {
            if(!tl.equals(getThis()))
            {
                int stat=checkInstersect(cir, tl);
                statusToConnect=stat;
                if(stat==-1)
                {
                    tlToConnect=null;
                }
                else
                {
                  
                    tlToConnect=tl;
                    break;
                }
                
            }
            
        }
        
    }
    private int checkInstersect(Circle cir, TrafficLight tl)
    {
        int ret=-1;
        double newMax;
        double x; double y;
        Circle actCircle=tl.getCircleRed();
        x=actCircle.getCenterX();
        y=actCircle.getCenterY();
        double max=Shape.intersect(cir,actCircle).getBoundsInLocal().getWidth();
        if(max>0)
            ret=0;
        actCircle=tl.getCircleOrange();
        newMax=Shape.intersect(cir,actCircle).getBoundsInLocal().getWidth();
        if(newMax>max)
        {
            x=actCircle.getCenterX();
            y=actCircle.getCenterY();
            max=newMax;
            ret=1;
        }
        actCircle=tl.getCircleGreen();
        newMax=Shape.intersect(cir,actCircle).getBoundsInLocal().getWidth();
        if(newMax>max)
        {
            x=actCircle.getCenterX();
            y=actCircle.getCenterY();
            max=newMax;
            ret=2;
        }
        
        pToConnect=new Point((int)x, (int)y);
        return ret;
    }
    public HBox getTlImage() {
        return tlBox;
    }

    public List<TrafficLightsConnection> getConnectionsRed() {
        return connectionsRed;
    }

    public List<TrafficLightsConnection> getConnectionsOrange() {
        return connectionsOrange;
    }

    public List<TrafficLightsConnection> getConnectionsGreen() {
        return connectionsGreen;
    }

    public List<TrafficLightsConnection> getRevConnectionsRed() {
        return revConnectionsRed;
    }

    public List<TrafficLightsConnection> getRevConnectionsOrange() {
        return revConnectionsOrange;
    }

    public List<TrafficLightsConnection> getRevConnectionsGreen() {
        return revConnectionsGreen;
    }
    
    public List<TrafficLightsConnection> getAllConnections()
    {
        List<TrafficLightsConnection> connections=new ArrayList<>();
        connections.addAll(connectionsRed);
        connections.addAll(connectionsOrange);
        connections.addAll(connectionsGreen);
        return connections;
    }
    public List<TrafficLightsConnection> getAllRevConnections()
    {
        List<TrafficLightsConnection> connections=new ArrayList<>();
        connections.addAll(revConnectionsGreen);
        connections.addAll(revConnectionsOrange);
        connections.addAll(revConnectionsRed);
        return connections;
    }
    public void removeConnection(TrafficLightsConnection tlc)
    {
        connectionsRed.remove(tlc);
        connectionsOrange.remove(tlc);
        connectionsGreen.remove(tlc);
    }
    public void removeRevConnection(TrafficLightsConnection tlc)
    {
        revConnectionsGreen.remove(tlc);
        revConnectionsOrange.remove(tlc);
        revConnectionsOrange.remove(tlc);
    }
    public void removeTL()
    {
        List<TrafficLightsConnection> connections=getAllConnections();
        List<TrafficLightsConnection> revConnections=getAllRevConnections();
        for (TrafficLightsConnection connection : revConnections) {
            //Dipl_project.getUI().removeComponents(connection.getConnectCurve());
            connections.addAll(connection.getTlRev().getAllConnections());
        }
        for (TrafficLightsConnection connection : connections) {
            Dipl_project.getUI().removeComponents(connection.getConnectCurve());
            TrafficLight tl=connection.getTl();
            tl.removeRevConnection(connection);
        }
        
        connectionsRed.clear();
        connectionsOrange.clear();
        connectionsGreen.clear();
        Dipl_project.getDC().setActualTL(null);
        Dipl_project.getDC().getTrafficLights().remove(this);
        Dipl_project.getUI().removeComponents(tlBox, circleGreen, circleOrange, circleRed);
    }
    public Circle getCircleRed() {
        return circleRed;
    }

    public Circle getCircleGreen() {
        return circleGreen;
    }

    public Circle getCircleOrange() {
        return circleOrange;
    }

    public CubicCurve getCurveToConnect() {
        return curveToConnect;
    }
    private void moveConnectCurveStart()
    {
        for (TrafficLightsConnection trafficLightsConnection : connectionsRed) {
            CubicCurve curve = trafficLightsConnection.getConnectCurve();
            curve.setStartX(layoutX+10);
            curve.setStartY(layoutY+redLayout);
            moveCurveToConnect(curve);
        }
        for (TrafficLightsConnection trafficLightsConnection : connectionsOrange) {
            CubicCurve curve = trafficLightsConnection.getConnectCurve();
            curve.setStartX(layoutX+10);
            curve.setStartY(layoutY+orangeLayout);
            moveCurveToConnect(curve);
        }
        for (TrafficLightsConnection trafficLightsConnection : connectionsGreen) {
            CubicCurve curve = trafficLightsConnection.getConnectCurve();
            curve.setStartX(layoutX+10);
            curve.setStartY(layoutY+greenLayout);
            moveCurveToConnect(curve);
        }
        //reverse
        for (TrafficLightsConnection trafficLightsConnection : revConnectionsRed) {
            CubicCurve curve = trafficLightsConnection.getConnectCurve();
            curve.setEndX(layoutX+10);
            curve.setEndY(layoutY+redLayout);
            moveCurveToConnect(curve);
        }
        for (TrafficLightsConnection trafficLightsConnection : revConnectionsOrange) {
            CubicCurve curve = trafficLightsConnection.getConnectCurve();
            curve.setEndX(layoutX+10);
            curve.setEndY(layoutY+orangeLayout);
            moveCurveToConnect(curve);
        }
        for (TrafficLightsConnection trafficLightsConnection : revConnectionsGreen) {
            CubicCurve curve = trafficLightsConnection.getConnectCurve();
            curve.setEndX(layoutX+10);
            curve.setEndY(layoutY+greenLayout);
            moveCurveToConnect(curve);
        }
    }
    public Point getPosition()
    {
        return location;
    }
    public void moveTL(double x, double y)
    {
        
        layoutX=x-distX;
        layoutY=y-distY;
        tlBox.setLayoutX(layoutX);
        tlBox.setLayoutY(layoutY);
        distX = startX-layoutX;
        distY = startY-layoutY;
        location.setLocation(layoutX, layoutY);
        moveConnectCurveStart();
    }
    public void setTLPosition(double x, double y)
    {
        tlBox.setLayoutX(x);
        tlBox.setLayoutY(y);
        layoutX=x;
        layoutY=y;
        location.setLocation(x, y);
        moveConnectCurveStart();
        moveCirclesToTL();
    }
    public void rsSelect()
    {
        tlBox.setStyle(STYLE_SELECT_SEC);
        layoutX-=2;
        layoutY-=2;
        tlBox.setLayoutX(layoutX);
        tlBox.setLayoutY(layoutY);
    }
    public void selectTL()
    {
        tlBox.setStyle(STYLE_SELECT_PRIM);
        layoutX-=2;
        layoutY-=2;
        location.setLocation(layoutX, layoutY);
        tlBox.setLayoutX(layoutX);
        tlBox.setLayoutY(layoutY);
    }
    public void deselectTL()
    {
        
        if(!tlBox.getStyle().equals(STYLE_DEF))
        {
            layoutX+=2;
            layoutY+=2;
        }
        location.setLocation(layoutX, layoutY);
        tlBox.setStyle(STYLE_DEF);
        tlBox.setLayoutX(layoutX);
        tlBox.setLayoutY(layoutY);
    }
    public int getStatus() {
        return status;
    }
    
    public void setStatus(int status, boolean forceTime) {
        this.status = status;
        switch(status)
        {
            case 0:
            {
                tlImage.setImage(imgGreen);
                if(forceTime)
                    setMaxTime(timeToSwitchGreen);
                break;
            }
            case 1:
            {
                tlImage.setImage(imgOrangeToRed);
                if(forceTime)
                    setMaxTime(timeToSwitchOrange);
                break;
            }
            case 2:
            {
                tlImage.setImage(imgRed);
                if(forceTime)
                    setMaxTime(timeToSwitchRed);
                break;
            }
            case 3:
            {
                tlImage.setImage(imgOrangeToGreen);
                if(forceTime)
                    setMaxTime(timeToSwitchOrange);
                break;
            }
            
        }
    }

    public int getTimeToSwitchRed() {
        return timeToSwitchRed;
    }

    public void setTimeToSwitchRed(int timeToSwitchRed) {
        this.timeToSwitchRed = timeToSwitchRed;
        if(status==2)
            setMaxTime(timeToSwitchRed);
    }

    public int getTimeToSwitchGreen() {
        return timeToSwitchGreen;
    }

    public void setTimeToSwitchGreen(int timeToSwitchGreen) {
        this.timeToSwitchGreen = timeToSwitchGreen;
        if(status==0)
            setMaxTime(timeToSwitchGreen);
    }

    public int getTimeToSwitchOrange() {
        return timeToSwitchOrange;
    }

    public void setTimeToSwitchOrange(int timeToSwitchOrange) {
        this.timeToSwitchOrange = timeToSwitchOrange;
        if(status==1 || status==3)
            setMaxTime(timeToSwitchOrange);
    }

    public boolean isRun() {
        return run;
    }

    public void setRun(boolean run) {
        this.run = run;
        if(run)
        {
            timer=new Timer();
            timerTask = new TimerTask() {
                @Override
                public void run(){
                    Platform.runLater(() -> {
                        tickTL();
                    });
                }
            };
            timer.schedule(timerTask, 1000, 1000);
        }
        else
        {
            if(timerTask!=null)
                timerTask.cancel();
            if(timer!=null)
                timer.cancel();
        }
    }
    private void tickTL()
    {
        time--;
        if(time<=0)
        {
            changeStatus();
        }
    }
    private void setMaxTime(int time)
    {
        this.time=time;
        maxTime=time;
    }
    public void changeStatusOne()
    {
        switch(status)
            {
                case 0:
                {
                    //green to orange
                    setMaxTime(timeToSwitchOrange);
                        setStatus(1, false);
                    break;
                }
                case 1:
                {
                    //orange to red
                    setMaxTime(timeToSwitchRed);
                    setStatus(2, false);
                    for (TrafficLightsConnection tlc : connectionsRed) {
                        tlc.startSwitch();
                    }
                    break;
                }
                case 2:
                {
                    //red to orange
                        setMaxTime(timeToSwitchOrange);
                        setStatus(3, false);
                        
                    
                    
                    break;
                }
                case 3:
                {
                    //orange to green

                    setMaxTime(timeToSwitchGreen);
                    setStatus(0, false);
                    for (TrafficLightsConnection tlc : connectionsGreen) {
                        tlc.startSwitch();
                    }
                    break;
                }
            }
    }
    public void changeStatus()
    {
        switch(status)
            {
                case 0:
                {
                    //green to orange
                    if(enableSwitchGreen)
                    {
                        setMaxTime(timeToSwitchOrange);
                        setStatus(1, false);
                        
                    }
                    
                    break;
                }
                case 1:
                {
                    //orange to red
                    setMaxTime(timeToSwitchRed);
                    setStatus(2, false);
                    for (TrafficLightsConnection tlc : connectionsRed) {
                        tlc.startSwitch();
                    }
                    break;
                }
                case 2:
                {
                    //red to orange
                    if(enableSwitchRed)
                    {
                        setMaxTime(timeToSwitchOrange);
                        setStatus(3, false);
                        
                    }
                    
                    break;
                }
                case 3:
                {
                    //orange to green

                    setMaxTime(timeToSwitchGreen);
                    setStatus(0, false);
                    for (TrafficLightsConnection tlc : connectionsGreen) {
                        tlc.startSwitch();
                    }
                    break;
                }
            }
    }
    public void setTime(int time) {
        this.time = time;
    }
    
    public void addConnectionsRed(TrafficLightsConnection conn) {
        connectionsRed.add(conn);
    }

    public void addConnectionsOrange(TrafficLightsConnection conn) {
        connectionsOrange.add(conn);
    }

    public void addConnectionsGreen(TrafficLightsConnection conn) {
        connectionsGreen.add(conn);
    }
    
    public void addRevConnectionsRed(TrafficLightsConnection conn) {
        revConnectionsRed.add(conn);
    }

    public void addRevConnectionsOrange(TrafficLightsConnection conn) {
        revConnectionsOrange.add(conn);
    }

    public void addRevConnectionsGreen(TrafficLightsConnection conn) {
        revConnectionsGreen.add(conn);
    }
    public void removeRevConnectionsRed(TrafficLightsConnection conn) {
        revConnectionsRed.remove(conn);
    }

    public void removeRevConnectionsOrange(TrafficLightsConnection conn) {
        revConnectionsOrange.remove(conn);
    }

    public void removeRevConnectionsGreen(TrafficLightsConnection conn) {
        revConnectionsGreen.remove(conn);
    }

    public boolean isEnableSwitchRed() {
        return enableSwitchRed;
    }

    public boolean isEnableSwitchGreen() {
        return enableSwitchGreen;
    }

    public boolean isEnableSwitchOrange() {
        return enableSwitchOrange;
    }

    public void setEnableSwitchRed(boolean enableSwitchRed) {
        this.enableSwitchRed = enableSwitchRed;
    }

    public void setEnableSwitchGreen(boolean enableSwitchGreen) {
        this.enableSwitchGreen = enableSwitchGreen;
    }

    public void setEnableSwitchOrange(boolean enableSwitchOrange) {
        this.enableSwitchOrange = enableSwitchOrange;
    }

    public int getId() {
        return id;
    }

    public int getTime() {
        return time;
    }
    public int getMaxTime()
    {
        return maxTime;
    }

    public void setLocOrig(Point locOrig) {
        this.locOrig = locOrig;
    }

    public Point getLocation() {
        return location;
    }

    public Point getLocOrig() {
        return locOrig;
    }

    public double getTlWidth() {
        return tlWidth;
    }

    public void setTlWidth(double tlWidth) {
        this.tlWidth = tlWidth;
    }

    public double getTlHeight() {
        return tlHeight;
    }

    public void setTlHeight(double tlHeight) {
        this.tlHeight = tlHeight;
    }

    public double getRedLayout() {
        return redLayout;
    }

    public void setRedLayout(double redLayout) {
        this.redLayout = redLayout;
    }

    public double getOrangeLayout() {
        return orangeLayout;
    }

    public void setOrangeLayout(double orangeLayout) {
        this.orangeLayout = orangeLayout;
    }

    public double getGreenLayout() {
        return greenLayout;
    }

    public void setGreenLayout(double greenLayout) {
        this.greenLayout = greenLayout;
    }
    
    
}
