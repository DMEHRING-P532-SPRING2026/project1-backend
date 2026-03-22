package iu.devinmehringer.project1.decorator;

import iu.devinmehringer.project1.model.trade.Trade;
import iu.devinmehringer.project1.model.user.User;

import java.util.ArrayList;
import java.util.List;

public class MockNotifier implements Notifier {
    private final List<Trade> capturedTrades = new ArrayList<>();
    private final List<User> capturedUsers = new ArrayList<>();

    @Override
    public void notify(User user, Trade trade) {
        capturedUsers.add(user);
        capturedTrades.add(trade);
    }

    public List<Trade> getCapturedTrades() { return capturedTrades; }
    public List<User> getCapturedUsers() { return capturedUsers; }
    public int getNotifyCount() { return capturedTrades.size(); }
}