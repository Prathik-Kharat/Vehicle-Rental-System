package com.vehiclerental.decorator;
import java.math.BigDecimal;
public class InsuranceDecorator extends VehicleDecorator {
    private static final BigDecimal INSURANCE_COST = new BigDecimal("10.00");
    public InsuranceDecorator(VehicleComponent wrapped) { super(wrapped); }
    @Override public String getDescription() { return wrapped.getDescription() + " + Insurance"; }
    @Override public BigDecimal getDailyPrice() { return wrapped.getDailyPrice().add(INSURANCE_COST); }
}
