package com.oroboks.dao;

import java.util.List;
import java.util.Map;

/**
 * @author Aditya Narain DAO for Entity of any dataType
 * @param <T>
 *            represents the type of entity to be persisted.
 */
public interface DAO<T> {

    /**
     * @param entity
     *            type to be persisted. Cannot be null.
     * @return the entity that is added.
     */
    public T addEntity(T entity);

    /**
     * Gets all the active entities.
     * 
     * @return non null active entities.
     */
    public List<T> getAllEntities();

    /**
     * Gets all entities by entity field
     * 
     * @param filterEntitiesByFieldMap
     *            retrieve entities by fields. Here the key is field by which
     *            entities needs to be filtered and value is the value of the
     *            field (Cannot be null)
     * @return non-null collection of all entities by their fields. Can return empty results
     */
    public List<T> getEntitiesByField(
	    Map<String, Object> filterEntitiesByFieldMap);

    /**
     * Gets the entities by itself. Essentially to check if entity already exists in database.
     * @param entity Entity to retrieve.
     * @return List of entities in the database. Can be empty
     */
    public List<T> getEntitiesByField(T entity);

    // TODO : Complete javadoc for updateentity
    /**
     * @param newEntity
     * @param primaryKeyId
     * @param updateByField
     * @return Updated entity
     */
    public List<T> updateEntity(T newEntity, String primaryKeyId,
	    String updateByField);

    /**
     * Deactivates the entity for the given id.
     * 
     * @param entity
     *            represents the entity to be deActivated. Cannot be null.
     * 
     * @return Entity that has been deactivated.
     */
    public T deActivateEntity(T entity);
}
