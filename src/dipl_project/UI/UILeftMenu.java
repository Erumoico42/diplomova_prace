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
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 *
 * @author Honza
 */
public class UILeftMenu {
    private Image imgStreetIcon=new Image(Dipl_project.class.getResource("Resources/menuIcons/streetIcon.png").toString());
    private Image imgTLIcon=new Image(Dipl_project.class.getResource("Resources/menuIcons/trafficLightIcon.png").toString());
    private Image imgBackgroundIcon=new Image(Dipl_project.class.getResource("Resources/menuIcons/backgroundIcon.png").toString());
    private Group root;
    private Button streetEdit;
    private Button trafficLightsEdit;
    private Button backgroundEdit;
     private ListView<HBox> selectedCPs;
    private RadioButton priority;
    private ToggleGroup priorityGroup;
    private RadioButton watch;
    private boolean priorityCP;
    private boolean watchCP=false;
    private VBox left;
    private Group cps;
    private final UIControll ui;
    public UILeftMenu(Group root, UIControll ui)
    {
        this.ui=ui;
        this.root=root;
        
        initCPList();
        initMenu();
        updateCPsPosition();
    }
    private void switchMenuButtons(Button menu)
    {
        streetEdit.setDisable(false);
        trafficLightsEdit.setDisable(false);
        backgroundEdit.setDisable(false);
        menu.setDisable(true);
    }
    private void initMenu()
    {
        left=new VBox();
        
        left.setAlignment(Pos.TOP_LEFT);
        left.setLayoutY(115);
        left.setLayoutX(5);
        streetEdit=new Button();
        streetEdit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                switchMenuButtons(streetEdit);
                Dipl_project.getDC().setDrawStatus(0);
                ui.getUiRightMenu().hideRightMenu();
                ui.setEditMode(false);
            }
        });
        streetEdit.setDisable(true);
        streetEdit.setPrefSize(30, 30);
        
        ImageView ivStreetIcon=new ImageView(imgStreetIcon);
        ivStreetIcon.setFitHeight(25);
        ivStreetIcon.setPreserveRatio(true);
        streetEdit.setGraphic(ivStreetIcon);
        
        trafficLightsEdit=new Button();
        trafficLightsEdit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                switchMenuButtons(trafficLightsEdit);
                Dipl_project.getDC().setDrawStatus(1);
                ui.getUiRightMenu().showRightMenu();
                ui.setEditMode(false);
                //Dipl_project.getUI().getUiTopMenu().showTLMenu();
            }
        });
        trafficLightsEdit.setPrefSize(30, 30);
        
        ImageView ivTLIcon=new ImageView(imgTLIcon);
        ivTLIcon.setFitHeight(25);
        ivTLIcon.setPreserveRatio(true);
        trafficLightsEdit.setGraphic(ivTLIcon);
        
        backgroundEdit=new Button();
        backgroundEdit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                switchMenuButtons(backgroundEdit);
                ui.getUiRightMenu().hideRightMenu();
                showCPs(false);
                ui.setEditMode(true);
            }
        });
        backgroundEdit.setPrefSize(30, 30);
        
        ImageView ivBackgroundIcon=new ImageView(imgBackgroundIcon);
        ivBackgroundIcon.setFitHeight(25);
        ivBackgroundIcon.setPreserveRatio(true);
        backgroundEdit.setGraphic(ivBackgroundIcon);
        HBox editModes=new HBox();
        editModes.setSpacing(5);
        editModes.getChildren().addAll(streetEdit,trafficLightsEdit,backgroundEdit);
        left.getChildren().addAll(editModes,cps);
        root.getChildren().add(left);
    }
    
    private void initCPList()
    {

        priorityGroup=new ToggleGroup();
        priority=new RadioButton("Přednost");
        priority.setLayoutY(40);
        priority.setMinSize(100, 50);
        priority.setMaxSize(100, 50);
        priority.setSelected(true);
        priority.setDisable(true);
        priority.setOnAction(new EventHandler<ActionEvent>() {
            
            
            @Override
            public void handle(ActionEvent event) {
                priorityCP=priority.isSelected();
                watchCP=!priority.isSelected();
            }
        });
        
        watch=new RadioButton("Volno");
        watch.setLayoutY(70);
        watch.setMinSize(100, 50);
        watch.setMaxSize(100, 50);
        watch.setDisable(true);
        watch.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                priorityCP=!watch.isSelected();
                watchCP=watch.isSelected();
                
            }
        });
        priorityGroup.getToggles().addAll(priority, watch);
        selectedCPs=new ListView<HBox>();
        selectedCPs.setMaxWidth(150);
        selectedCPs.setMinWidth(150);
        selectedCPs.setLayoutY(120);
        
        selectedCPs.setLayoutX(80);
        watch.setLayoutX(80);
        priority.setLayoutX(80);
        selectedCPs.setVisible(false);
        watch.setVisible(false);
        priority.setVisible(false);
        cps=new Group();
        cps.getChildren().addAll(watch, priority,selectedCPs);
        //left.getChildren().add(cps);
               
    }
     public void updateCPsPosition()
    {
        Canvas canvas=ui.getCanvas();
        selectedCPs.setMinHeight(canvas.getHeight()-selectedCPs.getLayoutY()-115);
        selectedCPs.setMaxHeight(canvas.getHeight()-selectedCPs.getLayoutY()-115);
    }
     public void setAddCP(boolean add)
    {
        watch.setDisable(!add);
        priority.setDisable(!add);
        priorityCP=add;
    }
    public void setSetPriority(boolean priority)
    {
        priorityCP=priority;
    }
    public boolean isSetPriority()
    {
        return priorityCP;
    }
    public void setSetWatch(boolean watch)
    {
        watchCP=watch;
    }
    public boolean isSetWatch()
    {
        return watchCP;
    }
    public void showCPs(boolean show)
    {
        selectedCPs.setVisible(show);
        watch.setVisible(show);
        priority.setVisible(show);
    }
    public void setVisibleCPs(boolean show, RoadSegment rs)
    {
        showCPs(show);
        Platform.runLater(
        () -> {
            selectedCPs.getItems().clear();
            if(rs!=null)
            {
                for (CheckPoint checkPoint : rs.getCheckPoints()) {
                addCPToList(checkPoint);
            }
                for (WatchPoint watchPoint : rs.getWatchPoints()) {
                    addWPToList(watchPoint);
                }
            }
            
            
        });
    }
    public void removeCPFromList(CheckPoint cp)
    {
        Platform.runLater(
        () -> {
            selectedCPs.getItems().remove(cp.getInfo());
        });
    }
    
    public void addCPToList(CheckPoint cp)
    {
        Platform.runLater(
        () -> {
            selectedCPs.getItems().add(cp.getInfo());
        });
    }
    public void removeWPFromList(WatchPoint wp)
    {
        Platform.runLater(
        () -> {
            selectedCPs.getItems().remove(wp.getInfo());
        });
    }
    public void addWPToList(WatchPoint wp)
    {
        Platform.runLater(
        () -> {
            selectedCPs.getItems().add(wp.getInfo());
        });
    }
}
