/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dipl_project.Simulation;

import TrafficLights.TrafficLight;
import dipl_project.Dipl_project;
import dipl_project.Roads.RoadSegment;
import dipl_project.UI.DrawControll;
import dipl_project.UI.UIControll;
import dipl_project.Vehicles.MyCar;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

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
    private MyCar mycar;
    private int mycarCount=0;
    private Timer changeSpeedTimer;
    private TimerTask changeSpeedTimerTask;
    private boolean changeSpeedLoop, autoGenerMyCar=false;
    private double myCarSpeedChange;
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

    public MyCar getMycar() {
        return mycar;
    }

    public void setMycar(MyCar mycar) {
        this.mycar = mycar;
        cancelChangeSpeed();
        
    }
    public void removeMyCar()
    {
        autoGenerMyCar=false;
        if(mycar!=null)
        Dipl_project.getAnim().removeVehicle(mycar);
    }
    public void newMyCar(RoadSegment startRS)
    {
        autoGenerMyCar=true;
        if(getMycar()==null){
            if(autoGenerMyCar)
            {
                setMycar(new MyCar(startRS));
                mycarCount++;
            }
            
        }
    }
    private void changeSpeedLoop()
    {
        changeSpeedTimer = new Timer();
        changeSpeedTimerTask=new TimerTask() {
            public void run() {
                Platform.runLater(() -> {
                    mycar.updateSpeed(myCarSpeedChange);
                    
                });
            }
        };
        changeSpeedTimer.schedule(changeSpeedTimerTask, 0, 50);   
    }
    public void stopChangeMyCarSpeed(KeyEvent event)
    {
        if(mycar!=null)
        if(event.getCode()==KeyCode.UP || event.getCode()==KeyCode.DOWN)
        {
            changeSpeedLoop=false;
            cancelChangeSpeed();
        }
        
    }
    
    public void changeMyCarSpeed(KeyEvent event)
    {
        if(mycar!=null){
            if(!changeSpeedLoop){
                if(event.getCode()==KeyCode.UP || event.getCode()==KeyCode.DOWN){
                    changeSpeedLoop=true;

                    if(event.getCode()==KeyCode.UP)
                        myCarSpeedChange=mycar.getMaxForce();
                    if(event.getCode()==KeyCode.DOWN)
                        myCarSpeedChange=-mycar.getMaxForce()*2;
                    changeSpeedLoop();
                }
            }
        }
    }
    public void cancelChangeSpeed()
    {
        if(changeSpeedTimer!=null)
            changeSpeedTimer.cancel();
        if(changeSpeedTimerTask!=null)
            changeSpeedTimerTask.cancel();
    }
}

