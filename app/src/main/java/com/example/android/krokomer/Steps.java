package com.example.android.krokomer;

public class Steps {

    private int ID;
    private String datum;
    private String steps;

    public Steps(int ID, String datum, String steps) {
        this.ID = ID;
        this.datum = datum;
        this.steps = steps;
    }

    public int getID() {
        return ID;
    }

    public String getDatum() {
        return datum;
    }

    public String getSteps() {
        return steps;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setDatum(String datum) {
        this.datum = datum;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }

}
