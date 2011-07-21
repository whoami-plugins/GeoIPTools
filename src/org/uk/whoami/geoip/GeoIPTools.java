package org.uk.whoami.geoip;

import com.maxmind.geoip.Country;
import com.maxmind.geoip.Location;
import com.maxmind.geoip.LookupService;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.logging.Logger;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * This bukkit plugin provides an API for Maxmind GeoIP database lookups.
 *
 * @author whoami <whoami@whoami.org.uk>
 * @author
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

    private String countryDatabasePath;
    private String cityDatabasePath;
    private String ipv6DatabasePath;

    private static final Logger log = Logger.getLogger("Minecraft");

    /**
     *
     */
    @Override
    public void onEnable() {
        //TODO
    }

    /**
     * Closes the databases
     */
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
    }

    /**
     *  Initialise the database
     *
     * @param databaseType city or country database
     * @param memoryCache cache database in memory?
     */
    public void init(int databaseType, boolean memoryCache) {
        String path = "";
        int cache = memoryCache ? LookupService.GEOIP_MEMORY_CACHE : LookupService.GEOIP_STANDARD;

        if(databaseType == COUNTRYDATABASE) {
            path = countryDatabasePath;
        } else if(databaseType == CITYDATABASE) {
            path = cityDatabasePath;
        } else {
            log.info("[GeoIPTool] Unknows database type");
            return;
        }
        try {
            this.geo = new LookupService(path, cache);
        } catch(IOException ex) {
            log.info("[GeoIPTools] Can't load " + path);
        }
    }

    /**
     * Initialise the IPv6 LookupService
     * @param memoryCache cache database in memory?
     */
    public void initIPv6(boolean memoryCache) {
        int cache = memoryCache ? LookupService.GEOIP_MEMORY_CACHE : LookupService.GEOIP_STANDARD;
        try {
            this.geov6 = new LookupService(ipv6DatabasePath, cache);
        } catch(IOException ex) {
            log.info("[GeoIPTools] Can't load " + ipv6DatabasePath);
        }
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
                log.info("[GeoIPTools] Uninitialised LookupService");
                return new Country("--", "N/A");
            }
        }
        if(inet instanceof Inet6Address) {
            if(geov6 != null) {
                return geov6.getCountryV6(inet);
            } else {
                log.info("[GeoIPTools] Uninitialised IPv6 LookupService");
                return new Country("--", "N/A");
            }
        }
        //Will never be reached
        log.info("[GeoIPTools] I see you are using IPv5");
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
                log.info("[GeoIPTools] Uninitialised LookupService");
            }
        } else if(inet instanceof Inet6Address) {
            log.info("[GeoIPTools] IPv6 is not supported for getLocation");
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
