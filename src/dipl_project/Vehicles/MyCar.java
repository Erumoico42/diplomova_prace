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
public class MyCar extends Vehicle{

    public MyCar(RoadSegment startSegment) {
        super(startSegment);
        super.setForce(0);
        super.setSpeed(0);
        setMyCarImage();
        setBreaksLayout(-5);
        super.setVehicleLenght(0);
        
    }
    private void setMyCarImage()
    {
        String carName="moje";
        Image carDef= new Image(Dipl_project.class.getResource("Resources/vehicles/"+carName+".png").toString());
        Image carLeft= new Image(Dipl_project.class.getResource("Resources/vehicles/blinker-l.png").toString());
        Image carRight= new Image(Dipl_project.class.getResource("Resources/vehicles/blinker-r.png").toString());
        Image carBreak= new Image(Dipl_project.class.getResource("Resources/vehicles/breaks.png").toString());
        initVehicleImage(carDef, carLeft, carRight, carBreak, 50, 50, 40, 18);

    }

    @Override
    public void removeCar() {
        super.removeCar(); //To change body of generated methods, choose Tools | Templates.
        Dipl_project.getSc().setMycar(null);
        Dipl_project.getUI().newMyCar();
    }
    
}
