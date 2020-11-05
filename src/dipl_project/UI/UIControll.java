/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dipl_project.UI;

import dipl_project.TrafficLights.TrafficLight;
import dipl_project.TrafficLights.TrafficLightsGroup;
import dipl_project.Dipl_project;
import dipl_project.Roads.Arrow;
import dipl_project.Roads.Connect;
import dipl_project.Roads.MyCurve;
import dipl_project.Roads.RoadSegment;
import dipl_project.Simulation.SimulationControll;
import dipl_project.Vehicles.Car;
import dipl_project.Vehicles.Tram;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
import javafx.scene.input.KeyEvent;
import javafx.util.Pair;

/**
 *
 * @author Honza
 */
public class UIControll {
    private Image imgSwitchGreen=new Image(Dipl_project.class.getResource("Resources/trafficLights/switchGreen.png").toString());
    private Image imgSwitchRed=new Image(Dipl_project.class.getResource("Resources/trafficLights/switchRed.png").toString());
    private Image imgSwitchOrange=new Image(Dipl_project.class.getResource("Resources/trafficLights/switchOrange.png").toString());
    private Stage primaryStage;
    private Group root;
    private Scene scene;
    private boolean popupShown=false, runGenerator=false, addTLToList=false;
    private int initialSizeX=1200, initialSizeY=800, moveStatus=0;
    private ContextMenu popupClick, popupTL;
    private MenuItem popupSplit, popupRemove, popupRemoveTL;
    private Canvas canvas, moveCanvas;
    
    
    private List<MyCurve> curves=new ArrayList<>();
    private List<RoadSegment> segments=new ArrayList<>();
    private List<RoadSegment> startCarSegments=new ArrayList<>();
    private List<Connect> connects=new ArrayList<>();
    private SimulationControll sc;
    private DrawControll dc;
    private TrafficLight actualTL;
    private boolean isTramCreating=false;
    private List<RoadSegment> startTramSegments;
    private boolean wantDrive;
    private TrafficLightsGroup actualTLGroup;
    private UITopMenu uiTopMenu;
    private UILeftMenu uiLeftMenu;
    private UIRightMenu uiRightMenu;
    public UIControll(Stage primaryStage) {
        this.primaryStage=primaryStage;
        root = new Group(); ;
        initComponents();
        uiTopMenu=new UITopMenu(root, this);
        uiLeftMenu = new UILeftMenu(root,this);
        uiRightMenu = new UIRightMenu(root, this);
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
                Dipl_project.getTlc().stopTrafficLights();
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
    public boolean isAddTLToList() {
        return addTLToList;
    }

    public void setAddTLToList(boolean addTLToList) {
        this.addTLToList = addTLToList;
    }
    
    public void setStartSegments(List<RoadSegment> carSegments, List<RoadSegment> tramSegments)
    {
        startCarSegments=carSegments;
        startTramSegments=tramSegments;
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
    
    public List<RoadSegment> getStartCarSegments() {
        return startCarSegments;
    }
    public List<RoadSegment> getStartTramSegments() {
        return startTramSegments;
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
            root.getChildren().add(3, node);
        }  
    }
    public void addBackground(ImageView bg)
    {
        root.getChildren().add(0, bg);
        uiTopMenu.setEditBackground(true);
    }
    
    public void addComponent(Node node)
    {
        root.getChildren().add(root.getChildren().size()-5, node);
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
    
    private void initComponents()
    {
        
        
        canvas=new Canvas(initialSizeX, initialSizeY);
        EditationControll.setCanvasSize(initialSizeX, initialSizeY);
        moveCanvas=new Canvas(initialSizeX, initialSizeY-130);
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
        
        
        initTrafficLights();
        
        popupClick.getItems().addAll(popupSplit, popupRemove);
        root.getChildren().addAll(canvas, btnCheckIntersect, addMyCar,  moveCanvas);
    }
    public void setEditMode(boolean edit)
    {
        moveStatus=2;
        getUiTopMenu().getEditBackground().setSelected(!edit);
        moveCanvas.setVisible(edit);
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
    }
    public void newMyCar()
    {
        if(wantDrive)
            Dipl_project.getSc().newMyCar(getRandomStart(startCarSegments));
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
        if(startSegm!=null) 
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
    
}
