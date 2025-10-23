package org.example;

import org.example.model.Product;
import java.util.List;

public class TableFormatter {
    public static void printProductTable(List<Product> products) {
        if (products.isEmpty()) {
            System.out.println("No products to display.");
            return;
        }
        final int COL_ID = 10;
        final int COL_NAME = 25;
        final int COL_CATEGORY = 15;
        final int COL_PRICE = 10;
        final int COL_QTY = 8;
        final int COL_DATE = 14;
        final int COL_SUPPLIER = 20;
        String separator = "+";
        separator += "-".repeat(COL_ID + 2) + "+";
        separator += "-".repeat(COL_NAME + 2) + "+";
        separator += "-".repeat(COL_CATEGORY + 2) + "+";
        separator += "-".repeat(COL_PRICE + 2) + "+";
        separator += "-".repeat(COL_QTY + 2) + "+";
        separator += "-".repeat(COL_DATE + 2) + "+";
        separator += "-".repeat(COL_SUPPLIER + 2) + "+";
        System.out.println(separator);
        System.out.printf("| %-" + COL_ID + "s | %-" + COL_NAME + "s | %-" + COL_CATEGORY + "s | %-" + COL_PRICE + "s | %-" + COL_QTY + "s | %-" + COL_DATE + "s | %-" + COL_SUPPLIER + "s |%n",
                "ID", "NAME", "CATEGORY", "PRICE ($)", "QTY", "MANU. DATE", "SUPPLIER");
        System.out.println(separator);
        for (Product p : products) {
            System.out.printf("| %-" + COL_ID + "s | %-" + COL_NAME + "s | %-" + COL_CATEGORY + "s | %-" + COL_PRICE + ".2f | %" + COL_QTY + "d | %-" + COL_DATE + "s | %-" + COL_SUPPLIER + "s |%n",
                    p.getId(),
                    truncate(p.getName(), COL_NAME),
                    truncate(p.getCategory(), COL_CATEGORY),
                    p.getPrice(),
                    p.getQuantity(),
                    p.getLastUpdated().toString(),
                    truncate(p.getSupplier(), COL_SUPPLIER));
        }
        System.out.println(separator);
    }
    private static String truncate(String s, int length) {
        if (s.length() > length) {
            return s.substring(0, length - 1) + "…";
        }
        return s;
    }
}
