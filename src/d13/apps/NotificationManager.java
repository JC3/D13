package d13.apps;

import d13.notify.BackgroundNotificationManager;

public class NotificationManager {

    public static void main (String[] args) {
        
        BackgroundNotificationManager m = new BackgroundNotificationManager();
        m.overrideConfiguration(true, 30000);
        m.contextInitialized(null);
        
    }

}
