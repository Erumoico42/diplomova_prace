/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dipl_project;

import dipl_project.Fuzzy.*;
import dipl_project.Roads.RoadCreator;
import dipl_project.Simulation.SimulationControll;
import dipl_project.Storage.StorageControll;
import dipl_project.TrafficLights.TrafficLightsControll;
import dipl_project.UI.UIControlls.DrawControll;
import dipl_project.UI.UIControlls.UIControll;
import dipl_project.Vehicles.Animation;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

/**
 *
 * @author Honza
 */
public class Dipl_project extends Application {
    private static UIControll ui;
    private static DrawControll dc;
    private static RoadCreator rc;
    private static Animation anim;
    private static RuleBaseReader rbrFollow,rbrCross;
    private static RulesCalculator rcFollow,rcCross;
    private static SimulationControll sc;
    private static StorageControll stc; 
    private static TrafficLightsControll tlc;
    private static String[] arg;

    
    @Override
    public void start(Stage primaryStage) {
        tlc=new TrafficLightsControll();
        rc=new RoadCreator();
        ui=new UIControll(primaryStage,arg);
        dc=new DrawControll(ui, rc);
        ui.setDc(dc);
        anim=new Animation();
        sc=new SimulationControll();
        stc=new StorageControll();
        ui.setSc(sc);
        loadRules();
        if(ui.getUiTestMenu()!=null)
            ui.getUiTestMenu().showInitMenu();
    }
    public static void loadRules()
    {
        rbrFollow=new RuleBaseReader(Dipl_project.class.getResourceAsStream("Resources/fuzzyRules/rules_follow.rb"));
        rcFollow=new RulesCalculator(rbrFollow.getOutputVariable(), rbrFollow.getFuzzyRules());
        rbrCross=new RuleBaseReader(Dipl_project.class.getResourceAsStream("Resources/fuzzyRules/rules_cross.rb"));
        rcCross=new RulesCalculator(rbrCross.getOutputVariable(), rbrCross.getFuzzyRules());
        
    }
    public static RulesCalculator getRcFollow() {
        return rcFollow;
    }
    public static DrawControll getDC()
    {
        return dc;
    }
    public static StorageControll getStc() {
        return stc;
    }
    
    public static RoadCreator getRC() {
        return rc;
    }

    public static UIControll getUI() {
        return ui;
    }

    public static Animation getAnim() {
        return anim;
    }

    public static RulesCalculator getRcCross() {
        return rcCross;
    }

    public static SimulationControll getSc() {
        return sc;
    }

    public static TrafficLightsControll getTlc() {
        return tlc;
    }
    public static void closeApp() {
        getAnim().stopAnimation();
        getSc().stopVehicleGenerator();
        getTlc().stopTrafficLights();
        getUI().savePlayer();
        Platform.exit();
        System.exit(0);
    }
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        arg=args;
        launch(args);
    }
    
}
