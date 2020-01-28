/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dipl_project.Simulation;

import TrafficLights.TrafficLight;
import dipl_project.Dipl_project;
import dipl_project.UI.DrawControll;
import dipl_project.UI.UIControll;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;

/**
 *
 * @author Honza
 */
public class SimulationControll {

    private int generateCarSize=6000;
    private boolean deleyChangedCar=false;
    private TimerTask timerTaskCars;
    private Timer timerCars;
    private UIControll ui;
    private DrawControll dc;
    private int generateTramSize=6000;
    private boolean deleyChangedTram;
    private Timer timerTrams;
    private TimerTask timerTaskTrams;
    public SimulationControll()
    {
        ui=Dipl_project.getUI();
        dc=Dipl_project.getDC();
    }
    public void stopSimulation()
    {
        if(timerTaskCars!=null)
        timerTaskCars.cancel();
        if(timerCars!=null)
        timerCars.cancel();
        
        if(timerTaskTrams!=null)
            timerTaskTrams.cancel();
        if(timerTrams!=null)
            timerTrams.cancel();
        
    }
    public void stopTrafficLights()
    {
        for (TrafficLight trafficLight : dc.getTrafficLights()) {
            trafficLight.setRun(false);
        }
    }
    public void startTrafficLights()
    {
        for (TrafficLight trafficLight : dc.getTrafficLights()) {
            trafficLight.setRun(true);
        }
    }
    public void startSimulationCar()
    {
        
        timerCars=new Timer();
        timerTaskCars = new TimerTask() {
            @Override
            public void run(){
                Platform.runLater(() -> {
                    tickCar();
                    if(deleyChangedCar)
                    {
                        deleyChangedCar=false;
                        timerCars.cancel();
                        timerTaskCars.cancel();
                        startSimulationCar(); 
                    }
                });
            }
        };
        timerCars.schedule(timerTaskCars, generateCarSize, generateCarSize);

    }
    public void startSimulationTram()
    {
        timerTrams=new Timer();
        timerTaskTrams = new TimerTask() {
            @Override
            public void run(){
                Platform.runLater(() -> {
                    tickTram();
                    if(deleyChangedTram)
                    {
                        deleyChangedTram=false;
                        timerTrams.cancel();
                        timerTaskTrams.cancel();
                        startSimulationTram(); 
                    }
                });
            }
        };
        timerTrams.schedule(timerTaskTrams, generateTramSize, generateTramSize);
    }
    private void tickCar()
    {
        ui.newCar();
    }
    private void tickTram()
    {
        ui.newTram();
    }
    public void changeGenerateCarSize(int size)
    {

        generateCarSize=60000/size;
        deleyChangedCar=true;
    }
    public void changeGenerateTramSize(int size)
    {
        generateTramSize=60000/size;
        deleyChangedTram=true;
    }
}

