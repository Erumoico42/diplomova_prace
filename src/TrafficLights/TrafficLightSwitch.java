/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TrafficLights;

import dipl_project.Dipl_project;
import java.util.Timer;
import java.util.TimerTask;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;


/**
 *
 * @author Honza
 */
public class TrafficLightSwitch {
    private int newStatus, switchTime, actualTime=1;
    private Image imgSwitchGreen=new Image(Dipl_project.class.getResource("Resources/trafficLights/switchGreen.png").toString());
    private Image imgSwitchRed=new Image(Dipl_project.class.getResource("Resources/trafficLights/switchRed.png").toString());
    private TrafficLight trafficLight;
    private Timer timer;
    private TimerTask timerTask;
    private HBox tlSwitchBox;
    private TrafficLightsGroup tlg;
    private ComboBox selectColorBox;
    public TrafficLightSwitch(int switchTime, TrafficLight trafficLight, TrafficLightsGroup tlg) {
        
        tlSwitchBox=new HBox();

        ObservableList<Image> switchImages = FXCollections.observableArrayList();
        switchImages.addAll(imgSwitchGreen, imgSwitchRed);
        selectColorBox= createComboBox(switchImages);
        selectColorBox.setMinWidth(50);
        selectColorBox.setMaxWidth(50);
        selectColorBox.getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) -> {
            newStatus=switchImages.indexOf((Image)newValue);
            if(newStatus==1)
                newStatus++;
        }); 
        
        
        Button btnRemoveTLS=new Button("-");
        btnRemoveTLS.setMaxSize(30, 30);
        btnRemoveTLS.setMinSize(30, 30);
        btnRemoveTLS.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                tlg.removeTrafficLightSwitch(getThis());
            }
        });
        tlSwitchBox.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                trafficLight.selectTL();
            }
        });
        tlSwitchBox.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                trafficLight.deselectTL();
            }
        });
        Label lblInfo=new Label("  id: "+String.valueOf(trafficLight.getId())+"  ");
        lblInfo.setMinSize(90, 30);
        lblInfo.setMaxSize(90, 30);
        tlSwitchBox.getChildren().addAll(lblInfo,selectColorBox,btnRemoveTLS);
        this.switchTime = switchTime;
        this.trafficLight = trafficLight;
        this.tlg=tlg;
    }
    
    private ComboBox<Image> createComboBox(ObservableList<Image> data) {
        ComboBox<Image> combo = new ComboBox<>();
        combo.getItems().addAll(data);
        combo.setButtonCell(new ImageListCell());
        combo.setCellFactory(listView -> new ImageListCell());
        combo.getSelectionModel().select(0);
        return combo;
    }
 
    class ImageListCell extends ListCell<Image> {
        private final ImageView view;
 
        ImageListCell() {
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            view = new ImageView();
        }
 
        @Override protected void updateItem(Image item, boolean empty) {
            super.updateItem(item, empty);
 
            if (item == null || empty) {
                setGraphic(null);
            } else {
                view.setImage(item);
                setGraphic(view);
            }
        }
 
    }
    
    public void activate()
    {
        if(trafficLight.isOrangeSwitching())
        {
            trafficLight.setOrangeSwitching(false);
            trafficLight.stopOrangeSwitching();
        }
            
        setOrangeStatus();
        timer=new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run(){
               activateTLs();
               actualTime++;
               trafficLight.setTimeCountDown(switchTime-actualTime);
            }
        };
        timer.schedule(timerTask, 1000, 1000);
        
        
    }
    public void stopSwitch()
    {
        if(timerTask!=null)
            timerTask.cancel();
        if(timer!=null)
        {
            timer.cancel(); 
        }
              
        actualTime=0;
    }
    private void activateTLs()
    {   
        if(newStatus!=1)
        {
            if(actualTime==switchTime)
            {
                trafficLight.setStatus(newStatus);
                stopSwitch(); 
            }
        }
        
        
    }
    
    
    private void setOrangeStatus()
    {
        if(newStatus==2)
        {
            trafficLight.setStatus(1);
        }
        else if(newStatus==0)
        {
            trafficLight.setStatus(3);
        }
    }
    private TrafficLightSwitch getThis()
    {
        return this;
    }

    public int getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(int newStatus) {
         
        this.newStatus = newStatus;
        if(newStatus==2)
            newStatus--;
        selectColorBox.getSelectionModel().select(newStatus);
    }

    public int getSwitchTime() {
        return switchTime;
    }

    public void setSwitchTime(int switchTime) {
        this.switchTime = switchTime;
    }

    public HBox getTlSwitchBox() {
        return tlSwitchBox;
    }

    public TrafficLight getTrafficLight() {
        return trafficLight;
    }
    
    
}
