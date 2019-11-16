/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dipl_project.UI;

import dipl_project.Dipl_project;
import dipl_project.Roads.Connect;
import dipl_project.Roads.MyCurve;
import dipl_project.Roads.RoadSegment;
import dipl_project.Vehicles.Vehicle;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author Honza
 */
public class UIControll {
    private Stage primaryStage;
    private Group root;
    private Scene scene;
    private boolean popupShown=false, addCP=false;
    private int initialSizeX=900, initialSizeY=600;
    private ContextMenu popupClick;
    private MenuItem popupSplit, popupRemove;
    private Canvas canvas;
    private ListView<HBox> selectedCPs;
    private List<MyCurve> curves=new ArrayList<>();
    private List<RoadSegment> segments=new ArrayList<>();
    private List<RoadSegment> startSegments=new ArrayList<>();
    private List<Connect> connects=new ArrayList<>();
    private CheckBox checkBoxNewCP;
    public UIControll(Stage primaryStage) {
        this.primaryStage=primaryStage;
        root = new Group(); 
        initComponents();
        scene = new Scene(root, initialSizeX, initialSizeY);
        primaryStage.setTitle("Diplomová práce");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    public void setStartSegments(List<RoadSegment> segments)
    {
        startSegments=segments;
    }

    public List<RoadSegment> getStartSegments() {
        return startSegments;
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
                curve.getStartControll().getLine());
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
    private void addComponentsDown(Node...nodes)
    {
        for (Node node : nodes) {
            root.getChildren().add(0, node);
        }
        
    }
    public void addComponent(Node node)
    {
        root.getChildren().add(root.getChildren().indexOf(selectedCPs)-1, node);
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
        checkBoxNewCP=new CheckBox("Upravit přednosti");
        checkBoxNewCP.setMinSize(125, 50);
        checkBoxNewCP.setMaxSize(125, 50);
        checkBoxNewCP.setVisible(false);
        checkBoxNewCP.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                addCP=checkBoxNewCP.isSelected();
            }
        });
        selectedCPs=new ListView<HBox>();
        selectedCPs.setLayoutY(40);
        selectedCPs.setVisible(false);
        canvas=new Canvas(initialSizeX, initialSizeY);
        updateCPsPosition();
        Button btnAdd=new Button("Přidat");
        btnAdd.setLayoutX(100);
        btnAdd.setLayoutY(10);
        btnAdd.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //System.out.println(startSegments.size());
                Vehicle veh=new Vehicle(getRandomStart());
                //System.out.println(startSegments.get(0).getRsNext().get(0).getRsNext().get(0).getRsNext().size());
                
            }
        });
        Button btnCheckIntersect=new Button("Zkontrolovat");
        btnCheckIntersect.setLayoutX(10);
        btnCheckIntersect.setLayoutY(10);
        btnCheckIntersect.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Dipl_project.getRC().findIntersects();
            }
        });
        
        Button btnHideAutoFound=new Button("Skrýt přednosti");
        btnHideAutoFound.setLayoutX(200);
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
        btnReload.setLayoutX(400);
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
        popupClick.getItems().addAll(popupSplit, popupRemove);
        root.getChildren().addAll(canvas, btnAdd, btnCheckIntersect, selectedCPs, btnHideAutoFound, checkBoxNewCP, btnReload);
    }
    public void showPopUp(Point loc, Connect con)
    {
        //split.setDisable(!con.canSplit());;
        popupShown=true;
        popupClick.show(root, primaryStage.getX()+loc.getX()+9, primaryStage.getY()+loc.getY()+30);
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
            for (RoadSegment checkPoint : rs.getCheckPoints()) {
                addCPToList(checkPoint);
            }
            
        });
    }
    public void removeCPFromList(RoadSegment rs)
    {
        Platform.runLater(
        () -> {
            selectedCPs.getItems().remove(rs.getCheckPointInfo());
        });
    }
    public void addCPToList(RoadSegment rs)
    {
        Platform.runLater(
        () -> {
            selectedCPs.getItems().add(rs.getCheckPointInfo());
        });
    }
    public void hidePopUp()
    {
        popupShown=false;
        popupClick.hide();
    }
    public boolean isPopupShown()
    {
        return popupShown;
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
                    next=true;
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

    public Stage getPrimaryStage() {
        return primaryStage;
    }
    
}
