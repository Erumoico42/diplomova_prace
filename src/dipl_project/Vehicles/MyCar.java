/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dipl_project.Vehicles;

import dipl_project.Dipl_project;
import dipl_project.Roads.CheckPoint;
import dipl_project.Roads.RoadSegment;
import dipl_project.Simulation.SimulationControll;
import dipl_project.TrafficLights.TrafficLight;
import dipl_project.UI.GUI.TestMenu.UITestMenu;
import dipl_project.UI.UIControlls.EditationControll;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author Honza
 */
public class MyCar extends Vehicle{

    private long runTime;
    private int runCount;
    private long fastRun=Long.MAX_VALUE;
    private long slowRun=0;
    private int crashCount;
    private int redCount;
    private int failCount;
    private boolean removing=false;
    private List<TrafficLight> failedTLs=new ArrayList<>();
    private UITestMenu uiTest=Dipl_project.getUI().getUiTestMenu();
    public MyCar(RoadSegment startSegment) {
        
        super(startSegment);
        runCount++;
        super.setForce(0);
        super.setSpeed(0);
        setMyCarImage();
        setBreaksLayout(-5);
        super.setVehicleLenght(0);
        
    }
    private void setMyCarImage()
    {
        String carName="my-car";
        Image carDef= new Image(Dipl_project.class.getResource("Resources/vehicles/"+carName+".png").toString());
        Image carLeft= new Image(Dipl_project.class.getResource("Resources/vehicles/blinker-l.png").toString());
        Image carRight= new Image(Dipl_project.class.getResource("Resources/vehicles/blinker-r.png").toString());
        Image carBreak= new Image(Dipl_project.class.getResource("Resources/vehicles/breaks.png").toString());
        initVehicleImage(carDef, carLeft, carRight, carBreak, 50, 50, 40, 18);

    }

    @Override
    public void tick() {
        super.tick(); 
        runTime++;
        uiTest.setRunTime(getTimeFromMiliseconds(runTime));
        checkLightsRun();
    }
    @Override
    public void crash()
    {
        super.crash();
        crashCount++;
        uiTest.setCrashCount(String.valueOf(crashCount));
    }
    public void cancelDrive()
    {
        super.removeVehicle();
        Dipl_project.getSc().setMycar(null);
    }
    @Override
    public void removeVehicle() {
        
        newRoad();
    }
    public void newRoad()
    {
        setRemoving(false);
        runCount++;
        if(runTime>slowRun)
            slowRun=runTime;
        if(runTime<fastRun)
            fastRun=runTime;
        runTime=0;

        uiTest.setRunCount(String.valueOf(runCount));
        uiTest.setFastRun(getTimeFromMiliseconds(fastRun));
        uiTest.setSlowRun(getTimeFromMiliseconds(slowRun));
        uiTest.setRunTime(getTimeFromMiliseconds(runTime));
        uiTest.setRedCount(String.valueOf(redCount));

        initNewRoad();
        
        
        setSpeed(0);
        setForce(0);
        
        setTime(0);
        move(0);
        setTime(0.01);
        move(0.01);
        tick();
        failedTLs.clear();
    }
    public void initNewRoad()
    {

        generateStreet(Dipl_project.getUI().getStartNoCare());
        nextSegment();
        setPoints();
    }
    private String getTimeFromMiliseconds(long time)
    {
        time*=20;
        long second = TimeUnit.MILLISECONDS.toSeconds(time);
        long minute = TimeUnit.MILLISECONDS.toMinutes(time);
        return String.format("%02d:%02d", minute, second);
    }
    private void checkLightsRun()
    {
        for (TrafficLight tl : getActualSegment().getTrafficLights()) {
            if(!failedTLs.contains(tl))
            if(!(tl.getStatus()==0 || (tl.getStatus()==1 && getDistanceCheck()<1)))
            {
                
                redCount++;
                uiTest.setRedCount(String.valueOf(redCount));
                failedTLs.add(tl);
            }
               
        }
    }
}
