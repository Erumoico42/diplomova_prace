/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dipl_project.TrafficLights;

import dipl_project.Dipl_project;
import dipl_project.Roads.RoadSegment;
import dipl_project.UI.DrawControll;
import dipl_project.UI.EditationControll;
import dipl_project.UI.UIControll;
import java.awt.Point;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

/**
 *
 * @author Honza
 */
public class TrafficLight {
    private Image imgGreen=new Image(Dipl_project.class.getResource("Resources/trafficLights/green.png").toString());
    private Image imgOrangeToRed=new Image(Dipl_project.class.getResource("Resources/trafficLights/orangeToRed.png").toString());
    private Image imgRed=new Image(Dipl_project.class.getResource("Resources/trafficLights/red.png").toString());
    private Image imgOrangeToGreen=new Image(Dipl_project.class.getResource("Resources/trafficLights/orangeToGreen.png").toString());
    private Image imgNone=new Image(Dipl_project.class.getResource("Resources/trafficLights/none.png").toString());
    private Point location, locOrig;
    private double layoutX, layoutY, startX, startY, distX, distY;
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
    private final int id;
    private double tlWidth=20, tlHeight=50;
    private ImageView tlImage=new ImageView();
    private int status;
    private int timeCountDown;
    UIControll ui=Dipl_project.getUI();
    private Timer timer;
    private TimerTask timerTask;
    private boolean orangeSwitching=false;
    private int lastStatus;
    public TrafficLight(double x, double y, int id) {
        this.id=id;

        tlBox=new HBox();
        tlBox.getChildren().addAll(tlImage);
        tlImage.setFitWidth(tlWidth);
        tlImage.setFitHeight(tlHeight);
        location=new Point((int)x-5, (int)y-10);
        moveTL(location.getX(), location.getY());
        setStatus(0);
        initHandlers();
        DrawControll dc=Dipl_project.getDC();
        TrafficLight tlAct=dc.getActualTL();
        if(tlAct!=null)
            tlAct.deselectTL();
        dc.setActualTL(getThis());
        selectTL();
        zoomTL(EditationControll.getZoomRatio());
        
    }
    private void initHandlers()
    {
        tlBox.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                DrawControll dc=Dipl_project.getDC();
                TrafficLight tlAct=dc.getActualTL();
                
                if(event.getButton()==MouseButton.PRIMARY){
                    
                    if(ui.isAddTLToList())
                    {
                        ui.getActualTLGroup().addTrafficLightSwitch(new TrafficLightSwitch(1, getThis(), ui.getActualTLGroup()));
                        ui.setAddTLToList(false);
                    }
                    else
                    {
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
                    }
                    
                }else if(event.getButton()==MouseButton.SECONDARY){
                    RoadSegment rsAct = dc.getActualRS();
                    int status=Dipl_project.getDC().getDrawStatus();
                    if(rsAct!=null && status==0)
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
                System.out.println(location);
            }
        });
        tlBox.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                moveTL(event.getX(), event.getY());
            }
        });
    }
    public Point getPosition()
    {
        return location;
    }
    public void moveTL(double x, double y)
    {
        
        layoutX=x-distX;
        layoutY=y-distY;
        System.out.println(distX+"x"+distY);
        moveImg(layoutX,layoutY);
        distX = startX-layoutX;
        distY = startY-layoutY;
        location.setLocation(layoutX, layoutY);
    }
    public void move(double x, double y)
    {
        layoutX=x;
        layoutY=y;
        moveImg(layoutX,layoutY);
    }
    public void setTLPosition(double x, double y)
    {
        moveImg(x,y);
        layoutX=x;
        layoutY=y;
        location.setLocation(x, y);
    }

    public int getTimeCountDown() {
        return timeCountDown;
    }

    public void setTimeCountDown(int timeCountDown) {
        this.timeCountDown = timeCountDown;
    }
    
    public void selectTL()
    {
        tlBox.setStyle(STYLE_SELECT_PRIM);
        layoutX-=2;
        layoutY-=2;
        //location.setLocation(layoutX, layoutY);
        moveImg(layoutX,layoutY);
    }
    public void rsSelect()
    {
        tlBox.setStyle(STYLE_SELECT_SEC);
        layoutX-=2;
        layoutY-=2;
        moveImg(layoutX,layoutY);
    }
    public void startOrangeSwitching()
    {
        timer=new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run(){
               
               activateSwitch();
            }
        };
        timer.schedule(timerTask, 1000, 1000);
    }
    public void stopOrangeSwitching()
    {
        if(timer!=null)
            timer.cancel();
        if(timerTask!=null)
            timerTask.cancel();
    }
    private void activateSwitch()
    {
        if(status==4)
        {
            setStatus(1);
        }
        else
        {
            setStatus(4);
        }
    }

    public void setOrangeSwitching(boolean orangeSwitching) {
        this.orangeSwitching = orangeSwitching;
    }
    
    public boolean isOrangeSwitching() {
        return orangeSwitching;
    }
    public void deselectTL()
    {
        if(!tlBox.getStyle().equals(STYLE_DEF))
        {
            layoutX+=2;
            layoutY+=2;
        }
        //location.setLocation(layoutX, layoutY);
        tlBox.setStyle(STYLE_DEF);
        moveImg(layoutX,layoutY);
    }
    private TrafficLight getThis()
    {
        return this;
    }
    public void zoomTL(double ratio)
    {
        tlWidth*=ratio;
        tlHeight*=ratio;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                tlImage.setFitWidth(tlWidth);
                tlImage.setFitHeight(tlHeight);
            }
        });
        

    }
    public int getStatus()
    {
        return status;
    }
    public void setStatus(int status)
    {
        this.status = status;
        switch(status)
        {
            case 0:
            {
                changeImage(imgGreen);
                break;
            }
            case 1:
            {
                changeImage(imgOrangeToRed);
                break;
            }
            case 2:
            {
                changeImage(imgRed);
                break;
            }
            case 3:
            {
                changeImage(imgOrangeToGreen);
                break;
            }
            case 4:
            {
                changeImage(imgNone);
                break;
            }
            
        }
    }
    private void changeImage(Image newImg)
    {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                tlImage.setImage(newImg);
            }
        });
    }
    private void moveImg(double newX, double newY)
    {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                tlBox.setLayoutX(newX);
                tlBox.setLayoutY(newY);
            }
        });
    }
    public void removeTL()
    {
        Dipl_project.getDC().setActualTL(null);
        Dipl_project.getDC().getTrafficLights().remove(this);
        Dipl_project.getUI().removeTL(getThis());
    }
    public void setLocOrig(Point locOrig) {
        this.locOrig = locOrig;
    }

    public Point getLocOrig() {
        return locOrig;
    }

    public int getId() {
        return id;
    }

    public HBox getTlImage() {
        return tlBox;
    }

    public void setTlImage(ImageView tlImage) {
        this.tlImage = tlImage;
    }
    public void setLastStatus()
    {
        lastStatus=status;
    }
    public int getLastStatus()
    {
        return lastStatus;
    }
}
