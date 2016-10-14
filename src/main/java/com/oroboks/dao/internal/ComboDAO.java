package com.oroboks.dao.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.oroboks.dao.DAO;
import com.oroboks.entities.Combo;

/**
 * 
 * @author Aditya Narain
 */
public class ComboDAO implements DAO<Combo> {
    private final Logger LOGGER = Logger.getLogger(ComboHistoryDAO.class.getSimpleName());
    private final String getComboById = "combos.getCombos";

    private final EntityManager entityManager;
    /**
     * 
     * Constructs the ComboDAO. This constructor allows Guice to inject the
     * {@link EntityManager}
     * 
     * @param entityManager
     *            {@link EntityManager} which will never be null
     */
    @Inject
    public ComboDAO(final EntityManager entityManager){
	this.entityManager = entityManager;
    }
    @Override
    public Combo addEntity(Combo entity) {
	throw new UnsupportedOperationException("This function is currently unavailaible");
    }

    @Override
    public List<Combo> getAllEntities() {
	throw new UnsupportedOperationException("This function is currently unavailaible");
    }

    @Override
    public List<Combo> getEntitiesByField(
	    Map<String, Object> filterEntitiesByFieldMap) {
	throw new UnsupportedOperationException("This function is currently unavailaible");
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Combo> getEntitiesByField(Combo entity) {
	if(entity == null){
	    LOGGER.log(Level.SEVERE, "entity is null");
	    throw new IllegalArgumentException("entity is null");
	}
	List<Combo> combos = new ArrayList<Combo>();
	Query query;
	try{
	    query = entityManager.createNamedQuery(getComboById);
	    query.setParameter("uuid", entity.getUUID());
	}
	catch(Exception e){
	    LOGGER.log(Level.SEVERE, "Error occured while retrieving combos");
	    return combos;
	}
	combos = query.getResultList();
	return combos;


    }

    @Override
    public List<Combo> updateEntity(Combo newEntity, String primaryKeyId,
	    String updateByField) {
	throw new UnsupportedOperationException("This function is currently unavailaible");
    }

    @Override
    public Combo deActivateEntity(Combo entity) {
	throw new UnsupportedOperationException("This function is currently unavailaible");
    }

}
