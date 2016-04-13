package com.oroboks;

import java.io.IOException;
import java.net.URI;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import com.google.inject.servlet.ServletModule;
import com.oroboks.dao.DAO;
import com.oroboks.dao.internal.LocationDAO;
import com.oroboks.dao.internal.UserDAO;
import com.oroboks.dao.internal.UserLocationDAO;
import com.oroboks.entities.Location;
import com.oroboks.entities.User;
import com.oroboks.entities.UserLocation;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProviderFactory;
import com.sun.jersey.guice.spi.container.GuiceComponentProviderFactory;

/**
 * Test case for {@link UserResource}
 * @author Aditya Narain
 */
//@RunWith(MockitoJUnitRunner.class)
public class UserResourceTest {
    //    @Mock
    //    private UserDAO userDAO;
    //
    //    @Mock
    //    private LocationDAO locationDAO;
    //
    //    @Mock
    //    private UserLocationDAO userLocationDAO;
    static final URI BASE_URI = getBaseURI();
    HttpServer server;
    private static URI getBaseURI() {
	return UriBuilder.fromUri( "http://localhost/" ).port( 9998 ).build();
    }

    /**
     * Setup for starting server
     * @throws IOException if an I/O exception occurs
     */
    @Before
    public void startServer() throws IOException{
	Injector injector = Guice.createInjector( new ServletModule() {
	    @Override
	    protected void configureServlets() {
		bind(new TypeLiteral<DAO<User>>() {}).to(UserDAO.class);
		bind(new TypeLiteral<DAO<UserLocation>>() {}).to(UserLocationDAO.class);
		bind(new TypeLiteral<DAO<Location>>() {}).to(LocationDAO.class);
	    }
	});

	ResourceConfig rc = new PackagesResourceConfig( "com.oroboks" );
	IoCComponentProviderFactory ioc = new GuiceComponentProviderFactory( rc, injector );
	server = GrizzlyServerFactory.createHttpServer( BASE_URI, rc, ioc );
    }

    /**
     *  Teardown to stop server after test is run completely.
     */
    @After
    public void tearDownServer(){
	server.stop();
    }

    /**
     * @throws IOException
     */
    @Test
    public void getAllUsers() throws IOException{
	Client client = Client.create( new DefaultClientConfig() );
	WebResource service = client.resource( getBaseURI() );
	ClientResponse reponse = service.path("/users").accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
	String text = reponse.getEntity(String.class);
    }


}
