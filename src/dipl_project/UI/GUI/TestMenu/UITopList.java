/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dipl_project.UI.GUI.TestMenu;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

/**
 *
 * @author Honza
 */
public class UITopList {

    private Stage statStage;
    private TableView<Object> playersTable;
    public UITopList()
    {
        initStatist();
    }
    private void initStatist()
    {
        Group statGroup = new Group();
        Scene statScene = new Scene(statGroup, 1000, 300);
        statStage = new Stage();
        statStage.setTitle("Seznam hráčů");
        statStage.setScene(statScene);
        
        
        playersTable = new TableView<>();
        playersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        TableColumn name = new TableColumn("Jméno");
        name.setMinWidth(65);
        name.setResizable(false);
        name.setCellValueFactory(
                new PropertyValueFactory<Player, String>("name"));  
        
        TableColumn totalRuns = new TableColumn("Počet kol");
        totalRuns.setMinWidth(35);
        totalRuns.setResizable(false);
        totalRuns.setCellValueFactory(
                new PropertyValueFactory<Player, String>("runCount"));
        
        TableColumn avgTime = new TableColumn("Průměrný čas");
        avgTime.setMinWidth(90);
        avgTime.setResizable(false);
        avgTime.setCellValueFactory(
                new PropertyValueFactory<Player, String>("avgTime"));
        
        TableColumn totalCrashes = new TableColumn("Počet nehod");
        totalCrashes.setMinWidth(70);
        totalCrashes.setResizable(false);
        totalCrashes.setCellValueFactory(
                new PropertyValueFactory<Player, String>("crashCount"));
        
        TableColumn lightsRuns = new TableColumn("Průjezd na červenou");
        lightsRuns.setMinWidth(120);
        lightsRuns.setResizable(false);
        lightsRuns.setCellValueFactory(
                new PropertyValueFactory<Player, String>("lightsRuns"));
        
        
        TableColumn minTimes = new TableColumn("Nejlepší kolo");
        minTimes.setMinWidth(75);
        minTimes.setResizable(false);
        minTimes.setCellValueFactory(
                new PropertyValueFactory<Player, String>("fastRun"));
        
        TableColumn maxTimes = new TableColumn("Nejhorší kolo");
        maxTimes.setMinWidth(75);
        maxTimes.setResizable(false);
        maxTimes.setCellValueFactory(
                new PropertyValueFactory<Player, String>("slowRun"));
        
        TableColumn marks = new TableColumn("Hodnocení");
        marks.setMinWidth(70);
        marks.setResizable(false);
        marks.setCellValueFactory(
                new PropertyValueFactory<Player, String>("mark"));
        
        TableColumn dates = new TableColumn("Datum");
        dates.setMinWidth(75);
        dates.setResizable(false);
        dates.setCellValueFactory(
                new PropertyValueFactory<Player, String>("date"));
        
        playersTable.getColumns().addAll(name, marks, totalRuns, avgTime, minTimes, maxTimes, totalCrashes, lightsRuns, dates);
        statGroup.getChildren().addAll(playersTable);
        
        /*
        playersTable.getItems().addAll(ps.getPlayers());*/
    }
}
