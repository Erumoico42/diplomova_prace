/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dipl_project.Storage;

import dipl_project.Dipl_project;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Honza
 */
public class SleeperStore {
    public void loadScreenSaverConfig()
    {
        
        try {
                String path=Dipl_project.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
                path=path.substring(0, path.lastIndexOf("/"))+"/sleeperConfig.txt";
                path=path.replaceAll("/", "\\\\");
                
                String tempPath=generateTemplatePath(path);
                System.out.println(tempPath);
                if(tempPath!=null && !tempPath.equals(""))
                {
                    
                    Dipl_project.getStc().loader(new File(tempPath));
                    Dipl_project.getSc().startVehicleGenerator();
                    Dipl_project.getTlc().startTrafficLights();
                }
                else
                    Dipl_project.closeApp();
                
            } catch (URISyntaxException ex) {
                Logger.getLogger(SleeperStore.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
    private String generateTemplatePath(String path)
    {
        List<String> paths=new ArrayList<>();
        File pathFile=new File(path);
        if(pathFile.exists()){
            try (BufferedReader br = new BufferedReader(new FileReader(pathFile))) {
                String line;
                while ((line = br.readLine()) != null) {
                   paths.add(line);
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(SleeperStore.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(SleeperStore.class.getName()).log(Level.SEVERE, null, ex);
            }
            if(!paths.isEmpty()){
                
                int rnd=(int)(Math.random()*paths.size());
                return paths.get(rnd);
            }
        }
        return null;
    }
}
