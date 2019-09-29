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
    private static RuleBaseReader rbr;
    private static RulesCalculator ruc;
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
        rbr=new RuleBaseReader("C:\\Users\\Honza\\Desktop\\mgr\\rules_v1.rb");
        ruc=new RulesCalculator(rbr.getOutputVariable(), rbr.getFuzzyRules());
    }
    public static RulesCalculator getRUC() {
        return ruc;
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
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
