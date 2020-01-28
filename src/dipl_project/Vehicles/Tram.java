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
public class Tram extends Vehicle{
    
    public Tram(RoadSegment startSegment) {
        super(startSegment);
        setVehicleLenght(2);
        initVehicleImage(new Image(Dipl_project.class.getResource("Resources/vehicles/tram.png").toString()), 80, 40, 70, 35);
    }
    
}
