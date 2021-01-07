/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dipl_project.Roads.VehicleGenerating;

import dipl_project.Dipl_project;
import dipl_project.Roads.Connect;
import dipl_project.Roads.MyCurve;
import dipl_project.Roads.RoadSegment;
import dipl_project.Vehicles.Car;
import dipl_project.Vehicles.Vehicle;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;

/**
 *
 * @author Honza
 */
public class StartSegment {
    private Timer timer;
    private TimerTask timerTask;
    protected RoadSegment startRS;
    private int frequencyMinute;
    private long generateDelay;
    private Connect startConnect;
    private boolean frequencyChangedActual=false, runGenerator=false;
    private final MyCurve mc;
    public StartSegment(RoadSegment startRS, int frequencyMinute, Connect startConnect, MyCurve mc) {
        this.startRS = startRS;
        this.startConnect=startConnect;
        this.frequencyMinute = frequencyMinute;
        generateDelay=60000/frequencyMinute;
        if(frequencyMinute!=0){
            generateDelay=60000/frequencyMinute;
        }
        this.mc=mc;
        mc.setStartStreet(this);
        if(Dipl_project.getSc().simulationRun() && frequencyMinute!=0)
        {
            runGenerator=true;
            startGenerator();
        }
            
    }

    public Connect getStartConnect() {
        return startConnect;
    }
    
    public RoadSegment getStartRS() {
        return startRS;
    }

    public void setStartRS(RoadSegment startRS) {
        this.startRS = startRS;
    }

    public int getFrequencyMinute() {
        return frequencyMinute;
    }

    public MyCurve getMc() {
        return mc;
    }
    

    public void setFrequencyMinute(int frequencyMinute) {
        this.frequencyMinute = frequencyMinute;
        if(frequencyMinute!=0){
            generateDelay=60000/frequencyMinute;
            stopGenerator();
            startGenerator();
        }
        else
            stopGenerator();
        
    }
    public void startGenerator()
    {
        if(runGenerator)
        {
            timer=new Timer();
            timerTask = new TimerTask() {
                @Override
                public void run(){
                    if(isStreetEmpty())
                        newVehicle();
                }
            };
            timer.schedule(timerTask, 0, generateDelay);
        }
        
    }

    public boolean isRunGenerator() {
        return runGenerator;
    }

    public void setRunGenerator(boolean runGenerator) {
        this.runGenerator = runGenerator;
    }
    
    private boolean isStreetEmpty()
    {
        boolean empty=(startRS.getVehicle()==null);
        for (MyCurve startCurve : startConnect.getStartCurves()) {
            RoadSegment rsFirst= startCurve.getFirstCurveSegment();
            if(rsFirst!=null)
            {
                if(rsFirst.getVehicle()!=null)
                {
                    empty=false;
                    break;
                }
                for (RoadSegment roadSegment : rsFirst.getRsSameWay()) {
                    if(roadSegment.getVehicle()!=null)
                    {
                        empty=false;
                        break;
                    }
                }
                for (RoadSegment rsSecond : rsFirst.getRsNext()) {
                    if(rsSecond.getVehicle()!=null)
                    {
                        empty=false;
                        break;
                    }
                    for (RoadSegment roadSegment : rsSecond.getRsSameWay()) {
                        if(roadSegment.getVehicle()!=null)
                        {
                            empty=false;
                            break;
                        }
                    }
                }
            }
        }
        return empty;
    }
    public void newVehicle()
    {
    }
    public void stopGenerator()
    {
        if(timer!=null)
            timer.cancel();
        if(timerTask!=null)
            timerTask.cancel();
    }

    public boolean isFrequencyChangedActual() {
        return frequencyChangedActual;
    }

    public void setFrequencyChangedActual(boolean frequencyChangedActual) {
        this.frequencyChangedActual = frequencyChangedActual;
    }
    
}
