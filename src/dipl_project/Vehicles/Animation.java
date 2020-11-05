/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dipl_project.Vehicles;

import dipl_project.Dipl_project;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;

/**
 *
 * @author Honza
 */
public class Animation {
    private List<Vehicle> vehicles=new ArrayList<>();
    private TimerTask timerTask, statStepTimerTask;
    private Timer timer, statStepTimer;
    private double zoomRatio=1;
    private FileWriter fileStatInsta, fileStatStep;
    private int vehID=0;
    public Animation()
    {
        startAnimation();
        //startSaveData();
        //statStepStart();
    }
    public void stopAnimation()
    {
        
        timerTask.cancel();
        timer.cancel();
        //statStepTimerTask.cancel();
       // statStepTimer.cancel();
        for (Vehicle vehicle : vehicles) {
            vehicle.stopBlink();
        }
        stopSaveData();
    }
    private void statStepStart()
    {
        statStepTimer=new Timer();
        statStepTimerTask = new TimerTask() {
            @Override
            public void run(){
                Platform.runLater(() -> {
                for (Vehicle vehicle : vehicles) {
                    tickStepSaveData(vehicle);
                }
                });
                
            }
        };
        statStepTimer.schedule(statStepTimerTask, 100, 100);
    }
    public void addVehicle(Vehicle vehicle)
    {
        Platform.runLater(() -> {
            vehicles.add(vehicle);
            vehicle.setId(vehID);
            vehID++;
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
            tickSaveData(vehicle);
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
    private void tickStepSaveData(Vehicle veh)
    {
        try {
            fileStatStep.write(veh.getStatisticsStep()+"\n");
        } catch (IOException ex) {

        }
    }
    private void tickSaveData(Vehicle veh)
    {
        try {
            fileStatInsta.write(veh.getStatisticsInsta()+"\n");
        } catch (IOException ex) {

        }
    }
    private void startSaveData()
    {
        try {
            fileStatInsta = new FileWriter("stat_data_insta.txt");
            fileStatStep = new FileWriter("stat_data_step.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void stopSaveData()
    {
        try{
            fileStatInsta.close();
            fileStatStep.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
       
    }
}
