package com.vehiclerental.decorator;
import java.math.BigDecimal;
public class GpsDecorator extends VehicleDecorator {
    private static final BigDecimal GPS_COST = new BigDecimal("5.00");
    public GpsDecorator(VehicleComponent wrapped) { super(wrapped); }
    @Override public String getDescription() { return wrapped.getDescription() + " + GPS"; }
    @Override public BigDecimal getDailyPrice() { return wrapped.getDailyPrice().add(GPS_COST); }
}
