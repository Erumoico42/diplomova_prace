/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dipl_project.UI.GUI.TestMenu;

import dipl_project.Dipl_project;
import dipl_project.Simulation.Player;
import dipl_project.UI.UIControlls.UIControll;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author Honza
 */
public class UITestInitMenu {
    private Player player;
    private final Stage secondaryStage;
    private final Stage primaryStage;
    private final UIControll ui;
    public UITestInitMenu(UIControll ui)
    {
        this.ui=ui;
        primaryStage=ui.getPrimaryStage();
        
        HBox center=new HBox();
        center.setAlignment(Pos.CENTER);
        center.setSpacing(10);
        center.setLayoutX(20);
        center.setLayoutY(50);
        Group loginRoot = new Group(center);
        Scene loginScene = new Scene(loginRoot, 350, 150);
        secondaryStage = new Stage();
        secondaryStage.setTitle("Nový uživatel");
        secondaryStage.setScene(loginScene);
        
        
        secondaryStage.show();
        Label lblName=new Label("Jméno:");
        TextField tfName=new TextField();
        tfName.setMaxWidth(110);
        tfName.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode()==KeyCode.ENTER){
                    newPlayer(tfName.getText());
                }
            }
        });
        Button btnEnter=new Button("Ok");
        btnEnter.setMinWidth(110);
        btnEnter.setMaxWidth(110);
        secondaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                Dipl_project.closeApp();
            }
        });
        btnEnter.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                newPlayer(tfName.getText());
            }
        });
        center.getChildren().addAll(lblName,tfName, btnEnter);
    }
    private void newPlayer(String name)
    {
        if(name.equals(""))
        {
            name="Nový uživatel";
        }
        secondaryStage.close();
        player=new Player(name);
        primaryStage.show();
        ui.getRoot().setDisable(false);
    }

    public Player getPlayer() {
        return player;
    }
    
}
