/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dipl_project.Vehicles;

import java.util.Timer;
import java.util.TimerTask;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.util.Duration;

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
        ScaleTransition st=new ScaleTransition(Duration.millis(1000), vehicle.getIvMaskBreaks());
        
        st.setByX(0.5);
        st.setByY(0.5);
        st.setCycleCount(5);
        st.setAutoReverse(true);
        st.play();
        FadeTransition ft = new FadeTransition(Duration.millis(1000), vehicle.getIvMaskBreaks());
        ft.setFromValue(1.0);
        ft.setToValue(0.3);
        ft.setCycleCount(5);
        ft.setAutoReverse(true);
        ft.play();
        
        vehicle.showCrash(true);
        
        timer=new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run(){
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
