package com.example.enumpatterns.examples;

import com.example.enumpatterns.strategy.*;
import java.math.BigDecimal;
import java.util.*;

/**
 * Demonstrates the strategy pattern implementation using enums.
 * Shows how enums can replace complex if-else chains with polymorphic behavior.
 */
public class StrategyPatternDemo {
    
    public static void main(String[] args) {
        System.out.println("=== Enum Strategy Pattern Demo ===\n");
        
        // Demo 1: Basic pricing strategies
        System.out.println("Demo 1: Basic Pricing Strategies");
        System.out.println("---------------------------------");
        demoBasicPricing();
        
        // Demo 2: Progressive taxation
        System.out.println("\nDemo 2: Progressive Taxation");
        System.out.println("-----------------------------");
        demoProgressiveTaxation();
        
        // Demo 3: Member pricing with tiers
        System.out.println("\nDemo 3: Member Pricing with Tiers");
        System.out.println("----------------------------------");
        demoMemberPricing();
        
        // Demo 4: Seasonal pricing
        System.out.println("\nDemo 4: Seasonal Pricing");
        System.out.println("-------------------------");
        demoSeasonalPricing();
        
        // Demo 5: Strategy comparison
        System.out.println("\nDemo 5: Strategy Comparison");
        System.out.println("----------------------------");
        compareStrategies();
    }
    
    private static void demoBasicPricing() {
        BigDecimal amount = new BigDecimal("1000.00");
        PricingStrategy.TaxContext taxContext = new PricingStrategy.TaxContext(new BigDecimal("0.08")); // 8% tax
        PricingStrategy.DiscountContext discountContext = new PricingStrategy.DiscountContext(new BigDecimal("0.10")); // 10% discount
        
        // Standard pricing
        PricingStrategy.PriceCalculation standard = PricingStrategy.STANDARD.calculate(amount, taxContext, discountContext);
        System.out.println("Standard Pricing:");
        System.out.println(standard.getBreakdown());
        
        // Promotional pricing
        PricingStrategy.PriceCalculation promotional = PricingStrategy.PROMOTIONAL.calculate(amount, taxContext, discountContext);
        System.out.println("\nPromotional Pricing:");
        System.out.println(promotional.getBreakdown());
    }
    
    private static void demoProgressiveTaxation() {
        // Create tax brackets
        List<PricingStrategy.TaxBracket> brackets = List.of(
            new PricingStrategy.TaxBracket(BigDecimal.ZERO, new BigDecimal("500"), new BigDecimal("0.05")),      // 5% up to $500
            new PricingStrategy.TaxBracket(new BigDecimal("500"), new BigDecimal("1000"), new BigDecimal("0.08")), // 8% $500-$1000
            new PricingStrategy.TaxBracket(new BigDecimal("1000"), new BigDecimal("2000"), new BigDecimal("0.10")) // 10% $1000-$2000
        );
        
        PricingStrategy.TaxContext progressiveTax = new PricingStrategy.TaxContext(brackets);
        PricingStrategy.DiscountContext discount = new PricingStrategy.DiscountContext(new BigDecimal("0.05"));
        
        // Test with different amounts
        BigDecimal[] amounts = {
            new BigDecimal("300"),
            new BigDecimal("750"),
            new BigDecimal("1500")
        };
        
        for (BigDecimal amount : amounts) {
            PricingStrategy.PriceCalculation calc = PricingStrategy.PROGRESSIVE.calculate(amount, progressiveTax, discount);
            System.out.println("\nAmount: $" + amount);
            System.out.println("Progressive tax: $" + calc.tax());
            System.out.println("Total: $" + calc.total());
        }
    }
    
    private static void demoMemberPricing() {
        BigDecimal amount = new BigDecimal("500.00");
        PricingStrategy.TaxContext tax = new PricingStrategy.TaxContext(new BigDecimal("0.08"));
        
        // Different membership tiers
        for (PricingStrategy.MembershipTier tier : PricingStrategy.MembershipTier.values()) {
            PricingStrategy.DiscountContext memberDiscount = 
                new PricingStrategy.DiscountContext(BigDecimal.ZERO, tier);
            
            PricingStrategy.PriceCalculation calc = 
                PricingStrategy.MEMBER.calculate(amount, tax, memberDiscount);
            
            System.out.println("\n" + tier + " Member:");
            System.out.println("Base Amount: $" + calc.baseAmount());
            System.out.println("Discount: $" + calc.discount());
            System.out.println("Final Price: $" + calc.total());
        }
    }
    
    private static void demoSeasonalPricing() {
        BigDecimal amount = new BigDecimal("200.00");
        PricingStrategy.TaxContext tax = new PricingStrategy.TaxContext(new BigDecimal("0.08"));
        PricingStrategy.DiscountContext discount = new PricingStrategy.DiscountContext(new BigDecimal("0.10"));
        
        PricingStrategy.PriceCalculation seasonal = 
            PricingStrategy.SEASONAL.calculate(amount, tax, discount);
        
        System.out.println("Seasonal Pricing (current date):");
        System.out.println(seasonal.getBreakdown());
        System.out.println("\nNote: Seasonal multipliers:");
        System.out.println("- November/December: 1.5x discount (holiday season)");
        System.out.println("- June/July: 1.3x discount (summer sale)");
        System.out.println("- Other months: 1.0x discount (regular)");
    }
    
    private static void compareStrategies() {
        // Setup common parameters
        BigDecimal[] amounts = {
            new BigDecimal("100"),
            new BigDecimal("500"),
            new BigDecimal("1000"),
            new BigDecimal("2000")
        };
        
        PricingStrategy.TaxContext standardTax = new PricingStrategy.TaxContext(new BigDecimal("0.08"));
        PricingStrategy.TaxContext progressiveTax = new PricingStrategy.TaxContext(List.of(
            new PricingStrategy.TaxBracket(BigDecimal.ZERO, new BigDecimal("500"), new BigDecimal("0.05")),
            new PricingStrategy.TaxBracket(new BigDecimal("500"), new BigDecimal("1000"), new BigDecimal("0.08")),
            new PricingStrategy.TaxBracket(new BigDecimal("1000"), new BigDecimal("5000"), new BigDecimal("0.10"))
        ));
        
        PricingStrategy.DiscountContext standardDiscount = new PricingStrategy.DiscountContext(new BigDecimal("0.10"));
        PricingStrategy.DiscountContext platinumDiscount = new PricingStrategy.DiscountContext(
            BigDecimal.ZERO, PricingStrategy.MembershipTier.PLATINUM
        );
        
        // Compare all strategies
        System.out.println("Strategy Comparison Table:");
        System.out.println("Amount   | Standard | Progressive | Promotional | Member (Platinum) | Seasonal");
        System.out.println("---------|----------|-------------|-------------|-------------------|----------");
        
        for (BigDecimal amount : amounts) {
            System.out.printf("$%,7.0f |", amount);
            
            // Standard
            BigDecimal standardTotal = PricingStrategy.STANDARD
                .calculate(amount, standardTax, standardDiscount).total();
            System.out.printf(" $%,7.2f |", standardTotal);
            
            // Progressive
            BigDecimal progressiveTotal = PricingStrategy.PROGRESSIVE
                .calculate(amount, progressiveTax, standardDiscount).total();
            System.out.printf(" $%,9.2f |", progressiveTotal);
            
            // Promotional
            BigDecimal promotionalTotal = PricingStrategy.PROMOTIONAL
                .calculate(amount, standardTax, standardDiscount).total();
            System.out.printf(" $%,9.2f |", promotionalTotal);
            
            // Member
            BigDecimal memberTotal = PricingStrategy.MEMBER
                .calculate(amount, standardTax, platinumDiscount).total();
            System.out.printf(" $%,15.2f |", memberTotal);
            
            // Seasonal
            BigDecimal seasonalTotal = PricingStrategy.SEASONAL
                .calculate(amount, standardTax, standardDiscount).total();
            System.out.printf(" $%,7.2f", seasonalTotal);
            
            System.out.println();
        }
        
        // Show savings analysis
        System.out.println("\nSavings Analysis for $1000 purchase:");
        BigDecimal baseAmount = new BigDecimal("1000");
        BigDecimal standardPrice = PricingStrategy.STANDARD
            .calculate(baseAmount, standardTax, standardDiscount).total();
        
        Map<String, BigDecimal> savings = new LinkedHashMap<>();
        
        savings.put("Progressive Tax", standardPrice.subtract(
            PricingStrategy.PROGRESSIVE.calculate(baseAmount, progressiveTax, standardDiscount).total()
        ));
        
        savings.put("Promotional", standardPrice.subtract(
            PricingStrategy.PROMOTIONAL.calculate(baseAmount, standardTax, standardDiscount).total()
        ));
        
        savings.put("Platinum Member", standardPrice.subtract(
            PricingStrategy.MEMBER.calculate(baseAmount, standardTax, platinumDiscount).total()
        ));
        
        for (Map.Entry<String, BigDecimal> entry : savings.entrySet()) {
            System.out.printf("%s: Save $%.2f (%.1f%%)%n", 
                entry.getKey(), 
                entry.getValue(),
                entry.getValue().multiply(new BigDecimal("100")).divide(standardPrice, 1, java.math.RoundingMode.HALF_UP)
            );
        }
    }
}