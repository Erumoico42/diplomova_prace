/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dipl_project.Simulation;

import dipl_project.TrafficLights.TrafficLight;
import dipl_project.Dipl_project;
import dipl_project.Roads.RoadSegment;
import dipl_project.Roads.VehicleGenerating.StartSegment;
import dipl_project.UI.UIControlls.DrawControll;
import dipl_project.UI.UIControlls.EditationControll;
import dipl_project.UI.UIControlls.UIControll;
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
    private boolean simulationRun=false;
    private UIControll ui;
    private DrawControll dc;
    private int generateTramSize=6000;
    private MyCar mycar;
    private int mycarCount=0;
    private Timer changeSpeedTimer;
    private TimerTask changeSpeedTimerTask;
    private boolean changeSpeedLoop, autoGenerMyCar=false;
    private double myCarSpeedChange;
    private int frequencyCarGeneration=20, frequencyTramGeneration=5;
    public SimulationControll()
    {
        ui=Dipl_project.getUI();
        dc=Dipl_project.getDC();
    }
    public void stopVehicleGenerator()
    {
        
        for (StartSegment starCarSegment : ui.getStarCarSegments()) {
            starCarSegment.setRunGenerator(false);
            starCarSegment.stopGenerator();
            
        }
        for (StartSegment starTramSegment : ui.getStarTramSegments()) {
            starTramSegment.setRunGenerator(false);
            starTramSegment.stopGenerator();
        }      
        simulationRun=false;
    }
    public void startVehicleGenerator()
    {
        for (StartSegment starCarSegment : ui.getStarCarSegments()) {
            
            starCarSegment.setRunGenerator(true);
            starCarSegment.startGenerator();
        }
        for (StartSegment starTramSegment : ui.getStarTramSegments()) {
            
            starTramSegment.setRunGenerator(true);
            starTramSegment.startGenerator();
        }
        simulationRun=true;

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
        {
            mycar.cancelDrive();
            mycar=null;
        }
    }
    public void newMyCar(RoadSegment startRS)
    {
        autoGenerMyCar=true;
        if(mycar==null){
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
                        myCarSpeedChange=mycar.getMaxForce()*4;
                    if(event.getCode()==KeyCode.DOWN)
                        myCarSpeedChange=-mycar.getMaxForce()*4;
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

    public void changeGenerateCarSize(int intValue) {
        frequencyCarGeneration=intValue;
        for (StartSegment starCarSegment : ui.getStarCarSegments()) {
            if(!starCarSegment.isFrequencyChangedActual())
            {
                starCarSegment.setFrequencyMinute(intValue);
            }
        }
    }

    public void changeGenerateTramSize(int intValue) {
        frequencyTramGeneration=intValue;
        for (StartSegment starTramSegment : ui.getStarTramSegments()) {
            if(!starTramSegment.isFrequencyChangedActual())
            {
                starTramSegment.setFrequencyMinute(intValue);
            }
        }
    }

    public int getFrequencyCarGeneration() {
        return frequencyCarGeneration;
    }

    public int getFrequencyTramGeneration() {
        return frequencyTramGeneration;
    }

    public boolean simulationRun() {
        return simulationRun;
    }
}

