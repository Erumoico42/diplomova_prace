/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dipl_project.Roads;

import java.awt.Point;
import java.util.List;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

/**
 *
 * @author Honza
 */
public class Controll {
    private Connect connect;
    private MyCurve curve;
    private Point location;
    private Line line;
    private Circle controll;
    public Controll(Connect connect, Point location, MyCurve curve) {
        
        this.connect=connect;
        this.curve=curve;
        
        this.location=location;
        line=new Line(connect.getX(), connect.getY(), location.getX(), location.getY());
        line.getStrokeDashArray().addAll(7d, 7d);
        line.setStroke(Color.DODGERBLUE);
        controll = new Circle(location.getX(), location.getY(), 4, Color.RED);
        
    }
    public void move(double x, double y)
    {
        controll.setCenterX(x);
        controll.setCenterY(y);
        location.setLocation(x,y);
        moveLineEnd(x,y);
    }
    
    public Connect getConnect() {
        return connect;
    }
    public void moveLineStart(double x, double y)
    {
        line.setStartX(x);
        line.setStartY(y);
    }
    private void moveLineEnd(double x, double y)
    {
        line.setEndX(x);
        line.setEndY(y);
    }
    public MyCurve getCurve() {
        return curve;
    }

    public Point getLocation() {
        return location;
    }

    public Line getLine() {
        return line;
    }

    public Circle getControll() {
        return controll;
    }
    
}
