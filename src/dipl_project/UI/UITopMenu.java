/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dipl_project.UI;

import dipl_project.TrafficLights.TrafficLight;
import dipl_project.Dipl_project;
import dipl_project.Roads.MyCurve;
import dipl_project.Roads.RoadCreator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.WindowEvent;
import sun.plugin2.os.windows.Windows;

/**
 *
 * @author Honza
 */
public class UITopMenu {
    private Image imgTLIcon=new Image(Dipl_project.class.getResource("Resources/menuIcons/trafficLightIcon.png").toString());
    private Image imgSwitchGreen=new Image(Dipl_project.class.getResource("Resources/trafficLights/switchGreen.png").toString());
    private Image imgSwitchRed=new Image(Dipl_project.class.getResource("Resources/trafficLights/switchRed.png").toString());
    private Image imgSwitchOrange=new Image(Dipl_project.class.getResource("Resources/trafficLights/switchOrange.png").toString());
    
    private Image imgOkIcon=new Image(Dipl_project.class.getResource("Resources/menuIcons/okIcon.png").toString());
    private Image imgPlayIcon=new Image(Dipl_project.class.getResource("Resources/menuIcons/playIcon.png").toString());
    private Image imgPauseIcon=new Image(Dipl_project.class.getResource("Resources/menuIcons/pauseIcon.png").toString());
    private MenuItem newFile;
    private MenuItem openFile;
    private MenuItem saveFile;
    private Rectangle backgroundBG, simulationBG;
    private Group simulationGroup, streetGroup, root,backgroundGroup;
    private RadioButton carCreate;
    private RadioButton tramCreate;
    private HBox menuBox;
    private Slider curveEdit;
    private Button saveEditedCurve;
    private Label lblCurveEdit;
    private ToggleGroup createVehicleGroup;
    private Rectangle downLine;
    private CheckBox editBackground;
    private UIControll ui;
    private Button btnRemoveBackhround;
    private CheckBox showRoads;
    private boolean onofTLS=true;
    private Slider carGeneratorSize;
    private Slider tramGeneratorSize;
    private boolean enableChangeGenerate;
    private MyCurve selectedCurve;
    private Label lblCarGenerSize;
    private Label lblTramGenerSize;
    public UITopMenu(Group root, UIControll ui)
    {
        this.ui=ui;
        this.root=root;
        
        initMenu();
        initSimulationMenu();
        initMenuButtons();
        initStreetMenu();
        initBackgroundMenu();
        //initTrafficLightsMenu();
    }
    private void initMenu()
    {
        VBox top=new VBox();
        MenuBar menu=new MenuBar();
        Menu file=new Menu("Soubor");
        
        newFile=new MenuItem("Nový");
        openFile=new MenuItem("Otevřít");
        saveFile=new MenuItem("Uložit");
        file.getItems().addAll(newFile, openFile, saveFile);

        
        simulationBG=new Rectangle();
        simulationBG.setFill(Color.LIGHTGRAY);
        simulationBG.setHeight(80);
        simulationBG.setWidth(300);
        
        Rectangle splitRectangle=new Rectangle(2, 80);
        splitRectangle.setFill(Color.BLACK);
        
        Rectangle splitRectangle2=new Rectangle(2, 80);
        splitRectangle2.setFill(Color.BLACK);
        splitRectangle2.setLayoutX(350);
        downLine=new Rectangle(1200, 2);
        downLine.setFill(Color.BLACK);
        
        backgroundBG=new Rectangle();
        backgroundBG.setFill(Color.LIGHTGRAY);
        backgroundBG.setHeight(80);
        backgroundBG.setWidth(600);
        
        Rectangle streetBG=new Rectangle();
        streetBG.setFill(Color.LIGHTGRAY);
        streetBG.setHeight(80);
        streetBG.setWidth(350);
        
        
        menu.getMenus().add(file);
        
        AnchorPane ap=new AnchorPane();
        ap.setPrefSize(200, 0);
        Pane pane=new Pane();
        pane.setPrefSize(100, 0);
        ap.getChildren().add(pane);
        simulationGroup=new Group();
        streetGroup=new Group();
        backgroundGroup=new Group();
        
        menuBox=new HBox();
        simulationGroup.getChildren().add(simulationBG);
        streetGroup.getChildren().addAll(streetBG,splitRectangle,splitRectangle2);
        backgroundGroup.getChildren().addAll(backgroundBG);
        menuBox.getChildren().addAll(simulationGroup,streetGroup,backgroundGroup);
        top.getChildren().addAll(menu,ap,menuBox,downLine);
        root.getChildren().add(top);
    }
    public void updateMenuSize(double width)
    {
        
        backgroundBG.setWidth(width);
        downLine.setWidth(width);
    }
    private void initMenuButtons()
    {
        saveFile.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Dipl_project.getStc().saveFile();
            }
        });

        openFile.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Dipl_project.getStc().loadFile();
            }
        });

        newFile.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Dipl_project.getDC().cleanAll();
                EditationControll.setDefRatio();
            }
        });
    }
    private void initSimulationMenu()
    {
        Button btnRunSimulation=new Button();
        btnRunSimulation.setLayoutX(10);
        btnRunSimulation.setLayoutY(10);
        btnRunSimulation.setMinSize(60, 60);
        btnRunSimulation.setMaxSize(60, 60);
        ImageView ivPlayPause=new ImageView(imgPlayIcon);
        ivPlayPause.setFitHeight(55);
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
        
        Label lblFrequency=new Label("Frekvence generování");
        lblFrequency.setLayoutX(90);
        lblFrequency.setLayoutY(10);
        carGeneratorSize=new Slider(0, 150, 40);
        carGeneratorSize.setLayoutX(110);
        carGeneratorSize.setLayoutY(35);
        lblCarGenerSize=new Label("20");
        lblCarGenerSize.setLayoutX(85);
        lblCarGenerSize.setLayoutY(35);
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
        tramGeneratorSize.setLayoutX(110);
        tramGeneratorSize.setLayoutY(55);
        lblTramGenerSize=new Label("5");
        lblTramGenerSize.setLayoutX(85);
        lblTramGenerSize.setLayoutY(55);
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
        ImageView iconTLRun=new ImageView(imgTLIcon);
        iconTLRun.setLayoutX(240);
        iconTLRun.setLayoutY(30);
        iconTLRun.setFitHeight(45);
        iconTLRun.setPreserveRatio(true);
        
        ImageView ivTLPlay=new ImageView(imgPauseIcon);
        
        ivTLPlay.setFitHeight(25);
        ivTLPlay.setPreserveRatio(true);
        Button btnTLPlay=new Button();
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
        btnTLPlay.setLayoutX(270);
        btnTLPlay.setLayoutY(40);
        simulationGroup.getChildren().addAll(iconTLRun,btnTLPlay,btnRunSimulation,carGeneratorSize,lblCarGenerSize,tramGeneratorSize,lblTramGenerSize,lblFrequency);
    }
    public void enableChangeGenerate(boolean enable, MyCurve curve)
    {
        
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
        Label lblCreateStreet=new Label("Druh cesty");
        lblCreateStreet.setLayoutX(20);
        lblCreateStreet.setLayoutY(10);
        carCreate=new RadioButton("Automobil");
        carCreate.setLayoutX(10);
        carCreate.setLayoutY(35);
        carCreate.setSelected(true);
        carCreate.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Dipl_project.getUI().setIsTramCreating(false);
            }
        });
        tramCreate=new RadioButton("Tramvaj");
        tramCreate.setLayoutX(10);
        tramCreate.setLayoutY(55);
        tramCreate.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Dipl_project.getUI().setIsTramCreating(true);
            }
        });
        createVehicleGroup=new ToggleGroup();
        createVehicleGroup.getToggles().addAll(tramCreate, carCreate);
        
        
        Label lblCurveSmooth=new Label("Vyhlazení cesty");
        lblCurveSmooth.setLayoutX(170);
        lblCurveSmooth.setLayoutY(10);
        
        curveEdit= new Slider(0, 100, 0);
        curveEdit.setLayoutX(160);
        curveEdit.setLayoutY(35);
        lblCurveEdit=new Label("0");
        lblCurveEdit.setLayoutX(140);
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
        saveEditedCurve.setLayoutX(300);
        saveEditedCurve.setLayoutY(30);
        saveEditedCurve.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Dipl_project.getDC().getSelectedCurve().deselectCurve();
                Dipl_project.getDC().setSelectedCurve(null);
                lblCurveEdit.setText("0");
            }
        });
        
        saveEditedCurve.setDisable(true);
        curveEdit.setDisable(true);
        saveEditedCurve.setDisable(true);
        
        showRoads=new CheckBox("Zobrazit cesty");
        showRoads.setLayoutX(135);
        showRoads.setLayoutY(55);
        showRoads.setSelected(true);
        showRoads.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                refreshShowRoads();
            }
        });
        streetGroup.getChildren().addAll(lblCreateStreet,carCreate,tramCreate,  lblCurveEdit, saveEditedCurve, curveEdit,lblCurveSmooth,showRoads);
    }
    
    public void refreshShowRoads()
    {
        ui.showRoads(showRoads.isSelected());
    }
    public void initBackgroundMenu()
    {
        editBackground=new CheckBox("Editovat pozadí");
        editBackground.setLayoutX(10);
        editBackground.setLayoutY(45);
        editBackground.setDisable(true);
        editBackground.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ui.setMoveStatus(1);
                ui.getMoveCanvas().setVisible(editBackground.isSelected());
            }
        });
        Button btnLoadBackground=new Button("Načíst pozadí");
         btnLoadBackground.setLayoutX(10);
         btnLoadBackground.setMinWidth(100);
        btnLoadBackground.setLayoutY(10);
        btnLoadBackground.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                BackgroundControll.loadImage();
            }
        });
        btnRemoveBackhround=new Button("X");
        btnRemoveBackhround.setLayoutX(120);
        btnRemoveBackhround.setLayoutY(10);
        enableRemoveBG(false);
        btnRemoveBackhround.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                setEditBackground(false);
                
                BackgroundControll.removeBG();
                enableRemoveBG(false);
            }
        });
        backgroundGroup.getChildren().addAll(editBackground,btnLoadBackground,btnRemoveBackhround);
    }
    public void enableRemoveBG(boolean enable)
    {
        btnRemoveBackhround.setDisable(!enable);
    }
    public void setEditBackground(boolean edit)
    {
        ui.getMoveCanvas().setVisible(edit);
        editBackground.setSelected(edit);
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
            if(Dipl_project.getDC().getSelectedCurve()!=null){
                Dipl_project.getDC().getSelectedCurve().deselectCurve();
                Dipl_project.getDC().setSelectedCurve(null);
            }
            int carFrequency=Dipl_project.getSc().getFrequencyCarGeneration();
            int tramFrequency=Dipl_project.getSc().getFrequencyTramGeneration();
            carGeneratorSize.setValue(carFrequency);
            lblCarGenerSize.setText(String.valueOf(carFrequency));
            
            tramGeneratorSize.setValue(tramFrequency);
            lblTramGenerSize.setText(String.valueOf(tramFrequency));
        }
    }

    public CheckBox getEditBackground() {
        return editBackground;
    }

    public Button getBtnRemoveBackhround() {
        return btnRemoveBackhround;
    }
 
}
