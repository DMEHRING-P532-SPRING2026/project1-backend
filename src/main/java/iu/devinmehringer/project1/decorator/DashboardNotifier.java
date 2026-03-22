package iu.devinmehringer.project1.decorator;

import iu.devinmehringer.project1.model.trade.Trade;
import iu.devinmehringer.project1.model.user.User;
import iu.devinmehringer.project1.service.WebSocketService;

public class DashboardNotifier extends NotifierDecorator {

    private final WebSocketService webSocketService;

    public DashboardNotifier(Notifier notifier, WebSocketService webSocketService) {
        super(notifier);
        this.webSocketService = webSocketService;
    }

    @Override
    public void notify(User user, Trade trade) {
        webSocketService.sendAlert(user.getId());
        System.out.println("[DashBoard] sent alert to: " + user.getId());
        super.notify(user, trade);
    }
}