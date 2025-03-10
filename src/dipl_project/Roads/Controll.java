/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dipl_project.Roads;

import java.awt.Point;
import java.util.List;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
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
    private Point location, locOrigin;
    private Line line;
    private double originAngle;
    private Circle controll;
    public Controll(Connect connect, Point location, MyCurve curve) {
        
        this.connect=connect;
        this.curve=curve;
        
        this.location=location;
        line=new Line(connect.getX(), connect.getY(), location.getX(), location.getY());
        line.getStrokeDashArray().addAll(7d, 7d);
        line.setStroke(Color.DODGERBLUE);
        controll = new Circle(location.getX(), location.getY(), 5, Color.RED);
        controll.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                controll.setRadius(7);
            }
        });
        controll.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                controll.setRadius(5);
            }
        });
        
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
        setOriginAngle(MyMath.angle( line.getEndX(),line.getEndY(),x,y));
    }

    public double getOriginAngle() {
        return originAngle;
    }

    public void setOriginAngle(double originAngle) {
        this.originAngle = originAngle;
    }
    
    private void moveLineEnd(double x, double y)
    {
        line.setEndX(x);
        line.setEndY(y);
        setOriginAngle(MyMath.angle(x,y,line.getStartX(),line.getStartY()));
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

    public Point getLocOrigin() {
        return locOrigin;
    }

    public void setLocOrigin(Point locOrigin) {
        this.locOrigin = locOrigin;
    }
    
}
