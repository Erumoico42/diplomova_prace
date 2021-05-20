/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dipl_project.Fuzzy;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Honza
 */
public class RulesCalculator {

    private Variable outputVariable;
    private List<FuzzySet> firedOutputs;
    private List<FuzzyRule> rules;
    public RulesCalculator(Variable outputVariable, List<FuzzyRule> rules) {
        this.outputVariable=outputVariable;
        outputVariable.createMatrixOfTruth(); 
        this.rules=rules;
    }
    
    //urceni aktivovanych pravidel podle zadanych hodnot
    private void setValues(double...inputValues)
    {
        firedOutputs=new ArrayList<>();
        for (FuzzyRule rule : rules) {
            FuzzySet[] inputSets=rule.getInputs();
            boolean ruleFired=true;
            double totalTruthValue=1;
            for (int i = 0; i < inputSets.length; i++) {
                double truthValue=inputSets[i].getMotherVariable().getTruthValue(inputSets[i], inputValues[i]);
                if(truthValue==0)
                    ruleFired=false;
                else if(totalTruthValue>truthValue)
                    totalTruthValue=truthValue;
            }
            rule.setFired(ruleFired);
            if(ruleFired && !firedOutputs.contains(rule.getOutput()))
                firedOutputs.add(rule.getOutput());
            if(ruleFired && rule.getOutput().getAlfaCut()<totalTruthValue)
                rule.getOutput().setAlfaCut(totalTruthValue);
        }
               
    }
    public double calculateByValues(double...inputValues)
    {
        setValues(inputValues);
        return defuzzificationCOG();
    }
    
    //defuzzyfikace vypoctenych hodnot
    private double defuzzificationCOG()
    {
        double[]valuesMatrix=outputVariable.getValues();
        double[][] totalTruthMatrix=outputVariable.getMatrixOfTruth();
        double[][] truthMatrix=new double[firedOutputs.size()][valuesMatrix.length];
        int tmId=0;
        for (int i = 0; i < outputVariable.getSets().size(); i++) {
            FuzzySet set=outputVariable.getSets().get(i);
            for (FuzzySet firedOutput : firedOutputs) {
                if(firedOutput.getName().equals(set.getName()))
                {
                    for (int j = 0; j < totalTruthMatrix[i].length; j++) {
                        double truthValMatrix=totalTruthMatrix[i][j];
                        if(totalTruthMatrix[i][j]>firedOutput.getAlfaCut())
                            truthValMatrix=firedOutput.getAlfaCut();
                        truthMatrix[tmId][j]=truthValMatrix;
                    }
                    tmId++;
                }
            } 
        }
        
        double truthSum=0;
        double truthValueSum=0;
        for (int i = 0; i < valuesMatrix.length; i++) {
            double maxTruthValueMatrix=0;
            for (int j = 0; j < truthMatrix.length; j++) {
                double truthValMatrix=truthMatrix[j][i];
                if(truthValMatrix>maxTruthValueMatrix)
                    maxTruthValueMatrix=truthValMatrix;
            }
            truthValueSum+=maxTruthValueMatrix*valuesMatrix[i];
            truthSum+=maxTruthValueMatrix;
        }
        return (double)Math.round((truthValueSum/truthSum)*1000)/1000;
    }

}
