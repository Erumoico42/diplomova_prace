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
    private int time=10;
    private boolean enableSwitchRed=true, enableSwitchGreen=true,enableSwitchOrange=true;
    private int timeToSwitchRed=10, timeToSwitchGreen=10, timeToSwitchOrange=2;
    private Timer timer;
    private TimerTask timerTask;
    private double layoutX, layoutY, startX, startY, distX, distY;
    private boolean run=false;
    private ImageView tlImage=new ImageView();
    private double moveColorX, moveColorY;
    
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
            

    public TrafficLight(double x, double y) {
        circleRed=new Circle(7,Color.RED);
        circleGreen=new Circle(7, Color.LIGHTGREEN);
        circleOrange=new Circle(7,Color.ORANGE);
        circleRed.setVisible(false);
        circleOrange.setVisible(false);
        circleGreen.setVisible(false);
        tlBox=new HBox();
        tlBox.getChildren().addAll(tlImage);
        tlImage.setFitWidth(20);
        tlImage.setFitHeight(50);
        moveTL(x-10, y-25);
        setStatus(0);
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
            double tlX=tlBox.getLayoutX();
            double tlY=tlBox.getLayoutY();
            circleRed.setCenterX(tlX+10);
            circleRed.setCenterY(tlY+redLayout);
            circleOrange.setCenterX(tlX+10);
            circleOrange.setCenterY(tlY+orangeLayout);
            circleGreen.setCenterX(tlX+10);
            circleGreen.setCenterY(tlY+greenLayout);
            deselectTL();
        }
        tlBox.setDisable(enable);
        circleRed.setVisible(enable);
        circleOrange.setVisible(enable);
        circleGreen.setVisible(enable);
    }
    private void moveCurveToConnect(CubicCurve curve)
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
    private void newConnection(double x, double y)
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
            System.out.println("connect to "+statusToConnect);
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
    private void moveTL(double x, double y)
    {
        
        layoutX=x-distX;
        layoutY=y-distY;
        tlBox.setLayoutX(layoutX);
        tlBox.setLayoutY(layoutY);
        distX = startX-layoutX;
        distY = startY-layoutY;
        moveConnectCurveStart();
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
        tlBox.setLayoutX(layoutX);
        tlBox.setLayoutY(layoutY);
    }
    public void deselectTL()
    {
        tlBox.setStyle(STYLE_DEF);
        if(!tlBox.getStyle().equals(STYLE_DEF))
        {
            layoutX+=2;
            layoutY+=2;
        }
        tlBox.setLayoutX(layoutX);
        tlBox.setLayoutY(layoutY);
    }
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
        switch(status)
        {
            case 0:
            {
                tlImage.setImage(imgGreen);
                break;
            }
            case 1:
            {
                tlImage.setImage(imgOrangeToRed);
                break;
            }
            case 2:
            {
                tlImage.setImage(imgRed);
                break;
            }
            case 3:
            {
                tlImage.setImage(imgOrangeToGreen);
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
            time=timeToSwitchRed;
    }

    public int getTimeToSwitchGreen() {
        return timeToSwitchGreen;
    }

    public void setTimeToSwitchGreen(int timeToSwitchGreen) {
        this.timeToSwitchGreen = timeToSwitchGreen;
        if(status==0)
            time=timeToSwitchGreen;
    }

    public int getTimeToSwitchOrange() {
        return timeToSwitchOrange;
    }

    public void setTimeToSwitchOrange(int timeToSwitchOrange) {
        this.timeToSwitchOrange = timeToSwitchOrange;
        if(status==1 || status==3)
            time=timeToSwitchOrange;
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
            timerTask.cancel();
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
    public void changeStatusOne()
    {
        switch(status)
            {
                case 0:
                {
                    //green to orange
                        time=timeToSwitchOrange;
                        setStatus(1);
                    break;
                }
                case 1:
                {
                    //orange to red
                    time=timeToSwitchRed;
                    setStatus(2);
                    for (TrafficLightsConnection tlc : connectionsRed) {
                        tlc.startSwitch();
                    }
                    break;
                }
                case 2:
                {
                    //red to orange
                        time=timeToSwitchOrange;
                        setStatus(3);
                        
                    
                    
                    break;
                }
                case 3:
                {
                    //orange to green

                    time=timeToSwitchGreen;
                    setStatus(0);
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
                        time=timeToSwitchOrange;
                        setStatus(1);
                        
                    }
                    
                    break;
                }
                case 1:
                {
                    //orange to red
                    time=timeToSwitchRed;
                    setStatus(2);
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
                        time=timeToSwitchOrange;
                        setStatus(3);
                        
                    }
                    
                    break;
                }
                case 3:
                {
                    //orange to green

                    time=timeToSwitchGreen;
                    setStatus(0);
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
    
}