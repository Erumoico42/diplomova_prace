/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TrafficLights;

import dipl_project.Dipl_project;
import dipl_project.Roads.MyCurve;
import dipl_project.UI.DrawControll;
import java.awt.Point;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.CubicCurve;

/**
 *
 * @author Honza
 */
public class TrafficLightsConnection {
    private final TrafficLight tl, tlRev;
    private DrawControll dc=dipl_project.Dipl_project.getDC();
    private int switchDelay=2;
    private CubicCurve connectCurve;
    private int status, startStatus;
    private int timeWait=0;
    private Timer timer;
    private TimerTask timerTask;

    public TrafficLightsConnection(TrafficLight tl, TrafficLight tlRev, int status, int startStatus, CubicCurve curve) {
        this.tl = tl;
        this.status=status;
        
        this.startStatus=startStatus;
        this.tlRev=tlRev;
        connectCurve=curve;
        switch(startStatus)
        {
            case 0:
            {
                connectCurve.setStroke(Color.GREEN);
                break;
            }
            case 1:
            {
                connectCurve.setStroke(Color.ORANGE);
                break;
            }
            case 2:
            {
                connectCurve.setStroke(Color.RED);
                break;
            }
        }
        
        initHandlers();
    }

    public int getStatus() {
        return status;
    }
    
    public TrafficLightsConnection getThisConnection()
    {
        return this;
    }
    private void initHandlers()
    {
        connectCurve.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                selectConnectCurve();
            }
        });
        connectCurve.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                TrafficLightsConnection selectedConnection=dc.getSelectedConnection();
                if((selectedConnection!=null && !selectedConnection.equals(getThisConnection())) || selectedConnection==null)
                    deselectConnectCurve();
            }
        });
        connectCurve.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                TrafficLightsConnection selectedConnection=dc.getSelectedConnection();
                if(event.getButton()==MouseButton.PRIMARY)
                {
                    if(selectedConnection!=null)
                    {
                        if(selectedConnection.equals(getThisConnection()))
                        {
                            deselectConnectCurve();
                            dc.setSelectedConnection(null);
                        }
                        else
                        {
                            selectedConnection.deselectConnectCurve();
                            dc.setSelectedConnection(getThisConnection());
                        }

                    }
                    else{
                        selectConnectCurve();
                        dc.setSelectedConnection(getThisConnection());
                    }
                }
                else
                if(event.getButton()==MouseButton.SECONDARY)
                {
                    if(selectedConnection!=null)
                            selectedConnection.deselectConnectCurve();
                    dc.setSelectedConnection(getThisConnection());
                    selectConnectCurve();
                    Dipl_project.getUI().showPopUpTLConnection(new Point((int)event.getX(),(int)event.getY()));
                }
            }
        });
    }
    public int getSwitchDelay() {
        return switchDelay;
    }

    public void setSwitchDelay(int switchDelay) {
        this.switchDelay = switchDelay;
    }

    public TrafficLight getTl() {
        return tl;
    }

    public CubicCurve getConnectCurve() {
        return connectCurve;
    }
    public void selectConnectCurve()
    {
        connectCurve.setStrokeWidth(5);
              
    }
    public int getStartStatus()
    {
        return startStatus;
    }
    public void removeConnection()
    {
        tl.removeConnection(this);
        tlRev.removeRevConnection(this);
        Dipl_project.getUI().removeComponents(connectCurve);
        Dipl_project.getDC().setSelectedConnection(null);
    }
    
    public void deselectConnectCurve()
    {
         connectCurve.setStrokeWidth(2);
    }

    public TrafficLight getTlRev() {
        return tlRev;
    }
    public void startSwitch()
    {
        if(timer!=null)
            timer.cancel();
        if(timerTask!=null)
            timerTask.cancel();
        timer=new Timer();
        timeWait=0;
        timerTask = new TimerTask() {
            @Override
            public void run(){
                
                timeWait++;
                if(timeWait>switchDelay)
                {
                    tl.setStatus(status, false);
                    tl.changeStatusOne();
                    timer.cancel();
                    timerTask.cancel();
                }
                
            }
        };
        timer.schedule(timerTask, 1000, 1000);
    }
}
