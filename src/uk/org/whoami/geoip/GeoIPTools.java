package uk.org.whoami.geoip;

import uk.org.whoami.geoip.util.ConsoleLogger;
import uk.org.whoami.geoip.util.Settings;
import uk.org.whoami.geoip.util.Updater;
import java.io.IOException;
import java.net.MalformedURLException;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * This bukkit plugin provides an API for Maxmind GeoIP database lookups.
 *
 * @author Sebastian Köhler <whoami@whoami.org.uk>
 */
public class GeoIPTools extends JavaPlugin {

    private Settings settings;
    private GeoIPLookup geo = null;

    @Override
    public void onEnable() {
        settings = new Settings(this.getConfiguration());
        ConsoleLogger.info("Starting database updates");
        try {
            Updater.update(settings);
        } catch(MalformedURLException ex) {
            ConsoleLogger.info(ex.getMessage());
        }
        ConsoleLogger.info(this.getDescription().getName() + " " + this.
                getDescription().getVersion() + " enabled");
    }

    @Override
    public void onDisable() {
        if(geo != null) {
            geo.close();
            geo = null;
        }
        ConsoleLogger.info(this.getDescription().getName() + " " + this.
                getDescription().getVersion() + " disabled");
    }

    /**
     * Get the GeoIPLookup. The returned object will at least have the
     * functionality specified by the bitmask.
     *
     * The bitmask can be combined with "or" for example:
     * getGeoIPLookup(GeoIPLookup.COUNTRYDATABASE | GeoIPLookup.IPV6DATABASE);
     *
     * @param bitmask Bitmask to specify the funtionality
     * @return A GeoIPLookup or null if the bitmask is wrong or an error occurs
     */
    public GeoIPLookup getGeoIPLookup(int bitmask) {
        try {
            if(geo == null) {
                geo = new GeoIPLookup(settings);
            }
            if(bitmask == GeoIPLookup.COUNTRYDATABASE) {
            } else if(bitmask == GeoIPLookup.CITYDATABASE) {
                geo.initCity();
            } else if(bitmask == (GeoIPLookup.COUNTRYDATABASE | GeoIPLookup.IPV6DATABASE)) {
                geo.initIPv6();
            } else if(bitmask == (GeoIPLookup.CITYDATABASE | GeoIPLookup.IPV6DATABASE)) {
                geo.initCity();
                geo.initIPv6();
            } else {
                ConsoleLogger.info("Unsupported bitmask");
                return null;
            }
        } catch(IOException e) {
            ConsoleLogger.info("Can't load database");
            ConsoleLogger.info(e.getMessage());
            return null;
        }
        return geo;
    }
}
