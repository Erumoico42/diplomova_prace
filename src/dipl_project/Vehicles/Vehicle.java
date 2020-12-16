/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dipl_project.Vehicles;

import dipl_project.TrafficLights.TrafficLight;
import dipl_project.Dipl_project;
import dipl_project.Roads.CheckPoint;
import dipl_project.Roads.MyMath;
import dipl_project.Roads.MyPoint;
import dipl_project.Roads.RoadSegment;
import dipl_project.Roads.WatchPoint;
import java.awt.Point;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

/**
 *
 * @author Honza
 */
public class Vehicle {
    
    private MyPoint lastPosition, newPosition, lastPositionStep;
    private double lastForce, lastDistance;
    private List<RoadSegment> road=new ArrayList<>();
    private RoadSegment actualSegment;
    private Point p0, p1, p2, p3;
    private double x0, y0, x1, y1, x2, y2, x3, y3, xLast, yLast, angle, actSegmentLenght, breaksLayout;
    private Animation animation;
    private Rectangle controlRectangle;
    private double controlWidth=34, controlHeight=14, vehWidth=40, vehHeight=40;
    private boolean paused=false;
    private final ImageView iv, ivMaskBlinker, ivMaskBreaks;
    private Image actualCar, carBlinkerLeft, carBlinkerRight, carBreak, defCar;
    private double maxSpeed=(0.07+(Math.random()*0.03)-0.025), maxForce=0.0006;
    private double speed=maxSpeed/3, force=0.0003, time=0, breakRatio=1, vehicleLenght;
    private boolean watch=false;
    private int id;
    private int watchCount=0, freeCount=0;
    private Timer blinkerTimer;
    private TimerTask blinkerTimerTask;
    private int breakCountDown=0;
    private boolean blink=false, blinkerOn=false, changedForce=false;
    private double lastAngle=0;
    private double newAngle;
    public Vehicle(RoadSegment startSegment)
    {
        animation=Dipl_project.getAnim();
        iv=new ImageView();
        iv.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(watch)
                    play();
                else
                    pause();
            }
        });
        ivMaskBlinker=new ImageView();
        ivMaskBreaks=new ImageView();
        /*iv.setFitWidth(40);
        iv.setFitHeight(40);*/
        actualSegment=startSegment;
        
        generateStreet(startSegment);
        setPoints();
        /*
        controlRectangle=new Rectangle(width,height, Color.TRANSPARENT);
        controlRectangle.setX(x0-width/2);
        controlRectangle.setY(y0-height/2);*/
        
       
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLastForce() {
        return lastForce;
    }

    public void setLastForce(double lastForce) {
        this.lastForce = lastForce;
    }
    public double getDAngle()
    {
        double ret=Math.abs(lastAngle-newAngle);
        if(ret>180)
            ret=Math.abs(ret-360);
        ret%=360;
        return ret;
    }
    public void initVehicleImage(Image carDef, Image carBlinkerLeft,Image carBlinkerRight, Image carBreak, double width, double height, double controlWidth, double controlHeight)
    {
        iv.setImage(carDef);
        iv.setFitWidth(width);
        iv.setFitHeight(height);
        ivMaskBlinker.setFitWidth(width);
        ivMaskBlinker.setFitHeight(height);
        ivMaskBreaks.setFitWidth(width);
        ivMaskBreaks.setFitHeight(height);
        this.actualCar=carDef;
        defCar=carDef;
        this.carBlinkerLeft=carBlinkerLeft;
        this.carBlinkerRight=carBlinkerRight;
        this.carBreak=carBreak;
        this.controlWidth=controlWidth;
        this.controlHeight=controlHeight;
        vehWidth=width;
        vehHeight=height;
        //controlRectangle=new Rectangle(controlWidth,controlHeight, Color.TRANSPARENT);
        
        
        //controlRectangle.setX(lastPosition.getX());
        //controlRectangle.setY(lastPosition.getY());
         move(0);
         time=0.01;
         
        move(time);
        lastPosition=new MyPoint((xLast-vehWidth/2), (yLast-vehHeight/2));
        lastPositionStep=new MyPoint(lastPosition.getX(), lastPosition.getY());
        animation.addVehicle(this);
        actSegmentLenght=actualSegment.getSegmentLenght();
    }
    public void setBreaksLayout(double breaksLayout)
    {
        this.breaksLayout=breaksLayout;
    }
    private void generateStreet(RoadSegment start)
    {
        RoadSegment newSegment=start;
        RoadSegment lastSplit=newSegment;
        int count=0;
        List<RoadSegment> toAdd=new ArrayList<>();
        boolean stop=false;
        while(!stop && !newSegment.getRsNext().isEmpty())
        {
            
            int size=newSegment.getRsNext().size();
            if(size>1){
                road.addAll(toAdd);
                lastSplit=newSegment;
                toAdd.clear();
                count=0;
            }
            newSegment=newRandomSegment(newSegment);
            if(!road.contains(newSegment) && !toAdd.contains(newSegment)){
                toAdd.add(newSegment);
            }
            else{
                toAdd.clear();
                count++;
                newSegment=lastSplit;
            }
            if(count>10)
                stop=true;
            
        }
        road.addAll(toAdd);
    }
    private RoadSegment newRandomSegment(RoadSegment rs)
    {
        return rs.getRsNext().get((int)(Math.random()*(rs.getRsNext().size())));
    }

    public ImageView getIV() {
        return iv;
    }

    public ImageView getIvMaskBlinker() {
        return ivMaskBlinker;
    }

    public ImageView getIvMaskBreaks() {
        return ivMaskBreaks;
    }
    
    public void nextSegment()
    {
        actualSegment.setVehicle(null);
        if(!road.isEmpty())
        {
            actualSegment=road.get(0);
            actSegmentLenght=actualSegment.getSegmentLenght();
            stopBlinker();
            
            
            setPoints();
            actualSegment.setVehicle(this); 
            road.remove(actualSegment);
        }
        else
        {
            removeCar();  
        }
    }

    public double getVehicleLenght() {
        return vehicleLenght;
    }

    public void setVehicleLenght(double vehicleLenght) {
        this.vehicleLenght = vehicleLenght;
    }
    
    public void changeValues(double zoomRatio)
    {
        vehWidth*=zoomRatio;
        vehHeight*=zoomRatio;
        iv.setFitWidth(vehWidth);
        iv.setFitHeight(vehHeight);
        ivMaskBlinker.setFitWidth(vehWidth);
        ivMaskBlinker.setFitHeight(vehHeight);
        ivMaskBreaks.setFitWidth(vehWidth);
        ivMaskBreaks.setFitHeight(vehHeight);
        controlWidth*=zoomRatio;
        controlHeight*=zoomRatio;
        //controlRectangle.setArcWidth(controlWidth);
        //controlRectangle.setArcHeight(controlHeight);
    }
    public void removeCar()
    {
        animation.removeVehicle(this);
    }
    public Rectangle getControlRectangle() {
        return controlRectangle;
    }
    
    private void setPoints()
    {
        p0=actualSegment.getP0();
        p3=actualSegment.getP3();
        p1=actualSegment.getP1();
        p2=actualSegment.getP2();
        x0=p0.getX();
        y0=p0.getY();
        x1=3*(p1.getX()-x0);
        y1=3*(p1.getY()-y0);
        x2=3*(x0-2*p1.getX()+p2.getX());
        y2=3*(y0-2*p1.getY()+p2.getY());
        x3=3*(p1.getX()-p2.getX())+p3.getX()-x0;
        y3=3*(p1.getY()-p2.getY())+p3.getY()-y0;
        
        xLast=x0;
        yLast=y0;
    }
    private void move(double t)
    {
        double t2=t*t;
        double t3=t2*t;
        double x = (x0+(t*x1)+(t2*x2)+(t3*x3));
        double y = (y0+(t*y1)+(t2*y2)+(t3*y3)); 
        angle=Math.toDegrees(MyMath.angle(x, y,xLast, yLast));
        if(angle!=0){
            lastAngle=newAngle;
            newAngle=angle;
            iv.setRotate(angle);
            ivMaskBlinker.setRotate(angle);
            ivMaskBreaks.setRotate(angle);
            //controlRectangle.setRotate(angle);
        }
        xLast=x;
        yLast=y;
        newPosition=new MyPoint((x-vehWidth/2), (y-vehHeight/2));
        iv.setX(newPosition.getX());
        iv.setY(newPosition.getY()); 
        ivMaskBlinker.setX(breaksLayout+newPosition.getX());
        ivMaskBlinker.setY(newPosition.getY()); 
        ivMaskBreaks.setX(breaksLayout+newPosition.getX());
        ivMaskBreaks.setY(newPosition.getY()); 
        //controlRectangle.setX(x-controlWidth/2);
        //controlRectangle.setY(y-controlHeight/2);

        
    }

    
    public void move()
    {
        setPoints();
        move(time);
    }
    public void tick()
    {
        double segmentLenghtKoef=Dipl_project.getRC().getSegLenght()/actSegmentLenght;
        double newSpeed=speed*segmentLenghtKoef;
        double newMaxSpeed=maxSpeed*segmentLenghtKoef;
        time+=newSpeed;     
        if(time>1)
            time=1;
        
        move(time);
        updateSpeed(force);
        
        if(newSpeed>newMaxSpeed){
           force=0;
           speed=newMaxSpeed;
        }
            
        if(time>=1){
            time-=1;
            nextSegment();
        }
        findBlinker();
    }

    public double getMaxForce() {
        return maxForce;
    }
    
    public void updateSpeed(double force) {
        
        double newSpeed=this.speed+force;
            if(newSpeed < speed)
            {
                breakCountDown=10;
                changedForce=!defCar.equals(carBreak);
                defCar=carBreak;
                setBreaks(carBreak);
            }
            else
            {
                breakCountDown--;
                if(breakCountDown<0)
                {
                    changedForce=!defCar.equals(actualCar);
                    setBreaks(null);
                    
                }
            }

            if(changedForce)
            {
                changedForce=false;
            }
        
        this.speed=newSpeed;
        if(newSpeed<0)
            this.speed=0;
    }
    private void pause()
    {
        paused=true;
        watch=true;
        setForce(-maxForce);
    }
    private void play()
    {
       watch=false;
        paused=false;
        setForce(maxForce/2);
        force=maxForce/2;
    }

    public boolean isPaused() {
        return paused;
    }
    
    public double getTime()
    {
        return time;
    }
    public double getSpeed()
    {
        return speed;
    }
    public double getStatSpeed()
    {
        return speed/actSegmentLenght;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }
    
    public void colisionDetect()
    {
        
        if(!paused){
           boolean carFoundStreet=findNextCar();
            if(!carFoundStreet)
            {
                setForce(maxForce);
            } 
        }
        
        
    }

    private boolean findCarCross(RoadSegment us, int nextDist, double actDist, int minDist)
    {
        
        boolean carFound=false;
        Vehicle nextVeh=us.getVehicle();
        for (RoadSegment rsNext : us.getRsLast()) {
            if(!carFound)
            {
                if(nextVeh==null)
                    nextVeh=rsNext.getVehicle();
                if(nextVeh!=null && nextVeh!=this)
                {
                    double dActVeh=actDist-getTime();
                    if(nextDist<minDist)
                    {
                        fuzzySpeed(dActVeh, getSpeed());
                    }
                    else if(nextDist>minDist)
                    {
                        double dNextVeh=nextDist-nextVeh.getTime()-1;
                        boolean stop=fuzzyCrossStop(dNextVeh, nextVeh.getSpeed(), dActVeh, getSpeed());
                        if(stop)
                            fuzzySpeed(dActVeh-1, getSpeed());
                    }
                    carFound=true;
                }
                else if(nextDist<10+minDist && rsNext!=actualSegment)
                {
                    carFound=findCarCross(rsNext, nextDist+1, actDist, minDist);
                }
                    
            }
        }
        return carFound;
    }
    private void fuzzySpeed(double distance, double speed)
    {
        double sp=Dipl_project.getRcFollow().calculateByValues(Math.round(speed*10000)/100, distance+1);
        setForce(sp/1000);
    }
    private boolean fuzzyCrossStop(double distanceB, double speedB, double distanceA, double speedA)
    {
        double sp=Dipl_project.getRcCross().calculateByValues(distanceB+1, Math.round(speedB*10000)/100,  distanceA, Math.round(speedA*10000)/100);
        if(sp<1)
            return true;
        else
            return false;
    }
    private void findBlinker()
    {
        for (int actDist = 0; actDist < 10; actDist++) {
            if(road.size()>actDist){
                RoadSegment rsNext=road.get(actDist);
                if(!blinkerOn && actDist-time<7)
                {
                    
                    if(rsNext.isBlinkerLeft())
                    {
                        runBlinker(carBlinkerLeft);
                    }
                        
                    if(rsNext.isBlinkerRight())
                        runBlinker(carBlinkerRight);
                    
                }
            }
        }
    }
    private boolean findNextCar()
    {
        boolean carFound=false;
        for (int actDist = 0; actDist < 10; actDist++) {
            if(road.size()>actDist){
                
                RoadSegment rsNext=road.get(actDist);
                
                List<RoadSegment> rssToCheck=new ArrayList<>();
                double distMinus=0;
                double speedMinus=1;
                rssToCheck.add(rsNext);
                List<RoadSegment> rsSameWay =rsNext.getRsSameWay();
                if(!rsNext.isRun())
                    rssToCheck.addAll(rsSameWay);
                for (RoadSegment rsCheck : rssToCheck) {
                    
                    if(!carFound)
                    {
                        if(rsCheck.getVehicle()!=null && rsCheck.getVehicle()!=this)
                        {   
                            double speedNextCar=rsCheck.getVehicle().getSpeed()*speedMinus;
                            double tNextCar=rsCheck.getVehicle().getTime();
                            double dist=actDist+tNextCar-getTime()+distMinus;
                            dist-=rsCheck.getVehicle().getVehicleLenght();
                            
                            if(dist<0.5 || distMinus==-1)
                                setForce(-maxForce);
                            fuzzySpeed(dist, getSpeed()-speedNextCar);
                                carFound=true;
                            
                            
                        }else if(!rsSameWay.contains(rsCheck))
                        {
                            boolean trafficLightFound=false;
                            for (TrafficLight trafficLight : rsCheck.getTrafficLights()) {
                                
                                int status=trafficLight.getStatus();
                                //int tlTime=trafficLight.getTimeCountDown();
                                //int tlMaxTime=trafficLight.getMaxTime();
                                
                                trafficLightFound=true;
                                if((status==1 && !trafficLight.isOrangeSwitching() && actDist-vehicleLenght-time<2) || status==2 || status==3)
                                {
                                    carFound=true;
                                    fuzzySpeed(actDist-vehicleLenght-time, speed);
                                }
                                if(status==0)
                                {
                                    setForce(maxForce);
                                }
                                    
                                  
                            }
                            if(trafficLightFound)
                                actDist=11; 
                            if(!trafficLightFound)
                            {
                                boolean watchPointFound=false;
                                List<WatchPoint> watchPoints=rsCheck.getWatchPoints();
                                for (WatchPoint watchPoint : watchPoints) {
                                    
                                    if(!watch)
                                    {
                                        watchCount=0;
                                        if(watchPoint.getRs().getVehicle()!=null)
                                            watchCount++;
                                        findWatchCar(watchPoint.getRs(), 0, watchPoint.getDistance());
                                        
                                        freeCount=0;
                                        if(watchPoint.getRs().getVehicle()==null)
                                            findWatchFree(watchPoint.getRs(),0, watchPoint.getDistance());
                                        if(freeCount<=watchCount)
                                        {
                                            watchPointFound=true;
                                            fuzzySpeed(actDist-time, speed);
                                        }
                                            
                                    }
                                        
                                }
                                carFound=watchPointFound;
                                if(!watchPointFound)
                                {
                                    List<CheckPoint> cps=new ArrayList<>();
                                    cps.addAll(rsCheck.getCheckPoints());
                                    cps.addAll(rsCheck.getSecondaryCheckPoints());
                                    if(!cps.isEmpty())
                                    {

                                        if(actDist>2 && actDist<6)
                                        {
                                            fuzzySpeed(actDist-getTime()+1, getSpeed());
                                        }  
                                        else
                                        {

                                            for (CheckPoint cp : cps) {
                                                if(cp.isEnabled())
                                                for (RoadSegment uN : cp.getRs().getRsNext()) {
                                                    carFound=findCarCross(uN, 1, actDist+1, cp.getDistance());                        
                                                }
                                            }
                                        }
                                    }
                                }
                                
                            }
                            
                        }
                    }
                    distMinus=-1;
                    speedMinus*=0.8;
                }
                
            }
            else
                break;
            
        }
        return carFound;
    }
    private void findWatchCar(RoadSegment rs, int actDist, int maxDist)
    {
            for (RoadSegment rsLast : rs.getRsLast()) {
                if(road.contains(rsLast))
                {
                    if(rsLast.equals(actualSegment))
                    {
                        Vehicle veh=rsLast.getVehicle();
                        if(veh!=null )
                            watchCount++;
                        if(actDist<maxDist)
                        findWatchCar(rsLast, actDist+1, maxDist);
                    }
                    else
                        break;
                    
                }
                
            }
    }
    private void findWatchFree(RoadSegment rs, int actDist, int maxDist)
    {
            for (RoadSegment rsNext : rs.getRsNext()) {
                if(road.contains(rsNext))
                {
                    Vehicle veh=rsNext.getVehicle();
                    if(veh==null)
                        freeCount++;
                    else if(veh.getSpeed()/veh.getMaxSpeed()<0.6)
                        break;
                    if(actDist<=maxDist)
                    findWatchFree(rsNext, actDist+1, maxDist);
                }
            }
    }
    public void setForce(double newForce) {
        lastForce=force;
        newForce*=breakRatio;
        if(newForce>maxForce)
            newForce=maxForce;
        this.force = newForce;
    }
    private void setBreaks(Image img)
    {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                ivMaskBreaks.setImage(img);
            }
        });
        
    }
    private void setBlinkers(Image img)
    {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                ivMaskBlinker.setImage(img);
            }
        });
        
    }
    public double getForce()
    {
        return force;
    }
    public double getMaxSpeed()
    {
        return maxSpeed;
    }
    public boolean checkColl(Rectangle r1, Rectangle r2)
    {
         if(Shape.intersect(r1, r2).getBoundsInLocal().getWidth()>10)
             return true;
         else
             return false;
    }
    private void runBlinker(Image blinkerImg)
    {
        blinkerTimer=new Timer();
        blinkerTimerTask=new TimerTask() {
            @Override
            public void run() {
                if(blink)
                    setBlinkers(null);
                else
                    setBlinkers(blinkerImg);
                blink=!blink;
            }
        };
        blinkerOn=true;
        blinkerTimer.schedule(blinkerTimerTask, 0,500);
    }
    public void stopBlink()
    {
        if(blinkerTimer!=null)
            blinkerTimer.cancel();
        if(blinkerTimerTask!=null)
            blinkerTimerTask.cancel();
    }
    private void stopBlinker()
    {
        if(actualSegment.isStopBlinker())
        {
            if(blinkerOn)
            {
                setBlinkers(null);
                stopBlink();
                blinkerOn=false;
            }
        }
        
    }

    public RoadSegment getActualSegment() {
        return actualSegment;
    }
    
}
