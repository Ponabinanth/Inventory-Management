package org.example.dao;
import org.example.model.Product;
import org.example.util.DBConnection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
class productdaoimpltest {

    private ProductDAO productDAO;
    private final Product TEST_PRODUCT_SIMPLE = new Product(
            "P1", "Laptop", "Electronics", 300000, 10,
            LocalDate.of(2025, 10, 10), "Kabi ltd"
    );

    @BeforeEach
    public void setup() {
        cleanTable();
        productDAO = new ProductDAOImpl();
    }

    private void cleanTable() {
        String sql = "DELETE FROM products";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.err.println("❌ Setup/Cleanup Error: Could not clean table. Check DBConnection settings.");
            e.printStackTrace();
            fail("Database connection failed during setup.");
        }
    }
    @Test
    void testAddProduct_SuccessAndRetrieval() {
        boolean result = productDAO.addProduct(TEST_PRODUCT_SIMPLE);
        assertTrue(result, "Adding the product should return true.");
        Optional<Product> retrieved = productDAO.getProductById(TEST_PRODUCT_SIMPLE.getId  ());
        assertTrue(retrieved.isPresent(), "Product must be successfully retrieved by ID after insertion.");
        assertEquals(TEST_PRODUCT_SIMPLE.getName(), retrieved.get().getName());
        assertEquals(TEST_PRODUCT_SIMPLE.getQuantity(), retrieved.get().getQuantity());
        assertEquals(300000.0, retrieved.get().getPrice(), 0.001);
    }

    @Test
    void testAddProduct_DuplicateIdFailure() {
        productDAO.addProduct(TEST_PRODUCT_SIMPLE);
        boolean result = productDAO.addProduct(TEST_PRODUCT_SIMPLE);
        assertFalse(result, "Adding a product with a duplicate ID should return false due to SQL constraint.");
    }

    @Test
    void testGetProductById_NotFound() {
        Optional<Product> retrieved = productDAO.getProductById("SKU999");
        assertFalse(retrieved.isPresent(), "Should not find a product with a non-existent ID.");
    }

    @Test
    void testUpdateProduct_Success() {
        productDAO.addProduct(TEST_PRODUCT_SIMPLE); // Original price: 300000, quantity: 10

        double newPrice = 315000.99;
        int newQuantity = 5;
        boolean result = productDAO.updateProduct(TEST_PRODUCT_SIMPLE.getId(), newPrice, newQuantity);
        assertTrue(result, "Update operation should return true.");
        Optional<Product> updated = productDAO.getProductById(TEST_PRODUCT_SIMPLE.getId());
        assertTrue(updated.isPresent());
        assertEquals(newPrice, updated.get().getPrice(), 0.001, "Price should be updated.");
        assertEquals(newQuantity, updated.get().getQuantity(), "Quantity should be updated.");
    }

    @Test
    void testUpdateProduct_NotFound() {
        boolean result = productDAO.updateProduct("NON_EXISTENT_ID", 50.0, 50);
        assertFalse(result, "Updating a non-existent product should return false.");
    }

    @Test
    void testDeleteProduct_Success() {
        productDAO.addProduct(TEST_PRODUCT_SIMPLE);
        boolean result = productDAO.deleteProduct(TEST_PRODUCT_SIMPLE.getId());
        assertTrue(result, "Delete operation should return true.");
        Optional<Product> deleted = productDAO.getProductById(TEST_PRODUCT_SIMPLE.getId());
        assertFalse(deleted.isPresent(), "Product should not be found after successful deletion.");
    }

    @Test
    void testDeleteProduct_NonExistent() {
        boolean result = productDAO.deleteProduct("DUMMY_ID_TO_DELETE");
        assertFalse(result, "Deleting a non-existent product should return false.");
    }
    @Test
    void verifySetup_TableIsEmpty() {
        List<Product> products = productDAO.getAllProducts();
        assertTrue(products.isEmpty(), "Table must be empty after @BeforeEach cleanup.");
    }
}
