/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dipl_project.Simulation;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Honza
 */
public class Player {

    private String name="Player";
    private int crashCount;
    private int runCount;
    private int redCount;
    private double avgTime;
    private String mark;
    private String date;
    private double slowRun;
    private double fastRun;
    public Player(String name)
    {
        this.name=name;
        newDate();
    }
    public Player(String name, int crashCount, int runCount, int redCount, double avgTime, double slowRun, double fastRun, String mark, String date) {
        this.name = name;
        this.crashCount = crashCount;
        this.runCount = runCount;
        this.redCount = redCount;
        this.avgTime = avgTime;
        this.mark=mark;
        this.slowRun=slowRun;
        this.fastRun=fastRun;
        this.date=date;
        
    }

    public void setCrashCount(int crashCount) {
        this.crashCount = crashCount;
        calcMark();
    }

    public void setRunCount(int runCount) {
        this.runCount = runCount;
        calcMark();
    }

    public void setRedCount(int redCount) {
        this.redCount = redCount;
        calcMark();
    }

    public void setSlowRun(double slowRun) {
        this.slowRun = slowRun;
        calcMark();
    }

    public void setFastRun(double fastRun) {
        this.fastRun = fastRun;
        calcMark();
    }
    
    private void newDate()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd/MM/yyyy");  
        Date newDate = new Date();  
        date=sdf.format(newDate);
    }
    public String getDate() {
        return date;
    }

    public String getName() {
        return name;
    }

    public int getCrashCount() {
        return crashCount;
    }

    public int getRunCount() {
        return runCount;
    }

    public int getRedCount() {
        return redCount;
    }

    public double getAvgTime() {
        return avgTime;
    }

    public String getMark() {
        return mark;
    }

    public double getSlowRun() {
        return slowRun;
    }

    public double getFastRun() {
        return fastRun;
    }
    public void calcMark()
    {
        double aMinMax=fastRun-slowRun;
        double aAvgMin=avgTime-fastRun;
        double perc=100/aMinMax*aAvgMin;
        if(aAvgMin==0)
            perc=0;
        perc+=(crashCount/2+redCount/2)*5;
        if(perc<20)
            mark= "A";
        else if(perc<40)
            mark= "B";
        else if(perc<60)
            mark= "C";
        else if(perc<80)
            mark= "D";
        else if(perc<90)
            mark= "E";
        else
            mark= "F";
    }
}
