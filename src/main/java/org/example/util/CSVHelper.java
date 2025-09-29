package org.example.util;
public class CSVHelper {

    public static final String[] RAW_INVENTORY_DATA = {

            "ID,Name          ,Price   ,Quantity  ,ManufacturingDate  ,Supplier",
            "P1,Laptop        ,1200.50 ,15        ,2024-03-10         ,TechCorp",
            "P2,Mouse         ,25.0    ,50        ,2024-01-22         ,GadgetCo",
            "P3,Keyboard      ,75.99   ,30        ,2023-11-05         ,Keytron"
    };
    public static String[] getRawInventoryData() {
        return RAW_INVENTORY_DATA;
    }
}

