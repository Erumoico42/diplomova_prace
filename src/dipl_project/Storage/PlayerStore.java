/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dipl_project.Storage;

import dipl_project.Dipl_project;
import dipl_project.Simulation.Player;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Honza
 */
public class PlayerStore {
    private static Document doc;
    private static Element root;
    private List<Player> players=new ArrayList<>();
    private File input;
    public void loadTopList()
    {
        try {
            String path=Dipl_project.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            path=path.substring(0, path.lastIndexOf("/"))+"/topList.xml";
            path=path.replaceAll("/", "\\\\");
            input=new File(path);
            if(input.exists()){
                
                DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
                DocumentBuilder db=dbf.newDocumentBuilder();
                doc = db.parse(input);
                doc.getDocumentElement().normalize();
                loadPlayers();
            }
            
        } catch (URISyntaxException ex) {
            Logger.getLogger(PlayerStore.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(PlayerStore.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(PlayerStore.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PlayerStore.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void loadPlayers()
    {
        NodeList players=doc.getElementsByTagName("player");
        for (int i = 0; i < players.getLength(); i++) {
            Node player=players.item(i);    
            
            String name=player.getAttributes().getNamedItem("name").getNodeValue();
            double avgTime=Double.valueOf(player.getAttributes().getNamedItem("avgTime").getNodeValue());
            int runCount=Integer.valueOf(player.getAttributes().getNamedItem("runCount").getNodeValue());
            int crashCount=Integer.valueOf(player.getAttributes().getNamedItem("crashCount").getNodeValue());
            int redCount=Integer.valueOf(player.getAttributes().getNamedItem("redCount").getNodeValue());
            double fastRun=Double.parseDouble(player.getAttributes().getNamedItem("fastRun").getNodeValue());
            double slowRun=Double.parseDouble(player.getAttributes().getNamedItem("slowRun").getNodeValue());
            String mark=player.getAttributes().getNamedItem("mark").getNodeValue();
            String date=player.getAttributes().getNamedItem("date").getNodeValue();
            Player p=new Player(name, crashCount, runCount, redCount, avgTime, slowRun, fastRun, mark, date);
            this.players.add(p);
        }         
    }
    public List<Player> getPlayers()
    {
        return players;
    }
    public void saveTopList(Player player)
    {
        DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
        DocumentBuilder db;
        try {
            db = dbf.newDocumentBuilder();
            
            if(!input.exists()){
                input.createNewFile();
                doc=db.newDocument();
                root=doc.createElement("root");
                doc.appendChild(root);
            }
            else
            {
                doc = db.parse(input);
                root = doc.getDocumentElement();
                
            }
            if(player!=null)
                savePlayer(player);
            TransformerFactory tfc=TransformerFactory.newInstance();
            Transformer tf=tfc.newTransformer();
            tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            tf.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source=new DOMSource(doc);
            StreamResult result=new StreamResult(input);
            tf.transform(source, result);
            
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(PlayerStore.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PlayerStore.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(PlayerStore.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            Logger.getLogger(PlayerStore.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(PlayerStore.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
    private void savePlayer(Player pla)
    {
        pla.calcMark();
        Element player=doc.createElement("player");

        Attr name=doc.createAttribute("name");
        name.setValue(pla.getName());
        player.setAttributeNode(name);
        
        Attr avgTime=doc.createAttribute("avgTime");
        avgTime.setValue(String.valueOf(pla.getAvgTime()));
        player.setAttributeNode(avgTime);
        
        Attr totalRuns=doc.createAttribute("runCount");
        totalRuns.setValue(String.valueOf(pla.getRunCount()));
        player.setAttributeNode(totalRuns);
        
        Attr crashCount=doc.createAttribute("crashCount");
        crashCount.setValue(String.valueOf(pla.getCrashCount()));
        player.setAttributeNode(crashCount);
        
        Attr redCount=doc.createAttribute("redCount");
        redCount.setValue(String.valueOf(pla.getRedCount()));
        player.setAttributeNode(redCount);
        
        Attr fastRun=doc.createAttribute("fastRun");
        fastRun.setValue(String.valueOf(pla.getFastRun()));
        player.setAttributeNode(fastRun);
        
        Attr slowRun=doc.createAttribute("slowRun");
        slowRun.setValue(String.valueOf(pla.getSlowRun()));
        player.setAttributeNode(slowRun);
        
        Attr mark=doc.createAttribute("mark");
        mark.setValue(String.valueOf(pla.getMark()));
        player.setAttributeNode(mark);
        
        Attr date=doc.createAttribute("date");
        date.setValue(String.valueOf(pla.getDate()));
        player.setAttributeNode(date);
        
        root.appendChild(player);
    }
}
