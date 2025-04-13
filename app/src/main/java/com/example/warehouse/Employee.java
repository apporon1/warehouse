package com.example.warehouse;

public class Employee {
    private String id;
    private String fullName;
    private String position;
    private String phone;

    public Employee(String id, String fullName, String position, String phone) {
        this.id = id;
        this.fullName = fullName;
        this.position = position;
        this.phone = phone;
    }

    public String getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPosition() {
        return position;
    }

    public String getPhone() {
        return phone;
    }
}