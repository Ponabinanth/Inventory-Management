package org.example.model;
import java.time.LocalDate;

public class Product {
    private final String id;
    private final String name;
    private final String category;
    private double price;
    private int quantity;
    private final LocalDate lastUpdated;
    private final String supplier;

    public Product(String id, String name, String category, double price, int quantity, LocalDate lastUpdated, String supplier) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
        this.lastUpdated = lastUpdated;
        this.supplier = supplier;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public LocalDate getLastUpdated() { return lastUpdated; }
    public String getSupplier() { return supplier; }
    public void setPrice(double price) { this.price = price; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}