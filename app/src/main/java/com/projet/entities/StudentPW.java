package com.projet.entities;

public class StudentPW {

    private int id;
    private String mesureAngle1;
    private String mesureAngle2;

    private PW pw;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMesureAngle1() {
        return mesureAngle1;
    }

    public void setMesureAngle1(String mesureAngle1) {
        this.mesureAngle1 = mesureAngle1;
    }

    public String getMesureAngle2() {
        return mesureAngle2;
    }

    public void setMesureAngle2(String mesureAngle2) {
        this.mesureAngle2 = mesureAngle2;
    }

    public PW getPw() {
        return pw;
    }

    public void setPw(PW pw) {
        this.pw = pw;
    }
}
