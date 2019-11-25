/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dipl_project.UI;

import dipl_project.Dipl_project;
import dipl_project.Roads.MyMath;
import java.awt.Point;
import java.io.File;
import javafx.geometry.Point3D;
import javafx.scene.SubScene;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.transform.Rotate;
import javafx.stage.FileChooser;

/**
 *
 * @author Honza
 */
public class BackgroundControll {
    private static double resizeRatio;
    private static String bgSource;
    private static ImageView background;
    
    private static UIControll ui=Dipl_project.getUI();
    private static Canvas backgroundCanvas=ui.getBackgroundCanvas();
    private static final int RESIZE_VALUE=50, MOVE_VALUE=25;
    private static double layoutX, layoutY, startX, startY, startAngle, angle;
    public static void loadImage()
    {
        //SubScene subScene=gui.getDrawScene();
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(null);
        if (file != null) { 
            bgSource=file.toURI().toString();
            Image image = new Image(bgSource);
            
            background=new ImageView();
            background.setImage(image);
            background.setFitHeight(image.getHeight());
            background.setFitWidth(image.getWidth());
            resizeRatio=image.getWidth()/image.getHeight();
            ui.getEditBackground().setDisable(false);
            ui.addBackground(background);
            layoutX=-(image.getWidth()-backgroundCanvas.getWidth())/2;
            layoutY=-(image.getHeight()-backgroundCanvas.getHeight())/2;
            background.setLayoutX(layoutX);
            background.setLayoutY(layoutY);
            
        }
    }
    public static void backgroundClick(double x, double y)
    {
        startX = x-layoutX;
        startY = y-layoutY;
        startAngle = Math.toDegrees(MyMath.angle(backgroundCanvas.getWidth()/2, backgroundCanvas.getHeight()/2, x, y+100))- angle;
    }
    public static void setBackground(double x, double y, double e,double width, double height, String path)
    {
        Image image = new Image(path);
        angle=e;
        background.setRotate(angle);
        background=new ImageView();
        background.setImage(image);
        background.setFitHeight(image.getHeight());
        background.setFitWidth(image.getWidth());
        resizeRatio=image.getWidth()/image.getHeight();
        background.setLayoutX(x);
        background.setLayoutY(y);
    }
    public static void moveBackground(double x, double y)
    {
        layoutX = x - startX;
        layoutY = y - startY;
        
        background.setLayoutX(layoutX);
        background.setLayoutY(layoutY);
    }
    public static void rotateBackground(double x, double y)
    {
        double newAngle = Math.toDegrees(MyMath.angle(backgroundCanvas.getWidth()/2, backgroundCanvas.getHeight()/2, x, y+100));
        angle = (newAngle - startAngle);
        if(angle>360) angle-=360; else if(angle<-360) angle+=360;
        background.setRotate(angle);
    }
    public static void zoomBackground(double d)
    {
        if(d<0)
        {
            if(background.getFitHeight()>=100 && background.getFitWidth()>=100)
            {
                background.setFitHeight(background.getFitHeight()-RESIZE_VALUE);
                background.setFitWidth(background.getFitWidth()-RESIZE_VALUE*resizeRatio);
                layoutX=layoutX+MOVE_VALUE*resizeRatio;
                layoutY=layoutY+MOVE_VALUE;
                background.setLayoutX(layoutX);
                background.setLayoutY(layoutY);
            }
        }
        else
        {
            background.setFitHeight(background.getFitHeight()+RESIZE_VALUE);
            background.setFitWidth(background.getFitWidth()+RESIZE_VALUE*resizeRatio);
            layoutX=layoutX-MOVE_VALUE*resizeRatio;
            layoutY=layoutY-MOVE_VALUE;
            background.setLayoutX(layoutX);
            background.setLayoutY(layoutY);
        }
    }
}
