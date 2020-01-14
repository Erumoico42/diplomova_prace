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
import dipl_project.Simulation.SimulationControll;
import dipl_project.Vehicles.Vehicle;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author Honza
 */
public class UIControll {
    private Stage primaryStage;
    private Group root;
    private Scene scene;
    private boolean popupShown=false, addCP=false, runGenerator=false;
    private int initialSizeX=1200, initialSizeY=800;
    private ContextMenu popupClick, popupTL, popupTLConnection;
    private MenuItem popupSplit, popupRemove, popupRemoveTL, popupRemoveTLConnection;
    private Canvas canvas, backgroundCanvas;
    private ListView<HBox> selectedCPs;
    private List<MyCurve> curves=new ArrayList<>();
    private List<RoadSegment> segments=new ArrayList<>();
    private List<RoadSegment> startSegments=new ArrayList<>();
    private List<Connect> connects=new ArrayList<>();
    private CheckBox checkBoxNewCP, editBackground, addTrafficLight, connectTrafficLight, enableSwitchRed, enableSwitchGreen, enableSwitchOrange;
    private Button btnRemoveBackhround, saveEditedCurve;
    private SimulationControll sc = Dipl_project.getSc();;
    private Slider curveEdit= new Slider(0, 100, 0);
    private Label lblCurveEdit;
    private DrawControll dc;
    private ToggleGroup trafficLightsColorGroup;
    private RadioButton rbGreen, rbOrange, rbRed;
    private Spinner<Integer> timeTLRed, timeTLOrange, timeTLGreen, delayConnectTL;
    private TrafficLight actualTL;
    private Rectangle menuBG;
    public UIControll(Stage primaryStage) {
        this.primaryStage=primaryStage;
        root = new Group(); 
        initComponents();
        scene = new Scene(root, initialSizeX, initialSizeY);
        primaryStage.setTitle("Diplomová práce");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public Rectangle getMenuBG() {
        return menuBG;
    }
    
    public void setStartSegments(List<RoadSegment> segments)
    {
        startSegments=segments;
    }

    public void setDc(DrawControll dc) {
        this.dc = dc;
    }
    public List<RoadSegment> getStartSegments() {
        return startSegments;
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
        curves.remove(curve);
        removeComponents(curve.getStartControll().getControll(),
                curve.getEndControll().getControll(),
                curve.getCurve(), 
                curve.getEndControll().getLine(), 
                curve.getStartControll().getLine(), curve.getStartArrow().getArrow(), curve.getEndArrow().getArrow());
        for (Arrow arrow : curve.getArows()) {
            removeComponents(arrow.getArrow());
        }
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
        backgroundCanvas.setVisible(edit);
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
        root.getChildren().add(root.getChildren().size()-2, node);
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
    }
    private void initComponents()
    {
        checkBoxNewCP=new CheckBox("Přednosti");
        checkBoxNewCP.setMinSize(125, 50);
        checkBoxNewCP.setMaxSize(125, 50);
        checkBoxNewCP.setVisible(false);
        checkBoxNewCP.setLayoutY(120);
        checkBoxNewCP.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                addCP=checkBoxNewCP.isSelected();
            }
        });
        selectedCPs=new ListView<HBox>();
        selectedCPs.setLayoutY(160);
        selectedCPs.setVisible(false);
        canvas=new Canvas(initialSizeX, initialSizeY);
        backgroundCanvas=new Canvas(initialSizeX, initialSizeY-100);
        backgroundCanvas.setLayoutY(100);
        backgroundCanvas.setVisible(false);
        updateCPsPosition();
        Slider generatorSize=new Slider(1, 150, 10);
        Button btnAdd=new Button("Spustit");
        btnAdd.setLayoutX(140);
        btnAdd.setLayoutY(10);
        
        btnAdd.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                
                if(runGenerator)
                {
                    Dipl_project.getSc().stopAnimation();
                    Dipl_project.getSc().stopTrafficLights();
                    runGenerator=false;
                    btnAdd.setText("Spustit");
                }
                else
                {
                    Dipl_project.getSc().startSimulation();
                    Dipl_project.getSc().startTrafficLights();
                    runGenerator=true;
                    btnAdd.setText("Zastavit");
                }
                    
            }
        });
        
        generatorSize.setLayoutX(35);
        generatorSize.setLayoutY(50);
        Label lblGenerSize=new Label("10");
        lblGenerSize.setLayoutX(10);
        lblGenerSize.setLayoutY(50);
        generatorSize.valueProperty().addListener((observable, oldValue, newValue)->{
            Dipl_project.getSc().changeGenerateSize(newValue.intValue());
            lblGenerSize.setText(String.valueOf(newValue.intValue()));
        });
        
        Button btnSave=new Button("Uložit");
        btnSave.setLayoutX(10);
        btnSave.setLayoutY(90);
        btnSave.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Dipl_project.getStc().saveFile();
            }
        });
        
        Button btnLoad=new Button("Otevřít");
        btnLoad.setLayoutX(120);
        btnLoad.setLayoutY(90);
        btnLoad.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Dipl_project.getStc().loadFile();
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
        btnReload.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Dipl_project.loadRules();
            }
        });
        popupClick=new ContextMenu();
        popupSplit=new MenuItem("Rozdělit");
        popupSplit.setDisable(true);
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
                backgroundCanvas.setVisible(editBackground.isSelected());
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
                backgroundCanvas.setVisible(false);
                root.getChildren().remove(0);
                BackgroundControll.setBackground(null);
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
        
        initTrafficLights();
        
        popupClick.getItems().addAll(popupSplit, popupRemove);
        menuBG=new Rectangle();
        menuBG.setFill(Color.LIGHTGRAY);
        menuBG.setHeight(130);
        menuBG.setWidth(initialSizeX);
        root.getChildren().addAll(canvas, menuBG, btnAdd, btnSave,btnLoad, generatorSize,lblGenerSize, btnCheckIntersect,
                btnHideAutoFound, checkBoxNewCP, editBackground,  btnLoadBackground,btnReload, curveEdit, saveEditedCurve, timeTLGreen, timeTLOrange, timeTLRed,
                addTrafficLight, connectTrafficLight, delayConnectTL,rbGreen,rbOrange,rbRed, enableSwitchRed, enableSwitchOrange, 
                enableSwitchGreen,lblCurveEdit, btnRemoveBackhround, selectedCPs, backgroundCanvas);
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
                actualTL.setStatus(1, true);
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
    public void showPopUpTLConnection(Point loc)
    {
        popupShown=true;
        popupTLConnection.show(root, primaryStage.getX()+loc.getX()+9, primaryStage.getY()+loc.getY()+30);
    }
    
    public void setAddCP(boolean add)
    {
        checkBoxNewCP.setSelected(add);
        addCP=add;
    }
    public boolean isAddCP()
    {
        return addCP;
    }
    public void setVisibleCPs(boolean show, RoadSegment rs)
    {
        selectedCPs.setVisible(show);
        checkBoxNewCP.setVisible(show);
        
        Platform.runLater(
        () -> {
            selectedCPs.getItems().clear();
            if(rs!=null)
            for (CheckPoint checkPoint : rs.getCheckPoints()) {
                addCPToList(checkPoint);
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
    public void newVehicle()
    {
        RoadSegment rs=getRandomStart();
        if(rs!=null)
            new Vehicle(rs);
        
    }
    public RoadSegment getRandomStart()
    {
        List<RoadSegment> sstoGen=new ArrayList<>();
        sstoGen.addAll(startSegments);

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
    public Canvas getBackgroundCanvas() {
        return backgroundCanvas;
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
    
}
