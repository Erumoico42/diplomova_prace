/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dipl_project.UI;

import TrafficLights.TrafficLight;
import TrafficLights.TrafficLightsConnection;
import dipl_project.Dipl_project;
import dipl_project.Roads.Arrow;
import dipl_project.Roads.CheckPoint;
import dipl_project.Roads.Connect;
import dipl_project.Roads.MyCurve;
import dipl_project.Roads.MyMath;
import dipl_project.Roads.RoadSegment;
import dipl_project.Roads.WatchPoint;
import dipl_project.Simulation.SimulationControll;
import dipl_project.Vehicles.Animation;
import dipl_project.Vehicles.Car;
import dipl_project.Vehicles.MyCar;
import dipl_project.Vehicles.Tram;
import dipl_project.Vehicles.Vehicle;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import javafx.util.Pair;

/**
 *
 * @author Honza
 */
public class UIControll {
    private Stage primaryStage;
    private Group root;
    private Scene scene;
    private boolean popupShown=false, addCP=false, runGenerator=false, watchCP=false;
    private int initialSizeX=1200, initialSizeY=800, moveStatus=0;
    private ContextMenu popupClick, popupTL, popupTLConnection;
    private MenuItem popupSplit, popupRemove, popupRemoveTL, popupRemoveTLConnection;
    private Canvas canvas, moveCanvas;
    private ListView<HBox> selectedCPs;
    private List<MyCurve> curves=new ArrayList<>();
    private List<RoadSegment> segments=new ArrayList<>();
    private List<RoadSegment> startCarSegments=new ArrayList<>();
    private List<Connect> connects=new ArrayList<>();
    private CheckBox checkBoxNewCP, editBackground, addTrafficLight, connectTrafficLight, enableSwitchRed, enableSwitchGreen, enableSwitchOrange;
    private Button btnRemoveBackhround, saveEditedCurve;
    private SimulationControll sc;
    private Slider curveEdit= new Slider(0, 100, 0);
    private Label lblCurveEdit;
    private DrawControll dc;
    private ToggleGroup trafficLightsColorGroup, priorityGroup;
    private RadioButton rbGreen, rbOrange, rbRed, priority, watch;
    private Spinner<Integer> timeTLRed, timeTLOrange, timeTLGreen, delayConnectTL;
    private TrafficLight actualTL;
    private Rectangle menuBG;
    private boolean priorityCP, isTramCreating=false;
    private CheckBox editConcept;
    private RadioButton tramCreate;
    private RadioButton carCreate;
    private ToggleGroup createVehicleGroup;
    private List<RoadSegment> startTramSegments;
    private boolean wantDrive;
    private CheckBox showRoads;
    public UIControll(Stage primaryStage) {
        this.primaryStage=primaryStage;
        root = new Group(); 
        initComponents();
        scene = new Scene(root, initialSizeX, initialSizeY);
        primaryStage.setTitle("Diplomová práce");
        primaryStage.setScene(scene);
        primaryStage.show();
        initStageHandler();
        initSceneHandler();
    }
    private void initStageHandler()
    {
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                Dipl_project.getAnim().stopAnimation();
                Dipl_project.getSc().stopSimulation();
                Dipl_project.getSc().stopTrafficLights();
            }
        });
    }
    private void initSceneHandler()
    {
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                sc.changeMyCarSpeed(event);
            }
        });
        scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                sc.stopChangeMyCarSpeed(event);
            }
        });
    }
    public Rectangle getMenuBG() {
        return menuBG;
    }
    
    public void setStartSegments(List<RoadSegment> carSegments, List<RoadSegment> tramSegments)
    {
        startCarSegments=carSegments;
        startTramSegments=tramSegments;
    }
    
    public void setDc(DrawControll dc) {
        this.dc = dc;
    }

    public void setSc(SimulationControll sc) {
        this.sc = sc;
    }
    
    public List<RoadSegment> getStartCarSegments() {
        return startCarSegments;
    }
    public List<RoadSegment> getStartTramSegments() {
        return startTramSegments;
    }
    public void enableCurveEdit(boolean enable)
    {
        curveEdit.setDisable(!enable);
        lblCurveEdit.setDisable(!enable);
        saveEditedCurve.setDisable(!enable);
        if(enable){
            curveEdit.setValue(0);
            lblCurveEdit.setText("0");
        }
        else if(Dipl_project.getDC().getSelectedCurve()!=null)
        {
            Dipl_project.getDC().getSelectedCurve().deselectCurve();
            Dipl_project.getDC().setSelectedCurve(null);
        }
        
    }
    public void addCurve(MyCurve curve)
    {
        addComponentsDown(curve.getCurve(), curve.getEndControll().getLine(), curve.getStartControll().getLine());
        addComponents(curve.getStartControll().getControll(),curve.getEndControll().getControll());
        curves.add(curve);
    }
    public void addRoadSegment(RoadSegment rs)
    {
        segments.add(rs);
        
        addComponentsDown(rs.getShape());
        addComponent(rs.getRoadSegment());
    }

    public List<MyCurve> getCurves() {
        return curves;
    }
    
    public void addConnect(Connect connect)
    {
        connects.add(connect);
        addComponent(connect.getConnect());
    }
    public void removeCurve(MyCurve curve)
    {
        
        removeComponents(curve.getStartControll().getControll(),
                curve.getEndControll().getControll(),
                curve.getCurve(), 
                curve.getEndControll().getLine(), 
                curve.getStartControll().getLine(), curve.getStartArrow().getArrow(), curve.getEndArrow().getArrow());
        for (Arrow arrow : curve.getArows()) {
            removeComponents(arrow.getArrow());
        }
        curves.remove(curve);
    }
    public void removeRoadSegment(RoadSegment rs)
    {
        segments.remove(rs);
        removeComponents(rs.getShape(), rs.getRoadSegment());
    }
    public void removeConnect(Connect connect)
    {
        connects.remove(connect);
        removeComponents(connect.getConnect());
    }
    public void moveComponentUp(Node node)
    {
        root.getChildren().remove(node);
        addComponent(node);
    }
    public void addComponentsDown(Node...nodes)
    {
        for (Node node : nodes) {
            root.getChildren().add(2, node);
        }  
    }
    public void addBackground(ImageView bg)
    {
        root.getChildren().add(0, bg);
        setEditBackground(true);
    }
    public void setEditBackground(boolean edit)
    {
        editBackground.setSelected(edit);
        btnRemoveBackhround.setDisable(!edit);
        moveCanvas.setVisible(edit);
    }
    public void enableEditTL(boolean enable)
    {
        rbGreen.setDisable(!enable);
        rbOrange.setDisable(!enable);
        rbRed.setDisable(!enable);
        timeTLGreen.setDisable(!enable);
        timeTLOrange.setDisable(!enable);
        timeTLRed.setDisable(!enable);
        enableSwitchGreen.setDisable(!enable);
        enableSwitchOrange.setDisable(!enable);
        enableSwitchRed.setDisable(!enable);
        if(enable)
        {
            actualTL=dc.getActualTL();
            timeTLGreen.getValueFactory().setValue(actualTL.getTimeToSwitchGreen());
            timeTLOrange.getValueFactory().setValue(actualTL.getTimeToSwitchOrange());
            timeTLRed.getValueFactory().setValue(actualTL.getTimeToSwitchRed());
            enableSwitchGreen.setSelected(actualTL.isEnableSwitchGreen());
            enableSwitchOrange.setSelected(actualTL.isEnableSwitchOrange());
            enableSwitchRed.setSelected(actualTL.isEnableSwitchRed());
            switch(actualTL.getStatus())
            {
                case 0:
                {
                    rbGreen.setSelected(true);
                    break;
                }
                case 1: case 3:
                {
                    rbOrange.setSelected(true);
                    break;
                }
                case 2:
                {
                    rbRed.setSelected(true);
                    break;
                }
            }
        }
    }
    public void addComponent(Node node)
    {
        root.getChildren().add(root.getChildren().indexOf(menuBG), node);
    }
    public void addComponents(Node...nodes)
    {
        for (Node node : nodes) {
            addComponent(node);
        }
    }
    public void removeComponents(Node...nodes)
    {
        root.getChildren().removeAll(nodes);
    }
    public List<Connect> getConnects()
    {
        return connects;
    }
    public void updateCPsPosition()
    {
        selectedCPs.setMinSize(125, canvas.getHeight()-45);
        selectedCPs.setMaxSize(125, canvas.getHeight()-45);
        selectedCPs.setLayoutX(canvas.getWidth()-130);
        checkBoxNewCP.setLayoutX(canvas.getWidth()-130);
        watch.setLayoutX(canvas.getWidth()-130);
        priority.setLayoutX(canvas.getWidth()-130);
    }
    private void initComponents()
    {
        checkBoxNewCP=new CheckBox("Upravit");
        checkBoxNewCP.setMinSize(125, 50);
        checkBoxNewCP.setMaxSize(125, 50);
        checkBoxNewCP.setVisible(false);
        checkBoxNewCP.setLayoutY(120);
        checkBoxNewCP.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                priority.setDisable(!checkBoxNewCP.isSelected());
                watch.setDisable(!checkBoxNewCP.isSelected());
                addCP=checkBoxNewCP.isSelected();;
                priorityCP=priority.isSelected();
                watchCP=watch.isSelected();
            }
        });
        priorityGroup=new ToggleGroup();
        priority=new RadioButton("Přednost");
        priority.setLayoutY(150);
        priority.setMinSize(100, 50);
        priority.setMaxSize(100, 50);
        priority.setSelected(true);
        priority.setVisible(false);
        priority.setDisable(true);
        priority.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                priorityCP=priority.isSelected();
                watchCP=!priority.isSelected();
            }
        });
        
        watch=new RadioButton("Volno");
        watch.setLayoutY(180);
        watch.setMinSize(100, 50);
        watch.setMaxSize(100, 50);
        watch.setVisible(false);
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
        selectedCPs.setLayoutY(220);
        selectedCPs.setVisible(false);
        canvas=new Canvas(initialSizeX, initialSizeY);
        EditationControll.setCanvasSize(initialSizeX, initialSizeY);
        moveCanvas=new Canvas(initialSizeX, initialSizeY-130);
        moveCanvas.setLayoutY(130);
        moveCanvas.setVisible(false);
        updateCPsPosition();
        
        Button btnAdd=new Button("Spustit");
        btnAdd.setLayoutX(140);
        btnAdd.setLayoutY(10);
        
        btnAdd.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                
                if(runGenerator)
                {
                    Dipl_project.getSc().stopSimulation();
                    Dipl_project.getSc().stopTrafficLights();
                    runGenerator=false;
                    btnAdd.setText("Spustit");
                }
                else
                {
                    Dipl_project.getSc().startSimulationCar();
                    Dipl_project.getSc().startSimulationTram();
                    Dipl_project.getSc().startTrafficLights();
                    runGenerator=true;
                    btnAdd.setText("Zastavit");
                }
                    
            }
        });
        
        
        Slider carGeneratorSize=new Slider(1, 150, 40);
        carGeneratorSize.setLayoutX(35);
        carGeneratorSize.setLayoutY(45);
        Label lblCarGenerSize=new Label("20");
        lblCarGenerSize.setLayoutX(10);
        lblCarGenerSize.setLayoutY(45);
        carGeneratorSize.valueProperty().addListener((observable, oldValue, newValue)->{
            Dipl_project.getSc().changeGenerateCarSize(newValue.intValue());
            lblCarGenerSize.setText(String.valueOf(newValue.intValue()));
        });
        
        Slider tramGeneratorSize=new Slider(1, 50, 10);
        tramGeneratorSize.setLayoutX(35);
        tramGeneratorSize.setLayoutY(65);
        Label lblTramGenerSize=new Label("5");
        lblTramGenerSize.setLayoutX(10);
        lblTramGenerSize.setLayoutY(65);
        tramGeneratorSize.valueProperty().addListener((observable, oldValue, newValue)->{
            Dipl_project.getSc().changeGenerateTramSize(newValue.intValue());
            lblTramGenerSize.setText(String.valueOf(newValue.intValue()));
        });
        
        
        
        Button btnSave=new Button("Uložit");
        btnSave.setLayoutX(10);
        btnSave.setLayoutY(90);
        btnSave.setMinWidth(70);
        btnSave.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Dipl_project.getStc().saveFile();
            }
        });
        
        Button btnLoad=new Button("Otevřít");
        btnLoad.setLayoutX(80);
        btnLoad.setLayoutY(90);
        btnLoad.setMinWidth(70);
        btnLoad.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Dipl_project.getStc().loadFile();
            }
        });
        Button btnClean=new Button("Nový");
        btnClean.setLayoutX(150);
        btnClean.setLayoutY(90);
        btnClean.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                dc.cleanAll();
                EditationControll.setDefRatio();
            }
        });
        
        Button btnCheckIntersect=new Button("Zkontrolovat");
        btnCheckIntersect.setLayoutX(10);
        btnCheckIntersect.setLayoutY(10);
        btnCheckIntersect.setDisable(true);
        btnCheckIntersect.setVisible(false);
        btnCheckIntersect.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Dipl_project.getRC().findIntersects();
            }
        });
        
        Button btnHideAutoFound=new Button("Skrýt přednosti");
        btnHideAutoFound.setLayoutX(10);
        btnHideAutoFound.setLayoutY(10);
        btnHideAutoFound.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                for (RoadSegment segment : segments) {
                    segment.setDefRoadSegment();
                }
                setVisibleCPs(false, null);
            }
        });
        Button btnReload=new Button("Reload");
        btnReload.setLayoutX(360);
        btnReload.setLayoutY(10);
        btnReload.setVisible(false);
        btnReload.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Dipl_project.loadRules();
            }
        });
        popupClick=new ContextMenu();
        popupSplit=new MenuItem("Rozdělit");
        popupSplit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Dipl_project.getDC().getActualConnect().splitConnect();
                
            }
        });
        popupRemove=new MenuItem("Odstranit");

        popupClick.setOnHiding(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                popupShown=false;
            }
        });
        popupRemove.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Dipl_project.getDC().getActualConnect().removeConnect();
            }
        });
        editBackground=new CheckBox("Editovat pozadí");
        editBackground.setLayoutX(220);
        editBackground.setLayoutY(10);
        editBackground.setDisable(true);
        editBackground.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                moveStatus=1;
                moveCanvas.setVisible(editBackground.isSelected());
                editConcept.setSelected(false);
            }
        });
        editConcept=new CheckBox("Editovat návrh");
        editConcept.setLayoutX(220);
        editConcept.setLayoutY(95);
        editConcept.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                moveStatus=2;
                editBackground.setSelected(false);
                moveCanvas.setVisible(editConcept.isSelected());
            }
        });
        
        Button btnLoadBackground=new Button("Načíst pozadí");
         btnLoadBackground.setLayoutX(220);
         btnLoadBackground.setMinWidth(100);
        btnLoadBackground.setLayoutY(40);
        btnLoadBackground.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                BackgroundControll.loadImage();
            }
        });
        btnRemoveBackhround=new Button("X");
        btnRemoveBackhround.setLayoutX(330);
        btnRemoveBackhround.setLayoutY(40);
        btnRemoveBackhround.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                editBackground.setSelected(false);
                moveCanvas.setVisible(false);
                
                BackgroundControll.removeBG();
                btnRemoveBackhround.setDisable(true);
                editBackground.setDisable(true);
            }
        });
        
        curveEdit.setLayoutX(480);
        curveEdit.setLayoutY(10);
        lblCurveEdit=new Label("0");
        lblCurveEdit.setLayoutX(450);
        lblCurveEdit.setLayoutY(10);
        curveEdit.valueProperty().addListener((observable, oldValue, newValue)->{
            //Dipl_project.getSc().changeGenerateSize(newValue.intValue());
            Dipl_project.getDC().getSelectedCurve().editCurve(newValue.intValue());
            lblCurveEdit.setText(String.valueOf(newValue.intValue()));
        });
        saveEditedCurve=new Button("Uložit");
        saveEditedCurve.setLayoutX(450);
        saveEditedCurve.setLayoutY(40);
        saveEditedCurve.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Dipl_project.getDC().getSelectedCurve().deselectCurve();
                Dipl_project.getDC().setSelectedCurve(null);
            }
        });
        
        saveEditedCurve.setDisable(true);
        curveEdit.setDisable(true);
        saveEditedCurve.setDisable(true);
        
        carCreate=new RadioButton("Automobil");
        carCreate.setLayoutX(450);
        carCreate.setLayoutY(75);
        carCreate.setSelected(true);
        carCreate.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                isTramCreating=false;
            }
        });
        tramCreate=new RadioButton("Tramvaj");
        tramCreate.setLayoutX(450);
        tramCreate.setLayoutY(100);
        tramCreate.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                isTramCreating=true;
            }
        });
         
        createVehicleGroup=new ToggleGroup();
        createVehicleGroup.getToggles().addAll(tramCreate, carCreate);
        wantDrive=false;
        Button addMyCar=new Button("Chci řídit");
        addMyCar.setLayoutX(840);
        addMyCar.setLayoutY(70);
        addMyCar.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(!wantDrive)
                {
                    wantDrive=true;
                    addMyCar.setText("Nechci řídit");
                    newMyCar();
                    
                }
                else
                {
                    addMyCar.setText("Chci řídit");
                    sc.removeMyCar();
                    wantDrive=false;
                }
                
            }
        });
        showRoads=new CheckBox("Zobrazit cesty");
        showRoads.setLayoutX(840);
        showRoads.setLayoutY(105);
        showRoads.setSelected(true);
        showRoads.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                refreshShowRoads();
            }
        });
        
        initTrafficLights();
        
        popupClick.getItems().addAll(popupSplit, popupRemove);
        menuBG=new Rectangle();
        menuBG.setFill(Color.LIGHTGRAY);
        menuBG.setHeight(130);
        menuBG.setWidth(initialSizeX);
        root.getChildren().addAll(canvas, menuBG, btnAdd, btnSave,btnLoad, btnClean, carGeneratorSize,lblCarGenerSize,tramGeneratorSize,lblTramGenerSize, btnCheckIntersect,
                btnHideAutoFound, checkBoxNewCP, watch, priority, editBackground, editConcept,  btnLoadBackground,btnReload, curveEdit, saveEditedCurve, 
                carCreate, tramCreate, timeTLGreen, timeTLOrange, timeTLRed,
                addTrafficLight, connectTrafficLight, delayConnectTL,rbGreen,rbOrange,rbRed, enableSwitchRed, enableSwitchOrange, 
                enableSwitchGreen,lblCurveEdit, btnRemoveBackhround, addMyCar, showRoads, selectedCPs, moveCanvas);
    }
    public void refreshShowRoads()
    {
        showRoads(showRoads.isSelected());
    }
    public void showRoads(boolean show)
    {
        for (MyCurve curve : curves) {
            curve.getCurve().setVisible(show);
            curve.getEndControll().getControll().setVisible(show);
            curve.getStartControll().getControll().setVisible(show);
            for (Arrow arrow : curve.getArows()) {
               arrow.getArrow().setVisible(show);
            }
            curve.getStartControll().getLine().setVisible(show);
            curve.getEndControll().getLine().setVisible(show);
            curve.getStartArrow().getArrow().setVisible(show);
            curve.getEndArrow().getArrow().setVisible(show);
        }
        
        for (RoadSegment segment : segments) {
            segment.getRoadSegment().setVisible(show);
        }
        for (Connect connect : connects) {
            connect.getConnect().setVisible(show);
            for(Map.Entry<Pair<MyCurve, MyCurve>, RoadSegment> rs : connect.getConnectSegmentsMap().entrySet()) {
                RoadSegment segment = rs.getValue();
                segment.setVisible(false);
            }
        }
        for (TrafficLight trafficLight : dc.getTrafficLights()) {
            for (TrafficLightsConnection allConnection : trafficLight.getAllConnections()) {
                allConnection.getConnectCurve().setVisible(show);
            }
        }
    }
    public void newMyCar()
    {
        if(wantDrive)
            Dipl_project.getSc().newMyCar(getRandomStart(startCarSegments));
    }
    private void initTrafficLights()
    {
        addTrafficLight=new CheckBox("Přidat semafor");
        addTrafficLight.setLayoutX(640);
        addTrafficLight.setLayoutY(10);
        addTrafficLight.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(addTrafficLight.isSelected())
                {
                    dc.setDrawStatus(1);
                }
                else
                {
                    dc.setDrawStatus(0);
                }
                    
            }
        });
        connectTrafficLight=new CheckBox("Propojit semafory");
        connectTrafficLight.setLayoutX(840);
        connectTrafficLight.setLayoutY(10);
        connectTrafficLight.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                for (TrafficLight trafficLight : dc.getTrafficLights()) {
                    trafficLight.enableConnectLights(connectTrafficLight.isSelected());
                }
            }
        });
        
        SpinnerValueFactory.IntegerSpinnerValueFactory valueFactoryDelayConnect = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 200);
        delayConnectTL=new Spinner<>(valueFactoryDelayConnect);
        delayConnectTL.setLayoutX(840);
        delayConnectTL.setLayoutY(35);
        delayConnectTL.setEditable(false);
        delayConnectTL.setDisable(true);
        delayConnectTL.setMaxWidth(80);
        delayConnectTL.valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                TrafficLightsConnection actualConnectionConnection=dc.getSelectedConnection();
                actualConnectionConnection.setSwitchDelay(newValue);
            }
        });
        
        
        
        enableSwitchRed=new CheckBox();
        enableSwitchRed.setLayoutX(610);
        enableSwitchRed.setLayoutY(35);
        enableSwitchRed.setSelected(true);
        enableSwitchRed.setDisable(true);
        enableSwitchRed.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                actualTL.setEnableSwitchRed(enableSwitchRed.isSelected());
            }
        });
        enableSwitchOrange=new CheckBox();
        enableSwitchOrange.setLayoutX(610);
        enableSwitchOrange.setLayoutY(67);
        enableSwitchOrange.setSelected(true);
        enableSwitchOrange.setDisable(true);
        enableSwitchOrange.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                actualTL.setEnableSwitchOrange(enableSwitchOrange.isSelected());
            }
        });
        
        enableSwitchGreen=new CheckBox(); 
        enableSwitchGreen.setLayoutX(610);
        enableSwitchGreen.setLayoutY(99);
        enableSwitchGreen.setSelected(true);
        enableSwitchGreen.setDisable(true);
        enableSwitchGreen.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                actualTL.setEnableSwitchGreen(enableSwitchGreen.isSelected());
            }
        });
        
        
        trafficLightsColorGroup=new ToggleGroup();
        rbRed=new RadioButton("Červené");
        rbRed.setLayoutX(725);
        rbRed.setLayoutY(35);
        rbRed.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                actualTL.setStatus(2, true);
            }
        });
        
        rbOrange=new RadioButton("Oranžová");
        rbOrange.setLayoutX(725);
        rbOrange.setLayoutY(70);
        rbOrange.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                actualTL.setStatus(3, true);
            }
        });
        rbGreen=new RadioButton("Zelená");
        rbGreen.setSelected(true);
        rbGreen.setLayoutX(725);
        rbGreen.setLayoutY(105);
        rbGreen.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                actualTL.setStatus(0, true);
            }
        });
        
        rbGreen.setToggleGroup(trafficLightsColorGroup);
        rbOrange.setToggleGroup(trafficLightsColorGroup);
        rbRed.setToggleGroup(trafficLightsColorGroup);
        rbGreen.setDisable(true);
        rbOrange.setDisable(true);
        rbRed.setDisable(true);
        
        SpinnerValueFactory.IntegerSpinnerValueFactory valueFactoryGreen = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 200);
        SpinnerValueFactory.IntegerSpinnerValueFactory valueFactoryOrange = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 200);
        SpinnerValueFactory.IntegerSpinnerValueFactory valueFactoryRed = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 200);
        timeTLRed=new Spinner<>(valueFactoryRed);
        timeTLRed.setLayoutX(640);
        timeTLRed.setLayoutY(35);
        timeTLRed.setEditable(false);
        timeTLRed.setDisable(true);
        timeTLRed.setMaxWidth(80);
        timeTLRed.valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                TrafficLight actualTL=dc.getActualTL();
                actualTL.setTimeToSwitchRed(newValue);
            }
        });
        
        timeTLOrange=new Spinner<>(valueFactoryOrange);
        timeTLOrange.setLayoutX(640);
        timeTLOrange.setLayoutY(65);
        timeTLOrange.setEditable(false);
        timeTLOrange.setDisable(true);
        timeTLOrange.setMaxWidth(80);
        timeTLOrange.valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                TrafficLight actualTL=dc.getActualTL();
                actualTL.setTimeToSwitchOrange(newValue);
            }
        });
        timeTLGreen=new Spinner<>(valueFactoryGreen);
        timeTLGreen.setLayoutX(640);
        timeTLGreen.setLayoutY(95);
        timeTLGreen.setEditable(false);
        timeTLGreen.setDisable(true);
        timeTLGreen.setMaxWidth(80);
        timeTLGreen.valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                TrafficLight actualTL=dc.getActualTL();
                actualTL.setTimeToSwitchGreen(newValue);
            }
        });
        
        popupTL=new ContextMenu();
        popupTL.setOnHiding(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                popupShown=false;
            }
        });
        popupRemoveTL=new MenuItem("Odstranit");
        popupRemoveTL.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                actualTL.removeTL();
            }
        });
        popupTLConnection=new ContextMenu();
        popupTLConnection.setOnHiding(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                popupShown=false;
            }
        });
        popupRemoveTLConnection=new MenuItem("Odstranit");
        popupRemoveTLConnection.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                dc.getSelectedConnection().removeConnection();
            }
        });
        popupTLConnection.getItems().addAll(popupRemoveTLConnection);
        popupTL.getItems().addAll(popupRemoveTL);
        
    }
    public boolean isEnabledConnectTL()
    {
        return connectTrafficLight.isSelected();
    }
    public void showPopUp(Point loc)
    {
        popupShown=true;
        popupClick.show(root, primaryStage.getX()+loc.getX()+9, primaryStage.getY()+loc.getY()+30);
    }
    public void showPopUpTL(Point loc)
    {
        popupShown=true;
        popupTL.show(root, primaryStage.getX()+loc.getX()+9, primaryStage.getY()+loc.getY()+30);
    }

    public int getMoveStatus() {
        return moveStatus;
    }
    
    public void showPopUpTLConnection(Point loc)
    {
        popupShown=true;
        popupTLConnection.show(root, primaryStage.getX()+loc.getX()+9, primaryStage.getY()+loc.getY()+30);
    }
    public void setAddCP(boolean add)
    {
        checkBoxNewCP.setSelected(false);
        checkBoxNewCP.setDisable(add);
        watch.setDisable(!add);
        priority.setDisable(!add);
        addCP=add;
    }

    public void setMoveStatus(int moveStatus) {
        this.moveStatus = moveStatus;
    }

    public CheckBox getEditConcept() {
        return editConcept;
    }
    
    public boolean isAddCP()
    {
        return addCP;
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
        return addCP;
    }
    public void setVisibleCPs(boolean show, RoadSegment rs)
    {
        selectedCPs.setVisible(show);
        checkBoxNewCP.setVisible(show);
        watch.setVisible(show);
        priority.setVisible(show);
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
    public void hidePopUp()
    {
        popupShown=false;
        popupClick.hide();
        popupTL.hide();
        //popupTLConnection.hide();
    }
    public boolean isPopupShown()
    {
        return popupShown;
    }
    public void newCar()
    {
        RoadSegment rs=getRandomStart(startCarSegments);
        if(rs!=null)
            new Car(rs);
    }
     public void newTram()
    {
        RoadSegment rs=getRandomStart(startTramSegments);
        if(rs!=null)
            new Tram(rs);
    }
    public RoadSegment getRandomStart(List<RoadSegment> startSegm)
    {
        List<RoadSegment> sstoGen=new ArrayList<>();
        sstoGen.addAll(startSegm);

        while(!sstoGen.isEmpty())
        {
            
            boolean next=false;
            RoadSegment ret=sstoGen.get((int)(Math.random()*sstoGen.size()));
            sstoGen.remove(ret);
            if(ret!=null)
            {
                if(ret.getVehicle()!=null)
                {
                    next=true;
                }
                else
                {
                    for (RoadSegment rs : ret.getRsNext()) {
                        if(rs.getVehicle()!=null)
                            next=true;
                        for (RoadSegment rss : rs.getRsNext()) {
                            if(rss.getVehicle()!=null)
                                next=true;
                        }
                    }
                }
                    
            }
            else
                next=true;
            if(!next){
                return ret;
            }
        }
        return null;
    }
    public Group getRoot() {
        return root;
    }

    public Canvas getCanvas() {
        return canvas;
    }
    public Canvas getMoveCanvas() {
        return moveCanvas;
    }
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public CheckBox getEditBackground() {
        return editBackground;
    }
    public void setChangeConnectDelay(int delay)
    {
        delayConnectTL.getValueFactory().setValue(delay);
    }
    public void enableChangeConnectDelay(boolean enable)
    {
        delayConnectTL.setDisable(!enable);
    }

    public List<RoadSegment> getSegments() {
        return segments;
    }

    public boolean isTramCreating() {
        return isTramCreating;
    }
    
}
