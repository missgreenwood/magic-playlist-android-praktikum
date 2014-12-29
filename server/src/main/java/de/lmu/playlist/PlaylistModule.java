package de.lmu.playlist;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import de.lmu.playlist.domain.dao.PlaylistDao;
import de.lmu.playlist.domain.dao.PlaylistDaoImpl;
import de.lmu.playlist.facade.PlaylistFacade;
import de.lmu.playlist.facade.PlaylistFacadeImpl;
import de.lmu.playlist.service.MongoService;
import de.lmu.playlist.service.MongoServiceImpl;
import de.lmu.playlist.service.PlaylistService;
import de.lmu.playlist.service.PlaylistServiceImpl;
import org.eclipse.jetty.servlet.DefaultServlet;

import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import java.util.HashMap;

public class PlaylistModule extends ServletModule {

    @Override
    protected void configureServlets() {
        bind(DefaultServlet.class).in(Singleton.class);
        bind(PlaylistDao.class).to(PlaylistDaoImpl.class).in(Singleton.class);
        bind(PlaylistService.class).to(PlaylistServiceImpl.class).in(Singleton.class);
        bind(PlaylistFacade.class).to(PlaylistFacadeImpl.class).in(Singleton.class);
        bind(MongoService.class).to(MongoServiceImpl.class).in(Singleton.class);

        bind(MessageBodyReader.class).to(JacksonJsonProvider.class);
        bind(MessageBodyWriter.class).to(JacksonJsonProvider.class);

        HashMap<String, String> options = new HashMap<>();
        options.put("com.sun.jersey.api.json.POJOMappingFeature", "true");
        serve("/*").with(GuiceContainer.class, options);
    }
}
