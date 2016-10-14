package com.oroboks.cache;

import java.io.IOException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.spy.memcached.AddrUtil;
import net.spy.memcached.ConnectionFactory;
import net.spy.memcached.ConnectionFactoryBuilder;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.auth.AuthDescriptor;
import net.spy.memcached.auth.PlainCallbackHandler;

/**
 * Handler for Memory caching in Oroboks.
 * 
 * @author Aditya Narain
 * 
 */
public class MemcacheHandler {
    private static final Logger LOGGER = Logger.getLogger(MemcacheHandler.class
	    .getSimpleName());
    private static MemcachedClient mc;

    /**
     * Creates memcache connection and returns {@link MemcachedClient memcache}
     * over specified memcached locations.
     * 
     * @return {@link MemcachedClient memcache client}
     * @throws MemcacheException
     *             if I/O exception occurs.
     */
    public static MemcachedClient getCacheClientConnection() {
	if (mc != null) {
	    return mc;
	}

	try {
	    // Allows auth to be disabled to be tested locally.
	    ConnectionFactory cf;
	    if (MemcacheProperties.getMemcacheAuth() == null) {
		String userName = MemcacheProperties.getMemcacheUserName();
		String passWord = MemcacheProperties.getMemcachePassword();
		AuthDescriptor ad = new AuthDescriptor(
			new String[] { "PLAIN" }, new PlainCallbackHandler(
				userName, passWord));
		cf = new ConnectionFactoryBuilder()
		.setProtocol(ConnectionFactoryBuilder.Protocol.BINARY)
		.setAuthDescriptor(ad).build();
	    } else {
		// No Auth required. Will be used while doing local development.
		cf = new ConnectionFactoryBuilder().setProtocol(
			ConnectionFactoryBuilder.Protocol.BINARY).build();
	    }
	    mc = new MemcachedClient(cf,
		    AddrUtil.getAddresses(MemcacheProperties
			    .getMemecacheServers()));
	    return (isConnectionActive(mc)) ? mc : null;

	} catch (IOException exception) {
	    LOGGER.log(Level.SEVERE,
		    "Couldn't create a connection to MemCachier");
	    mc.shutdown();
	    throw new MemcacheException(
		    "An I/O Exception occoured. StackTrace: " + exception);
	}
    }

    static boolean isConnectionActive(MemcachedClient mc) {
	Future<Object> f = null;
	try {
	    f = mc.asyncGet("someKey");
	    // Try to get a value, for up to 5 seconds, and cancel if it
	    // doesn't return
	    f.get(5, TimeUnit.SECONDS);
	    // throws expecting InterruptedException, ExecutionException
	    // or TimeoutException
	} catch (Exception e) {
	    // Since we don't need this, go ahead and cancel the operation.
	    // This is not strictly necessary, but it'll save some work on
	    // the server. It is okay to cancel it if running.
	    mc.shutdown();
	    if(f != null){
		f.cancel(true);
	    }
	    return false;
	}
	return true;
    }

    /**
     * Shutsdown the memcache client connection gracefully. If memcache client
     * is not initialized nothing happens.
     */
    public static void shutdownConnection() {
	if (mc != null) {
	    mc.shutdown();
	    mc = null;
	    LOGGER.log(Level.INFO, "Memcache client successfully shutdown");
	}
	LOGGER.log(Level.WARNING,
		"Memcache client cannot be shutdown as its not initialized.");
    }

    private MemcacheHandler() {
	/**
	 * Empty Constructor.
	 */
    }

    /**
     * Exception Handling for Memcache.
     * 
     * @author Aditya Narain
     * 
     */
    static class MemcacheException extends RuntimeException {

	/**
	 * Default Serial version.
	 */
	private static final long serialVersionUID = -7920146359793455025L;

	public MemcacheException() {
	    super();
	}

	public MemcacheException(String message) {
	    super(message);
	}
    }

    static class MemcacheProperties {
	public static String getMemcacheUserName() {
	    return System.getenv("MEMCACHIER_USERNAME");
	}

	public static String getMemcachePassword() {
	    return System.getenv("MEMCACHIER_PASSWORD");
	}

	public static String getMemecacheServers() {
	    return System.getenv("MEMCACHIER_SERVERS");
	}

	public static String getMemcacheAuth(){
	    return System.getenv("MEMCACHIER_NOAUTH");
	}
    }
}
