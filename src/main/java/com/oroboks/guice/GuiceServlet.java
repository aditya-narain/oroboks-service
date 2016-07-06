package com.oroboks.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import com.oroboks.dao.DAO;
import com.oroboks.dao.internal.ComboHistoryDAO;
import com.oroboks.dao.internal.LocationDAO;
import com.oroboks.dao.internal.RestaurantDAO;
import com.oroboks.dao.internal.UserDAO;
import com.oroboks.dao.internal.UserLocationDAO;
import com.oroboks.entities.ComboHistory;
import com.oroboks.entities.Location;
import com.oroboks.entities.Restaurant;
import com.oroboks.entities.User;
import com.oroboks.entities.UserLocation;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

//TODO Write Javadocs
/**
 * @author Aditya Narain
 */
public class GuiceServlet extends GuiceServletContextListener {

    @Override
    protected Injector getInjector() {
	final ResourceConfig rc = new PackagesResourceConfig(
		"com.oroboks");
	return Guice.createInjector(new ServletModule() {
	    @Override
	    protected void configureServlets() {
		bind(new TypeLiteral<DAO<User>>() {}).to(UserDAO.class);
		bind(new TypeLiteral<DAO<UserLocation>>() {}).to(UserLocationDAO.class);
		bind(new TypeLiteral<DAO<Location>>() {}).to(LocationDAO.class);
		bind(new TypeLiteral<DAO<Restaurant>>(){}).to(RestaurantDAO.class);
		bind(new TypeLiteral<DAO<ComboHistory>>(){}).to(ComboHistoryDAO.class);
		bind(JacksonObjectMapperProvider.class).in(Scopes.SINGLETON);
		bind(
			forName("com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider"))
			.in(Scopes.SINGLETON);
		bind(
			forName("com.fasterxml.jackson.jaxrs.json.JsonParseExceptionMapper"))
			.in(Scopes.SINGLETON);
		bind(
			forName("com.fasterxml.jackson.jaxrs.json.JsonMappingExceptionMapper"))
			.in(Scopes.SINGLETON);

		for (Class<?> resources : rc.getClasses()) {
		    bind(resources);
		}
		serve("/*").with(GuiceContainer.class);
		filter("/*").through(CorsFilter.class);

	    }

	    private Class<?> forName(final String name) {
		try {
		    return Class.forName(name);
		} catch (final ClassNotFoundException e) {
		    throw new RuntimeException(e);
		}
	    }

	}, new LbHibernateModule());
    }

}
