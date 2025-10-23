package org.example.service;
import org.example.model.Product;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDateTime;

public class StockAlertService {

    private final EmailUtil emailUtil;
    private static final int LOW_STOCK_THRESHOLD = 20;
    private static final String ALERT_RECIPIENT_EMAIL = "inventory.manager@corp.com";

    private final Map<String, LocalDateTime> lastAlertTime = new HashMap<>();
    private static final long ALERT_COOLDOWN_MINUTES = 5;

    public StockAlertService(EmailUtil emailUtil) {
        this.emailUtil = emailUtil;
    }
    public void checkStockAndAlert(Product product, int newQuantity) {
        if (newQuantity <= LOW_STOCK_THRESHOLD && newQuantity >= 0) {

            String productId = product.getId();
            LocalDateTime lastAlert = lastAlertTime.get(productId);
            LocalDateTime currentTime = LocalDateTime.now();
            if (lastAlert != null) {
                long minutesElapsed = java.time.Duration.between(lastAlert, currentTime).toMinutes();

                if (minutesElapsed < ALERT_COOLDOWN_MINUTES) {
                    System.out.println("⏳ ALERT SKIPPED: Low stock for " + product.getName() + " was checked recently. Next alert in " + (ALERT_COOLDOWN_MINUTES - minutesElapsed) + " minutes.");
                    return;
                }
            }
            String subject = "🚨 LOW STOCK ALERT: " + product.getName() + " (" + productId + ")";
            String content = String.format(
                    """
                            The stock level for product '%s' (ID: %s) has dropped to %d units.
                            The defined threshold limit is %d units.
                            
                            Please place a new order with supplier %s immediately.""",
                    product.getName(),
                    productId,
                    newQuantity,
                    LOW_STOCK_THRESHOLD,
                    product.getSupplier()
            );

            emailUtil.sendReport(ALERT_RECIPIENT_EMAIL, subject, content);
            lastAlertTime.put(productId, currentTime);

            System.out.println("⚠️ ALERT: Low stock threshold reached for " + product.getName() + " (" + newQuantity + " units). Alert email sent to " + ALERT_RECIPIENT_EMAIL);
        }
    }

    public void setThresholdLimit(int threshold) {
        System.out.println("Threshold limit cannot be changed dynamically in this demo setup (uses static final field).");
        System.out.println("Current LOW_STOCK_THRESHOLD is: " + LOW_STOCK_THRESHOLD);
    }
}