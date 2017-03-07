package com.oroboks.entities;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.validation.constraints.NotNull;

/**
 * Generic Interface for every table keyed to generate UUID.
 * A UUID will make sure to generate unique primary key.
 * The base entity defines a set of tags. This is done so that any entity that inherits baseentity will also include
 * set of generic tags.
 * 
 * @author Aditya Narain
 *
 */
@MappedSuperclass
public abstract class BaseEntity implements Serializable {
    /**
     * Default Serial Version ID
     */
    private static final long serialVersionUID = 5456329733337540169L;
    @Id
    @NotNull
    @Column(name = "UUID", unique = true, updatable = false)
    private String uuid;

    /**
     * Gets the Unique Identifier. Will be null if UUID is set as null.
     * @return  unique identifier
     */
    public String getUUID(){
	return (uuid == null)? null :(uuid.contains("@"))? uuid.split("@", 2)[1]:uuid;
    }

    /**
     * Sets the unique UUID
     * @param uuid represents the unique identifier for the entity to persist.
     */
    public final void setUUID(String uuid){
	this.uuid = (uuid == null)?null : getClass().getSimpleName() + "@" + uuid;
    }

    /**
     * Sets the UUID
     */
    @PrePersist
    @PreUpdate
    public void setUUID(){
	setUUID(UUID.randomUUID().toString());
    }
}
