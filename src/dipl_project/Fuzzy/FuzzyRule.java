/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dipl_project.Fuzzy;

/**
 *
 * @author Honza
 */
public class FuzzyRule {
    private FuzzySet[] inputSets; 
    private FuzzySet output;
    private boolean fired=false;
    public FuzzyRule(FuzzySet outputSet, FuzzySet...inputSets) {
        this.output = outputSet;
        this.inputSets=inputSets; 
    }

    public FuzzySet[] getInputs() {
        return inputSets;
    }
    public FuzzySet getOutput() {
        return output;
    }

    public boolean isFired() {
        return fired;
    }

    public void setFired(boolean fired) {
        this.fired = fired;
    }
    
}
