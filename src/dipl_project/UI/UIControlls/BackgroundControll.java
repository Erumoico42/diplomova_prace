/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dipl_project.UI.UIControlls;

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
    private static double width, height;
    private static UIControll ui=Dipl_project.getUI();
    private static Canvas backgroundCanvas=ui.getMoveCanvas();
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
            
            if(background==null)
            {
                background=new ImageView();
                ui.addBackground(background);
                ui.getUiTopMenu().enableRemoveBG(true);
            }
            
            background.setImage(image);
            width=image.getWidth();
            height=image.getHeight();
            background.setFitHeight(height);
            background.setFitWidth(width);
            resizeRatio=image.getWidth()/image.getHeight();
            ui.getUiTopMenu().getEditBackground().setDisable(false);
            ui.setMoveStatus(1);
            layoutX=-(image.getWidth()-backgroundCanvas.getWidth())/2;
            layoutY=-(image.getHeight()-backgroundCanvas.getHeight())/2;
            background.setLayoutX(layoutX);
            background.setLayoutY(layoutY);
        }
    }
    public static void removeBG()
    {
        Dipl_project.getUI().removeBackground(background);
                BackgroundControll.setBackground(null);
    }
    public static void backgroundClick(double x, double y)
    {
        startX = x-layoutX;
        startY = y-layoutY;
        startAngle = Math.toDegrees(MyMath.angle(backgroundCanvas.getWidth()/2, backgroundCanvas.getHeight()/2, x, y+100))- angle;
    }
    public static boolean isBackground()
    {
        return background!=null;
    }
    public static void setBackground(double x, double y, double e,double w, double h,double resRatio, String path)
    {
        bgSource=path;
        Image image = new Image(bgSource);
        if(background==null)
        {
            background=new ImageView();
            ui.addBackground(background);
        }
        resizeRatio=resRatio;
        background.setImage(image);
        angle=e;
        width=w;
        height=h;
        layoutX = x;
        layoutY = y;
        background.setRotate(angle);
        background.setFitHeight(height);
        background.setFitWidth(width);
        background.setLayoutX(x);
        background.setLayoutY(y);
    }
    public static void moveBackground(double x, double y)
    {
        layoutX = x - startX;
        layoutY = y - startY;
        if(background!=null)
        {
            background.setLayoutX(layoutX);
            background.setLayoutY(layoutY);
        }
        
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
                width=background.getFitWidth()-RESIZE_VALUE*resizeRatio;
                height=background.getFitHeight()-RESIZE_VALUE;
                background.setFitHeight(height);
                background.setFitWidth(width);
                layoutX=layoutX+MOVE_VALUE*resizeRatio;
                layoutY=layoutY+MOVE_VALUE;
                background.setLayoutX(layoutX);
                background.setLayoutY(layoutY);
            }
        }
        else
        {
            width=background.getFitWidth()+RESIZE_VALUE*resizeRatio;
            height=background.getFitHeight()+RESIZE_VALUE;
            background.setFitHeight(height);
            background.setFitWidth(width);
            layoutX=layoutX-MOVE_VALUE*resizeRatio;
            layoutY=layoutY-MOVE_VALUE;
            background.setLayoutX(layoutX);
            background.setLayoutY(layoutY);
        }
    }
    public static void zoomBackgroundByRatio(double d)
    {
        width=background.getFitWidth()*d;
        height=background.getFitHeight()*d;
        background.setFitHeight(height);
        background.setFitWidth(width);
        double canvasWidhth=Dipl_project.getUI().getCanvas().getWidth()/2;
        double canvasHeight=Dipl_project.getUI().getCanvas().getHeight()/2;
        layoutX=((layoutX-canvasWidhth)*d)+canvasWidhth;
        layoutY=((layoutY-canvasHeight)*d)+canvasHeight;
        background.setLayoutX(layoutX);
        background.setLayoutY(layoutY);
    }
    public static double getResizeRatio() {
        return resizeRatio;
    }

    public static String getBgSource() {
        return bgSource;
    }

    public static double getLayoutX() {
        return layoutX;
    }

    public static double getLayoutY() {
        return layoutY;
    }

    public static double getAngle() {
        return angle;
    }

    public static double getWidth() {
        return width;
    }

    public static double getHeight() {
        return height;
    }

    public static void setBackground(ImageView background) {
        BackgroundControll.background = background;
    }
    
}
