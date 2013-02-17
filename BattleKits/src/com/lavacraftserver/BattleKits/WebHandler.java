package com.lavacraftserver.BattleKits;
import java.io.File;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;



public class WebHandler implements Container {
	private BattleKits plugin;
	public String html = "";
	public String serverName = "BattleKits";
	public String serverDes = "A BattleKits server";
	
	public ConcurrentHashMap<String, Integer> deaths = new ConcurrentHashMap<String, Integer>();
	public ConcurrentHashMap<String, Integer> kills = new ConcurrentHashMap<String, Integer>();
	
	public WebHandler(BattleKits p) {
		plugin = p;
		if (new File(plugin.getDataFolder().getPath() + "/stat-deaths.bin").exists()) {
			try {
				deaths = (ConcurrentHashMap<String, Integer>) SLAPI.load(plugin.getDataFolder().getPath() + "/stat-deaths.bin");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if (new File(plugin.getDataFolder().getPath() + "/stat-kills.bin").exists()) {
			try {
				kills = (ConcurrentHashMap<String, Integer>) SLAPI.load(plugin.getDataFolder().getPath() + "/stat-kills.bin");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void saveAll() {
		try {
			SLAPI.save(deaths, plugin.getDataFolder().getPath() + "/stat-deaths.bin");
			plugin.getDataFolder().
			SLAPI.save(kills, plugin.getDataFolder().getPath() + "/stat-kills.bin");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void handle(Request request, Response response) {
	      try {
	         PrintStream body = response.getPrintStream();
	         long time = System.currentTimeMillis();
	   
	         response.setValue("Content-Type", "text/html");
	         response.setValue("Server", "BattleKits/1.0 (Simple 4.0)");
	         response.setDate("Date", time);
	         response.setDate("Last-Modified", time);
	         html = html.replace("##SERVERNAME##", serverName);
	 		 html = html.replace("##DESCRIPTION##", serverDes);
	         
	 		String table = "";
	 		Iterator it = deaths.entrySet().iterator();
	 	    while (it.hasNext()) {
	 	    	Map.Entry pairs = (Map.Entry)it.next();
	 	    	int death = (int) pairs.getValue();
	        	int kill = 0;
	        	if (this.kills.get(pairs.getKey()) != null) {
	        		kill = this.kills.get(pairs.getKey());
	        	}
	        	plugin.getLogger().info("Adding stats for " + pairs.getKey());
	        	double per = kill / (double) (kill + death);
	        	per = per * 100;
	        	per = Math.ceil(per);
	        	table = table + "<tr><td>" + pairs.getKey() + "</td><td>" + kill + "</td><td>" + death + "</td><td>" + per + "%</td>";
	         }
	       
	         
	         body.println(html.replace("##STATS##", table));
	         body.close();
	      } catch(Exception e) {
	         e.printStackTrace();
	      }
	   } 
	


}
