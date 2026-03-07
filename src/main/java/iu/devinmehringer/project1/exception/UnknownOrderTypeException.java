package iu.devinmehringer.project1.exception;

public class UnknownOrderTypeException extends RuntimeException {
    public UnknownOrderTypeException(String orderType) {
        super("Unknown order type: " + orderType);
    }
}
