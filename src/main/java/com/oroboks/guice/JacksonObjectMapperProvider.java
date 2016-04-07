package com.oroboks.guice;

import javax.ws.rs.ext.ContextResolver;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Provides an Object Mapper to configure Jackson serialization
 * @author Aditya Narain
 *
 */
public class JacksonObjectMapperProvider implements ContextResolver<ObjectMapper> {

	@Override
	public ObjectMapper getContext(Class<?> type) {
		final ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper;
	}

}
