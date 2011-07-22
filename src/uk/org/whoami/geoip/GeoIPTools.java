package uk.org.whoami.geoip;

import com.maxmind.geoip.Country;
import com.maxmind.geoip.Location;
import com.maxmind.geoip.LookupService;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.MalformedURLException;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * This bukkit plugin provides an API for Maxmind GeoIP database lookups.
 *
 * @author whoami <whoami@whoami.org.uk>
 */
public class GeoIPTools extends JavaPlugin {

    /**
     * Type for country database
     */
    public final static int COUNTRYDATABASE = 1;
    /**
     * Type for city database
     */
    public final static int CITYDATABASE = 2;

    private LookupService geo = null;
    private LookupService geov6 = null;
    private Settings settings;

    @Override
    public void onEnable() {
        settings = new Settings(this.getConfiguration());
        ConsoleLogger.info("Starting database updates");
        try {
            Updater.update(settings);
        } catch(MalformedURLException ex) {
            ConsoleLogger.info(ex.getMessage());
        }
        ConsoleLogger.info(this.getDescription().getName() + " " + this.getDescription().getVersion() + " enabled");
    }

    @Override
    public void onDisable() {
        if(geo != null) {
            geo.close();
        }
        if(geov6 != null) {
            geov6.close();
        }

        geo = null;
        geov6 = null;
        ConsoleLogger.info(this.getDescription().getName() + " " + this.getDescription().getVersion() + " disabled");
    }

    /**
     *  Initialise the database
     *
     * @param databaseType city or country database
     * @param memoryCache cache database in memory?
     */
    public boolean init(int databaseType, boolean memoryCache) {
        String path = "";
        int cache = memoryCache ? LookupService.GEOIP_MEMORY_CACHE : LookupService.GEOIP_STANDARD;

        if(databaseType == COUNTRYDATABASE) {
            path = settings.getCountryDatabasePath();
        } else if(databaseType == CITYDATABASE) {
            path = settings.getCityDatabasePath();
        } else {
            ConsoleLogger.info("Unknown database type");
            return false;
        }
        try {
            this.geo = new LookupService(path, cache);
        } catch(IOException ex) {
            ConsoleLogger.info("Can't load " + path);
            return false;
        }
        return true;
    }

    /**
     * Initialise the IPv6 LookupService
     * @param memoryCache cache database in memory?
     */
    public boolean initIPv6(boolean memoryCache) {
        int cache = memoryCache ? LookupService.GEOIP_MEMORY_CACHE : LookupService.GEOIP_STANDARD;
        try {
            this.geov6 = new LookupService(settings.getIPv6DatabasePath(), cache);
        } catch(IOException ex) {
            ConsoleLogger.info("Can't load " + settings.getIPv6DatabasePath());
            return false;
        }
        return true;
    }

    /**
     * Look up a Country in the database. initCountry() or initCity() needs to
     * be called once before you can use that method
     *
     * @param inet Can be Inet4Address or Inet6Address
     * @return The country
     */
    public Country getCountry(InetAddress inet) {
        if(inet instanceof Inet4Address) {
            if(geo != null) {
                return geo.getCountry(inet);
            } else {
                ConsoleLogger.info("Uninitialised LookupService");
                return new Country("--", "N/A");
            }
        }
        if(inet instanceof Inet6Address) {
            if(geov6 != null) {
                return geov6.getCountryV6(inet);
            } else {
                ConsoleLogger.info("Uninitialised IPv6 LookupService");
                return new Country("--", "N/A");
            }
        }
        //Will never be reached
        ConsoleLogger.info("I see you are using IPv5");
        return new Country("--", "N/A");
    }

    /**
     * Look up a Location in the database. initCity() needs to
     * be called once before you can use that method
     *
     * @param inet A Inet4Address
     * @return
     */
    public Location getLocation(InetAddress inet) {
        if(inet instanceof Inet4Address) {
            if(geo != null) {
                return geo.getLocation(inet);
            } else {
                ConsoleLogger.info("Uninitialised LookupService");
            }
        } else if(inet instanceof Inet6Address) {
            ConsoleLogger.info("IPv6 is not supported for getLocation");
        }
        return null;
    }

    /**
     * Get the LookupService
     * @return The LookupService
     */
    public LookupService getLookupService() {
        return geo;
    }

    /**
     * Get the IPv6 LookupService
     * @return The LookupService
     */
    public LookupService getLookupServiccev6() {
        return geov6;
    }
}
