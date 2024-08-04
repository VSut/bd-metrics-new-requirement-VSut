import com.amazonaws.services.cloudwatch.model.StandardUnit;

import java.util.Timer;

/**
 * Class representing final state of the coding activity.
 */
public class OrderProcessor {

    private CustomerManager customerManager = new CustomerManager();
    private InventoryManager inventoryManager = new InventoryManager();
    private CreditProcessor creditProcessor = new CreditProcessor();
    private MetricsPublisher metricsPublisher;

    /**
     * Constructs a OrderProcessor object.
     *
     * @param metricsPublisher Used to publish metrics to CloudWatch.
     */
    public OrderProcessor(MetricsPublisher metricsPublisher) {
        this.metricsPublisher = metricsPublisher;
    }

    /**
     *  Processes orders by verifying the customer data, checking against the inventory, processing payment,
     *  and then allowing the order to be shipped.
     *
     * @param newOrder The order to be processed
     * @return True if the order was successfully processed, false otherwise.
     */
    public boolean processOrder(Order newOrder) {
        boolean success = false;
        long firstTime = System.currentTimeMillis();
        try {
            customerManager.verifyCustomerInfo(newOrder);
            int pickListNumber = inventoryManager.createPickList(newOrder);
            creditProcessor.processPayment(newOrder);
            inventoryManager.processPickList(pickListNumber);
            success = true;
            long secondTime = System.currentTimeMillis();
            metricsPublisher.addMetric("ORDER_PROCESSING_TIMES", secondTime - firstTime, StandardUnit.Milliseconds);
        } catch (Exception e) {
            long secondTime = System.currentTimeMillis();
            System.out.println("Error processing order " + newOrder.getOrderNumber());
            metricsPublisher.addMetric("ORDER_PROCESSING_TIMES", secondTime - firstTime, StandardUnit.Milliseconds);
        }

        return success;
    }
}
