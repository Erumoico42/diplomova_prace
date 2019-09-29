/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dipl_project.Fuzzy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Honza
 */
public class Variable {
    private List<FuzzySet> sets=new ArrayList<>();
    private final String NAME;
    private final int DISCRETIZATION;
    private final double LOW, MEDIUM,HIGH;
    private double[][] matrixOfTruth;
    private double[] values;
    private Map mapSets=new HashMap();
    public Variable(String NAME, int DISCRETIZATION, double LOW, double MEDIUM, double HIGH) {
        this.NAME = NAME;
        this.DISCRETIZATION = DISCRETIZATION;
        this.LOW = LOW;
        this.HIGH = HIGH;
        this.MEDIUM = MEDIUM;
    }

    public List<FuzzySet> getSets() {
        return sets;
    }

    public String getNAME() {
        return NAME;
    }

    public int getDISCRETIZATION() {
        return DISCRETIZATION;
    }

    public double getLOW() {
        return LOW;
    }

    public double getMEDIUM() {
        return MEDIUM;
    }

    public double getHIGH() {
        return HIGH;
    }

    public Map getMapSets() {
        return mapSets;
    }
    
    public void addSet(FuzzySet set) {
        this.sets.add(set);
        mapSets.put(set.getName(), set);
        set.setMotherVariable(this);
    }
    public double[] getValues()
    {
        return values;
    }
    public void createMatrixOfTruth()
    {
        values=new double[DISCRETIZATION];
        double onePiece=(HIGH-LOW)/DISCRETIZATION;
        double ap=LOW;
        for (int i = 0; i < DISCRETIZATION; i++) {
            values[i]=ap;
            ap+=onePiece;
        }       

        matrixOfTruth=new double[sets.size()][DISCRETIZATION];
        for (int i = 0; i < sets.size(); i++) {
            double actPiece=LOW;
            FuzzySet fs=sets.get(i);
            for (int j = 0; j < DISCRETIZATION; j++) {
                matrixOfTruth[i][j]=getTruthValue(fs, actPiece);
                actPiece+=onePiece;
            }
        }
    }
    public double getTruthValue(FuzzySet fs, double inputValue)
    {
        double fsLeftSupp=fs.getLeftSupp();
        double fsLeftCore=fs.getLeftCore();
        double fsRightCore=fs.getRightCore();
        double fsRightSupp=fs.getRightSupp();
        double truthValue=0;
        if(inputValue<fsLeftSupp || inputValue>fsRightSupp)
            truthValue=0;
        else if(inputValue > fsLeftSupp && inputValue < fsLeftCore){
            truthValue=calculateTruthValue(fsLeftCore, fsLeftSupp, inputValue-fsLeftSupp);
        }
        else if(inputValue>=fsLeftCore && inputValue<=fsRightCore){
            truthValue=1;
        }
        else if(inputValue>fsRightCore && inputValue<fsRightSupp){
            truthValue=(double)Math.round((1-calculateTruthValue(fsRightSupp, fsRightCore, inputValue-fsRightCore))*1000)/1000;
        }
        return truthValue;
    }
    private double calculateTruthValue(double value1, double value2, double actPiece)
    {
        double dVal=value1-value2;
        double value=(double)Math.round((actPiece/dVal)*1000)/1000;
        return value;
    }
    public double[][] getMatrixOfTruth()
    {
        return matrixOfTruth;
    }
    
}
