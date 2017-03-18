package com.oroboks.guice;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Singleton;

/**
 * Cors Filter to allow acces of service from client side
 * @author Aditya Narain
 */
@Singleton
public class CorsFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
	/*
	 * Left Blank intentionally
	 */
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
	    FilterChain chain) throws IOException, ServletException {
	if(response instanceof HttpServletResponse){
	    HttpServletResponse alteredResponse = ((HttpServletResponse)response);
	    addCorsHeader(alteredResponse);
	}

	chain.doFilter(request, response);
    }

    private void addCorsHeader(HttpServletResponse response) {
	response.addHeader("Access-Control-Allow-Origin", "https://safe-reef-14664.herokuapp.com");
	response.addHeader("Access-Control-Allow-Credentials", "true");
	response.addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE, HEAD");
	response.addHeader("Access-Control-Allow-Headers", "X-PINGOTHER, Origin, X-Requested-With, Content-Type, Accept, Authorization");
	response.addHeader("Access-Control-Max-Age", "1728000");

    }

    @Override
    public void destroy() {
	/*
	 * Left blank intentionally
	 */

    }

}
