/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dipl_project.Roads;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Honza
 */
public class CheckPoint {
    private final RoadSegment rs;
    private int distance=0;
    private List<CheckPoint> secondaryRS=new ArrayList<>();
    public CheckPoint(RoadSegment rs) {
        this.rs = rs;
    }
    public CheckPoint(RoadSegment rs, int distance) {
        this.rs = rs;
        this.distance=distance;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public RoadSegment getRs() {
        return rs;
    }

    public List<CheckPoint> getSecondaryRS() {
        return secondaryRS;
    }

    public void addSecondaryRS(CheckPoint srs) {
        secondaryRS.add(srs);
    }
    public void clearSecondaryRS()
    {
        for (CheckPoint srs : secondaryRS) {
            srs.getRs().removeSecondaryCheckPointsByRS(rs);
        }
        secondaryRS.clear();
    }
    
}
