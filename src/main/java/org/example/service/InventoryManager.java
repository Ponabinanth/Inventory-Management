package org.example.service;
import org.example.model.Product;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InventoryManager {
    private final Map<String, Product> inventory = new HashMap<>();
    private final StockAlertService stockAlertService; // NEW DEPENDENCY

    public InventoryManager(StockAlertService stockAlertService) {
        this.stockAlertService = stockAlertService;
    }

    public boolean addProduct(Product p) {
        if (inventory.containsKey(p.getId())) return false;
        inventory.put(p.getId(), p);
        return true;
    }

    public Optional<Product> searchProduct(String id) {
        return Optional.ofNullable(inventory.get(id));
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>(inventory.values());
    }

    public int getTotalProducts() {
        return inventory.size();
    }

    public int getTotalQuantity() {
        return inventory.values().stream().mapToInt(Product::getQuantity).sum();
    }

    public double getTotalValue() {
        return inventory.values().stream().mapToDouble(p -> p.getPrice() * p.getQuantity()).sum();
    }

    public boolean updateProduct(String id, double newPrice, int newQuantity) {
        if (!inventory.containsKey(id)) return false;

        Product p = inventory.get(id);
        p.setPrice(newPrice);
        p.setQuantity(newQuantity);

        stockAlertService.checkStockAndAlert(p, newQuantity);

        return true;
    }

    public boolean removeProduct(String id) {
        return inventory.remove(id) != null;
    }

    public void saveToCsv() {
        System.out.println("💾 Inventory saved to products.csv (Simulated).");
    }
    public Optional<String> generateInventoryReportFile() {
        String fileName = "inventory_report_" + LocalDate.now() + ".csv";

        try {
            File reportFile = new File(fileName);

            try (FileWriter writer = new FileWriter(reportFile)) {
                writer.write("ID,Name,Category,Quantity,Price,Supplier\n");

                for (Product p : inventory.values()) {
                    writer.write(String.format("%s,%s,%s,%d,%.2f,%s\n",
                            p.getId(), p.getName(), p.getCategory(), p.getQuantity(), p.getPrice(), p.getSupplier()));
                }

                writer.flush();
            }

            System.out.println("📊 Report file generated and exists at: " + reportFile.getAbsolutePath());
            return Optional.of(reportFile.getAbsolutePath());

        } catch (IOException e) {
            System.err.println("❌ Critical error during simulated file generation: " + e.getMessage());
            return Optional.empty();
        }
    }
}