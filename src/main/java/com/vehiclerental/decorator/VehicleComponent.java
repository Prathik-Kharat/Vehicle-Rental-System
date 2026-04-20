package com.vehiclerental.decorator;
import java.math.BigDecimal;

/**
 * Design Pattern: Decorator (Structural)
 * Base component interface for the Vehicle decorator chain.
 */
public interface VehicleComponent {
    String getDescription();
    BigDecimal getDailyPrice();
}
