/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dipl_project.Roads.VehicleGenerating;

import dipl_project.Roads.Connect;
import dipl_project.Roads.MyCurve;
import dipl_project.Roads.RoadSegment;
import dipl_project.Vehicles.Car;

/**
 *
 * @author Honza
 */
public class StartCar extends StartSegment {
    
     public StartCar(RoadSegment startRS, int frequencyMinute, Connect startConnect, MyCurve mc) {
        super(startRS, frequencyMinute, startConnect, mc);
    }
    @Override
    public void newVehicle()
    {
        new Car(super.startRS);
    }
}
