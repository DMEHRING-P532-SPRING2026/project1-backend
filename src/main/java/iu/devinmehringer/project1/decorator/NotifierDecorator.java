package iu.devinmehringer.project1.decorator;

public abstract class NotifierDecorator implements Notifier {
    protected final Notifier notifier;

    public NotifierDecorator(Notifier notifier) {
        this.notifier = notifier;
    }

    @Override
    public void notify(String message) {
        notifier.notify(message);
    }
}
