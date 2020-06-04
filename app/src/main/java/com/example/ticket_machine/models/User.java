package com.example.ticket_machine.models;

public class User {

    private String Name;
    private String LastName;
    private String Email;
    private String Password;
    private String Permission_level;

    public User(){

    }

    public User(String name, String lastName, String email) {
        Name = name;
        LastName = lastName;
        Email = email;
    }

    public User(String id, String name, String lastName, String email, String password, String permission_level) {
        this.Id = id;
        this.Name = name;
        this.LastName = lastName;
        this.Email = email;
        this.Password = password;
        this.Permission_level = permission_level;
    }

    private String Id;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getPermission_level() {
        return Permission_level;
    }

    public void setPermission_level(String permission_level) {
        Permission_level = permission_level;
    }


}
