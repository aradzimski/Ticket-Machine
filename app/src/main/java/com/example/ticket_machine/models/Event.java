package com.example.ticket_machine.models;

public class Event {

    public String Id;
    public String Name;
    public String Description;
    public String Price;
    public String DateOfStart;
    public String DateOfEnd;

    public Event() {
    }

    public Event(String id, String name, String description, String price, String dateOfStart, String dateOfEnd) {
        Id = id;
        Name = name;
        Description = description;
        Price = price;
        DateOfStart = dateOfStart;
        DateOfEnd = dateOfEnd;
    }
}