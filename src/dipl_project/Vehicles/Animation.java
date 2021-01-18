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
import javafx.application.Platform;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

/**
 *
 * @author Honza
 */
public class Animation {
    private DecimalFormat df=new DecimalFormat("##.######");
    private List<Vehicle> vehicles=new ArrayList<>();
    private TimerTask animationTimerTask;
    private Timer animationTimer;
    private double zoomRatio=1;
    private FileWriter speedStatistic, angleStatistic;
    private int vehID=0;
    private final Map<Vehicle, List<String>> statisticsSpeedMap =new HashMap<>();
    private final Map<Vehicle, List<String>> statisticsAngleMap =new HashMap<>();
    private final Map<Vehicle, List<String>> statisticsDistanceMap =new HashMap<>();
    private List<VehicleToRemove> removingVehicle=new ArrayList<>();
    private Timer colissionTimer;
    private TimerTask colissionTimerTask;
    public Animation()
    {
            startAnimation();
    }
    public void stopAnimation()
    {
        if(animationTimerTask!=null)
            animationTimerTask.cancel();
        if(animationTimer!=null)
            animationTimer.cancel();
        for (Vehicle vehicle : vehicles) {
            vehicle.stopBlink();
        }
        for (VehicleToRemove vehicleToRemove : removingVehicle) {
            vehicleToRemove.stopCountDown();
        }
        stopCheckColissions();
        //saveStatisticData();
    }
    public void stopCheckColissions()
    {
        
        colissionTimerTask.cancel();
        colissionTimer.cancel();
    }
    public void addVehicle(Vehicle vehicle)
    {
        Platform.runLater(() -> {
            vehicles.add(vehicle);
            vehicle.setId(vehID);
            vehID++;
            vehicle.changeValues(zoomRatio);
            Dipl_project.getUI().addVehicle(vehicle);
            //statisticsSpeedMap.put(vehicle, new ArrayList<>());
            //statisticsAngleMap.put(vehicle, new ArrayList<>());
            //statisticsDistanceMap.put(vehicle, new ArrayList<>());
        });
    }
    public void removeAllVehicles()
    {
        List<Vehicle> vehiclesToRemove=new ArrayList<>();
        vehiclesToRemove.addAll(vehiclesToRemove);
        for (Vehicle vehicle : vehiclesToRemove) {
            removeVehicle(vehicle);
        }
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
        
        animationTimer=new Timer();
        animationTimerTask = new TimerTask() {
            @Override
            public void run(){
                    tick();
            }
        };
        animationTimer.schedule(animationTimerTask, 20, 20);
        startCheckColissions();
        
    }
    public void startCheckColissions()
    {
        colissionTimer=new Timer();
        colissionTimerTask = new TimerTask() {
            @Override
            public void run(){
                    checkColission();
            }
        };
        colissionTimer.schedule(colissionTimerTask, 200, 200);
    }
    private void checkColission()
    {
        for (int i = 0; i < vehicles.size(); i++) {
            Vehicle vehicle1=vehicles.get(i);
            for (int j = 0; j < vehicles.size(); j++) {
                Vehicle vehicle2=vehicles.get(j);
                if(!vehicle1.equals(vehicle2))
                {
                    if(!vehicle1.isRemoving())
                    {
                        boolean colission = checkColl(vehicle1.getControlRectangle(), vehicle2.getControlRectangle());
                        if(colission)
                        {
                            vehicle1.crash();
                            if(!vehicle2.isRemoving())
                                vehicle2.crash();
                            break;
                        }
                    }

                }
            }
        }
    }
    public boolean checkColl(Rectangle r1, Rectangle r2)
    {
         if(Shape.intersect(r1, r2).getBoundsInLocal().getWidth()>10)
             return true;
         else
             return false;
    }
    private void tick()
    {
        Platform.runLater(() -> {
        for (Vehicle vehicle : vehicles) {
            vehicle.tick();
            //tickSaveData(vehicle);
            //addSpeedStat(vehicle,df.format(vehicle.getStatSpeed()));
            //addAngleStat(vehicle,df.format(vehicle.getDAngle()));
            //addDistanceStat(vehicle,df.format(vehicle.getStatDistance()));
        }
        });
    }
    public void cleanVehicles()
    {
        List<Vehicle> vehs=new ArrayList<>();
        vehs.addAll(vehicles);
        for (Vehicle vehicle : vehs) {
            vehicle.removeVehicle();
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
    public void moveVehiclesByPx(double x, double y)
    {
        
        for (Vehicle vehicle : vehicles) {
            vehicle.moveByPx(x, y);
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
            
            /*distanceStatistic = new FileWriter("stat_data_distance.txt");
            for(Map.Entry<Vehicle, List<String>> entry : statisticsDistanceMap.entrySet()) {
            List<String> values = entry.getValue();
                for (String value : values) {
                    distanceStatistic.write(value+";");
                }
                distanceStatistic.write("\n");
            }
            
            distanceStatistic.close();*/
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
    private  void addDistanceStat(Vehicle veh, String value)
    {
        statisticsDistanceMap.get(veh).add(value);
    }

    void addRemovingVehicle(VehicleToRemove vehicle) {
        removingVehicle.add(vehicle);
    }
}
