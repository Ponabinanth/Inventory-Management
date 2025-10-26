package org.example;

import org.example.dao.userDAOImpl;
import org.example.model.Product;
import org.example.model.User;
import org.example.model.User.UserRole;
import org.example.service.EmailUtil;
import org.example.service.InventoryManager;
import org.example.service.OTPService;
import org.example.service.StockAlertService;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.UUID;

public class Main {

    private static final EmailUtil emailUtil = new EmailUtil();
    private static final OTPService otpService = new OTPService(emailUtil);
    private static final StockAlertService stockAlertService = new StockAlertService(emailUtil);
    private static final InventoryManager manager = new InventoryManager(stockAlertService);

    private static final userDAOImpl userAuthDAO = new userDAOImpl();
    private static final Scanner sc = new Scanner(System.in);

    private enum MenuOption {
        ADD_PRODUCT(1), VIEW_ALL_PRODUCTS(2), SEARCH_PRODUCT(3),
        UPDATE_PRODUCT(4), REMOVE_PRODUCT(5), VIEW_REPORT(7), SEND_REPORT(8), EXIT(6), INVALID(-1);

        private final int value;
        MenuOption(int value) { this.value = value; }
        public static MenuOption fromValue(int value) {
            for (MenuOption option : MenuOption.values()) {
                if (option.value == value) return option;
            }
            return INVALID;
        }
    }

    public static void main(String[] args) {
        displayWelcomeScreen();
        mainMenu();
    }

    private static void displayWelcomeScreen() {
        String LINE_WIDTH = "═════════════════════════════════════════════════";

        System.out.println("╔" + LINE_WIDTH +  "╗");
        System.out.println("         📦 INVENTORY MANAGEMENT SYSTEM 📈       ");
        System.out.println("     Welcome to your central stock control hub!      ");
        System.out.println("╚" + LINE_WIDTH +  "╝");
    }
    private static void mainMenu() {
        boolean running = true;
        while (running) {
            System.out.println("\n--- Main Menu ---");
            System.out.println("1. 🔑 Login");
            System.out.println("2. 📝 Register");
            System.out.println("3. 📧 Verify Email (Simulated)");
            System.out.println("4. 🚪 Exit");
            System.out.print("🎯 Enter choice: ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    if (login()) {
                        managerMenu();
                    }
                    break;
                case "2":
                    register();
                    break;
                case "3":
                    verifyEmail();
                    break;
                case "4":
                    exit();
                    running = false;
                    break;
                default:
                    System.err.println("❌ Invalid choice. Please try again.");
            }
        }
    }

    private static boolean login() {
        System.out.println("\n🔑 --- Login Required --- 🔑");
        System.out.print("📧 Enter Email : ");
        String email = sc.nextLine();
        System.out.print("🔒 Enter Password: ");
        String password = sc.nextLine();

        User user = userAuthDAO.login(email, password);

        if (user == null) {
            System.err.println("❌ Invalid credentials. Access denied.");
            return false;
        }

        System.out.println("\n✅ Login successful. Welcome, " + user.getFullName() + " (" + user.getRole() + ")!");

        if (user.getRole() != UserRole.MANAGER) {
            System.err.println("🚫 Access denied. Only Managers can log in to the main system.");
            userAuthDAO.logout();
            return false;
        }

        if (user.getRole() != UserRole.MANAGER && !user.isVerified()) {
            handleUnverifiedUser(user);
            userAuthDAO.logout();
            return false;
        }
        return performOtpCheck(user);
    }

    private static boolean performOtpCheck(User user) {
        otpService.generateAndSendOTP(user.getUserId(), user.getEmail());
        System.out.print("📬 Enter the 6-digit OTP received: ");
        String enteredOTP = sc.nextLine();

        if (otpService.verifyOTP(user.getUserId(), enteredOTP)) {
            System.out.println("✅ OTP verified successfully. Entering system...");
            return true;
        } else {
            System.err.println("❌ OTP verification failed. Access denied.");
            userAuthDAO.logout();
            return false;
        }
    }

    private static void handleUnverifiedUser(User user) {
        System.err.println("\n⚠️ Access Denied: Your email address is unverified.");
        System.err.println("   Please check your email (" + user.getEmail() + ") for a verification link.");

        System.err.print("   Would you like to resend the verification link? (Y/N): ");
        System.err.flush();

        String choice = sc.nextLine().trim().toUpperCase();

        if (choice.equals("Y")) {
            System.err.println("   📧 Verification link resent. Please check your inbox.");
        }

        System.err.println("   You must verify your email before proceeding.");
    }

    private static void register() {
        System.out.println("\n--- User Registration ---");
        System.out.print("👤 Enter Full Name: ");
        String name = sc.nextLine();
        System.out.print("📧 Enter Email: ");
        String email = sc.nextLine();
        System.out.print("🔒 Choose Password: ");
        String password = sc.nextLine();

        User newUser = new User(
                UUID.randomUUID().toString(),
                name,
                email,
                password,
                UserRole.VIEWER // Default role
        );

        if (userAuthDAO.addUser(newUser)) {
            String token = UUID.randomUUID().toString();
            emailUtil.sendVerificationEmail(email, token);

            System.out.println("\n✅ Registration successful for " + name + ".");
            System.out.println("   A verification link has been sent to " + email + ".");
            System.out.println("   Please verify your email to enable full access.");
        } else {
            System.err.println("❌ Registration failed. Email may already be in use.");
        }
    }

    private static void verifyEmail() {
        System.out.println("\n--- Email Verification (Simulated) ---");
        System.out.print("📧 Enter Email to Verify: ");
        String email = sc.nextLine();
        System.out.print("🔑 Enter Verification Token (Simulated: enter 'VERIFY'): ");
        String token = sc.nextLine();

        Optional<User> user = userAuthDAO.getUserByEmail(email);

        if (user.isPresent() && token.equalsIgnoreCase("VERIFY")) {

            user.get().setVerified(true);

            if (userAuthDAO.updateUser(user.orElse(null))) {
                System.out.println("\n✅ Email " + email + " verified successfully! You can now log in.");
            } else {
                System.err.println("❌ Could not update user status in the DAO.");
            }
        } else {
            System.err.println("❌ Verification failed. Invalid email or token.");
        }
    }

    private static void exit() {
        System.out.println("\n👋 Thank you for using the system. Goodbye!");
        userAuthDAO.logout();
        sc.close();
    }
    private static void managerMenu() {
        if (userAuthDAO.getCurrentUser() == null || userAuthDAO.getCurrentUser().getRole() != UserRole.MANAGER) {
            System.err.println("🚫 Access denied. Must be logged in as a Manager.");
            return;
        }

        System.out.println("\n🚀 Entering Manager System...");

        while (true) {
            displayMenu();
            System.out.print("\n🎯 Enter your choice: ");
            try {
                int choice = Integer.parseInt(sc.nextLine());
                MenuOption option = MenuOption.fromValue(choice);

                switch (option) {
                    case ADD_PRODUCT:
                        handleAddProduct();
                        break;
                    case VIEW_ALL_PRODUCTS:
                        handleViewAllProducts();
                        break;
                    case SEARCH_PRODUCT:
                        handleSearchProduct();
                        break;
                    case UPDATE_PRODUCT:
                        handleUpdateProduct();
                        break;
                    case REMOVE_PRODUCT:
                        handleRemoveProduct();
                        break;
                    case VIEW_REPORT:
                        handleViewReport();
                        break;
                    case SEND_REPORT:
                        sendInventoryReport();
                        break;
                    case EXIT:
                        System.out.println("\n👋 Logging out. Have a productive day!");
                        userAuthDAO.logout();
                        return;
                    default:
                        System.err.println("⚠️ Invalid option (" + choice + "). Please choose a number from the menu.");
                }
            } catch (NumberFormatException e) {
                System.err.println("❌ Invalid input. Please enter a number.");
            }
        }
    }

    private static void displayMenu() {
        System.out.println("\n═════════════════════════════════════════════════");
        System.out.println("📊                MANAGER MENU                   ");
        System.out.println("═════════════════════════════════════════════════");
        System.out.println("  🔢  STOCK ACTIONS:");
        System.out.println("  [1] ➕ Add New Product");
        System.out.println("  [2] 📋 View All Products");
        System.out.println("  [3] 🔍 Search Product by ID");
        System.out.println("  [4] ✏️ Update Product Details");
        System.out.println("  [5] 🗑️ Remove Product");
        System.out.println(" ");
        System.out.println("  📩  REPORTING:");
        System.out.println(" ");
        System.out.println("  [7] 📈 View Inventory Report Summary");
        System.out.println("  [8] 📧 Send Inventory Report (Email)");
        System.out.println(" ");
        System.out.println("  [6] 🚪 Logout and Exit");
        System.out.println("═════════════════════════════════════════════════");
    }

    private static void handleAddProduct() {
        System.out.println("\n➕ --- Add New Product --- 🛒");
        try {
            System.out.print("🆔 Enter ID: ");
            String id = sc.nextLine();
            System.out.print("🏷️ Enter Name: ");
            String name = sc.nextLine();
            System.out.print("📁 Enter Category: ");
            String category = sc.nextLine();
            System.out.print("💰 Enter Price: ");
            double price = Double.parseDouble(sc.nextLine());
            System.out.print("📦 Enter Quantity: ");
            int quantity = Integer.parseInt(sc.nextLine());
            System.out.print("🏭 Enter Supplier: ");
            String supplier = sc.nextLine();

            Product p = new Product(id, name, category, price, quantity, LocalDate.now(), supplier);

            if (manager.addProduct(p)) {
                System.out.println("\n✅ Product '" + name + "' added successfully.");
                manager.saveToCsv();
            } else {
                System.err.println("❌ Product with ID " + id + " already exists. Cannot add.");
            }
        } catch (NumberFormatException e) {
            System.err.println("❌ Invalid input for price or quantity. Please enter numbers only.");
        }
    }

    private static void handleViewAllProducts() {
        System.out.println("\n📋 --- Current Stock Listing --- 📦");
        List<Product> products = manager.getAllProducts();
        if (products.isEmpty()) {
            System.out.println("The inventory is currently empty. Start adding products!");
            return;
        }

        System.out.printf("%-10s | %-25s | %-15s | %-10s | %-10s%n",
                "ID", "Name", "Category", "QTY", "Price");
        System.out.println("----------------------------------------------------------------------");

        for (Product p : products) {
            System.out.printf(" %-9s | %-25s | %-15s | %-10d | $%.2f%n",
                    p.getId(), p.getName(), p.getCategory(), p.getQuantity(), p.getPrice());
        }
    }

    private static void handleSearchProduct() {
        System.out.println("\n🔍 --- Search Product by ID ---");
        System.out.print("🆔 Enter Product ID: ");
        String id = sc.nextLine();

        Optional<Product> productOpt = manager.searchProduct(id);
        if (productOpt.isPresent()) {
            Product p = productOpt.get();
            System.out.println("\n✅ Product Found:");
            System.out.println("  🏷️ Name: " + p.getName());
            System.out.println("  📁 Category: " + p.getCategory());
            System.out.println("  💰 Price: $" + p.getPrice());
            System.out.println("  📦 Quantity: " + p.getQuantity());
            System.out.println("  🏭 Supplier: " + p.getSupplier());
            System.out.println("  📅 Last Updated: " + p.getLastUpdated());
        } else {
            System.err.println("❌ Product with ID " + id + " not found in inventory.");
        }
    }

    private static void handleUpdateProduct() {
        System.out.println("\n✏️ --- Update Product Details ---");
        System.out.print("🆔 Enter Product ID to update: ");
        String id = sc.nextLine();

        Optional<Product> productOpt = manager.searchProduct(id);
        if (productOpt.isEmpty()) {
            System.err.println("❌ Product not found.");
            return;
        }

        Product p = productOpt.get();
        double newPrice = p.getPrice();
        int newQuantity = p.getQuantity();

        try {
            System.out.println("\nCurrent Price: $" + p.getPrice());
            System.out.print("💰 Enter new Price (or press Enter to skip): ");
            String priceStr = sc.nextLine();
            if (!priceStr.isEmpty()) {
                newPrice = Double.parseDouble(priceStr);
            }

            System.out.println("Current Quantity: " + p.getQuantity());
            System.out.print("📦 Enter new Quantity (or press Enter to skip): ");
            String quantityStr = sc.nextLine();
            if (!quantityStr.isEmpty()) {
                newQuantity = Integer.parseInt(quantityStr);
            }

            if (manager.updateProduct(id, newPrice, newQuantity)) {
                System.out.println("\n✅ Product details updated successfully.");
            } else {
                System.err.println("❌ Failed to update product (ID not found).");
            }
        } catch (NumberFormatException e) {
            System.err.println("❌ Invalid input for price or quantity.");
        }
    }

    private static void handleRemoveProduct() {
        System.out.println("\n🗑️ --- Remove Product ---");
        System.out.print("🆔 Enter Product ID to remove: ");
        String id = sc.nextLine();

        if (manager.removeProduct(id)) {
            System.out.println("\n✅ Product " + id + " removed successfully from inventory.");
            manager.saveToCsv();
        } else {
            System.err.println("❌ Product with ID " + id + " not found. Cannot remove.");
        }
    }

    private static void handleViewReport() {
        System.out.println("\n📈 --- Inventory Report Summary --- 📋");
        System.out.println("  📦 Total Unique Products: " + manager.getTotalProducts());
        System.out.println("  🔢 Total Items in Stock: " + manager.getTotalQuantity());
        System.out.printf("  💰 Total Inventory Value: $%.2f%n", manager.getTotalValue());
    }

    private static void sendInventoryReport() {
        System.out.println("\n📧 --- Send Inventory Report via Email --- 📤");

        Optional<String> filePathOptional = manager.generateInventoryReportFile();

        if (filePathOptional.isEmpty()) {
            System.err.println("❌ Failed to generate report file. Cannot proceed with email.");
            return;
        }

        String filePath = filePathOptional.get();

        System.out.print("📬 Enter final recipient email address: ");
        String recipientEmail = sc.nextLine();

        String subject = "Inventory Report - " + LocalDate.now();
        String body = "Dear Recipient,\n\nPlease find the attached CSV file containing the latest inventory report details.\n\nBest Regards,\nInventory System";

        emailUtil.sendReport(recipientEmail, subject, body, filePath);
    }
}