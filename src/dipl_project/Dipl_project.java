/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dipl_project;

import dipl_project.Fuzzy.*;
import dipl_project.Roads.RoadCreator;
import dipl_project.Roads.RoadSegment;
import dipl_project.UI.DrawControll;
import dipl_project.UI.UIControll;
import dipl_project.Vehicles.Animation;
import java.nio.file.Paths;
import javafx.application.Application;
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
    @Override
    public void start(Stage primaryStage) {
        rc=new RoadCreator();
        ui=new UIControll(primaryStage);
        dc=new DrawControll(ui, rc);
        anim=new Animation();
        loadRules();
    }
    public static void loadRules()
    {
        
        rbrFollow=new RuleBaseReader("Resources/fuzzyRules/rules_follow.rb");
        rcFollow=new RulesCalculator(rbrFollow.getOutputVariable(), rbrFollow.getFuzzyRules());
        rbrCross=new RuleBaseReader("Resources/fuzzyRules/rules_cross.rb");
        rcCross=new RulesCalculator(rbrCross.getOutputVariable(), rbrCross.getFuzzyRules());
    }
    public static RulesCalculator getRcFollow() {
        return rcFollow;
    }
    public static DrawControll getDC()
    {
        return dc;
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
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
