package org.example.service;

import org.example.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class StockAlertServiceTest {

    @Mock
    private EmailUtil emailUtil;

    @InjectMocks
    private StockAlertService stockAlertService;

    private Product mockProduct;

    private static final String RECIPIENT_EMAIL = "inventory.manager@corp.com";
    private static final int LOW_STOCK_THRESHOLD = 20;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        mockProduct = new Product("SKU-123", "Widget Alpha", "Electronics",
                10.0, 50, null, "Tech Suppliers Inc.");
    }
    @Test
    void testAlertSentWhenQuantityDropsBelowThreshold() {
        int lowQuantity = LOW_STOCK_THRESHOLD - 1; // 19 units

        stockAlertService.checkStockAndAlert(mockProduct, lowQuantity);
        verify(emailUtil, times(1)).sendReport(
                eq(RECIPIENT_EMAIL),
                anyString(),
                anyString()
        );
    }
    @Test
    void testNoAlertSentWhenQuantityIsAboveThreshold() {
        int safeQuantity = LOW_STOCK_THRESHOLD + 10; // 30 units

        stockAlertService.checkStockAndAlert(mockProduct, safeQuantity);

        verify(emailUtil, never()).sendReport(
                anyString(), anyString(), anyString()
        );
    }

    @Test
    void testAlertSentAtZeroQuantity() {
        stockAlertService.checkStockAndAlert(mockProduct, 0);
        verify(emailUtil, times(1)).sendReport(anyString(), anyString(), anyString());
    }
    @Test
    void testNoAlertSentAtNegativeQuantity() {
        stockAlertService.checkStockAndAlert(mockProduct, -5);
        verify(emailUtil, never()).sendReport(anyString(), anyString(), anyString());
    }

    @Test
    void testSetThresholdLimit() {
        stockAlertService.setThresholdLimit(10);
    }
}
