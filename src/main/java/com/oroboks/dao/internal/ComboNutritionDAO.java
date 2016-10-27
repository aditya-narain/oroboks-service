package com.oroboks.dao.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.hibernate.HibernateException;

import com.google.inject.persist.Transactional;
import com.oroboks.dao.DAO;
import com.oroboks.entities.Combo;
import com.oroboks.entities.ComboNutrition;
import com.oroboks.entities.ComboNutrition.NutritionType;
import com.oroboks.exception.SaveException;

/**
 * DAO for {@link ComboNutrition}
 * @author Aditya Narain
 *
 */
public class ComboNutritionDAO implements DAO<ComboNutrition> {
    private static final Logger LOGGER = Logger.getLogger(ComboNutritionDAO.class.getCanonicalName());
    private final String GET_NUTRITION_ATTR = "combo.getNutritionAttr";
    private final String DELETE_NUTRITION_ATTR = "combo.deleteNutritionAttr";
    private final String DELETE_COMBO = "comboNutr.deleteCombo";
    private final EntityManager entityManager;
    /**
     * Constructs the ComboDAO. This constructor allows Guice to inject the
     * {@link EntityManager}
     * 
     * @param entityManager
     *            {@link EntityManager} which will never be null
     */
    @Inject
    public ComboNutritionDAO(EntityManager entityManager){
	this.entityManager = entityManager;
    }
    @Override
    @Transactional
    public ComboNutrition addEntity(ComboNutrition entity) {
	if(entity == null){
	    LOGGER.log(Level.SEVERE, "entity cannot be null");
	    throw new SaveException("Unable to save entity");
	}
	try{
	    return entityManager.merge(entity);
	}
	catch(HibernateException exception){
	    LOGGER.log(Level.SEVERE,
		    "Hibernate Exception while saving/updating: More exception chain with :"
			    + exception);
	    throw new SaveException("Save Exception occured: More exception chain with :" + exception);
	}
    }

    @Override
    public List<ComboNutrition> getAllEntities() {
	throw new UnsupportedOperationException("This function is not supported");
    }

    @Override
    public List<ComboNutrition> getEntitiesByField(
	    Map<String, Object> filterEntitiesByFieldMap) {
	if(filterEntitiesByFieldMap == null){
	    LOGGER.log(Level.SEVERE, "filterEntitiesByFieldMap cannot be null");
	    throw new IllegalArgumentException("filterEntitiesByFieldMap cannot be null");
	}
	List<ComboNutrition> results = new ArrayList<ComboNutrition>();
	if(!filterEntitiesByFieldMap.keySet().isEmpty()){
	    String id = (String)filterEntitiesByFieldMap.get("comboId");
	    if(id != null && !id.trim().isEmpty()){
		String comboId = Combo.class.getSimpleName() + "@" + id;
		Query query = entityManager.createNamedQuery(GET_NUTRITION_ATTR);
		query.setParameter("comboId", comboId);
		try{
		    results = query.getResultList();
		}
		catch(PersistenceException exception){
		    LOGGER.log(Level.SEVERE, "Error retrieving results: Error "
			    + exception);
		}
	    }

	}
	return results;

    }

    @Override
    public List<ComboNutrition> getEntitiesByField(ComboNutrition entity) {
	throw new UnsupportedOperationException("This function is not operational");
    }

    @Override
    public List<ComboNutrition> updateEntity(ComboNutrition newEntity,
	    String primaryKeyId, String updateByField) {
	throw new UnsupportedOperationException("This function is not operational");
    }

    @Override
    public ComboNutrition deActivateEntity(ComboNutrition entity) {
	if(entity == null){
	    LOGGER.log(Level.INFO, "Entity to deleted is null");
	    throw new IllegalArgumentException("Entity to be deleted is null");
	}
	if(entity.getComboId() == null || entity.getComboId().trim().isEmpty()){
	    LOGGER.log(Level.INFO,"Entity cannot be deleted as comboId is unknown");
	    return null;
	}
	Query query;
	// To delete the combo itself, we will set comboNutrient as DEFAULT;
	if(entity.getComboNutrient().equals(NutritionType.DEFAULT)){
	    query = entityManager.createNamedQuery(DELETE_COMBO);
	}
	else{
	    query = entityManager.createNamedQuery(DELETE_NUTRITION_ATTR);
	    query.setParameter("comboNutrient", entity.getComboNutrient());
	}
	query.setParameter("comboId", Combo.class.getSimpleName() + "@" + entity.getComboId());
	try{
	    query.executeUpdate();
	}
	catch(Exception e){
	    LOGGER.log(Level.SEVERE, "Exception occured while updating combo table");
	    return null;
	}

	return entity;
    }

}
