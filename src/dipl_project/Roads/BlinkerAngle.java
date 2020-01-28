/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dipl_project.Roads;

/**
 *
 * @author Honza
 */
public class BlinkerAngle {
    private final double angle;
    private final MyCurve mc;
    private boolean run=false;

    public BlinkerAngle(double angle, MyCurve mc) {
        this.angle = angle;
        this.mc = mc;
    }

    public double getAngle() {
        return angle;
    }

    public MyCurve getMc() {
        return mc;
    }

    public boolean isRun() {
        return run;
    }

    public void setRun(boolean run) {
        this.run = run;
    }
    
}
