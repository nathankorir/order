package com.ecommerce.order.enums;

public class Enum {
    public enum OrderStatus {
        PLACED,
        CANCELLED,
        COMPLETED
    }

    public enum PaymentStatus {
        UNPAID,
        PAID,
        FAILED
    }
}
