package com.oroboks.exception;

/**
 * SaveException is an {@link Exception}  thrown when entity is unable to be persisted
 * @author Aditya Narain
 *
 */
public class SaveException extends RuntimeException {


    static final long serialVersionUID = -8737064874929437205L;

    /**
     * Constructs a SaveException with {@code null} as its detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     */
    public SaveException(){
	super();
    }

    /**
     * Constructs SaveException with the specified detail message
     * @param message The detail message of error.
     */
    public SaveException(String message){
	super(message);
    }

}
