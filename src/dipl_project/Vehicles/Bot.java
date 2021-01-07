/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dipl_project.Vehicles;

import dipl_project.Roads.RoadSegment;

/**
 *
 * @author Honza
 */
public class Bot extends Vehicle {
    
    public Bot(RoadSegment startSegment) {
        super(startSegment);
    }


    @Override
    public void tick() {
        super.tick(); //To change body of generated methods, choose Tools | Templates.
        colisionDetect();
    }
    
}
