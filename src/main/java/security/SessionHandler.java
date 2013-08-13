package security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpSessionEvent;

public interface SessionHandler {

	public void create(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException;
	
	public void destroy(HttpSessionEvent se);
}
