package com.oroboks.guice;

import com.google.inject.TypeLiteral;
import com.google.inject.persist.PersistFilter;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.google.inject.servlet.ServletModule;
import com.oroboks.dao.DAO;
import com.oroboks.dao.internal.ComboDAO;
import com.oroboks.dao.internal.ComboHistoryDAO;
import com.oroboks.dao.internal.ComboNutritionDAO;
import com.oroboks.dao.internal.LocationDAO;
import com.oroboks.dao.internal.OrderDAO;
import com.oroboks.dao.internal.RestaurantDAO;
import com.oroboks.dao.internal.UserDAO;
import com.oroboks.dao.internal.UserLocationDAO;
import com.oroboks.entities.Combo;
import com.oroboks.entities.ComboHistory;
import com.oroboks.entities.ComboNutrition;
import com.oroboks.entities.Location;
import com.oroboks.entities.OroOrder;
import com.oroboks.entities.Restaurant;
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
	bind(new TypeLiteral<DAO<Restaurant>>(){}).to(RestaurantDAO.class);
	bind(new TypeLiteral<DAO<ComboHistory>>(){}).to(ComboHistoryDAO.class);
	bind(new TypeLiteral<DAO<OroOrder>>(){}).to(OrderDAO.class);
	bind(new TypeLiteral<DAO<Combo>>(){}).to(ComboDAO.class);
	bind(new TypeLiteral<DAO<ComboNutrition>>(){}).to(ComboNutritionDAO.class);
	filter("/*").through(PersistFilter.class);
    }
}
