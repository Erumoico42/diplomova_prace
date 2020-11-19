/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dipl_project.Storage;

import dipl_project.Dipl_project;
import dipl_project.Roads.Connect;
import dipl_project.Roads.MyCurve;
import dipl_project.Roads.RoadSegment;
import dipl_project.UI.EditationControll;
import java.util.List;
import java.util.Map;
import javafx.util.Pair;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Honza
 */
public class EditationStore {

    private final Document doc;
    private final Element root;
    public EditationStore(Document doc, Element root) {
        this.doc = doc;
        this.root = root;
    }
    public void saveEditation()
    {
        Element edit=doc.createElement("editation"); 

        Attr zoomRatio=doc.createAttribute("zoomRatio");
        zoomRatio.setValue(String.valueOf(EditationControll.getZoomRatio()));
        edit.setAttributeNode(zoomRatio);

        Attr resetRatio=doc.createAttribute("resetRatio");
        resetRatio.setValue(String.valueOf(EditationControll.getResetRatio()));
        edit.setAttributeNode(resetRatio);
        
        Attr zoomCount=doc.createAttribute("zoomCount");
        zoomCount.setValue(String.valueOf(EditationControll.getZoomCount()));
        edit.setAttributeNode(zoomCount);
        root.appendChild(edit);
        
    }
    public void loadEditation()
    {
        NodeList edit=doc.getElementsByTagName("editation");
        Node editation=edit.item(0);
        
        double zoomRatio= Double.parseDouble(editation.getAttributes().getNamedItem("zoomRatio").getNodeValue());
        double resetRatio= Double.parseDouble(editation.getAttributes().getNamedItem("resetRatio").getNodeValue());
        int zoomCount= Integer.parseInt(editation.getAttributes().getNamedItem("zoomCount").getNodeValue());
        EditationControll.setZoomRatio(zoomRatio);
        EditationControll.setResetRatio(resetRatio);
        EditationControll.setZoomCount(zoomCount);
    }
}
