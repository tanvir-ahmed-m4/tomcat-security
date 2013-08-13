package security;

import java.io.IOException;
import java.security.Principal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.TreeMap;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import javax.sql.DataSource;

/**
 * Application Lifecycle Listener implementation class
 * SingleSignOnLifeCycleListener
 * 
 */
public class CopyOfSingleSignOnLifeCycleListener implements HttpSessionListener, Filter {

	private static final Logger logger = Logger.getLogger(CopyOfSingleSignOnLifeCycleListener.class.getName());

	private static final String SSO_USER_PRINCIPAL = "SSO_USER_PRINCIPAL";

	private FilterConfig filterConfig = null;

	// Cookie name for single sign on support
	/*
	 * @see org.apache.catalina.authenticator.Constants
	 */
	public static final String SINGLE_SIGN_ON_COOKIE = 
			System.getProperty(
					"org.apache.catalina.authenticator.Constants.SSO_SESSION_COOKIE_NAME",
					"JSESSIONIDSSO");

	/**
	 * Default constructor.
	 */
	public CopyOfSingleSignOnLifeCycleListener() {
		// TODO Change log level to debug
		logger.info("Instance created.");
	}

	/**
	 * @see HttpSessionListener#sessionCreated(HttpSessionEvent)
	 */
	public void sessionCreated(HttpSessionEvent se) {
		// TODO Change log level to debug
		logger.info("New session: " + se.getSession().getId());
	}

	/**
	 * @see HttpSessionListener#sessionDestroyed(HttpSessionEvent)
	 */
	public void sessionDestroyed(HttpSessionEvent se) {
		// XXX Invalidate/remove the Session record from the database
		// TODO Change log level to debug
		logger.info("Session destroyed: " + se.getSession().getId());
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Change log level to debug
		this.filterConfig = filterConfig;
		// filterInfo = filterConfig.getFilterName() + "=>" +
		// filterConfig.getServletContext().getContextPath();
		logger.info("Filter initialised: " + filterConfig);
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		// TODO Change log level to debug
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		Principal user = httpRequest.getUserPrincipal();
		HttpSession session = httpRequest.getSession();
		if (user != null && session.getAttribute(SSO_USER_PRINCIPAL) == null) {
			// First-time AFTER login. Store the user in session
			session.setAttribute(SSO_USER_PRINCIPAL, user);

			logger.info("First-time AFTER login - intercepted. Session ID: " + session.getId());
			if (httpRequest.getCookies() != null) {
				TreeMap<String, String> cookies = new TreeMap<String, String>();
				for (Cookie c : httpRequest.getCookies()) {
					cookies.put(c.getName(), c.getValue());
				}
				logger.info("Request Cookies: " + cookies.toString());
			}

			// XXX Create a Session record in the database
			Connection conn = null;
			try {
				InitialContext ic = new InitialContext();
				String datasource = (String) ic.lookup("java:comp/env/auth/datasource");
				DataSource ds = (DataSource) ic.lookup(datasource);
				conn = ds.getConnection();

				PreparedStatement pstmt;

				// NetCourier
//				pstmt = conn.prepareStatement("INSERT INTO SE (CRCD, SECD, USID, DATE, TIME, LUPD, STAT, SEID, SESS)"
//						+ " VALUES ('017', ?, '00000000', ?, ?, ?, 'L', '00000000', ?)");
//				
//				String couriercode = (String) ic.lookup("java:comp/env/auth/couriercode");
//				Date created = new Date(session.getCreationTime());
//				Date updated = new Date(session.getLastAccessedTime());
//				
//				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//				SimpleDateFormat tf = new SimpleDateFormat("hh:mm");
//				
//				pstmt.setString(1, session.getId()); // SECD Date created
//				pstmt.setString(2, df.format(created)); // DATE
//				pstmt.setString(3, tf.format(created)); // TIME
//				pstmt.setString(4, tf.format(updated)); // LUPD
//				pstmt.setString(5, session.getId()); // SESS

				// Empower Office
				pstmt = conn.prepareStatement("INSERT INTO SE (SYST, PGCD, SECD, USCD, DSRT, TSRT, DUPD, LUPD, DEXP, TEXP, STAT)"
						+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'L')");
				
				String system = (String) ic.lookup("java:comp/env/auth/system");
				String programme = (String) ic.lookup("java:comp/env/auth/programme");
				Date created = new Date(session.getCreationTime());
				Date updated = new Date(session.getLastAccessedTime());
				Date expire = new Date(session.getLastAccessedTime() + (session.getMaxInactiveInterval() * 1000));
				
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				SimpleDateFormat tf = new SimpleDateFormat("hh:mm");
				
				pstmt.setString(1, system); // SYST
				pstmt.setString(2, programme); // PGCD
				pstmt.setString(3, session.getId()); // SECD
				pstmt.setString(4, user.getName()); // USCD
				pstmt.setString(5, df.format(created)); // DSRT
				pstmt.setString(6, tf.format(created)); // TSRT
				pstmt.setString(7, df.format(updated)); // DUPD
				pstmt.setString(8, tf.format(updated)); // LUPD
				pstmt.setString(9, df.format(expire)); // DEXP
				pstmt.setString(10, tf.format(expire)); // TEXP

				pstmt.executeUpdate();
				pstmt.close();

			} catch (NamingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if (conn != null) {
					try {
						conn.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			// Pass the request along the filter chain
			ResponseWrapper wrappedResponse = new ResponseWrapper((HttpServletResponse) response);
			chain.doFilter(request, wrappedResponse);
			logger.info("Response Cookies: " + wrappedResponse.cookies);
			// processing complete
			return;
		}
		// ... all other occasions
		chain.doFilter(request, response);
	}

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Change log level to debug
		logger.info("Filter destroyed: " + filterConfig);
		this.filterConfig = null;
	}

	private class ResponseWrapper extends HttpServletResponseWrapper {

		private Collection<Cookie> cookies = new ArrayList<Cookie>();

		public ResponseWrapper(HttpServletResponse response) {
			super(response);
		}

		@Override
		public void addCookie(Cookie cookie) {
			super.addCookie(cookie);
			cookies.add(cookie);
		}
	}
}
