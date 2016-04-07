package com.oroboks.guice;

import com.google.inject.TypeLiteral;
import com.google.inject.persist.PersistFilter;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.google.inject.servlet.ServletModule;
import com.oroboks.dao.DAO;
import com.oroboks.dao.internal.LocationDAO;
import com.oroboks.dao.internal.UserDAO;
import com.oroboks.dao.internal.UserLocationDAO;
import com.oroboks.entities.Location;
import com.oroboks.entities.User;
import com.oroboks.entities.UserLocation;

/**
 * @author Aditya Narain
 *
 */
public class LbHibernateModule extends ServletModule {
    @Override
    protected void configureServlets(){
	install(new JpaPersistModule("lbPersistenceUnit"));
	bind(new TypeLiteral<DAO<User>>(){}).to(UserDAO.class);
	bind(new TypeLiteral<DAO<Location>>(){}).to(LocationDAO.class);
	bind(new TypeLiteral<DAO<UserLocation>>(){}).to(UserLocationDAO.class);
	filter("/*").through(PersistFilter.class);
    }
}
