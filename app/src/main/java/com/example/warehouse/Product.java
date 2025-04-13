package com.example.warehouse;

public class Product {
    private String id;
    private String name;
    private double price;
    private int quantity;
    private String barcode;

    public Product(String id, String name, double price, int quantity, String barcode) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.barcode = barcode;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getBarcode() {
        return barcode;
    }
}