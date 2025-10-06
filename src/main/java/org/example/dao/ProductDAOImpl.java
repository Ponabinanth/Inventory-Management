package org.example.dao;

import org.example.model.Product;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class ProductDAOImpl implements ProductDAO {
    private static final String DB_URL = "jdbc:sqlite:inventory.db";

    public ProductDAOImpl() {
        try {
            Class.forName("org.sqlite.JDBC");
            createTable();
        } catch (ClassNotFoundException e) {
            System.err.println("❌ SQLite JDBC driver not found.");
            e.printStackTrace();
        }
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    // 🔑 UPDATED: Added category column
    private void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS products (\n"
                + "    productId TEXT PRIMARY KEY,\n"
                + "    productName TEXT NOT NULL,\n"
                + "    category TEXT, \n" // 👈 New Column Added
                + "    price REAL NOT NULL,\n"
                + "    quantity INTEGER NOT NULL,\n"
                + "    manufacturingDate TEXT,\n"
                + "    supplier TEXT\n"
                + ");";
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("❌ Error creating table: " + e.getMessage());
        }
    }

    @Override
    public boolean addProduct(Product product) {
        // 🔑 UPDATED: Added category to the INSERT statement
        String sql = "INSERT INTO products(productId, productName, category, price, quantity, manufacturingDate, supplier) VALUES(?,?,?,?,?,?,?)";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, product.getProductId());
            pstmt.setString(2, product.getProductName());
            pstmt.setString(3, product.getCategory()); // 👈 Bind Category
            pstmt.setDouble(4, product.getPrice());
            pstmt.setInt(5, product.getQuantity());
            pstmt.setString(6, product.getManufacturingDate().toString());
            pstmt.setString(7, product.getSupplier());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            // Note: This will fail if a product with the same ID already exists due to the PRIMARY KEY constraint.
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
                // 🔑 UPDATED: Added category to the Product constructor
                Product product = new Product(
                        rs.getString("productId"),
                        rs.getString("productName"),
                        rs.getString("category"), // 👈 Retrieve Category
                        rs.getDouble("price"),
                        rs.getInt("quantity"),
                        LocalDate.parse(rs.getString("manufacturingDate")),
                        rs.getString("supplier")
                );
                return Optional.of(product);
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
                // 🔑 UPDATED: Added category to the Product constructor
                Product product = new Product(
                        rs.getString("productId"),
                        rs.getString("productName"),
                        rs.getString("category"), // 👈 Retrieve Category
                        rs.getDouble("price"),
                        rs.getInt("quantity"),
                        LocalDate.parse(rs.getString("manufacturingDate")),
                        rs.getString("supplier")
                );
                products.add(product);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error getting all products: " + e.getMessage());
        }
        return products;
    }

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