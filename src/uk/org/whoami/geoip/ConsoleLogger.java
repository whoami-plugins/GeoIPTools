package uk.org.whoami.geoip;

import java.util.logging.Logger;

public class ConsoleLogger {
    private static final Logger log = Logger.getLogger("Minecraft");

    public static void info(String message) {
        log.info("[GeoIPTools] " + message);
    }
}
