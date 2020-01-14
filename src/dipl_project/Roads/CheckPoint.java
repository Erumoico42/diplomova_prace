/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dipl_project.Roads;

import dipl_project.UI.DrawControll;
import dipl_project.UI.UIControll;
import java.util.ArrayList;
import java.util.List;
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
public class CheckPoint {
    private final RoadSegment rs, prim;
    private int distance=0, id;
    private List<CheckPoint> secondaryRP=new ArrayList<>();
    private boolean enabled=true;
    private HBox checkPointInfo;
    private Button removeFromCPs;
    private DrawControll dc=dipl_project.Dipl_project.getDC();
    private UIControll ui=dipl_project.Dipl_project.getUI();
    private Label lblInfo;
    private Spinner<Integer> secondaryDistance;
    public CheckPoint(RoadSegment prim, RoadSegment sec) {
        this.rs = sec;
        this.prim=prim;
        initInfoBox();
    }
    public CheckPoint(RoadSegment prim,RoadSegment rs, int distance) {
        this.rs = rs;
        this.prim=prim;
        this.distance=distance;
        initInfoBox();
    }

    public int getDistance() {
        return distance;
    }

    public RoadSegment getPrim() {
        return prim;
    }
    
    public void setDistance(int distance) {
        this.distance = distance;
    }

    public RoadSegment getRs() {
        return rs;
    }

    public List<CheckPoint> getSecondaryCP() {
        return secondaryRP;
    }

    public void addSecondaryCP(CheckPoint srs) {
        secondaryRP.add(srs);
    }
    public void clearSecondaryRS()
    {
        for (CheckPoint srs : secondaryRP) {
            srs.getRs().removeSecondaryCheckPointsByRS(rs);
        }
        secondaryRP.clear();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    private void initInfoBox()
    {
        checkPointInfo=new HBox();
        checkPointInfo.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                prim.getShape().setStrokeWidth(13);
                System.out.println(id);
            }
        });
        checkPointInfo.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                prim.getShape().setStrokeWidth(7);
            }
        });
        removeFromCPs=new Button("X");
        removeFromCPs.setFont(new Font(8));
        removeFromCPs.setMinSize(25, 25);
        removeFromCPs.setMaxSize(25, 25);
        removeFromCPs.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                CheckPoint cpRem=dc.getActualRS().getCPByRS(rs);
                dc.getActualRS().getCheckPoints().remove(cpRem);
                rs.setDefRoadSegment();
                ui.removeCPFromList(cpRem);
            }
        });
        lblInfo=new Label();
        lblInfo.setLayoutX(5);
        lblInfo.setLayoutY(10);
        lblInfo.setMinWidth(25);
        lblInfo.setMaxWidth(25);
        lblInfo.setText(String.valueOf(rs.getId()));
        SpinnerValueFactory.IntegerSpinnerValueFactory valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 20, distance);
        secondaryDistance=new Spinner<>(valueFactory);
        secondaryDistance.setMinWidth(65);
        secondaryDistance.setMaxWidth(65);
        secondaryDistance.setEditable(false);
        secondaryDistance.valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                
                RoadSegment actRS=dc.getActualRS();
                CheckPoint cp =actRS.getCPByRS(rs);
                if(newValue>cp.getDistance()+1)
                    secondaryDistance.getValueFactory().setValue(cp.getDistance());
                cp.setEnabled(newValue==0);
                rs.setRun(true);
                rs.runSecondary(0, newValue, oldValue, rs);
                cp.clearSecondaryRS();
                rs.findSecondarySegment(0, newValue, actRS);
                
            }
        });
        checkPointInfo.getChildren().addAll(lblInfo,secondaryDistance, removeFromCPs);
    }
    public HBox getInfo()
    {
        return checkPointInfo;
    }
}
