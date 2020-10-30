/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dipl_project.UI;

import dipl_project.TrafficLights.TrafficLight;
import dipl_project.Dipl_project;
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
    private Image imgSwitchGreen=new Image(Dipl_project.class.getResource("Resources/trafficLights/switchGreen.png").toString());
    private Image imgSwitchRed=new Image(Dipl_project.class.getResource("Resources/trafficLights/switchRed.png").toString());
    private Image imgSwitchOrange=new Image(Dipl_project.class.getResource("Resources/trafficLights/switchOrange.png").toString());
    
    private Image imgOkIcon=new Image(Dipl_project.class.getResource("Resources/menuIcons/okIcon.png").toString());
    private Image imgPlayIcon=new Image(Dipl_project.class.getResource("Resources/menuIcons/playIcon.png").toString());
    private Image imgPauseIcon=new Image(Dipl_project.class.getResource("Resources/menuIcons/pauseIcon.png").toString());
    private MenuItem newFile;
    private MenuItem openFile;
    private MenuItem saveFile;
    private Rectangle menuBG, simulationBG;
    private Group simulationGroup, streetGroup, root,tlGroup;
    private RadioButton carCreate;
    private RadioButton tramCreate;
    private HBox menuBox;
    private Slider curveEdit;
    private Button saveEditedCurve;
    private Label lblCurveEdit;
    private ToggleGroup createVehicleGroup;
    private Rectangle downLine;
    public UITopMenu(Group root)
    {
        this.root=root;
        
        initMenu();
        initSimulationMenu();
        initMenuButtons();
        initStreetMenu();
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
        
        menuBG=new Rectangle();
        menuBG.setFill(Color.LIGHTGRAY);
        menuBG.setHeight(80);
        menuBG.setWidth(600);
        
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
        tlGroup=new Group();
         
        menuBox=new HBox();
        simulationGroup.getChildren().add(simulationBG);
        streetGroup.getChildren().addAll(streetBG,splitRectangle,splitRectangle2);
        tlGroup.getChildren().addAll(menuBG);
        menuBox.getChildren().addAll(simulationGroup,streetGroup,tlGroup);
        top.getChildren().addAll(menu,ap,menuBox,downLine);
        root.getChildren().add(top);
    }
    public void updateMenuSize(double width)
    {
        
        menuBG.setWidth(width);
        downLine.setWidth(width);
    }
    /*public void showStreetMenu()
    {
        menuGroup.getChildren().clear();
        menuGroup.getChildren().addAll(menuBG,streetGroup);
    }
    public void showTLMenu()
    {
        menuGroup.getChildren().clear();
        menuGroup.getChildren().addAll(menuBG,tlGroup);
    }*/
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
                    Dipl_project.getSc().stopSimulation();
                    Dipl_project.getTlc().stopTrafficLights();
                    runGenerator=false;
                    //btnRunSimulation.setText("Spustit");
                    ivPlayPause.setImage(imgPlayIcon);
                }
                else
                {
                    Dipl_project.getSc().startSimulationCar();
                    Dipl_project.getSc().startSimulationTram();
                    Dipl_project.getTlc().startTrafficLights();
                    runGenerator=true;
                    //btnRunSimulation.setText("Zastavit");
                    ivPlayPause.setImage(imgPauseIcon);
                }
                    
            }
        });
        
        Label lblFrequency=new Label("Frekvence generování");
        lblFrequency.setLayoutX(90);
        lblFrequency.setLayoutY(10);
        Slider carGeneratorSize=new Slider(0, 150, 40);
        carGeneratorSize.setLayoutX(110);
        carGeneratorSize.setLayoutY(35);
        Label lblCarGenerSize=new Label("20");
        lblCarGenerSize.setLayoutX(85);
        lblCarGenerSize.setLayoutY(35);
        carGeneratorSize.valueProperty().addListener((observable, oldValue, newValue)->{
            Dipl_project.getSc().changeGenerateCarSize(newValue.intValue());
            lblCarGenerSize.setText(String.valueOf(newValue.intValue()));
        });
        
        Slider tramGeneratorSize=new Slider(0, 50, 10);
        tramGeneratorSize.setLayoutX(110);
        tramGeneratorSize.setLayoutY(55);
        Label lblTramGenerSize=new Label("5");
        lblTramGenerSize.setLayoutX(85);
        lblTramGenerSize.setLayoutY(55);
        tramGeneratorSize.valueProperty().addListener((observable, oldValue, newValue)->{
            Dipl_project.getSc().changeGenerateTramSize(newValue.intValue());
            lblTramGenerSize.setText(String.valueOf(newValue.intValue()));
        });
        simulationGroup.getChildren().addAll(btnRunSimulation,carGeneratorSize,lblCarGenerSize,tramGeneratorSize,lblTramGenerSize,lblFrequency);
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
            //Dipl_project.getSc().changeGenerateSize(newValue.intValue());
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
        
        streetGroup.getChildren().addAll(lblCreateStreet,carCreate,tramCreate,  lblCurveEdit, saveEditedCurve, curveEdit,lblCurveSmooth);
        //setActualMenu(streetGroup);
        //menuGroup.getChildren().addAll(streetGroup);
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
   
    
 
}
