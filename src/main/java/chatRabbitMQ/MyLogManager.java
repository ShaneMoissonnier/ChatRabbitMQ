package chatRabbitMQ;

import java.util.logging.LogManager;

/**
 * This class is used as a replacement to the logging manager.
 * <p>
 * By default, the logger is reset sometime during shutdown, meaning that it cannot be used reliably in a shutdown hook.
 * Since we use a shutdown hook and want to log some things in it, we use this LogManager to delay the resetting of the
 * logger
 * <p>
 * Source of this code : https://stackoverflow.com/a/13825590
 */
public class MyLogManager extends LogManager {
    static MyLogManager instance;

    public MyLogManager() {
        instance = this;
    }

    @Override
    public void reset() {
        /* This is called too early, we don't want to reset yet */
    }

    private void reset0() {
        /* This method will be called later, but it will do exactly the same this as super.reset() */
        super.reset();
    }

    public static void resetFinally() {
        /* This method is here to allow a static access */
        if (instance != null) {
            instance.reset0();
        }
    }
}
