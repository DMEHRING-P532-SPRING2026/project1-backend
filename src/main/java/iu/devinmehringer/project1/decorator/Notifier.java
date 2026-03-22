package iu.devinmehringer.project1.decorator;

import iu.devinmehringer.project1.model.trade.Trade;
import iu.devinmehringer.project1.model.user.User;

public interface Notifier {
    void notify(User user, Trade trade);
}
