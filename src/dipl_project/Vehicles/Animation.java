/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dipl_project.Vehicles;

import dipl_project.Dipl_project;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private DecimalFormat df=new DecimalFormat("##.######");
    private List<Vehicle> vehicles=new ArrayList<>();
    private TimerTask timerTask;
    private Timer timer;
    private double zoomRatio=1;
    private FileWriter speedStatistic, angleStatistic;
    private int vehID=0;
    private Map<Vehicle, List<String>> statisticsSpeedMap =new HashMap<>();
    private Map<Vehicle, List<String>> statisticsAngleMap =new HashMap<>();
    public Animation()
    {
        startAnimation();
        saveStatisticData();
    }
    public void stopAnimation()
    {
        
        timerTask.cancel();
        timer.cancel();
        //statStepTimerTask.cancel();
       //statStepTimer.cancel();
        for (Vehicle vehicle : vehicles) {
            vehicle.stopBlink();
        }
        saveStatisticData();
    }
    public void addVehicle(Vehicle vehicle)
    {
        Platform.runLater(() -> {
            vehicles.add(vehicle);
            vehicle.setId(vehID);
            vehID++;
            vehicle.changeValues(zoomRatio);
            Dipl_project.getUI().addVehicle(vehicle);
            statisticsSpeedMap.put(vehicle, new ArrayList<>());
            statisticsAngleMap.put(vehicle, new ArrayList<>());
        });
    }
    public void removeVehicle(Vehicle vehicle)
    {
        Platform.runLater(() -> {
            vehicles.remove(vehicle);
            Dipl_project.getUI().removeVehicle(vehicle);
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
            //tickSaveData(vehicle);
            addSpeedStat(vehicle,df.format(vehicle.getStatSpeed()));
            addAngleStat(vehicle,df.format(vehicle.getDAngle()));
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

    private void saveStatisticData()
    {
        
        try{
            speedStatistic = new FileWriter("stat_data_speed.txt");
            for(Map.Entry<Vehicle, List<String>> entry : statisticsSpeedMap.entrySet()) {
            List<String> values = entry.getValue();
                for (String value : values) {
                    speedStatistic.write(value+";");
                }
                speedStatistic.write("\n");
            }
            speedStatistic.close();
            
            angleStatistic = new FileWriter("stat_data_angle.txt");
            for(Map.Entry<Vehicle, List<String>> entry : statisticsAngleMap.entrySet()) {
            List<String> values = entry.getValue();
                for (String value : values) {
                    angleStatistic.write(value+";");
                }
                angleStatistic.write("\n");
            }
            
            angleStatistic.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
       
    }
    private  void addSpeedStat(Vehicle veh, String value)
    {
        statisticsSpeedMap.get(veh).add(value);
    }
    private  void addAngleStat(Vehicle veh, String value)
    {
        statisticsAngleMap.get(veh).add(value);
    }
}
