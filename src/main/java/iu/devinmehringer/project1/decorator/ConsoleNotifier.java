package iu.devinmehringer.project1.decorator;

import org.springframework.stereotype.Component;

@Component
public class ConsoleNotifier implements Notifier {
    @Override
    public void notify(String message) {
        System.out.println("Notification: " + message);
    }
}
