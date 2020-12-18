/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dipl_project.UI.EditMenu;

import dipl_project.Dipl_project;
import dipl_project.Roads.CheckPoint;
import dipl_project.Roads.MyCurve;
import dipl_project.Roads.RoadSegment;
import dipl_project.Roads.WatchPoint;
import dipl_project.TrafficLights.TrafficLight;
import dipl_project.UI.UIControll;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

/**
 *
 * @author Honza
 */
public class UILeftMenu {
    private Image imgStreetIcon=new Image(Dipl_project.class.getResource("Resources/menuIcons/streetIcon.png").toString());
    private Image imgTLIcon=new Image(Dipl_project.class.getResource("Resources/menuIcons/trafficLightIcon.png").toString());
    private Image imgBackgroundIcon=new Image(Dipl_project.class.getResource("Resources/menuIcons/backgroundIcon.png").toString());
    private Image imgOkIcon=new Image(Dipl_project.class.getResource("Resources/menuIcons/okIcon.png").toString());
    private Image imgPlayIcon=new Image(Dipl_project.class.getResource("Resources/menuIcons/playIcon.png").toString());
    private Image imgPauseIcon=new Image(Dipl_project.class.getResource("Resources/menuIcons/pauseIcon.png").toString());
    private Image carIcon=new Image(Dipl_project.class.getResource("Resources/menuIcons/car_icon.png").toString());
    private Image tramIcon=new Image(Dipl_project.class.getResource("Resources/menuIcons/tram_icon.png").toString());
    private Group root;
    private Button streetEdit;
    private Button trafficLightsEdit;
    private Button designEdit;
     private ListView<HBox> selectedCPs;
    private RadioButton priority;
    private ToggleGroup priorityGroup;
    private RadioButton watch;
    private boolean priorityCP;
    private boolean watchCP=false;
    private VBox leftCPS;
    
    private Group cpsGroup, modesGroup,leftGroup, curveSmoothGroup,simulationGroup;
    private final UIControll ui;
    private HBox editModes;
    private Slider curveEdit;
    private Label lblCurveEdit;
    private Button saveEditedCurve;
    
    private boolean onofTLS=true;
    private Slider carGeneratorSize;
    private Slider tramGeneratorSize;
    private boolean enableChangeGenerate;
    private Label lblCarGenerSize;
    private Label lblTramGenerSize;
    private Button btnTLPlay;
    private Button btnRunSimulation;
    private MyCurve selectedCurve;
    private Rectangle simulationBG, cpBG;
    private ImageView ivStreetIcon;
    private Rectangle curveSmoothBG;
    public UILeftMenu(Group root, UIControll ui)
    {
        this.ui=ui;
        this.root=root;
        
        initCPList();
        initStreetMenu();
        initMenu();
        initSimulationMenu();
        updateCPsPosition();
    }
    private void switchMenuButtons(Button menu)
    {
        streetEdit.setDisable(false);
        trafficLightsEdit.setDisable(false);
        designEdit.setDisable(false);
        menu.setDisable(true);
    }
    private void initMenu()
    {
        simulationGroup=new Group();
        
        simulationBG=new Rectangle();
        simulationBG.setLayoutX(5);
        simulationBG.setLayoutY(5);
        simulationBG.setFill(Color.rgb(250, 250, 250, 0.7));
        simulationBG.setHeight(60);
        simulationBG.setWidth(260);
        simulationBG.setArcWidth(10); 
        simulationBG.setArcHeight(10); 
        simulationBG.setStroke(Color.rgb(0, 0, 0, 0.2));
        simulationGroup.getChildren().add(simulationBG);
        
        leftCPS=new VBox();
        leftCPS.setVisible(false);
        leftCPS.setAlignment(Pos.TOP_LEFT);
        leftCPS.setLayoutY(70);
        leftCPS.setLayoutX(5);
        
        streetEdit=new Button();
        streetEdit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                switchMenuButtons(streetEdit);
                Dipl_project.getDC().setDrawStatus(0);
                ui.getUiRightMenu().hideRightMenu();
                hideSmoothtMenu();
                ui.setEditMode(false);
                ui.enableStreet(true);
                ui.getUiTopMenu().setEditDesign(false);
            }
        });
        streetEdit.setDisable(true);
        streetEdit.setPrefSize(30, 30);
        
        ivStreetIcon=new ImageView(imgStreetIcon);
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
                hideSmoothtMenu();
                showCPs(false);
                ui.setEditMode(false);
                ui.enableStreet(false);
                ui.enableSegments(true);
                ui.getUiTopMenu().setEditDesign(true);
                //Dipl_project.getUI().getUiTopMenu().showTLMenu();
            }
        });
        trafficLightsEdit.setPrefSize(30, 30);
        
        ImageView ivTLIcon=new ImageView(imgTLIcon);
        ivTLIcon.setFitHeight(25);
        ivTLIcon.setPreserveRatio(true);
        trafficLightsEdit.setGraphic(ivTLIcon);
        
        designEdit=new Button();
        designEdit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                switchMenuButtons(designEdit);
                ui.getUiRightMenu().hideRightMenu();
                hideSmoothtMenu();
                showCPs(false);
                ui.getUiTopMenu().setEditDesign(true);
                ui.setEditMode(true);
            }
        });
        designEdit.setPrefSize(30, 30);
        
        ImageView ivBackgroundIcon=new ImageView(imgBackgroundIcon);
        ivBackgroundIcon.setFitHeight(25);
        ivBackgroundIcon.setPreserveRatio(true);
        designEdit.setGraphic(ivBackgroundIcon);
        editModes=new HBox();
        editModes.setSpacing(2);
        editModes.getChildren().addAll(streetEdit,trafficLightsEdit,designEdit);
        editModes.setLayoutX(5);
        modesGroup=new Group();
        modesGroup.getChildren().add(editModes);
        leftCPS.getChildren().addAll(cpsGroup);
        leftGroup=new Group();
        leftGroup.setLayoutY(30);
        
        leftGroup.getChildren().addAll(simulationGroup,curveSmoothGroup,leftCPS,modesGroup);
        root.getChildren().addAll(leftGroup);
    }
    public void hideSmoothtMenu()
    {
        curveSmoothGroup.setVisible(false);
    }
    public void showSmoothMenu()
    {
        curveSmoothGroup.setVisible(true);
    }
    private void initSimulationMenu()
    {
        btnRunSimulation=new Button();
        btnRunSimulation.setLayoutX(10);
        btnRunSimulation.setLayoutY(15);
        btnRunSimulation.setMinSize(40, 40);
        btnRunSimulation.setMaxSize(40, 40);
        ImageView ivPlayPause=new ImageView(imgPlayIcon);
        ivPlayPause.setFitHeight(35);
        ivPlayPause.setPreserveRatio(true);
        btnRunSimulation.setGraphic(ivPlayPause);
        btnRunSimulation.setOnAction(new EventHandler<ActionEvent>() {
            private boolean runGenerator;
            @Override
            public void handle(ActionEvent event) {
                
                if(runGenerator)
                {
                    Dipl_project.getSc().stopVehicleGenerator();
                    Dipl_project.getTlc().stopTrafficLights();
                    runGenerator=false;
                    ivPlayPause.setImage(imgPlayIcon);
                }
                else
                {
                    Dipl_project.getSc().startVehicleGenerator();
                    Dipl_project.getTlc().startTrafficLights();
                    runGenerator=true;
                    ivPlayPause.setImage(imgPauseIcon);
                }
                    
            }
        });
        ImageView iconTLRun=new ImageView(imgTLIcon);
        iconTLRun.setLayoutX(45);
        iconTLRun.setLayoutY(15);
        iconTLRun.setFitHeight(40);
        iconTLRun.setPreserveRatio(true);
        
        ImageView ivTLPlay=new ImageView(imgPauseIcon);
        
        ivTLPlay.setFitHeight(25);
        ivTLPlay.setPreserveRatio(true);
        btnTLPlay=new Button();
        btnTLPlay.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                
                onofTLS=!onofTLS;
                if(!onofTLS)
                    ivTLPlay.setImage(imgPlayIcon);
                else
                    ivTLPlay.setImage(imgPauseIcon);
                turnOnOffTLs(onofTLS);
            }
        });
        btnTLPlay.setMinSize(25, 25);
        btnTLPlay.setMaxSize(25, 25);
        btnTLPlay.setGraphic(ivTLPlay);
        btnTLPlay.setLayoutX(75);
        btnTLPlay.setLayoutY(22);
        
        Label lblFrequency=new Label("Hustota provozu");
        lblFrequency.setLayoutX(120);
        lblFrequency.setLayoutY(2);
        
        carGeneratorSize=new Slider(0, 150, 40);
        carGeneratorSize.setPrefWidth(110);
        carGeneratorSize.setLayoutX(125);
        carGeneratorSize.setLayoutY(20);
        ImageView ivCar=new ImageView(carIcon);
        ivCar.setFitHeight(20);
        ivCar.setFitWidth(20);
        ivCar.setLayoutX(105);
        ivCar.setLayoutY(20);
        lblCarGenerSize=new Label("20");
        lblCarGenerSize.setLayoutX(235);
        lblCarGenerSize.setLayoutY(20);
        carGeneratorSize.valueProperty().addListener((observable, oldValue, newValue)->{
            if(!enableChangeGenerate)
                Dipl_project.getSc().changeGenerateCarSize(newValue.intValue());
            else
            {
                selectedCurve.getStartSegment().setFrequencyChangedActual(true);
                selectedCurve.getStartSegment().setFrequencyMinute(newValue.intValue()); 
            }
                
            lblCarGenerSize.setText(String.valueOf(newValue.intValue()));
        });
        
        tramGeneratorSize=new Slider(0, 50, 10);
        tramGeneratorSize.setPrefWidth(110);
        tramGeneratorSize.setLayoutX(125);
        tramGeneratorSize.setLayoutY(40);
        ImageView ivTram=new ImageView(tramIcon);
        ivTram.setFitHeight(20);
        ivTram.setFitWidth(20);
        ivTram.setLayoutX(105);
        ivTram.setLayoutY(40);
        lblTramGenerSize=new Label("5");
        lblTramGenerSize.setLayoutX(235);
        lblTramGenerSize.setLayoutY(40);
        tramGeneratorSize.valueProperty().addListener((observable, oldValue, newValue)->{
            if(!enableChangeGenerate)
                Dipl_project.getSc().changeGenerateTramSize(newValue.intValue());
            else
            {
                selectedCurve.getStartSegment().setFrequencyChangedActual(true);
                selectedCurve.getStartSegment().setFrequencyMinute(newValue.intValue()); 
            }
            lblTramGenerSize.setText(String.valueOf(newValue.intValue()));
        });
        
        
        simulationGroup.getChildren().addAll(iconTLRun,btnTLPlay,btnRunSimulation,lblFrequency,ivCar,ivTram, carGeneratorSize,lblCarGenerSize,tramGeneratorSize, lblTramGenerSize);
    }
    private void turnOnOffTLs(boolean onof)
    {
        if(!onof)
        {
            Dipl_project.getTlc().stopTrafficLights();
            for (TrafficLight tl : Dipl_project.getDC().getTrafficLights()) {
                tl.setLastStatus();
                tl.setOrangeSwitching(true);
                tl.startOrangeSwitching();
            }
            
        }else{
            for (TrafficLight tl : Dipl_project.getDC().getTrafficLights()) {
                tl.setStatus(tl.getLastStatus());
                tl.setOrangeSwitching(false);
                tl.stopOrangeSwitching();
            }
            Dipl_project.getTlc().startTrafficLights();
        }
    }
    private void initStreetMenu()
    {
        curveSmoothGroup=new Group();
        curveSmoothBG=new Rectangle();
        curveSmoothBG.setLayoutX(5);
        curveSmoothBG.setLayoutY(10);
        curveSmoothBG.setFill(Color.rgb(250, 250, 250, 0.7));
        curveSmoothBG.setHeight(55);
        curveSmoothBG.setWidth(215);
        curveSmoothBG.setArcWidth(10); 
        curveSmoothBG.setArcHeight(10); 
        curveSmoothBG.setStroke(Color.rgb(0, 0, 0, 0.2));
        
        
        curveSmoothGroup.setLayoutY(60);
        Label lblCurveSmooth=new Label("Vyhlazení cesty");
        lblCurveSmooth.setLayoutX(50);
        lblCurveSmooth.setLayoutY(10);
        
        curveEdit= new Slider(0, 100, 0);
        curveEdit.setLayoutX(30);
        curveEdit.setLayoutY(35);
        lblCurveEdit=new Label("0");
        lblCurveEdit.setLayoutX(10);
        lblCurveEdit.setLayoutY(35);
        curveEdit.valueProperty().addListener((observable, oldValue, newValue)->{
            Dipl_project.getDC().getSelectedCurve().editCurve(newValue.intValue());
            lblCurveEdit.setText(String.valueOf(newValue.intValue()));
        });
        saveEditedCurve=new Button();
        ImageView ivOkIcon=new ImageView(imgOkIcon);
        ivOkIcon.setFitHeight(15);
        ivOkIcon.setPreserveRatio(true);
        saveEditedCurve.setGraphic(ivOkIcon);
        saveEditedCurve.setLayoutX(170);
        saveEditedCurve.setLayoutY(30);
        saveEditedCurve.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Dipl_project.getDC().deselectCurve();
                lblCurveEdit.setText("0");
                hideSmoothtMenu();
            }
        });
        
        saveEditedCurve.setDisable(true);
        curveEdit.setDisable(true);
        saveEditedCurve.setDisable(true);
        
        curveSmoothGroup.getChildren().addAll(curveSmoothBG,lblCurveEdit, saveEditedCurve, curveEdit,lblCurveSmooth);
        hideSmoothtMenu();
    }

    private void initCPList()
    {
        cpBG=new Rectangle();
        cpBG.setFill(Color.rgb(250, 250, 250, 0.7));
        cpBG.setHeight(70);
        cpBG.setWidth(140);
        cpBG.setArcWidth(10); 
        cpBG.setArcHeight(10); 
        cpBG.setStroke(Color.rgb(0, 0, 0, 0.2));
        priorityGroup=new ToggleGroup();
        priority=new RadioButton("Přednost");
        priority.setFont(Font.font("Family", FontWeight.BOLD, FontPosture.REGULAR, 15));
        priority.setLayoutY(0);
        priority.setMinSize(120, 50);
        priority.setMaxSize(120, 50);
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
        watch.setFont(Font.font("Family", FontWeight.BOLD, FontPosture.REGULAR, 15));
        watch.setLayoutY(25);
        watch.setMinSize(120, 50);
        watch.setMaxSize(120, 50);
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
        selectedCPs.setMaxWidth(140);
        selectedCPs.setMinWidth(140);
        selectedCPs.setLayoutY(75);
        
        watch.setLayoutX(10);
        priority.setLayoutX(10);
        cpsGroup=new Group();
        
        cpsGroup.getChildren().addAll(cpBG, watch, priority,selectedCPs);
               
    }
     public void updateCPsPosition()
    {
        Canvas canvas=ui.getCanvas();
        double newHeight=canvas.getHeight()-selectedCPs.getLayoutY()-150;
        selectedCPs.setMinHeight(newHeight);
        selectedCPs.setMaxHeight(newHeight);
        modesGroup.setLayoutY(canvas.getHeight()-75);
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
        leftCPS.setVisible(show);
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
    public void enableCurveEdit(boolean enable, MyCurve selectedCurve)
    {
        this.selectedCurve=selectedCurve;
        curveEdit.setDisable(!enable); 
        lblCurveEdit.setDisable(!enable);
        saveEditedCurve.setDisable(!enable);

        if(enable){
            
            curveEdit.setValue(0);
            lblCurveEdit.setText("0");
            
            if(selectedCurve.getStartSegment()!=null)
            {
                enableChangeGenerate= true;
                
                boolean tram=selectedCurve.isTramCurve();
                if(tram)
                {
                    tramGeneratorSize.setValue(selectedCurve.getStartSegment().getFrequencyMinute());
                    lblTramGenerSize.setText(String.valueOf(selectedCurve.getStartSegment().getFrequencyMinute()));
                }
                else
                {
                    carGeneratorSize.setValue(selectedCurve.getStartSegment().getFrequencyMinute());
                    lblCarGenerSize.setText(String.valueOf(selectedCurve.getStartSegment().getFrequencyMinute()));
                }
            
                tramGeneratorSize.setDisable(!tram);
                carGeneratorSize.setDisable(tram);
            }
            else
            {
                tramGeneratorSize.setDisable(true);
                carGeneratorSize.setDisable(true);
            }
        }
        else 
        {
            enableChangeGenerate= false;
            tramGeneratorSize.setDisable(false);
            carGeneratorSize.setDisable(false);
            Dipl_project.getDC().deselectCurve();
            int carFrequency=Dipl_project.getSc().getFrequencyCarGeneration();
            int tramFrequency=Dipl_project.getSc().getFrequencyTramGeneration();
            carGeneratorSize.setValue(carFrequency);
            lblCarGenerSize.setText(String.valueOf(carFrequency));
            
            tramGeneratorSize.setValue(tramFrequency);
            lblTramGenerSize.setText(String.valueOf(tramFrequency));
        }
    }
}
