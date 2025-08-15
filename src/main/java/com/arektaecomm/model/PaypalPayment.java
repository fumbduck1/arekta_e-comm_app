package com.arektaecomm.model;

public class PaypalPayment implements PaymentMethod {
    @Override
    public void pay(Order order) throws Exception {
        // Simulate PayPal payment (always succeeds for demo)
    }
}