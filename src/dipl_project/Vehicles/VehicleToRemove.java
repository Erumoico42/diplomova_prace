/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dipl_project.Vehicles;

import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author Honza
 */
public class VehicleToRemove {
    private TimerTask timerTask;
    private Timer timer;
    private Vehicle vehicle;
    private int countOfCrash=10;
    public VehicleToRemove(Vehicle vehicle)
    {
        this.vehicle=vehicle;
        startCountDown();
    }
    public void startCountDown()
    {
        
        timer=new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run(){
               vehicle.showCrash(countOfCrash%2==0);
               countOfCrash--;
               if(countOfCrash==0)
               {
                   removeVehicle();
               }
            }
        };
        timer.schedule(timerTask, 500, 500);

    }
    private void removeVehicle()
    {
        stopCountDown();
        vehicle.showCrash(false);
        if(vehicle instanceof MyCar)
            ((MyCar)vehicle).newRoad();
        else
            vehicle.removeVehicle();
    }

    public void stopCountDown() {
        timer.cancel();
        timerTask.cancel();
    }
}