package d13.util;

public final class Logger {

    public static void exception (Throwable t, String when) {
        System.err.println("Error: " + when + t.getMessage());
        t.printStackTrace();
    }
    
    public static void warning (String message) {
        System.err.println("Warning: " + message);
    }
    
}
