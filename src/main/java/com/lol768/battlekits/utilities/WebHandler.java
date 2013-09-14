package com.lol768.battlekits.utilities;

import com.lol768.battlekits.BattleKits;
import java.io.File;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;


import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;

public class WebHandler implements Container {

    private BattleKits plugin;
    public String html = "";
    public String serverName = "BattleKits";
    public String serverDes = "A BattleKits server";
    public ConcurrentHashMap<String, Integer> deaths = new ConcurrentHashMap<>();
    public ConcurrentHashMap<String, Integer> kills = new ConcurrentHashMap<>();

    public WebHandler(BattleKits p) {
        plugin = p;
        if (new File(plugin.getDataFolder().getPath() + File.separator + "stat-deaths.bin").exists()) {
            try {
                deaths = (ConcurrentHashMap<String, Integer>) SLAPI.load(plugin.getDataFolder().getPath() + File.separator + "stat-deaths.bin");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (new File(plugin.getDataFolder().getPath() + File.separator + "stat-kills.bin").exists()) {
            try {
                kills = (ConcurrentHashMap<String, Integer>) SLAPI.load(plugin.getDataFolder().getPath() + File.separator + "stat-kills.bin");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void saveAll() {
        try {
            SLAPI.save(deaths, plugin.getDataFolder().getPath() + "/stat-deaths.bin");

            SLAPI.save(kills, plugin.getDataFolder().getPath() + "/stat-kills.bin");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handle(Request request, Response response) {
        try {
            try (PrintStream body = response.getPrintStream()) {
                long time = System.currentTimeMillis();

                response.setValue("Content-Type", "text/html");
                response.setValue("Server", "BattleKits/1.0 (Simple 4.0)");
                response.setDate("Date", time);
                response.setDate("Last-Modified", time);
                html = html.replace("##SERVERNAME##", serverName);
                html = html.replace("##DESCRIPTION##", serverDes);

                String table = "";
                for (Map.Entry pairs : deaths.entrySet()) {
                    int death = (int) pairs.getValue();
                    int kill = 0;
                    if (this.kills.get(pairs.getKey()) != null) {
                        kill = this.kills.get(pairs.getKey());
                    }
                    plugin.getLogger().log(Level.INFO, "Adding stats for {0}", pairs.getKey());
                    double per = kill / (double) (kill + death);
                    per = per * 100;
                    per = Math.ceil(per);
                    table = table + "<tr><td>" + pairs.getKey() + "</td><td>" + kill + "</td><td>" + death + "</td><td>" + per + "%</td>";
                }


                body.println(html.replace("##STATS##", table));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
