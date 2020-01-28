/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dipl_project.Vehicles;

import dipl_project.Dipl_project;
import dipl_project.Roads.RoadSegment;
import javafx.scene.image.Image;

/**
 *
 * @author Honza
 */
public class Car extends Vehicle{
    
    public Car(RoadSegment startSegment) {
        super(startSegment);
        initVehicleImage(getRandomCarImage(), 40, 40, 34, 14);
        setVehicleLenght(0);
    }
    private Image getRandomCarImage()
    {
        Image imgRet=null;
        int rnd=(int)(Math.random()*3);
        switch(rnd)
        {
            case 0:
            {
                imgRet= new Image(Dipl_project.class.getResource("Resources/vehicles/auto-01.png").toString());
                break;
            }
            case 1:
            {
                imgRet= new Image(Dipl_project.class.getResource("Resources/vehicles/auto-02.png").toString());
                break;
            }
            case 2:
            {
                imgRet= new Image(Dipl_project.class.getResource("Resources/vehicles/auto-03.png").toString());
                break;
            }
        }
        return imgRet;
    }
}
