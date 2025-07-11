package com.example.enumpatterns.strategy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;

/**
 * Pricing strategy implementation using enums.
 * Demonstrates how enums can replace complex if-else chains with polymorphic behavior.
 */
public enum PricingStrategy {
    /**
     * Standard pricing with fixed tax and discount rates
     */
    STANDARD {
        @Override
        public BigDecimal calculateTax(BigDecimal amount, TaxContext context) {
            return amount.multiply(context.getTaxRate())
                .setScale(2, RoundingMode.HALF_UP);
        }
        
        @Override
        public BigDecimal calculateDiscount(BigDecimal amount, DiscountContext context) {
            return amount.multiply(context.getDiscountRate())
                .setScale(2, RoundingMode.HALF_UP);
        }
        
        @Override
        public String getDescription() {
            return "Standard pricing with fixed rates";
        }
    },
    
    /**
     * Progressive pricing with tiered tax brackets
     */
    PROGRESSIVE {
        @Override
        public BigDecimal calculateTax(BigDecimal amount, TaxContext context) {
            BigDecimal tax = BigDecimal.ZERO;
            BigDecimal remaining = amount;
            
            for (TaxBracket bracket : context.getTaxBrackets()) {
                if (remaining.compareTo(BigDecimal.ZERO) <= 0) break;
                
                BigDecimal bracketAmount = bracket.upperLimit()
                    .subtract(bracket.lowerLimit());
                BigDecimal taxableInBracket = remaining.min(bracketAmount);
                
                tax = tax.add(taxableInBracket.multiply(bracket.rate()));
                remaining = remaining.subtract(taxableInBracket);
            }
            
            return tax.setScale(2, RoundingMode.HALF_UP);
        }
        
        @Override
        public BigDecimal calculateDiscount(BigDecimal amount, DiscountContext context) {
            // Volume-based discount
            BigDecimal discountRate;
            if (amount.compareTo(new BigDecimal("1000")) >= 0) {
                discountRate = new BigDecimal("0.15");
            } else if (amount.compareTo(new BigDecimal("500")) >= 0) {
                discountRate = new BigDecimal("0.10");
            } else if (amount.compareTo(new BigDecimal("100")) >= 0) {
                discountRate = new BigDecimal("0.05");
            } else {
                discountRate = BigDecimal.ZERO;
            }
            
            return amount.multiply(discountRate)
                .setScale(2, RoundingMode.HALF_UP);
        }
        
        @Override
        public String getDescription() {
            return "Progressive pricing with tiered rates";
        }
    },
    
    /**
     * Promotional pricing with special rules
     */
    PROMOTIONAL {
        @Override
        public BigDecimal calculateTax(BigDecimal amount, TaxContext context) {
            // Reduced tax during promotions
            BigDecimal standardTax = STANDARD.calculateTax(amount, context);
            return standardTax.multiply(new BigDecimal("0.5"))
                .setScale(2, RoundingMode.HALF_UP);
        }
        
        @Override
        public BigDecimal calculateDiscount(BigDecimal amount, DiscountContext context) {
            // Enhanced discount with maximum cap
            BigDecimal maxDiscount = new BigDecimal("100");
            BigDecimal enhancedRate = context.getDiscountRate()
                .multiply(new BigDecimal("2"));
            BigDecimal calculatedDiscount = amount.multiply(enhancedRate);
            
            return calculatedDiscount.min(maxDiscount)
                .setScale(2, RoundingMode.HALF_UP);
        }
        
        @Override
        public String getDescription() {
            return "Promotional pricing with special offers";
        }
    },
    
    /**
     * Seasonal pricing with date-based adjustments
     */
    SEASONAL {
        @Override
        public BigDecimal calculateTax(BigDecimal amount, TaxContext context) {
            // Standard tax year-round
            return STANDARD.calculateTax(amount, context);
        }
        
        @Override
        public BigDecimal calculateDiscount(BigDecimal amount, DiscountContext context) {
            LocalDate today = LocalDate.now();
            BigDecimal seasonalMultiplier = getSeasonalMultiplier(today);
            
            BigDecimal baseDiscount = amount.multiply(context.getDiscountRate());
            return baseDiscount.multiply(seasonalMultiplier)
                .setScale(2, RoundingMode.HALF_UP);
        }
        
        private BigDecimal getSeasonalMultiplier(LocalDate date) {
            Month month = date.getMonth();
            
            // Holiday season: enhanced discounts
            if (month == Month.NOVEMBER || month == Month.DECEMBER) {
                return new BigDecimal("1.5");
            }
            // Summer sale
            else if (month == Month.JUNE || month == Month.JULY) {
                return new BigDecimal("1.3");
            }
            // Regular season
            else {
                return BigDecimal.ONE;
            }
        }
        
        @Override
        public String getDescription() {
            return "Seasonal pricing with date-based adjustments";
        }
    },
    
    /**
     * Member pricing with loyalty benefits
     */
    MEMBER {
        @Override
        public BigDecimal calculateTax(BigDecimal amount, TaxContext context) {
            // Members get standard tax
            return STANDARD.calculateTax(amount, context);
        }
        
        @Override
        public BigDecimal calculateDiscount(BigDecimal amount, DiscountContext context) {
            // Base member discount
            BigDecimal memberDiscount = new BigDecimal("0.10");
            
            // Additional discount based on membership tier
            BigDecimal tierMultiplier = switch (context.getMembershipTier()) {
                case BRONZE -> BigDecimal.ONE;
                case SILVER -> new BigDecimal("1.5");
                case GOLD -> new BigDecimal("2.0");
                case PLATINUM -> new BigDecimal("3.0");
            };
            
            BigDecimal totalDiscountRate = memberDiscount.multiply(tierMultiplier);
            return amount.multiply(totalDiscountRate)
                .setScale(2, RoundingMode.HALF_UP);
        }
        
        @Override
        public String getDescription() {
            return "Member pricing with loyalty benefits";
        }
    };
    
    /**
     * Calculate tax for the given amount
     */
    public abstract BigDecimal calculateTax(BigDecimal amount, TaxContext context);
    
    /**
     * Calculate discount for the given amount
     */
    public abstract BigDecimal calculateDiscount(BigDecimal amount, DiscountContext context);
    
    /**
     * Get human-readable description
     */
    public abstract String getDescription();
    
    /**
     * Calculate complete pricing breakdown
     */
    public PriceCalculation calculate(
            BigDecimal baseAmount, 
            TaxContext taxContext, 
            DiscountContext discountContext) {
        
        BigDecimal discount = calculateDiscount(baseAmount, discountContext);
        BigDecimal subtotal = baseAmount.subtract(discount);
        BigDecimal tax = calculateTax(subtotal, taxContext);
        BigDecimal total = subtotal.add(tax);
        
        return new PriceCalculation(
            baseAmount,
            discount,
            subtotal,
            tax,
            total,
            this
        );
    }
    
    /**
     * Price calculation result
     */
    public static record PriceCalculation(
        BigDecimal baseAmount,
        BigDecimal discount,
        BigDecimal subtotal,
        BigDecimal tax,
        BigDecimal total,
        PricingStrategy strategy
    ) {
        public String getBreakdown() {
            StringBuilder sb = new StringBuilder();
            sb.append("Price Breakdown (").append(strategy.getDescription()).append(")\n");
            sb.append("=====================================\n");
            sb.append(String.format("Base Amount:    $%,10.2f%n", baseAmount));
            sb.append(String.format("Discount:      -$%,10.2f%n", discount));
            sb.append(String.format("Subtotal:       $%,10.2f%n", subtotal));
            sb.append(String.format("Tax:           +$%,10.2f%n", tax));
            sb.append("-------------------------------------\n");
            sb.append(String.format("Total:          $%,10.2f%n", total));
            
            return sb.toString();
        }
    }
    
    /**
     * Tax calculation context
     */
    public static class TaxContext {
        private final BigDecimal taxRate;
        private final List<TaxBracket> taxBrackets;
        
        // Simple tax context
        public TaxContext(BigDecimal taxRate) {
            this.taxRate = taxRate;
            this.taxBrackets = List.of();
        }
        
        // Progressive tax context
        public TaxContext(List<TaxBracket> taxBrackets) {
            this.taxRate = BigDecimal.ZERO;
            this.taxBrackets = List.copyOf(taxBrackets);
        }
        
        public BigDecimal getTaxRate() { return taxRate; }
        public List<TaxBracket> getTaxBrackets() { return taxBrackets; }
    }
    
    /**
     * Tax bracket for progressive taxation
     */
    public static record TaxBracket(
        BigDecimal lowerLimit,
        BigDecimal upperLimit,
        BigDecimal rate
    ) {}
    
    /**
     * Discount calculation context
     */
    public static class DiscountContext {
        private final BigDecimal discountRate;
        private final MembershipTier membershipTier;
        
        // Simple discount context
        public DiscountContext(BigDecimal discountRate) {
            this.discountRate = discountRate;
            this.membershipTier = MembershipTier.BRONZE;
        }
        
        // Member discount context
        public DiscountContext(BigDecimal discountRate, MembershipTier tier) {
            this.discountRate = discountRate;
            this.membershipTier = tier;
        }
        
        public BigDecimal getDiscountRate() { return discountRate; }
        public MembershipTier getMembershipTier() { return membershipTier; }
    }
    
    /**
     * Membership tiers for member pricing
     */
    public enum MembershipTier {
        BRONZE, SILVER, GOLD, PLATINUM
    }
}