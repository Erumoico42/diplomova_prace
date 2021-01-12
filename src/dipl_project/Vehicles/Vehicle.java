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
    private Image boomIcon= new Image(Dipl_project.class.getResource("Resources/vehicles/boom_icon.png").toString());
    private double lastForce;
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
    private double speed=maxSpeed/3, force=0.0003, time=0, vehicleLenght;
    private double nonRatioSpeed=speed, lastSpeed=0.001;
    private boolean watch=false;
    private int id;
    private int watchCount=0, freeCount=0;
    private Timer blinkerTimer;
    private TimerTask blinkerTimerTask;
    private int breakCountDown=0;
    private boolean blink=false, blinkerOn=false, changedForce=false;
    private double lastAngle=0;
    private double newAngle;
    private double segmentLenghtKoef;
    private double speedCoef;
    private double maxSpeedCoef;
    private double forceCoef;
    private double distanceToCheck;
    private boolean carFoundStreet;
    private boolean removing=false;
    private double lastMoveX;
    private double lastMoveY;
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
        actualSegment=startSegment;
        actSegmentLenght=actualSegment.getSegmentLenght();
        segmentLenghtKoef=Dipl_project.getRC().getSegLenght()/actSegmentLenght;
        setMoveValuesBySegmentCoef();
        generateStreet(startSegment);
        setPoints();
        
        
        
       
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
        if(ret>350)
            ret=360-ret;
        if(ret>20)
        {
            
            lastAngle=newAngle;
            ret=lastAngle;
        }
        return ret;
    }
    public double getStatDistance()
    {
        return MyMath.length(lastPosition.getX(), lastPosition.getY(), newPosition.getX(), newPosition.getY());
    }
    public void showCrash(boolean crash)
    {
        
        if(crash)
           setBoom();
        else
            setBreaks(null);
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
        
        controlRectangle=new Rectangle(controlWidth,controlHeight, Color.TRANSPARENT);

        
        
        newPosition=new MyPoint((xLast-vehWidth/2), (yLast-vehHeight/2));
        move(0);
        time=0.01;
        move(time);
       
        animation.addVehicle(this);
        actSegmentLenght=actualSegment.getSegmentLenght();
    }
    public void setTime(double time)
    {
        this.time=time;
    }
    public void setBreaksLayout(double breaksLayout)
    {
        this.breaksLayout=breaksLayout;
    }
    protected void generateStreet(RoadSegment start)
    {
        road.clear();
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
        showVehicle(true);
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
    public void showVehicle(boolean show)
    {
        iv.setVisible(show);
    }
    public void nextSegment()
    {
        actualSegment.setVehicle(null);
        if(!road.isEmpty())
        {
            actualSegment=road.get(0);
            actSegmentLenght=actualSegment.getSegmentLenght();
            segmentLenghtKoef=Dipl_project.getRC().getSegLenght()/actSegmentLenght;
            setMoveValuesBySegmentCoef();
            stopBlinker();
            
            
            setPoints();
            actualSegment.setVehicle(this); 
            road.remove(actualSegment);
        }
        else
        {
            removeVehicle(); 
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
        controlRectangle.setArcWidth(controlWidth);
        controlRectangle.setArcHeight(controlHeight);
    }
    public void removeVehicle()
    {
        actualSegment.setVehicle(null);
        animation.removeVehicle(this);
    }
    public Rectangle getControlRectangle() {
        return controlRectangle;
    }
    
    protected void setPoints()
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
    protected void setPosition(double x, double y)
    {
        iv.setX(x);
        iv.setY(y); 
        
        ivMaskBlinker.setX(breaksLayout+x);
        ivMaskBlinker.setY(y); 
        ivMaskBreaks.setX(breaksLayout+x);
        ivMaskBreaks.setY(y); 
        controlRectangle.setX(x-controlWidth/2);
        controlRectangle.setY(y-controlHeight/2);
    }
    protected void move(double t)
    {
        double t2=t*t;
        double t3=t2*t;
        double x = (x0+(t*x1)+(t2*x2)+(t3*x3));
        double y = (y0+(t*y1)+(t2*y2)+(t3*y3)); 
        double updatedAngle=Math.toDegrees(MyMath.angle(x, y,xLast, yLast));
        if(updatedAngle!=0){
            double lastUpdatedAngle=angle;
            angle=updatedAngle;
            lastAngle=newAngle;
            newAngle=angle;
            if(getDAngle()>15)
                angle=lastUpdatedAngle;
            iv.setRotate(angle);
            ivMaskBlinker.setRotate(angle);
            ivMaskBreaks.setRotate(angle);
            //controlRectangle.setRotate(angle);
        }
        lastMoveX=xLast-x;
        lastMoveY=yLast-y;
        xLast=x;
        yLast=y;
        lastPosition=new MyPoint(newPosition.getX(),newPosition.getY());
        newPosition=new MyPoint((x-vehWidth/2), (y-vehHeight/2));
        setPosition(newPosition.getX(),newPosition.getY());
        
    }
    private void setMoveValuesBySegmentCoef()
    {
        speedCoef=speed*segmentLenghtKoef;
        maxSpeedCoef=maxSpeed*segmentLenghtKoef;
        forceCoef=force*segmentLenghtKoef;
    }
    
    public void move()
    {
        setPoints();
        move(time);
    }
    public void moveByPx(double x, double y)
    {
        setPosition(newPosition.getX()+x, newPosition.getY()+y);
    }
    public void tick()
    {
        time+=speedCoef;     
        if(time>1)
            time=1;
        
        move(time);
        
        updateSpeed();
        /*
        if(speed>newMaxSpeed){
           force=0;
           speed=newMaxSpeed;
        }*/
            
        if(time>=1){
            time-=1;
            nextSegment();
        }
        findBlinker();
        
        
    }
    public void setRemoving(boolean remove)
    {
        removing=remove;
    }

    public boolean isRemoving() {
        return removing;
    }
    
    public double getMaxForce() {
        return maxForce;
    }
    public void updateSpeed(double speed)
    {
        this.speed+=speed;
        //updateSpeed();
    }

    public MyPoint getNewPosition() {
        return newPosition;
    }
    public double getLastMoveX()
    {
        return lastMoveX;
    }
    public double getLastMoveY()
    {
        return lastMoveY;
    }
    public MyPoint getLastPosition() {
        return lastPosition;
    }
    
    public void updateSpeed() {
        
        forceCoef =force*segmentLenghtKoef;
        speedCoef=speed*segmentLenghtKoef;
        
        double newSpeedCoef=speedCoef+forceCoef;
        
        double newSpeed=speed+force;
            if(newSpeedCoef < speedCoef)
            {
                breakCountDown=10;
                changedForce=!defCar.equals(carBreak);
                defCar=carBreak;
                if(!removing)
                {
                    showBreaks(true);
                    
                }
                    
                if((100/speedCoef)*newSpeedCoef<20)
                {
                    newSpeedCoef=speedCoef;
                }
                    
            }
            else if(!this.getClass().equals(MyCar.class))
            {
                breakCountDown--;
                if(breakCountDown<0)
                {
                    changedForce=!defCar.equals(actualCar);
                    if(!removing)
                        showBreaks(false);
                    
                }
            }

            if(changedForce)
            {
                changedForce=false;
            }
        
        
        if(newSpeedCoef<0)
            newSpeedCoef=0;
        if(newSpeedCoef>maxSpeedCoef)
            newSpeedCoef=maxSpeedCoef; 
        speedCoef=newSpeedCoef;
        
        if(newSpeed<0)
            newSpeed=0;
        if(newSpeed>maxSpeed)
            newSpeed=maxSpeed; 
        speed=newSpeed;
        
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
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }
    
    public void colisionDetect()
    {
        
        if(!paused){
           carFoundStreet=findNextCar();
            if(!carFoundStreet && !removing)
            {
                setForce(maxForce);
            } 
        }
        
    }
    protected boolean carDetected()
    {
        return carFoundStreet;
    }
    protected boolean findCarCross(RoadSegment us, int nextDist, double actDist, int minDist)
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
                        double distanceToCheck=dNextVeh/Dipl_project.getRC().getSegLenght()-vehicleLenght-time;
                        boolean stop=fuzzyCrossStop(distanceToCheck, nextVeh.getSpeed(), dActVeh, getSpeed());
                        if(actDist>0.5 && stop)
                        {
                            fuzzySpeed(dActVeh-1, getSpeed());
                        }
                        
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
    protected double getDistanceCheck()
    {
        return distanceToCheck;
    }
    private boolean findNextCar()
    {
        boolean carFound=false;
        double distanceReal=0;
        for (int actDist = 0; actDist < 10; actDist++) {
            if(road.size()>actDist){
                
                RoadSegment rsNext=road.get(actDist);
                distanceReal+=rsNext.getSegmentLenght();
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
                        distanceToCheck=distanceReal/Dipl_project.getRC().getSegLenght()-vehicleLenght-time;
                        if(rsCheck.getVehicle()!=null && rsCheck.getVehicle()!=this)
                        {   
                            double speedNextCar=rsCheck.getVehicle().getSpeed()*speedMinus;
                            double tNextCar=rsCheck.getVehicle().getTime();
                            
                            double dist=distanceToCheck+tNextCar-getTime()+distMinus;
                            
                            dist-=rsCheck.getVehicle().getVehicleLenght();
                            
                            if(dist<1 || distMinus==-1)
                                setForce(-maxForce);
                            fuzzySpeed(dist-0.3, getSpeed()-speedNextCar);
                                carFound=true;
                            
                            
                        }else if(!rsSameWay.contains(rsCheck))
                        {
                            boolean trafficLightFound=false;
                            for (TrafficLight trafficLight : rsCheck.getTrafficLights()) {
                                
                                int status=trafficLight.getStatus();
                                //int tlTime=trafficLight.getTimeCountDown();
                                //int tlMaxTime=trafficLight.getMaxTime();
                                
                                trafficLightFound=true;
                                
                                
                                if((status==1 && !trafficLight.isOrangeSwitching() && distanceToCheck<2) || status==2 || status==3)
                                {
                                    fuzzySpeed(distanceToCheck, getSpeed());
                                    carFound=true; 
                                    
                                }
                                if(status==0 || (status==1 && distanceToCheck<1))
                                {
                                    carFound=false;
                                    setForce(maxForce);
                                }
                                    
                                  
                            }
                            if(trafficLightFound)
                                distanceReal=11; 
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
                                            fuzzySpeed(distanceToCheck, getSpeed());
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

                                        if(distanceToCheck>2 && distanceToCheck<6)
                                        {
                                            fuzzySpeed(distanceToCheck+1, getSpeed());
                                        }  
                                        else
                                        {

                                            for (CheckPoint cp : cps) {
                                                if(cp.isEnabled())
                                                for (RoadSegment uN : cp.getRs().getRsNext()) {
                                                    carFound=findCarCross(uN, 1, distanceToCheck-1, cp.getDistance());                        
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
    
    private void findWatchCar(RoadSegment rs, double actDist, int maxDist)
    {
            for (RoadSegment rsLast : rs.getRsLast()) {
                if(road.contains(rsLast))
                {
                    if(rsLast.equals(actualSegment))
                    {
                        Vehicle veh=rsLast.getVehicle();
                        if(veh!=null )
                            watchCount++;
                        if(actDist/Dipl_project.getRC().getSegLenght()<maxDist)
                        findWatchCar(rsLast, actDist+rsLast.getSegmentLenght(), maxDist);
                    }
                    else
                        break;
                    
                }
                
            }
    }
    private void findWatchFree(RoadSegment rs, double actDist, int maxDist)
    {
            for (RoadSegment rsNext : rs.getRsNext()) {
                if(road.contains(rsNext))
                {
                    Vehicle veh=rsNext.getVehicle();
                    if(veh==null)
                        freeCount++;
                    else if(veh.getSpeed()/veh.getMaxSpeed()<0.6)
                        break;
                    if(actDist/Dipl_project.getRC().getSegLenght()<=maxDist)
                    findWatchFree(rsNext, actDist+rsNext.getSegmentLenght(), maxDist);
                }
            }
    }
    public void setForce(double newForce) {
        lastForce=force;
        if(newForce>maxForce)
            newForce=maxForce;
        force = newForce;
    }
    public void showBreaks(boolean show)
    {
        if(show)
            setBreaks(carBreak);
        else
            setBreaks(null);
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

    protected void setBoom() {
        setBreaks(boomIcon);
    }

    protected void crash() {
        setRemoving(true);
        animation.addRemovingVehicle(new VehicleToRemove(this));
        
        pause();
        setSpeed(0);
        setForce(0);
    }
    
}
