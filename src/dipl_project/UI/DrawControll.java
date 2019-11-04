/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dipl_project.UI;

import dipl_project.Roads.MyCurve;
import dipl_project.Roads.Connect;
import dipl_project.Roads.RoadCreator;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author Honza
 */
public  class DrawControll {
    private UIControll ui;
    private RoadCreator rc;
    private Canvas canvas;
    private Connect actualConnect;
    private MyCurve actualCurve;
    private List<Connect> connects=new ArrayList<>();
    private List<MyCurve> curves=new ArrayList<>();
    private int idCurve=0;
    public DrawControll(UIControll ui, RoadCreator rc)
    {
        this.ui=ui;
        this.rc=rc;
        this.canvas=ui.getCanvas();
        initHandlers();
    }
    public void newRoad()
    {
        rc.createRoad(connects, curves);
        ui.setStartSegments(rc.getStartSegments());
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
                        if(actualConnect==null){
                            actualConnect=newConnect(event.getX(), event.getY());
                            actualConnect.select();
                        }
                        else
                        {
                            Connect newConnect=newConnect(event.getX(), event.getY());
                            newCurve(newConnect);
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
                    if(actualConnect!=null)
                    {
                        actualConnect.move(event.getX(), event.getY());
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
        });
        ui.getPrimaryStage().heightProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            double oldHeight=canvas.getHeight();
            double newHeight=newValue.doubleValue()-oldValue.doubleValue();
            canvas.setHeight(oldHeight+newHeight);
        });
                
    }

    private Connect newConnect(double x, double y)
    {
        Connect connect=new Connect(new Point((int)x, (int)y));
        ui.addConnect(connect);
        connects.add(connect);
        return connect;
    }
    public void setActualConnect(Connect connect)
    {
        this.actualConnect=connect;
    }
    public void newCurve(Connect endConnect)
    {
        if(actualConnect!=null)
        {
            MyCurve curve=new MyCurve(actualConnect, endConnect);
            curve.setId(idCurve);
            curves.add(curve);
            idCurve++;
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
    
}
