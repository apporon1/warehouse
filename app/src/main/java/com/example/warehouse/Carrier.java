package com.example.warehouse;

public class Carrier {
    private String id;
    private String name;
    private String phone;
    private String license;

    public Carrier(String id, String name, String phone, String license) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.license = license;
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

    public String getLicense() {
        return license;
    }
}