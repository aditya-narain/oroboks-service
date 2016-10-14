package com.oroboks.cache;

import net.spy.memcached.MemcachedClient;
import net.spy.memcached.internal.GetFuture;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Test for {@link MemcacheHandler}
 * @author Aditya Narain
 */
@RunWith(MockitoJUnitRunner.class)
public class MemcacheHandlerTest {
    @Mock
    private MemcachedClient client;

    @Mock
    private GetFuture<Object> object;

    /**
     * 
     */
    @Test
    public void testConnectionIsInActive_FutureIsNull(){
	Mockito.when(client.asyncGet(Matchers.isA(String.class))).thenReturn((GetFuture<Object>)null);
	Assert.assertFalse(MemcacheHandler.isConnectionActive(client));
	Mockito.verify(client).shutdown();
    }

    @Test
    public void testConnectionIsActive(){
	Mockito.when(client.asyncGet(Matchers.isA(String.class))).thenReturn(object);
	Assert.assertTrue(MemcacheHandler.isConnectionActive(client));
    }

    @Test
    public void testConnectionIsActive_Exception(){
	Mockito.when(client.asyncGet(Matchers.isA(String.class))).thenThrow(IllegalStateException.class);
	Assert.assertFalse(MemcacheHandler.isConnectionActive(client));
	Mockito.verify(client).shutdown();
    }
}
