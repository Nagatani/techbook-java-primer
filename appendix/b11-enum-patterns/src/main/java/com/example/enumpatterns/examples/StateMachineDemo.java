package com.example.enumpatterns.examples;

import com.example.enumpatterns.statemachine.*;

/**
 * Demonstrates the state machine pattern using enums for order processing.
 * Shows how enum-based state machines provide type safety and clear business logic.
 */
public class StateMachineDemo {
    
    public static void main(String[] args) {
        IO.println("=== Enum State Machine Demo ===\n");
        
        // Demo 1: Normal order flow
        IO.println("Demo 1: Normal Order Processing Flow");
        IO.println("------------------------------------");
        demoNormalOrderFlow();
        
        // Demo 2: Order cancellation
        IO.println("\nDemo 2: Order Cancellation Flow");
        IO.println("--------------------------------");
        demoCancellationFlow();
        
        // Demo 3: Invalid transitions
        IO.println("\nDemo 3: Invalid State Transitions");
        IO.println("----------------------------------");
        demoInvalidTransitions();
        
        // Demo 4: State validation and queries
        IO.println("\nDemo 4: State Validation and Queries");
        IO.println("-------------------------------------");
        demoStateQueries();
    }
    
    private static void demoNormalOrderFlow() {
        Order order = new Order("John Doe", 299.99, "123 Main St, Anytown, USA");
        
        try {
            // Show initial state
            IO.println("Initial state: " + order.getState());
            IO.println("Can modify: " + order.getState().canModify());
            
            // Update shipping address while still pending
            order.updateShippingAddress("456 Oak St, Anytown, USA");
            IO.println("Updated shipping address while in PENDING state");
            
            // Confirm the order
            order.confirm("PAY-12345");
            IO.println("State after confirmation: " + order.getState());
            
            // Ship the order
            order.ship("TRACK-98765");
            IO.println("State after shipping: " + order.getState());
            
            // Deliver the order
            order.deliver("John Doe");
            IO.println("State after delivery: " + order.getState());
            IO.println("Is terminal state: " + order.getState().isTerminal());
            
            // Print order summary
            IO.println("\n" + order.getSummary());
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
    
    private static void demoCancellationFlow() {
        Order order = new Order("Jane Smith", 199.99, "789 Elm St, Othertown, USA");
        
        try {
            IO.println("Initial state: " + order.getState());
            
            // Cancel from pending state
            order.cancel("Customer requested cancellation");
            IO.println("State after cancellation: " + order.getState());
            IO.println("Is terminal state: " + order.getState().isTerminal());
            
            // Try to perform actions on cancelled order
            try {
                order.confirm("PAY-99999");
            } catch (IllegalStateException e) {
                IO.println("Expected error: " + e.getMessage());
            }
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
        
        // Demo cancelling after confirmation
        Order order2 = new Order("Bob Johnson", 399.99, "321 Pine St, Somewhere, USA");
        
        try {
            order2.confirm("PAY-55555");
            IO.println("\nConfirmed order state: " + order2.getState());
            
            order2.cancel("Payment issue");
            IO.println("Successfully cancelled confirmed order");
            IO.println("Final state: " + order2.getState());
            
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
            IO.println("Expected error when shipping unconfirmed order: " + e.getMessage());
        }
        
        // Try to modify after confirmation
        try {
            order.confirm("PAY-77777");
            order.updateShippingAddress("New address");
        } catch (IllegalStateException e) {
            IO.println("Expected error when modifying confirmed order: " + e.getMessage());
        }
        
        // Try to deliver without shipping
        try {
            order.deliver("Someone");
        } catch (IllegalStateException e) {
            IO.println("Expected error when delivering unshipped order: " + e.getMessage());
        }
        
        // Complete the order properly then try invalid transitions
        try {
            order.ship("TRACK-11111");
            order.deliver("Alice Brown");
            
            // Try to cancel delivered order
            order.cancel("Too late");
        } catch (IllegalStateException e) {
            IO.println("Expected error when cancelling delivered order: " + e.getMessage());
        }
    }
    
    private static void demoStateQueries() {
        // Show available transitions from each state
        for (OrderState state : OrderState.values()) {
            IO.println("\nState: " + state);
            IO.println("  Description: " + state.getDescription());
            IO.println("  Can modify: " + state.canModify());
            IO.println("  Is terminal: " + state.isTerminal());
            IO.println("  Valid transitions: " + state.getValidTransitions());
            IO.println("  Valid actions: " + state.getValidActions());
        }
        
        // Demonstrate state checking
        IO.println("\nState Transition Validation:");
        OrderState pending = OrderState.PENDING;
        IO.println("Can transition from PENDING to CONFIRMED: " + 
            pending.canTransitionTo(OrderState.CONFIRMED));
        IO.println("Can transition from PENDING to DELIVERED: " + 
            pending.canTransitionTo(OrderState.DELIVERED));
        
        // Show return flow
        IO.println("\nReturn Flow Demo:");
        Order returnOrder = new Order("Return Customer", 99.99, "999 Return St");
        
        try {
            returnOrder.confirm("PAY-RETURN");
            returnOrder.ship("TRACK-RETURN");
            returnOrder.deliver("Customer");
            
            IO.println("Order delivered, state: " + returnOrder.getState());
            
            returnOrder.returnOrder("Defective product");
            IO.println("Order returned, state: " + returnOrder.getState());
            IO.println("Is terminal: " + returnOrder.getState().isTerminal());
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}