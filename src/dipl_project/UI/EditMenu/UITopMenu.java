/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dipl_project.UI.EditMenu;

import dipl_project.TrafficLights.TrafficLight;
import dipl_project.Dipl_project;
import dipl_project.Roads.MyCurve;
import dipl_project.Roads.RoadCreator;
import dipl_project.UI.BackgroundControll;
import dipl_project.UI.EditationControll;
import dipl_project.UI.UIControll;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.stage.WindowEvent;
import sun.plugin2.os.windows.Windows;

/**
 *
 * @author Honza
 */
public class UITopMenu {
    
    
    private Image imgTLIcon=new Image(Dipl_project.class.getResource("Resources/menuIcons/trafficLightIcon.png").toString());
    private Image imgSwitchGreen=new Image(Dipl_project.class.getResource("Resources/trafficLights/switchGreen.png").toString());
    private Image imgSwitchRed=new Image(Dipl_project.class.getResource("Resources/trafficLights/switchRed.png").toString());
    private Image imgSwitchOrange=new Image(Dipl_project.class.getResource("Resources/trafficLights/switchOrange.png").toString());
    
    
    
    private MenuItem newFile;
    private MenuItem openFile;
    private MenuItem saveFile;
    private Group streetGroup, root;
    
    private ToggleGroup createVehicleGroup;
    private UIControll ui;
    private CheckMenuItem showRoads;
    private AnchorPane ap;
    private RadioMenuItem radioCar;
    private RadioMenuItem radioTram;
    private MenuItem addBackground;
    private MenuItem removeBackground;
    private CheckMenuItem editBackground;
    private Menu background;
    private Menu streetType;
    public UITopMenu(Group root, UIControll ui)
    {
        this.ui=ui;
        this.root=root;
        
        initMenu();
        
        initMenuButtons();
        //initBackgroundMenu();
    }
    private void initMenu()
    {
        VBox top=new VBox();
        MenuBar menu=new MenuBar();
        Menu file=new Menu("Soubor");
        
        newFile=new MenuItem("Nový");
        openFile=new MenuItem("Otevřít");
        saveFile=new MenuItem("Uložit");
        file.getItems().addAll(newFile, openFile, saveFile);
        
        Menu edit=new Menu("Upravit");
        showRoads=new CheckMenuItem("Zobrazit cesty");
        
        streetType=new Menu("Druh cesty");
        radioCar=new RadioMenuItem("Automobil");
        radioCar.setSelected(true);
        radioTram=new RadioMenuItem("Tramvaj");
        streetType.getItems().addAll(radioCar,radioTram);
        createVehicleGroup=new ToggleGroup();
        createVehicleGroup.getToggles().addAll(radioCar,radioTram);
        edit.getItems().addAll(showRoads,streetType);
        
        background=new Menu("Pozadí");
        addBackground=new MenuItem("Vložit pozadí");
        editBackground=new CheckMenuItem("Upravit pozadí");
        removeBackground=new MenuItem("Odebrat pozadí");
        background.getItems().addAll(addBackground,editBackground,removeBackground);
        
        
        menu.getMenus().addAll(file,edit,background);
        
        ap=new AnchorPane();
        ap.setPrefSize(1200, 0);
        Pane pane=new Pane();
        pane.setPrefSize(100, 0);
        ap.getChildren().add(pane);
        
        top.getChildren().addAll(menu,ap);
        root.getChildren().add(top);
    }
    public void updateMenuSize(double width)
    {
        
        ap.setPrefWidth(width);
    }
    
    private void initMenuButtons()
    {
        saveFile.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Dipl_project.getStc().saveFile();
            }
        });

        openFile.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Dipl_project.getStc().loadFile();
            }
        });

        newFile.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Dipl_project.getDC().cleanAll();
                EditationControll.setDefRatio();
            }
        });
        
        
        showRoads.setSelected(true);
        showRoads.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                refreshShowRoads();
            }
        });
        radioCar.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Dipl_project.getUI().setIsTramCreating(false);
            }
        });
        radioTram.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Dipl_project.getUI().setIsTramCreating(true);
            }
        });
        initBackgroundMenu();
    }
    
    public void enableChangeGenerate(boolean enable, MyCurve curve)
    {
        
    }
    
    
    
    public void refreshShowRoads()
    {
        ui.showRoads(showRoads.isSelected());
    }
    public void initBackgroundMenu()
    {
        
        editBackground.setDisable(true);
        editBackground.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ui.setMoveStatus(1);
                ui.getMoveCanvas().setVisible(editBackground.isSelected());
            }
        });
        addBackground.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                BackgroundControll.loadImage();
            }
        });
        enableRemoveBG(false);
        removeBackground.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                setEditBackground(false);
                
                BackgroundControll.removeBG();
                enableRemoveBG(false);
            }
        });
    }
    public void enableRemoveBG(boolean enable)
    {
        removeBackground.setDisable(!enable);
        editBackground.setDisable(!enable);
    }
    public void setEditBackground(boolean edit)
    {
        ui.getMoveCanvas().setVisible(edit);
        editBackground.setSelected(edit);
    }
    public void setEditDesign(boolean edit)
    {
        streetType.setDisable(edit);
        background.setDisable(edit);
    }

    public CheckMenuItem getEditBackground() {
        return editBackground;
    }
    public MenuItem getBtnRemoveBackhround() {
        return removeBackground;
    }
 
}
