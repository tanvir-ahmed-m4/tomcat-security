package security;

import java.util.logging.Logger;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * Application Lifecycle Listener implementation class SSOSessionListener
 *
 */
public class SSOSessionListener implements HttpSessionListener, HttpSessionBindingListener {

	private static final Logger logger = Logger.getLogger(SSOSessionListener.class.getName());
	
    /**
     * Default constructor. 
     */
    public SSOSessionListener() {
        // TODO Auto-generated constructor stub
    	logger.info("SSOSessionListener - instance created.");
    }

	/**
     * @see HttpSessionListener#sessionCreated(HttpSessionEvent)
     */
    public void sessionCreated(HttpSessionEvent se) {
        // TODO Auto-generated method stub
    	logger.info("SSOSessionListener - new session created: " + se.getSession().getId());
    }

	/**
     * @see HttpSessionListener#sessionDestroyed(HttpSessionEvent)
     */
    public void sessionDestroyed(HttpSessionEvent se) {
        // TODO Auto-generated method stub
    	logger.info("SSOSessionListener - session destroyed: " + se.getSession().getId());
    }

	@Override
	public void valueBound(HttpSessionBindingEvent event) {
		// TODO Auto-generated method stub
		logger.info("SSOSessionListener - new value bound: " + event.toString());
		
	}

	@Override
	public void valueUnbound(HttpSessionBindingEvent event) {
		// TODO Auto-generated method stub
		logger.info("SSOSessionListener - value unbound: " + event.toString());
		
	}
	
}
