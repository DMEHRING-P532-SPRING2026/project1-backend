package iu.devinmehringer.project1.dto.user;

public class NotificationSettingsRequest {
    private boolean emailEnabled;
    private boolean smsEnabled;
    private boolean dashboardEnabled;

    public boolean isEmailEnabled() { return emailEnabled; }
    public void setEmailEnabled(boolean emailEnabled) { this.emailEnabled = emailEnabled; }

    public boolean isSmsEnabled() { return smsEnabled; }
    public void setSmsEnabled(boolean smsEnabled) { this.smsEnabled = smsEnabled; }

    public boolean isDashboardEnabled() { return dashboardEnabled; }
    public void setDashboardEnabled(boolean dashboardEnabled) { this.dashboardEnabled = dashboardEnabled; }
}