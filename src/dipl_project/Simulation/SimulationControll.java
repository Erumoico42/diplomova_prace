/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dipl_project.Simulation;

import dipl_project.Dipl_project;
import dipl_project.UI.UIControll;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;

/**
 *
 * @author Honza
 */
public class SimulationControll {

    private int generateSize=6000;
    private boolean deleyChangedCar=false;
    private TimerTask timerTask;
    private Timer timer;
    private UIControll ui;
    public SimulationControll()
    {
        ui=Dipl_project.getUI();
    }
    public void stopAnimation()
    {
        timerTask.cancel();
        timer.cancel();
    }
    public void startSimulation()
    {
        timer=new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run(){
                Platform.runLater(() -> {
                    tick();
                    if(deleyChangedCar)
                    {
                        deleyChangedCar=false;
                        timer.cancel();
                        startSimulation(); 
                    }
                });
            }
        };
        timer.schedule(timerTask, generateSize, generateSize);
    }
    private void tick()
    {
        ui.newVehicle();
    }
    public void changeGenerateSize(int size)
    {
        generateSize=60000/size;
        deleyChangedCar=true;
    }
}
