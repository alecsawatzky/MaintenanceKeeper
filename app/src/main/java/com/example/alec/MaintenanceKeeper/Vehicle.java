package com.example.alec.MaintenanceKeeper;

/**
 * Created by Alec on 2017-11-06.
 * Represents a Vehicle.
 */

public class Vehicle
{
    private String id;
    private String make;
    private String model;
    private String year;

    public Vehicle() {
    }

    public Vehicle(String make, String model, String year) {
        this.make = make;
        this.model = model;
        this.year = year;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMake() {return make;}

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
}
