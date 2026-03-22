package iu.devinmehringer.project1.decorator;

import iu.devinmehringer.project1.model.trade.Trade;
import iu.devinmehringer.project1.model.user.User;

public abstract class NotifierDecorator implements Notifier {
    protected final Notifier notifier;

    public NotifierDecorator(Notifier notifier) {
        this.notifier = notifier;
    }

    @Override
    public void notify(User user, Trade trade) {
        notifier.notify(user, trade);
    }
}
