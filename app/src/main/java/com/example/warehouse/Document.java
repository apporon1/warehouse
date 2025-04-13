package com.example.warehouse;

import java.util.List;

public class Document {
    private String id;
    private String type;
    private String date;
    private String warehouseId;
    private String status;
    private List<String> products;

    public Document(String id, String type, String date, String warehouseId, String status, List<String> products) {
        this.id = id;
        this.type = type;
        this.date = date;
        this.warehouseId = warehouseId;
        this.status = status;
        this.products = products;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getDate() {
        return date;
    }

    public String getWarehouseId() {
        return warehouseId;
    }

    public String getStatus() {
        return status;
    }

    public List<String> getProducts() {
        return products;
    }
}