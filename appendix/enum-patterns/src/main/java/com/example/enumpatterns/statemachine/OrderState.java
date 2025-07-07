package com.example.enumpatterns.statemachine;

import java.util.EnumSet;
import java.util.Set;

/**
 * Order processing state machine with compile-time validated transitions.
 * Demonstrates how enum can enforce business rules and prevent invalid states.
 */
public enum OrderState {
    /**
     * Initial state when order is created
     */
    PENDING {
        @Override
        public OrderState cancel() {
            return CANCELLED;
        }
        
        @Override
        public OrderState confirm() {
            return CONFIRMED;
        }
        
        @Override
        public boolean canModify() {
            return true;
        }
        
        @Override
        public String getDescription() {
            return "Order has been created and is awaiting confirmation";
        }
    },
    
    /**
     * Order has been confirmed and payment processed
     */
    CONFIRMED {
        @Override
        public OrderState ship() {
            return SHIPPED;
        }
        
        @Override
        public OrderState cancel() {
            // Business rule: confirmed orders can be cancelled with restrictions
            return CANCELLED;
        }
        
        @Override
        public boolean canModify() {
            return false;
        }
        
        @Override
        public String getDescription() {
            return "Order has been confirmed and payment processed";
        }
    },
    
    /**
     * Order has been shipped to customer
     */
    SHIPPED {
        @Override
        public OrderState deliver() {
            return DELIVERED;
        }
        
        @Override
        public OrderState returnOrder() {
            return RETURNED;
        }
        
        @Override
        public boolean canModify() {
            return false;
        }
        
        @Override
        public String getDescription() {
            return "Order has been shipped and is in transit";
        }
    },
    
    /**
     * Order has been delivered to customer
     */
    DELIVERED {
        @Override
        public boolean canModify() {
            return false;
        }
        
        @Override
        public boolean isTerminal() {
            return true;
        }
        
        @Override
        public OrderState returnOrder() {
            return RETURNED;
        }
        
        @Override
        public String getDescription() {
            return "Order has been successfully delivered to customer";
        }
    },
    
    /**
     * Order was cancelled
     */
    CANCELLED {
        @Override
        public boolean canModify() {
            return false;
        }
        
        @Override
        public boolean isTerminal() {
            return true;
        }
        
        @Override
        public String getDescription() {
            return "Order has been cancelled";
        }
    },
    
    /**
     * Order was returned by customer
     */
    RETURNED {
        @Override
        public boolean canModify() {
            return false;
        }
        
        @Override
        public boolean isTerminal() {
            return true;
        }
        
        @Override
        public String getDescription() {
            return "Order has been returned by customer";
        }
    };
    
    // Default implementations that throw exceptions for invalid transitions
    public OrderState confirm() {
        throw new IllegalStateException("Cannot confirm order in state: " + this);
    }
    
    public OrderState ship() {
        throw new IllegalStateException("Cannot ship order in state: " + this);
    }
    
    public OrderState deliver() {
        throw new IllegalStateException("Cannot deliver order in state: " + this);
    }
    
    public OrderState cancel() {
        throw new IllegalStateException("Cannot cancel order in state: " + this);
    }
    
    public OrderState returnOrder() {
        throw new IllegalStateException("Cannot return order in state: " + this);
    }
    
    /**
     * Whether the order can be modified in this state
     */
    public abstract boolean canModify();
    
    /**
     * Whether this is a terminal state (no further transitions possible)
     */
    public boolean isTerminal() {
        return false;
    }
    
    /**
     * Human-readable description of the state
     */
    public abstract String getDescription();
    
    /**
     * Get all valid transitions from this state
     */
    public Set<OrderState> getValidTransitions() {
        Set<OrderState> transitions = EnumSet.noneOf(OrderState.class);
        
        // Try each possible transition and collect valid ones
        try { transitions.add(confirm()); } catch (IllegalStateException ignored) {}
        try { transitions.add(ship()); } catch (IllegalStateException ignored) {}
        try { transitions.add(deliver()); } catch (IllegalStateException ignored) {}
        try { transitions.add(cancel()); } catch (IllegalStateException ignored) {}
        try { transitions.add(returnOrder()); } catch (IllegalStateException ignored) {}
        
        return transitions;
    }
    
    /**
     * Check if a specific transition is valid from this state
     */
    public boolean canTransitionTo(OrderState targetState) {
        return getValidTransitions().contains(targetState);
    }
    
    /**
     * Get valid actions from this state
     */
    public Set<String> getValidActions() {
        Set<String> actions = EnumSet.noneOf(OrderAction.class)
            .stream()
            .map(Enum::name)
            .collect(java.util.stream.Collectors.toSet());
        
        try { confirm(); actions.add("CONFIRM"); } catch (IllegalStateException ignored) {}
        try { ship(); actions.add("SHIP"); } catch (IllegalStateException ignored) {}
        try { deliver(); actions.add("DELIVER"); } catch (IllegalStateException ignored) {}
        try { cancel(); actions.add("CANCEL"); } catch (IllegalStateException ignored) {}
        try { returnOrder(); actions.add("RETURN"); } catch (IllegalStateException ignored) {}
        
        return actions;
    }
    
    /**
     * Enum for order actions (used for clarity in some contexts)
     */
    public enum OrderAction {
        CONFIRM, SHIP, DELIVER, CANCEL, RETURN
    }
}