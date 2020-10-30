/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dipl_project.UI;

import dipl_project.Dipl_project;
import dipl_project.Roads.CheckPoint;
import dipl_project.Roads.RoadSegment;
import dipl_project.Roads.WatchPoint;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author Honza
 */
public class UIRightMenu {
    private Group root;
    private ListView<ListView> trafficLightsGroups;
    private Group right, tls;
    private UIControll ui;
    private Label tlGroupsTime;
    public UIRightMenu(Group root, UIControll ui) {
        this.root = root;
        this.ui=ui;
        initMenu();
        
        initTrafficLightsGroups();
        updateTLGsPosition();
    }
    private void initMenu()
    {
        right=new Group();
        right.setLayoutY(115);
        
        
    }
    public void showRightMenu()
    {
        root.getChildren().add(right);
    }
    public void hideRightMenu()
    {
        root.getChildren().remove(right);
    }
    
    public void updateTLGsPosition()
    {
        Canvas canvas=ui.getCanvas();
        trafficLightsGroups.setMinSize(230, canvas.getHeight()-135);
        trafficLightsGroups.setMaxSize(230, canvas.getHeight()-135);
        right.setLayoutX(canvas.getWidth()-235);
        //trafficLightsGroups.setLayoutX(canvas.getWidth()-235);
        //tlGroupsTime.setLayoutX(canvas.getWidth()-235);
        //tlGroupsTime.setLayoutY(canvas.getHeight()-40);
        //tls.setLayoutX(canvas.getWidth()-235);
    }
    private void initTrafficLightsGroups()
    {
        tlGroupsTime=new Label("Čas: 0s");
        trafficLightsGroups=Dipl_project.getTlc().getTrafficLightsGroups();
        trafficLightsGroups.setLayoutY(10);
        
        right.getChildren().addAll(tlGroupsTime,trafficLightsGroups);
    }
    
    public void setTlGroupTime(String time)
    {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                tlGroupsTime.setText(time);
            }
        });
        
    }
}
