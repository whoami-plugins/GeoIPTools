package uk.org.whoami.geoip.util;

import java.util.logging.Logger;

/**
 *
 * @author Sebastian Köhler <sebkoehler@whoami.org.uk>
 */
public class ConsoleLogger {
    private static final Logger log = Logger.getLogger("Minecraft");

    /**
     *
     * @param message
     */
    public static void info(String message) {
        log.info("[GeoIPTools] " + message);
    }
}
