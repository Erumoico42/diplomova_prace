/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TrafficLights;

import java.util.Timer;
import java.util.TimerTask;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;


/**
 *
 * @author Honza
 */
public class TrafficLightSwitch {
    private int newStatus, switchTime, actualTime=1;
    private TrafficLight trafficLight;
    private Timer timer;
    private TimerTask timerTask;
    private HBox tlSwitchBox;
    private TrafficLightsGroup tlg;
    public TrafficLightSwitch(int newStatus, int switchTime, TrafficLight trafficLight, TrafficLightsGroup tlg) {
        tlSwitchBox=new HBox();
        Button btnRemoveTLS=new Button("-");
        btnRemoveTLS.setMaxSize(30, 30);
        btnRemoveTLS.setMinSize(30, 30);
        btnRemoveTLS.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                tlg.removeTrafficLightSwitch(getThis());
            }
        });
        tlSwitchBox.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                trafficLight.selectTL();
            }
        });
        tlSwitchBox.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                trafficLight.deselectTL();
            }
        });
        Label lblInfo=new Label("ID: "+String.valueOf(trafficLight.getId())+" -> red ");
        lblInfo.setMinSize(120, 30);
        lblInfo.setMaxSize(120, 30);
        tlSwitchBox.getChildren().addAll(lblInfo,btnRemoveTLS);
        this.newStatus = newStatus;
        this.switchTime = switchTime;
        this.trafficLight = trafficLight;
        this.tlg=tlg;
    }
    public void activate()
    {
        setOrangeStatus();
        timer=new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run(){
               activateTLs();
               actualTime++;
            }
        };
        timer.schedule(timerTask, 1000, 1000);
    }
    private void stopSwitch()
    {
        timerTask.cancel();
        timer.cancel();
        actualTime=0;
    }
    private void activateTLs()
    {
        if(actualTime==switchTime)
        {
            trafficLight.setStatus(newStatus);
            stopSwitch();
            System.out.println("activating");
        }
    }
    private void setOrangeStatus()
    {
        int actualStatus=trafficLight.getStatus();
        if(newStatus==2)
        {
            trafficLight.setStatus(1);
            System.out.println("orange");
        }
        else if(newStatus==3)
        {
            trafficLight.setStatus(2);
        }
    }
    private TrafficLightSwitch getThis()
    {
        return this;
    }

    public int getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(int newStatus) {
        this.newStatus = newStatus;
    }

    public int getSwitchTime() {
        return switchTime;
    }

    public void setSwitchTime(int switchTime) {
        this.switchTime = switchTime;
    }

    public HBox getTlSwitchBox() {
        return tlSwitchBox;
    }
    
    
}
