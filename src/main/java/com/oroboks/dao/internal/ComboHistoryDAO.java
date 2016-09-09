package com.oroboks.dao.internal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.joda.time.DateTime;

import com.oroboks.dao.DAO;
import com.oroboks.entities.Combo;
import com.oroboks.entities.ComboHistory;
import com.oroboks.util.DateUtility;

/**
 * DAO for {@link ComboHistory}
 * @author Aditya Narain
 *
 */
public class ComboHistoryDAO implements DAO<ComboHistory> {
    private final Logger LOGGER = Logger.getLogger(ComboHistoryDAO.class.getSimpleName());
    private final String getWeekCombos = "ComboHistory.getWeekCombosFromCurrent";
    private final String getCombosHistory = "comboHistory.getCombosHistory";

    private final EntityManager entityManager;

    /**
     * Constructs the ComboHistoryDAO. This constructor allows Guice to inject the
     * {@link EntityManager}
     * 
     * @param entityManager
     *            {@link EntityManager} which will never be null
     */
    @Inject
    public ComboHistoryDAO(final EntityManager entityManager){
	this.entityManager = entityManager;
    }
    @Override
    public ComboHistory addEntity(ComboHistory entity) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public List<ComboHistory> getAllEntities() {
	throw new UnsupportedOperationException("This method is unavailaible");
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ComboHistory> getEntitiesByField(
	    Map<String, Object> filterEntitiesByFieldMap) {
	if(filterEntitiesByFieldMap == null){
	    throw new IllegalArgumentException("filterEntitiesByFieldMap cannot be null");
	}
	List<ComboHistory> combosList = new ArrayList<ComboHistory>();
	if(filterEntitiesByFieldMap.isEmpty()){
	    LOGGER.log(Level.SEVERE, "filterEntitiesByFieldMap is empty");
	    return combosList;
	}
	Query query = null;
	for(String key : filterEntitiesByFieldMap.keySet()){
	    switch(key){
	    case "comboId":
		query = entityManager.createNamedQuery(getCombosHistory);
		query.setParameter("comboId", Combo.class.getSimpleName() + "@"
			+ filterEntitiesByFieldMap.get(key));
		break;

	    case "comboLists":
		if(!(filterEntitiesByFieldMap.get(key) instanceof List)){
		    LOGGER.log(Level.SEVERE,"Expected list of combo ids");
		    return combosList;
		}
		List<Combo> comboListsFromMap = (List<Combo>)filterEntitiesByFieldMap.get(key);
		if(comboListsFromMap == null || comboListsFromMap.isEmpty()){
		    LOGGER.log(Level.WARNING, "comboLists cannot be null or empty");
		    return combosList;
		}
		query = entityManager.createNamedQuery(getWeekCombos);
		DateTime currentDate = new DateTime();
		Date dayAfterCurrentDate = DateUtility.addHoursToDate(12,currentDate.toDate());
		// Add 12 hours to current Date. So from endDate we assume 8 days i.e 1+7Days
		Date endTargetDate = DateUtility.addDaysToDate(
			8, currentDate.toDate());
		query.setParameter("currentDate", dayAfterCurrentDate);
		query.setParameter("targetDate", endTargetDate);
		query.setParameter("combosList", comboListsFromMap);
		break;

	    default:
		return combosList;
	    }
	}
	try {
	    combosList = query.getResultList();
	} catch (PersistenceException exception) {
	    LOGGER.log(Level.SEVERE, "Error retrieving results: Error "
		    + exception);
	}
	return combosList;
    }

    @Override
    public List<ComboHistory> getEntitiesByField(ComboHistory entity) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public List<ComboHistory> updateEntity(ComboHistory newEntity,
	    String primaryKeyId, String updateByField) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public ComboHistory deActivateEntity(ComboHistory entity) {
	// TODO Auto-generated method stub
	return null;
    }

}
