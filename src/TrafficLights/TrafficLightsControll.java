/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TrafficLights;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author Honza
 */
public class TrafficLightsControll {
    private int timeSeconds=0, maxTime;
    private Timer timer;
    private TimerTask timerTask;
    private List<TrafficLightsGroup> tlsGroups=new ArrayList<>();
    
    public void resetTimer()
    {
        timeSeconds=0;
    }
    public void startTrafficLights()
    {
        timer=new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run(){
               activateTLs();
               timeSeconds++;
               if(timeSeconds>maxTime)
                   timeSeconds=0;
            }
        };
        timer.schedule(timerTask, 1000, 1000);
    }
    public void addTLGroup(TrafficLightsGroup tlg)
    {
        tlsGroups.add(tlg);
        if(tlg.getTime()>maxTime)
            maxTime=tlg.getTime();
    }

    public List<TrafficLightsGroup> getTlsGroups() {
        return tlsGroups;
    }
    
    public void stopTrafficLights()
    {
        if(timerTask!=null)
            timerTask.cancel();
        if(timer!=null)
            timer.cancel();
    }
    private void activateTLs()
    {
        for (TrafficLightsGroup trafficLightGroup : tlsGroups) {
            if(timeSeconds==trafficLightGroup.getTime())
            {
                for (TrafficLightSwitch trafficLightSwitch : trafficLightGroup.getTrafficLightSwitchList()) {
                    trafficLightSwitch.activate();
                }
            }
        }
    }
}
