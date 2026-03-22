package iu.devinmehringer.project1.decorator;

import iu.devinmehringer.project1.model.trade.Trade;
import iu.devinmehringer.project1.model.user.User;
import org.springframework.stereotype.Component;

@Component
public class ConsoleNotifier implements Notifier {

    @Override
    public void notify(User user, Trade trade) {
        System.out.println("[Console] " + user.getId() + " | Trade executed: " + trade);
    }
}
