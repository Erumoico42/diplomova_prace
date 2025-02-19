/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dipl_project.UI.UIControlls;

import dipl_project.UI.GUI.EditMenu.UITopMenu;
import dipl_project.UI.GUI.EditMenu.UILeftMenu;
import dipl_project.UI.GUI.EditMenu.UIRightMenu;
import dipl_project.TrafficLights.TrafficLight;
import dipl_project.TrafficLights.TrafficLightsGroup;
import dipl_project.Dipl_project;
import dipl_project.Roads.Arrow;
import dipl_project.Roads.Connect;
import dipl_project.Roads.MyCurve;
import dipl_project.Roads.RoadSegment;
import dipl_project.Roads.VehicleGenerating.StartSegment;
import dipl_project.Simulation.SimulationControll;
import dipl_project.Storage.PlayerStore;
import dipl_project.Storage.SleeperStore;
import dipl_project.UI.GUI.TestMenu.UITestMenu;
import dipl_project.Vehicles.MyCar;
import dipl_project.Vehicles.Vehicle;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.util.Pair;

/**
 *
 * @author Honza
 */
public class UIControll {
    private String guiStatus="-t";
    private Image imgSwitchGreen=new Image(Dipl_project.class.getResource("Resources/trafficLights/switchGreen.png").toString());
    private Image imgSwitchRed=new Image(Dipl_project.class.getResource("Resources/trafficLights/switchRed.png").toString());
    private Image imgSwitchOrange=new Image(Dipl_project.class.getResource("Resources/trafficLights/switchOrange.png").toString());
    private Stage primaryStage;
    private Group root, curvesGroup, connectsGroup, controlsGroup, segmentsGroup, arrowsGroup, tlsGroup, backgroundGroup, vehiclesGroup;
    private Scene scene;
    private boolean popupShown=false, runGenerator=false, addTLToList=false;
    private int initialSizeX=1200, initialSizeY=800, moveStatus=0;
    private ContextMenu popupClick, popupTL;
    private MenuItem popupSplit, popupRemove, popupRemoveTL;
    private Canvas canvas, moveCanvas;
    private boolean enableMouseMoveExit=false;
    
    private List<MyCurve> curves=new ArrayList<>();
    private List<RoadSegment> segments=new ArrayList<>();
    private List<StartSegment> startTramSegments=new ArrayList<>();
    private List<StartSegment> startCarSegments=new ArrayList<>();
    private List<Connect> connects=new ArrayList<>();
    private SimulationControll sc;
    private DrawControll dc;
    private TrafficLight actualTL;
    private boolean isTramCreating=false;
    private boolean wantDrive;
    private TrafficLightsGroup actualTLGroup;
    private UITopMenu uiTopMenu;
    private UILeftMenu uiLeftMenu;
    private UIRightMenu uiRightMenu;
    private UITestMenu uiTestMenu;
    public UIControll(Stage primaryStage) {
        this.primaryStage=primaryStage;
        root = new Group(); ;
        initComponents();
        scene = new Scene(root, initialSizeX, initialSizeY);
       
        
        
        primaryStage.setTitle("Diplomová práce");
        primaryStage.setScene(scene);
        primaryStage.show();
        initStageHandler();
        initSceneHandler();
        
    }

    public String getGuiStatus() {
        return guiStatus;
    }
    
    //rozpoznani modu aplikace podle parametru
    public void initMenu(String[] args)
    {
        for (String arg : args) {
            if(arg.split("=").length==2){
                guiStatus=arg.split("=")[1];
                break;
            }
        }
        switch(guiStatus)
        {
            case "-e":
            {
                uiTopMenu=new UITopMenu(root, this);
                uiLeftMenu = new UILeftMenu(root,this);
                uiRightMenu = new UIRightMenu(root, this);
                break;
            }
            case "-t":
            {
                
                uiTestMenu=new UITestMenu(root, this);
                break;
            }
            case "-s":
            {
                showRoads(false);
                runScreenSaver();
                new SleeperStore().loadScreenSaverConfig();
                
                break;
            }
            default:
            {
                guiStatus="-t";
                uiTestMenu=new UITestMenu(root, this);
                break;
            }
        }
         
    }
    private void runScreenSaver()
    {
        
        primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        primaryStage.setFullScreen(true);
        try {
            Thread.sleep(5);
        } catch (InterruptedException ex) {
            Logger.getLogger(UIControll.class.getName()).log(Level.SEVERE, null, ex);
        }
        scene.setFill(Color.BLACK);
        scene.setOnMouseMoved(new EventHandler<MouseEvent>() {
            
            @Override
            public void handle(MouseEvent event) {
                
                if(enableMouseMoveExit)
                    Dipl_project.closeApp();
                enableMouseMoveExit=true;
            }
        });
        
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                Dipl_project.closeApp();
            }
        });
        
    }
    private void initStageHandler()
    {
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                Dipl_project.closeApp();
            }
        });
    }
    public void updateWidth(double width)
    {
        switch(guiStatus)
        {
            case "-e":
            {
                getUiLeftMenu().updateCPsPosition();
                getUiTopMenu().updateMenuSize(width);
                getUiRightMenu().updateTLGsPosition();
                break;
            }
            case "-t":
            {
                getUiTestMenu().updateMenuSize(width);
                break;
            }
            case "-s":
            {
                break;
            }
        }
        
    }
    public void updateheight(double height)
    {
        switch(guiStatus)
        {
            case "-e":
            {
                getUiLeftMenu().updateCPsPosition();
                getUiRightMenu().updateTLGsPosition();
                break;
            }
            case "-t":
            {
                break;
            }
            case "-s":
            {
                break;
            }
        }
        
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
    public boolean isAddTLToList() {
        return addTLToList;
    }

    public void setAddTLToList(boolean addTLToList) {
        this.addTLToList = addTLToList;
    }
    
    public void setStartSegments(List<StartSegment> startCarSegments,List<StartSegment> startTramSegments)
    {
        this.startCarSegments=startCarSegments;
        this.startTramSegments=startTramSegments;
    }

    public TrafficLightsGroup getActualTLGroup() {
        return actualTLGroup;
    }

    public void setActualTLGroup(TrafficLightsGroup actualTLGroup) {
        this.actualTLGroup = actualTLGroup;
    }
    
    public void setDc(DrawControll dc) {
        this.dc = dc;
    }

    public void setSc(SimulationControll sc) {
        this.sc = sc;
    }
    
    public List<StartSegment> getStarCarSegments() {
        return startCarSegments;
    }
    public List<StartSegment> getStarTramSegments() {
        return startTramSegments;
    }
    public void addTL(TrafficLight tl)
    {
        addComponent(tlsGroup, tl.getTlImage());
    }
    public void removeTL(TrafficLight tl)
    {
        tlsGroup.getChildren().remove(tl.getTlImage());
    }
    public void addCurve(MyCurve curve)
    {
        addComponent(curvesGroup,curve.getCurve(),curve.getEndControll().getLine(), curve.getStartControll().getLine());
        addComponent(controlsGroup
                ,curve.getStartControll().getControll(),curve.getEndControll().getControll());
        curves.add(curve);
        
    }
    public void addArrow(Arrow arrow)
    {
        addComponent(arrowsGroup,arrow.getArrow());
    }
    public void removeArrow(Arrow arrow)
    {
        arrowsGroup.getChildren().remove(arrow.getArrow());
    }
    public void addRoadSegment(RoadSegment rs)
    {
        segments.add(rs);
        segmentsGroup.getChildren().add(0,rs.getShape());
        segmentsGroup.getChildren().add(rs.getRoadSegment());
    }

    public List<MyCurve> getCurves() {
        return curves;
    }
    public void addComponent(Group group, Node...nodes)
    {
        group.getChildren().addAll(nodes);
    }
    public void addConnect(Connect connect)
    {
        connects.add(connect);
        addComponent(connectsGroup,connect.getConnect());
    }
    public void moveConnectUp(Connect connect)
    {
        connectsGroup.getChildren().remove(connect.getConnect());
        addComponent(connectsGroup, connect.getConnect());
    }
    public void removeCurve(MyCurve curve)
    {
        
        curvesGroup.getChildren().removeAll(curve.getCurve(),curve.getEndControll().getLine(), 
                curve.getStartControll().getLine());
        for (Arrow arrow : curve.getArows()) {
            arrowsGroup.getChildren().remove(arrow.getArrow());
        }
        controlsGroup.getChildren().removeAll(curve.getStartControll().getControll(),
                curve.getEndControll().getControll());
        arrowsGroup.getChildren().removeAll(curve.getStartArrow().getArrow(), curve.getEndArrow().getArrow());
        curves.remove(curve);
    }
    public void removeRoadSegment(RoadSegment rs)
    {
        segments.remove(rs);
        segmentsGroup.getChildren().removeAll(rs.getShape(), rs.getRoadSegment());
    }
    public void removeConnect(Connect connect)
    {
        connects.remove(connect);
        connectsGroup.getChildren().remove(connect.getConnect());
    }
    public void addComponentsDown(Node...nodes)
    {
        for (Node node : nodes) {
            root.getChildren().add(3, node);
        }  
    }
    public void addBackground(ImageView bg)
    {
        backgroundGroup.getChildren().add(bg);
        if(guiStatus.equals("-e"))
            uiTopMenu.setEditBackground(true);
    }
    public void removeBackground(ImageView bg)
    {
        backgroundGroup.getChildren().remove(bg);
    }
    public List<Connect> getConnects()
    {
        return connects;
    }
    public void addVehicle(Vehicle vehicle)
    {
        addComponent(vehiclesGroup, vehicle.getIV(), vehicle.getIvMaskBlinker(), vehicle.getIvMaskBreaks());
    }
    public void removeVehicle(Vehicle vehicle)
    {
        vehiclesGroup.getChildren().removeAll(vehicle.getIV(), vehicle.getIvMaskBlinker(), vehicle.getIvMaskBreaks());
    }
    public void enableStreet(boolean enable)
    {
        connectsGroup.setDisable(!enable);
        controlsGroup.setDisable(!enable);
        curvesGroup.setDisable(!enable);
        segmentsGroup.setDisable(!enable);
    }
    public void enableSegments(boolean enable)
    {
        segmentsGroup.setDisable(!enable);
    }
    private void initComponents()
    {
        
        
        canvas=new Canvas(initialSizeX, initialSizeY);
        EditationControll.setCanvasSize(initialSizeX, initialSizeY);
        moveCanvas=new Canvas(initialSizeX, initialSizeY);
        moveCanvas.setVisible(false);
        
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
        
        
        /*Button btnReload=new Button("Reload");
        btnReload.setLayoutX(360);
        btnReload.setLayoutY(10);
        btnReload.setVisible(false);
        btnReload.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Dipl_project.loadRules();
            }
        });*/
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
        wantDrive=false;
        
        
        
        initTrafficLights();
        
        popupClick.getItems().addAll(popupSplit, popupRemove);
        
        curvesGroup=new Group(); 
        connectsGroup=new Group();
        controlsGroup=new Group();
        segmentsGroup=new Group();
        arrowsGroup=new Group();
        tlsGroup=new Group();
        backgroundGroup=new Group();
        vehiclesGroup=new Group();
        
        root.getChildren().addAll(backgroundGroup,canvas,  curvesGroup,arrowsGroup,segmentsGroup,connectsGroup,controlsGroup,tlsGroup,vehiclesGroup,moveCanvas);
    }
    public void setWantDrive(boolean wantDrive)
    {
        this.wantDrive=wantDrive;
    }
    public void setEditMode(boolean edit)
    {
        setDrawMode(edit);
        getUiTopMenu().setEditBackground(edit);
        
    }
    public void setDrawMode(boolean draw)
    {
        moveStatus=2;
        moveCanvas.setVisible(draw);
    }
    public void showRoads(boolean show)
    {
        curvesGroup.setVisible(show);
        segmentsGroup.setVisible(show);
        connectsGroup.setVisible(show);
        controlsGroup.setVisible(show);
        arrowsGroup.setVisible(show);/*
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
            segment.getShape().setVisible(show);
        }
        for (Connect connect : connects) {
            connect.getConnect().setVisible(show);
            for(Map.Entry<Pair<MyCurve, MyCurve>, RoadSegment> rs : connect.getConnectSegmentsMap().entrySet()) {
                RoadSegment segment = rs.getValue();
                segment.setVisible(false);
            }
        }*/
    }
    public RoadSegment getRandomStartCar()
    {
        return getRandomStart(startCarSegments);
    }
    public void newMyCar()
    {
        if(wantDrive)
        {
            RoadSegment rs=getStartNoCare();
            if(rs!=null)
                Dipl_project.getSc().newMyCar(rs);
        }
            
    }
    private void initTrafficLights()
    {
        
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
        Menu popupChangeColorTL=new Menu("Změnit barvu");
        ImageView ivRed=new ImageView(imgSwitchRed);
        MenuItem comboRed=new MenuItem();
        comboRed.setGraphic(ivRed);
        comboRed.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                actualTL.setStatus(2);
            }
        });
        
        ImageView ivOrange=new ImageView(imgSwitchOrange);
        MenuItem comboOrange=new MenuItem();
        comboOrange.setGraphic(ivOrange);
        comboOrange.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                actualTL.setStatus(1);
                actualTL.setOrangeSwitching(true);
            }
        });
        ImageView ivGreen=new ImageView(imgSwitchGreen);
        MenuItem comboGreen=new MenuItem();
        comboGreen.setGraphic(ivGreen);
        comboGreen.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                actualTL.setStatus(0);
            }
        });
        popupChangeColorTL.getItems().addAll(comboRed,comboOrange,comboGreen);
        popupTL.getItems().addAll(popupChangeColorTL,popupRemoveTL);
        
    }
    public void setActualTL(TrafficLight tl)
    {
        actualTL=tl;
    }
    public void showPopUp(Point loc, boolean enableSplit)
    {
        popupSplit.setDisable(!enableSplit);
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
   

    public void setMoveStatus(int moveStatus) {
        this.moveStatus = moveStatus;
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
    public RoadSegment getStartNoCare()
    {
        StartSegment ret=startCarSegments.get((int)(Math.random()*startCarSegments.size()));
        RoadSegment rsStart=ret.getStartRS();
        if(rsStart!=null)
        {
            Vehicle veh=rsStart.getVehicle();
            if(veh!=null)
                veh.removeVehicle();
            for (RoadSegment roadSegment : rsStart.getRsSameWay()) {
                veh=roadSegment.getVehicle();
                if(veh!=null)
                    veh.removeVehicle();
                for (RoadSegment roadSegment2 : roadSegment.getRsSameWay()) {
                    veh=roadSegment2.getVehicle();
                    if(veh!=null)
                        veh.removeVehicle();
                }
            }
            for (RoadSegment roadSegment : rsStart.getRsNext()) {
                veh=roadSegment.getVehicle();
                if(veh!=null)
                    veh.removeVehicle();
                for (RoadSegment roadSegment2 : roadSegment.getRsSameWay()) {
                    veh=roadSegment2.getVehicle();
                    if(veh!=null)
                        veh.removeVehicle();
                }
            }
        }
        return ret.getStartRS();
    }
    public RoadSegment getRandomStart(List<StartSegment> startSegm)
    {
        
        
        List<StartSegment> sstoGen=new ArrayList<>();
        if(startSegm!=null) 
            sstoGen.addAll(startSegm);
        
        while(!sstoGen.isEmpty())
        {
            
            boolean next=false;
            StartSegment ret=sstoGen.get((int)(Math.random()*sstoGen.size()));
            sstoGen.remove(ret);
            if(ret!=null)
            {
                if(ret.getStartRS().getVehicle()!=null)
                {
                    next=true;
                }
                else
                {
                    for (RoadSegment rs : ret.getStartRS().getRsNext()) {
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
                return ret.getStartRS();
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
    public List<RoadSegment> getSegments() {
        return segments;
    }

    public boolean isTramCreating() {
        return isTramCreating;
    }

    public void setIsTramCreating(boolean isTramCreating) {
        this.isTramCreating = isTramCreating;
    }

    public UITopMenu getUiTopMenu() {
        return uiTopMenu;
    }

    public UILeftMenu getUiLeftMenu() {
        return uiLeftMenu;
    }

    public UIRightMenu getUiRightMenu() {
        return uiRightMenu;
    }

    public boolean wantDrive() {
        return wantDrive;
    }

    public UITestMenu getUiTestMenu() {
        return uiTestMenu;
    }
    
    public void savePlayer() {
        if(uiTestMenu!=null)
        {
            uiTestMenu.savePlayer();
            
        }
    }
    
}
