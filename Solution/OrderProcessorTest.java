package com.amazon.ata.metrics.prework.newrequirement;

import com.amazon.ata.metrics.prework.newrequirement.resources.Order;

import com.amazonaws.services.cloudwatch.model.StandardUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.MockitoAnnotations.initMocks;

public class OrderProcessorTest {

    private static final String ORDER_ID = "orderId-1234";
    private static final String CUSTOMER_ID =  "customerId-58483";
    private static final String PAYMENT_ID = "paymentId-13424";
    private static final double TOTAL_PRICE = 13.99;
    private static final String METRIC_NAME = "ORDER_FAILURES";

    @Mock
    private MetricsPublisher metricsPublisher;

    @InjectMocks
    private OrderProcessor orderProcessor;

    @BeforeEach
    public void setup() {
        initMocks(this);
    }

    @Test
    public void processOrder_measuresTime_correctTimeRangeReturned() {
        //GIVEN
        Order order = new Order();
        ArgumentCaptor<Double> captor = ArgumentCaptor.forClass(Double.class);
        // minElapsedTime based on number and length of Sleep calls in Resources
        double minElapsedTime = 250 * 4;

        //WHEN
        long startTime = System.currentTimeMillis();
        orderProcessor.processOrder(order);
        long endTime = System.currentTimeMillis();

        //THEN
        double maxElapsedTime = endTime - startTime;
        verify(metricsPublisher, times(1)).addMetric(any(String.class), captor.capture(), any(StandardUnit.class));
        double reportedElapsedTime = captor.getValue();
        assertTrue(reportedElapsedTime >= minElapsedTime, "The time measured is less than of the minimum time");
        assertTrue(reportedElapsedTime <= maxElapsedTime, "The time measured is greater than of the maximum time");
        verifyNoMoreInteractions(metricsPublisher);
    }
}
