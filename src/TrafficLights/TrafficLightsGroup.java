/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TrafficLights;

import dipl_project.Dipl_project;
import dipl_project.UI.UIControll;
import javafx.scene.control.Label;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javax.swing.Box;

/**
 *
 * @author Honza
 */
public class TrafficLightsGroup {
    private int id;
    private int time;
    private List<TrafficLightSwitch> trafficLightSwitchList=new ArrayList<>();
    private ListView<HBox> groupViews=new ListView<HBox>();
    private HBox groupInfo=new HBox();
    private HBox addToGroup=new HBox();
    private UIControll ui=Dipl_project.getUI();
    public TrafficLightsGroup(int id, int time) {
        
        Button btnAdd=new Button("+");
        btnAdd.setFont(Font.font(10));
        btnAdd.setMaxSize(30, 30);
        btnAdd.setMinSize(30, 30);
        Label lblTime=new Label(" Čas: "+String.valueOf(time));
        lblTime.setMinSize(130, 30);
        lblTime.setMaxSize(130, 30);
        groupInfo.getChildren().addAll(lblTime,btnAdd);
        groupInfo.setBackground(new Background(new BackgroundFill(Color.rgb(240, 240, 240),new CornerRadii(2),null)));
        btnAdd.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Dipl_project.getUI().setAddTLToList(true);
                Dipl_project.getUI().setActualTLGroup(getThis());
            }
        });
        
        groupViews.getItems().addAll(groupInfo);
        this.id = id;
        this.time = time;
        
        fitGroupViewsBySize();
    }
    private TrafficLightsGroup getThis()
    {
        return this;
    }
    public ListView getGroupViews()
    {
        return groupViews;
    }
    public void fitGroupViewsBySize()
    {
        groupViews.setMaxWidth(180);
        groupViews.setPrefHeight(trafficLightSwitchList.size() * 38 + 70);
        
    }
    public void addTrafficLightSwitch(TrafficLightSwitch tls)
    {
        trafficLightSwitchList.add(tls);
        groupViews.getItems().add(tls.getTlSwitchBox());
        fitGroupViewsBySize();
        
    }
    public void removeTrafficLightSwitch(TrafficLightSwitch tls)
    {
        trafficLightSwitchList.remove(tls);
        groupViews.getItems().remove(tls.getTlSwitchBox());
        fitGroupViewsBySize();
    }

    public List<TrafficLightSwitch> getTrafficLightSwitchList() {
        return trafficLightSwitchList;
    }

    public int getTime() {
        return time;
    }

    public int getId() {
        return id;
    }
    
    
}
