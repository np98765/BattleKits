package com.lavacraftserver.BattleKits;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

public class WebHandler extends AbstractHandler {


	@Override
	public void handle(String arg0, Request arg1, HttpServletRequest arg2, HttpServletResponse response) throws IOException, ServletException {
		byte[] body;
		String html = "<h1>Java web server!</h1>";
		body = html.getBytes();
		response.setContentType("text/html");
		response.setContentLength(body.length);
		response.getOutputStream().write(body);
		response.getOutputStream().close();
	}

}
