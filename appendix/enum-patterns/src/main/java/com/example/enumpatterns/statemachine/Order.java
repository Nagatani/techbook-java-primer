package com.example.enumpatterns.statemachine;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Order class that uses the OrderState enum for state management.
 * Demonstrates practical usage of enum state machine in a real-world scenario.
 */
public class Order {
    private final String orderId;
    private OrderState state;
    private final List<StateTransition> transitionHistory;
    private final Instant createdAt;
    private Instant lastModifiedAt;
    private String customerName;
    private double totalAmount;
    private String shippingAddress;
    
    public Order(String customerName, double totalAmount, String shippingAddress) {
        this.orderId = UUID.randomUUID().toString();
        this.state = OrderState.PENDING;
        this.transitionHistory = new ArrayList<>();
        this.createdAt = Instant.now();
        this.lastModifiedAt = createdAt;
        this.customerName = customerName;
        this.totalAmount = totalAmount;
        this.shippingAddress = shippingAddress;
        
        // Record initial state
        transitionHistory.add(new StateTransition(null, state, "Order created", createdAt));
    }
    
    /**
     * Perform a state transition with validation
     */
    private void performTransition(OrderState newState, String reason) {
        OrderState oldState = this.state;
        
        // Validate the transition by checking if it's possible
        if (!oldState.canTransitionTo(newState)) {
            throw new IllegalStateException(
                String.format("Invalid transition from %s to %s", oldState, newState)
            );
        }
        
        // Perform the transition
        this.state = newState;
        this.lastModifiedAt = Instant.now();
        
        // Record the transition
        transitionHistory.add(new StateTransition(oldState, newState, reason, lastModifiedAt));
        
        // Log the transition
        System.out.printf("Order %s: %s -> %s (%s)%n", 
            orderId.substring(0, 8), oldState, newState, reason);
    }
    
    /**
     * Business methods that delegate to state machine
     */
    public void confirm(String paymentReference) {
        if (state.canModify()) {
            // Can modify order details before confirming if needed
            validateOrderDetails();
        }
        
        OrderState newState = state.confirm();
        performTransition(newState, "Payment confirmed: " + paymentReference);
    }
    
    public void ship(String trackingNumber) {
        OrderState newState = state.ship();
        performTransition(newState, "Shipped with tracking: " + trackingNumber);
    }
    
    public void deliver(String deliverySignature) {
        OrderState newState = state.deliver();
        performTransition(newState, "Delivered, signed by: " + deliverySignature);
    }
    
    public void cancel(String reason) {
        OrderState newState = state.cancel();
        performTransition(newState, "Cancelled: " + reason);
    }
    
    public void returnOrder(String returnReason) {
        OrderState newState = state.returnOrder();
        performTransition(newState, "Returned: " + returnReason);
    }
    
    /**
     * Update order details (only allowed in certain states)
     */
    public void updateShippingAddress(String newAddress) {
        if (!state.canModify()) {
            throw new IllegalStateException(
                "Cannot modify order in state: " + state
            );
        }
        this.shippingAddress = newAddress;
        this.lastModifiedAt = Instant.now();
    }
    
    /**
     * Validate order details before confirmation
     */
    private void validateOrderDetails() {
        if (customerName == null || customerName.trim().isEmpty()) {
            throw new IllegalStateException("Customer name is required");
        }
        if (totalAmount <= 0) {
            throw new IllegalStateException("Total amount must be positive");
        }
        if (shippingAddress == null || shippingAddress.trim().isEmpty()) {
            throw new IllegalStateException("Shipping address is required");
        }
    }
    
    /**
     * Get a summary of the order
     */
    public String getSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("Order Summary\n");
        sb.append("============\n");
        sb.append("Order ID: ").append(orderId).append("\n");
        sb.append("Customer: ").append(customerName).append("\n");
        sb.append("Amount: $").append(String.format("%.2f", totalAmount)).append("\n");
        sb.append("Status: ").append(state).append(" - ").append(state.getDescription()).append("\n");
        sb.append("Created: ").append(createdAt).append("\n");
        sb.append("Last Modified: ").append(lastModifiedAt).append("\n");
        sb.append("\nValid Actions: ").append(state.getValidActions()).append("\n");
        
        if (!transitionHistory.isEmpty()) {
            sb.append("\nTransition History:\n");
            for (StateTransition transition : transitionHistory) {
                sb.append("  ").append(transition).append("\n");
            }
        }
        
        return sb.toString();
    }
    
    // Getters
    public String getOrderId() { return orderId; }
    public OrderState getState() { return state; }
    public List<StateTransition> getTransitionHistory() { 
        return new ArrayList<>(transitionHistory); 
    }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getLastModifiedAt() { return lastModifiedAt; }
    public String getCustomerName() { return customerName; }
    public double getTotalAmount() { return totalAmount; }
    public String getShippingAddress() { return shippingAddress; }
    
    /**
     * Record of a state transition
     */
    public static record StateTransition(
        OrderState fromState,
        OrderState toState,
        String reason,
        Instant timestamp
    ) {
        @Override
        public String toString() {
            if (fromState == null) {
                return String.format("[%s] Initial state: %s - %s",
                    timestamp, toState, reason);
            }
            return String.format("[%s] %s -> %s - %s",
                timestamp, fromState, toState, reason);
        }
    }
}