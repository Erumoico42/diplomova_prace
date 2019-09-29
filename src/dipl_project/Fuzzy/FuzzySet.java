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
public class FuzzySet {
    private String name;
    private double leftSupp, rightSupp, leftCore, rightCore;
    private Variable motherVariable;
    private double alfaCut=0;
    public FuzzySet(String name, double leftSupp, double leftCore, double rightCore, double rightSupp) {
        this.name = name;
        this.leftSupp = leftSupp;
        this.rightSupp = rightSupp;
        this.leftCore = leftCore;
        this.rightCore = rightCore;
    }

    public FuzzySet(String name, double leftSupp, double center, double rightSupp) {
        this.name = name;
        this.leftSupp = leftSupp;
        this.rightSupp = rightSupp;
        this.leftCore = center;
        this.rightCore=center;
    }

    public double getAlfaCut() {
        return alfaCut;
    }

    public void setAlfaCut(double alfaCut) {
        this.alfaCut = alfaCut;
    }
    
    public Variable getMotherVariable() {
        return motherVariable;
    }

    public void setMotherVariable(Variable motherVariable) {
        this.motherVariable = motherVariable;
    }

    public String getName() {
        return name;
    }

    public double getLeftSupp() {
        return leftSupp;
    }

    public double getRightSupp() {
        return rightSupp;
    }

    public double getLeftCore() {
        return leftCore;
    }

    public double getRightCore() {
        return rightCore;
    }
    
    
}
