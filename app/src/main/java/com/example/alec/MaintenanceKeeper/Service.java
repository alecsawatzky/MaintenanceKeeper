package com.example.alec.MaintenanceKeeper;

/**
 * Created by Alec on 2017-11-06.
 * Represents a Vehicle.
 */

public class Service
{
    private String name;
    private String date;


    public Service()
    {
    }

    public Service(String name, String date)
    {
        this.name = name;
        this.date = date;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

}

