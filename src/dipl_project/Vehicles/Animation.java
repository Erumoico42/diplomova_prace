/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dipl_project.Vehicles;

import dipl_project.Dipl_project;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;

/**
 *
 * @author Honza
 */
public class Animation {
    private List<Vehicle> vehicles=new ArrayList<>();
    private TimerTask timerTask;
    private Timer timer;
    private double zoomRatio=1;
    public Animation()
    {
        startAnimation();
    }
    public void stopAnimation()
    {
        timerTask.cancel();
        timer.cancel();
        for (Vehicle vehicle : vehicles) {
            vehicle.stopBlink();
        }
    }
    public void addVehicle(Vehicle vehicle)
    {
        Platform.runLater(() -> {
            vehicles.add(vehicle);
            vehicle.changeValues(zoomRatio);
            Dipl_project.getUI().addComponents(vehicle.getIV(), vehicle.getIvMaskBlinker(), vehicle.getIvMaskBreaks());
        });
    }
    public void removeVehicle(Vehicle vehicle)
    {
        Platform.runLater(() -> {
            vehicles.remove(vehicle);
            Dipl_project.getUI().removeComponents(vehicle.getIV(), vehicle.getIvMaskBlinker(), vehicle.getIvMaskBreaks());
        });
    }
    public void startAnimation()
    {
        timer=new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run(){
                
                    tick();
               
            }
        };
        timer.schedule(timerTask, 20, 20);
    }
    private void tick()
    {
        Platform.runLater(() -> {
        for (Vehicle vehicle : vehicles) {
            
                vehicle.tick();
            
        }
        });
    }
    public void cleanVehicles()
    {
        List<Vehicle> vehs=new ArrayList<>();
        vehs.addAll(vehicles);
        for (Vehicle vehicle : vehs) {
            vehicle.removeCar();
        }
    }
    public void setZoomRatio(double zoomRatio)
    {
        this.zoomRatio=zoomRatio;
    }
    public void changeZoomRatio(double zoomRatio)
    {
         Platform.runLater(() -> {
        for (Vehicle vehicle : vehicles) {
           
                vehicle.changeValues(zoomRatio);
                vehicle.move();
            
        }
        });
    }
    public void moveVehicles()
    {
        
        for (Vehicle vehicle : vehicles) {
            vehicle.move();
        }
    }
    public List<Vehicle> getVehicles()
    {
        return vehicles;
    }
}
