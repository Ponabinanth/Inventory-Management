package org.example.model;
public class ProductValidator {

    public static void validateProductId(int productId) throws IllegalArgumentException {
        if (productId <= 0) {
            throw new IllegalArgumentException("❌ Product ID must be a positive number.");
        }
    }
    public static void validatePrice(double price) throws IllegalArgumentException {
        if (price < 0.0) {
            throw new IllegalArgumentException("❌ Price cannot be negative.");
        }
    }
    public static void validateQuantity(int quantity) throws IllegalArgumentException {
        if (quantity < 0) {
            throw new IllegalArgumentException("❌ Quantity cannot be negative.");
        }
    }
}
