package org.example;
import org.example.model.Product;
import org.example.service.InventoryManager;
import org.example.util.CSVHelper;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Main {
    private static final InventoryManager manager = new InventoryManager();
    private static final Scanner sc = new Scanner(System.in);

    // Updated enum to include VIEW_REPORT(7)
    private enum MenuOption {
        ADD_PRODUCT(1), VIEW_ALL_PRODUCTS(2), SEARCH_PRODUCT(3),
        UPDATE_PRODUCT(4), REMOVE_PRODUCT(5), EXIT(6), VIEW_REPORT(7), INVALID(-1);

        private final int value;

        MenuOption(int value) {
            this.value = value;
        }

        public static MenuOption fromValue(int value) {
            for (MenuOption option : MenuOption.values()) {
                if (option.value == value) {
                    return option;
                }
            }
            return INVALID;
        }
    }

    public static void main(String[] args) {
        new Main().run();
    }

    private void run() {
        showWelcomeScreen();
        loadInitialData(); // Load products from CSVHelper on startup
        MenuOption choice;

        do {
            showMenu();
            String input = sc.nextLine();
            int inputChoice;

            try {
                inputChoice = Integer.parseInt(input.trim());
            } catch (NumberFormatException e) {
                inputChoice = -1; // Treat non-numeric input as invalid
            }

            choice = MenuOption.fromValue(inputChoice);

            switch (choice) {
                case ADD_PRODUCT:
                    addProduct();
                    break;
                case VIEW_ALL_PRODUCTS:
                    viewAllProducts(); // Now handles sorting
                    break;
                case SEARCH_PRODUCT:
                    searchProduct();
                    break;
                case UPDATE_PRODUCT:
                    updateProduct();
                    break;
                case REMOVE_PRODUCT:
                    removeProduct();
                    break;
                case VIEW_REPORT:
                    viewReport();
                    break;
                case EXIT:
                    // Save inventory before exiting
                    manager.saveToCsv();
                    System.out.println("👋 Exiting... Thank you!");
                    break;
                case INVALID:
                default:
                    System.out.println("⚠️ Invalid choice. Try again.");
                    break;
            }
        } while (choice != MenuOption.EXIT);

        sc.close();
    }
    private boolean validateStringInput(String input, String fieldName) {
        if (input == null || input.trim().isEmpty()) {
            System.err.println("❌ Error: " + fieldName + " cannot be empty. Operation aborted.");
            return false;
        }
        return true;
    }
    private void viewAllProducts() {
        System.out.println("\n--- All Products in Inventory ---");

        System.out.println("Sort by:");
        System.out.println("  1. ID (Default)");
        System.out.println("  2. Name");
        System.out.println("  3. Price");
        System.out.println("  4. Quantity");
        System.out.println("  (Enter anything else to cancel)");
        System.out.print("Enter your sort choice (1-4): ");

        String sortInput = sc.nextLine().trim();
        Comparator<Product> comparator = null;

        switch (sortInput) {
            case "1":
                comparator = Comparator.comparing(Product::getProductId);
                System.out.println("Sorting by ID...");
                break;
            case "2":
                comparator = Comparator.comparing(Product::getProductName);
                System.out.println("Sorting by Name...");
                break;
            case "3":
                comparator = Comparator.comparingDouble(Product::getPrice);
                System.out.println("Sorting by Price...");
                break;
            case "4":
                comparator = Comparator.comparingInt(Product::getQuantity);
                System.out.println("Sorting by Quantity...");
                break;
            default:
                // If input is invalid or user cancels, just show the unsorted list
                System.out.println("Displaying unsorted list...");
                TableFormatter.printProductTable(manager.getAllProducts());
                return;
        }
        List<Product> sortedProducts = manager.getSortedProducts(comparator);
        TableFormatter.printProductTable(sortedProducts);
    }

    private void viewReport() {
        System.out.println("\n--- Inventory Summary Report ---");

        int totalProducts = manager.getTotalProducts();
        int totalQuantity = manager.getTotalQuantity();
        double totalValue = manager.getTotalValue();

        System.out.println("-----------------------------------");
        System.out.printf("| %-25s | %-10d |%n", "Unique Product Types", totalProducts);
        System.out.printf("| %-25s | %-10d |%n", "Total Items in Stock", totalQuantity);
        System.out.printf("| %-25s | $%-9.2f |%n", "Total Inventory Value", totalValue);
        System.out.println("-----------------------------------");
    }

    private void showWelcomeScreen() {
        System.out.println("=========================================");
        System.out.println("  Welcome to Inventory Management System");
        System.out.println("=========================================");
    }

    private void showMenu() {
        System.out.println("\n--- Main Menu ---");
        System.out.println("1. Add a new product");
        System.out.println("2. View all products ");
        System.out.println("3. Search for a product ");
        System.out.println("4. Update a product");
        System.out.println("5. Remove a product");
        System.out.println("7. View Inventory Report");
        System.out.println("6. Exit ");
        System.out.print("Enter your choice: ");
    }

    private void addProduct() {
        try {
            System.out.print("ID: ");
            String id = sc.nextLine();
            if (!validateStringInput(id, "ID")) return;

            System.out.print("Name: ");
            String name = sc.nextLine();
            if (!validateStringInput(name, "Name")) return;

            double price;
            System.out.print("Price: ");
            String priceInput = sc.nextLine().trim();
            if (priceInput.isEmpty()) {
                System.err.println("❌ Error: Price cannot be empty."); return;
            }
            price = Double.parseDouble(priceInput);
            if (price <= 0) {
                System.err.println("❌ Error: Price must be a positive number (> 0). Operation aborted."); return;
            }

            int quantity;
            System.out.print("Quantity: ");
            String quantityInput = sc.nextLine().trim();
            if (quantityInput.isEmpty()) {
                System.err.println("❌ Error: Quantity cannot be empty."); return;
            }
            quantity = Integer.parseInt(quantityInput);
            if (quantity < 0) {
                System.err.println("❌ Error: Quantity cannot be negative. Operation aborted."); return;
            }

            System.out.print("Manufacturing Date (YYYY-MM-DD): ");
            String dateInput = sc.nextLine();
            if (!validateStringInput(dateInput, "Manufacturing Date")) return;
            LocalDate date = LocalDate.parse(dateInput);

            System.out.print("Supplier: ");
            String supplier = sc.nextLine();
            if (!validateStringInput(supplier, "Supplier")) return;


            Product newProduct = new Product(id, name, price, quantity, date, supplier);

            if (manager.addProduct(newProduct)) {
                System.out.println("✅ Product added successfully in the inventory 📦");
            }

        } catch (NumberFormatException e) {
            System.err.println("❌ Error: Invalid number format. Please enter a valid number for Price and Quantity.");
        } catch (DateTimeParseException e) {
            System.err.println("❌ Error: Invalid date format. Please use YYYY-MM-DD (e.g., 2024-12-31). Operation aborted.");
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
        }
    }

    private void loadInitialData() {
        try {
            String[] data = CSVHelper.getRawInventoryData();
            // Start from index 1 to skip the CSV header line
            if (data.length > 1) {
                for (int i = 1; i < data.length; i++) {
                    String line = data[i];
                    String[] fields = line.split(",");
                    if (fields.length == 6) {
                        String id = fields[0].trim();
                        String name = fields[1].trim();
                        double price = Double.parseDouble(fields[2].trim());
                        int quantity = Integer.parseInt(fields[3].trim());
                        LocalDate date = LocalDate.parse(fields[4].trim());
                        String supplier = fields[5].trim();

                        Product product = new Product(id, name, price, quantity, date, supplier);
                        manager.addProduct(product);
                    }
                }
                System.out.println("✅ Initial inventory loaded successfully.");
            }
        } catch (Exception e) {
            System.err.println("❌ Failed to load initial inventory data. Check CSV format: " + e.getMessage());
        }
    }


    private void searchProduct() {
        System.out.print("Enter Product ID to search: ");
        String id = sc.nextLine();
        if (!validateStringInput(id, "Product ID")) return; // Validation added

        Optional<Product> result = manager.searchProduct(id);
        if (result.isPresent()) {
            Product p = result.get();
            System.out.println("✅ Found: " + p.getProductName() + " | Price: $" + p.getPrice());
        } else {
            System.out.println("❌ Product not found.");
        }
    }

    private void updateProduct() {
        try {
            System.out.print("Enter Product ID to update: ");
            String id = sc.nextLine();
            if (!validateStringInput(id, "Product ID")) return; // Validation added

            double newPrice;
            System.out.print("Enter new Price: ");
            String priceInput = sc.nextLine().trim();
            if (priceInput.isEmpty()) {
                System.err.println("❌ Error: New Price cannot be empty. Operation aborted."); return;
            }
            newPrice = Double.parseDouble(priceInput);
            if (newPrice <= 0) {
                System.err.println("❌ Error: New Price must be a positive number (> 0). Operation aborted."); return;
            }

            int newQuantity;
            System.out.print("Enter new Quantity: ");
            String quantityInput = sc.nextLine().trim();
            if (quantityInput.isEmpty()) {
                System.err.println("❌ Error: New Quantity cannot be empty. Operation aborted."); return;
            }
            newQuantity = Integer.parseInt(quantityInput);
            if (newQuantity < 0) {
                System.err.println("❌ Error: New Quantity cannot be negative. Operation aborted."); return;
            }

            if (manager.updateProduct(id, newPrice, newQuantity)) {
                System.out.println("🔄 Product updated successfully in the inventory!");
            } else {
                System.out.println("❌ Product not found. Update failed.");
            }
        } catch (NumberFormatException e) {
            System.err.println("❌ Error: Invalid number format. Please enter a valid number for Price and Quantity.");
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
        }
    }

    private void removeProduct() {
        System.out.print("Enter Product ID to remove: ");
        String id = sc.nextLine();
        if (!validateStringInput(id, "Product ID")) return; // Validation added

        if (manager.removeProduct(id)) {
            System.out.println("🗑️ Product removed successfully in the inventory!");
        } else {
            System.out.println("❌ Product not found. Removal failed.");
        }
    }
}
