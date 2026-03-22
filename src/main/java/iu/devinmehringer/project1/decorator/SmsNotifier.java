package iu.devinmehringer.project1.decorator;

import iu.devinmehringer.project1.model.trade.Trade;
import iu.devinmehringer.project1.model.user.User;

public class SmsNotifier extends NotifierDecorator {

    public SmsNotifier(Notifier notifier) {
        super(notifier);
    }

    @Override
    public void notify(User user, Trade trade) {
        System.out.println("[SMS] To:" +  user.getId()
                + " | Status: " + trade.getStatus()
                + " | " + trade.getSide() + " " + trade.getTicker()
                + " x" + trade.getQuantity()
                + " @ " + trade.getPrice());
        super.notify(user, trade);
    }
}