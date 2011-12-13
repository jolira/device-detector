package com.jolira.detector;

import net.sourceforge.wurfl.core.web.WURFLServletContextListener;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.bio.SocketConnector;
import org.eclipse.jetty.webapp.WebAppContext;

public class Start {
    public static void main(final String[] args) throws Exception {
        final Server server = new Server();
        final SocketConnector connector = new SocketConnector();

        // Set some timeout options to make debugging easier.
        connector.setSoLingerTime(-1);
        connector.setPort(8080);
        server.addConnector(connector);

        final WebAppContext bb = new WebAppContext();
        final WURFLServletContextListener wurflListener = new WURFLServletContextListener();

        bb.setServer(server);
        bb.setContextPath("/");
        bb.addEventListener(wurflListener);
        bb.addServlet(DetectorServlet.class, "/*");
        bb.setInitParameter("wurfl", "/WEB-INF/wurfl.zip");
        bb.setInitParameter("wurflPatch", "/WEB-INF/web_browsers_patch.xml");
        bb.setWar("src/main/webapp");

        server.setHandler(bb);

        try {
            System.out.println(">>> STARTING EMBEDDED JETTY SERVER, PRESS ANY KEY TO STOP");
            server.start();
            System.in.read();
            server.stop();
            server.join();
        } catch (final Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
