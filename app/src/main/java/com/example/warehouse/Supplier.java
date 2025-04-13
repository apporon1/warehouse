package com.example.warehouse;

public class Supplier {
    private String id;
    private String name;
    private String phone;
    private String email;
    private String inn;

    public Supplier(String id, String name, String phone, String email, String inn) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.inn = inn;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getInn() {
        return inn;
    }
}