package com.vehiclerental.decorator;
import java.math.BigDecimal;
public class ChildSeatDecorator extends VehicleDecorator {
    private static final BigDecimal CHILD_SEAT_COST = new BigDecimal("3.00");
    public ChildSeatDecorator(VehicleComponent wrapped) { super(wrapped); }
    @Override public String getDescription() { return wrapped.getDescription() + " + Child Seat"; }
    @Override public BigDecimal getDailyPrice() { return wrapped.getDailyPrice().add(CHILD_SEAT_COST); }
}
