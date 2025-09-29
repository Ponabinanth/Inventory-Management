package org.example.model;
import java.time.LocalDate;
public class Product {
    private final String productId;
    private final String productName;
    private double price;
    private int quantity;
    private final LocalDate manufacturingDate;
    private final String supplier;

    public Product(String productId, String productName, double price, int quantity, LocalDate manufacturingDate, String supplier) {
        this.productId = productId;
        this.productName = productName;
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

    // --- Setters (Only for mutable fields: Price and Quantity) ---

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
                ", price=" + price +
                ", quantity=" + quantity +
                ", manufacturingDate=" + manufacturingDate +
                ", supplier='" + supplier + '\'' +
                '}';
    }
}

