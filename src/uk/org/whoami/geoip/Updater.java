package uk.org.whoami.geoip;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.zip.GZIPInputStream;

/**
 *
 * @author whoami <whoami@whoami.org.uk>
 */
public class Updater {

    public static void update(Settings settings) throws MalformedURLException {
        if(settings.getCityDatabasePath().equals(settings.CITYDATABASEPATH)) {
            URL url = new URL(settings.getCityDatabaseURL());
            File file = new File(settings.CITYDATABASEPATH);
            updateFile(url, file, settings.getLastUpdated());
            ConsoleLogger.info(settings.CITYDATABASEPATH + " updated");
        }

        if(settings.getCountryDatabasePath().equals(settings.COUNTRYDATABASEPATH)) {
            URL url = new URL(settings.getCountryDatabaseURL());
            File file = new File(settings.COUNTRYDATABASEPATH);
            updateFile(url, file, settings.getLastUpdated());
            ConsoleLogger.info(settings.COUNTRYDATABASEPATH + " updated");
        }

        if(settings.getIPv6DatabasePath().equals(settings.IPV6DATABASEBATH)) {
            URL url = new URL(settings.getIPv6DatabaseURL());
            File file = new File(settings.IPV6DATABASEBATH);
            updateFile(url, file, settings.getLastUpdated());
            ConsoleLogger.info(settings.IPV6DATABASEBATH + " updated");
        }
        settings.setLastUpdated(new Date().getTime());
        settings.write();
    }

    private static void updateFile(URL url, File file, long lastUpdated) {
        HttpURLConnection con = null;
        BufferedOutputStream out = null;
        GZIPInputStream in = null;

        try {
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setReadTimeout(10000);
            con.setConnectTimeout(10000);
            con.setIfModifiedSince(lastUpdated);
            con.connect();

            if(con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                byte[] buffer = new byte[4096];
                out = new BufferedOutputStream(new FileOutputStream(file));
                in = new GZIPInputStream(con.getInputStream());
                int len;
                while((len = in.read(buffer, 0, buffer.length)) > -1) {
                    out.write(buffer, 0, len);
                }
            }
        } catch(IOException e) {
            ConsoleLogger.info(e.getMessage());
        } finally {
            try {
                out.close();
            } catch(IOException e) {
            } catch(NullPointerException e) {
            }
            try {
                in.close();
            } catch(IOException e) {
            } catch(NullPointerException e) {
            }
            try {
                con.disconnect();
            } catch(NullPointerException e) {
            }
        }
    }
}
