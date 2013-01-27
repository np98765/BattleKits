package com.lavacraftserver.BattleKits;
import java.io.PrintStream;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;



public class WebHandler implements Container {
	private BattleKits plugin;
	public WebHandler(BattleKits p) {
		plugin = p;
		plugin.html = plugin.html.replace("##SERVERNAME##", plugin.global.getConfig().getString("server.server-name"));
		plugin.html = plugin.html.replace("##DESCRIPTION##", plugin.global.getConfig().getString("server.server-description"));
	}
	public void handle(Request request, Response response) {
	      try {
	         PrintStream body = response.getPrintStream();
	         long time = System.currentTimeMillis();
	   
	         response.setValue("Content-Type", "text/html");
	         response.setValue("Server", "BattleKits/1.0 (Simple 4.0)");
	         response.setDate("Date", time);
	         response.setDate("Last-Modified", time);
	   
	         body.println(plugin.html);
	         body.close();
	      } catch(Exception e) {
	         e.printStackTrace();
	      }
	   } 
	


}
