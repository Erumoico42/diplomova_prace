/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dipl_project.Roads;

import dipl_project.UI.UIControlls.DrawControll;
import dipl_project.UI.UIControlls.UIControll;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;

/**
 *
 * @author Honza
 */
public class WatchPoint {
    private int id, distance=2;
    private RoadSegment sec;
    private HBox watchPointInfo;
    private Button removeFromWPs;
    private DrawControll dc=dipl_project.Dipl_project.getDC();
    private UIControll ui=dipl_project.Dipl_project.getUI();
    private Label lblInfo;
    private Spinner<Integer> freeDistance;
    private RoadSegment prim;
    public WatchPoint(RoadSegment prim, RoadSegment sec) {
        this.sec = sec;
        this.prim=prim;
        initInfoBox();
    }
    public WatchPoint(RoadSegment prim,RoadSegment sec, int distance) {
        this.sec = sec;
        this.prim=prim;
        this.distance=distance;
        initInfoBox();
    }
    public WatchPoint getThisWP()
    {
        return this;
    }
     private void initInfoBox()
    {
        watchPointInfo=new HBox();
        watchPointInfo.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                sec.getShape().setStrokeWidth(13);
            }
        });
        watchPointInfo.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                sec.getShape().setStrokeWidth(7);
            }
        });
        removeFromWPs=new Button("X");
        removeFromWPs.setFont(new Font(8));
        removeFromWPs.setMinSize(25, 25);
        removeFromWPs.setMaxSize(25, 25);
        removeFromWPs.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                WatchPoint wpRem=getThisWP();
                dc.getActualRS().removeWP(wpRem);
                sec.setDefRoadSegment();
                ui.getUiLeftMenu().removeWPFromList(wpRem);
            }
        });
        lblInfo=new Label();
        lblInfo.setLayoutX(5);
        lblInfo.setLayoutY(10);
        lblInfo.setMinWidth(25);
        lblInfo.setMaxWidth(25);
        lblInfo.setText("WP");
        SpinnerValueFactory.IntegerSpinnerValueFactory valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 20, distance);
        freeDistance=new Spinner<>(valueFactory);
        freeDistance.setMinWidth(65);
        freeDistance.setMaxWidth(65);
        freeDistance.setEditable(false);
        freeDistance.valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                distance=newValue;
            }
        });
        watchPointInfo.getChildren().addAll(lblInfo,freeDistance, removeFromWPs);
    }
    public HBox getInfo()
    {
        return watchPointInfo;
    }

    public RoadSegment getRs() {
        return sec;
    }

    public RoadSegment getPrim() {
        return prim;
    }

    public int getDistance() {
        return distance;
    }
    
    
}
