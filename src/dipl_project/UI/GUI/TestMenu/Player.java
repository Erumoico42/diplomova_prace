/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dipl_project.UI.GUI.TestMenu;

/**
 *
 * @author Honza
 */
public class Player {

    private final String name;
    private final int totalCrashes;
    private final int totalRuns;
    private final int lightsRuns;
    private final double avgTime;
    private final String mark;
    private final String date;
    private final String slowRun;
    private final String fastRun;
    public Player(String name, int crashCount, int runCount, int lightsCount, double avgTime, String slowRun, String fastRun, String mark, String date) {
        this.name = name;
        this.totalCrashes = crashCount;
        this.totalRuns = runCount;
        this.lightsRuns = lightsCount;
        this.avgTime = avgTime;
        this.mark=mark;
        this.slowRun=slowRun;
        this.fastRun=fastRun;
        this.date=date;
        
    }
}
