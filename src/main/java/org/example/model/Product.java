package org.example.model;

import java.time.LocalDate;

public class Product {
    private final String productId;
    private final String productName;
    private final String category; // 👈 NEW FIELD
    private double price;
    private int quantity;
    private final LocalDate manufacturingDate;
    private final String supplier;

    public Product(String productId, String productName, String category, double price, int quantity, LocalDate manufacturingDate, String supplier) {
        this.productId = productId;
        this.productName = productName;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
        this.manufacturingDate = manufacturingDate;
        this.supplier = supplier;
    }
    public String getProductId() {
        return productId;
    }
    public String getProductName() {
        return productName;
    }
    public String getCategory() { // 👈 NEW GETTER
        return category;
    }
    public double getPrice() {
        return price;
    }
    public int getQuantity() {
        return quantity;
    }
    public LocalDate getManufacturingDate() {
        return manufacturingDate;
    }
    public String getSupplier() {
        return supplier;
    }
    public void setPrice(double price) {
        this.price = price;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    @Override
    public String toString() {
        return "Product{" +
                "productId='" + productId + '\'' +
                ", productName='" + productName + '\'' +
                ", category='" + category + '\'' + // 👈 Added to output
                ", price=" + price +
                ", quantity=" + quantity +
                ", manufacturingDate=" + manufacturingDate +
                ", supplier='" + supplier + '\'' +
                '}';
    }
}