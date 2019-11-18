/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dipl_project.Roads;
import dipl_project.Dipl_project;
import java.awt.Point;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

/**
 *
 * @author Honza
 */
public class Arrow {
    private Polygon arrow;

    public Arrow(double angle, double x, double y) {
        this.arrow = arrow;
        arrow=new Polygon();
        moveArrow(x, y, angle);
        
        arrow.setFill(Color.FIREBRICK);
        arrow.setStroke(Color.WHITESMOKE);
        Dipl_project.getUI().addComponentsDown(arrow);
    }
    public void moveArrow(double x, double y, double angle)
    {
        Point pt2=MyMath.rotate(new Point((int)x, (int)y), 15, angle);
        Point pt1=MyMath.rotate(pt2, 20, angle+2.75);
        Point pt3=MyMath.rotate(pt2, 20, angle-2.75); 
        arrow.getPoints().setAll(new Double[]{x,y, pt1.getX(), pt1.getY() 
                ,pt2.getX(), pt2.getY(), pt3.getX(), pt3.getY()});
    }

    public Polygon getArrow() {
        return arrow;
    }
    
}
