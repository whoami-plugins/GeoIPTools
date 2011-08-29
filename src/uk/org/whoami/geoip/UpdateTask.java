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

import java.io.IOException;
import java.net.MalformedURLException;
import org.bukkit.command.CommandSender;
import uk.org.whoami.geoip.util.Settings;
import uk.org.whoami.geoip.util.Updater;

public class UpdateTask implements Runnable {
    
    private CommandSender admin;
    private Settings settings;
    private GeoIPLookup geo;

    public UpdateTask(CommandSender admin, Settings settings, GeoIPLookup geo) {
        this.admin = admin;
        this.settings = settings;
        this.geo = geo;
    }
        
    @Override
    public void run() {
        admin.sendMessage("[GeoIPTools] Starting update");
        try {
            Updater.update(settings);
        } catch (MalformedURLException ex) {
            admin.sendMessage("[GeoIPTools] Error: " + ex.getMessage());
            return;
        }
        admin.sendMessage("[GeoIPTools] Update finished");
        if(geo == null) return;
        admin.sendMessage("[GeoIPTools] Reloading database");
        try {
            geo.reload();
        } catch (IOException ex) {
            admin.sendMessage("[GeoIPTools] Error: " + ex.getMessage());
            return;
        }
        admin.sendMessage("[GeoIPTools] database reloaded");
    }
}
