/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dipl_project.Fuzzy;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Honza
 */
public class RuleBaseReader {
    private Variable actualVariable;
    private String name="", nameTerm="";
    private double low=0, medium=0, high=0;
    private int discretization=0;
    private boolean newVariable=false, newUserTerm=false, newRule=false, outputVar=false;
    private List<Variable> variables=new ArrayList<>();
    private List<FuzzyRule> fuzzyRules=new ArrayList<>();
    private Variable outputVariable;
    public RuleBaseReader(InputStream path) {
        try(BufferedReader br = new BufferedReader(new InputStreamReader(path))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
                checkLine(line);
            }
        }   catch (FileNotFoundException ex) {
            Logger.getLogger(RuleBaseReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RuleBaseReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void checkLine(String line)
    {
        
        if(line!=null)
        {
            String[] parts=line.split("=");
            String lab=parts[0].replace(" ", "");
            if(lab.matches("[a-zA-Z]*Variable[0-9]*") && !lab.matches("End_[a-zA-Z]*Variable[0-9]*"))
            {
                if(lab.matches("SucVariable[0-9]*") && !lab.matches("End_SucVariable[0-9]*"))
                    outputVar=true;
                newVariable=true;
            }
            if(newVariable)
            {
                switch(lab)
                {
                    case "name":
                    {
                        name=parts[1];
                        break;
                    }
                    case "context":
                    {
                        String[] cont=parts[1].replace("<", "").replace(">", "").split(",");
                        if(cont.length==5)
                        {
                            low=Double.parseDouble(cont[0]);
                            medium=Double.parseDouble(cont[2]);
                            high=Double.parseDouble(cont[4]);
                        }
                        else if(cont.length==3)
                        {
                            low=Double.parseDouble(cont[0]);
                            medium=Double.parseDouble(cont[1]);
                            high=Double.parseDouble(cont[2]);
                        }
                        break;
                    }
                    case "discretization":
                    {
                        discretization = Integer.parseInt(parts[1]);
                        break;
                    }
                    case "discretization_left":
                    {
                        discretization += Integer.parseInt(parts[1]);
                        break;
                    }
                }
            }
            if(lab.matches("UserTerm") && !lab.matches("End_UserTerm"))
            {
                if(newVariable)
                {
                    actualVariable=new Variable(name, discretization, low, medium, high);
                    if(!outputVar)
                        variables.add(actualVariable);
                    else
                        outputVariable=actualVariable;
                }
                
                outputVar=false;
                newVariable=false;
                newUserTerm=true;
            }
            if(newUserTerm)
            {
                switch(lab)
                {
                    case "name":
                    {
                        nameTerm=parts[1];
                        break;
                    }
                    case "parameters":
                    {
                        String[] cont=parts[1].split(" ");
                        FuzzySet actualSet;
                        if(cont.length==5)
                        {
                            
                            actualSet=new FuzzySet(nameTerm, Double.parseDouble(cont[1]), Double.parseDouble(cont[2]), Double.parseDouble(cont[3]), Double.parseDouble(cont[4]));
                            actualVariable.addSet(actualSet);
                        }
                        else
                        {
                            actualSet=new FuzzySet(nameTerm, Double.parseDouble(cont[1]), Double.parseDouble(cont[2]), Double.parseDouble(cont[3]));
                            actualVariable.addSet(actualSet);
                        }
                        
                        break;
                    }
                }
            }
            if(!lab.matches("UserTerm") && lab.matches("End_UserTerm"))
                newUserTerm=false;
            if(lab.matches("RULES") && !lab.matches("END_RULES"))
                newRule=true;
            if(newRule)
            {
                String[]rules=line.split("\\|");
                if(rules.length>1)
                {
                    String output=rules[1].replace("\"", "").replaceFirst(" ", "");
                    String[]inputs=rules[0].split("\" \"");
                    FuzzySet[]inputSets=new FuzzySet[inputs.length];
                    inputs[0]= inputs[0].replaceFirst(" ", "");
                    inputs[inputs.length-1]= inputs[inputs.length-1].substring(0, inputs[inputs.length-1].length()-1);
                    for (int i = 0; i < inputs.length; i++) {
                        String fsName=inputs[i].replace("\"", "");
                        inputSets[i]=(FuzzySet)variables.get(i).getMapSets().get(fsName);
                    }
                    FuzzySet fs=(FuzzySet)outputVariable.getMapSets().get(output);
                    FuzzyRule fr=new FuzzyRule(fs, inputSets);
                    fuzzyRules.add(fr);
                }
            }
        }
    }

    public List<Variable> getVariables() {
        return variables;
    }

    public List<FuzzyRule> getFuzzyRules() {
        return fuzzyRules;
    }

    public Variable getOutputVariable() {
        return outputVariable;
    }
    
    
}
