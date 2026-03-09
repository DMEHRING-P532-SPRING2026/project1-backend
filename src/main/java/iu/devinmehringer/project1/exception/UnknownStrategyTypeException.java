package iu.devinmehringer.project1.exception;

public class UnknownStrategyTypeException extends RuntimeException {
    public UnknownStrategyTypeException(String type) {
        super("Unknown strategy: " + type);
    }
}
