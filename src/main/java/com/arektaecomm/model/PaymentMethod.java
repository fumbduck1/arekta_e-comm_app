package com.arektaecomm.model;

/**
 * Strategy interface for payment methods.
 * Implementations should define how to process payment for an order.
 */
public interface PaymentMethod {
    /**
     * Process payment for the given order.
     * 
     * @param order the order to pay for
     * @throws Exception if payment fails
     */
    void pay(Order order) throws Exception;
}