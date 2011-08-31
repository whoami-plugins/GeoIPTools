/*
 * Copyright 2011 Sebastian Köhler <sebkoehler@whoami.org.uk>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.org.whoami.geoip;

import uk.org.whoami.geoip.util.ConsoleLogger;
import uk.org.whoami.geoip.util.Settings;
import com.maxmind.geoip.Country;
import com.maxmind.geoip.Location;
import com.maxmind.geoip.LookupService;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;

/**
 *
 * @author Sebastian Köhler <sebkoehler@whoami.org.uk>
 */
public class GeoIPLookup {

    /**
     * Bitmask for country database
     */
    public final static int COUNTRYDATABASE = 100;

    /**
     * Bitmask for city database
     */
    public final static int CITYDATABASE = 200;

    /**
     * Bitmask for IPv6 database
     */
    public final static int IPV6DATABASE = 300;

    private LookupService geo = null;
    private LookupService geov6 = null;
    private Settings settings;
    private int type;

    GeoIPLookup(Settings settings) throws IOException {
        type = -1;
        this.settings = settings;
    }

    /**
     * Look up a Country in the database.
     *
     * @param inet Can be Inet4Address or Inet6Address
     * @return The country
     */
    public synchronized Country getCountry(InetAddress inet) {
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
     * Look up a Location in the database. The Object needs to be created with
     * the CITYDATABASE bitmask for this method wo work.
     *
     * @param inet A Inet4Address
     * @return Location or null if the city database was not initialised
     */
    public synchronized Location getLocation(InetAddress inet) {
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

    synchronized void initCountry() throws IOException {
        if(type == -1) {
            geo = new LookupService(settings.getCountryDatabasePath(),LookupService.GEOIP_MEMORY_CACHE);
            type = COUNTRYDATABASE;
        }
    }

    synchronized void initCity() throws IOException {
        if(type == COUNTRYDATABASE || type == -1) {
            if(type != -1) {
                geo.close();
            }
            geo = new LookupService(settings.getCityDatabasePath(),LookupService.GEOIP_MEMORY_CACHE);
            type = CITYDATABASE;
        }
    }

    synchronized void initIPv6() throws IOException {
        if(geov6 == null) {
            geov6 = new LookupService(settings.getIPv6DatabasePath(),LookupService.GEOIP_MEMORY_CACHE);
        }
    }
    
    synchronized void reload() throws IOException {
        if(geo != null) {
            geo.close();
            if(type == COUNTRYDATABASE) {
                geo = new LookupService(settings.getCountryDatabasePath(),LookupService.GEOIP_MEMORY_CACHE);
            } else {
                geo = new LookupService(settings.getCityDatabasePath(),LookupService.GEOIP_MEMORY_CACHE);
            }
        }
        if(geov6 != null) {
            geov6.close();
            geov6 = new LookupService(settings.getIPv6DatabasePath(),LookupService.GEOIP_MEMORY_CACHE);
        }
    }

    synchronized void close() {
        if(geo != null) {
            geo.close();
            geo = null;
        }
        if(geov6 != null) {
            geov6.close();
            geov6 = null;
        }
    }
}
