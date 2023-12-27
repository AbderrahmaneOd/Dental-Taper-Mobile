package com.projet.entities;

public class StudentPW {

    private int id;
    private Double angleInterneG;
    private Double angleInterneD;
    private Double angleExterneG;
    private Double angleExterneD;
    private Double angledepouilleG;
    private Double angledepouilleD;
    private Double angleConvergence;

    private PW pw;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Double getAngleInterneG() {
        return angleInterneG;
    }

    public void setAngleInterneG(Double angleInterneG) {
        this.angleInterneG = angleInterneG;
    }

    public Double getAngleInterneD() {
        return angleInterneD;
    }

    public void setAngleInterneD(Double angleInterneD) {
        this.angleInterneD = angleInterneD;
    }

    public Double getAngleExterneG() {
        return angleExterneG;
    }

    public void setAngleExterneG(Double angleExterneG) {
        this.angleExterneG = angleExterneG;
    }

    public Double getAngleExterneD() {
        return angleExterneD;
    }

    public void setAngleExterneD(Double angleExterneD) {
        this.angleExterneD = angleExterneD;
    }

    public Double getAngledepouilleG() {
        return angledepouilleG;
    }

    public void setAngledepouilleG(Double angledepouilleG) {
        this.angledepouilleG = angledepouilleG;
    }

    public Double getAngledepouilleD() {
        return angledepouilleD;
    }

    public void setAngledepouilleD(Double angledepouilleD) {
        this.angledepouilleD = angledepouilleD;
    }

    public Double getAngleConvergence() {
        return angleConvergence;
    }

    public void setAngleConvergence(Double angleConvergence) {
        this.angleConvergence = angleConvergence;
    }

    public PW getPw() {
        return pw;
    }

    public void setPw(PW pw) {
        this.pw = pw;
    }
}
