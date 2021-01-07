/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dipl_project.UI.GUI.TestMenu;

import dipl_project.Dipl_project;
import dipl_project.Simulation.Player;
import dipl_project.Simulation.SimulationControll;
import dipl_project.Storage.PlayerStore;
import dipl_project.TrafficLights.TrafficLight;
import dipl_project.UI.UIControlls.EditationControll;
import dipl_project.UI.UIControlls.UIControll;
import dipl_project.Vehicles.MyCar;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

/**
 *
 * @author Honza
 */
public class UITestMenu {
    private Image imgPlayIcon=new Image(Dipl_project.class.getResource("Resources/menuIcons/playIcon.png").toString());
    private Image imgPauseIcon=new Image(Dipl_project.class.getResource("Resources/menuIcons/pauseIcon.png").toString());
    private Image imgTLIcon=new Image(Dipl_project.class.getResource("Resources/menuIcons/trafficLightIcon.png").toString());
    private Image imgMyCar=new Image(Dipl_project.class.getResource("Resources/vehicles/my-car.png").toString());
    private MenuItem openFile;
    private AnchorPane ap;
    private final UIControll ui;
    private final Group root;
    private MenuItem topList;
    private Button btnRunSimulation;
    private Button btnTLPlay;
    private Button btnAddMyCar;
    private Group simulationGroup, leftGroup;
    private boolean onofTLS=true;
    private Group statisticGroup;
    private Label runTime;
    private Label runCount;
    private Label fastRun;
    private Label slowRun;
    private Label crashCount;
    private Label redCount;
    private Rectangle simulationBG;
    private Rectangle statisticsBG;
    private CheckMenuItem showStatistics;
    private UITestInitMenu testInitMenu;
    private PlayerStore playerStore;
    private Stage statisticsStage;
    private TableView<Object> playersTable;
    private boolean carExist=false;
    public UITestMenu(Group root, UIControll ui)
    {
        
        this.ui=ui;
        this.root=root;
        initMenu();
        initSimulationMenu();
        initStatisticMenu();
        root.getChildren().addAll(leftGroup);
        playerStore = new PlayerStore();
        playerStore.loadTopList();
        initStatisticsTable();
        
    }
    public void showInitMenu()
    {
        ui.getRoot().setDisable(true);
        testInitMenu=new UITestInitMenu(ui);
    }
    private void initMenu()
    {
        VBox top=new VBox();
        MenuBar menu=new MenuBar();
        Menu file=new Menu("Soubor");
        openFile=new MenuItem("Otevřít");
        openFile.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Dipl_project.getStc().loadFile();
                ui.setWantDrive(false);
                ui.showRoads(false);
                Dipl_project.getSc().removeMyCar();
                btnAddMyCar.setDisable(false);
                resetStatistics();
            }
        });
        file.getItems().addAll(openFile);
        
        
        Menu statistics=new Menu("Statistiky");

        topList=new MenuItem("Žebříček");
        topList.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                statisticsStage.show();
            }
        });
        showStatistics=new CheckMenuItem("Zobrazit data");
        showStatistics.setSelected(true);
        showStatistics.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                
                statisticGroup.setVisible(showStatistics.isSelected());  
            }
        });
        statistics.getItems().addAll(topList,showStatistics);
        
        menu.getMenus().addAll(file,statistics);
        
        ap=new AnchorPane();
        ap.setPrefSize(1200, 0);
        Pane pane=new Pane();
        pane.setPrefSize(100, 0);
        ap.getChildren().add(pane);
        
        top.getChildren().addAll(menu,ap);
        root.getChildren().add(top);
        
        ui.setDrawMode(true);
        leftGroup=new Group();
        leftGroup.setLayoutY(30);
    }
    public void resetStatistics()
    {
        setLblText(runTime,"00:00");
        setLblText(runCount,"0");
        setLblText(fastRun,"00:00");
        setLblText(slowRun,"00:00");
        setLblText(crashCount,"0");
        setLblText(redCount,"0");
    }
    private void initStatisticMenu()
    {
        statisticGroup=new Group();
        
        statisticsBG=new Rectangle();
        statisticsBG.setFill(Color.rgb(250, 250, 250, 0.7));
        statisticsBG.setHeight(135);
        statisticsBG.setWidth(180);
        statisticsBG.setArcWidth(10); 
        statisticsBG.setArcHeight(10); 
        statisticsBG.setStroke(Color.rgb(0, 0, 0, 0.2));
        
        Label lblRunTime=new Label("Doba jízdy:");
        Label lblRunCount=new Label("Počet jízd:");
        Label lblFastRun=new Label("Nejkratší čas:");
        Label lblSlowRun=new Label("Nejdelší čas:");
        Label lblCrashCount=new Label("Počet nehod:");
        Label lblRedCount=new Label("Jízda na červenou:");
        VBox statisticsLabels=new VBox(lblRunTime,lblRunCount, lblFastRun, lblSlowRun, lblCrashCount, lblRedCount);
        runTime=new Label("00:00");
        runCount=new Label("0");
        fastRun=new Label("00:00");
        slowRun=new Label("00:00");
        crashCount=new Label("0");
        redCount=new Label("0");
        
        VBox statisticsValues=new VBox(runTime,runCount, fastRun, slowRun, crashCount, redCount);
        HBox statistics=new HBox(statisticsLabels,statisticsValues);
        statistics.setSpacing(5);
        statistics.setLayoutX(5);
        statisticGroup.getChildren().addAll(statisticsBG, statistics);
        statisticGroup.setLayoutY(70);
        statisticGroup.setLayoutX(5);
        leftGroup.getChildren().add(statisticGroup);
    }
    private void initSimulationMenu()
    {
        simulationGroup=new Group();
        
        simulationBG=new Rectangle();
        simulationBG.setLayoutX(5);
        simulationBG.setLayoutY(5);
        simulationBG.setFill(Color.rgb(250, 250, 250, 0.7));
        simulationBG.setHeight(60);
        simulationBG.setWidth(180);
        simulationBG.setArcWidth(10); 
        simulationBG.setArcHeight(10); 
        simulationBG.setStroke(Color.rgb(0, 0, 0, 0.2));
        
        btnRunSimulation=new Button();
        btnRunSimulation.setLayoutX(15);
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
        iconTLRun.setLayoutX(50);
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
        btnTLPlay.setLayoutX(80);
        btnTLPlay.setLayoutY(22);
        

        
        ImageView ivMyRun=new ImageView(imgMyCar);
        ivMyRun.setFitHeight(45);
        ivMyRun.setPreserveRatio(true);
        btnAddMyCar=new Button();
        btnAddMyCar.setGraphic(ivMyRun);
        btnAddMyCar.setLayoutX(120);
        btnAddMyCar.setLayoutY(10);
        btnAddMyCar.setMinSize(50, 50);
        btnAddMyCar.setMaxSize(50, 50);
        btnAddMyCar.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(!ui.wantDrive())
                {
                    ui.setWantDrive(true);
                    ui.newMyCar();
                    btnAddMyCar.setDisable(true);
                }
                else
                {
                    MyCar myCar= Dipl_project.getSc().getMyCar();
                    myCar.initNewRoad();
                    btnAddMyCar.setDisable(true);
                    
                }
            }
        });
        
        
        
        
        simulationGroup.getChildren().addAll(simulationBG,iconTLRun,btnTLPlay,btnRunSimulation,btnAddMyCar);
        leftGroup.getChildren().addAll(simulationGroup);
        
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
    public void updateMenuSize(double width) {
         ap.setPrefWidth(width);
    }
    private void setLblText(Label lbl, String text)
    {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                lbl.setText(text);
            }
        });
    }
    public void setRunTime(String time) {
        setLblText(runTime,time);
    }

    public void setRunCount(String count) {
        setLblText(runCount,count);
    }

    public void setFastRun(String time) {
        setLblText(fastRun,time);
    }

    public void setSlowRun(String time) {
        setLblText(slowRun,time);
    }

    public void setCrashCount(String count) {
        setLblText(crashCount,count);
    }

    public void setRedCount(String count) {
        setLblText(redCount,count);
    }

    public UITestInitMenu getInitMenu() {
        return testInitMenu;
    }

    public void savePlayer() {
        if(testInitMenu!=null)
            playerStore.saveTopList(testInitMenu.getPlayer());
    }
    private void initStatisticsTable()
    {
        Group statGroup = new Group();
        Scene statScene = new Scene(statGroup, 980, 400);
        
        statisticsStage = new Stage();
        statisticsStage.setTitle("Seznam hráčů");
        statisticsStage.setScene(statScene);
        statisticsStage.heightProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            playersTable.setMinHeight((double)newValue);
            playersTable.setMaxHeight((double)newValue);
        });
        statisticsStage.widthProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            playersTable.setMinWidth((double)newValue);
            playersTable.setMaxWidth((double)newValue);
        });
        playersTable = new TableView<>();
        playersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        TableColumn name = new TableColumn("Jméno");
        name.setMinWidth(80);
        name.setResizable(false);
        name.setCellValueFactory(
                new PropertyValueFactory<Player, String>("name"));  
        
        TableColumn totalRuns = new TableColumn("Počet kol");
        totalRuns.setMinWidth(40);
        totalRuns.setResizable(false);
        totalRuns.setCellValueFactory(
                new PropertyValueFactory<Player, String>("runCount"));
        
        TableColumn avgTime = new TableColumn("Průměrný čas");
        avgTime.setMinWidth(120);

        avgTime.setResizable(false);
        avgTime.setCellValueFactory(
                new PropertyValueFactory<Player, String>("avgTime"));
        
        TableColumn crashCount = new TableColumn("Počet nehod");
        crashCount.setMinWidth(100);
        crashCount.setResizable(false);
        crashCount.setCellValueFactory(
                new PropertyValueFactory<Player, String>("crashCount"));
        
        TableColumn redCount = new TableColumn("Průjezd na červenou");
        redCount.setMinWidth(160);
        redCount.setResizable(false);
        redCount.setCellValueFactory(
                new PropertyValueFactory<Player, String>("redCount"));
        
        TableColumn fastRun = new TableColumn("Nejlepší kolo");
        fastRun.setMinWidth(120);
        fastRun.setResizable(false);
        fastRun.setCellValueFactory(
                new PropertyValueFactory<Player, String>("fastRun"));
        
        TableColumn slowRun = new TableColumn("Nejhorší kolo");
        slowRun.setMinWidth(120);
        slowRun.setResizable(false);
        slowRun.setCellValueFactory(
                new PropertyValueFactory<Player, String>("slowRun"));
        
        TableColumn marks = new TableColumn("Hodnocení");
        marks.setMinWidth(90);
        marks.setResizable(false);
        marks.setCellValueFactory(
                new PropertyValueFactory<Player, String>("mark"));
        
        TableColumn dates = new TableColumn("Datum");
        dates.setMinWidth(140);
        dates.setResizable(false);
        dates.setCellValueFactory(
                new PropertyValueFactory<Player, String>("date"));
        
        playersTable.getColumns().addAll(name, marks, totalRuns, avgTime, fastRun, slowRun, crashCount,   redCount, dates);
        statGroup.getChildren().addAll(playersTable);
        playersTable.getItems().addAll(playerStore.getPlayers());
    }

    public void setEnableAddCar() {
        btnAddMyCar.setDisable(false);
    }
    
}
