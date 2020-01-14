/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dipl_project.Storage;

import dipl_project.UI.BackgroundControll;
import java.awt.Point;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Honza
 */
public class BackgroundStore {

    private Document doc;
    private Element root;
    public BackgroundStore(Document doc, Element root) {
        this.doc=doc;
        this.root=root;
    }
    
    public void saveBackground()
    {
        String bgSource=BackgroundControll.getBgSource();
        double bgWidth=BackgroundControll.getWidth();
        double bgHeight=BackgroundControll.getHeight();
        double layoutX=BackgroundControll.getLayoutX();
        double layoutY=BackgroundControll.getLayoutY();
        double rotate=BackgroundControll.getAngle();
        double resRat=BackgroundControll.getResizeRatio();
            Element background=doc.createElement("background");
            Attr isnull=doc.createAttribute("isNull");
            isnull.setValue("false");
            Attr bgs=doc.createAttribute("bgSource");
            Attr width=doc.createAttribute("width");
            Attr height=doc.createAttribute("height");
            Attr pos=doc.createAttribute("position");     
            Attr rot=doc.createAttribute("rotation");      
            Attr resRatio=doc.createAttribute("resRatio");   
            bgs.setValue(bgSource);
            if(bgSource!=null)
            {
                width.setValue(String.valueOf((int)bgWidth));
                height.setValue(String.valueOf((int)bgHeight));
                rot.setValue(String.valueOf((int)rotate));
                pos.setValue(String.valueOf((int)layoutX+","+(int)layoutY));
                resRatio.setValue(String.valueOf((double)resRat));

            }
            else
            {
                width.setValue("null");
                height.setValue("null");
                rot.setValue("null");
                pos.setValue(String.valueOf("0,0"));
                resRatio.setValue(String.valueOf("0,0"));
                isnull.setValue("true");
            }
            background.setAttributeNode(bgs);
            background.setAttributeNode(width);
            background.setAttributeNode(height);
            background.setAttributeNode(pos);
            background.setAttributeNode(rot);
            background.setAttributeNode(resRatio);
            background.setAttributeNode(isnull); 
            root.appendChild(background);
    }
    public void loadBackground()
    {
        
            NodeList bg=doc.getElementsByTagName("background");
            Node background=bg.item(0);
            boolean isnull= Boolean.parseBoolean(background.getAttributes().getNamedItem("isNull").getNodeValue());
            
            if(!isnull){
                String source= background.getAttributes().getNamedItem("bgSource").getNodeValue();
                String width=background.getAttributes().getNamedItem("width").getNodeValue();
                String height=background.getAttributes().getNamedItem("height").getNodeValue();
                String angle=background.getAttributes().getNamedItem("rotation").getNodeValue();
                String resRatio=background.getAttributes().getNamedItem("rotation").getNodeValue();
                String p= background.getAttributes().getNamedItem("position").getNodeValue();
                String[] pos=p.split(",");
                Point position=new Point(Integer.parseInt(pos[0]),Integer.parseInt(pos[1]));
                Image bgImage=new Image(source);
                if(!bgImage.isError())
                {
                     BackgroundControll.setBackground(position.getX(), position.getY(), Double.valueOf(angle),  Double.valueOf(width), Double.valueOf(height),Double.valueOf(resRatio), source);  
                     dipl_project.Dipl_project.getUI().setEditBackground(false);
                }
        }
    }
}
