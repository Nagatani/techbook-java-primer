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
        IO.println("=== Enum Strategy Pattern Demo ===\n");
        
        // Demo 1: Basic pricing strategies
        IO.println("Demo 1: Basic Pricing Strategies");
        IO.println("---------------------------------");
        demoBasicPricing();
        
        // Demo 2: Progressive taxation
        IO.println("\nDemo 2: Progressive Taxation");
        IO.println("-----------------------------");
        demoProgressiveTaxation();
        
        // Demo 3: Member pricing with tiers
        IO.println("\nDemo 3: Member Pricing with Tiers");
        IO.println("----------------------------------");
        demoMemberPricing();
        
        // Demo 4: Seasonal pricing
        IO.println("\nDemo 4: Seasonal Pricing");
        IO.println("-------------------------");
        demoSeasonalPricing();
        
        // Demo 5: Strategy comparison
        IO.println("\nDemo 5: Strategy Comparison");
        IO.println("----------------------------");
        compareStrategies();
    }
    
    private static void demoBasicPricing() {
        BigDecimal amount = new BigDecimal("1000.00");
        PricingStrategy.TaxContext taxContext = new PricingStrategy.TaxContext(new BigDecimal("0.08")); // 8% tax
        PricingStrategy.DiscountContext discountContext = new PricingStrategy.DiscountContext(new BigDecimal("0.10")); // 10% discount
        
        // Standard pricing
        PricingStrategy.PriceCalculation standard = PricingStrategy.STANDARD.calculate(amount, taxContext, discountContext);
        IO.println("Standard Pricing:");
        IO.println(standard.getBreakdown());
        
        // Promotional pricing
        PricingStrategy.PriceCalculation promotional = PricingStrategy.PROMOTIONAL.calculate(amount, taxContext, discountContext);
        IO.println("\nPromotional Pricing:");
        IO.println(promotional.getBreakdown());
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
            IO.println("\nAmount: $" + amount);
            IO.println("Progressive tax: $" + calc.tax());
            IO.println("Total: $" + calc.total());
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
            
            IO.println("\n" + tier + " Member:");
            IO.println("Base Amount: $" + calc.baseAmount());
            IO.println("Discount: $" + calc.discount());
            IO.println("Final Price: $" + calc.total());
        }
    }
    
    private static void demoSeasonalPricing() {
        BigDecimal amount = new BigDecimal("200.00");
        PricingStrategy.TaxContext tax = new PricingStrategy.TaxContext(new BigDecimal("0.08"));
        PricingStrategy.DiscountContext discount = new PricingStrategy.DiscountContext(new BigDecimal("0.10"));
        
        PricingStrategy.PriceCalculation seasonal = 
            PricingStrategy.SEASONAL.calculate(amount, tax, discount);
        
        IO.println("Seasonal Pricing (current date):");
        IO.println(seasonal.getBreakdown());
        IO.println("\nNote: Seasonal multipliers:");
        IO.println("- November/December: 1.5x discount (holiday season)");
        IO.println("- June/July: 1.3x discount (summer sale)");
        IO.println("- Other months: 1.0x discount (regular)");
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
        IO.println("Strategy Comparison Table:");
        IO.println("Amount   | Standard | Progressive | Promotional | Member (Platinum) | Seasonal");
        IO.println("---------|----------|-------------|-------------|-------------------|----------");
        
        for (BigDecimal amount : amounts) {
            IO.print("$%,7.0f |".formatted(amount));
            
            // Standard
            BigDecimal standardTotal = PricingStrategy.STANDARD
                .calculate(amount, standardTax, standardDiscount).total();
            IO.print(" $%,7.2f |".formatted(standardTotal));
            
            // Progressive
            BigDecimal progressiveTotal = PricingStrategy.PROGRESSIVE
                .calculate(amount, progressiveTax, standardDiscount).total();
            IO.print(" $%,9.2f |".formatted(progressiveTotal));
            
            // Promotional
            BigDecimal promotionalTotal = PricingStrategy.PROMOTIONAL
                .calculate(amount, standardTax, standardDiscount).total();
            IO.print(" $%,9.2f |".formatted(promotionalTotal));
            
            // Member
            BigDecimal memberTotal = PricingStrategy.MEMBER
                .calculate(amount, standardTax, platinumDiscount).total();
            IO.print(" $%,15.2f |".formatted(memberTotal));
            
            // Seasonal
            BigDecimal seasonalTotal = PricingStrategy.SEASONAL
                .calculate(amount, standardTax, standardDiscount).total();
            IO.print(" $%,7.2f".formatted(seasonalTotal));
            
            IO.println();
        }
        
        // Show savings analysis
        IO.println("\nSavings Analysis for $1000 purchase:");
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
            IO.println("%s: Save $%.2f (%.1f%%)".formatted(
                entry.getKey(), 
                entry.getValue(),
                entry.getValue().multiply(new BigDecimal("100")).divide(standardPrice, 1, java.math.RoundingMode.HALF_UP)
            ));
        }
    }
}