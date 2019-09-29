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
    public Animation()
    {
        animationTimer();
    }
    public void stopAnimation()
    {
        timerTask.cancel();
        timer.cancel();
    }
    public void addVehicle(Vehicle vehicle)
    {
        Platform.runLater(() -> {
            vehicles.add(vehicle);
            Dipl_project.getUI().addComponent(vehicle.getIV());
        });
    }
    public void removeVehicle(Vehicle vehicle)
    {
        Platform.runLater(() -> {
            vehicles.remove(vehicle);
            Dipl_project.getUI().removeComponents(vehicle.getIV());
        });
    }
    private void animationTimer()
    {
        timer=new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run(){
                Platform.runLater(() -> {
                    tick();
                });
            }
        };
        timer.schedule(timerTask, 20, 20);
    }
    private void tick()
    {
        for (int i = 0; i < vehicles.size(); i++) {
            vehicles.get(i).tick();
        }
    }
}
