/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dipl_project.UI;

import TrafficLights.TrafficLight;
import TrafficLights.TrafficLightsConnection;
import dipl_project.Dipl_project;
import dipl_project.Roads.MyCurve;
import dipl_project.Roads.Connect;
import dipl_project.Roads.RoadCreator;
import dipl_project.Roads.RoadSegment;
import dipl_project.Simulation.SimulationControll;
import dipl_project.Vehicles.Animation;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author Honza
 */
public  class DrawControll {
    private UIControll ui;
    private RoadCreator rc;
    
    private int idLastConnect=0;
    private Canvas canvas, moveCanvas;
    private Connect actualConnect;
    private MyCurve actualCurve, selectedCurve;
    private TrafficLight actualTL;
    private RoadSegment actualRS;
    private TrafficLightsConnection actualTLConnection;
    private List<Connect> connects=new ArrayList<>();
    private List<MyCurve> curves=new ArrayList<>();
    private List<TrafficLight> trafficLights=new ArrayList<>();
    private int idLastTL=0;
    private int idLastCurve=0;
    private int drawStatus=0;
    private Rectangle menuBG;
    private boolean loadingMap=false;
    public DrawControll(UIControll ui, RoadCreator rc)
    {
        this.ui=ui;
        this.rc=rc;
        this.canvas=ui.getCanvas();
        menuBG=ui.getMenuBG();
        moveCanvas=ui.getMoveCanvas();
        initHandlers();
    }

    public void setIdLastConnect(int idLastConnect) {
        this.idLastConnect = idLastConnect;
    }

    public void setIdLastTL(int idLastTL) {
        this.idLastTL = idLastTL;
    }

    public void setIdLastCurve(int idLastCurve) {
        this.idLastCurve = idLastCurve;
    }
    
    public boolean isLoadingMap() {
        return loadingMap;
    }

    public void setLoadingMap(boolean loadingMap) {
        this.loadingMap = loadingMap;
    }
    
    public void newRoad()
    {
        if(!loadingMap)
        {
            rc.createRoad(connects, curves);
            ui.setStartSegments(rc.getStartCarSegments(), rc.getStartTramSegments());
        }
    }

    public TrafficLight getActualTL() {
        return actualTL;
    }

    public void setActualTL(TrafficLight actualTL) {
        this.actualTL = actualTL;
        ui.enableEditTL(actualTL!=null);
    }
    
    public void setDrawStatus(int drawStatus) {
        this.drawStatus = drawStatus;
    }

    public List<TrafficLight> getTrafficLights() {
        return trafficLights;
    }
    public void addTrafficLight(TrafficLight tl)
    {
        trafficLights.add(tl);
    }
    private void initHandlers()
    {
        canvas.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(!ui.isPopupShown())
                {
                    if(event.getButton()==MouseButton.PRIMARY)
                    {
                        switch(drawStatus)
                        {
                            case 0:
                            {
                                if(actualConnect==null){
                                    actualConnect=newConnect(event.getX(), event.getY());
                                    actualConnect.setTramConnect(Dipl_project.getUI().isTramCreating());
                                    actualConnect.select();
                                }
                                else
                                {
                                    Connect newConnect=newConnect(event.getX(), event.getY());
                                    if(actualConnect.isTramConnect() == Dipl_project.getUI().isTramCreating())
                                        newCurve(newConnect);
                                     else
                                    {
                                        actualConnect.deselect();
                                        actualConnect=newConnect(event.getX(), event.getY());
                                        actualConnect.setTramConnect(Dipl_project.getUI().isTramCreating());
                                        actualConnect.select();
                                    }   
                                }
                                break;
                            }
                            case 1:
                            {
                                TrafficLight tl=new TrafficLight(event.getX(), event.getY(), idLastTL);
                                idLastTL++;
                                trafficLights.add(tl);
                                tl.enableConnectLights(ui.isEnabledConnectTL());
                                ui.addComponents(tl.getTlImage(), tl.getCircleRed(), 
                                        tl.getCircleOrange(), tl.getCircleGreen());
                                break;
                            }
                        }
                        
                    }
                }else
                    ui.hidePopUp();
                    
            }
        });
        canvas.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(!ui.isPopupShown())
                {
                    switch(drawStatus)
                    {
                        case 0:
                        {
                            if(event.getButton()==MouseButton.PRIMARY)
                            {
                                if(actualConnect!=null)
                                {
                                    actualConnect.move(event.getX(), event.getY());
                                }
                            }
                            break;
                        }

                    }
                    
                }
                else
                    ui.hidePopUp();
                    
            }
        });
        ui.getPrimaryStage().widthProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            double oldWidth=canvas.getWidth();
            double newWidth=newValue.doubleValue()-oldValue.doubleValue();
            canvas.setWidth(oldWidth+newWidth);
            menuBG.setWidth(oldWidth+newWidth);
            moveCanvas.setWidth(oldWidth+newWidth);
            ui.updateCPsPosition();
        });
        ui.getPrimaryStage().heightProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            double oldHeight=canvas.getHeight();
            double newHeight=newValue.doubleValue()-oldValue.doubleValue();
            canvas.setHeight(oldHeight+newHeight);
            moveCanvas.setHeight(oldHeight+newHeight);
            ui.updateCPsPosition();
        });
        moveCanvas.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(ui.getMoveStatus()==1)
                {
                BackgroundControll.backgroundClick(event.getX(), event.getY());
                }else if(ui.getMoveStatus()==2)
                {
                    EditationControll.editClick(event.getX(), event.getY());
                }
                    
            }
        });
        moveCanvas.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(ui.getMoveStatus()==1)
                {
                    if(event.getButton()==MouseButton.PRIMARY)
                    {
                        BackgroundControll.moveBackground(event.getX(), event.getY());
                    }
                    else if(event.getButton()==MouseButton.SECONDARY)
                    {
                        BackgroundControll.rotateBackground(event.getX(), event.getY());
                    }
                }else if(ui.getMoveStatus()==2)
                {
                    if(event.getButton()==MouseButton.PRIMARY)
                    {
                        //BackgroundControll.moveBackground(event.getX(), event.getY());
                        EditationControll.moveAll(event.getX(), event.getY());
                    }
                }
                
            }
        });
        moveCanvas.setOnScroll(new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {
                if(ui.getMoveStatus()==1)
                {
                    BackgroundControll.zoomBackground(event.getDeltaY());
                }
                else if(ui.getMoveStatus()==2)
                {
                    EditationControll.zoomAll(event.getDeltaY());
                }
            }
        });
    }
    private Connect newConnect(double x, double y)
    {
        Connect connect=new Connect(new Point((int)x, (int)y), idLastConnect);
        idLastConnect++;
        ui.addConnect(connect);
        connects.add(connect);
        return connect;
    }
    public void addConnect(Connect con)
    {
        connects.add(con);
    }
    public void addCurve(MyCurve curve)
    {
        curves.add(curve);
    }
    public void setActualConnect(Connect connect)
    {
        this.actualConnect=connect;
    }
    public void newCurve(Connect endConnect)
    {
        if(actualConnect!=null)
        {
            MyCurve curve=new MyCurve(actualConnect, endConnect, idLastCurve);
            curves.add(curve);
            idLastCurve++;
            endConnect.setTramConnect(actualConnect.isTramConnect());
            if(actualConnect.isTramConnect())
                curve.setTramCurve();
            ui.addCurve(curve);
            actualCurve=curve;
            actualConnect.deselect();
            actualConnect=endConnect;
            
        }
        endConnect.select();
    }
    public MyCurve getActualCurve() {
        return actualCurve;
    }
    
    public Connect getActualConnect() {
        return actualConnect;
    }

    public RoadSegment getActualRS() {
        return actualRS;
    }

    public void setActualRS(RoadSegment actualRS) {
        this.actualRS = actualRS;
    }

    public MyCurve getSelectedCurve() {
        return selectedCurve;
    }

    public void setSelectedCurve(MyCurve selectedCurve) {
        this.selectedCurve = selectedCurve;
        ui.enableCurveEdit(selectedCurve!=null);
    }

    public TrafficLightsConnection getSelectedConnection() {
        return actualTLConnection;
    }

    public void setSelectedConnection(TrafficLightsConnection connection) {
        actualTLConnection=connection;
        if(connection!=null)
        {
            ui.setChangeConnectDelay(connection.getSwitchDelay());
        }
        ui.enableChangeConnectDelay(connection!=null);
    }
    public void cleanAll()
    {
        Dipl_project.getAnim().cleanVehicles();
        for (Connect connect : connects) {
            connect.removeConnect();
        }
        List<TrafficLight> tls=new ArrayList<>();
        tls.addAll(trafficLights);
        for (TrafficLight trafficLight : tls) {
            trafficLight.removeTL();
        }
        
        BackgroundControll.removeBG();
    }
}
