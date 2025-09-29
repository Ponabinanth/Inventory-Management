package org.example;

import org.example.model.Product;
import java.util.Collection;

public class TableFormatter {
    public static void printProductTable(Collection<Product> products) {
        if (products == null || products.isEmpty()) {
            System.out.println("Inventory is empty.");
            return;
        }
        final int ID_WIDTH = 4;
        final int NAME_WIDTH = 15;
        final int PRICE_WIDTH = 10;
        final int QTY_WIDTH = 10;
        final int DATE_WIDTH = 15;
        final int SUPP_WIDTH = 15;
        int totalWidth = ID_WIDTH + NAME_WIDTH + PRICE_WIDTH + QTY_WIDTH + DATE_WIDTH + SUPP_WIDTH + (6 * 3) + 1;

        String horizontalLine = "-".repeat(totalWidth);
        String headerFormat = "| %-" + ID_WIDTH + "s | %-" + NAME_WIDTH + "s | %" + PRICE_WIDTH + "s | %" + QTY_WIDTH + "s | %-" + DATE_WIDTH + "s | %-" + SUPP_WIDTH + "s |%n";

        String rowFormat = "| %-" + ID_WIDTH + "s | %-" + NAME_WIDTH + "s | %" + PRICE_WIDTH + ".2f | %" + QTY_WIDTH + "d | %-" + DATE_WIDTH + "s | %-" + SUPP_WIDTH + "s |%n";
        System.out.println(horizontalLine);
        System.out.printf(headerFormat, "ID", "Name", "Price", "Quantity", "Manuf. Date", "Supplier");
        System.out.println(horizontalLine);

        for (Product p : products) {
            System.out.printf(rowFormat,
                    p.getProductId(),
                    p.getProductName(),
                    p.getPrice(),
                    p.getQuantity(),
                    p.getManufacturingDate().toString(),
                    p.getSupplier()
            );
        }
        System.out.println(horizontalLine);
    }
}

