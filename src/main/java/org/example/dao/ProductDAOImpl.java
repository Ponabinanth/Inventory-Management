package org.example.dao;

import org.example.model.Product;
import org.example.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductDAOImpl implements ProductDAO {

    public ProductDAOImpl()
    {
        createTable();
    }

    private Connection connect() throws SQLException {
        return DBConnection.getConnection();
    }
    private void createTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS products (
                    productId VARCHAR(36) PRIMARY KEY,
                    productName VARCHAR(255) NOT NULL,
                    category VARCHAR(100),\s
                    price REAL NOT NULL,
                    quantity INTEGER NOT NULL,
                    manufacturingDate DATE,
                    supplier VARCHAR(100)
                );""";
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("❌ Error creating products table: " + e.getMessage());
        }
    }
    @Override
    public boolean addProduct(Product product) {
        String sql = "INSERT INTO products(productId, productName, category, price, quantity, manufacturingDate, supplier) VALUES(?,?,?,?,?,?,?)";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, product.getId());
            pstmt.setString(2, product.getName());
            pstmt.setString(3, product.getCategory());
            pstmt.setDouble(4, product.getPrice());
            pstmt.setInt(5, product.getQuantity());
            // Convert Java LocalDate to SQL Date
            pstmt.setDate(6, Date.valueOf(product.getLastUpdated()));
            pstmt.setString(7, product.getSupplier());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("❌ Error adding product: " + e.getMessage());
            return false;
        }
    }
    @Override
    public Optional<Product> getProductById(String id) {
        String sql = "SELECT * FROM products WHERE productId = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(new Product(
                        rs.getString("productId"),
                        rs.getString("productName"),
                        rs.getString("category"),
                        rs.getDouble("price"),
                        rs.getInt("quantity"),
                        // Convert SQL Date back to Java LocalDate
                        rs.getDate("manufacturingDate").toLocalDate(),
                        rs.getString("supplier")
                ));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error getting product: " + e.getMessage());
        }
        return Optional.empty();
    }
    @Override
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products";
        try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                products.add(new Product(
                        rs.getString("productId"),
                        rs.getString("productName"),
                        rs.getString("category"),
                        rs.getDouble("price"),
                        rs.getInt("quantity"),
                        rs.getDate("manufacturingDate").toLocalDate(),
                        rs.getString("supplier")
                ));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error retrieving all products: " + e.getMessage());
        }
        return products;
    }

    /**
     * Updates the price and quantity of an existing product.
     */
    @Override
    public boolean updateProduct(String id, double newPrice, int newQuantity) {
        String sql = "UPDATE products SET price = ?, quantity = ? WHERE productId = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, newPrice);
            pstmt.setInt(2, newQuantity);
            pstmt.setString(3, id);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error updating product: " + e.getMessage());
            return false;
        }
    }
    @Override
    public boolean deleteProduct(String id) {
        String sql = "DELETE FROM products WHERE productId = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error deleting product: " + e.getMessage());
            return false;
        }
    }
}
