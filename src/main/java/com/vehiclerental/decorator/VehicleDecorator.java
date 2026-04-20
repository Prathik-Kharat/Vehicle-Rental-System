package com.vehiclerental.decorator;
import java.math.BigDecimal;

/** Abstract decorator. */
public abstract class VehicleDecorator implements VehicleComponent {
    protected final VehicleComponent wrapped;
    public VehicleDecorator(VehicleComponent wrapped) { this.wrapped = wrapped; }
    @Override public String getDescription() { return wrapped.getDescription(); }
    @Override public BigDecimal getDailyPrice() { return wrapped.getDailyPrice(); }
}
