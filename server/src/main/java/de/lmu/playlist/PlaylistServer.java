package de.lmu.playlist;

/**
 * @author martin
 */

import com.google.inject.Guice;
import com.google.inject.servlet.GuiceFilter;
import com.sun.jersey.spi.container.servlet.ServletContainer;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import java.util.EnumSet;

import javax.servlet.DispatcherType;

public class PlaylistServer {

    public static void main(String[] args) throws Exception {
        Guice.createInjector(new PlaylistModule());

        Server server = new Server(5050);

        ServletHolder servletHolder = new ServletHolder(ServletContainer.class);
        servletHolder.setInitParameter("javax.ws.rs.Application", "PlaylistRestServer");
        servletHolder.setInitParameter("com.sun.jersey.config.feature.Debug", "true");
        servletHolder.setInitParameter("com.sun.jersey.config.feature.Trace", "true");
        servletHolder.setInitParameter("com.sun.jersey.spi.container.ContainerRequestFilters",
                "com.sun.jersey.api.container.filter.LoggingFilter");
        servletHolder.setInitParameter("com.sun.jersey.spi.container.ContainerResponseFilters",
                "com.sun.jersey.api.container.filter.LoggingFilter");

        ServletContextHandler context = new ServletContextHandler(server, "/", ServletContextHandler.SESSIONS);
        context.addFilter(GuiceFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST, DispatcherType.ASYNC));
        context.addServlet(servletHolder, "/*");

        server.start();
        server.join();
    }
}
