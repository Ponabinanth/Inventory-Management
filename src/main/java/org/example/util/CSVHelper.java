package org.example.util;
import org.example.model.Product;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

class CSVHelper {

    private static final String CSV_FILE = "products.csv";
    private static List<Product> getDefaultProducts() {
        List<Product> defaults = new ArrayList<>();
        LocalDate today = LocalDate.now();
        defaults.add(new Product("1", "Laptop", "Electronics", 120000, 45, today, "ViewTech Inc."));
        defaults.add(new Product("2", "Keyboard", "Electronics", 4000, 45, today, "Key Ltd."));
        defaults.add(new Product("3", "Chair", "Furnitures", 4500, 56, today, "Vasanthan Co."));

        return defaults;
    }

    public static List<Product> loadProducts() {
        List<Product> products;
        try {
            products = loadProductsFromResource();
            if (!products.isEmpty()) {
                System.out.println("✅ Loaded " + products.size() + " products from CSV resource.");
                return products;
            }
        } catch (Exception ignored) {
        }
        products = loadProductsFromFileSystem();

        if (!products.isEmpty()) {
            System.out.println("✅ Loaded " + products.size() + " products from local CSV file.");
        } else {
            products = getDefaultProducts();
            System.out.println("⚠️ '" + CSV_FILE + "' not found. Starting with " + products.size() + " default products.");
        }

        return products;
    }

    private static List<Product> loadProductsFromResource() throws IOException {
        List<Product> products = new ArrayList<>();
        try (InputStream is = CSVHelper.class.getClassLoader().getResourceAsStream(CSV_FILE)) {
            if (is == null) {
                throw new IOException("CSV file not found in resources.");
            }
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                parseCSV(br, products);
            }
        }
        return products;
    }

    private static List<Product> loadProductsFromFileSystem() {
        List<Product> products = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(CSV_FILE))) {
            parseCSV(br, products);
        } catch (IOException ignored) {
        }
        return products;
    }

    private static void parseCSV(BufferedReader br, List<Product> products) throws IOException {
        String line;
        br.readLine(); // Skip header row

        while ((line = br.readLine()) != null) {
            String[] values = line.split(",");

            if (values.length < 7) {
                System.err.println("Skipping malformed CSV line: " + line);
                continue;
            }

            try {
                String id = values[0].trim();
                String name = values[1].trim();
                String category = values[2].trim();
                double price = Double.parseDouble(values[3].trim());
                int quantity = Integer.parseInt(values[4].trim());
                LocalDate date = LocalDate.parse(values[5].trim());
                String supplier = values[6].trim();

                Product product = new Product(id, name, category, price, quantity, date, supplier);
                products.add(product);
            } catch (NumberFormatException | DateTimeParseException e) {
                System.err.println("Error parsing data in CSV line: " + line + ". " + e.getMessage());
            }
        }
    }
}