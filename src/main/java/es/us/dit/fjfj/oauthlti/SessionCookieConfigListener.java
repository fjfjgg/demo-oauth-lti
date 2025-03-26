package es.us.dit.fjfj.oauthlti;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.SessionCookieConfig;
import jakarta.servlet.annotation.WebListener;

/**
 * Application Lifecycle Listener implementation class SessionCookieConfigListener
 *
 */
@WebListener
public class SessionCookieConfigListener implements ServletContextListener {
	
	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent sce)  { 
    	SessionCookieConfig sessionCookieConfig = sce.getServletContext().getSessionCookieConfig();
        sessionCookieConfig.setHttpOnly( true );
        sessionCookieConfig.setSecure( true );
        sessionCookieConfig.setAttribute("SameSite", "None");
        sessionCookieConfig.setAttribute("Partitioned", "True");
    }

	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent sce)  { 
         // TODO Auto-generated method stub
    }
	
}
