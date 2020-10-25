/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TrafficLights;

import dipl_project.Dipl_project;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;

/**
 *
 * @author Honza
 */
public class TrafficLightsControll {
    private int timeSeconds=0, maxTime, lastIdTLG=0;
    private Timer timer;
    private TimerTask timerTask;
    private List<TrafficLightsGroup> tlsGroups=new ArrayList<>();
    private ListView trafficLightsGroups=new ListView<>();
        
    public TrafficLightsControll()
    {
        trafficLightsGroups.setFocusTraversable(false);
        for (int i = 0; i < 3; i++) {
            TrafficLightsGroup tlg=new TrafficLightsGroup(lastIdTLG, i);
            addTLGroup(i,tlg);
        }
        initAddNewGroup();
    }
    public void initAddNewGroup()
    {
        ListView<HBox> newGroup=new ListView<HBox>();
        newGroup.setFocusTraversable(false);
        HBox addBox=new HBox();
        Button addButton=new Button("+");
        Button removeButton=new Button("-");
        addBox.setAlignment(Pos.CENTER);
        Spinner spinnerTime=new Spinner(0, Integer.MAX_VALUE, tlsGroups.size());
        spinnerTime.setMinWidth(80);
        spinnerTime.setMaxWidth(80);   
        spinnerTime.valueProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                int newTime=(int)newValue;
                for (TrafficLightsGroup tlsGroup : tlsGroups) {
                    if(newTime==tlsGroup.getTime())
                    {
                        addButton.setDisable(true);
                        removeButton.setDisable(false);
                        break;
                    }
                    else
                    {
                        addButton.setDisable(false);
                        removeButton.setDisable(true);
                    }
                        
                }
            }
        });
        newGroup.setMaxWidth(210);
        newGroup.setPrefHeight(43);
        
        addButton.setMaxSize(40, 30);
        addButton.setMinSize(40, 30);
        addButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                int newTime=(int)spinnerTime.getValue();
                TrafficLightsGroup tlg=new TrafficLightsGroup(lastIdTLG,newTime);
                int bestPosition=findBestPosition(newTime);
                addTLGroup(bestPosition,tlg);
                spinnerTime.getValueFactory().setValue(newTime+1);
            }
        });
        
        removeButton.setMaxSize(40, 30);
        removeButton.setMinSize(40, 30);
        removeButton.setDisable(true);
        removeButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                int oldTime=(int)spinnerTime.getValue();
                for (int i = 0; i < tlsGroups.size(); i++) {
                    TrafficLightsGroup tlg=tlsGroups.get(i);
                    if(tlg.getTime()==oldTime)
                    {
                        tlsGroups.remove(tlg);
                        trafficLightsGroups.getItems().remove(tlg.getGroupViews());
                        if(!tlsGroups.isEmpty())
                            spinnerTime.getValueFactory().setValue(tlsGroups.get(tlsGroups.size()-1).getTime());
                        else
                        {
                            removeButton.setDisable(true);
                            addButton.setDisable(false);
                            spinnerTime.getValueFactory().setValue(0);
                        }
                        break;
                    }
                }
            }
        });
        addBox.getChildren().addAll(removeButton,spinnerTime,addButton);
        newGroup.getItems().add(addBox);
        
        trafficLightsGroups.getItems().add(newGroup);
    }
    private int findBestPosition(int newTime)
    {
        int position=1;
        boolean found=false;
        if(tlsGroups.size()>1)
        {
            for (int i = 0; i < tlsGroups.size()-1; i++) {
                TrafficLightsGroup tlg0=tlsGroups.get(i);
                TrafficLightsGroup tlg1=tlsGroups.get(i+1);
                if(tlg0.getTime()<newTime && tlg1.getTime()>newTime)
                {
                    
                    position=i+1;
                    found=true;
                    break;
                }
                    
            }
        }
        if(newTime==0)
        {
            position=0;
            found=true;
        }
                
        if(!found)
            position=tlsGroups.size();
        return position;
    }

    public ListView getTrafficLightsGroups() {
        return trafficLightsGroups;
    }
    
    
    public void resetTimer()
    {
        timeSeconds=0;
    }
    public void startTrafficLights()
    {
        for (TrafficLight trafficLight : Dipl_project.getDC().getTrafficLights()) {
            if(trafficLight.isOrangeSwitching())
                trafficLight.startOrangeSwitching();
        }
        timer=new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run(){
               activateTLs();
               timeSeconds++;
                
               if(timeSeconds>maxTime)
                   timeSeconds=0;
               Dipl_project.getUI().setTlGroupTime("Čas: "+String.valueOf(timeSeconds)+"s");
            }
        };
        timer.schedule(timerTask, 1000, 1000);
    }
    public void addTLGroup(int position, TrafficLightsGroup tlg)
    {
        trafficLightsGroups.getItems().add(position,tlg.getGroupViews());
        lastIdTLG++;
        tlsGroups.add(position,tlg);
        if(tlg.getTime()>maxTime)
            maxTime=tlg.getTime();
    }

    public List<TrafficLightsGroup> getTlsGroups() {
        return tlsGroups;
    }
    
    public void stopTrafficLights()
    {
        if(timerTask!=null)
            timerTask.cancel();
        if(timer!=null)
            timer.cancel();
        for (TrafficLight trafficLight : Dipl_project.getDC().getTrafficLights()) {
            if(trafficLight.isOrangeSwitching())
                trafficLight.stopOrangeSwitching();
        }
        for (TrafficLightsGroup tlsGroup : tlsGroups) {
            for (TrafficLightSwitch trafficLightSwitch : tlsGroup.getTrafficLightSwitchList()) {
                trafficLightSwitch.stopSwitch();
            }
        }
    }
    private void activateTLs()
    {
        for (TrafficLightsGroup trafficLightGroup : tlsGroups) {
            if(timeSeconds==trafficLightGroup.getTime())
            {
                for (TrafficLightSwitch trafficLightSwitch : trafficLightGroup.getTrafficLightSwitchList()) {
                    trafficLightSwitch.activate();
                }
                break;
            }
        }
    }
}
