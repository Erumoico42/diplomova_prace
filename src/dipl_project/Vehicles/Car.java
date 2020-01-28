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
        
        setRandomCarImage();
        setVehicleLenght(0);
    }
    private void setRandomCarImage()
    {
        int rnd=(int)(Math.random()*11)+1;
        String carTemp="auto-";
        if(rnd<10)
            carTemp+="0";
        String carName=carTemp+rnd;
        Image carDef= new Image(Dipl_project.class.getResource("Resources/vehicles/"+carName+".png").toString());
        Image carLeft= new Image(Dipl_project.class.getResource("Resources/vehicles/"+carName+"-b-l.png").toString());
        Image carRight= new Image(Dipl_project.class.getResource("Resources/vehicles/"+carName+"-b-p.png").toString());
        Image carBreak= new Image(Dipl_project.class.getResource("Resources/vehicles/"+carName+"-b.png").toString());
        initVehicleImage(carDef, carLeft, carRight, carBreak, 40, 40, 34, 14);

    }
}
