package com.example.enumpatterns.examples;

import com.example.enumpatterns.statemachine.*;

/**
 * Demonstrates the state machine pattern using enums for order processing.
 * Shows how enum-based state machines provide type safety and clear business logic.
 */
public class StateMachineDemo {
    
    public static void main(String[] args) {
        System.out.println("=== Enum State Machine Demo ===\n");
        
        // Demo 1: Normal order flow
        System.out.println("Demo 1: Normal Order Processing Flow");
        System.out.println("------------------------------------");
        demoNormalOrderFlow();
        
        // Demo 2: Order cancellation
        System.out.println("\nDemo 2: Order Cancellation Flow");
        System.out.println("--------------------------------");
        demoCancellationFlow();
        
        // Demo 3: Invalid transitions
        System.out.println("\nDemo 3: Invalid State Transitions");
        System.out.println("----------------------------------");
        demoInvalidTransitions();
        
        // Demo 4: State validation and queries
        System.out.println("\nDemo 4: State Validation and Queries");
        System.out.println("-------------------------------------");
        demoStateQueries();
    }
    
    private static void demoNormalOrderFlow() {
        Order order = new Order("John Doe", 299.99, "123 Main St, Anytown, USA");
        
        try {
            // Show initial state
            System.out.println("Initial state: " + order.getState());
            System.out.println("Can modify: " + order.getState().canModify());
            
            // Update shipping address while still pending
            order.updateShippingAddress("456 Oak St, Anytown, USA");
            System.out.println("Updated shipping address while in PENDING state");
            
            // Confirm the order
            order.confirm("PAY-12345");
            System.out.println("State after confirmation: " + order.getState());
            
            // Ship the order
            order.ship("TRACK-98765");
            System.out.println("State after shipping: " + order.getState());
            
            // Deliver the order
            order.deliver("John Doe");
            System.out.println("State after delivery: " + order.getState());
            System.out.println("Is terminal state: " + order.getState().isTerminal());
            
            // Print order summary
            System.out.println("\n" + order.getSummary());
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
    
    private static void demoCancellationFlow() {
        Order order = new Order("Jane Smith", 199.99, "789 Elm St, Othertown, USA");
        
        try {
            System.out.println("Initial state: " + order.getState());
            
            // Cancel from pending state
            order.cancel("Customer requested cancellation");
            System.out.println("State after cancellation: " + order.getState());
            System.out.println("Is terminal state: " + order.getState().isTerminal());
            
            // Try to perform actions on cancelled order
            try {
                order.confirm("PAY-99999");
            } catch (IllegalStateException e) {
                System.out.println("Expected error: " + e.getMessage());
            }
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
        
        // Demo cancelling after confirmation
        Order order2 = new Order("Bob Johnson", 399.99, "321 Pine St, Somewhere, USA");
        
        try {
            order2.confirm("PAY-55555");
            System.out.println("\nConfirmed order state: " + order2.getState());
            
            order2.cancel("Payment issue");
            System.out.println("Successfully cancelled confirmed order");
            System.out.println("Final state: " + order2.getState());
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
    
    private static void demoInvalidTransitions() {
        Order order = new Order("Alice Brown", 149.99, "555 Maple Ave, Nowhere, USA");
        
        // Try to ship without confirming
        try {
            order.ship("INVALID-TRACK");
        } catch (IllegalStateException e) {
            System.out.println("Expected error when shipping unconfirmed order: " + e.getMessage());
        }
        
        // Try to modify after confirmation
        try {
            order.confirm("PAY-77777");
            order.updateShippingAddress("New address");
        } catch (IllegalStateException e) {
            System.out.println("Expected error when modifying confirmed order: " + e.getMessage());
        }
        
        // Try to deliver without shipping
        try {
            order.deliver("Someone");
        } catch (IllegalStateException e) {
            System.out.println("Expected error when delivering unshipped order: " + e.getMessage());
        }
        
        // Complete the order properly then try invalid transitions
        try {
            order.ship("TRACK-11111");
            order.deliver("Alice Brown");
            
            // Try to cancel delivered order
            order.cancel("Too late");
        } catch (IllegalStateException e) {
            System.out.println("Expected error when cancelling delivered order: " + e.getMessage());
        }
    }
    
    private static void demoStateQueries() {
        // Show available transitions from each state
        for (OrderState state : OrderState.values()) {
            System.out.println("\nState: " + state);
            System.out.println("  Description: " + state.getDescription());
            System.out.println("  Can modify: " + state.canModify());
            System.out.println("  Is terminal: " + state.isTerminal());
            System.out.println("  Valid transitions: " + state.getValidTransitions());
            System.out.println("  Valid actions: " + state.getValidActions());
        }
        
        // Demonstrate state checking
        System.out.println("\nState Transition Validation:");
        OrderState pending = OrderState.PENDING;
        System.out.println("Can transition from PENDING to CONFIRMED: " + 
            pending.canTransitionTo(OrderState.CONFIRMED));
        System.out.println("Can transition from PENDING to DELIVERED: " + 
            pending.canTransitionTo(OrderState.DELIVERED));
        
        // Show return flow
        System.out.println("\nReturn Flow Demo:");
        Order returnOrder = new Order("Return Customer", 99.99, "999 Return St");
        
        try {
            returnOrder.confirm("PAY-RETURN");
            returnOrder.ship("TRACK-RETURN");
            returnOrder.deliver("Customer");
            
            System.out.println("Order delivered, state: " + returnOrder.getState());
            
            returnOrder.returnOrder("Defective product");
            System.out.println("Order returned, state: " + returnOrder.getState());
            System.out.println("Is terminal: " + returnOrder.getState().isTerminal());
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}