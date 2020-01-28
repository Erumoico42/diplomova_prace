/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dipl_project.Roads;

import java.awt.Point;
import java.util.Vector;
import javafx.scene.shape.CubicCurve;

/**
 *
 * @author Honza
 */
public class MyMath {
    public static double angle(double x1, double y1, double x2, double y2)
    {
        double angle = (double) (Math.atan2(y1 - y2, x1 - x2));
        if(angle<0)
            angle+=(2*Math.PI);
        if(angle>2*Math.PI)
            angle-=(2*Math.PI);
        return angle;
    }
    public static double angle(Point p1, Point p2) {
        return angle(p1.getX(),p1.getY(),p2.getX(),p2.getY());
    }
    public static double length(Point p1,Point p2)
    {
        double distance = length(p1.getX(), p1.getY(), p2.getX(), p2.getY());
        return distance;
    }
    public static double length(double x1, double y1, double x2, double y2)
    {
        double distance = Math.sqrt(Math.pow(x2-x1,2) + Math.pow(y2-y1,2));
        return distance;
    }
    public static Point rotate(Point p1, double length, double angle)
    {
        Point pp=new Point();
        double x=Math.cos(angle)*length;
        double y=Math.sin(angle)*length;
        pp.setLocation(x+p1.getX(),y+p1.getY());
        return pp;
    }
    public static Point getCurvePointAtT(Point p0, Point p1, Point p2, Point p3, double time)
    {
        
        Point p01=getLinePointAtT(p0, p1, time);
        Point p11=getLinePointAtT(p1, p2, time);
        Point p21=getLinePointAtT(p2, p3, time);
        Point p12=getLinePointAtT(p01, p11, time);
        Point p22=getLinePointAtT(p11, p21, time);
        
        Point pRet=getLinePointAtT(p12, p22, time);
        return pRet;
    }
    public static Point getLinePointAtT(Point p0, Point p1, double time)
    {
        Point u= new Point((int)(p1.getX()-p0.getX()), (int)(p1.getY()-p0.getY()));
        Point pRet=new Point((int)(p0.getX()+(time*u.getX())),(int)(p0.getY()+(time*u.getY())));
        return pRet;
    }
}
