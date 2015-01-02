package ny2.flex.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SystemUtility {

    // //////////////////////////////////////
    // Field
    // //////////////////////////////////////

    private static final Logger logger = LoggerFactory.getLogger(SystemUtility.class);

    // //////////////////////////////////////
    // Method
    // //////////////////////////////////////

    /**
     * 指定時間waitします
     *
     * @param millis
     */
    public static void waitSleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            logger.error("", e);
        }
    }
}
