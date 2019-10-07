/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dipl_project.Vehicles;

import dipl_project.Dipl_project;
import dipl_project.Roads.MyMath;
import dipl_project.Roads.RoadSegment;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
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
    private List<RoadSegment> road=new ArrayList<>();
    private RoadSegment actualSegment,oldSegment;
    private Point p0, p1, p2, p3;
    private double x0, y0, x1, y1, x2, y2, x3, y3, xLast, yLast, angle;
    private Animation animation;
    private Rectangle controlRectangle;
    private int width=34, height=14;
    private boolean paused=false;
    private final ImageView iv;
    private final double MAX_SPEED=0.07, MAX_FORCE=0.0008;
    private double speed=MAX_SPEED, force=MAX_FORCE, time=0;
    
    public Vehicle(RoadSegment startSegment)
    {
        animation=Dipl_project.getAnim();
        iv=new ImageView(new Image(Dipl_project.class.getResource("/vehicles/auto-01.png").toString()));
        iv.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(paused)
                    play();
                else
                    pause();
            }
        });
        iv.setFitWidth(40);
        iv.setFitHeight(40);
        actualSegment=startSegment;
        generateStreet(startSegment);
        setPoints();
        
        controlRectangle=new Rectangle(width,height, Color.TRANSPARENT);
        controlRectangle.setX(x0-width/2);
        controlRectangle.setY(y0-height/2);
        animation.addVehicle(this);
        move(0);
        move(0.01);
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
    
    public void nextSegment()
    {
        actualSegment.setVehicle(null);
        if(!road.isEmpty())
        {
            oldSegment=actualSegment;
            actualSegment=road.get(0);
            road.remove(actualSegment);
            setPoints();
            actualSegment.setVehicle(this); 
        }
        else
        {
            removeCar();  
        }
    }
    private void removeCar()
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
        
        if(actualSegment.getRsLast().size()>1)
            calcConnectPoints();
        else
        {
            p1=actualSegment.getP1();
            p2=actualSegment.getP2();
        }
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
    
    private void calcConnectPoints()
    {
        p0=oldSegment.getP3();
        double length=MyMath.length(p0, p3)/3;
        double angle=MyMath.angle(p0, oldSegment.getP2());
        p1=MyMath.rotate(p0, length, angle);

        angle=MyMath.angle(p1, p3);
        p2=MyMath.rotate(p3, length, angle); 
    }
    private void move(double t)
    {
        double t2=t*t;
        double t3=t2*t;
        double x = (x0+(t*x1)+(t2*x2)+(t3*x3));
        double y = (y0+(t*y1)+(t2*y2)+(t3*y3)); 
        angle=Math.toDegrees(MyMath.angle(x, y,xLast, yLast));
        if(angle!=0){
            iv.setRotate(angle);
            controlRectangle.setRotate(angle);
        }
        xLast=x;
        yLast=y;
        iv.setX(x-20);
        iv.setY(y-20); 
        controlRectangle.setX(x-width/2);
        controlRectangle.setY(y-height/2);
    }
    public void tick()
    {
        time+=speed;
        move(time);
        updateSpeed(force);
        if(speed>MAX_SPEED){
           force=0;
           speed=MAX_SPEED;
        }
        if(time>=1){
            time-=1;
            nextSegment();
        }
        if(!paused)
           colisionDetect();
    }
    public void updateSpeed(double speed) {
        double newSpeed=this.speed+speed;
        this.speed=newSpeed;
        if(newSpeed<0)
            this.speed=0;
    }
    private void pause()
    {
        paused=true;
        force=-MAX_FORCE;
    }
    private void play()
    {
        paused=false;
        force=MAX_FORCE;
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

    private void colisionDetect()
    {
        boolean carFoundStreet=findNextCar();
        if(!carFoundStreet)
        {
            setForce(MAX_FORCE);
        }
        
    }

    private boolean findCarCross(RoadSegment us, int nextDist, double actDist)
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
                    double dActVeh=actDist-getTime()-1;
                    double dNextVeh=nextDist-nextVeh.getTime()-1;
                    carFound=true;
                    if(fuzzyCrossStop(dActVeh-dNextVeh, getSpeed()-nextVeh.getSpeed()))
                        fuzzySpeed(dActVeh, getSpeed());
                }
                else if(nextDist<10)
                {
                    carFound=findCarCross(rsNext, nextDist+1, actDist);
                }
                else
                {
                    setForce(MAX_FORCE);
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
    private boolean fuzzyCrossStop(double distance, double speed)
    {
        double sp=Dipl_project.getRcCross().calculateByValues(Math.round(speed*10000)/100, distance+1);
        if(sp<1)
            return true;
        else
            return false;
    }
    private boolean findNextCar()
    {
        boolean carFound=false;
        for (int actDist = 0; actDist < 10; actDist++) {
            if(road.size()>actDist){
                RoadSegment rsNext=road.get(actDist);
                if(!carFound)
                {
                    if(rsNext.getVehicle()!=null && rsNext.getVehicle()!=this)
                    {

                        double speedNextCar=rsNext.getVehicle().getSpeed();
                        double tNextCar=rsNext.getVehicle().getTime();
                        double dist=actDist+tNextCar-getTime();
                        fuzzySpeed(dist, getSpeed()-speedNextCar);
                        carFound=true;
                        break;
                    }else if(!rsNext.getCheckPoints().isEmpty())
                    {
                        if(actDist>2 && actDist<6)
                            fuzzySpeed(actDist-getTime()+1, getSpeed());
                        else
                        {
                            for (RoadSegment cp : rsNext.getCheckPoints()) {
                                for (RoadSegment uN : cp.getRsNext()) {
                                    findCarCross(uN, 1, actDist+1);                        
                                }
                            }
                        }
                        
                        carFound=true;
                        break;
                    }
                    else if(!carFound)
                    {
                        setForce(MAX_FORCE);
                    }
                }
            }
            else
                break;
            
        }
        return carFound;
    }
    public void setForce(double force) {
        this.force = force;
    }
    public boolean checkColl(Rectangle r1, Rectangle r2)
    {
         if(Shape.intersect(r1, r2).getBoundsInLocal().getWidth()>10)
             return true;
         else
             return false;
    }
}
